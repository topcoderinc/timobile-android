package com.topcoder.timobile.model;

import lombok.Data;

@Data
public class ChapterProgress {
  private Long id;
  private Long chapterId;
  private Long trackStoryUserProgressId;
  private Integer wordsRead = 0;
  private Boolean completed = false;
}
