package com.topcoder.timobile.model;


import java.util.Date;

import lombok.Data;

@Data
public class Comment {
  private Long id;
  private Long trackStoryId;
  private Long chapterId;
  private String text;
  private Long userId;

  private User user;
  private String type;

  private Date createdAt;
  private Date updatedAt;
}
