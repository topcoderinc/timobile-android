package com.topcoder.timobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.blankj.utilcode.util.ActivityUtils;
import com.timobileapp.R;
import com.topcoder.timobile.activity.BrowseStoryActivity;
import com.topcoder.timobile.adapter.StoryAdapter;
import com.topcoder.timobile.baseclasses.BaseFragment;
import com.topcoder.timobile.model.StoryModel;
import com.topcoder.timobile.model.event.RemoveEvent;
import com.topcoder.timobile.utility.AppConstants;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

/**
 * Author: Harshvardhan
 * Date: 01/11/17
 */

public class BookmarkFragment extends BaseFragment {

  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  Unbinder unbinder;

  private StoryAdapter adapter;
  private List<StoryModel> storyModelList = new ArrayList<>();
  private int selectPosition;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_bookmark, container, false);
    unbinder = ButterKnife.bind(this, view);
    EventBus.getDefault().register(this);
    return view;
  }



  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setTitle(R.string.my_bookmark);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    apiService.getBookmarks().subscribe(this::onSuccess, this::onError);
  }

  private void onError(Throwable throwable) {
    Timber.d(throwable);
  }

  private void onSuccess(List<StoryModel> storyModelList) {
    this.storyModelList.addAll(storyModelList);
    adapter = new StoryAdapter(getActivity(), this.storyModelList);
    recyclerView.setAdapter(adapter);
    adapter.setRecycleOnItemClickListner((view, position) -> {
      selectPosition = position;
      Intent intent = new Intent(getActivity(), BrowseStoryActivity.class);
      intent.putExtra(AppConstants.KEY_BOOl, true);
      ActivityUtils.startActivity(intent);
    });
  }

  /**
   * remove unBookmark from list
   * @param event remove event
   */
  @Subscribe public void onEvent(RemoveEvent event) {
    if (event.isRemove()) {
      storyModelList.remove(selectPosition);
      adapter.notifyItemRemoved(selectPosition);
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    EventBus.getDefault().unregister(this);
    unbinder.unbind();
  }
}
