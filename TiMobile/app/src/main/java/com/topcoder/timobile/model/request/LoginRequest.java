package com.topcoder.timobile.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"password"})
public class LoginRequest {

  private String email;

  private String password;
}
