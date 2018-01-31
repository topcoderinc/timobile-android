package com.topcoder.timobile.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"newPassword"})
public class ChangeForgotPassword {
  private String email;
  private String forgotPasswordToken;
  private String newPassword;
}
