package com.topcoder.timobile.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.timobileapp.R;
import com.topcoder.timobile.adapter.FAQAdapter;
import com.topcoder.timobile.baseclasses.BaseFragment;
import com.topcoder.timobile.model.FAQSectionModel;
import java.util.List;
import timber.log.Timber;

/**
 * Author: Harshvardhan
 * Date: 01/11/17
 */

public class FAQFragment extends BaseFragment {

  Unbinder unbinder;
  @BindView(R.id.expandListView) ExpandableListView mExpandableList;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_faq, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    apiService.getFAQs().subscribe(this::onSuccess, this::onError);
    mExpandableList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
      // Keep track of previous expanded parent
      int previousGroup = -1;

      @Override
      public void onGroupExpand(int groupPosition) {
        // Collapse previous parent if expanded.
        if ((previousGroup != -1) && (groupPosition != previousGroup)) {
          mExpandableList.collapseGroup(previousGroup);
        }
        previousGroup = groupPosition;
      }
    });
  }

  private void onError(Throwable throwable) {
    Timber.d(throwable);
  }

  private void onSuccess(List<FAQSectionModel> faqSectionModels) {
    mExpandableList.setAdapter(new FAQAdapter(getActivity(), faqSectionModels));
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}
