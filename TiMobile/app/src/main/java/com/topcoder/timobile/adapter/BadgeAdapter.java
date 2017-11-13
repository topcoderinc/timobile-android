package com.topcoder.timobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseRecyclerAdapter;
import com.topcoder.timobile.model.BadgeModel;
import java.util.List;

/**
 * Author: Harshvardhan
 * Date: 01/11/17
 */

public class BadgeAdapter extends BaseRecyclerAdapter<BadgeAdapter.MyViewHolder> {
  private final Context context;
  private final List<BadgeModel> badgeModelList;

  public BadgeAdapter(Context context, List<BadgeModel> badgeModelList) {
    this.context = context;
    this.badgeModelList = badgeModelList;
  }

  @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_badges, parent, false);
    return new MyViewHolder(view);
  }

  @Override public void onBindViewHolder(MyViewHolder holder, int position) {
    BadgeModel model = badgeModelList.get(position);
    if (model.isActive()) {
      holder.imgBadge.setImageResource(R.drawable.ic_active_badge);
    } else {
      holder.imgBadge.setImageResource(R.drawable.ic_disable_badge);
    }
    holder.tvName.setText(model.getTitle());
  }

  @Override public int getItemCount() {
    return badgeModelList.size();
  }

  public class MyViewHolder extends BaseRecyclerAdapter.ViewHolder {
    @BindView(R.id.imgBadge) ImageView imgBadge;
    @BindView(R.id.tvName) TextView tvName;

    public MyViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      clickableViews(itemView);
    }
  }
}
