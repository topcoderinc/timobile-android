package com.topcoder.timobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseRecyclerAdapter;
import com.topcoder.timobile.customviews.roundimageview.RoundedImageView;
import com.topcoder.timobile.glide.GlideApp;
import com.topcoder.timobile.model.RewardShopModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Harshvardhan
 * Date: 01/11/17
 */

public class RewardShopAdapter extends BaseRecyclerAdapter<RewardShopAdapter.MyViewHolder> implements Filterable {
  private final Context context;
  private final List<RewardShopModel> rewardShopModels;
  private List<RewardShopModel> mFilteredList;

  public RewardShopAdapter(Context context, List<RewardShopModel> rewardShopModels) {
    this.context = context;
    this.rewardShopModels = rewardShopModels;
    mFilteredList = this.rewardShopModels;
  }

  @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward_shop, parent, false);
    return new MyViewHolder(view);
  }

  @Override public void onBindViewHolder(MyViewHolder holder, int position) {
    GlideApp.with(context).load(mFilteredList.get(position).getImage()).into(holder.imgCardFirst);
    holder.tvName.setText(mFilteredList.get(position).getTitle());
    holder.tvRewardPoints.setText(context.getResources().getString(R.string.reward_pts, mFilteredList.get(position).getPoints()));
  }

  @Override public int getItemCount() {
    return mFilteredList.size();
  }
  /**
   * filter base on title
   * @return filtered data
   */
  @Override public Filter getFilter() {
    return new Filter() {
      @Override protected FilterResults performFiltering(CharSequence charSequence) {

        String charString = charSequence.toString();

        if (charString.isEmpty()) {

          mFilteredList = rewardShopModels;
        } else {

          ArrayList<RewardShopModel> filteredList = new ArrayList<>();

          for (RewardShopModel model : rewardShopModels) {

            if (model.getTitle().toLowerCase().contains(charString)) {

              filteredList.add(model);
            }
          }

          mFilteredList = filteredList;
        }

        FilterResults filterResults = new FilterResults();
        filterResults.values = mFilteredList;
        return filterResults;
      }

      @Override protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        mFilteredList = (ArrayList<RewardShopModel>) filterResults.values;
        notifyDataSetChanged();
      }
    };
  }

  public class MyViewHolder extends BaseRecyclerAdapter.ViewHolder {
    @BindView(R.id.imgCardFirst) RoundedImageView imgCardFirst;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvRewardPoints) TextView tvRewardPoints;

    public MyViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
