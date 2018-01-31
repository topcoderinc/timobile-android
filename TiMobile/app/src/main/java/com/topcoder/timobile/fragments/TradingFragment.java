package com.topcoder.timobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
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
import com.topcoder.timobile.adapter.TradingCardAdapter;
import com.topcoder.timobile.baseclasses.BaseFragment;
import com.topcoder.timobile.dialogs.TradingCardDialog;
import com.topcoder.timobile.model.UserCard;
import com.topcoder.timobile.model.event.GotoStoryEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Author: Harshvardhan
 * Date: 01/11/17
 */

public class TradingFragment extends BaseFragment {

  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  Unbinder unbinder;
  private TradingCardAdapter adapter;


  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EventBus.getDefault().register(this);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.recyclerview, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

  }

  public void onSuccess(List<UserCard> tradingCardModels) {
    adapter = new TradingCardAdapter(tradingCardModels);
    recyclerView.setAdapter(adapter);
    adapter.setRecycleOnItemClickListner((view, position) -> {
      UserCard userCard = tradingCardModels.get(position);
      TradingCardDialog dialog = TradingCardDialog.newInstance(userCard.getCard(), apiService);
      dialog.show(getChildFragmentManager(), dialog.getClass().getName());
    });
  }

  @Subscribe public void onGotoStory(GotoStoryEvent event) {
    Intent intent = new Intent(ActivityUtils.getTopActivity(), BrowseStoryActivity.class);
    intent.putExtra(BrowseStoryActivity.PASS_STORY_KEY, event.getStoryId());
    ActivityUtils.startActivity(intent);
  }

  @Override public void onDestroyView() {
    unbinder.unbind();
    EventBus.getDefault().unregister(this);
    super.onDestroyView();
  }
}
