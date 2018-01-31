package com.topcoder.timobile.model;

import lombok.Data;

@Data
public class Badge {
  private Long id;
  private Long trackStoryId;
  private String name;
  private String description;
  private String imageUrl;
}
