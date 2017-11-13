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
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.blankj.utilcode.util.KeyboardUtils;
import com.timobileapp.R;
import com.topcoder.timobile.adapter.CommentAdapter;
import com.topcoder.timobile.baseclasses.BaseActivity;
import com.topcoder.timobile.baseclasses.BaseRecyclerAdapter;
import com.topcoder.timobile.model.CommentModel;
import com.topcoder.timobile.utility.AppUtils;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

/**
 * Author: Harshvardhan
 * Date: 02/11/17
 */

public class CommentActivity extends BaseActivity implements BaseRecyclerAdapter.RecycleOnItemClickListener {

  @BindView(R.id.imgClose) ImageView imgClose;
  @BindView(R.id.tvTitle) TextView tvTitle;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.etAddComment) EditText etAddComment;
  @BindView(R.id.tvPost) TextView tvPost;

  private List<CommentModel> commentModelList = new ArrayList<>();
  private CommentAdapter adapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_comment);
    ButterKnife.bind(this);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    apiService.getComments().subscribe(this::onSuccess, this::onError);
  }

  private void onError(Throwable throwable) {
    Timber.d(throwable);
  }

  private void onSuccess(List<CommentModel> commentModels) {
    commentModelList.addAll(commentModels);
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
    CommentModel model = new CommentModel();
    model.setDate(System.currentTimeMillis() / 1000);
    model.setMessage(AppUtils.getText(etAddComment));
    model.setNew(true);
    model.setUserName("John");
    model.setUserProfileImageUrl("https://randomuser.me/api/portraits/thumb/men/81.jpg");
    commentModelList.add(0, model);
    adapter.notifyItemInserted(0);
    etAddComment.setText("");
    recyclerView.scrollToPosition(0);
  }

  @OnClick(R.id.imgClose) void onClose() {
    finish();
  }

  /**
   * remove comment
   * @param view view
   * @param position position
   */
  @Override public void onItemClick(View view, int position) {
    int id = view.getId();
    if (id == R.id.imgMenu) {
      commentModelList.remove(position);
      adapter.notifyItemRemoved(0);
    }
  }
}
