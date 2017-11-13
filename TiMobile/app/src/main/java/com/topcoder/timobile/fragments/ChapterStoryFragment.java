package com.topcoder.timobile.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseFragment;
import com.topcoder.timobile.model.ChapterStoryModel;
import com.topcoder.timobile.utility.AppConstants;

/**
 * Author: Harshvardhan
 * Date: 02/11/17
 */

public class ChapterStoryFragment extends BaseFragment implements EasyVideoCallback {

  @BindView(R.id.imgVideo) EasyVideoPlayer imgVideo;
  @BindView(R.id.imgPlay) ImageView imgPlay;
  @BindView(R.id.videoView) FrameLayout videoView;
  @BindView(R.id.tvChapterTitle) TextView tvChapterTitle;
  @BindView(R.id.tvRaceCourse) TextView tvRaceCourse;
  @BindView(R.id.tvDescription) TextView tvDescription;
  Unbinder unbinder;

  /**
   * create fragment instance
   * @param chapterStoryModel chapter model
   * @return fragment
   */
  public static ChapterStoryFragment getInstance(ChapterStoryModel chapterStoryModel) {
    ChapterStoryFragment chapterStoryFragment = new ChapterStoryFragment();
    Bundle bundle = new Bundle();
    bundle.putParcelable(AppConstants.KEY_OBJ, chapterStoryModel);
    chapterStoryFragment.setArguments(bundle);
    return chapterStoryFragment;
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.item_chapter_pager, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ChapterStoryModel model = getArguments().getParcelable(AppConstants.KEY_OBJ);
    imgVideo.setCallback(this);
    if (model != null) {
      if (!TextUtils.isEmpty(model.getVideo())) {
        videoView.setVisibility(View.VISIBLE);
        imgVideo.setSource(Uri.parse(model.getVideo()));
        // Hides the default controls. They can be shown again if the user taps the player.
        imgVideo.hideControls();
      } else {
        videoView.setVisibility(View.GONE);
      }

      tvChapterTitle.setText(model.getTitle());
      tvRaceCourse.setText(model.getRacecourse());
      tvDescription.setMaxLines(Integer.MAX_VALUE);
      tvDescription.setMovementMethod(new ScrollingMovementMethod());
      tvDescription.setText(model.getDescription());
    }
  }

  /**
   * play video
   */
  @OnClick(R.id.imgPlay) void onClick() {
    imgVideo.start();
  }

  @Override public void onPause() {
    super.onPause();
    // Make sure the player stops playing if the user presses the home button.
    if (imgVideo.isPlaying()) {
      imgVideo.pause();
    }
  }

  /**
   * only play when fragment is visible to user
   * @param isVisibleToUser isVisibleToUser
   */
  @Override public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (!isVisibleToUser) {
      if (imgVideo != null && imgVideo.isPlaying()) {
        imgVideo.stop();
      }
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override public void onStarted(EasyVideoPlayer player) {
    imgPlay.setVisibility(View.GONE);
    imgVideo.enableControls(true);
  }

  @Override public void onPaused(EasyVideoPlayer player) {

  }

  @Override public void onPreparing(EasyVideoPlayer player) {

  }

  @Override public void onPrepared(EasyVideoPlayer player) {

  }

  @Override public void onBuffering(int percent) {

  }

  @Override public void onError(EasyVideoPlayer player, Exception e) {

  }

  @Override public void onCompletion(EasyVideoPlayer player) {

  }

  @Override public void onRetry(EasyVideoPlayer player, Uri source) {

  }

  @Override public void onSubmit(EasyVideoPlayer player, Uri source) {

  }
}
