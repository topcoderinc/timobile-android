package com.topcoder.timobile.model;

import lombok.Data;

@Data
public class AdditionalTask {
  private Long id;
  private Long trackStoryId;
  private Long progressId;
  private String name;
  private String description;
  private Integer points;
}
