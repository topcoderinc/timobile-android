package com.topcoder.timobile.model;

import lombok.Data;

/**
 * Author: Harshvardhan
 * Date: 02/11/17
 */

@Data public class CommentModel {
  private String message;
  private String userName;
  private long date;
  private String userProfileImageUrl;
  private boolean isNew;

}
