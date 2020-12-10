package com.example.leochris.launcher;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.example.leochris.launcher.announcement.AnnounFrag;
import com.example.leochris.launcher.featured.FeaturedFragment;
import com.example.leochris.launcher.weather.WeatherTab;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SmartTabLayout tabLayout;
    private ViewPager viewPager;
    private int mInterval = 10000; // 5 seconds by default, can be changed later
    //private int mIdleTime = 30000; // 5 seconds by default, can be changed later
    private int popupOffsetX = 0;
    private int popupOffsetY = 10;
    private boolean hasTaskPopup = false;
    private LinearLayout root;

    private Handler mHandler;
    private ViewPagerAdapter adapter;

    private AnnounFrag announFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = (LinearLayout) findViewById(R.id.root_view);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new WelcomeTab(), "WELCOME");

        // Needed to call onClose
        announFrag = new AnnounFrag();
        adapter.addFragment(announFrag, "ANNOUNCEMENT");
        adapter.addFragment(new FeaturedFragment(), "FEATURED");
        //adapter.addFragment(new WeatherTab(), "WEATHER");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount() - 1);

        tabLayout = (SmartTabLayout) findViewById(R.id.tabs);
        tabLayout.setViewPager(viewPager);
        //Set tab color
        tabLayout.setBackgroundColor(Color.parseColor("#45B39D"));
        mHandler = new Handler();

        //automatically scroll to next tab
        startRepeatingTask();
        viewPager.setCurrentItem(0);

        // Resets timer when user scrolls to every page
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                stopRepeatingTask();
                mHandler.postDelayed(mStatusChecker, mInterval);
            }

            public void onPageSelected(int position) {
                stopRepeatingTask();
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        });

        // Calls mShowPopup only when root is initiated
        //hasTaskPopup = false;
        //root.post(mShowPopup);

        // Set up bounce animation on view all apps button
        Button viewAllApps = (Button) findViewById(R.id.view_all_apps);
        Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        bounceAnim.setRepeatCount(Animation.INFINITE);
        bounceAnim.setRepeatMode(Animation.START_ON_FIRST_FRAME);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        BounceInterpolator interpolator = new BounceInterpolator(0.1, 20);
        bounceAnim.setInterpolator(interpolator);

        viewAllApps.startAnimation(bounceAnim);
    }

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        requestDisallowInterceptTouchEvent(
                (ViewGroup) root,
                true
        );
        // When touch on anywhere on screen, reset time for popup
        restartRepeatingPopup();
        return super.dispatchTouchEvent(event);
    }

    private void requestDisallowInterceptTouchEvent(ViewGroup v, boolean disallowIntercept) {
        if(v == null) return;
        v.requestDisallowInterceptTouchEvent(disallowIntercept);
        int childCount = v.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = v.getChildAt(i);
            if (child instanceof ViewGroup) {
                requestDisallowInterceptTouchEvent((ViewGroup) child, disallowIntercept);
            }
        }
    }

    Runnable mShowPopup = new Runnable() {
        @Override
        public void run() {
            hasTaskPopup = false;
            Log.d("startNew","startNew");
            LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View inflatedView = layoutInflater.inflate(R.layout.side_popup, null,false);

            final PopupWindow popWindow = new PopupWindow(inflatedView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

            // Dismiss popup when touch happens outside of popup
            popWindow.setBackgroundDrawable(new BitmapDrawable());
            popWindow.setOutsideTouchable(true);

            // Dismiss popup when touch happens inside of popup
            popWindow.setTouchable(true);
            inflatedView.findViewById(R.id.side_popup_layout).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popWindow.dismiss();
                    return false;
                }
            });

            popWindow.setAnimationStyle(R.style.popupAnimation);

            int[] a = new int[2];
            viewPager.getLocationInWindow(a);

            // Use showAtLocation instead of showAnchor because showAnchor does not work
            popWindow.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY, a[0] + popupOffsetX, a[1] + popupOffsetY);

            // Schedule next popup when current popup dismissed
            popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    mHandler.postDelayed(mShowPopup, mIdleTime);
                    hasTaskPopup = true;
                }
            });
        }
    };*/

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //adapter.addFragment(new VideoTab(), "THREE");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {

    }

    public void launchLauncher(View view) {
        Intent intent = getPackageManager().getLaunchIntentForPackage("io.makerforce.ambrose.launcher");
        startActivity(intent);
    }

    /*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                FragmentInfo mFragmentInfo = (FragmentInfo) data.getSerializableExtra("new fragment");
                ImageTab mImageTab = new ImageTab();
                Bundle arg = new Bundle();

                System.out.println(mFragmentInfo.getName());

                arg.putString("name", mFragmentInfo.getName());
                arg.putString("uri", mFragmentInfo.getUri());
                mImageTab.setArguments(arg);

                adapter.addFragment(mImageTab, mImageTab.getArguments().getString("name"));
                adapter.notifyDataSetChanged();
            }
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();

        announFrag.onClose();
        //stopRepeatingPopup();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                if(viewPager.getCurrentItem() < viewPager.getAdapter().getCount() - 1) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
                else {
                    viewPager.setCurrentItem(0);
                }
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    /*void stopRepeatingPopup() {
        mHandler.removeCallbacks(mShowPopup);
    }

    void restartRepeatingPopup() {
        stopRepeatingPopup();
        if(hasTaskPopup) mHandler.postDelayed(mShowPopup, mIdleTime);
    }*/
}
