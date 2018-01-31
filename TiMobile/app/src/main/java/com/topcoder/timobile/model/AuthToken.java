package com.topcoder.timobile.model;


import java.util.Date;

import lombok.Data;

/**
 * the login auth token entity.
 */

@Data
public class AuthToken {


  private String accessToken;

  private Date accessTokenValidUntil;

}
