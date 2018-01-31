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

import com.blankj.utilcode.util.ActivityUtils;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseActivity;
import com.topcoder.timobile.customviews.roundimageview.RoundedImageView;
import com.topcoder.timobile.glide.GlideApp;
import com.topcoder.timobile.model.Card;
import com.topcoder.timobile.model.event.FinishEvent;
import com.topcoder.timobile.utility.AppUtils;
import com.topcoder.timobile.utility.CustomTypefaceSpan;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Harshvardhan
 * Date: 02/11/17
 */

public class CollectRewardActivity extends BaseActivity {

  public static final String STORY_CARDS_KEY = "STORY_CARDS_KEY";

  @BindView(R.id.imgCardFirst) RoundedImageView imgCardFirst;
  @BindView(R.id.imgCardSecond) RoundedImageView imgCardSecond;
  @BindView(R.id.imgCardThree) RoundedImageView imgCardThree;
  @BindView(R.id.btnBackStory) Button btnBackStory;
  @BindView(R.id.btnMainMenu) Button btnMainMenu;
  @BindView(R.id.tvMsg) TextView tvMsg;
  @BindView(R.id.tvRewardTitle) TextView tvRewardTitle;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_collect_rewards);
    ButterKnife.bind(this);

    List<Card> cards = AppUtils.jsonStringToList(getIntent().getStringExtra(STORY_CARDS_KEY), Card[].class);
    init(cards);

  }

  private void updateRichText(SpannableStringBuilder spannableStringBuilder,
                              String value,
                              Typeface typeface) {
    spannableStringBuilder.setSpan(new CustomTypefaceSpan("", typeface),
        spannableStringBuilder.length() - value.length(),
        spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
  }

  /**
   * init cards
   *
   * @param cards
   */
  private void init(List<Card> cards) {
    String title = String.format(getString(R.string.congratulations_card), cards.size()) + (cards.size() > 1 ? "s" : "");
    tvRewardTitle.setText(title);

    List<RoundedImageView> imageViews = new ArrayList<>();
    imageViews.add(imgCardFirst);
    imageViews.add(imgCardSecond);
    imageViews.add(imgCardThree);

    for (int i = cards.size(); i < 3; i++) {
      imageViews.get(i).setVisibility(View.GONE);
    }
    for (int i = 0; i < cards.size() && i < 3; i++) {
      Card card = cards.get(i);
      if (card.getImageURL() == null) continue;
      GlideApp.with(ActivityUtils.getTopActivity())
          .load(card.getImageURL())
          .placeholder(R.drawable.ic_story_ph).into(imageViews.get(i));
    }

    //custom font for specific word
    Typeface bold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");
    Typeface regular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
    tvMsg.setTextSize(14);

    String prefix = "Wow! You have unlocked ";
    String suffix = "\nCheck your trading card gallery for details!";
    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(prefix);
    for (int i = 0; i < cards.size(); i++) {
      Card card = cards.get(i);
      if (i > 0) {
        String p = ",";
        if (i == 1) {
          p += "\n";
        } else {
          p += " ";
        }
        if (i == cards.size() - 1) {
          p += "and ";
        }
        spannableStringBuilder.append(p);
        this.updateRichText(spannableStringBuilder, p, regular);
        spannableStringBuilder.append(card.getName());
        this.updateRichText(spannableStringBuilder, card.getName(), bold);
      } else {
        spannableStringBuilder.append(card.getName());
        this.updateRichText(spannableStringBuilder, card.getName(), bold);
      }
    }
    spannableStringBuilder.append(suffix);
    this.updateRichText(spannableStringBuilder, suffix, regular);
    tvMsg.setText(spannableStringBuilder);
  }

  @OnClick({R.id.btnBackStory, R.id.btnMainMenu}) public void onViewClicked(View view) {
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
