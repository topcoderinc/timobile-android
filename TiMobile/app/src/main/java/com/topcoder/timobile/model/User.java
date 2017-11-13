package com.topcoder.timobile.model;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Data;

@Data public class User implements Parcelable {

  String profileImage;
  String name;
  String email;
  String password;

  private void readIn(Parcel in) {
    profileImage = in.readString();
    name = in.readString();
    email = in.readString();
    password = in.readString();
  }

  public static final Creator<User> CREATOR = new Creator<User>() {
    @Override public User createFromParcel(Parcel in) {
      User user = new User();
      user.readIn(in);
      return user;
    }

    @Override public User[] newArray(int size) {
      return new User[size];
    }
  };

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(profileImage);
    dest.writeString(name);
    dest.writeString(email);
    dest.writeString(password);
  }
}