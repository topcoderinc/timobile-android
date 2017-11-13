package com.topcoder.timobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.timobileapp.R;
import com.topcoder.timobile.model.FAQSectionModel;
import java.util.List;

/**
 * Author: Harshvardhan
 * Date: 01/11/17
 */

public class FAQAdapter extends BaseExpandableListAdapter {

  private final Context context;
  private final List<FAQSectionModel> mSectionModelList;

  public FAQAdapter(Context context, List<FAQSectionModel> mSectionModelList) {
    this.context = context;
    this.mSectionModelList = mSectionModelList;
  }

  @Override public int getGroupCount() {
    return mSectionModelList.size();
  }

  @Override public int getChildrenCount(int groupPosition) {
    return mSectionModelList.get(groupPosition).getModelList().size();
  }

  @Override public Object getGroup(int groupPosition) {
    return mSectionModelList.get(groupPosition);
  }

  @Override public Object getChild(int groupPosition, int childPosition) {
    return mSectionModelList.get(groupPosition).getModelList().get(childPosition);
  }

  @Override public long getGroupId(int groupPosition) {
    return groupPosition;
  }

  @Override public long getChildId(int groupPosition, int childPosition) {
    return childPosition;
  }

  @Override public boolean hasStableIds() {
    return false;
  }

  @Override public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
    GroupHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(context).inflate(R.layout.list_item_faq_section, parent, false);
      holder = new GroupHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (GroupHolder) convertView.getTag();
    }
    if (isExpanded) {
      holder.imageArrow.setImageResource(R.drawable.ic_blue_up_arrow);
    } else {
      holder.imageArrow.setImageResource(R.drawable.ic_arrow_down);

    }
    holder.listItemName.setText(mSectionModelList.get(groupPosition).getTopicTitle());
    return convertView;
  }

  @Override public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
    ChildHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(context).inflate(R.layout.list_item_faq_child, parent, false);
      holder = new ChildHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ChildHolder) convertView.getTag();
    }
    holder.listItemName.setText(mSectionModelList.get(groupPosition).getModelList().get(childPosition).getDescription());
    return convertView;
  }

  @Override public boolean isChildSelectable(int groupPosition, int childPosition) {
    return false;
  }

  public class GroupHolder {
    @BindView(R.id.listItemSectionName) TextView listItemName;
    @BindView(R.id.listItemSectionArrow) ImageView imageArrow;

    public GroupHolder(View itemView) {
      ButterKnife.bind(this, itemView);
    }
  }

  class ChildHolder {
    @BindView(R.id.listItemChild) TextView listItemName;

    public ChildHolder(View itemView) {
      ButterKnife.bind(this, itemView);
    }
  }
}
