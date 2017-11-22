package com.ppbike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.ppbike.R;
import com.ppbike.adapter.StatePagerAdapter;
import com.ppbike.bean.UserBean;
import com.ppbike.constant.Api;
import com.ppbike.fragment.AboutFragment;
import com.ppbike.fragment.BikeRentOrderFragment;
import com.ppbike.fragment.BoothListFragment;
import com.ppbike.fragment.EvaluationFragment;
import com.ppbike.fragment.FeedbackFragment;
import com.ppbike.fragment.HelpFragment;
import com.ppbike.fragment.HomeFragment;
import com.ppbike.fragment.RentBikeOrderListFragment;
import com.ppbike.fragment.RepairFragment;
import com.ppbike.fragment.UserFragment;
import com.ppbike.model.UserModel;
import com.ppbike.view.StillViewPager;
import com.shelwee.update.UpdateHelper;

import cn.master.util.utils.RequestCodeUtil;
import cn.master.util.utils.ToastUtil;
import cn.master.volley.commons.AppUtils;
import cn.sharesdk.framework.ShareSDK;

public class MainActivity extends SlidingFragmentActivity
        implements View.OnClickListener {
    public static final int PAGE_HOME = 0;
    public static final int PAGE_USER = 1;
    public static final int PAGE_RENTBIKE = 2;
    public static final int PAGE_BIKERENT = 3;
    public static final int PAGE_BOOTH = 4;
    public static final int PAGE_REPAIR = 5;
    public static final int PAGE_HELP = 6;
    public static final int PAGE_FEEDBACK = 7;
    public static final int PAGE_EVALUATION = 8;
    public static final int PAGE_ABOUT = 9;

    public static final String INTENT_PAGE = "page_position";
    private StillViewPager viewPager;
    private int pagePosition = PAGE_HOME;
    private long exitAppTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       initView();
//        UpdateApp updateApp = new UpdateApp(this);
//        updateApp.update();
        UpdateHelper updateHelper = new UpdateHelper.Builder(this)
                .checkUrl(Api.URL)
                .isAutoInstall(false)
                .build();
        updateHelper.check();
        ShareSDK.initSDK(this);
    }

    private void initView(){
        // set the Behind View
        setBehindContentView(R.layout.menu_fragment);
        setContentView(R.layout.activity_main);
        SlidingMenu sm = getSlidingMenu();
        sm.setShadowWidthRes(R.dimen.fab_margin);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setBehindOffsetRes(R.dimen.nav_header_height);
        sm.setFadeDegree(0.35f);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        viewPager = (StillViewPager) findViewById(R.id.viewPager);
        StatePagerAdapter adapter = new StatePagerAdapter(this.getSupportFragmentManager(),null);
        adapter.addFragment(MainActivity.PAGE_HOME,new HomeFragment());
        adapter.addFragment(MainActivity.PAGE_USER,new UserFragment());
        adapter.addFragment(MainActivity.PAGE_RENTBIKE,new RentBikeOrderListFragment());
        adapter.addFragment(MainActivity.PAGE_BIKERENT,new BikeRentOrderFragment());
        adapter.addFragment(MainActivity.PAGE_BOOTH,new BoothListFragment());
        adapter.addFragment(MainActivity.PAGE_REPAIR,new RepairFragment());
        adapter.addFragment(MainActivity.PAGE_HELP,new HelpFragment());
        adapter.addFragment(MainActivity.PAGE_FEEDBACK,new FeedbackFragment());
        adapter.addFragment(MainActivity.PAGE_EVALUATION,new EvaluationFragment());
        adapter.addFragment(MainActivity.PAGE_ABOUT,new AboutFragment());
        viewPager.setAdapter(adapter);
        viewPager.setScanScroll(false);

        setShowPage(pagePosition);
        getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        TextView tv_versionName = (TextView) findViewById(R.id.tv_versionName);
        tv_versionName.setText("版本：v" + AppUtils.getVersionName(this));
    }

    private void initMenuView(){
        UserModel userModel = UserModel.getInstance(this);
        TextView tv_nikename = (TextView) findViewById(R.id.tv_nikeName);
        TextView tv_level = (TextView) findViewById(R.id.tv_level);
        UserBean userBean = userModel.getUserBean();

        if (TextUtils.isEmpty(userBean.getToken())){
            tv_level.setVisibility(View.GONE);
            tv_nikename.setText("未登录");
            findViewById(R.id.layout_user).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getSlidingMenu().toggle();
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivityForResult(intent, RequestCodeUtil.getInstance().obtainRequestCode(LoginActivity.class));
                }
            });
            findViewById(R.id.layout_perfectInfo).setVisibility(View.GONE);
        }else{
            tv_level.setVisibility(View.VISIBLE);
            tv_nikename.setText(userBean.getNick());
            findViewById(R.id.layout_user).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getSlidingMenu().toggle();
                    setShowPage(PAGE_USER);
                }
            });
            if (UserBean.DSTATUS_NOT_PERFECT == userBean.getDstatus()){
                findViewById(R.id.layout_perfectInfo).setVisibility(View.VISIBLE);
            }else{
                findViewById(R.id.layout_perfectInfo).setVisibility(View.GONE);
            }
        }

        setShowPage(pagePosition);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.layout_perfectInfo){
            Intent intent = new Intent();
            intent.setClass(this,PerfectInfomationActivity.class);
            startActivity(intent);
            pagePosition = PAGE_HOME;
        }
        switch (v.getId()){

            case R.id.layout_home:
                setShowPage(PAGE_HOME);
                break;
            case R.id.layout_rentBike:
                setShowPage(PAGE_RENTBIKE);
            break;
            case R.id.layout_bikeRent:
            {
                setShowPage(PAGE_BIKERENT);
            }
            break;
            case R.id.layout_booth:
            {
                setShowPage(PAGE_BOOTH);
            }
                break;
            case R.id.layout_repair:
            {
                setShowPage(PAGE_REPAIR);
            }
                break;
            case R.id.layout_help:
                setShowPage(PAGE_HELP);
                break;

            case R.id.layout_feedback:
                setShowPage(PAGE_FEEDBACK);
                break;
            case R.id.layout_evaluation:
                setShowPage(PAGE_EVALUATION);
                break;
            case R.id.layout_about:
                setShowPage(PAGE_ABOUT);
                break;
        }
        getSlidingMenu().toggle();
    }

    public void setShowPage(int position){
        setShowPage(position,false);
    }
    public void setShowPage(int position,boolean needRefreshMenu){
        if (needRefreshMenu)
            initMenuView();
        pagePosition = position;
        viewPager.setCurrentItem(position,false);
    }
    @Override
    protected void onResume() {
        super.onResume();
        initMenuView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        pagePosition = getIntent().getIntExtra(INTENT_PAGE,pagePosition);
    }

    @Override
    public void onBackPressed() {
        if (getSlidingMenu().isMenuShowing()) {
            getSlidingMenu().toggle();
        } else {
            if (System.currentTimeMillis() - exitAppTime < 1000) {
                try {
                    ShareSDK.stopSDK(this);
                    finish();
                    System.exit(0);
                } catch (Exception e) {
                }
                return;
            }
            exitAppTime = System.currentTimeMillis();
            ToastUtil.show(this,"再按一次退出");
        }
    }
}
