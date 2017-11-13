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
import com.topcoder.timobile.activity.ProfileSettingsActivity;
import com.topcoder.timobile.baseclasses.BaseFragment;
import com.topcoder.timobile.glide.GlideApp;
import com.topcoder.timobile.model.AchievementModel;
import com.topcoder.timobile.model.User;
import com.topcoder.timobile.utility.AppConstants;
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
  private User user;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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
    apiService.getUser().subscribe(this::onUser, this::onError);
    apiService.getAchievements().subscribe(this::onSuccess, this::onError);
  }

  private void onUser(User user) {
    this.user = user;
    GlideApp.with(this).load(user.getProfileImage()).into(profileImage);
    tvUserName.setText(user.getName());
    tvUserEmail.setText(user.getEmail());
  }

  private void onError(Throwable throwable) {
    Timber.d(throwable);
  }

  private void onSuccess(AchievementModel achievementModel) {
    tvReviewCount.setText(String.valueOf(achievementModel.getReviews()));
    tvBadgeCount.setText(String.valueOf(achievementModel.getBadges()));
    tvStoriesCount.setText(String.valueOf(achievementModel.getStories()));
    tvCardCount.setText(String.valueOf(achievementModel.getCards()));
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
    ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
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

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    switch (id) {
      case R.id.settings:
        Intent intent = new Intent(getActivity(), ProfileSettingsActivity.class);
        intent.putExtra(AppConstants.KEY_OBJ, user);
        ActivityUtils.startActivity(intent);
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}
