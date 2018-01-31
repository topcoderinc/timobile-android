package com.topcoder.timobile.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseActivity;
import com.topcoder.timobile.baseclasses.BaseRecyclerAdapter;
import com.topcoder.timobile.customviews.ReadMoreTextView;
import com.topcoder.timobile.customviews.progress.CircleProgressView;
import com.topcoder.timobile.customviews.roundimageview.RoundedImageView;
import com.topcoder.timobile.fragments.ChapterStoryFragment;
import com.topcoder.timobile.glide.GlideApp;
import com.topcoder.timobile.model.Bookmark;
import com.topcoder.timobile.model.Card;
import com.topcoder.timobile.model.Chapter;
import com.topcoder.timobile.model.ChapterProgress;
import com.topcoder.timobile.model.EmptyObject;
import com.topcoder.timobile.model.StoryProgress;
import com.topcoder.timobile.model.StoryReward;
import com.topcoder.timobile.model.Tag;
import com.topcoder.timobile.model.TrackStory;
import com.topcoder.timobile.model.UserCard;
import com.topcoder.timobile.model.event.AdditionalTaskEvent;
import com.topcoder.timobile.model.event.FinishEvent;
import com.topcoder.timobile.model.event.RemoveEvent;
import com.topcoder.timobile.utility.AppConstants;
import com.topcoder.timobile.utility.AppUtils;
import com.topcoder.timobile.utility.ImagePicker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Author: Harshvardhan
 * Date: 31/10/17
 */

public class BrowseStoryActivity extends BaseActivity {

  private static final int REQUEST_CODE_CHOOSE = 1111;
  public static final String PASS_STORY_KEY = "PASS_STORY_KEY";
  private String TAG = BrowseStoryActivity.class.getName();
  @BindView(R.id.imgStory) ImageView imgStory;
  @BindView(R.id.imgBack) ImageView imgBack;
  @BindView(R.id.imgBookmark) ImageView imgBookmark;
  @BindView(R.id.tvTitle) TextView tvTitle;
  @BindView(R.id.tvChapter) TextView tvChapter;
  @BindView(R.id.tvCard) TextView tvCard;
  @BindView(R.id.imgArrow) ImageView imgArrow;
  @BindView(R.id.linearStoryProgress) LinearLayout linearStoryProgress;
  @BindView(R.id.tvDescription) ReadMoreTextView tvDescription;
  @BindView(R.id.recyclerViewTags) RecyclerView recyclerViewTags;
  @BindView(R.id.recyclerViewRewards) RecyclerView recyclerViewRewards;
  @BindView(R.id.additionalTask) RelativeLayout additionalTask;
  @BindView(R.id.additionalTaskName) TextView additionalTaskName;
  @BindView(R.id.additionalTaskDescription) TextView additionalTaskDescription;
  @BindView(R.id.imgCamera) ImageView imgCamera;
  @BindView(R.id.tvRewardPoints) TextView tvRewardPoints;
  @BindView(R.id.tvTakeSelfie) TextView tvTakeSelfie;
  @BindView(R.id.btnCollectRewards) Button btnCollectRewards;
  @BindView(R.id.linearDetail) LinearLayout linearDetail;
  @BindView(R.id.storyProgressBar) SeekBar storyProgressBar;
  @BindView(R.id.recyclerViewChapter) RecyclerView recyclerViewChapter;
  @BindView(R.id.linearProgress) LinearLayout linearProgress;

  private TrackStory story = null;
  private StoryProgress storyProgress = null;
  private boolean isBookmarked = false;

  /**
   * onCreate
   *
   * @param savedInstanceState saved instance state
   */
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_browse_story);
    ButterKnife.bind(this);
    EventBus.getDefault().register(this);
    recyclerViewTags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    recyclerViewRewards.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    recyclerViewChapter.setLayoutManager(new LinearLayoutManager(this));
    recyclerViewTags.setNestedScrollingEnabled(false);
    recyclerViewRewards.setNestedScrollingEnabled(false);
    recyclerViewChapter.setNestedScrollingEnabled(false);
    DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
    itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.white_divider));
    recyclerViewTags.addItemDecoration(itemDecorator);
    recyclerViewRewards.addItemDecoration(itemDecorator);
    DividerItemDecoration itemDecoratorChapter = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
    recyclerViewChapter.addItemDecoration(itemDecoratorChapter);

    Long id = getIntent().getLongExtra(PASS_STORY_KEY, 0);
    apiService.getTrackStoryById(id).subscribe(this::onSuccess, this::onError);
    storyProgressBar.setOnTouchListener((v, event) -> true);
  }

  /**
   * throw an error
   *
   * @param throwable throwable
   */
  private void onError(Throwable throwable) {
    Timber.d(throwable);
    AppUtils.showError(throwable, getString(R.string.error));
  }

  /**
   * bookmarks fetched succeed.
   *
   * @param bookmarkList the bookmark list
   */
  private void onBookmarksSuccess(List<Bookmark> bookmarkList) {
    List<Bookmark> bookmarkList1 = bookmarkList;
    this.isBookmarked = false;
    for (Bookmark bookmark : bookmarkList1) {
      if (bookmark.getRacetrack() != null && this.story.getRacetrack() != null
          && this.story.getRacetrack().getId().equals(bookmark.getRacetrack().getId())) {
        isBookmarked = true;
      }
    }
    imgBookmark.setImageResource(isBookmarked ? R.drawable.ic_yellow_star : R.drawable.ic_bookmark);
  }

  /**
   * onSuccess api call fetch story.
   *
   * @param story get browse model
   */
  private void onSuccess(TrackStory story) {
    this.story = story;
    GlideApp.with(this).load(story.getLargeImageURL()).placeholder(R.drawable.ic_browse_ph).into(imgStory);
    tvCard.setText(getResources().getQuantityString(R.plurals.card, story.getCards().size(), story.getCards().size()));
    tvChapter.setText(getResources().getQuantityString(R.plurals.chapter, story.getChapters().size(), story.getChapters().size()));
    tvDescription.setText(story.getDescription());
    tvTitle.setText(story.getTitle());
    recyclerViewTags.setAdapter(new TagAdapter(story.getTags()));

    if (this.story.getCards() != null) {
      recyclerViewRewards.setAdapter(new RewardAdapter(story.getCards()));
    }

    if (story.getAdditionalTask() != null) {
      tvRewardPoints.setText(getString(R.string.reward_pts, story.getAdditionalTask().getPoints()));
      additionalTaskName.setText(story.getAdditionalTask().getName());
      additionalTaskDescription.setText(story.getDescription());
      this.story.getAdditionalTask().setTrackStoryId(this.story.getId());
    } else {
      additionalTask.setVisibility(View.GONE);
    }
    apiService.getStoryProgressById(this.story.getId())
        .subscribe(this::onProgressFetched, this::onError);
    apiService.getRacetrackBookmarks().subscribe(this::onBookmarksSuccess, this::onError);
  }

  /**
   * bookmark add succeed.
   *
   * @param bookmark the bookmark
   */
  private void onBookMarkAddSuccess(Bookmark bookmark) {
    isBookmarked = true;
    imgBookmark.setImageResource(R.drawable.ic_yellow_star);
  }

  /**
   * bookmark removed
   */
  private void onBookmarkRemoved(EmptyObject emptyObject) {
    isBookmarked = false;
    imgBookmark.setImageResource(R.drawable.ic_bookmark);
  }

  /**
   * story progress fetched succeed.
   *
   * @param storyProgress the story progress
   */
  private void onProgressFetched(StoryProgress storyProgress) {
    this.storyProgress = storyProgress;
    if (this.story.getChapters() == null || this.story.getChapters().size() <= 0) return;


    boolean activeFound = false; // first not read chapter
    int totalCount = 0;
    int readCount = 0;
    for (Chapter chapter : this.story.getChapters()) {
      ChapterProgress progress = getChapterProgress(chapter.getId());
      if (progress == null) {
        if (!activeFound) chapter.setIsActive(true);
        activeFound = true;
        progress = new ChapterProgress();
      }
      chapter.setProgress(progress);
      totalCount += chapter.getWordsCount();
      readCount += progress.getWordsRead();
    }
    storyProgressBar.setProgress((int) ((readCount * 100.0f) / (totalCount * 1.0f)));
    if (story.getChapters() != null) {
      ChapterAdapter chapterAdapter = new ChapterAdapter(story.getChapters());
      recyclerViewChapter.setAdapter(chapterAdapter);
      chapterAdapter.setRecycleOnItemClickListner((view, position) -> {

        ChapterStoryFragment.setStoryProgress(storyProgress); // inject story progress
        Chapter chapter = this.story.getChapters().get(position);
        Intent intent = new Intent(ActivityUtils.getTopActivity(), ChapterStoryActivity.class);
        // combine chapter id and story id , then jump to chapterStoryActivity
        intent.putExtra(ChapterStoryActivity.PASSED_KEY, this.story.getId() + ";" + chapter.getId());
        startActivityForResult(intent, AppConstants.CHAPTER_PROGRESS_RETURNED_CODE);
      });
    }


    if (storyProgress.getCompleted()
        && !storyProgress.getCardsAndRewardsReceived()) {
      btnCollectRewards.setEnabled(true);
      storyProgressBar.setProgress(100);  // completed
      btnCollectRewards.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_indicator));
    } else {

      if (storyProgress.getCardsAndRewardsReceived()) {
        btnCollectRewards.setText(R.string.rewardsReceived);
      }
      btnCollectRewards.setEnabled(false);
      btnCollectRewards.setBackgroundColor(ContextCompat.getColor(this, R.color.disable_color));
    }

    if (!storyProgress.getCompleted()) {  // check is can mark completed
      boolean isCompleted = true;
      for (Chapter c : this.story.getChapters()) {
        if (!c.getProgress().getCompleted()) {
          isCompleted = false;
          break;
        }
      }
      if (isCompleted) { // update story progress to completed
        apiService.completedStory(storyProgress.getId())
            .doOnNext(s -> apiService.checkForNewAchievements().subscribe())
            .subscribe(this::onProgressFetched, this::onError);
      }
    }

    if (storyProgress.getAdditionalTaskCompleted() && this.story.getAdditionalTask() != null) {
      disableAddtionalTaskBtn(); // disable additional task
    }

    if (this.story.getAdditionalTask() != null) {
      this.story.getAdditionalTask().setProgressId(this.storyProgress.getId());
    }
  }

  /**
   * get chapter progress by id
   *
   * @param chapterId the chapter id
   * @return the chapter progress
   */
  private ChapterProgress getChapterProgress(Long chapterId) {
    if (this.storyProgress == null || this.storyProgress.getChaptersUserProgress() == null) {
      return null;
    }
    for (ChapterProgress progress : this.storyProgress.getChaptersUserProgress()) {
      if (progress.getChapterId().equals(chapterId)) return progress;
    }
    return null;
  }

  /**
   * disable additional task btn
   */
  private void disableAddtionalTaskBtn() {
    tvTakeSelfie.setEnabled(false);
    tvTakeSelfie.setBackgroundColor(ContextCompat.getColor(this, R.color.disable_color));
    tvTakeSelfie.setTextColor(ContextCompat.getColor(this, R.color.white));
    tvTakeSelfie.setText("Completed");
  }


  @OnClick(R.id.linearStoryProgress) void onClick(View view) {
    if (linearDetail.isShown()) {
      linearDetail.setVisibility(View.GONE);
      linearProgress.setVisibility(View.VISIBLE);
      imgArrow.setRotation(180);
    } else {
      linearDetail.setVisibility(View.VISIBLE);
      linearProgress.setVisibility(View.GONE);
      imgArrow.setRotation(0);
    }
  }

  @OnClick(R.id.imgBookmark) void onBookmark() {
    if (isBookmarked) {
      apiService.removeBookmark(this.story.getRacetrack().getId())
          .subscribe(this::onBookmarkRemoved, this::onError);
    } else {
      apiService.createBookmark(this.story.getRacetrack().getId())
          .subscribe(this::onBookMarkAddSuccess, this::onError);
    }
  }

  @OnClick(R.id.imgBack) void onBack() {
    onBackPressed();
  }

  @Override public void onBackPressed() {
    if (!isBookmarked) { // removed book mark
      EventBus.getDefault().post(new RemoveEvent(true));
    }
    super.onBackPressed();
  }

  @OnClick(R.id.tvTakeSelfie) void onSelfie() {
    showDialog();
  }


  @Subscribe public void onEvent(AdditionalTaskEvent event) {
    if (event.isCompleted()) {
      this.storyProgress.setAdditionalTaskCompleted(true);
      this.disableAddtionalTaskBtn();
    }
  }

  @Override protected void onDestroy() {
    EventBus.getDefault().unregister(this);
    super.onDestroy();
  }


  @OnClick(R.id.btnCollectRewards) void onReward() {
    apiService.receiveRewards(this.storyProgress.getId()).subscribe(this::onReceiveRewardsSuccess, this::onError);
  }

  /**
   * when receive rewards succeed
   *
   * @param reward the rewards
   */
  private void onReceiveRewardsSuccess(StoryReward reward) {
    this.storyProgress.setCardsAndRewardsReceived(true);
    this.onProgressFetched(this.storyProgress);
    List<Card> cards = new ArrayList<>();
    if (reward.getUserCards() != null) {
      for (UserCard userCard : reward.getUserCards()) {
        cards.add(userCard.getCard());
      }
    }

    Intent intent = new Intent(ActivityUtils.getTopActivity(), CollectRewardActivity.class);
    intent.putExtra(CollectRewardActivity.STORY_CARDS_KEY, new Gson().toJson(cards));
    ActivityUtils.startActivity(intent);
  }

  @Subscribe public void onColletDone(FinishEvent event) {
    finish();
  }

  //Tag Adapter
  public static class TagAdapter extends BaseRecyclerAdapter<TagAdapter.MyViewHolder> {

    private final List<Tag> tagList;

    public TagAdapter(List<Tag> tagList) {
      this.tagList = tagList;
    }

    @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
      return new MyViewHolder(view);
    }

    @Override public void onBindViewHolder(MyViewHolder holder, int position) {
      holder.tvTag.setText(tagList.get(position).getValue());
    }

    @Override public int getItemCount() {
      return tagList.size();
    }

    public class MyViewHolder extends BaseRecyclerAdapter.ViewHolder {
      @BindView(R.id.tvTag) TextView tvTag;

      public MyViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
      }
    }
  }

  //Reward Adapter
  public static class RewardAdapter extends BaseRecyclerAdapter<RewardAdapter.MyViewHolder> {

    private final List<Card> rewardModelList;

    public RewardAdapter(List<Card> rewardModelList) {
      this.rewardModelList = rewardModelList;
    }

    @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_browse_reward, parent, false);
      return new MyViewHolder(view);
    }

    @Override public void onBindViewHolder(MyViewHolder holder, int position) {
      Card card = rewardModelList.get(position);
      holder.tvName.setText(card.getName());
      GlideApp.with(ActivityUtils.getTopActivity())
          .load(card.getImageURL()).placeholder(R.drawable.ic_card_close).into(holder.roundedImageView);
    }

    @Override public int getItemCount() {
      return rewardModelList.size();
    }

    public class MyViewHolder extends BaseRecyclerAdapter.ViewHolder {
      @BindView(R.id.tvName) TextView tvName;
      @BindView(R.id.imgReward) RoundedImageView roundedImageView;

      public MyViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
      }
    }
  }

  //Chapter Adapter
  public static class ChapterAdapter extends BaseRecyclerAdapter<ChapterAdapter.MyViewHolder> {

    private final List<Chapter> chapterModelList;
    private Context context;

    public ChapterAdapter(List<Chapter> chapterModelList) {
      this.chapterModelList = chapterModelList;
    }

    @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      context = parent.getContext();
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, parent, false);
      return new MyViewHolder(view);
    }

    @Override public void onBindViewHolder(MyViewHolder holder, int position) {
      Chapter model = chapterModelList.get(position);
      holder.tvName.setText(chapterModelList.get(position).getTitle());
      holder.tvCount.setText(String.valueOf(++position));

      int progressValue = model.getProgress().getCompleted()
          ? 100 : getProgress(model.getProgress().getWordsRead(), model.getWordsCount());
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        holder.circleProgress.setProgress(progressValue, true);
      } else {
        holder.circleProgress.setProgress(progressValue);
      }

      holder.tvTotal.setText(String.format(Locale.ENGLISH, "/%d", model.getWordsCount()));
      holder.tvProgress.setText(String.valueOf(model.getProgress().getWordsRead()));
      if (model.getProgress().getCompleted()) {
        holder.imgCheck.setVisibility(View.VISIBLE);
      } else {
        holder.imgCheck.setVisibility(View.GONE);
      }
      if (model.getIsActive()) {
        holder.itemView.setBackgroundColor(Color.WHITE);
        holder.tvCount.setBackgroundResource(R.drawable.blue_radius);
        holder.tvProgress.setTextColor(ContextCompat.getColor(context, R.color.green));
      } else {
        holder.tvCount.setBackgroundResource(R.drawable.gray_radius);
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue));
        holder.tvProgress.setTextColor(ContextCompat.getColor(context, R.color.hint_color));
      }
    }

    private int getProgress(int current, int total) {
      return (current * 100) / total;
    }

    @Override public int getItemCount() {
      return chapterModelList.size();
    }

    public class MyViewHolder extends BaseRecyclerAdapter.ViewHolder {
      @BindView(R.id.tvCount) TextView tvCount;
      @BindView(R.id.itemView) View itemView;
      @BindView(R.id.tvName) TextView tvName;
      @BindView(R.id.tvStoryCompleted) TextView tvProgress;
      @BindView(R.id.tvTotalStory) TextView tvTotal;
      @BindView(R.id.circleProgress) CircleProgressView circleProgress;
      @BindView(R.id.imgCheck) ImageView imgCheck;

      public MyViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        clickableViews(itemView);
      }
    }
  }

  /**
   * open gallery for image pick
   */
  private void openGallery() {
    Intent chooseImageIntent = ImagePicker.getPickImageIntent(this, false);
    startActivityForResult(chooseImageIntent, REQUEST_CODE_CHOOSE);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case REQUEST_CODE_CHOOSE:
        if (data == null || data.getData() == null) {  // user cancel choose images
        } else {
          Intent intent = new Intent(ActivityUtils.getTopActivity(), SelfieCompletedActivity.class);
          intent.putExtra(AppConstants.KEY_OBJ, new Gson().toJson(this.story.getAdditionalTask()));
          ActivityUtils.startActivity(intent);
        }
        break;
      case AppConstants.CHAPTER_PROGRESS_RETURNED_CODE:
        StoryProgress progress = new Gson().fromJson(data.getStringExtra(AppConstants.KEY_OBJ), StoryProgress.class);
        this.onProgressFetched(progress);
        break;
      default:
        super.onActivityResult(requestCode, resultCode, data);
        break;
    }
  }

  /**
   * permission check for external storage
   */
  private void permissionCheck() {
    Dexter.withActivity(this).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
      @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
        if (report.areAllPermissionsGranted()) {
          openGallery();
        } else {
          SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(findViewById(android.R.id.content), R.string.all_permissions_denied_feedback)
              .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
              .withCallback(new Snackbar.Callback() {
                @Override public void onShown(Snackbar snackbar) {
                  super.onShown(snackbar);
                }

                @Override public void onDismissed(Snackbar snackbar, int event) {
                  super.onDismissed(snackbar, event);
                }
              })
              .build();
        }
      }

      @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, final PermissionToken token) {
        new AlertDialog.Builder(BrowseStoryActivity.this).setTitle(R.string.permission_rationale_title)
            .setMessage(R.string.permission_rationale_message)
            .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
              dialog.dismiss();
              token.cancelPermissionRequest();
            })
            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
              dialog.dismiss();
              token.continuePermissionRequest();
            })
            .setOnDismissListener(dialog -> token.cancelPermissionRequest())
            .show();
      }
    }).check();
  }

  /**
   * show chooser dialog
   */
  private void showDialog() {
    // Create custom dialog object
    final Dialog dialog = new Dialog(this);
    // Include dialog.xml file
    dialog.setContentView(R.layout.dialog_image_picker);


    // set values for custom dialog components - text, image and button
    TextView camera = (TextView) dialog.findViewById(R.id.tvCamera);
    TextView gallery = (TextView) dialog.findViewById(R.id.tvGallery);

    dialog.show();

    camera.setOnClickListener(v -> {
      // Close dialog
      dialog.dismiss();
      Intent intent = new Intent(ActivityUtils.getTopActivity(), CameraActivity.class);
      intent.putExtra(AppConstants.KEY_OBJ, new Gson().toJson(this.story.getAdditionalTask()));
      ActivityUtils.startActivity(intent);
    });

    gallery.setOnClickListener(v -> {
      // Close dialog
      dialog.dismiss();
      permissionCheck();
    });
  }
}
