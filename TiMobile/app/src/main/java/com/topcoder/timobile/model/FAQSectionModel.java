package com.topcoder.timobile.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.Data;

/**
 * Author: Harshvardhan
 * Date: 01/11/17
 */

@Data public class FAQSectionModel  {
  private String topicTitle;
  private int id;
  @SerializedName("topic") private List<FAQDetailModel> modelList;


}
