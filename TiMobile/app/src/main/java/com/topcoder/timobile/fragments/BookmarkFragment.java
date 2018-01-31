package com.topcoder.timobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ToastUtils;
import com.timobileapp.R;
import com.topcoder.timobile.activity.BrowseStoryActivity;
import com.topcoder.timobile.adapter.StoryAdapter;
import com.topcoder.timobile.baseclasses.BaseFragment;
import com.topcoder.timobile.model.Bookmark;
import com.topcoder.timobile.model.PageResult;
import com.topcoder.timobile.model.TrackStory;
import com.topcoder.timobile.model.event.LocationUpdatedEvent;
import com.topcoder.timobile.model.event.RemoveEvent;
import com.topcoder.timobile.utility.AppConstants;
import com.topcoder.timobile.utility.AppUtils;
import com.topcoder.timobile.utility.LoadMore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Author: Harshvardhan
 * Date: 01/11/17
 */

public class BookmarkFragment extends BaseFragment {

  public static final int REQUEST_VIEW_STORY = 1001;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  Unbinder unbinder;

  private StoryAdapter adapter;
  private List<TrackStory> trackStories = new ArrayList<>();
  private int selectPosition;
  private String racetrackIds = "";
  private int offset = 0;
  private LoadMore loadMore;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_bookmark, container, false);
    unbinder = ButterKnife.bind(this, view);
    EventBus.getDefault().register(this);
    loadMore = new LoadMore(recyclerView);
    loadMore.setOnLoadMoreListener(() -> {
      fetchStories(false);
    });
    return view;
  }

  /**
   * when location changed
   *
   * @param event
   */
  @Subscribe public void onLocationUpdated(LocationUpdatedEvent event) {
    adapter.notifyDataSetChanged();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setTitle(R.string.my_bookmark);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    this.fetchBookmarks();
  }

  /**
   * fetch bookmarks
   */
  private void fetchBookmarks() {
    racetrackIds = "";
    apiService.getRacetrackBookmarks().subscribe(bl -> {
      StringBuilder stringBuilder = new StringBuilder();
      for (Bookmark bookmark : bl) {
        stringBuilder.append(bookmark.getRacetrack().getId()).append(",");
      }
      racetrackIds = stringBuilder.toString();
      if (racetrackIds.length() <= 0) {
        ToastUtils.showShort("you don't have any bookmark yet.");
      } else {
        racetrackIds = racetrackIds.substring(0, racetrackIds.length() - 1);
        this.fetchStories(true);
      }
    }, this::onError);
  }

  /**
   * fetch stories
   */
  private void fetchStories(boolean clean) {
    if (clean) {
      offset = 0;
      this.trackStories.clear();
      adapter = new StoryAdapter(getActivity(), this.trackStories);
      recyclerView.setAdapter(adapter);
    }

    apiService.getTrackStories(null, null, racetrackIds, null, offset,
        AppConstants.DEFAULT_LIMIT, null, null)
        .subscribe(this::onSuccess, throwable -> {
          loadMore.loadedDone();
          this.onError(throwable);
        });
  }

  private void onError(Throwable throwable) {
    Timber.d(throwable);
    AppUtils.showError(throwable, getString(R.string.error));
  }

  private void onSuccess(PageResult<TrackStory> storyPageResult) {
    offset += storyPageResult.getItems().size();
    loadMore.loadedDone();
    this.trackStories.addAll(storyPageResult.getItems());
    adapter = new StoryAdapter(getActivity(), this.trackStories);
    recyclerView.setAdapter(adapter);
    adapter.setRecycleOnItemClickListner((view, position) -> {
      selectPosition = position;
      TrackStory story = this.trackStories.get(position);
      Intent intent = new Intent(getActivity(), BrowseStoryActivity.class);
      intent.putExtra(BrowseStoryActivity.PASS_STORY_KEY, story.getId());
      startActivityForResult(intent, REQUEST_VIEW_STORY);
    });
  }


  /**
   * remove unBookmark from list
   *
   * @param event remove event
   */
  @Subscribe public void onEvent(RemoveEvent event) {
    if (event.isRemove()) {
      this.trackStories.remove(selectPosition);
      adapter.notifyItemRemoved(selectPosition);
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    EventBus.getDefault().unregister(this);
    unbinder.unbind();
  }
}
