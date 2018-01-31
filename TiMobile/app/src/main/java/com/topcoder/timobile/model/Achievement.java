package com.topcoder.timobile.model;

import lombok.Data;

@Data
public class Achievement {
  private Long id;
  private String name;
  private String description;
  private String imageURL;
  private AchievementRule achievementRule;
}
