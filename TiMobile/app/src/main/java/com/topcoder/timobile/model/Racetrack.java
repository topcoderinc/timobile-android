package com.topcoder.timobile.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class Racetrack {
  private Long id;
  private String name;
  private State state;
  private String locality;
  private String street;
  private Float locationLat;
  private Float locationLng;
}
