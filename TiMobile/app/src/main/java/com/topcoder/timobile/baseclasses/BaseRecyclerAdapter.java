/*
 * Copyright (c) 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential by BNBJobs
 */

package com.topcoder.timobile.baseclasses;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Author: Harshvardhan
 * Date: 11/7/16.
 */

public abstract class BaseRecyclerAdapter<T extends BaseRecyclerAdapter.ViewHolder> extends RecyclerView.Adapter<T> {

  private RecycleOnItemClickListener mRecycleOnItemClickListener;

  public void setRecycleOnItemClickListner(RecycleOnItemClickListener mRecycleOnItemClickListener) {
    this.mRecycleOnItemClickListener = mRecycleOnItemClickListener;
  }

  public interface RecycleOnItemClickListener {
    public void onItemClick(View view, int position);
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (mRecycleOnItemClickListener != null) {
          mRecycleOnItemClickListener.onItemClick(v, getLayoutPosition());
        }
      }
    };

    public ViewHolder(View itemView) {
      super(itemView);
    }

    //put here clickable views list
    public void clickableViews(View... views) {
      for (View view : views) {
        view.setOnClickListener(mOnClickListener);
      }
    }
  }
}
