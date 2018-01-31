package com.topcoder.timobile.model;


import lombok.Data;

@Data
public class Card {
  private Long id;
  private Long trackStoryId;
  private String name;
  private String description;
  private String imageURL;
  private String type;
  private Integer pricePoints;
}
