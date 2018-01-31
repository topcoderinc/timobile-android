package com.topcoder.timobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseRecyclerAdapter;
import com.topcoder.timobile.glide.GlideApp;
import com.topcoder.timobile.model.Comment;

import java.util.List;

/**
 * Author: Harshvardhan
 * Date: 02/11/17
 */

public class CommentAdapter extends BaseRecyclerAdapter<CommentAdapter.MyViewHolder> {

  private final Context context;
  private final List<Comment> commentModelList;

  public CommentAdapter(Context context, List<Comment> commentModelList) {
    this.context = context;
    this.commentModelList = commentModelList;
  }

  @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comments, parent, false);
    return new MyViewHolder(view);
  }

  @Override public void onBindViewHolder(MyViewHolder holder, int position) {
    Comment model = commentModelList.get(position);
    if (model.getUser() != null) {
      holder.tvUserName.setText(model.getUser().getName());
      GlideApp.with(context).load(model.getUser().getProfilePhotoURL())
          .placeholder(R.drawable.ic_story_ph).into(holder.imgComment);
    }
    holder.tvComment.setText(model.getText());
    holder.tvTimeStamp.setText(TimeAgo.using(model.getCreatedAt().getTime()));

  }

  @Override public int getItemCount() {
    return commentModelList.size();
  }

  public class MyViewHolder extends BaseRecyclerAdapter.ViewHolder {
    @BindView(R.id.imgComment) ImageView imgComment;
    @BindView(R.id.tvUserName) TextView tvUserName;
    @BindView(R.id.tvTimeStamp) TextView tvTimeStamp;
    @BindView(R.id.imgMenu) ImageView imgMenu;
    @BindView(R.id.tvComment) TextView tvComment;

    public MyViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      clickableViews(imgMenu);
    }
  }
}
