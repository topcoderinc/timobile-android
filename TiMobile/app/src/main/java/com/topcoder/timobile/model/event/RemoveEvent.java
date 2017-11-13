package com.topcoder.timobile.model.event;

import lombok.Data;

/**
 * Author: Harshvardhan
 * Date: 02/11/17
 */

@Data public class RemoveEvent {
  private boolean remove;

  public RemoveEvent(boolean remove) {
    this.remove = remove;
  }
}
