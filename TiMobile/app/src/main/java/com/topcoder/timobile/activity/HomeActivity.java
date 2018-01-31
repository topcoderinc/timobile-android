package com.topcoder.timobile.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.topcoder.timobile.model.event.LocationUpdatedEvent;
import com.topcoder.timobile.model.event.UserUpdateEvent;
import com.topcoder.timobile.utility.AppConstants;
import com.topcoder.timobile.utility.AppUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
  private int PERMISSION_REQUEST_CODE = 10002;
  private String TAG = HomeActivity.class.getName();
  private LocationManager mLocationManager;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    ButterKnife.bind(this);
    EventBus.getDefault().register(this);
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
    this.permissionCheck();
    this.onUser(apiService.getCurrentUser());
  }

  /**
   * check location permission
   */
  private void permissionCheck() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      switchFragment(new PreStoryFragment(), false);
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
              Manifest.permission.ACCESS_COARSE_LOCATION},
          PERMISSION_REQUEST_CODE);
    } else {
      setupLocationService();
      if (!AppUtils.getPreferences().getBoolean(AppConstants.DONT_SHOW_PRE_STORY_FRAGMENT, false)) {
        switchFragment(new PreStoryFragment(), false);
      } else {
        switchFragment(new StoryFragment(), false);
      }
    }
  }

  /**
   * setup location service
   */
  @SuppressLint("MissingPermission") private void setupLocationService() {
    mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    Location lastLocation = mLocationManager.getLastKnownLocation(LOCATION_SERVICE);
    Log.d(TAG, "permissionCheck: lastLocation = " + lastLocation);
    AppUtils.setCurrentLocation(lastLocation);
    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, AppConstants.LOCATION_REFRESH_TIME,
        AppConstants.LOCATION_REFRESH_DISTANCE, mLocationListener);
  }


  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    Log.d(TAG, "onRequestPermissionsResult: " + grantResults);
    if (requestCode == PERMISSION_REQUEST_CODE) {
      boolean locationPermissionAllowed = true;
      for (int i = 0; i < permissions.length; i += 1) {
        if ((permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[i] == PackageManager.PERMISSION_DENIED)
            || (permissions[i].equals(Manifest.permission.ACCESS_COARSE_LOCATION) && grantResults[i] == PackageManager.PERMISSION_DENIED)
            ) {
          locationPermissionAllowed = false;
        }
      }
      if (locationPermissionAllowed) {
        setupLocationService();
      } else { // permission denied
        Log.d(TAG, "onRequestPermissionsResult: denied");
        AppUtils.setCurrentLocation(null);
        switchFragment(new StoryFragment(), false); // goto story directly
      }
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  /**
   * update user event
   *
   * @param event the event entity
   */
  @Subscribe public void onUpdateUserEvent(UserUpdateEvent event) {
    onUser(apiService.getCurrentUser());
  }


  /**
   * get sample user
   *
   * @param user user
   */
  private void onUser(User user) {
    if (user.getProfilePhotoURL() != null) {
      GlideApp.with(this).load(user.getProfilePhotoURL()).into(headerHolder.profileImage);
    }
    headerHolder.tvUserEmail.setText(user.getEmail());
    headerHolder.tvUserName.setText(user.getName());
  }

  /**
   * set title
   *
   * @param title title
   */
  public void setTitle(@StringRes int title) {
    tvTitle.setText(title);
  }

  /**
   * switch fragment
   *
   * @param fragment       fragment
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
        showLoadingDialog();
        apiService.logout().subscribe(voidResponse -> {
          this.doLogout();
        }, throwable -> {
          Timber.e(throwable);
          AppUtils.showError(throwable, "logout failed");
          this.doLogout();
        });
        return true;
    }
    drawerLayout.closeDrawer(GravityCompat.START);
    new Handler().postDelayed(() -> switchFragment(fragment, false), 350);

    return false;
  }

  /**
   * when request back from backend
   */
  private void doLogout() {
    cancelLoadingDialog();
    AppUtils.getPreferences().edit().remove(AppConstants.TOKEN_EXPIRES_KEY)
        .remove(AppConstants.TOKEN_KEY).apply();
    finish();
    ActivityUtils.startActivity(LoginActivity.class);
  }

  @Override public boolean dispatchTouchEvent(MotionEvent ev) {
    AppUtils.hideKeyBoardWhenClickOther(ev, this);
    return super.dispatchTouchEvent(ev);
  }


  @Override protected void onDestroy() {
    EventBus.getDefault().unregister(this);
    super.onDestroy();
  }

  public void checkNavItem(int id) {
    navView.setCheckedItem(id);
  }

  private final LocationListener mLocationListener = new LocationListener() {
    @Override
    public void onLocationChanged(final Location location) {
      Log.d(TAG, "onLocationChanged: new Location = " + location);
      AppUtils.setCurrentLocation(location);
      EventBus.getDefault().post(new LocationUpdatedEvent());
    }

    @Override public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override public void onProviderEnabled(String s) {

    }

    @Override public void onProviderDisabled(String s) {

    }
  };
}
