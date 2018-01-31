package com.topcoder.timobile.model;

import com.google.gson.annotations.SerializedName;

import lombok.Data;


@Data
public class PreferenceOption {
  private Long id;
  private String value;

  @SerializedName("default")
  private Boolean defaultValue;
}
