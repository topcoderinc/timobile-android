package com.topcoder.timobile.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.timobileapp.R;
import com.topcoder.timobile.activity.HomeActivity;
import com.topcoder.timobile.baseclasses.BaseFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Harshvardhan
 * Date: 01/11/17
 */

public class TiPointsFragment extends BaseFragment {

  @BindView(R.id.tabLayout) TabLayout tabLayout;
  @BindView(R.id.pager) ViewPager pager;
  @BindView(R.id.btnChapter) Button btnChapter;
  Unbinder unbinder;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_ti_points, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setTitle(R.string.ti_points);
    setupViewPager(pager);
    tabLayout.setupWithViewPager(pager);
    changeTabsFont(tabLayout);
  }

  /**
   * setup view pager
   *
   * @param viewPager viewpager
   */
  private void setupViewPager(ViewPager viewPager) {
    ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
    adapter.addFragment(new AchievementFragment(), getString(R.string.achievements));
    adapter.addFragment(new DailyTaskFragment(), getString(R.string.daily_tasks));
    viewPager.setAdapter(adapter);
  }

  @OnClick(R.id.btnChapter) void onChapter() {
    ((HomeActivity) getActivity()).checkNavItem(R.id.nav_reward);
    switchFragment(new RewardShopFragment(), false);
  }

  static class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
      super(manager);
    }

    @Override public Fragment getItem(int position) {
      return mFragmentList.get(position);
    }

    @Override public int getCount() {
      return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
      mFragmentList.add(fragment);
      mFragmentTitleList.add(title);
    }

    @Override public CharSequence getPageTitle(int position) {
      return mFragmentTitleList.get(position);
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.ti_points_menu, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }
}
