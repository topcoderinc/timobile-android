package com.topcoder.timobile.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.Data;

/**
 * Author: Harshvardhan
 * Date: 31/10/17
 */

@Data public class BrowseStoryModel {
  private int id;
  private String title;
  private String description;
  private String chapter;
  private String card;
  private String image;
  private boolean bookmark;
  private List<String> tags;
  @SerializedName("reward") private List<RewardModel> rewardModelList;
  private int storyProgress;
  @SerializedName("chapterStatus")
  private List<ChapterModel> chapterModelList;
  private int additionRewardPoints;
}
