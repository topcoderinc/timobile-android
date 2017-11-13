package com.topcoder.timobile.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseActivity;
import com.topcoder.timobile.customviews.roundimageview.RoundedImageView;
import com.topcoder.timobile.model.event.FinishEvent;
import com.topcoder.timobile.utility.CustomTypefaceSpan;
import org.greenrobot.eventbus.EventBus;

/**
 * Author: Harshvardhan
 * Date: 02/11/17
 */

public class CollectRewardActivity extends BaseActivity {
  @BindView(R.id.imgCardFirst) RoundedImageView imgCardFirst;
  @BindView(R.id.imgCardSecond) RoundedImageView imgCardSecond;
  @BindView(R.id.imgCardThree) RoundedImageView imgCardThree;
  @BindView(R.id.btnBackStory) Button btnBackStory;
  @BindView(R.id.btnMainMenu) Button btnMainMenu;
  @BindView(R.id.tvMsg) TextView tvMsg;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_collect_rewards);
    ButterKnife.bind(this);

    //custom font for specific word
    Typeface bold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");
    Typeface regular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
    tvMsg.setTextSize(14);
    SpannableStringBuilder SS = new SpannableStringBuilder("Wow! You have unlocked Card Name Lorem,\nCard Name Lorem, and Card Name Lorem.\nCheck your trading card gallery for details!");
    SS.setSpan(new CustomTypefaceSpan("", regular), 0, 22, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    SS.setSpan(new CustomTypefaceSpan("", bold), 23, 56, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    SS.setSpan(new CustomTypefaceSpan("", regular), 57, 60, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    SS.setSpan(new CustomTypefaceSpan("", bold), 61, 77, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    tvMsg.setText(SS);
  }

  @OnClick({ R.id.btnBackStory, R.id.btnMainMenu }) public void onViewClicked(View view) {
    finish();
    switch (view.getId()) {
      case R.id.btnBackStory:
        EventBus.getDefault().post(new FinishEvent(true));
        break;
      case R.id.btnMainMenu:
        //post an event for finish activity
        FinishEvent event = new FinishEvent(true);
        event.setFinishBrowseStory(true);
        EventBus.getDefault().post(event);
        break;
    }

  }
}
