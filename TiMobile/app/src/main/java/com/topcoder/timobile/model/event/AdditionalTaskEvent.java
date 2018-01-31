package com.topcoder.timobile.model.event;


import lombok.Data;

@Data
public class AdditionalTaskEvent {
  boolean isCompleted;

  public AdditionalTaskEvent(boolean isCompleted) {
    this.isCompleted = isCompleted;
  }
}
