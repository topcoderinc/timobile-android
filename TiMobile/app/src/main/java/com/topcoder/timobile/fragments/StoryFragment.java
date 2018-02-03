package com.topcoder.timobile.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.github.florent37.viewanimator.ViewAnimator;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.timobileapp.R;
import com.topcoder.timobile.activity.BrowseStoryActivity;
import com.topcoder.timobile.adapter.PreStoryListAdapter;
import com.topcoder.timobile.adapter.StoryAdapter;
import com.topcoder.timobile.baseclasses.BaseFragment;
import com.topcoder.timobile.baseclasses.BaseRecyclerAdapter;
import com.topcoder.timobile.glide.GlideApp;
import com.topcoder.timobile.model.PageResult;
import com.topcoder.timobile.model.PreStorySampleModel;
import com.topcoder.timobile.model.TrackStory;
import com.topcoder.timobile.model.event.LocationUpdatedEvent;
import com.topcoder.timobile.utility.AppConstants;
import com.topcoder.timobile.utility.AppUtils;
import com.topcoder.timobile.utility.LoadMore;
import com.topcoder.timobile.utility.OnLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lombok.Setter;
import timber.log.Timber;


/**
 * Author: Harshvardhan
 * Date: 28/10/17
 */

public class StoryFragment extends BaseFragment implements BaseRecyclerAdapter.RecycleOnItemClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.filterRecyclerView) RecyclerView filterRecyclerView;
  @BindView(R.id.relativeRaceTrack) RelativeLayout relativeRaceTrack;
  @BindView(R.id.linearFilter) LinearLayout linearFilter;
  @BindView(R.id.tvSelectTrack) TextView tvSelectTrack;
  @BindView(R.id.map) MapView mMapView;
  @BindView(R.id.linearTile) LinearLayout linearTile;
  @BindView(R.id.imgDown) ImageView imgDown;
  @BindView(R.id.tvStoryTips) TextView tvStoryTips;
  @BindView(R.id.tvSortBy) TextView tvSortBy;

  Unbinder unbinder;
  private List<PreStorySampleModel> preStorySampleModels = new ArrayList<>();
  private PreStoryListAdapter raceTrackAdapter;

  private List<TrackStory> storyModelList = new ArrayList<>();
  private StoryAdapter storyAdapter;
  private Menu menu;
  private boolean isList = true;
  private final List<Marker> mMarkerList = new ArrayList<>();
  private GoogleMap map;
  private Marker lastClickedMarker = null;
  private ViewHolder bottomTileHolder;
  private LoadMore loadMore;

  /**
   * search string
   */
  private String filterSearchString = null;

  /**
   * the filter racetrack ids
   */
  private String filterRacetrackIds = null;

  /**
   * the stories offset.
   */
  private int offset = 0;
  private int total = 0;
  private String TAG = StoryFragment.class.getName();

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EventBus.getDefault().register(this);
    setHasOptionsMenu(true);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_story_location, container, false);
    unbinder = ButterKnife.bind(this, view);
    loadMore = new LoadMore(recyclerView);
    loadMore.setOnLoadMoreListener(() -> {
      fetchStories(false);
    });
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setTitle(R.string.story_selection);
    mMapView.onCreate(savedInstanceState);
    mMapView.getMapAsync(this);
    bottomTileHolder = new ViewHolder(linearTile);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    filterRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    filterRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
    fetchStories(false);
    fetchRacetracks();
  }

  private void fetchRacetracks() {
    Location location = AppUtils.getCurrentLocation();
    Float lat = location == null ? null : ((float) location.getLatitude());
    Float lng = location == null ? null : ((float) location.getLongitude());
    String sortName = "name";
    String sortOrder = "ASC";
    if (lat != null) {
      sortName = "distance";
      sortOrder = "DESC";
    }
    apiService.getRacetracks(null, null,
        AppConstants.SEARCH_RACETRACKS_DISTANCE_IN_M, lat, lng, offset, AppConstants.DEFAULT_LIMIT,
        sortName, sortOrder)
        .subscribe(this::onTrackSuccess, this::onTrackError);
  }

  /**
   * fetch stories from backend.
   *
   * @param needClear is that need clear exist stories.
   */
  private void fetchStories(boolean needClear) {
    if (needClear) {
      offset = 0;
      mMarkerList.clear();
      storyModelList.clear();
      bottomTileHolder.hide();
    }

    String sortColumn = "title";
    String sortOrder = "ASC";
    Float locationLat = null;
    Float locationLng = null;
    tvSortBy.setText("Title");
    if (AppUtils.getCurrentLocation() != null) {
      sortColumn = "distance";
      sortOrder = "ASC";
      tvSortBy.setText("Nearest");
      locationLat = (float) AppUtils.getCurrentLocation().getLatitude();
      locationLng = (float) AppUtils.getCurrentLocation().getLongitude();
    }
    apiService.getTrackStories(filterSearchString, null, filterRacetrackIds,
        null, offset, AppConstants.DEFAULT_LIMIT,
        locationLat, locationLng, sortColumn, sortOrder)
        .subscribe(storyPageResult -> {
              loadMore.loadedDone();
              this.onSuccess(storyPageResult, needClear);
            }
            , throwable -> {
              loadMore.loadedDone();
              this.onError(throwable);
            });
  }


  private void onError(Throwable throwable) {
    Timber.d(throwable);
    AppUtils.showError(throwable, getString(R.string.error));
  }

  private void onSuccess(PageResult<TrackStory> trackStoryList, boolean needClear) {
    total = trackStoryList.getTotal();
    offset += trackStoryList.getItems().size();
    if (needClear) {
      offset = trackStoryList.getItems().size();
      storyModelList.clear();
    }
    storyModelList.addAll(trackStoryList.getItems());
    if (storyAdapter == null) {
      storyAdapter = new StoryAdapter(getActivity(), storyModelList);
      recyclerView.setAdapter(storyAdapter);
      storyAdapter.setRecycleOnItemClickListner(recycleOnItemClickListener);
    } else {
      storyAdapter.notifyDataSetChanged();
    }

    if (!isList) {
      initMarker();
    }
    tvStoryTips.setText(String.format(getString(R.string.map_story_tips), storyModelList.size(), total));
  }

  BaseRecyclerAdapter.RecycleOnItemClickListener recycleOnItemClickListener = (view, position) -> {
    Long id = storyModelList.get(position).getId();
    Intent intent = new Intent(ActivityUtils.getTopActivity(), BrowseStoryActivity.class);
    intent.putExtra(BrowseStoryActivity.PASS_STORY_KEY, id);
    ActivityUtils.startActivity(intent);
  };


  private void onTrackError(Throwable throwable) {
    Timber.e(throwable);
    ToastUtils.showShort(R.string.error);
  }

  private void onTrackSuccess(PageResult<PreStorySampleModel> pageResult) {
    preStorySampleModels.clear();
    PreStorySampleModel storySampleModel = new PreStorySampleModel();
    storySampleModel.setId(0);
    storySampleModel.setValue("All Racetracks");
    storySampleModel.setCheck(false);
    this.preStorySampleModels.add(storySampleModel);
    this.preStorySampleModels.addAll(pageResult.getItems());
    raceTrackAdapter = new PreStoryListAdapter(getActivity(), this.preStorySampleModels);
    filterRecyclerView.setAdapter(raceTrackAdapter);
    raceTrackAdapter.setRecycleOnItemClickListner(this);
    tvSelectTrack.setText(storySampleModel.getValue());
  }

  @OnClick({R.id.relativeRaceTrack, R.id.relativeSortBy}) void onFilter(View view) {
    int id = view.getId();
    switch (id) {
      case R.id.relativeRaceTrack:
        this.toggleRaceTrackFilterPanel();
        break;
    }
  }

  /**
   * toggle filter panel
   */
  private void toggleRaceTrackFilterPanel() {
    if (filterRecyclerView.isShown()) {
      hide(filterRecyclerView, false);
      imgDown.setRotation(0);
    } else {
      imgDown.setRotation(180);
      show(filterRecyclerView, false);
    }
  }

  /**
   * show view animation
   *
   * @param view     view
   * @param positive for positive height
   */
  private void show(final View view, boolean positive) {
    int height;
    if (positive) {
      height = view.getHeight();
    } else {
      height = -view.getHeight();
    }
    ViewAnimator.animate(view).translationY(height, 0).alpha(0, 1).duration(500).onStart(() -> view.setVisibility(View.VISIBLE)).start();
  }

  /**
   * hide view animation
   *
   * @param view     view
   * @param positive for positive height
   */
  private void hide(final View view, boolean positive) {
    int height;
    if (positive) {
      height = view.getHeight();
    } else {
      height = -view.getHeight();
    }
    ViewAnimator.animate(view).translationY(0, height).alpha(1, 0).duration(500).onStop(() -> view.setVisibility(View.GONE)).start();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }


  @Override public void onItemClick(View view, int position) {
    PreStorySampleModel sampleModel = preStorySampleModels.get(position);
    //reset map tile and marker
    hide(linearTile, true);
    lastClickedMarker = null;
    if (position == 0) {
      onFilter(relativeRaceTrack);
      unCheckList();
      filterRacetrackIds = null;
      fetchStories(true);
    } else {
      sampleModel.setCheck(!sampleModel.isCheck());
      raceTrackAdapter.notifyItemChanged(position);
      StringBuilder ids = new StringBuilder();
      for (PreStorySampleModel preStorySampleModel : preStorySampleModels) {
        if (preStorySampleModel.isCheck()) {
          ids.append(preStorySampleModel.getId()).append(",");
        }
      }
      Log.d(TAG, "onItemClick: filterRacetrackIds = " + ids.toString());
      if (ids.length() == 0) { // unselected all
        filterRacetrackIds = null;
        fetchStories(true);
      } else {
        filterRacetrackIds = ids.toString().substring(0, ids.length() - 1);
        fetchStories(true);
      }
    }
    tvSelectTrack.setText(sampleModel.getValue());
  }

  /**
   * unCheck all racetrack item
   */
  private void unCheckList() {
    for (PreStorySampleModel storySampleModel : preStorySampleModels) {
      storySampleModel.setCheck(false);
    }
    raceTrackAdapter.notifyDataSetChanged();
  }


  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    this.menu = menu;
    inflater.inflate(R.menu.story_menu, menu);
    MenuItem myActionMenuItem = menu.findItem(R.id.nav_search);
    final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override public boolean onQueryTextChange(String newText) {
        lastClickedMarker = null;
        if (TextUtils.isEmpty(newText)) {
          filterSearchString = null;
        } else {
          filterSearchString = newText;
        }
        fetchStories(true);
        return true;
      }
    });

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    switch (id) {
      case R.id.nav_search:
        break;

      case R.id.nav_map:
        isList = !isList;
        if (isList) {
          menu.getItem(1).setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_map_view));
          mMapView.setVisibility(View.GONE);
          hide(linearTile, true);
          recyclerView.setVisibility(View.VISIBLE);
        } else {
          menu.getItem(1).setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_list));
          mMapView.setVisibility(View.VISIBLE);
          recyclerView.setVisibility(View.GONE);
          initMarker();
        }
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * when location changed
   *
   * @param event
   */
  @Subscribe public void onLocationUpdated(LocationUpdatedEvent event) {
    storyAdapter.notifyDataSetChanged();
    fetchStories(true);
    fetchRacetracks();
    if (!isList) {
      initMarker();
    }
  }

  @Override public void onMapReady(GoogleMap map) {
    Log.d(TAG, "onMapReady: " + map);
    this.map = map;
    this.map.setOnMarkerClickListener(this);
  }

  /**
   * add markers on map
   */
  private void initMarker() {
    if (map == null) {
      ToastUtils.showShort("Google map init failed, please make sure you installed google Play store");
      return;
    }

    map.clear();
    if (storyModelList.isEmpty()) return;

    LatLngBounds.Builder latLongBounds = new LatLngBounds.Builder();

    for (TrackStory model : storyModelList) {
      if (model.getRacetrack() == null
          || model.getRacetrack().getLocationLat() == null
          || model.getRacetrack().getLocationLng() == null) {
        continue;
      }

      LatLng latlng = new LatLng(model.getRacetrack().getLocationLat(), model.getRacetrack().getLocationLng());
      latLongBounds.include(latlng);
      Marker marker = map.addMarker(new MarkerOptions().position(latlng).title(model.getTitle())
          .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_green)));
      marker.setTag(model);
      mMarkerList.add(marker);
    }

    LatLngBounds bounds = latLongBounds.build();
    int width = getResources().getDisplayMetrics().widthPixels;
    int height = getResources().getDisplayMetrics().heightPixels
        - (int) AppUtils.convertDpToPixel(220, ActivityUtils.getTopActivity());
    int padding = (int) (width * 0.05); // offset from edges of the map 5% of screen
    map.setOnMapLoadedCallback(() -> map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)));
  }

  /**
   * show tile as per marker click
   *
   * @param marker clicked marker
   * @return boolean
   */
  @Override public boolean onMarkerClick(Marker marker) {
    if (lastClickedMarker != null && lastClickedMarker.getPosition().equals(marker.getPosition())) {
      return true;
    }
    if (lastClickedMarker != null && lastClickedMarker.isVisible()) {
      lastClickedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_green));
    }
    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_orange));
    lastClickedMarker = marker;
    show(linearTile, true);
    TrackStory model = (TrackStory) marker.getTag();
    if (model != null) {
      setMapData(model);
    }
    return true;
  }

  public class ViewHolder {
    @BindView(R.id.imgStory) ImageView imgStory;
    @BindView(R.id.tvStoryTitle) TextView tvStoryTitle;
    @BindView(R.id.tvRaceCourse) TextView tvRaceCourse;
    @BindView(R.id.tvDescription) TextView tvDescription;
    @BindView(R.id.tvChapter) TextView tvChapter;
    @BindView(R.id.tvCard) TextView tvCard;
    @BindView(R.id.tvDistance) TextView tvDistance;
    @Setter private TrackStory story;

    @Setter View view;

    public void hide() {
      if (this.view != null) {
        this.view.setVisibility(View.INVISIBLE);
      }
    }


    public void show() {
      if (this.view != null) {
        this.view.setVisibility(View.VISIBLE);
      }
    }

    public ViewHolder(View itemView) {
      ButterKnife.bind(this, itemView);
      this.view = itemView;
      itemView.setOnClickListener(v -> {
        if (story == null) {
          ToastUtils.showShort("story is null, cannot jump");
          return;
        }
        Intent intent = new Intent(ActivityUtils.getTopActivity(), BrowseStoryActivity.class);
        intent.putExtra(BrowseStoryActivity.PASS_STORY_KEY, story.getId());
        ActivityUtils.startActivity(intent);
      });
    }
  }

  private void setMapData(TrackStory model) {
    bottomTileHolder.show();
    GlideApp.with(this).load(model.getSmallImageURL()).into(bottomTileHolder.imgStory);
    bottomTileHolder.setStory(model);
    bottomTileHolder.tvStoryTitle.setText(model.getTitle());
    bottomTileHolder.tvRaceCourse.setText(model.getSubtitle());
    bottomTileHolder.tvDescription.setText(model.getDescription());
    bottomTileHolder.tvChapter.setText(getResources().getQuantityString(R.plurals.chapter, model.getChapters().size(), model.getChapters().size()));
    bottomTileHolder.tvCard.setText(getResources().getQuantityString(R.plurals.card, model.getCards().size(), model.getCards().size()));
    bottomTileHolder.tvDistance.setText(AppUtils.getDistance(model.getRacetrack()));
  }


  @Override public void onResume() {
    if (mMapView != null) mMapView.onResume();
    super.onResume();
  }

  @Override public void onDestroy() {
    if (mMapView != null) mMapView.onDestroy();
    EventBus.getDefault().unregister(this);
    super.onDestroy();
  }

  @Override public void onLowMemory() {
    if (mMapView != null) mMapView.onLowMemory();
    super.onLowMemory();
  }
}
