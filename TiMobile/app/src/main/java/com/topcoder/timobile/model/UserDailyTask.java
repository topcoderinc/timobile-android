package com.topcoder.timobile.model;

import java.util.Date;

import lombok.Data;


@Data
public class UserDailyTask {
  public Long id;
  public Long userId;
  private DailyTask dailyTask;
  private Date createdAt;
  private Boolean completed;
}
