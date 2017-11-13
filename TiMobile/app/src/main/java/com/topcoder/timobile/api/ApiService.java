package com.topcoder.timobile.api;

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
import io.reactivex.Observable;
import java.util.List;

/**
 * Api definition for Retrofit. Once the API end points are available, the methods can be
 * annotated as per Retrofit.
 */
public interface ApiService {
  Observable<String> login(LoginRequest user);

  Observable<User> getUser();

  Observable<List<PreStorySampleModel>> stateList();

  Observable<List<PreStorySampleModel>> raceTrackList();

  Observable<List<StoryModel>> getStoryList();

  Observable<BrowseStoryModel> browseStory();

  Observable<List<FAQSectionModel>> getFAQs();

  Observable<List<StoryModel>> getBookmarks();

  Observable<List<TiMobileModel>> getTiPoints();

  Observable<List<TiMobileModel>> getDailyTasks();

  Observable<List<RewardShopModel>> getRewardShop();

  Observable<List<BadgeModel>> getBadge();

  Observable<List<TradingCardModel>> getTradingCard();

  Observable<AchievementModel> getAchievements();

  Observable<List<CommentModel>> getComments();

  Observable<List<ChapterStoryModel>> getChapterStories();
}
