package com.topcoder.timobile.utility;


/**
 * define app constants values
 */
public interface AppConstants {

  String Show_Welcome = "show_welcome";
  String KEY_OBJ = "key_obj";
  String KEY_BOOl = "key_bool";

  /**
   * the search racetracks distance in story page
   */
  int SEARCH_RACETRACKS_DISTANCE_IN_M = 1000 * 1000; // 1000 KM

  /**
   * is need show pre story fragment if this value not true
   */
  String DONT_SHOW_PRE_STORY_FRAGMENT = "DONT_SHOW_PRE_STORY_FRAGMENT";

  /**
   * the key that store token expire time in sharedPreferences
   */
  String TOKEN_EXPIRES_KEY = "TOKEN_EXPIRES_KEY";

  /**
   * the ket that store the token in sharedPreferences
   */
  String TOKEN_KEY = "TOKEN_KEY";

  /**
   * every 5 seconds to fetch current location
   */
  long LOCATION_REFRESH_TIME = 5000;

  /**
   * refresh location if distance > 200
   */
  float LOCATION_REFRESH_DISTANCE = 200;

  /**
   * the page default size
   */
  int DEFAULT_LIMIT = 10;

  /**
   * the story description max length
   */
  int MAX_STORY_DESCRIPTION_LEN = 200;

  /**
   * update chapter progress every  3 seconds
   */
  long UPDATE_PROGRESS_INTERVAL = 3000;

  /**
   * newest progress return to story
   */
  int CHAPTER_PROGRESS_RETURNED_CODE = 1001;

  /**
   * the aws key
   */
  String AWS_KEY = "AKIAJKNEAH6KRIIW2MJQ";

  /**
   * the aws secret
   */
  String AWS_SECRET = "TiBlt8JzXyfDJ4OcMgVavQLY0iGjyJrDao/WRj4c";
  /**
   * the aws s3 bucket name
   */
  String BUCKET_NAME = "tc-ti-mobile";


  /**
   * the aws s3 region name
   */
  String AWS_S3_REGION = "us-east-2";
}
