package com.topcoder.timobile.model;

import lombok.Data;

@Data
public class Bookmark {
  private Long id;
  private Long userId;
  private Racetrack racetrack;
}
