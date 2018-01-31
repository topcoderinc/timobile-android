package com.topcoder.timobile.model;


import java.util.List;

import lombok.Data;

@Data
public class PageResult<T> {
  private List<T> items;
  private Integer total;
  private Integer offset;
  private Integer limit;
}
