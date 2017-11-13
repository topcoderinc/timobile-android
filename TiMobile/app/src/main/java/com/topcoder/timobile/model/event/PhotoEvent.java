package com.topcoder.timobile.model.event;

import lombok.Data;

/**
 * Author: Harshvardhan
 * Date: 02/11/17
 */

@Data public class PhotoEvent {
  private String path;

  public PhotoEvent(String path) {
    this.path = path;
  }
}
