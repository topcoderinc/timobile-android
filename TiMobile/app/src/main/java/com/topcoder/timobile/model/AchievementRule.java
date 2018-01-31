package com.topcoder.timobile.model;

import lombok.Data;

@Data
public class AchievementRule {
  private Long id;
  private Long achievementId;
  private String model;
  private String whereClause;
  private Integer countNumber;
}
