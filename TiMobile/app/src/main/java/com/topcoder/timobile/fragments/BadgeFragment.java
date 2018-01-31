package com.topcoder.timobile.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.timobileapp.R;
import com.topcoder.timobile.adapter.BadgeAdapter;
import com.topcoder.timobile.baseclasses.BaseFragment;
import com.topcoder.timobile.dialogs.BadgeDialogFragment;
import com.topcoder.timobile.model.UserBadge;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author: Harshvardhan
 * Date: 01/11/17
 */

public class BadgeFragment extends BaseFragment {

  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  Unbinder unbinder;
  private List<UserBadge> badgeModelList = new ArrayList<>();
  private BadgeAdapter adapter;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.recyclerview, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
  }


  public void onSuccess(List<UserBadge> badgeModels) {
    badgeModelList = badgeModels;
    adapter = new BadgeAdapter(getActivity(), badgeModelList);
    recyclerView.setAdapter(adapter);
    adapter.setRecycleOnItemClickListner((view, position) -> {
      UserBadge userBadge = badgeModels.get(position);
      BadgeDialogFragment dialogFragment = BadgeDialogFragment.newInstance(userBadge.getBadge());
      dialogFragment.show(getChildFragmentManager(), dialogFragment.getClass().getName());
    });
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}
