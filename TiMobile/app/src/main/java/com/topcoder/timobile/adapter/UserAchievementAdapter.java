package com.topcoder.timobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseRecyclerAdapter;
import com.topcoder.timobile.glide.GlideApp;
import com.topcoder.timobile.model.Achievement;
import com.topcoder.timobile.model.DailyTask;
import com.topcoder.timobile.model.UserAchievement;
import com.topcoder.timobile.model.UserDailyTask;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * the user daily task adapter
 */
public class UserAchievementAdapter extends BaseRecyclerAdapter<UserAchievementAdapter.ViewHolder> {
  private List<UserAchievement> userAchievements;
  private Context context;

  public UserAchievementAdapter(Context context, List<UserAchievement> userAchievements) {
    this.context = context;
    this.userAchievements = userAchievements;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ti_achievement, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    UserAchievement userAchievement = userAchievements.get(position);
    if (userAchievement.getAchievement() == null) {
      ToastUtils.showShort("Achievement should not be null");
      return;
    }
    Achievement achievement = userAchievement.getAchievement();
    holder.tvTitle.setText(achievement.getName());
    holder.tvDescription.setText(achievement.getDescription());
    GlideApp.with(context).load(achievement.getImageURL())
        .placeholder(R.drawable.ic_achievement_green).into(holder.imgMedal);
  }


  @Override public int getItemCount() {
    return userAchievements.size();
  }

  public class ViewHolder extends BaseRecyclerAdapter.ViewHolder {
    @BindView(R.id.imgMedal) CircleImageView imgMedal;
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvDescription) TextView tvDescription;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      clickableViews(itemView);
    }
  }
}
