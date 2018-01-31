package com.topcoder.timobile.fragments;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseFragment;
import com.topcoder.timobile.model.Chapter;
import com.topcoder.timobile.model.ChapterProgress;
import com.topcoder.timobile.model.StoryProgress;
import com.topcoder.timobile.utility.AppConstants;
import com.topcoder.timobile.utility.AppUtils;

import java.util.ArrayList;

/**
 * Author: Harshvardhan
 * Date: 02/11/17
 */

public class ChapterStoryFragment extends BaseFragment implements EasyVideoCallback {

  @BindView(R.id.chapterScrollview) ScrollView chapterRootScrollview;
  @BindView(R.id.imgVideo) EasyVideoPlayer imgVideo;
  @BindView(R.id.imgPlay) ImageView imgPlay;
  @BindView(R.id.videoView) FrameLayout videoView;
  @BindView(R.id.tvChapterTitle) TextView tvChapterTitle;
  @BindView(R.id.tvRaceCourse) TextView tvRaceCourse;
  @BindView(R.id.tvDescription) TextView tvDescription;
  Unbinder unbinder;

  private String TAG = ChapterStoryFragment.class.getName();
  private Long preUpdateProgressTime = System.currentTimeMillis();
  private Chapter chapter = null;

  /**
   * gson pack and uppack object.
   */
  private static Gson gson = new Gson();

  /**
   * all chapter should use one storyProgress
   */
  @Setter @Getter private static StoryProgress storyProgress;

  /**
   * create fragment instance
   *
   * @param chapter chapter model
   * @return fragment
   */
  public static ChapterStoryFragment getInstance(Chapter chapter) {
    if (storyProgress == null) {
      ToastUtils.showShort("Unexpected error,storyProgress shoud not be null.");
      throw new IllegalArgumentException("storyProgress in chapter should not be null");
    }
    ChapterStoryFragment chapterStoryFragment = new ChapterStoryFragment();
    Bundle bundle = new Bundle();
    bundle.putString(AppConstants.KEY_OBJ, gson.toJson(chapter));
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
    Chapter model = gson.fromJson(getArguments().getString(AppConstants.KEY_OBJ), Chapter.class);
    this.chapter = model;
    imgVideo.setCallback(this);
    if (model != null) {
      if (!TextUtils.isEmpty(model.getMedia())) {
        videoView.setVisibility(View.VISIBLE);
        imgVideo.setSource(Uri.parse(model.getMedia()));
        // Hides the default controls. They can be shown again if the user taps the player.
        imgVideo.hideControls();
      } else {
        videoView.setVisibility(View.GONE);
      }

      tvChapterTitle.setText(model.getTitle());
      tvRaceCourse.setText(model.getSubtitle());
      tvDescription.setMaxLines(Integer.MAX_VALUE);

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        tvDescription.setText(Html.fromHtml(model.getContent(), Html.FROM_HTML_MODE_COMPACT));
      } else {
        tvDescription.setText(Html.fromHtml(model.getContent()));
      }
    }

    chapterRootScrollview.setOnScrollChangeListener((sView, x, y, oldX, oldY) -> {
      float progress = (y + sView.getHeight()) * 1.0f / chapterRootScrollview.getChildAt(0).getHeight();
      updateChapterProgress(progress);
    });
  }

  /**
   * update chapter progress to backend
   *
   * @param progressValue the progress value
   */
  public void updateChapterProgress(float progressValue) {
    long nowTime = System.currentTimeMillis();
    long timeInterval = nowTime - preUpdateProgressTime;

    // found progress entity
    ChapterProgress progress = null;
    if (storyProgress.getChaptersUserProgress() != null
        && storyProgress.getChaptersUserProgress().size() > 0) {
      for (ChapterProgress p : storyProgress.getChaptersUserProgress()) {
        if (p.getChapterId().equals(chapter.getId())) {
          progress = p;
          break;
        }
      }
    }

    if (progress == null) {
      progress = new ChapterProgress();
      progress.setChapterId(chapter.getId());
      if (storyProgress.getChaptersUserProgress() == null) {
        storyProgress.setChaptersUserProgress(new ArrayList<>());
      }
      storyProgress.getChaptersUserProgress().add(progress);
      Log.d(TAG, "updateChapterProgress: create new progress for chapter " + chapter.getId());
    }
    if (progress.getCompleted()) {  // already completed , no need update
      Log.d(TAG, "updateChapterProgress: chapter already completed, skip update.");
      return;
    }

    if (progressValue >= 1.0) { // mark completed directly
      progress.setCompleted(true);
      progress.setWordsRead(chapter.getWordsCount());
      apiService.updateProgress(storyProgress.getId(), storyProgress)
          .subscribe(this::onUpdateProgressSuccess, this::onUpdateError);
    } else {
      if (timeInterval >= AppConstants.UPDATE_PROGRESS_INTERVAL) {
        int wordReadied = (int) (chapter.getWordsCount() * progressValue);
        if (wordReadied > progress.getWordsRead()) { // read new content
          progress.setWordsRead(wordReadied);
          progress.setCompleted(false);
          apiService.updateProgress(storyProgress.getId(),
              storyProgress).subscribe(this::onUpdateProgressSuccess, this::onUpdateError);
          preUpdateProgressTime = nowTime;
        } else {
          Log.d(TAG, "updateChapterProgress: skip updated, because read back");
        }
      } else {
        Log.d(TAG, "updateChapterProgress: skip updated, because update in seconds, " + timeInterval);
      }
    }
  }


  public void onUpdateProgressSuccess(StoryProgress storyProgress) {
    ChapterStoryFragment.setStoryProgress(storyProgress);
    Log.d(TAG, "onUpdateProgressSuccess: " + storyProgress);
  }

  public void onUpdateError(Throwable throwable) {
    Timber.d(throwable);
    AppUtils.showError(throwable, "Update chapter read progress failed.");
  }

  /**
   * when user read this chapter
   */
  public void onActive() {
    if (chapterRootScrollview == null)
      return;
    // content don't full a page , directly send completed
    if (!contentCanScroll()) {
      this.updateChapterProgress(1.01f);
    }
  }

  private boolean contentCanScroll() {
    View child = chapterRootScrollview.getChildAt(0);
    if (child != null) {
      int childHeight = (child).getHeight();
      return chapterRootScrollview.getHeight() < childHeight
          + chapterRootScrollview.getPaddingTop()
          + chapterRootScrollview.getPaddingBottom();
    }
    return false;
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
   *
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
