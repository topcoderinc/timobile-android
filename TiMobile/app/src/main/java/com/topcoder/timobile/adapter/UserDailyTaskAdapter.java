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

import com.blankj.utilcode.util.ToastUtils;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseRecyclerAdapter;
import com.topcoder.timobile.model.DailyTask;
import com.topcoder.timobile.model.UserDailyTask;

import java.util.List;

/**
 * the user daily task adapter
 */
public class UserDailyTaskAdapter extends BaseRecyclerAdapter<UserDailyTaskAdapter.MyViewHolder> {
  private final Context context;

  private List<UserDailyTask> userDailyTasks;
  private MyViewHolder holder;


  public UserDailyTaskAdapter(Context context, List<UserDailyTask> userDailyTasks) {
    this.context = context;
    this.userDailyTasks = userDailyTasks;
  }

  @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ti_user_daily_task, parent, false);
    return new MyViewHolder(view);
  }

  @Override public void onBindViewHolder(MyViewHolder holder, int position) {
    this.holder = holder;
    UserDailyTask userDailyTask = userDailyTasks.get(position);
    if (userDailyTask.getDailyTask() == null) {
      ToastUtils.showShort("DailyTask should not be null");
      return;
    }
    DailyTask task = userDailyTask.getDailyTask();
    holder.tvTitle.setText(task.getName());
    holder.tvDescription.setText(task.getDescription());
    holder.tvRewardPoints.setText(context.getResources().getString(R.string.reward_pts, task.getPoints()));
    if (task.getActive()) {
      if (userDailyTask.getCompleted()) {
        this.updateButton("Completed", true);
      } else {
        this.updateButton("Redeem", false);
      }
    } else {
      this.updateButton("Redeem", true);
    }
  }

  public void updateButton(String text, boolean disabled) {
    if (disabled) {
      holder.tvRedeem.setEnabled(false);
      holder.imgMedal.setImageResource(R.drawable.ic_achievement_gray);
      holder.tvRedeem.setBackgroundResource(R.drawable.gray_border);
      holder.tvRedeem.setText(text);
      holder.tvRedeem.setTextColor(ContextCompat.getColor(context, R.color.redeem_gray));
    } else {
      holder.tvRedeem.setEnabled(true);
      holder.imgMedal.setImageResource(R.drawable.ic_achievement_green);
      holder.tvRedeem.setBackgroundResource(R.drawable.blue_border);
      holder.tvRedeem.setText(text);
      holder.tvRedeem.setTextColor(ContextCompat.getColor(context, R.color.blue_indicator));
    }
  }

  @Override public int getItemCount() {
    return userDailyTasks.size();
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
