package com.topcoder.timobile.model;

import lombok.Data;

/**
 * Author: Harshvardhan
 * Date: 30/10/17
 */

@Data public class StoryModel {
  private int id;
  private String title;
  private String raceCourse;
  private String description;
  private String chapter;
  private String card;
  private String distance;
  private String image;
  private String lat;
  private String lng;
}
