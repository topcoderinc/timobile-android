package com.topcoder.timobile.model;


import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

@Data
public class Chapter implements Parcelable {
  private Long id;
  private Long trackStoryId;
  private Integer number;
  private String title;
  private String subtitle;
  private String content;
  private String media;
  private Integer wordsCount;


  private transient Boolean isActive = false;
  private transient ChapterProgress progress;

  private void readIn(Parcel in) {
    id = in.readLong();
    number = in.readInt();
    title = in.readString();
    subtitle = in.readString();
    content = in.readString();
    media = in.readString();
  }

  public static final Parcelable.Creator<Chapter> CREATOR = new Parcelable.Creator<Chapter>() {
    @Override public Chapter createFromParcel(Parcel in) {
      Chapter model = new Chapter();
      model.readIn(in);
      return model;
    }

    @Override public Chapter[] newArray(int size) {
      return new Chapter[size];
    }
  };

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(id);
    dest.writeInt(number);
    dest.writeString(title);
    dest.writeString(subtitle);
    dest.writeString(content);
    dest.writeString(media);
  }
}
