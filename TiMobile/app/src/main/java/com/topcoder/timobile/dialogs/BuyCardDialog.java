package com.topcoder.timobile.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
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
import com.topcoder.timobile.customviews.roundimageview.RoundedImageView;
import com.topcoder.timobile.glide.GlideApp;
import com.topcoder.timobile.model.Card;
import com.topcoder.timobile.model.event.BuyCardEvent;
import com.topcoder.timobile.utility.AppConstants;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * buy card dialog
 */
public class BuyCardDialog extends DialogFragment {

  @BindView(R.id.imgReward) RoundedImageView imgReward;
  @BindView(R.id.tvCardTitle) TextView tvCardTitle;
  @BindView(R.id.tvCardDescription) TextView tvCardDescription;
  @BindView(R.id.tvRewardPoints) TextView tvRewardPoints;
  Unbinder unbinder;


  private Card card;
  private static Gson gson = new Gson();


  public static BuyCardDialog newInstance(Card card) {
    BuyCardDialog dialog = new BuyCardDialog();
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

  @OnClick(R.id.btnBuy) public void onBuy() {
    EventBus.getDefault().post(new BuyCardEvent(card, "buy"));
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_buy_card, container, true);

    getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

    if (getDialog().getWindow() != null) {
      WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
      params.height = WindowManager.LayoutParams.MATCH_PARENT;
      params.width = WindowManager.LayoutParams.MATCH_PARENT;
      getDialog().getWindow().setAttributes(params);
    }
    unbinder = ButterKnife.bind(this, view);
    setCancelable(true);

    tvCardTitle.setText(card.getName());
    tvCardDescription.setText(card.getDescription());
    String points = card.getPricePoints() + " pts";
    tvRewardPoints.setText(points);
    GlideApp.with(ActivityUtils.getTopActivity()).load(card.getImageURL())
        .placeholder(R.drawable.ic_card_close).into(imgReward);
    return view;
  }


  @Override public void onDestroy() {
    unbinder.unbind();
    super.onDestroy();
  }
}

