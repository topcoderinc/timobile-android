package com.topcoder.timobile.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseActivity;
import com.topcoder.timobile.fragments.ChapterStoryFragment;
import com.topcoder.timobile.model.ChapterStoryModel;
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

  @BindView(R.id.imgClose) ImageView imgClose;
  @BindView(R.id.tvTitle) TextView tvTitle;
  @BindView(R.id.imgLeft) ImageView imgLeft;
  @BindView(R.id.indicator) CircleIndicator indicator;
  @BindView(R.id.imgRight) ImageView imgRight;
  @BindView(R.id.seekBar) SeekBar seekBar;
  @BindView(R.id.pager) ViewPager pager;
  @BindView(R.id.imgComment) ImageView imgComment;

  private List<ChapterStoryModel> chapterStoryModelList = new ArrayList<>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chapter_detail);
    ButterKnife.bind(this);
    seekBar.setPadding(0, 0, 0, 0);

    apiService.getChapterStories().subscribe(this::onSuccess, this::onError);
    imgClose.setImageResource(R.drawable.ic_back);
    tvTitle.setText("Chapter 1");
    seekBar.setOnTouchListener((v, event) -> true);
    pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override public void onPageSelected(int position) {
        tvTitle.setText(String.format(Locale.ENGLISH, "Chapter %d", ++position));
        setSeekProgress(position);
      }

      @Override public void onPageScrollStateChanged(int state) {

      }
    });
  }

  /**
   * show seekbar progress
   * @param position page position
   */
  private void setSeekProgress(int position) {
    int progress = (position * 100) / chapterStoryModelList.size();
    seekBar.setProgress(progress);
  }

  private void onError(Throwable throwable) {
    Timber.d(throwable);
  }

  private void onSuccess(List<ChapterStoryModel> chapterStoryModels) {
    chapterStoryModelList.addAll(chapterStoryModels);
    ChapterStoryPagerAdapter adapter = new ChapterStoryPagerAdapter(getSupportFragmentManager());
    for (ChapterStoryModel chapterStoryModel : chapterStoryModelList) {
      adapter.addFragment(ChapterStoryFragment.getInstance(chapterStoryModel));
    }
    pager.setAdapter(adapter);
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
  }

  @OnClick({ R.id.imgComment, R.id.imgLeft, R.id.imgRight, R.id.imgClose }) public void onViewClicked(View view) {
    int currItem = pager.getCurrentItem();
    switch (view.getId()) {
      case R.id.imgComment:
        ActivityUtils.startActivity(CommentActivity.class);
        break;
      case R.id.imgLeft:
        pager.setCurrentItem(--currItem, true);
        break;
      case R.id.imgRight:
        pager.setCurrentItem(++currItem, true);
        break;
      case R.id.imgClose:
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
}
