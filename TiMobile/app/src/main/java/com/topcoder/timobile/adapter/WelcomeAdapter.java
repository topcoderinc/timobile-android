package com.topcoder.timobile.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.timobileapp.R;

/**
 * Author: Harshvardhan
 * Date: 26/10/17.
 */
public class WelcomeAdapter extends PagerAdapter {

  private final Context context;
  private final String[] welcomeText;

  public WelcomeAdapter(Context context) {
    this.context = context;
    welcomeText = context.getResources().getStringArray(R.array.welcome_text);
  }

  @Override public int getCount() {
    return welcomeText.length;
  }

  @Override public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }

  @Override public void destroyItem(ViewGroup view, int position, Object object) {
    view.removeView((View) object);
  }

  @Override public Object instantiateItem(ViewGroup container, int position) {
    LayoutInflater inflater = LayoutInflater.from(context);
    ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.item_welcome, container, false);
    TextView tvWelcomeText = layout.findViewById(R.id.tvWelcomeText);
    tvWelcomeText.setText(welcomeText[position]);
    container.addView(layout);
    return layout;
  }
}
