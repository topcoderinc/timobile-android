package com.topcoder.timobile.model;

import com.topcoder.timobile.utility.AppConstants;

import java.util.Collections;
import java.util.List;

import lombok.Data;

@Data
public class TrackStory {
  private Long id;
  private String title;
  private String subtitle;
  private List<Tag> tags;
  private List<Chapter> chapters;
  private List<Card> cards;
  private Racetrack racetrack;
  private String smallImageURL;
  private String largeImageURL;
  private Badge badge;
  private AdditionalTask additionalTask;

  private String chapterContents;

  public String getDescription() {
    String d = this.getDescriptionFromChapter();
    return d.substring(0, Math.min(AppConstants.MAX_STORY_DESCRIPTION_LEN, d.length()));
  }

  private String getDescriptionFromChapter() {
    if (this.chapterContents != null) return this.chapterContents;
    StringBuilder description = new StringBuilder();
    for (Chapter chapter : this.getChapters()) {
      description.append(description.length() == 0 ? "" : " ").append(chapter.getContent());
    }
    this.chapterContents = description.toString();
    return this.chapterContents;
  }

  public List<Chapter> getChapters() {
    if (chapters.size() <= 0) return chapters;
    Collections.sort(chapters, (c1, c2) -> c1.getNumber() - c2.getNumber());
    return chapters;
  }
}
