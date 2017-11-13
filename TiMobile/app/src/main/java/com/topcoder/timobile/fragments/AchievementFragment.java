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
import com.topcoder.timobile.adapter.TiMobileAdapter;
import com.topcoder.timobile.baseclasses.BaseFragment;
import com.topcoder.timobile.model.TiMobileModel;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

/**
 * Author: Harshvardhan
 * Date: 01/11/17
 */

public class AchievementFragment extends BaseFragment {

  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  Unbinder unbinder;

  private TiMobileAdapter adapter;
  private List<TiMobileModel> tiMobileModels = new ArrayList<>();

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.recyclerview, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    apiService.getTiPoints().subscribe(this::onSuccess, this::onError);
  }

  private void onError(Throwable throwable) {
    Timber.d(throwable);
  }

  private void onSuccess(List<TiMobileModel> tiMobileModelList) {
    this.tiMobileModels.addAll(tiMobileModelList);
    adapter = new TiMobileAdapter(getActivity(), tiMobileModelList);
    recyclerView.setAdapter(adapter);
    adapter.setRecycleOnItemClickListner((view, position) -> ToastUtils.showShort("Redeem"));
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}
