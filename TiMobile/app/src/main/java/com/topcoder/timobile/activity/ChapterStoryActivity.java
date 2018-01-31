package com.topcoder.timobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.blankj.utilcode.util.ActivityUtils;
import com.google.gson.Gson;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseActivity;
import com.topcoder.timobile.fragments.ChapterStoryFragment;
import com.topcoder.timobile.model.Chapter;
import com.topcoder.timobile.model.TrackStory;
import com.topcoder.timobile.utility.AppConstants;
import com.topcoder.timobile.utility.AppUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.relex.circleindicator.CircleIndicator;
import timber.log.Timber;

/**
 * Author: Harshvardhan
 * Date: 02/11/17
 */

public class ChapterStoryActivity extends BaseActivity {

  public static final String PASSED_KEY = "STORY;CHAPTER";

  @BindView(R.id.imgClose) ImageView imgClose;
  @BindView(R.id.tvTitle) TextView tvTitle;
  @BindView(R.id.imgLeft) ImageView imgLeft;
  @BindView(R.id.indicator) CircleIndicator indicator;
  @BindView(R.id.imgRight) ImageView imgRight;
  @BindView(R.id.seekBar) SeekBar seekBar;
  @BindView(R.id.pager) ViewPager pager;
  @BindView(R.id.imgComment) ImageView imgComment;

  private List<Chapter> chapterList = new ArrayList<>();
  private Long initChapterId;
  private Long storyId;
  private String TAG = ChapterStoryActivity.class.getName();
  private ChapterStoryPagerAdapter chapterStoryPagerAdapter;
  private int prePageIndex = -1;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chapter_detail);
    ButterKnife.bind(this);
    seekBar.setPadding(0, 0, 0, 0);
    imgClose.setImageResource(R.drawable.ic_back);
    setTitle("N/A");
    this.parseAndFetchStory();
  }

  private void parseAndFetchStory() {
    String passedValue = getIntent().getStringExtra(PASSED_KEY);
    Log.d(TAG, "parseAndFetchStory: " + passedValue);
    String[] ids = passedValue.split(";");
    storyId = Long.valueOf(ids[0]);
    initChapterId = Long.valueOf(ids[1]);
    apiService.getTrackStoryById(storyId).subscribe(this::onSuccess, this::onError);
  }

  /**
   * show seekbar progress
   *
   * @param position page position
   */
  private void setSeekProgress(int position) {
    int progress = (position * 100) / chapterList.size();
    seekBar.setProgress(progress);
  }

  private void onError(Throwable throwable) {
    Timber.d(throwable);
    AppUtils.showError(throwable, getString(R.string.error));
  }

  private void setActivityTitle(int idx) {
    Chapter chapter = chapterList.get(idx);
    if (chapter != null) {
      tvTitle.setText(String.format(Locale.ENGLISH, "Chapter %d", chapter.getNumber()));
    }
  }

  private void onChapterPageScrolled(int position) {
    if (position != prePageIndex) {
      Log.d(TAG, "onChapterPageScrolled: " + position);
      setActivityTitle(position);
      setSeekProgress(position + 1);
      ChapterStoryFragment chapterStoryFragment = (ChapterStoryFragment) chapterStoryPagerAdapter.getItem(position);
      chapterStoryFragment.onActive();
      prePageIndex = position;
      this.openOrDisable(imgLeft, position == 0);
      this.openOrDisable(imgRight, position == chapterList.size() - 1);
    }
  }

  /**
   * open or disable button
   *
   * @param view    the button view
   * @param disable is need disable
   */
  private void openOrDisable(ImageView view, boolean disable) {
    view.setVisibility(disable ? View.INVISIBLE : View.VISIBLE);
  }

  private void onSuccess(TrackStory story) {

    chapterList.addAll(story.getChapters());
    int initPosition = -1;
    for (int i = 0; i < chapterList.size(); i += 1) {
      Chapter chapter = chapterList.get(i);
      if (chapter.getId().equals(initChapterId)) {
        initPosition = i;
      }
    }

    seekBar.setOnTouchListener((v, event) -> true);
    pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        onChapterPageScrolled(position);
      }

      @Override public void onPageSelected(int position) {
      }

      @Override public void onPageScrollStateChanged(int state) {
      }
    });
    chapterStoryPagerAdapter = new ChapterStoryPagerAdapter(getSupportFragmentManager());
    for (Chapter chapter : chapterList) {
      chapterStoryPagerAdapter.addFragment(ChapterStoryFragment.getInstance(chapter));
    }
    pager.setAdapter(chapterStoryPagerAdapter);
    indicator.setViewPager(pager);
    setSeekProgress(1);

    for (int i = 0; i < indicator.getChildCount(); i++) {
      View v = indicator.getChildAt(i);
      v.setTag(i);
      v.setOnClickListener(v1 -> {
        int pageIndex = (int) v1.getTag();
        pager.setCurrentItem(pageIndex, true);
      });
    }

    if (initPosition >= 0) {
      pager.setCurrentItem(initPosition);
    }
  }

  @OnClick({R.id.imgComment, R.id.imgLeft, R.id.imgRight, R.id.imgClose}) public void onViewClicked(View view) {
    int currItem = pager.getCurrentItem();
    switch (view.getId()) {
      case R.id.imgComment:
        Intent commentIntent = new Intent(ActivityUtils.getTopActivity(), CommentActivity.class);
        commentIntent.putExtra(CommentActivity.STORY_CHAPTER, storyId + ";" + chapterList.get(prePageIndex).getId());
        ActivityUtils.startActivity(commentIntent);
        break;
      case R.id.imgLeft:
        pager.setCurrentItem(--currItem, true);
        break;
      case R.id.imgRight:
        pager.setCurrentItem(++currItem, true);
        break;
      case R.id.imgClose:
        Intent intent = new Intent();
        intent.putExtra(AppConstants.KEY_OBJ, (new Gson().toJson(ChapterStoryFragment.getStoryProgress())));
        setResult(AppConstants.CHAPTER_PROGRESS_RETURNED_CODE, intent);
        finish();
        break;
    }
  }

  static class ChapterStoryPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();

    public ChapterStoryPagerAdapter(FragmentManager manager) {
      super(manager);
    }

    @Override public Fragment getItem(int position) {
      return mFragmentList.get(position);
    }

    @Override public int getCount() {
      return mFragmentList.size();
    }

    public void addFragment(Fragment fragment) {
      mFragmentList.add(fragment);
    }
  }

  @Override protected void onDestroy() {
    ChapterStoryFragment.setStoryProgress(null);
    super.onDestroy();
  }
}
