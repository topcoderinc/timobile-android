package com.topcoder.timobile.model;


import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {


  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String profilePhotoURL;
  private String facebookToken;
  private String verified;
  private String role;
  private Integer pointsAmount;
  private Date createdAt;
  private Date updatedAt;

  private String password;


  public String getName() {
    return this.firstName + " " + this.lastName;
  }

}