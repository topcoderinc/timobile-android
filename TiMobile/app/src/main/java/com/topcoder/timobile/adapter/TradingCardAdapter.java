package com.topcoder.timobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.blankj.utilcode.util.ActivityUtils;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseRecyclerAdapter;
import com.topcoder.timobile.customviews.roundimageview.RoundedImageView;
import com.topcoder.timobile.glide.GlideApp;
import com.topcoder.timobile.model.UserCard;

import java.util.List;

/**
 * Author: Harshvardhan
 * Date: 01/11/17
 */

public class TradingCardAdapter extends BaseRecyclerAdapter<TradingCardAdapter.MyViewHolder> {
  private final List<UserCard> tradingCardModels;

  public TradingCardAdapter(List<UserCard> tradingCardModels) {
    this.tradingCardModels = tradingCardModels;
  }

  @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward, parent, false);
    return new MyViewHolder(view);
  }

  @Override public void onBindViewHolder(MyViewHolder holder, int position) {
    UserCard model = tradingCardModels.get(position);
    if (model.getCard() != null) {
      GlideApp.with(ActivityUtils.getTopActivity())
          .load(model.getCard().getImageURL()).placeholder(R.drawable.ic_card_close).into(holder.imgReward);
      holder.tvName.setText(model.getCard().getName());
    }

  }

  @Override public int getItemCount() {
    return tradingCardModels.size();
  }

  public class MyViewHolder extends BaseRecyclerAdapter.ViewHolder {
    @BindView(R.id.imgReward) RoundedImageView imgReward;
    @BindView(R.id.tvName) TextView tvName;

    public MyViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      clickableViews(itemView);
    }
  }
}

