package com.topcoder.timobile.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;
import timber.log.Timber;

import com.google.gson.Gson;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseActivity;
import com.topcoder.timobile.model.AdditionalTask;
import com.topcoder.timobile.model.EmptyObject;
import com.topcoder.timobile.model.event.AdditionalTaskEvent;
import com.topcoder.timobile.model.event.EnableEvent;
import com.topcoder.timobile.model.event.FinishEvent;
import com.topcoder.timobile.utility.AppConstants;
import com.topcoder.timobile.utility.AppUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Author: Harshvardhan
 * Date: 02/11/17
 */

public class SelfieCompletedActivity extends BaseActivity {
  @BindView(R.id.tvRewardPoints) TextView tvRewardPoints;
  @BindView(R.id.btnContinueStory) Button btnContinueStory;
  @BindView(R.id.tv_points_description) TextView tvDescription;


  private AdditionalTask additionalTask;


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_selfie_complete);
    ButterKnife.bind(this);
    additionalTask = new Gson().fromJson(
        getIntent().getStringExtra(AppConstants.KEY_OBJ), AdditionalTask.class);
    setPoints(0);
    apiService.completeAdditionalTask(additionalTask.getProgressId())
        .subscribe(this::onCompletedAdditionalTask, this::onError);
  }

  private void setPoints(int p) {
    String points = (p == 0 ? "N/A" : p) + " pts";
    tvRewardPoints.setText(points);
    String description = String.format(getString(R.string.earn_points), p == 0 ? "N/A" : p);
    tvDescription.setText(description);
  }

  protected void onError(Throwable throwable) {
    cancelLoadingDialog();
    Timber.e(throwable);
    AppUtils.showError(throwable, "Cannot completed task");
  }

  private void onCompletedAdditionalTask(EmptyObject emptyObject) {
    setPoints(additionalTask.getPoints());
    cancelLoadingDialog();
    EventBus.getDefault().post(new AdditionalTaskEvent(true));
  }

  @OnClick(R.id.btnContinueStory) void onClick() {
    //post an event to finish activity
    finish();
    EventBus.getDefault().post(new EnableEvent(true));
    EventBus.getDefault().post(new FinishEvent(true));
  }
}
