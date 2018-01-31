package com.topcoder.timobile.model;


import java.util.List;

import lombok.Data;

@Data
public class StoryReward {
  private UserBadge userBadge;
  private List<UserCard> userCards;
}
