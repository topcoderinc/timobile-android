package com.topcoder.timobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseRecyclerAdapter;
import com.topcoder.timobile.customviews.roundimageview.RoundedImageView;
import com.topcoder.timobile.glide.GlideApp;
import com.topcoder.timobile.model.TradingCardModel;
import java.util.List;

/**
 * Author: Harshvardhan
 * Date: 01/11/17
 */

public class TradingCardAdapter extends BaseRecyclerAdapter<TradingCardAdapter.MyViewHolder> {
  private final Context context;
  private final List<TradingCardModel> tradingCardModels;

  public TradingCardAdapter(Context context, List<TradingCardModel> tradingCardModels) {
    this.context = context;
    this.tradingCardModels = tradingCardModels;
  }

  @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward, parent, false);
    return new MyViewHolder(view);
  }

  @Override public void onBindViewHolder(MyViewHolder holder, int position) {
    TradingCardModel model = tradingCardModels.get(position);
    if (model.isActive()) {
      GlideApp.with(context).load(model.getImage()).into(holder.imgReward);
    } else {
      holder.imgReward.setImageResource(R.drawable.ic_card_close);
    }
    holder.tvName.setText(model.getTitle());
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

