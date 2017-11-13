package com.topcoder.timobile.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.timobileapp.R;
import com.topcoder.timobile.adapter.RewardShopAdapter;
import com.topcoder.timobile.baseclasses.BaseFragment;
import com.topcoder.timobile.model.RewardShopModel;
import com.topcoder.timobile.utility.ItemDecorationAlbumColumns;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

/**
 * Author: Harshvardhan
 * Date: 01/11/17
 */

public class RewardShopFragment extends BaseFragment {

  @BindView(R.id.tvRewardPoints) TextView tvRewardPoints;
  @BindView(R.id.tvOrderBy) TextView tvOrderBy;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  Unbinder unbinder;

  private List<RewardShopModel> rewardShopModels = new ArrayList<>();
  private RewardShopAdapter adapter;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_reward_shop, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setTitle(R.string.rewards_shop);
    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    ItemDecorationAlbumColumns decoration = new ItemDecorationAlbumColumns(1, 2);
    recyclerView.addItemDecoration(decoration);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    apiService.getRewardShop().subscribe(this::onSuccess, this::onError);
  }

  private void onError(Throwable throwable) {
    Timber.d(throwable);
  }

  private void onSuccess(List<RewardShopModel> rewardShopModels) {
    this.rewardShopModels.addAll(rewardShopModels);
    adapter = new RewardShopAdapter(getActivity(), this.rewardShopModels);
    recyclerView.setAdapter(adapter);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  /**
   * show result as per search
   * @param menu menu
   * @param inflater inflater
   */
  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.card_shop_menu, menu);
    MenuItem myActionMenuItem = menu.findItem(R.id.search);
    final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
    searchView.setOnSearchClickListener(v -> setTitle(R.string.empty_string));
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextSubmit(String query) {

        return false;
      }

      @Override public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return true;
      }
    });
    searchView.setOnCloseListener(() -> {
      setTitle(R.string.rewards_shop);
      return false;
    });

    super.onCreateOptionsMenu(menu, inflater);
  }
}
