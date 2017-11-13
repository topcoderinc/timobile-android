package com.topcoder.timobile.model;

import lombok.Data;

/**
 * Author: Harshvardhan
 * Date: 31/10/17
 */

@Data public class ChapterModel {
  private int id;
  private String name;
  private int total;
  private int progress;
  private boolean complete;
  private boolean activate;
}
