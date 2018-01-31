package com.topcoder.timobile.api;


import com.topcoder.timobile.model.AuthToken;
import com.topcoder.timobile.model.Bookmark;
import com.topcoder.timobile.model.Card;
import com.topcoder.timobile.model.Comment;
import com.topcoder.timobile.model.EmptyObject;
import com.topcoder.timobile.model.FAQSectionModel;
import com.topcoder.timobile.model.ItemCount;
import com.topcoder.timobile.model.PageResult;
import com.topcoder.timobile.model.PreStorySampleModel;
import com.topcoder.timobile.model.StoryProgress;
import com.topcoder.timobile.model.StoryReward;
import com.topcoder.timobile.model.TrackStory;
import com.topcoder.timobile.model.User;
import com.topcoder.timobile.model.UserAchievement;
import com.topcoder.timobile.model.UserBadge;
import com.topcoder.timobile.model.UserCard;
import com.topcoder.timobile.model.UserDailyTask;
import com.topcoder.timobile.model.UserPreference;
import com.topcoder.timobile.model.request.ChangeForgotPassword;
import com.topcoder.timobile.model.request.LoginRequest;
import com.topcoder.timobile.model.request.UpdatePassword;


import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

/**
 * Api definition for Retrofit. Once the API end points are available, the methods can be
 * annotated as per Retrofit.
 */
public interface ApiService {

  @POST("api/v1/login")
  Observable<AuthToken> signin(@Body LoginRequest loginRequest);

  User getCurrentUser();

  @GET("api/v1/currentUser")
  Observable<User> getUser();

  @GET("api/v1/lookup/states")
  Observable<PageResult<PreStorySampleModel>> getStates(@Query("offset") int offset, @Query("limit") int limit);


  @GET("api/v1/racetracks")
  Observable<PageResult<PreStorySampleModel>> getRacetracks(
      @Query("name") String name, @Query("stateIds") String stateIds,
      @Query("distanceToLocationMiles") Integer distanceToLocationMiles,
      @Query("locationLat") Float locationLat, @Query("locationLng") Float locationLng,
      @Query("offset") int offset, @Query("limit") int limit,
      @Query("sortColumn") String sortColumn, @Query("sortOrder") String sortOrder);

  @GET("api/v1/trackStories")
  Observable<PageResult<TrackStory>> getTrackStories(
      @Query("title") String title, @Query("racetrackId") Long racetrackId,
      @Query("racetrackIds") String racetrackIds, @Query("tagIds") String tagIds,
      @Query("offset") int offset, @Query("limit") int limit,
      @Query("sortColumn") String sortColumn, @Query("sortOrder") String sortOrder);

  @GET("api/v1/trackStories/{id}")
  Observable<TrackStory> getTrackStoryById(@Path("id") Long id);

  @GET("api/v1/currentUser/racetrackBookmarks")
  Observable<List<Bookmark>> getRacetrackBookmarks();

  @POST("api/v1/racetracks/{id}/bookmark")
  Observable<Bookmark> createBookmark(@Path("id") Long Id);

  @DELETE("api/v1/racetracks/{id}/bookmark")
  Observable<EmptyObject> removeBookmark(@Path("id") Long Id);

  @GET("api/v1/trackStories/{id}/userProgress")
  Observable<StoryProgress> getStoryProgressById(@Path("id") Long id);


  @PUT("api/v1/currentUser/trackStoryUserProgress/{id}")
  Observable<StoryProgress> updateProgress(@Path("id") Long storyId, @Body StoryProgress storyProgress);

  @PUT("api/v1/currentUser/trackStoryUserProgress/{id}/complete")
  Observable<StoryProgress> completedStory(@Path("id") Long storyId);

  @PUT("api/v1/currentUser/trackStoryUserProgress/{id}/receiveRewards")
  Observable<StoryReward> receiveRewards(@Path("id") Long storyId);

  @PUT("api/v1/currentUser/trackStoryUserProgress/{id}/completeAdditionalTask")
  Observable<EmptyObject> completeAdditionalTask(@Path("id") Long storyId);

  @GET("api/v1/comments")
  Observable<PageResult<Comment>> searchComments(
      @Query("userId") Long userId, @Query("chapterId") Long chapterId,
      @Query("trackStoryId") Long trackStoryId, @Query("types") String types,
      @Query("offset") int offset, @Query("limit") int limit,
      @Query("sortColumn") String sortColumn, @Query("sortOrder") String sortOrder);

  @POST("api/v1/comments")
  Observable<Comment> createComments(@Body Comment comment);


  @GET("api/v1/currentUser/userDailyTasks")
  Observable<List<UserDailyTask>> getCurrentUserDailyTasks();

  @PUT("api/v1/currentUser/userDailyTasks/{id}/complete")
  Observable<UserDailyTask> completedDailyTask(@Path("id") Long userTaskId);

  @GET("api/v1/cards")
  Observable<PageResult<Card>> searchCards(
      @Query("name") String name, @Query("trackStoryId") Long trackStoryId,
      @Query("types") String types, @Query("offset") int offset, @Query("limit") int limit,
      @Query("sortColumn") String sortColumn, @Query("sortOrder") String sortOrder
  );

  @POST("api/v1/cards/{id}/purchase")
  Observable<UserCard> buyCard(@Path("id") Long cardId);


  @GET("api/v1/currentUser/trackStoryUserProgress")
  Observable<List<StoryProgress>> getCurrentUserStoryProgresses();

  @GET("api/v1/count/comments")
  Observable<ItemCount> countComments(@Query("userId") Long userId);


  @GET("api/v1/currentUser/userCards")
  Observable<List<UserCard>> getCurrentUserCard();

  @GET("api/v1/currentUser/userBadges")
  Observable<List<UserBadge>> getCurrentUserBadge();


  @GET("api/v1/currentUser/userPreferenceOptions")
  Observable<List<UserPreference>> getCurrentUserPreference();

  @PUT("api/v1/currentUser/userPreferenceOptions/{id}")
  Observable<UserPreference> updateUserPreference(@Path("id") Long id, @Body UserPreference preference);


  @PUT("api/v1/users/{id}")
  Observable<User> updateUser(@Path("id") Long id, @Body User user);

  @PUT("api/v1/updatePassword")
  Observable<EmptyObject> updatePassword(@Body UpdatePassword updatePassword);

  @POST("api/v1/initiateForgotPassword")
  Observable<EmptyObject> initiateForgotPassword(@Query("email") String email);

  @POST("api/v1/changeForgotPassword")
  Observable<EmptyObject> changeForgotPassword(@Body ChangeForgotPassword changeForgotPassword);

  @POST("api/v1/signup")
  Observable<User> singup(@Body User user);

  @POST("api/v1/logout")
  Observable<EmptyObject> logout();

  @POST("api/v1/currentUser/achievements/checkForNew")
  Observable<List<UserAchievement>> checkForNewAchievements();

  @GET("api/v1/currentUser/achievements")
  Observable<List<UserAchievement>> getCurrentUserAchievements();

  @DELETE("api/v1/comments/{id}")
  Observable<EmptyObject> deleteComment(@Path("id") Long id);

  Observable<List<FAQSectionModel>> getFAQs();
}
