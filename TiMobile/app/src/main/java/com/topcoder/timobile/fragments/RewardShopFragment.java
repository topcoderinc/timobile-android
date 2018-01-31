package com.topcoder.timobile.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
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

import com.blankj.utilcode.util.ToastUtils;
import com.timobileapp.R;
import com.topcoder.timobile.adapter.RewardShopAdapter;
import com.topcoder.timobile.baseclasses.BaseFragment;
import com.topcoder.timobile.dialogs.BuyCardDialog;
import com.topcoder.timobile.model.Card;
import com.topcoder.timobile.model.PageResult;
import com.topcoder.timobile.model.User;
import com.topcoder.timobile.model.event.BuyCardEvent;
import com.topcoder.timobile.utility.AppConstants;
import com.topcoder.timobile.utility.AppUtils;
import com.topcoder.timobile.utility.ItemDecorationAlbumColumns;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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

  private List<Card> rewardShopModels = new ArrayList<>();
  private RewardShopAdapter adapter;
  private int offset = 0;
  private String seachName = null;
  private String TAG = RewardShopFragment.class.getName();
  private BuyCardDialog buyCardDialog;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    EventBus.getDefault().register(this);
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
    fetchCards(false);
    updateTotalPoint();
    apiService.getUser().subscribe(this::onFectchUserSucceed, this::onError);
  }

  /**
   * update total points
   */
  private void updateTotalPoint() {
    User user = apiService.getCurrentUser();
    String points = user.getPointsAmount() + " pts";
    tvRewardPoints.setText(points);
  }

  /**
   * on fetch current user succeed
   *
   * @param user the newest user
   */
  private void onFectchUserSucceed(User user) {
    updateTotalPoint();
  }

  /**
   * fetch cards
   *
   * @param clear clear card
   */
  private void fetchCards(boolean clear) {
    if (clear) {
      rewardShopModels.clear();
      offset = 0;
    }
    apiService.searchCards(seachName, null, "Market", offset,
        AppConstants.DEFAULT_LIMIT, "pricePoints", "asc").subscribe(this::onSuccess, this::onError);
  }

  private void onError(Throwable throwable) {
    Timber.d(throwable);
    AppUtils.showError(throwable, getString(R.string.error));
  }

  private void onSuccess(PageResult<Card> cardPageResult) {
    Log.d(TAG, "onSuccess: ");
    rewardShopModels.addAll(cardPageResult.getItems());
    updateUI();
  }

  /**
   * update list
   */
  private void updateUI() {
    Log.d(TAG, "updateUI: ");
    adapter = new RewardShopAdapter(getActivity(), this.rewardShopModels);
    recyclerView.setAdapter(adapter);
  }

  /**
   * open dialog or send request to backend buy card
   *
   * @param event the click event
   */
  @Subscribe public void onItemClickEvent(BuyCardEvent event) {
    if (event.getEvent().equals("open")) {
      buyCardDialog = BuyCardDialog.newInstance(event.getCard());
      buyCardDialog.show(getFragmentManager(), buyCardDialog.getClass().getName());
    } else {
      Log.d(TAG, "onItemClickEvent: buy card");
      showLoadingDialog();
      apiService.buyCard(event.getCard().getId()).subscribe(userCard -> {
        cancelLoadingDialog();
        this.updateTotalPoint();
        ToastUtils.showLong("You succeed cost " + event.getCard().getPricePoints() + " pts bought a Card");
        buyCardDialog.dismiss();
      }, throwable -> {
        cancelLoadingDialog();
        Timber.e(throwable);
        AppUtils.showError(throwable, "bought card failed");
      });
    }
  }


  @Override public void onDestroyView() {
    EventBus.getDefault().unregister(this);
    super.onDestroyView();
    unbinder.unbind();
  }

  /**
   * show result as per search
   *
   * @param menu     menu
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
        if (newText.length() == 0) {
          seachName = null;
        } else {
          seachName = newText;
        }
        fetchCards(true);
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
