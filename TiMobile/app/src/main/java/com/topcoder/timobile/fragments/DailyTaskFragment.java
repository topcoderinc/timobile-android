package com.topcoder.timobile.fragments;

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

import com.blankj.utilcode.util.ToastUtils;
import com.timobileapp.R;
import com.topcoder.timobile.adapter.UserDailyTaskAdapter;
import com.topcoder.timobile.baseclasses.BaseFragment;
import com.topcoder.timobile.model.UserDailyTask;
import com.topcoder.timobile.model.event.UserUpdateEvent;
import com.topcoder.timobile.utility.AppUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Author: Harshvardhan
 * Date: 01/11/17
 */

public class DailyTaskFragment extends BaseFragment {

  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  Unbinder unbinder;

  private UserDailyTaskAdapter adapter;
  private List<UserDailyTask> dailyTaskArrayList = new ArrayList<>();

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.recyclerview, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    apiService.getCurrentUserDailyTasks().subscribe(this::onSuccess, this::onError);
  }

  private void onError(Throwable throwable) {
    Timber.d(throwable);
    AppUtils.showError(throwable, "Redeem failed.");
  }

  private void onSuccess(List<UserDailyTask> userDailyTasks) {
    this.dailyTaskArrayList = userDailyTasks;
    adapter = new UserDailyTaskAdapter(getActivity(), this.dailyTaskArrayList);
    recyclerView.setAdapter(adapter);
    adapter.setRecycleOnItemClickListner((view, position) -> {
      UserDailyTask userDailyTask = this.dailyTaskArrayList.get(position);
      if (userDailyTask.getCompleted()) return;
      apiService.completedDailyTask(userDailyTask.getId()).subscribe(task -> {
        userDailyTask.setCompleted(true);
        ToastUtils.showShort("Task completed, you got " + userDailyTask.getDailyTask().getPoints() + " pts");
        adapter.notifyItemChanged(position);
        EventBus.getDefault().post(new UserUpdateEvent());
      }, this::onError);
    });
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}
