package com.topcoder.timobile.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import lombok.Setter;

import com.blankj.utilcode.util.ToastUtils;
import com.timobileapp.R;
import com.topcoder.timobile.adapter.PreStoryListAdapter;
import com.topcoder.timobile.baseclasses.BaseFragment;
import com.topcoder.timobile.baseclasses.BaseRecyclerAdapter;
import com.topcoder.timobile.model.PageResult;
import com.topcoder.timobile.model.PreStorySampleModel;
import com.topcoder.timobile.utility.AppConstants;
import com.topcoder.timobile.utility.AppUtils;
import com.topcoder.timobile.utility.LoadMore;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.IterableUtils;

/**
 * Author: Harshvardhan
 * Date: 27/10/17
 */

public class RaceTrackFragment extends BaseFragment implements BaseRecyclerAdapter.RecycleOnItemClickListener {

  @BindView(R.id.etFilter) EditText etFilter;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  Unbinder unbinder;

  private List<PreStorySampleModel> sampleModelList = new ArrayList<>();
  private PreStoryListAdapter adapter;

  private String TAG = RaceTrackFragment.class.getName();
  private LoadMore loadMore;
  @Setter private String stateIds;
  /**
   * the offset
   */
  private int offset = 0;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_pre_story_list, container, false);
    unbinder = ButterKnife.bind(this, view);
    loadMore = new LoadMore(recyclerView);
    loadMore.setOnLoadMoreListener(() -> fetchRacetracks(false));
    return view;
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
  }

  private void onError(Throwable throwable) {
    AppUtils.showError(throwable, "Could not get track information");
    loadMore.loadedDone();
  }

  private void onSuccess(PageResult<PreStorySampleModel> trackPageResult) {
    sampleModelList.addAll(trackPageResult.getItems());
    offset += trackPageResult.getItems().size();
    loadMore.loadedDone();
    if (adapter == null) {
      adapter = new PreStoryListAdapter(getActivity(), sampleModelList);
      recyclerView.setAdapter(adapter);
      adapter.setRecycleOnItemClickListner(this);
    } else {
      adapter.notifyDataSetChanged();
    }
  }


  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @OnTextChanged(R.id.etFilter) void onTextChange(Editable text) {
    if (adapter != null) adapter.getFilter().filter(text.toString());
  }

  @Override public void onItemClick(View view, int position) {

    final PreStorySampleModel selectModel = adapter.getModel(position);
    PreStorySampleModel model = IterableUtils.find(sampleModelList, object -> selectModel.getId() == object.getId());

    if (model != null) {
      int pos = sampleModelList.indexOf(model);
      boolean isCheck = model.isCheck();
      sampleModelList.get(pos).setCheck(!isCheck);
      adapter.notifyItemChanged(position);
    }
  }

  /**
   * check at least one item selected
   *
   * @return validation result
   */
  public boolean checkValidation() {
    boolean success = false;
    for (PreStorySampleModel model : sampleModelList) {
      if (model.isCheck()) {
        success = true;
        break;
      }
    }
    return success;
  }

  /**
   * fetch racetracks by state ids
   *
   * @param clear is that need clear
   */
  public void fetchRacetracks(boolean clear) {
    if (clear) {
      sampleModelList.clear();
      offset = 0;
      if (adapter != null) {
        adapter.notifyDataSetChanged();
      }
    }
    apiService.getRacetracks(null, stateIds, null, null, null, offset, AppConstants.DEFAULT_LIMIT, null, null)
        .subscribe(this::onSuccess, this::onError);
  }

  /**
   * get select ids
   *
   * @return the ids
   */
  public List<Long> getSelected() {
    List<Long> ids = new ArrayList<Long>();
    for (PreStorySampleModel model : sampleModelList) {
      if (model.isCheck()) {
        ids.add(model.getId());
      }
    }
    return ids;
  }
}
