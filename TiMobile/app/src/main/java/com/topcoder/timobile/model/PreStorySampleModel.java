package com.topcoder.timobile.model;

import lombok.Data;

/**
 * Author: Harshvardhan
 * Date: 28/10/17
 */

@Data
public class PreStorySampleModel {
  protected long id;
  protected String value;
  protected String name;
  protected boolean isCheck;


  public String getValue() {
    if (this.value == null) return this.name;
    return this.value;
  }
}
