package com.topcoder.timobile.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseActivity;
import com.topcoder.timobile.model.event.EnableEvent;
import com.topcoder.timobile.model.event.FinishEvent;
import org.greenrobot.eventbus.EventBus;

/**
 * Author: Harshvardhan
 * Date: 02/11/17
 */

public class SelfieCompletedActivity extends BaseActivity {
  @BindView(R.id.tvRewardPoints) TextView tvRewardPoints;
  @BindView(R.id.btnContinueStory) Button btnContinueStory;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_selfie_complete);
    ButterKnife.bind(this);
  }

  @OnClick(R.id.btnContinueStory) void onClick() {
    //post an event to finish activity
    finish();
    EventBus.getDefault().post(new EnableEvent(true));
    EventBus.getDefault().post(new FinishEvent(true));
  }
}
