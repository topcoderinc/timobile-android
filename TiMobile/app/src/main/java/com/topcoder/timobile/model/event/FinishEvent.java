package com.topcoder.timobile.model.event;

import lombok.Data;

/**
 * Author: Harshvardhan
 * Date: 02/11/17
 */

@Data public class FinishEvent {
  private boolean finish;
  private boolean finishBrowseStory;

  public FinishEvent(boolean finish) {
    this.finish = finish;
  }
}
