package com.topcoder.timobile.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.timobileapp.R;
import com.topcoder.timobile.adapter.WelcomeAdapter;
import com.topcoder.timobile.baseclasses.BaseActivity;
import com.topcoder.timobile.utility.AppConstants;
import com.topcoder.timobile.utility.LocalStorage;
import me.relex.circleindicator.CircleIndicator;

/**
 * Author: Harshvardhan
 * Date: 26/10/17.
 */
public class WelcomeActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

  @BindView(R.id.imgWelcome) ImageView imgWelcome;
  @BindView(R.id.pager) ViewPager pager;
  @BindView(R.id.indicator) CircleIndicator indicator;
  @BindView(R.id.tvSkip) TextView tvSkip;
  private int[] welcomeImages = new int[] { R.drawable.ic_welcome_first, R.drawable.ic_welcome_second, R.drawable.ic_welcome_third };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (!LocalStorage.getInstance().getBoolean(AppConstants.Show_Welcome, true)) {
      nextScreen();
      return;
    }
    setContentView(R.layout.activity_welcome);
    ButterKnife.bind(this);
    pager.setAdapter(new WelcomeAdapter(this));
    indicator.setViewPager(pager);
    imgWelcome.setImageResource(welcomeImages[0]);
    pager.addOnPageChangeListener(this);
    for (int i = 0; i < indicator.getChildCount(); i++) {
      View v = indicator.getChildAt(i);
      v.setTag(i);
      v.setOnClickListener(v1 -> {
        int pageIndex = (int) v1.getTag();
        pager.setCurrentItem(pageIndex, true);
      });
    }
  }

  @OnClick(R.id.tvSkip) void onClick() {
    //stored in preference
    LocalStorage.getInstance().storeData(AppConstants.Show_Welcome, false);
    nextScreen();
  }

  private void nextScreen() {
    ActivityUtils.startActivity(LoginActivity.class);
    finish();
  }

  @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

  }

  @Override public void onPageSelected(int position) {
    imgWelcome.setImageResource(welcomeImages[position]);
    if (position == 2) {
      tvSkip.setText(getString(R.string.finish));
    } else {
      tvSkip.setText(getString(R.string.skip_this));
    }
  }

  @Override public void onPageScrollStateChanged(int state) {

  }
}
