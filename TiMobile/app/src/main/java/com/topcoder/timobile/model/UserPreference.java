package com.topcoder.timobile.model;

import java.util.Date;

import lombok.Data;

@Data
public class UserPreference {
  private Long id;
  private Long userId;
  private PreferenceOption preferenceOption;
  private Date createdAt;
  private Boolean selected;
  private Long preferenceOptionId;
}
