package com.topcoder.timobile.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
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
import com.topcoder.timobile.model.PreStorySampleModel;
import com.topcoder.timobile.model.StoryModel;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
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
  Unbinder unbinder;
  private List<PreStorySampleModel> preStorySampleModels = new ArrayList<>();
  private PreStoryListAdapter raceTrackAdapter;

  private List<StoryModel> storyModelList = new ArrayList<>();
  private StoryAdapter storyAdapter;
  private Menu menu;
  private boolean isList = true;
  private final List<Marker> mMarkerList = new ArrayList<>();
  private GoogleMap map;
  private Marker lastClickedMarker = null;
  private ViewHolder bottomTileHolder;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_story_location, container, false);
    unbinder = ButterKnife.bind(this, view);
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

    apiService.getStoryList().subscribe(this::onSuccess, this::onError);
    apiService.raceTrackList().subscribe(this::onTrackSuccess, this::onTrackError);
  }

  private void onError(Throwable throwable) {
    Timber.e(throwable);
    ToastUtils.showShort(R.string.error);
  }

  private void onSuccess(List<StoryModel> storyModels) {
    storyModelList.addAll(storyModels);
    storyAdapter = new StoryAdapter(getActivity(), storyModelList);
    recyclerView.setAdapter(storyAdapter);
    storyAdapter.setRecycleOnItemClickListner(recycleOnItemClickListener);
  }

  BaseRecyclerAdapter.RecycleOnItemClickListener recycleOnItemClickListener = (view, position) -> ActivityUtils.startActivity(BrowseStoryActivity.class);

  private void onTrackError(Throwable throwable) {
    Timber.e(throwable);
    ToastUtils.showShort(R.string.error);
  }

  private void onTrackSuccess(List<PreStorySampleModel> preStorySampleModels) {
    PreStorySampleModel storySampleModel = new PreStorySampleModel();
    storySampleModel.setId(0);
    storySampleModel.setName("All Racetracks");
    storySampleModel.setCheck(false);
    this.preStorySampleModels.add(storySampleModel);
    this.preStorySampleModels.addAll(preStorySampleModels);
    raceTrackAdapter = new PreStoryListAdapter(getActivity(), this.preStorySampleModels);
    filterRecyclerView.setAdapter(raceTrackAdapter);
    raceTrackAdapter.setRecycleOnItemClickListner(this);
    tvSelectTrack.setText(storySampleModel.getName());
  }

  @OnClick({ R.id.relativeRaceTrack, R.id.relativeSortBy }) void onFilter(View view) {
    int id = view.getId();
    switch (id) {
      case R.id.relativeRaceTrack:
        if (filterRecyclerView.isShown()) {
          hide(filterRecyclerView, false);
          imgDown.setRotation(0);
        } else {
          imgDown.setRotation(180);
          show(filterRecyclerView, false);
        }
        break;
    }
  }

  /**
   * show view animation
   * @param view view
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
   * @param view view
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

  private List<PreStorySampleModel> selectedRaceTrack = new ArrayList<>();

  @Override public void onItemClick(View view, int position) {
    PreStorySampleModel sampleModel = preStorySampleModels.get(position);
    //reset map tile and marker
    hide(linearTile, true);
    lastClickedMarker = null;
    if (position == 0) {
      onFilter(relativeRaceTrack);
      selectedRaceTrack.clear();
      storyAdapter.setData(storyModelList);
      initMarker(storyModelList);
      unCheckList();
    } else {
      PreStorySampleModel model = IterableUtils.find(selectedRaceTrack, object -> sampleModel.getName().equalsIgnoreCase(object.getName()));
      if (model != null) {
        sampleModel.setCheck(false);
        selectedRaceTrack.remove(model);
      } else {
        sampleModel.setCheck(true);
        selectedRaceTrack.add(sampleModel);
      }
      raceTrackAdapter.notifyItemChanged(position);
      filterList(selectedRaceTrack);
    }
    tvSelectTrack.setText(sampleModel.getName());
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

  /**
   * filter list for selected racetrack
   * @param selectModels selected racetrack
   */
  private void filterList(List<PreStorySampleModel> selectModels) {
    List<StoryModel> storyModelArrayList = new ArrayList<>();
    for (PreStorySampleModel model : selectModels) {
      Iterable<StoryModel> modelList = IterableUtils.filteredIterable(storyModelList, object -> model.getName().equalsIgnoreCase(object.getRaceCourse()));
      storyModelArrayList.addAll(IterableUtils.toList(modelList));
    }
    storyAdapter.setData(storyModelArrayList);
    initMarker(storyModelArrayList);
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
          storyAdapter.setData(storyModelList);
          initMarker(storyModelList);
        } else {
          if (linearTile.isShown()) {
            lastClickedMarker = null;
            hide(linearTile, true);
          }
          filterable.getFilter().filter(newText);
        }
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

          if (mMarkerList.isEmpty()) {
            initMarker(storyModelList);
          }
        }

        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void onMapReady(GoogleMap map) {
    this.map = map;
    this.map.setOnMarkerClickListener(this);
  }

  /**
   * add markers on map
   *
   * @param storyModelList story model for locations
   */
  private void initMarker(List<StoryModel> storyModelList) {
    map.clear();
    if (storyModelList.isEmpty()) return;
    LatLngBounds.Builder latLongBounds = new LatLngBounds.Builder();
    for (StoryModel model : storyModelList) {
      LatLng latlng = new LatLng(Double.parseDouble(model.getLat()), Double.parseDouble(model.getLng()));
      latLongBounds.include(latlng);
      Marker marker = map.addMarker(new MarkerOptions().position(latlng).title(model.getTitle()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_green)));
      marker.setTag(model);
      mMarkerList.add(marker);
    }
    LatLngBounds bounds = latLongBounds.build();
    map.setOnMapLoadedCallback(() -> map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50)));
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
    StoryModel model = (StoryModel) marker.getTag();
    setData(model);
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

    public ViewHolder(View itemView) {
      ButterKnife.bind(this, itemView);
      itemView.setOnClickListener(v -> ActivityUtils.startActivity(BrowseStoryActivity.class));
    }
  }

  private void setData(StoryModel model) {
    GlideApp.with(this).load(model.getImage()).into(bottomTileHolder.imgStory);
    bottomTileHolder.tvStoryTitle.setText(model.getTitle());
    bottomTileHolder.tvRaceCourse.setText(model.getRaceCourse());
    bottomTileHolder.tvDescription.setText(model.getDescription());
    bottomTileHolder.tvChapter.setText(getResources().getQuantityString(R.plurals.chapter, Integer.parseInt(model.getChapter()), model.getChapter()));
    bottomTileHolder.tvCard.setText(getResources().getQuantityString(R.plurals.card, Integer.parseInt(model.getCard()), model.getCard()));
    bottomTileHolder.tvDistance.setText(getResources().getQuantityString(R.plurals.distance, Integer.parseInt(model.getDistance()), model.getDistance()));
  }

  private List<StoryModel> mFilteredList = new ArrayList<>();

  //search filter
  Filterable filterable = () -> new Filter() {
    @Override protected FilterResults performFiltering(CharSequence charSequence) {

      String charString = charSequence.toString();

      ArrayList<StoryModel> filteredList = new ArrayList<>();

      for (StoryModel model : storyModelList) {

        if (model.getTitle().toLowerCase().contains(charString) || model.getDescription().toLowerCase().contains(charString) || model.getRaceCourse().toLowerCase().contains(charSequence)) {

          filteredList.add(model);
        }
      }

      mFilteredList = filteredList;

      FilterResults filterResults = new FilterResults();
      filterResults.values = mFilteredList;
      return filterResults;
    }

    @Override protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
      mFilteredList = (ArrayList<StoryModel>) filterResults.values;
      storyAdapter.setData(mFilteredList);
      initMarker(mFilteredList);
    }
  };

  @Override public void onResume() {
    if (mMapView != null) mMapView.onResume();
    super.onResume();
  }

  @Override public void onDestroy() {
    if (mMapView != null) mMapView.onDestroy();
    super.onDestroy();
  }

  @Override public void onLowMemory() {
    if (mMapView != null) mMapView.onLowMemory();
    super.onLowMemory();
  }
}
