package cn.finalteam.galleryfinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.finalteam.galleryfinal.adapter.PhotoPreviewAdapter;
import cn.finalteam.galleryfinal.widget.GFViewPager;

/**
 * Desction:
 * Author:pengjianbo
 * Date:2015/12/29 0029 14:43
 */
public class PhotoPreviewActivity extends PhotoBaseActivity implements ViewPager.OnPageChangeListener{

    public static final String INTENT_PHOTO_LIST = "photo_list";
    public static final String INTENT_DELETE = "delete";
    public static final String INTENT_POSITION = "position";
    private RelativeLayout mTitleBar;
    private ImageView mIvBack;
//    private TextView mTvTitle;
    private TextView mTvIndicator;

    private GFViewPager mVpPager;
    private ArrayList<String> mPhotoList;
    private PhotoPreviewAdapter mPhotoPreviewAdapter;

    private boolean delete;
    private ImageView image_right;
    private  int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.gf_activity_photo_preview);
            findViews();
            setListener();
            delete = getIntent().getBooleanExtra(INTENT_DELETE,false);
            position = getIntent().getIntExtra(INTENT_POSITION,0);
            mPhotoList = (ArrayList<String>) getIntent().getSerializableExtra(INTENT_PHOTO_LIST);
            mPhotoPreviewAdapter = new PhotoPreviewAdapter(this, mPhotoList);
            mVpPager.setAdapter(mPhotoPreviewAdapter);
            mVpPager.setCurrentItem(position);
            if (delete){
                image_right.setVisibility(View.VISIBLE);
            }
    }

    private void findViews() {
        mTitleBar = (RelativeLayout) findViewById(R.id.titlebar);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
//        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvIndicator = (TextView) findViewById(R.id.tv_indicator);

        mVpPager = (GFViewPager) findViewById(R.id.vp_pager);
        image_right = (ImageView)findViewById(R.id.image_right);
    }

    private void setListener() {
        mVpPager.addOnPageChangeListener(this);
        mIvBack.setOnClickListener(mBackListener);
        image_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoList.remove(position);
                mPhotoPreviewAdapter.notifyDataSetChanged();
                if (mPhotoList.size() == 0)
                    v.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void takeResult(String info) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mTvIndicator.setText((position + 1) + "/" + mPhotoList.size());
    }

    @Override
    public void onPageSelected(int position) {
        this.position = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private View.OnClickListener mBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putExtra(INTENT_PHOTO_LIST,mPhotoList);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
