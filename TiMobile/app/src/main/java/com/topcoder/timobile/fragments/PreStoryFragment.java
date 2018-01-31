package com.topcoder.timobile.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import com.blankj.utilcode.util.ToastUtils;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseFragment;
import com.topcoder.timobile.customviews.CustomViewPager;
import com.topcoder.timobile.utility.AppConstants;
import com.topcoder.timobile.utility.AppUtils;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

/**
 * Author: Harshvardhan
 * Date: 27/10/17
 */

public class PreStoryFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

  @BindView(R.id.pager) CustomViewPager pager;
  Unbinder unbinder;
  @BindView(R.id.tvStoryTitle) TextView tvStoryTitle;
  @BindView(R.id.tvLeft) TextView tvLeft;
  @BindView(R.id.indicator) CircleIndicator indicator;
  @BindView(R.id.tvRight) TextView tvRight;

  private String[] storyTitle;
  private List<Fragment> fragments = new ArrayList<>();
  private MyPagerAdapter adapter;
  private String TAG = PreStoryFragment.class.getName();
  private int bookmarkCreateProgress = 0;
  private int totalBookmarkNeedCreated = 0;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_pre_story, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setTitle(R.string.throughbred_insider);
    storyTitle = getResources().getStringArray(R.array.pre_story);

    fragments.clear();
    fragments.add(new AreaFragment());
    fragments.add(new StateFragment());
    fragments.add(new RaceTrackFragment());

    adapter = new MyPagerAdapter(getFragmentManager(), fragments);
    pager.setAdapter(adapter);
    indicator.setViewPager(pager);
    tvStoryTitle.setText(storyTitle[0]);
    pager.addOnPageChangeListener(this);
    pager.setPagingEnabled(false);
  }

  @OnClick({R.id.tvLeft, R.id.tvRight}) void onClick(View view) {
    int id = view.getId();
    int currentPage = pager.getCurrentItem();
    switch (id) {
      case R.id.tvLeft:
        if (pager.getCurrentItem() == 0) {
          switchFragment(new StoryFragment(), false);
        } else {
          pager.setCurrentItem(--currentPage, true);
        }
        break;
      case R.id.tvRight:
        goNext();
        break;
    }
  }

  /**
   * check validation
   *
   * @return validation result
   */
  private boolean checkValidation() {
    int currentPage = pager.getCurrentItem();
    switch (currentPage) {
      case 1:
        StateFragment fragment = (StateFragment) adapter.getCurrentFragment();
        boolean passed = fragment.checkValidation();
        if (passed) {
          RaceTrackFragment raceTrackFragment = (RaceTrackFragment) adapter.getItem(2);
          raceTrackFragment.setStateIds(fragment.getStateIds());
          raceTrackFragment.fetchRacetracks(true);
        }
        return passed;
      case 2:
        RaceTrackFragment raceTrackFragment = (RaceTrackFragment) adapter.getCurrentFragment();
        return raceTrackFragment.checkValidation();
    }
    return false;
  }

  /**
   * move to next screen
   */
  private void goNext() {
    int currentPage = pager.getCurrentItem();
    switch (currentPage) {
      case 0:
        pager.setCurrentItem(++currentPage, true);
        break;
      case 1:
        if (checkValidation()) {
          pager.setCurrentItem(++currentPage, true);
        } else {
          ToastUtils.showShort("Select at least one state");
        }
        break;
      case 2:
        if (checkValidation()) {
          RaceTrackFragment raceTrackFragment = (RaceTrackFragment) adapter.getCurrentFragment();
          this.createBookmarks(raceTrackFragment.getSelected());
        } else {
          ToastUtils.showShort("Select at least one racetrack");
        }
        break;
    }
  }

  /**
   * create all bookmarks
   *
   * @param ids the bookmarks
   */
  private void createBookmarks(List<Long> ids) {
    bookmarkCreateProgress = 0;
    totalBookmarkNeedCreated = ids.size();
    showLoadingDialog();
    for (Long id : ids) {
      apiService.createBookmark(id).subscribe(bookmark -> this.onOneBookmarkCreated(),
          throwable -> this.onOneBookmarkCreated()); // ignore error
    }
    Log.d(TAG, "createBookmarks: " + ids);
  }

  /**
   * one bookmark created
   */
  private void onOneBookmarkCreated() {
    bookmarkCreateProgress += 1;
    Log.d(TAG, "onOneBookmarkCreated: " + bookmarkCreateProgress + " / " + totalBookmarkNeedCreated);
    if (bookmarkCreateProgress >= totalBookmarkNeedCreated) {
      this.createBookMarksDone();
    }
  }

  /**
   * all bookmark created
   */
  private void createBookMarksDone() {
    cancelLoadingDialog();
    Log.d(TAG, "createBookMarksDone: all completed");
    AppUtils.getPreferences().edit().putBoolean(AppConstants.DONT_SHOW_PRE_STORY_FRAGMENT, true).apply();
    switchFragment(new StoryFragment(), false);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

  }

  @Override public void onPageSelected(int position) {
    tvStoryTitle.setText(storyTitle[position]);
    pageStateChange(position);
  }

  private void pageStateChange(int position) {
    switch (position) {
      case 0:
        tvLeft.setText(getString(R.string.decline));
        tvRight.setText(getString(R.string.allow));
        break;
      case 1:
        tvLeft.setText(getString(R.string.back));
        tvRight.setText(getString(R.string.next));
        break;
      case 2:
        tvLeft.setText(getString(R.string.back));
        tvRight.setText(getString(R.string.finish));
        break;
    }
  }

  @Override public void onPageScrollStateChanged(int state) {

  }

  public static class MyPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public MyPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
      super(fragmentManager);
      this.fragments = fragments;
    }

    // Returns total number of pages
    @Override public int getCount() {
      return this.fragments.size();
    }

    // Returns the fragment to display for that page
    @Override public Fragment getItem(int position) {
      return this.fragments.get(position);
    }

    private Fragment mCurrentFragment;

    public Fragment getCurrentFragment() {
      return mCurrentFragment;
    }

    @Override public void setPrimaryItem(ViewGroup container, int position, Object object) {
      if (getCurrentFragment() != object) {
        mCurrentFragment = ((Fragment) object);
      }
      super.setPrimaryItem(container, position, object);
    }

  }
}
