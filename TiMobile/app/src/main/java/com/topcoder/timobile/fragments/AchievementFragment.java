package com.topcoder.timobile.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ToastUtils;
import com.timobileapp.R;
import com.topcoder.timobile.adapter.UserAchievementAdapter;
import com.topcoder.timobile.adapter.UserDailyTaskAdapter;
import com.topcoder.timobile.baseclasses.BaseFragment;
import com.topcoder.timobile.model.UserAchievement;
import com.topcoder.timobile.utility.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Author: Harshvardhan
 * Date: 01/11/17
 */

public class AchievementFragment extends BaseFragment {

  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  Unbinder unbinder;

  private UserAchievementAdapter adapter;


  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.recyclerview, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    apiService.getCurrentUserAchievements().subscribe(this::onSuccess, this::onError);
  }

  private void onError(Throwable throwable) {
    Timber.e(throwable);
    AppUtils.showError(throwable, "cannot get user achievements");
  }

  private void onSuccess(List<UserAchievement> userAchievementList) {
    if (userAchievementList == null || userAchievementList.size() < 0) {
      ToastUtils.showShort("you don't have any achievements");
    }
    adapter = new UserAchievementAdapter(getActivity(), userAchievementList);
    recyclerView.setAdapter(adapter);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}
