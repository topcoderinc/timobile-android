package com.topcoder.timobile.model.event;

import lombok.Data;

/**
 * Author: Harshvardhan
 * Date: 02/11/17
 */

@Data public class EnableEvent {
  boolean enable;

  public EnableEvent(boolean enable) {
    this.enable = enable;
  }
}
