package com.topcoder.timobile.utility;


import android.support.v7.widget.RecyclerView;
import android.util.Log;

import lombok.Setter;

public class LoadMore {

  @Setter private OnLoadMoreListener onLoadMoreListener;

  private boolean isLoading = false;


  private Long preFetchTime = System.currentTimeMillis();

  public LoadMore(RecyclerView recyclerView) {
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int offset = recyclerView.computeVerticalScrollOffset();
        int extent = recyclerView.computeVerticalScrollExtent();
        int range = recyclerView.computeVerticalScrollRange();
        int percentage = (int) (100.0 * offset / (float) (range - extent));
        Long now = System.currentTimeMillis();
        Long diff = now - preFetchTime;

        if (diff > AppConstants.UPDATE_PROGRESS_INTERVAL
            && percentage > 90
            && !isLoading
            && dy > 0
            ) {
          if (onLoadMoreListener != null) {
            onLoadMoreListener.onLoadMore();
            Log.d("Load more", "onScrolled: on load more ------- >");
          }
          isLoading = true;
          preFetchTime = now;
        }
      }
    });
  }

  public void loadedDone() {
    isLoading = false;
  }
}
