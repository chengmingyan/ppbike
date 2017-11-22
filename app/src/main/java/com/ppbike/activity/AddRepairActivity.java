package com.ppbike.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.ppbike.R;
import com.ppbike.bean.AddRepairShopRequest;
import com.ppbike.bean.PoiAddressBean;
import com.ppbike.listener.GlideImageLoader;
import com.ppbike.listener.GlidePauseOnScrollListener;
import com.ppbike.model.RequestModel;
import com.ppbike.model.UserModel;
import com.ppbike.util.BitmapUtil;
import com.ppbike.util.DialogUtil;
import com.ppbike.util.GPSUtil;
import com.ppbike.util.ZipUtil;
import com.ppbike.view.BottomDialog.BottomDialog;
import com.ppbike.view.BottomDialog.OnDialogItemClickListener;
import com.ppbike.view.LoadingDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.PauseOnScrollListener;
import cn.master.util.utils.RequestCodeUtil;
import cn.master.util.utils.ToastUtil;
import cn.master.volley.commons.JacksonJsonUtil;
import cn.master.volley.models.response.handler.ResponseHandler;
import cn.master.volley.models.response.listener.OnNeedLoginListener;

/**
 * Created by chengmingyan on 16/7/1.
 */
public class AddRepairActivity extends ParentActivity implements OnNeedLoginListener {

    private Dialog loadingDialog;
    private ResponseHandler uploadHandler;
    private TextView tv_address;
    private EditText edit_link,edit_name;
    private ArrayList<String> mCurPhotoList = new ArrayList<>();
    private static final int MAX_UPLOAD_COUNT = 1;
    private AddRepairShopRequest request = new AddRepairShopRequest();
    private PoiAddressBean currentPoiAddressBean = new PoiAddressBean();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_repair);
        initView();
        initData();
    }

    private void initView(){
        findViewById(R.id.icon_back).setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("添加修车铺");
        tv_address = (TextView) findViewById(R.id.tv_address);
        edit_link = (EditText) findViewById(R.id.edit_link);
        edit_name = (EditText) findViewById(R.id.edit_name);
    }
    private void initData(){
        loadingDialog = new LoadingDialog(this);
        uploadHandler = new ResponseHandler();
        uploadHandler.setOnNeedLoginListener(this);
        uploadHandler.setOnFailedListener(this);
        uploadHandler.setOnSucceedListener(this);
        location();
    }

    private void location(){
        AMapLocation location = GPSUtil.getInstance(this).getaMapLocation();
        if (location != null){
            tv_address.setText(location.getAddress()+"(当前地址)");
            currentPoiAddressBean.setLo(location.getLongitude());
            currentPoiAddressBean.setLa(location.getLatitude());
            currentPoiAddressBean.setAddress(location.getAddress());
            currentPoiAddressBean.setPoiAddress(location.getAoiName());
            currentPoiAddressBean.setDistrict(location.getDistrict());
        }else{
            loadingDialog.show();
            GPSUtil.getInstance(this).startLocation(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation location) {
                    if (loadingDialog.isShowing())
                        loadingDialog.dismiss();
                    GPSUtil.getInstance(AddRepairActivity.this).stopLocation();
                    if (location == null || location.getErrorCode() != 0){
                        tv_address.setText("定位失败");
                    }else{
                        tv_address.setText(location.getAddress()+"(当前地址)");
                        currentPoiAddressBean.setLo(location.getLongitude());
                        currentPoiAddressBean.setLa(location.getLatitude());
                        currentPoiAddressBean.setAddress(location.getAddress());
                        currentPoiAddressBean.setPoiAddress(location.getAoiName());
                        currentPoiAddressBean.setDistrict(location.getDistrict());
                    }
                }
            });
        }

    }
    private final int REQUEST_CODE_CAMERA = 1000;
    private final int REQUEST_CODE_GALLERY = 1001;

    private FunctionConfig initFinal() {
        //公共配置都可以在application中配置，这里只是为了代码演示而写在此处
        FunctionConfig.Builder functionConfigBuilder = new FunctionConfig.Builder();
        cn.finalteam.galleryfinal.ImageLoader imageLoader = new GlideImageLoader();
        PauseOnScrollListener pauseOnScrollListener = new GlidePauseOnScrollListener(false, true);
        functionConfigBuilder.setMutiSelectMaxSize(MAX_UPLOAD_COUNT);
        functionConfigBuilder.setEnablePreview(false);

        functionConfigBuilder.setSelected(mCurPhotoList);//添加过滤集合
        final FunctionConfig functionConfig = functionConfigBuilder.build();

        CoreConfig coreConfig = new CoreConfig.Builder(this, imageLoader)
                .setFunctionConfig(functionConfig)
                .setPauseOnScrollListener(pauseOnScrollListener)
                .setNoAnimcation(true)
                .build();
        GalleryFinal.init(coreConfig);
        return functionConfig;
    }
    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<String> resultList) {
            if (resultList != null) {
                mCurPhotoList.clear();
                mCurPhotoList.addAll(resultList);
                Bitmap bitmap = BitmapUtil.zoomBitmapByWidthAndHeight(resultList.get(0),720,387);
                findViewById(R.id.layou_addPhoto).setBackgroundDrawable(new BitmapDrawable(getResources(),bitmap));
                String bitmapString = ZipUtil.bitmapZipAndEncode(bitmap);
                request.setPic(bitmapString);
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            ToastUtil.show(AddRepairActivity.this, errorMsg);
        }
    };
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                finish();
                break;
            case R.id.layou_addPhoto:
                ArrayList<String> photos = new ArrayList<>();
                photos.add("相机");
                photos.add("相册");
                BottomDialog.showBottomDialog(this, photos, new OnDialogItemClickListener() {
                    @Override
                    public void onDialogItemClick(int position, View buttonView, Dialog dialog) {
                        switch (position){
                            case 0:
                                GalleryFinal.openCamera(REQUEST_CODE_CAMERA, initFinal(), mOnHanlderResultCallback);
                                break;
                            case 1:
                                GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY,initFinal(),mOnHanlderResultCallback);
                                break;
                        }
                    }
                });
                break;
            case R.id.layout_address:
            {
                AMapLocation location = GPSUtil.getInstance(this).getaMapLocation();
                if (location != null){
                    Intent intent = new Intent(this,BikeAddressActivity.class);
                    intent.putExtra(BikeAddressActivity.INTENT_BEAN,currentPoiAddressBean);
                    startActivityForResult(intent, RequestCodeUtil.getInstance().obtainRequestCode(BikeAddressActivity.class));
                }else{
                    location();
                }
            }
                break;
            case R.id.btn_upload:
                AMapLocation location = GPSUtil.getInstance(this).getaMapLocation();
                if (location == null){
                    ToastUtil.show(this,"未获取到位置信息，请重新定位");
                    return;
                }
                if (TextUtils.isEmpty(request.getPic())){
                    ToastUtil.show(this,"请上传一张照片");
                }
                String token = UserModel.getInstance(this).getUserBean().getToken();
                if (TextUtils.isEmpty(token)){
                    DialogUtil.showLoginDialog(this,"您需要登录后才能上传信息",false);
                    return;
                }
                loadingDialog.show();
                AddRepairShopRequest request = new AddRepairShopRequest();
                request.setLa(location.getLatitude());
                request.setLo(location.getLongitude());
                request.setAdress(location.getAddress());
                request.setBname(edit_name.getText().toString().trim());
                request.setMobile(edit_link.getText().toString().trim());
                try {
                    String body = JacksonJsonUtil.toJson(request);
                    RequestModel.uploadRepairShop(uploadHandler,body,token);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    @Override
    public void onFailed(String tag, int resultCode, Object data) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        ToastUtil.show(this,(String)data);
    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        setResult(RESULT_OK);
        finish();
    }
    @Override
    public void onNeedLogin(String tag) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        DialogUtil.resetLoginDialog(this,false);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode){
            currentPoiAddressBean = (PoiAddressBean) data.getSerializableExtra(BikeAddressActivity.INTENT_BEAN);
            tv_address.setText(currentPoiAddressBean.getAddress() );
        }
    }
}
