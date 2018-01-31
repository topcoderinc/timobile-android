package com.topcoder.timobile.fragments;

import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import com.blankj.utilcode.util.ActivityUtils;
import com.timobileapp.R;
import com.topcoder.timobile.activity.HomeActivity;
import com.topcoder.timobile.activity.ProfileSettingsActivity;
import com.topcoder.timobile.baseclasses.BaseFragment;
import com.topcoder.timobile.glide.GlideApp;
import com.topcoder.timobile.model.StoryProgress;
import com.topcoder.timobile.model.User;
import com.topcoder.timobile.model.UserBadge;
import com.topcoder.timobile.model.UserCard;
import com.topcoder.timobile.model.event.FragmentSwitchEvent;
import com.topcoder.timobile.model.event.UserUpdateEvent;
import com.topcoder.timobile.utility.AppUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Author: Harshvardhan
 * Date: 01/11/17
 */

public class ProfileFragment extends BaseFragment {

  @BindView(R.id.profileImage) CircleImageView profileImage;
  @BindView(R.id.tvUserName) TextView tvUserName;
  @BindView(R.id.tvUserEmail) TextView tvUserEmail;
  @BindView(R.id.imgArrow) ImageView imgArrow;
  @BindView(R.id.linearStoryProgress) LinearLayout linearStoryProgress;
  @BindView(R.id.tabLayout) TabLayout tabLayout;
  @BindView(R.id.pager) ViewPager pager;
  @BindView(R.id.tvReviewCount) TextView tvReviewCount;
  @BindView(R.id.tvBadgeCount) TextView tvBadgeCount;
  @BindView(R.id.tvStoriesCount) TextView tvStoriesCount;
  @BindView(R.id.tvCardCount) TextView tvCardCount;
  @BindView(R.id.linearAchievements) LinearLayout linearAchievements;
  Unbinder unbinder;
  private ViewPagerAdapter adapter;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EventBus.getDefault().register(this);
    setHasOptionsMenu(true);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_profile, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setTitle(R.string.empty_string);
    setupViewPager(pager);
    tabLayout.setupWithViewPager(pager);
    changeTabsFont(tabLayout);

    this.onUser(apiService.getCurrentUser()); // use cache fill first
    apiService.getUser().subscribe(this::onUser, this::onError); // fetch backend current user

    apiService.getCurrentUserStoryProgresses().subscribe(storyProgresses -> {  //completed stories count
      int count = 0;
      for (StoryProgress progress : storyProgresses) {
        if (progress.getCompleted()) count++;
      }
      tvStoriesCount.setText(String.valueOf(count));
    }, this::onError);

    apiService.countComments(apiService.getCurrentUser().getId()) // count reviews
        .subscribe(itemCount -> tvReviewCount.setText(String.valueOf(itemCount.getCount())), this::onError);


    apiService.getCurrentUserBadge().subscribe(this::onUserBadgeLoaded, this::onError);
    apiService.getCurrentUserCard().subscribe(this::onUserCardLoaded, this::onError);

  }

  /**
   * update user event
   *
   * @param event the event
   */
  @Subscribe public void updateUserEvent(UserUpdateEvent event) {
    this.onUser(apiService.getCurrentUser());
  }

  /**
   * goto switch fragment
   *
   * @param event the event with fragment
   */
  @Subscribe public void fragmentSwitchEvent(FragmentSwitchEvent event) {
    ((HomeActivity) getActivity()).checkNavItem(event.getNavId());
    switchFragment(event.getBaseFragment(), false);
  }

  private void onUser(User user) {
    if (user.getProfilePhotoURL() != null) {
      GlideApp.with(this).load(user.getProfilePhotoURL()).into(profileImage);
    }
    tvUserName.setText(user.getName());
    tvUserEmail.setText(user.getEmail());
  }

  private void onError(Throwable throwable) {
    Timber.d(throwable);
    AppUtils.showError(throwable, getString(R.string.error));
  }

  /**
   * user badges fetched
   *
   * @param userBadges the user badges
   */
  private void onUserBadgeLoaded(List<UserBadge> userBadges) {
    tvBadgeCount.setText(String.valueOf(userBadges.size()));
    BadgeFragment fragment = (BadgeFragment) adapter.getItem(0);
    fragment.onSuccess(userBadges);
  }

  /**
   * the user cards fetched
   *
   * @param userCards the user cards
   */
  private void onUserCardLoaded(List<UserCard> userCards) {
    tvCardCount.setText(String.valueOf(userCards.size()));
    TradingFragment fragment = (TradingFragment) adapter.getItem(1);
    fragment.onSuccess(userCards);
  }

  @OnClick(R.id.linearStoryProgress) void onClick() {
    if (linearAchievements.isShown()) {
      linearAchievements.setVisibility(View.GONE);
      imgArrow.setRotation(0);
    } else {
      linearAchievements.setVisibility(View.VISIBLE);
      imgArrow.setRotation(180);
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  private void setupViewPager(ViewPager viewPager) {
    adapter = new ViewPagerAdapter(getChildFragmentManager());
    adapter.addFragment(new BadgeFragment(), "Badges");
    adapter.addFragment(new TradingFragment(), "Trading Cards");
    viewPager.setAdapter(adapter);
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

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.profile_menu, menu);
  }

  @Override public void onDestroy() {
    EventBus.getDefault().unregister(this);
    super.onDestroy();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    switch (id) {
      case R.id.settings:
        Intent intent = new Intent(getActivity(), ProfileSettingsActivity.class);
        ActivityUtils.startActivity(intent);
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}
