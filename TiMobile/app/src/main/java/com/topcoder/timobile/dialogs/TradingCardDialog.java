package com.topcoder.timobile.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.google.gson.Gson;
import com.timobileapp.R;
import com.topcoder.timobile.api.ApiService;
import com.topcoder.timobile.customviews.roundimageview.RoundedImageView;
import com.topcoder.timobile.fragments.RewardShopFragment;
import com.topcoder.timobile.glide.GlideApp;
import com.topcoder.timobile.model.Card;
import com.topcoder.timobile.model.event.BuyCardEvent;
import com.topcoder.timobile.model.event.FragmentSwitchEvent;
import com.topcoder.timobile.model.event.GotoStoryEvent;
import com.topcoder.timobile.utility.AppConstants;
import com.topcoder.timobile.utility.AppUtils;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lombok.Setter;
import timber.log.Timber;


/**
 * simple trading card dialog
 */
public class TradingCardDialog extends DialogFragment {

  @BindView(R.id.imgReward) RoundedImageView imgReward;
  @BindView(R.id.tvCardTitle) TextView tvCardTitle;
  @BindView(R.id.tvCardDescription) TextView tvCardDescription;
  @BindView(R.id.tvRelateTo) TextView tvRelateTo;
  @BindView(R.id.btnGoto) Button btnGoto;
  Unbinder unbinder;

  private Card card;
  private static Gson gson = new Gson();
  @Setter private ApiService apiService;

  public static TradingCardDialog newInstance(Card card, ApiService apiService) {
    TradingCardDialog dialog = new TradingCardDialog();
    dialog.setApiService(apiService);
    Bundle bundle = new Bundle();
    bundle.putString(AppConstants.KEY_OBJ, gson.toJson(card));
    dialog.setArguments(bundle);
    return dialog;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    card = gson.fromJson(getArguments().getString(AppConstants.KEY_OBJ), Card.class);
  }

  @OnClick(R.id.btnGoto) public void onGotoChapter() {
    dismiss();
    if (card.getType().equals("Market")) {
      EventBus.getDefault().post(new FragmentSwitchEvent(new RewardShopFragment(), R.id.nav_reward));
    } else {
      EventBus.getDefault().post(new GotoStoryEvent(card.getTrackStoryId()));
    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_trading_cards, container, true);
    unbinder = ButterKnife.bind(this, view);

    getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

    if (getDialog().getWindow() != null) {
      WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
      params.height = WindowManager.LayoutParams.MATCH_PARENT;
      params.width = WindowManager.LayoutParams.MATCH_PARENT;
      getDialog().getWindow().setAttributes(params);
    }
    setCancelable(true);
    tvCardTitle.setText(card.getName());
    tvCardDescription.setText(card.getDescription());
    GlideApp.with(ActivityUtils.getTopActivity()).load(card.getImageURL())
        .placeholder(R.drawable.ic_card_close).into(imgReward);

    if (card.getType().equals("Market")) {
      tvRelateTo.setText(R.string.card_type_market);
      btnGoto.setText(R.string.go_to_market);
    } else {
      tvRelateTo.setText("Story : N/A");

      apiService.getTrackStoryById(card.getTrackStoryId()).subscribe(story -> {
        tvRelateTo.setText(story.getTitle());
      }, throwable -> {
        Timber.e(throwable);
        AppUtils.showError(throwable, "fetch story failed");
      });
    }
    return view;
  }


  @Override public void onDestroy() {
    unbinder.unbind();
    super.onDestroy();
  }
}

