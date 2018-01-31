package com.topcoder.timobile.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString(exclude = {"oldPassword", "newPassword"})
public class UpdatePassword {
  private String oldPassword;
  private String newPassword;
}
