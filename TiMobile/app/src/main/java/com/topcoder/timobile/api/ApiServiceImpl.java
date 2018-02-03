package com.topcoder.timobile.api;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.timobileapp.R;
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
import com.topcoder.timobile.utility.AppUtils;

import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * A implementation of ApiService that provides data from backend server.
 */
public class ApiServiceImpl implements ApiService {

  /**
   * current cached user
   */
  private User currentUser = null;

  /**
   * the cached trackStory page result
   */
  private PageResult<TrackStory> trackStoryPageResult = null;

  /**
   * the cached bookmark list
   */
  private List<Bookmark> bookmarkList = null;

  /**
   * the http api call instance
   */
  private ApiService apiService;

  public ApiServiceImpl(ApiService apiService) {
    this.apiService = apiService;
  }

  /**
   * insert http request to main thread
   *
   * @param observable the request
   * @param <T>        the result type
   */
  private <T> Observable<T> schedule(Observable<T> observable) {
    return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
  }

  /**
   * sign in app
   *
   * @param loginRequest the login request
   * @return the token entity
   */
  @Override public Observable<AuthToken> signin(LoginRequest loginRequest) {
    return schedule(apiService.signin(loginRequest));
  }

  /**
   * get current user ,it will return cache first if cached
   *
   * @return the user entity
   */
  @Override public User getCurrentUser() {
    if (currentUser == null) {
      throw new IllegalArgumentException("current user should not null");
    } else {
      return currentUser;
    }
  }


  /**
   * get current user and cache it
   *
   * @return the user
   */
  @Override public Observable<User> getUser() {
    return schedule(apiService.getUser().doOnNext(user -> currentUser = user));
  }

  /**
   * Get states from backend.
   *
   * @param offset the offset.
   * @param limit  the limit.
   * @return the states with page information.
   */
  @Override public Observable<PageResult<PreStorySampleModel>> getStates(int offset, int limit) {
    return schedule(apiService.getStates(offset, limit));
  }

  /**
   * Get racetracks.
   *
   * @param name                    the racetracks value
   * @param stateIds                the state ids
   * @param distanceToLocationMiles the distance
   * @param locationLat             the latitude
   * @param locationLng             the longitude
   * @param offset                  the offset
   * @param limit                   the limit
   * @param sortColumn              the sort name
   * @param sortOrder               the sort order
   * @return the racetracks with page information
   */
  @Override public Observable<PageResult<PreStorySampleModel>> getRacetracks(
      String name, String stateIds, Integer distanceToLocationMiles,
      Float locationLat, Float locationLng, int offset, int limit, String sortColumn, String sortOrder) {
    return schedule(apiService.getRacetracks(name, stateIds, distanceToLocationMiles,
        locationLat, locationLng, offset, limit, sortColumn, sortOrder));
  }

  /**
   * search stories
   *
   * @param title       the title
   * @param racetrackId the racetrack id
   * @param tagIds      the tags ids
   * @param offset      the offset
   * @param limit       the limit
   * @param locationLat the locationLat
   * @param locationLng the locationLng
   * @param sortColumn  the sort name
   * @param sortOrder   the sort order
   * @return the stories with page information
   */
  @Override public Observable<PageResult<TrackStory>> getTrackStories(
      String title, Long racetrackId, String racetrackIds, String tagIds,
      int offset, int limit, Float locationLat, Float locationLng, String sortColumn, String sortOrder) {
    return schedule(apiService.getTrackStories(title, racetrackId, racetrackIds,
        tagIds, offset, limit, locationLat, locationLng, sortColumn, sortOrder))
        .doOnNext(trackStoryPageResult -> this.trackStoryPageResult = trackStoryPageResult);
  }

  /**
   * get TrackStory by id , search in cache first.
   *
   * @param id the story id
   * @return the story
   */
  @Override public Observable<TrackStory> getTrackStoryById(Long id) {
    TrackStory trackStory = null;
    if (trackStoryPageResult != null
        && trackStoryPageResult.getItems() != null
        && trackStoryPageResult.getItems().size() > 0) {
      for (TrackStory story : trackStoryPageResult.getItems()) {
        if (story.getId().equals(id)) {
          trackStory = story;
          break;
        }
      }
    }
    if (trackStory != null) {
      return schedule(Observable.just(trackStory));
    } else {
      return schedule(apiService.getTrackStoryById(id)).doOnNext(story -> {
        if (trackStoryPageResult == null) return;
        trackStoryPageResult.setTotal(trackStoryPageResult.getTotal() + 1);
        trackStoryPageResult.getItems().add(story);
      });
    }
  }

  /**
   * get current user racetrack Bookmarks
   *
   * @return the bookmarks
   */
  @Override public Observable<List<Bookmark>> getRacetrackBookmarks() {
    if (bookmarkList != null) {
      return Observable.just(bookmarkList);
    } else {
      return schedule(apiService.getRacetrackBookmarks())
          .doOnNext(bookmarkList -> this.bookmarkList = bookmarkList);
    }
  }

  /**
   * create bookmark
   *
   * @param id the Racetrack id
   * @return the bookmark
   */
  @Override public Observable<Bookmark> createBookmark(Long id) {
    return schedule(apiService.createBookmark(id)).doOnNext(bookmark -> {
      if (this.bookmarkList != null) {  // add it to cache
        this.bookmarkList.add(bookmark);
      }
    });
  }

  /**
   * delete racetrack Bookmarks
   *
   * @param id the racetrack id
   * @return void
   */
  @Override public Observable<EmptyObject> removeBookmark(Long id) {
    return schedule(apiService.removeBookmark(id)).doOnNext(voidResponse -> {
      if (this.bookmarkList != null) {  // remove it from cache
        for (Iterator<Bookmark> it = this.bookmarkList.listIterator(); it.hasNext(); ) {
          Bookmark bookmark = it.next();
          if (bookmark.getRacetrack() != null &&
              bookmark.getRacetrack().getId().equals(id)) {
            it.remove();
          }
        }
      }
    });
  }

  /**
   * get story progress.
   *
   * @param id the story id.
   * @return the story
   */
  @Override public Observable<StoryProgress> getStoryProgressById(Long id) {
    return schedule(apiService.getStoryProgressById(id));
  }

  /**
   * update story progress
   *
   * @param storyId       the story id
   * @param storyProgress the progress entity
   * @return the updated storyProgress
   */
  @Override public Observable<StoryProgress> updateProgress(Long storyId, StoryProgress storyProgress) {

    StoryProgress progress = new StoryProgress();
    progress.setChaptersUserProgress(storyProgress.getChaptersUserProgress());
    progress.setTrackStoryId(storyProgress.getTrackStoryId());
    progress.setUserId(storyProgress.getUserId());

    return schedule(apiService.updateProgress(storyId, progress));
  }

  /**
   * complete a story
   *
   * @param storyId the story id
   * @return the updated storyProgress
   */
  @Override public Observable<StoryProgress> completedStory(Long storyId) {
    return schedule(apiService.completedStory(storyId));
  }

  /**
   * receive a story rewards
   *
   * @param storyId the story id
   * @return the updated storyProgress
   */
  @Override public Observable<StoryReward> receiveRewards(Long storyId) {
    return schedule(apiService.receiveRewards(storyId));
  }

  /**
   * complete an additional task
   *
   * @param storyId the story id
   * @return the updated storyProgress
   */
  @Override public Observable<EmptyObject> completeAdditionalTask(Long storyId) {
    return schedule(apiService.completeAdditionalTask(storyId));
  }

  /**
   * search comments
   *
   * @param userId       the user id
   * @param chapterId    the chapter id
   * @param trackStoryId the story id
   * @param types        the comment types
   * @param offset       the offset
   * @param limit        the limit
   * @param sortColumn   the sort name
   * @param sortOrder    the sort order
   * @return the comments
   */
  @Override public Observable<PageResult<Comment>> searchComments(
      Long userId, Long chapterId, Long trackStoryId, String types,
      int offset, int limit, String sortColumn, String sortOrder) {
    return schedule(apiService.searchComments(
        userId, chapterId, trackStoryId, types, offset, limit, sortColumn, sortOrder));
  }

  /**
   * create comment
   *
   * @param comment the comment entity
   * @return the created comment
   */
  @Override public Observable<Comment> createComments(Comment comment) {
    return schedule(apiService.createComments(comment));
  }

  /**
   * get current user daily tasks
   *
   * @return the tasks
   */
  @Override public Observable<List<UserDailyTask>> getCurrentUserDailyTasks() {
    return schedule(apiService.getCurrentUserDailyTasks());
  }

  /**
   * complete an daily task
   *
   * @param userTaskId the user daily task id
   * @return the dailyTask
   */
  @Override public Observable<UserDailyTask> completedDailyTask(Long userTaskId) {
    return schedule(apiService.completedDailyTask(userTaskId))
        .doOnNext(userDailyTask -> currentUser
            .setPointsAmount(currentUser.getPointsAmount() + userDailyTask.getDailyTask().getPoints()));
  }

  /**
   * search cards
   *
   * @param name         the card name
   * @param trackStoryId the story id
   * @param types        the card types
   * @param offset       the offset
   * @param limit        the limit
   * @param sortColumn   the sort name
   * @param sortOrder    the sort order
   * @return the card with page information
   */
  @Override public Observable<PageResult<Card>> searchCards(
      String name, Long trackStoryId, String types, int offset, int limit, String sortColumn, String sortOrder) {
    return schedule(apiService.searchCards(name, trackStoryId, types, offset, limit, sortColumn, sortOrder));
  }

  /**
   * buy card
   *
   * @param cardId the card id
   * @return the bought card
   */
  @Override public Observable<UserCard> buyCard(Long cardId) {
    return schedule(apiService.buyCard(cardId)).doOnNext(userCard ->
        currentUser.setPointsAmount(currentUser.getPointsAmount() - userCard.getCard().getPricePoints()));
  }

  /**
   * get current user all StoryProgress
   *
   * @return the list StoryProgress
   */
  @Override public Observable<List<StoryProgress>> getCurrentUserStoryProgresses() {
    return schedule(apiService.getCurrentUserStoryProgresses());
  }

  /**
   * get user comments count
   *
   * @param userId the user id
   */
  @Override public Observable<ItemCount> countComments(Long userId) {
    return schedule(apiService.countComments(userId));
  }

  /**
   * get current user cards
   *
   * @return the list of user cards
   */
  @Override public Observable<List<UserCard>> getCurrentUserCard() {
    return schedule(apiService.getCurrentUserCard());
  }

  /**
   * get current user badges
   *
   * @return the list of user badges
   */
  @Override public Observable<List<UserBadge>> getCurrentUserBadge() {
    return schedule(apiService.getCurrentUserBadge());
  }

  /**
   * get current user userPreferences
   *
   * @return
   */
  @Override public Observable<List<UserPreference>> getCurrentUserPreference() {
    return schedule(apiService.getCurrentUserPreference());
  }

  /**
   * update user Preference
   *
   * @param id the user Preference id
   * @return the update UserPreference
   */
  @Override public Observable<UserPreference> updateUserPreference(Long id, UserPreference preference) {
    UserPreference userPreference = new UserPreference();
    userPreference.setSelected(preference.getSelected());
    userPreference.setPreferenceOptionId(preference.getPreferenceOption().getId());
    return schedule(apiService.updateUserPreference(id, userPreference));
  }

  /**
   * update user
   *
   * @param id   the user id
   * @param user the user entity
   * @return the returned user
   */
  @Override public Observable<User> updateUser(Long id, User user) {
    return schedule(apiService.updateUser(id, user)).doOnNext(currentUser -> this.currentUser = currentUser);
  }

  /**
   * update password
   *
   * @param updatePassword the update password entity
   * @return return void
   */
  @Override public Observable<EmptyObject> updatePassword(UpdatePassword updatePassword) {
    return schedule(apiService.updatePassword(updatePassword));
  }

  /**
   * send an forgot password email to user
   *
   * @param email the email address
   * @return void
   */
  @Override public Observable<EmptyObject> initiateForgotPassword(String email) {
    return schedule(apiService.initiateForgotPassword(email));
  }

  /**
   * reset password use token
   *
   * @param changeForgotPassword the changeForgotPassword entity
   * @return void
   */
  @Override public Observable<EmptyObject> changeForgotPassword(ChangeForgotPassword changeForgotPassword) {
    return schedule(apiService.changeForgotPassword(changeForgotPassword));
  }

  /**
   * sign up
   *
   * @param user the user entity
   * @return
   */
  @Override public Observable<User> singup(User user) {
    return schedule(apiService.singup(user));
  }

  /**
   * logout app
   *
   * @return logout
   */
  @Override public Observable<EmptyObject> logout() {
    return schedule(apiService.logout()).doOnNext(voidResponse -> {
      currentUser = null;
      bookmarkList = null;
      trackStoryPageResult = null;
    });
  }

  /**
   * check for new achievements
   *
   * @return the user achievements
   */
  @Override public Observable<List<UserAchievement>> checkForNewAchievements() {
    return schedule(apiService.checkForNewAchievements());
  }

  /**
   * get current UserAchievements
   *
   * @return the list of UserAchievement
   */
  @Override public Observable<List<UserAchievement>> getCurrentUserAchievements() {
    return schedule(apiService.getCurrentUserAchievements());
  }

  /**
   * delete comments
   *
   * @param id the comment id
   * @return void
   */
  @Override public Observable<EmptyObject> deleteComment(Long id) {
    return schedule(apiService.deleteComment(id));
  }

  @Override
  public Observable<List<FAQSectionModel>> getFAQs() {
    String response = AppUtils.getJSONStringFromRawResource(R.raw.faq);
    List<FAQSectionModel> list = new Gson().fromJson(response, new TypeToken<List<FAQSectionModel>>() {
    }.getType());
    return schedule(Observable.just(list));
  }
}

