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
import com.topcoder.timobile.glide.GlideApp;
import com.topcoder.timobile.model.TrackStory;
import com.topcoder.timobile.utility.AppUtils;

import java.util.List;

/**
 * Author: Harshvardhan
 * Date: 29/10/17
 */

public class StoryAdapter extends BaseRecyclerAdapter<StoryAdapter.MyViewHolder> {

  private final Context context;
  private List<TrackStory> storyModelList;

  public StoryAdapter(Context context, List<TrackStory> storyModelList) {
    this.context = context;
    this.storyModelList = storyModelList;
  }

  @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story, parent, false);
    return new MyViewHolder(view);
  }

  public void setData(List<TrackStory> storyModelList) {
    this.storyModelList = storyModelList;
    notifyDataSetChanged();
  }

  @Override public void onBindViewHolder(MyViewHolder holder, int position) {
    TrackStory model = storyModelList.get(position);
    GlideApp.with(context).load(model.getSmallImageURL()).into(holder.imgStory);
    holder.tvStoryTitle.setText(model.getTitle());
    holder.tvRaceCourse.setText(model.getSubtitle());
    holder.tvDescription.setText(model.getDescription());
    holder.tvChapter.setText(context.getResources().getQuantityString(R.plurals.chapter, model.getChapters().size(), model.getChapters().size()));
    holder.tvCard.setText(context.getResources().getQuantityString(R.plurals.card, model.getCards().size(), model.getCards().size()));
    holder.tvDistance.setText(AppUtils.getDistance(model.getRacetrack()));
  }

  @Override public int getItemCount() {
    return storyModelList.size();
  }

  public class MyViewHolder extends BaseRecyclerAdapter.ViewHolder {
    @BindView(R.id.imgStory) ImageView imgStory;
    @BindView(R.id.tvStoryTitle) TextView tvStoryTitle;
    @BindView(R.id.tvRaceCourse) TextView tvRaceCourse;
    @BindView(R.id.tvDescription) TextView tvDescription;
    @BindView(R.id.tvChapter) TextView tvChapter;
    @BindView(R.id.tvCard) TextView tvCard;
    @BindView(R.id.tvDistance) TextView tvDistance;

    public MyViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      clickableViews(itemView);
    }
  }
}
