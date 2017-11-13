package com.topcoder.timobile.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseRecyclerAdapter;
import com.topcoder.timobile.model.TiMobileModel;
import java.util.List;

/**
 * Author: Harshvardhan
 * Date: 01/11/17
 */

public class TiMobileAdapter extends BaseRecyclerAdapter<TiMobileAdapter.MyViewHolder> {
  private final Context context;

  private List<TiMobileModel> tiMobileModelList;

  public TiMobileAdapter(Context context, List<TiMobileModel> tiMobileModelList) {
    this.context = context;
    this.tiMobileModelList = tiMobileModelList;
  }

  @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ti_achievements, parent, false);
    return new MyViewHolder(view);
  }

  @Override public void onBindViewHolder(MyViewHolder holder, int position) {
    TiMobileModel mobileModel = tiMobileModelList.get(position);
    holder.tvTitle.setText(mobileModel.getTitle());
    holder.tvDescription.setText(mobileModel.getDescription());
    holder.tvRewardPoints.setText(context.getResources().getString(R.string.reward_pts, mobileModel.getPts()));
    if (mobileModel.isActive()) {
      holder.imgMedal.setImageResource(R.drawable.ic_achievement_green);
      holder.tvRedeem.setBackgroundResource(R.drawable.blue_border);
      holder.tvRedeem.setTextColor(ContextCompat.getColor(context, R.color.blue_indicator));
    } else {
      holder.imgMedal.setImageResource(R.drawable.ic_achievement_gray);
      holder.tvRedeem.setBackgroundResource(R.drawable.gray_border);
      holder.tvRedeem.setTextColor(ContextCompat.getColor(context, R.color.redeem_gray));
    }
  }

  @Override public int getItemCount() {
    return tiMobileModelList.size();
  }

  public class MyViewHolder extends BaseRecyclerAdapter.ViewHolder {
    @BindView(R.id.imgMedal) ImageView imgMedal;
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvDescription) TextView tvDescription;
    @BindView(R.id.tvRewardPoints) TextView tvRewardPoints;
    @BindView(R.id.tvRedeem) TextView tvRedeem;

    public MyViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      clickableViews(itemView);
    }
  }
}
