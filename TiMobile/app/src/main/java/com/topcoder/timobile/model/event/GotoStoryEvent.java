package com.topcoder.timobile.model.event;


import lombok.Data;

@Data
public class GotoStoryEvent {
  private Long storyId;

  public GotoStoryEvent(Long storyId) {
    this.storyId = storyId;
  }
}
