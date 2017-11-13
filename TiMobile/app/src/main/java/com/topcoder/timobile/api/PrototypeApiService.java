package com.topcoder.timobile.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseApplication;
import com.topcoder.timobile.model.AchievementModel;
import com.topcoder.timobile.model.BadgeModel;
import com.topcoder.timobile.model.BrowseStoryModel;
import com.topcoder.timobile.model.ChapterStoryModel;
import com.topcoder.timobile.model.CommentModel;
import com.topcoder.timobile.model.FAQSectionModel;
import com.topcoder.timobile.model.PreStorySampleModel;
import com.topcoder.timobile.model.RewardShopModel;
import com.topcoder.timobile.model.StoryModel;
import com.topcoder.timobile.model.TiMobileModel;
import com.topcoder.timobile.model.TradingCardModel;
import com.topcoder.timobile.model.User;
import com.topcoder.timobile.model.request.LoginRequest;
import com.topcoder.timobile.utility.AppUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import timber.log.Timber;

/**
 * A mock implementation of ApiService that provides data from local json files or manually created.
 */
public class PrototypeApiService implements ApiService {

  private <T> Observable<T> schedule(Observable<T> observable) {
    return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
  }

  @Override public Observable<String> login(LoginRequest req) {

    if (req.getEmail().equalsIgnoreCase(BaseApplication.getInstance().getString(R.string.mock_email)) && req.getPassword()
        .equalsIgnoreCase(BaseApplication.getInstance().getString(R.string.mock_pwd))) {
      // Test valid login
      String token = AppUtils.getJSONStringFromRawResource(R.raw.loginresponse);
      return schedule(Observable.just(token).map(user -> {
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          Timber.e(e, "interrupted");
          return user;
        }
        return user;
      }));
    }
    // Test invalid login
    ResponseBody body = ResponseBody.create(MediaType.parse("application/json"), "Invalid user");
    Response errorResponse = Response.error(403, body);
    return schedule(Observable.error(new HttpException(errorResponse)));
  }

  @Override public Observable<User> getUser() {
    User mockUser = new User();
    mockUser.setName("John Dillaine");
    mockUser.setEmail("john.dillaine@gmail.com");
    mockUser.setPassword("xxxxxx");
    mockUser.setProfileImage("https://randomuser.me/api/portraits/med/men/89.jpg");
    return schedule(Observable.just(mockUser));
  }

  @Override public Observable<List<PreStorySampleModel>> stateList() {
    String stateList = AppUtils.getJSONStringFromRawResource(R.raw.state);
    List<PreStorySampleModel> list = new Gson().fromJson(stateList, new TypeToken<List<PreStorySampleModel>>() {
    }.getType());
    return schedule(Observable.just(list));
  }

  @Override public Observable<List<PreStorySampleModel>> raceTrackList() {
    String trackList = AppUtils.getJSONStringFromRawResource(R.raw.racetrack);
    List<PreStorySampleModel> list = new Gson().fromJson(trackList, new TypeToken<List<PreStorySampleModel>>() {
    }.getType());
    return schedule(Observable.just(list));
  }

  @Override public Observable<List<StoryModel>> getStoryList() {
    String storyList = AppUtils.getJSONStringFromRawResource(R.raw.story_location);
    List<StoryModel> list = new Gson().fromJson(storyList, new TypeToken<List<StoryModel>>() {
    }.getType());
    return schedule(Observable.just(list));
  }

  @Override public Observable<BrowseStoryModel> browseStory() {
    String response = AppUtils.getJSONStringFromRawResource(R.raw.browse_story);
    BrowseStoryModel data = new Gson().fromJson(response, BrowseStoryModel.class);
    return schedule(Observable.just(data));
  }

  @Override public Observable<List<FAQSectionModel>> getFAQs() {
    String response = AppUtils.getJSONStringFromRawResource(R.raw.faq);
    List<FAQSectionModel> list = new Gson().fromJson(response, new TypeToken<List<FAQSectionModel>>() {
    }.getType());
    return schedule(Observable.just(list));
  }

  @Override public Observable<List<StoryModel>> getBookmarks() {
    String storyList = AppUtils.getJSONStringFromRawResource(R.raw.bookmark);
    List<StoryModel> list = new Gson().fromJson(storyList, new TypeToken<List<StoryModel>>() {
    }.getType());
    return schedule(Observable.just(list));
  }

  @Override public Observable<List<TiMobileModel>> getTiPoints() {
    String response = AppUtils.getJSONStringFromRawResource(R.raw.timobile);
    List<TiMobileModel> list = new Gson().fromJson(response, new TypeToken<List<TiMobileModel>>() {
    }.getType());
    return schedule(Observable.just(list));
  }

  @Override public Observable<List<TiMobileModel>> getDailyTasks() {
    String response = AppUtils.getJSONStringFromRawResource(R.raw.daily_task);
    List<TiMobileModel> list = new Gson().fromJson(response, new TypeToken<List<TiMobileModel>>() {
    }.getType());
    return schedule(Observable.just(list));
  }

  @Override public Observable<List<RewardShopModel>> getRewardShop() {
    String response = AppUtils.getJSONStringFromRawResource(R.raw.reward_shop);
    List<RewardShopModel> list = new Gson().fromJson(response, new TypeToken<List<RewardShopModel>>() {
    }.getType());
    return schedule(Observable.just(list));
  }

  @Override public Observable<List<BadgeModel>> getBadge() {
    String response = AppUtils.getJSONStringFromRawResource(R.raw.badge);
    List<BadgeModel> list = new Gson().fromJson(response, new TypeToken<List<BadgeModel>>() {
    }.getType());
    return schedule(Observable.just(list));
  }

  @Override public Observable<List<TradingCardModel>> getTradingCard() {
    String response = AppUtils.getJSONStringFromRawResource(R.raw.trading_card);
    List<TradingCardModel> list = new Gson().fromJson(response, new TypeToken<List<TradingCardModel>>() {
    }.getType());
    return schedule(Observable.just(list));
  }

  @Override public Observable<AchievementModel> getAchievements() {
    String response = AppUtils.getJSONStringFromRawResource(R.raw.profile_achievement);
    AchievementModel data = new Gson().fromJson(response, AchievementModel.class);
    return schedule(Observable.just(data));
  }

  @Override public Observable<List<CommentModel>> getComments() {
    String response = AppUtils.getJSONStringFromRawResource(R.raw.comment);
    List<CommentModel> list = new Gson().fromJson(response, new TypeToken<List<CommentModel>>() {
    }.getType());
    return schedule(Observable.just(list));
  }

  @Override public Observable<List<ChapterStoryModel>> getChapterStories() {
    String response = AppUtils.getJSONStringFromRawResource(R.raw.chapter_stories);
    List<ChapterStoryModel> list = new Gson().fromJson(response, new TypeToken<List<ChapterStoryModel>>() {
    }.getType());
    return schedule(Observable.just(list));
  }
}

