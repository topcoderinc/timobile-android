package com.topcoder.timobile.model;


import lombok.Data;

@Data
public class UserAchievement {
  private Long id;
  private Long userId;
  private Achievement achievement;
}
