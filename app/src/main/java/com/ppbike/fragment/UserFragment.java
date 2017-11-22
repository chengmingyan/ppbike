package com.ppbike.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jauker.widget.BadgeView;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.ppbike.R;
import com.ppbike.activity.BalanceActivity;
import com.ppbike.activity.MainActivity;
import com.ppbike.activity.MessageActivity;
import com.ppbike.activity.UserInfomationActivity;
import com.ppbike.activity.WithdrawalActivity;
import com.ppbike.bean.AccountInfomationResult;
import com.ppbike.bean.UserBean;
import com.ppbike.model.RequestModel;
import com.ppbike.model.UserModel;
import com.ppbike.util.DialogUtil;
import com.ppbike.view.CircleImageView;
import com.ppbike.view.IconView;
import com.ppbike.view.LoadingDialog;

import cn.master.util.utils.RequestCodeUtil;
import cn.master.util.utils.ToastUtil;
import cn.master.volley.models.response.handler.ResponseHandler;

/**
 * Created by chengmingyan on 16/6/16.
 */
public class UserFragment extends ViewPagerNetworkFragment{
    private BadgeView badgeView;
    private IconView icon_message;
    private TextView tv_nikeName,tv_blance;
    private CircleImageView image_user;
    private Dialog loadingDialog;
    private ResponseHandler infoHandler,loginoutHandler;
    private final String TAG_INFO = "info",TAG_LOGINOUT = "loginout";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_user,null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView(getView());
        initData();

    }
    private void initView(View view) {
        view.findViewById(R.id.icon_menu).setOnClickListener(this);
        view.findViewById(R.id.layout_balance).setOnClickListener(this);
        view.findViewById(R.id.layout_payments).setOnClickListener(this);
        view.findViewById(R.id.layout_user).setOnClickListener(this);
        view.findViewById(R.id.layout_withdrawal).setOnClickListener(this);
        view.findViewById(R.id.btn_loginout).setOnClickListener(this);

        image_user = (CircleImageView) view.findViewById(R.id.image_user);
        tv_nikeName = (TextView) view.findViewById(R.id.tv_nikeName);
        tv_blance = (TextView) view.findViewById(R.id.tv_blance);

        badgeView = new BadgeView(getActivity());
        badgeView.setHeight(15);
        badgeView.setWidth(15);
        badgeView.setBadgeMargin(0, 2, 0, 0);
        badgeView.setBackgroundResource(R.drawable.hot_point);
        badgeView.setTextColor(getActivity().getResources().getColor(android.R.color.transparent));

        icon_message = (IconView) view.findViewById(R.id.icon_message);
        icon_message.setOnClickListener(this);
        badgeView.setBadgeCount(1);
    }

    private void initData(){
        infoHandler = new ResponseHandler(TAG_INFO);
        infoHandler.setOnFailedListener(this);
        infoHandler.setOnSucceedListener(this);
        infoHandler.setOnNeedLoginListener(this);

        loginoutHandler = new ResponseHandler(TAG_LOGINOUT);
        loginoutHandler.setOnFailedListener(this);
        loginoutHandler.setOnSucceedListener(this);
        loginoutHandler.setOnNeedLoginListener(this);

        loadingDialog = new LoadingDialog(getActivity());
    }

    private void refreshData(){
        UserBean userBean = UserModel.getInstance(getActivity()).getUserBean();
        tv_nikeName.setText(userBean.getNick());
        if (!TextUtils.isEmpty(userBean.getPicUrl())){
            image_user.setImageUrl(userBean.getPicUrl(),null);
        }

        RequestModel.obtainUserAccountInfomation(infoHandler,TAG_INFO,userBean.getToken());
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_menu:
                ((SlidingFragmentActivity)getActivity()).getSlidingMenu().toggle();
                break;
            case R.id.layout_user:
            {
                Intent intent = new Intent(getActivity(),UserInfomationActivity.class);
                startActivityForResult(intent, RequestCodeUtil.getInstance().obtainRequestCode(UserInfomationActivity.class));
            }
                break;
            case R.id.layout_balance:
            {

            }
            break;
            case R.id.layout_payments:
            {
                Intent intent = new Intent(getActivity(),BalanceActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.layout_withdrawal:
            {
                Intent intent = new Intent(getActivity(),WithdrawalActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.icon_message:
            {
                Intent intent = new Intent(getActivity(),MessageActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.btn_loginout:
                loadingDialog.show();
                RequestModel.loginout(loginoutHandler,TAG_LOGINOUT,UserModel.getInstance(getActivity()).getUserBean().getToken());
                break;
        }
    }

    @Override
    public void onFailed(String tag, int resultCode, Object data) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        ToastUtil.show(getActivity(),(String)data);
    }

    @Override
    public void onNeedLogin(String tag) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        DialogUtil.showLoginDialogCanReturnHome(this,"您的账号已在其他设备上登录，是否重新登录?",true);
    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        if (TAG_INFO.equals(tag)){
            AccountInfomationResult result = (AccountInfomationResult) data;
            tv_blance.setText("￥"+result.getMoney());
        }
        else if (TAG_LOGINOUT.equals(tag)){
            UserModel.getInstance(getActivity()).loginout();
            if (getActivity() instanceof MainActivity){
                MainActivity activity = (MainActivity) getActivity();
                activity.setShowPage(MainActivity.PAGE_HOME,true);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (requestCode == RequestCodeUtil.getInstance().obtainRequestCode(UserInfomationActivity.class)){
           UserBean userBean = UserModel.getInstance(getActivity()).getUserBean();
           tv_nikeName.setText(userBean.getNick());
           if (!TextUtils.isEmpty(userBean.getPicUrl())){
               image_user.setImageUrl(userBean.getPicUrl(),null);
           }
       }
    }

    @Override
    public void onVisibleToUserChanged(boolean isVisibleToUser, boolean invokeInResumeOrPause) {
        if (isVisibleToUser){
            String token = UserModel.getInstance(getActivity()).getUserBean().getToken();
            if(TextUtils.isEmpty(token)){
                DialogUtil.showLoginDialogCanReturnHome(this,"检测到您还未登录?",true);
            }else{
                refreshData();
            }
        }
    }
}
