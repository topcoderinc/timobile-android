package com.topcoder.timobile.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseRecyclerAdapter;
import com.topcoder.timobile.model.PreStorySampleModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Harshvardhan
 * Date: 28/10/17
 */

public class PreStoryListAdapter extends BaseRecyclerAdapter<PreStoryListAdapter.MyViewHolder> implements Filterable {

  private final Context context;
  private final List<PreStorySampleModel> sampleModelList;
  private List<PreStorySampleModel> mFilteredList;

  public PreStoryListAdapter(Context context, List<PreStorySampleModel> sampleModelList) {
    this.context = context;
    this.sampleModelList = sampleModelList;
    mFilteredList = this.sampleModelList;
  }


  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story_list, parent, false);
    return new MyViewHolder(view);
  }

  @Override
  public void onBindViewHolder(MyViewHolder holder, int position) {
    holder.tvItemName.setText(mFilteredList.get(position).getValue());
    if (mFilteredList.get(position).isCheck()) {
      holder.tvItemName.setTextColor(ContextCompat.getColor(context, R.color.blue_indicator));
      holder.imgCheck.setVisibility(View.VISIBLE);
    } else {
      holder.tvItemName.setTextColor(ContextCompat.getColor(context, R.color.dark_gray));
      holder.imgCheck.setVisibility(View.INVISIBLE);
    }
  }

  @Override
  public int getItemCount() {
    return mFilteredList.size();
  }

  public PreStorySampleModel getModel(int position) {
    return mFilteredList.get(position);
  }

  /**
   * filter base on name
   *
   * @return filtered data
   */
  @Override
  public Filter getFilter() {
    return new Filter() {
      @Override
      protected FilterResults performFiltering(CharSequence charSequence) {

        String charString = charSequence.toString();

        if (charString.isEmpty()) {
          mFilteredList = sampleModelList;
        } else {
          ArrayList<PreStorySampleModel> filteredList = new ArrayList<>();
          for (PreStorySampleModel model : sampleModelList) {
            if (model.getValue().toLowerCase().contains(charString)) {
              filteredList.add(model);
            }
          }
          mFilteredList = filteredList;
        }

        FilterResults filterResults = new FilterResults();
        filterResults.values = mFilteredList;
        return filterResults;
      }

      @Override
      protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        mFilteredList = (ArrayList<PreStorySampleModel>) filterResults.values;
        notifyDataSetChanged();
      }
    };
  }

  public class MyViewHolder extends BaseRecyclerAdapter.ViewHolder {
    @BindView(R.id.tvItemName)
    TextView tvItemName;
    @BindView(R.id.imgCheck)
    ImageView imgCheck;

    public MyViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      clickableViews(itemView);
    }
  }
}
