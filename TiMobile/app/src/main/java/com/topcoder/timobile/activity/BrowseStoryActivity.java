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
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
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
import com.topcoder.timobile.glide.GlideApp;
import com.topcoder.timobile.model.BrowseStoryModel;
import com.topcoder.timobile.model.ChapterModel;
import com.topcoder.timobile.model.RewardModel;
import com.topcoder.timobile.model.event.EnableEvent;
import com.topcoder.timobile.model.event.FinishEvent;
import com.topcoder.timobile.model.event.RemoveEvent;
import com.topcoder.timobile.utility.AppConstants;
import com.topcoder.timobile.utility.ImagePicker;
import java.util.List;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

/**
 * Author: Harshvardhan
 * Date: 31/10/17
 */

public class BrowseStoryActivity extends BaseActivity {

  private static final int REQUEST_CODE_CHOOSE = 1111;
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
  @BindView(R.id.imgCamera) ImageView imgCamera;
  @BindView(R.id.tvRewardPoints) TextView tvRewardPoints;
  @BindView(R.id.tvTakeSelfie) TextView tvTakeSelfie;
  @BindView(R.id.btnCollectRewards) Button btnCollectRewards;
  @BindView(R.id.linearDetail) LinearLayout linearDetail;
  @BindView(R.id.storyProgressBar) SeekBar storyProgressBar;
  @BindView(R.id.recyclerViewChapter) RecyclerView recyclerViewChapter;
  @BindView(R.id.linearProgress) LinearLayout linearProgress;

  private BrowseStoryModel model;
  private ChapterAdapter chapterAdapter;
  private boolean isBookmarked;

  /**
   * onCreate
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
    apiService.browseStory().subscribe(this::onSuccess, this::onError);
    storyProgressBar.setOnTouchListener((v, event) -> true);
  }

  /**
   * throw an error
   * @param throwable throwable
   */
  private void onError(Throwable throwable) {
    Timber.d(throwable);
    ToastUtils.showShort(getString(R.string.error));
  }

  /**
   * onSuccess api call
   * @param browseStoryModel get browse model
   */
  private void onSuccess(BrowseStoryModel browseStoryModel) {
    model = browseStoryModel;
    GlideApp.with(this).load(model.getImage()).placeholder(R.drawable.ic_browse_ph).into(imgStory);
    tvCard.setText(getResources().getQuantityString(R.plurals.card, Integer.parseInt(model.getCard()), model.getCard()));
    tvChapter.setText(getResources().getQuantityString(R.plurals.chapter, Integer.parseInt(model.getChapter()), model.getChapter()));
    tvDescription.setText(model.getDescription());
    recyclerViewTags.setAdapter(new TagAdapter(model.getTags()));
    //this is temp when api integrate we get status from it
    isBookmarked = getIntent().getBooleanExtra(AppConstants.KEY_BOOl, false);
    model.setBookmark(isBookmarked);

    if (model.isBookmark()) {
      imgBookmark.setImageResource(R.drawable.ic_yellow_star);
    }
    recyclerViewRewards.setAdapter(new RewardAdapter(model.getRewardModelList()));
    tvRewardPoints.setText(getString(R.string.reward_pts, model.getAdditionRewardPoints()));
    chapterAdapter = new ChapterAdapter(model.getChapterModelList());
    recyclerViewChapter.setAdapter(chapterAdapter);
    chapterAdapter.setRecycleOnItemClickListner((view, position) -> {
      ActivityUtils.startActivity(ChapterStoryActivity.class);
    });
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
    //remove code when api integrate
    isBookmarked = !isBookmarked;
    if (model.isBookmark()) {
      imgBookmark.setImageResource(R.drawable.ic_bookmark);
    } else {
      imgBookmark.setImageResource(R.drawable.ic_yellow_star);
    }
    model.setBookmark(!model.isBookmark());
  }

  @OnClick(R.id.imgBack) void onBack() {
    onBackPressed();
  }

  @Override public void onBackPressed() {
    //remove code when api integrate
    if (!isBookmarked) {
      EventBus.getDefault().post(new RemoveEvent(true));
    }
    super.onBackPressed();
  }

  @OnClick(R.id.tvTakeSelfie) void onSelfie() {
    showDialog();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }

  /**
   * list on specific event
   * @param event event
   */

  @Subscribe public void onEvent(EnableEvent event) {
    btnCollectRewards.setEnabled(event.isEnable());
    //remove when api integrate
    storyProgressBar.setProgress(100);
    btnCollectRewards.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_indicator));
  }

  @Subscribe public void onEvent(FinishEvent event) {
    if (event.isFinishBrowseStory()) {
      finish();
    }
  }

  @OnClick(R.id.btnCollectRewards) void onReward() {
    ActivityUtils.startActivity(CollectRewardActivity.class);
  }

  //Tag Adapter
  public static class TagAdapter extends BaseRecyclerAdapter<TagAdapter.MyViewHolder> {

    private final List<String> tagList;

    public TagAdapter(List<String> tagList) {
      this.tagList = tagList;
    }

    @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
      return new MyViewHolder(view);
    }

    @Override public void onBindViewHolder(MyViewHolder holder, int position) {
      holder.tvTag.setText(tagList.get(position));
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

    private final List<RewardModel> rewardModelList;

    public RewardAdapter(List<RewardModel> rewardModelList) {
      this.rewardModelList = rewardModelList;
    }

    @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_browse_reward, parent, false);
      return new MyViewHolder(view);
    }

    @Override public void onBindViewHolder(MyViewHolder holder, int position) {
      holder.tvName.setText(rewardModelList.get(position).getName());
    }

    @Override public int getItemCount() {
      return rewardModelList.size();
    }

    public class MyViewHolder extends BaseRecyclerAdapter.ViewHolder {
      @BindView(R.id.tvName) TextView tvName;

      public MyViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
      }
    }
  }

  //Chapter Adapter
  public static class ChapterAdapter extends BaseRecyclerAdapter<ChapterAdapter.MyViewHolder> {

    private final List<ChapterModel> chapterModelList;
    private Context context;

    public ChapterAdapter(List<ChapterModel> chapterModelList) {
      this.chapterModelList = chapterModelList;
    }

    @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      context = parent.getContext();
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, parent, false);
      return new MyViewHolder(view);
    }

    @Override public void onBindViewHolder(MyViewHolder holder, int position) {
      ChapterModel model = chapterModelList.get(position);
      holder.tvName.setText(chapterModelList.get(position).getName());
      holder.tvCount.setText(String.valueOf(++position));

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        holder.circleProgress.setProgress(getProgress(model.getProgress(), model.getTotal()), true);
      } else {
        holder.circleProgress.setProgress(getProgress(model.getProgress(), model.getTotal()));
      }

      holder.tvTotal.setText(String.format(Locale.ENGLISH, "/%d", model.getTotal()));
      holder.tvProgress.setText(String.valueOf(model.getProgress()));
      if (model.isComplete()) {
        holder.imgCheck.setVisibility(View.VISIBLE);
      } else {
        holder.imgCheck.setVisibility(View.GONE);
      }
      if (model.isActivate()) {
        holder.itemView.setBackgroundColor(Color.WHITE);
        holder.tvCount.setBackgroundResource(R.drawable.blue_radius);
        holder.tvProgress.setTextColor(ContextCompat.getColor(context,R.color.green));
      } else {
        holder.tvCount.setBackgroundResource(R.drawable.gray_radius);
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue));
        holder.tvProgress.setTextColor(ContextCompat.getColor(context,R.color.hint_color));
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
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case REQUEST_CODE_CHOOSE:
        Timber.d("here uri " + data.getData());
        ActivityUtils.startActivity(SelfieCompletedActivity.class);
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
      ActivityUtils.startActivity(CameraActivity.class);
    });

    gallery.setOnClickListener(v -> {
      // Close dialog
      dialog.dismiss();
      permissionCheck();
    });
  }
}
