package com.topcoder.timobile.model;

import java.util.List;

import lombok.Data;


@Data
public class StoryProgress {
  private Long id;
  private Long trackStoryId;
  private Long userId;
  private List<ChapterProgress> chaptersUserProgress;
  private Boolean completed;
  private Boolean cardsAndRewardsReceived;
  private Boolean additionalTaskCompleted;
}
