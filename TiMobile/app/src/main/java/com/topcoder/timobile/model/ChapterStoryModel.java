package com.topcoder.timobile.model;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Data;

/**
 * Author: Harshvardhan
 * Date: 02/11/17
 */

@Data public class ChapterStoryModel implements Parcelable {
  private int id;
  private String title;
  private String racecourse;
  private String description;
  private String video;

  private void readIn(Parcel in) {
    id = in.readInt();
    title = in.readString();
    racecourse = in.readString();
    description = in.readString();
    video = in.readString();
  }

  public static final Creator<ChapterStoryModel> CREATOR = new Creator<ChapterStoryModel>() {
    @Override public ChapterStoryModel createFromParcel(Parcel in) {
      ChapterStoryModel model = new ChapterStoryModel();
      model.readIn(in);
      return model;
    }

    @Override public ChapterStoryModel[] newArray(int size) {
      return new ChapterStoryModel[size];
    }
  };

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(id);
    dest.writeString(title);
    dest.writeString(racecourse);
    dest.writeString(description);
    dest.writeString(video);
  }
}
