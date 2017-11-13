package com.topcoder.timobile.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseActivity;
import com.topcoder.timobile.customviews.AdvanceDrawerLayout;
import com.topcoder.timobile.fragments.BookmarkFragment;
import com.topcoder.timobile.fragments.HelpFragment;
import com.topcoder.timobile.fragments.PreStoryFragment;
import com.topcoder.timobile.fragments.ProfileFragment;
import com.topcoder.timobile.fragments.RewardShopFragment;
import com.topcoder.timobile.fragments.StoryFragment;
import com.topcoder.timobile.fragments.TiPointsFragment;
import com.topcoder.timobile.glide.GlideApp;
import com.topcoder.timobile.model.User;
import com.topcoder.timobile.utility.LocalStorage;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

/**
 * Author: Harshvardhan
 * Date: 26/10/17
 */

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.drawer_layout) AdvanceDrawerLayout drawerLayout;
  @BindView(R.id.container) FrameLayout container;
  @BindView(R.id.nav_view) NavigationView navView;
  @BindView(R.id.tvTitle) TextView tvTitle;
  private HeaderHolder headerHolder;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    ButterKnife.bind(this);
    View view = navView.getHeaderView(0);
    headerHolder = new HeaderHolder(view);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);

    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    navView.setNavigationItemSelectedListener(this);
    drawerLayout.useCustomBehavior(Gravity.START);
    drawerLayout.useCustomBehavior(Gravity.END);
    checkNavItem(R.id.nav_story);
    switchFragment(new PreStoryFragment(), false);
    apiService.getUser().subscribe(this::onUser, this::onError);
  }

  private void onError(Throwable throwable) {
    Timber.d(throwable);
  }

  /**
   * get sample user
   * @param user user
   */
  private void onUser(User user) {
    GlideApp.with(this).load(user.getProfileImage()).into(headerHolder.profileImage);
    headerHolder.tvUserEmail.setText(user.getEmail());
    headerHolder.tvUserName.setText(user.getName());
  }

  /**
   * set title
   * @param title title
   */
  public void setTitle(@StringRes int title) {
    tvTitle.setText(title);
  }

  /**
   * switch fragment
   * @param fragment fragment
   * @param addToBackStack addToBackStack
   */
  public void switchFragment(Fragment fragment, boolean addToBackStack) {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    // Replace whatever is in the fragment_container view with this fragment,
    // and add the transaction to the back stack so the user can navigate back
    transaction.replace(R.id.container, fragment);
    if (addToBackStack) transaction.addToBackStack(null);

    // Commit the transaction
    transaction.commit();
  }

  @Override public void onBackPressed() {
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  class HeaderHolder {
    @BindView(R.id.profileImage) CircleImageView profileImage;
    @BindView(R.id.tvUserName) TextView tvUserName;
    @BindView(R.id.tvUserEmail) TextView tvUserEmail;
    @BindView(R.id.imgClose) ImageView imgClose;

    public HeaderHolder(View view) {
      ButterKnife.bind(this, view);
    }

    @OnClick(R.id.imgClose) void onClose() {
      if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
        drawerLayout.closeDrawer(GravityCompat.START);
      }
    }
  }

  private Fragment fragment = null;
  //navigation item click manage
  @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    int id = item.getItemId();
    navView.setCheckedItem(id);

    switch (id) {
      case R.id.nav_story:
        fragment = new StoryFragment();
        break;
      case R.id.nav_bookmark:
        fragment = new BookmarkFragment();
        break;
      case R.id.nav_profile:
        fragment = new ProfileFragment();
        break;

      case R.id.nav_points:
        fragment = new TiPointsFragment();
        break;

      case R.id.nav_reward:
        fragment = new RewardShopFragment();
        break;
      case R.id.nav_help:
        fragment = new HelpFragment();
        break;
      case R.id.nav_logout:
        LocalStorage.getInstance().clear();
        finish();
        ActivityUtils.startActivity(LoginActivity.class);
        return true;
    }
    drawerLayout.closeDrawer(GravityCompat.START);
    new Handler().postDelayed(() -> switchFragment(fragment, false), 350);

    return false;
  }

  public void checkNavItem(int id) {
    navView.setCheckedItem(id);
  }
}
