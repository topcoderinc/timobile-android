package com.topcoder.timobile.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.timobileapp.R;
import com.topcoder.timobile.adapter.CommentAdapter;
import com.topcoder.timobile.baseclasses.BaseActivity;
import com.topcoder.timobile.baseclasses.BaseRecyclerAdapter;
import com.topcoder.timobile.model.Comment;
import com.topcoder.timobile.model.PageResult;
import com.topcoder.timobile.utility.AppConstants;
import com.topcoder.timobile.utility.AppUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Author: Harshvardhan
 * Date: 02/11/17
 */

public class CommentActivity extends BaseActivity implements BaseRecyclerAdapter.RecycleOnItemClickListener {

  public static final String STORY_CHAPTER = "STORY;CHAPTER";

  @BindView(R.id.imgClose) ImageView imgClose;
  @BindView(R.id.tvTitle) TextView tvTitle;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.etAddComment) EditText etAddComment;
  @BindView(R.id.tvPost) TextView tvPost;

  private List<Comment> commentModelList = new ArrayList<>();
  private CommentAdapter adapter;

  private String TAG = CommentActivity.class.getName();
  private int offset = 0;
  private Long storyId;
  private Long chapterId;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_comment);
    ButterKnife.bind(this);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    String storyIdAndChapter = getIntent().getStringExtra(STORY_CHAPTER);
    String[] ids = storyIdAndChapter.split(";");
    storyId = Long.valueOf(ids[0]);
    chapterId = Long.valueOf(ids[1]);
    fetchComments();
  }

  /**
   * fetch comment
   */
  private void fetchComments() {
    apiService.searchComments(null, chapterId, storyId,
        null, offset, AppConstants.DEFAULT_LIMIT, "createdAt", "desc")
        .subscribe(this::onSuccess, this::onError);
  }

  private void onError(Throwable throwable) {
    Timber.d(throwable);
    AppUtils.showError(throwable, getString(R.string.error));
  }

  private void onSuccess(PageResult<Comment> commentPageResult) {
    commentModelList.addAll(commentPageResult.getItems());
    adapter = new CommentAdapter(this, commentModelList);
    recyclerView.setAdapter(adapter);
    adapter.setRecycleOnItemClickListner(this);
  }

  /**
   * post a sample comment on post click
   */
  @OnClick(R.id.tvPost) void onPost() {
    if (TextUtils.isEmpty(AppUtils.getText(etAddComment))) return;
    KeyboardUtils.hideSoftInput(this);
    Comment comment = new Comment();
    comment.setUserId(apiService.getCurrentUser().getId());
    comment.setChapterId(chapterId);
    comment.setTrackStoryId(storyId);
    comment.setType("Chapter");
    comment.setText(AppUtils.getText(etAddComment));
    apiService.createComments(comment).subscribe(this::onCreateSucceed, this::onError);
  }

  /**
   * on create comment succeed
   */
  private void onCreateSucceed(Comment comment) {
    commentModelList.add(0, comment);
    comment.setUser(apiService.getCurrentUser());
    adapter.notifyItemInserted(0);
    etAddComment.setText("");
    recyclerView.scrollToPosition(0);
    // check for new
    apiService.checkForNewAchievements().subscribe();
  }

  @OnClick(R.id.imgClose) void onClose() {
    finish();
  }

  /**
   * remove comment
   *
   * @param view     view
   * @param position position
   */
  @Override public void onItemClick(View view, int position) {
    int id = view.getId();
    if (commentModelList.size() <= 0 || position >= commentModelList.size()) {
      return;
    }
    Comment comment = commentModelList.get(position);
    if (comment == null || comment.getId() == null) {
      return;
    }
    if (comment.getUser() != null && comment.getUser().getId() != apiService.getCurrentUser().getId()) {
      ToastUtils.showShort("you cannot delete other user comment");
      return;
    }
    if (id == R.id.imgMenu) {
      int oldPosition = position;
      apiService.deleteComment(comment.getId()).subscribe(emptyObject -> {
        commentModelList.remove(oldPosition);
        adapter.notifyItemRemoved(position);
      }, throwable -> {
        Timber.e(throwable);
        AppUtils.showError(throwable, "cannot delete comment");
      });
    }
  }


}
