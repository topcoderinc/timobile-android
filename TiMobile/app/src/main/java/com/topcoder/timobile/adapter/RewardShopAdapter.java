package com.topcoder.timobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Setter;

import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseRecyclerAdapter;
import com.topcoder.timobile.customviews.roundimageview.RoundedImageView;
import com.topcoder.timobile.glide.GlideApp;
import com.topcoder.timobile.model.Card;
import com.topcoder.timobile.model.event.BuyCardEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Author: Harshvardhan
 * Date: 01/11/17
 */

public class RewardShopAdapter extends BaseRecyclerAdapter<RewardShopAdapter.MyViewHolder> {
  private final Context context;
  private final List<Card> rewardShopModels;

  public RewardShopAdapter(Context context, List<Card> rewardShopModels) {
    this.context = context;
    this.rewardShopModels = rewardShopModels;
  }

  @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward_shop, parent, false);
    return new MyViewHolder(view);
  }

  @Override public void onBindViewHolder(MyViewHolder holder, int position) {
    Card card = rewardShopModels.get(position);
    GlideApp.with(context).load(card.getImageURL()).placeholder(R.drawable.ic_card_close).into(holder.imgCardFirst);
    holder.tvName.setText(card.getName());
    holder.setCard(card);
    holder.tvRewardPoints.setText(context.getResources()
        .getString(R.string.reward_pts, card.getPricePoints()));
  }

  @Override public int getItemCount() {
    return rewardShopModels.size();
  }


  public class MyViewHolder extends BaseRecyclerAdapter.ViewHolder {
    @BindView(R.id.imgCardFirst) RoundedImageView imgCardFirst;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvRewardPoints) TextView tvRewardPoints;

    @Setter private Card card;

    public MyViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      itemView.setOnClickListener(view -> EventBus.getDefault().post(new BuyCardEvent(card, "open")));
    }
  }
}
