package com.topcoder.timobile.model;

import java.util.Date;

import lombok.Data;


@Data
public class DailyTask {
  private Long id;
  private String name;
  private String description;
  private Integer points;
  private Date date;
  private String partnerLink;
  private Boolean active;
}
