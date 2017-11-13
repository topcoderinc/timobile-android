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

class MockApiService implements ApiService {

    @Override public Observable<String> login(LoginRequest user) {
        return null;
    }

    @Override public Observable<User> getUser() {
        return null;
    }

    @Override public Observable<List<PreStorySampleModel>> stateList() {
        return null;
    }

    @Override public Observable<List<PreStorySampleModel>> raceTrackList() {
        return null;
    }

    @Override public Observable<List<StoryModel>> getStoryList() {
        return null;
    }

    @Override public Observable<BrowseStoryModel> browseStory() {
        return null;
    }

    @Override public Observable<List<FAQSectionModel>> getFAQs() {
        return null;
    }

    @Override public Observable<List<StoryModel>> getBookmarks() {
        return null;
    }

    @Override public Observable<List<TiMobileModel>> getTiPoints() {
        return null;
    }

    @Override public Observable<List<TiMobileModel>> getDailyTasks() {
        return null;
    }

    @Override public Observable<List<RewardShopModel>> getRewardShop() {
        return null;
    }

    @Override public Observable<List<BadgeModel>> getBadge() {
        return null;
    }

    @Override public Observable<List<TradingCardModel>> getTradingCard() {
        return null;
    }

    @Override public Observable<AchievementModel> getAchievements() {
        return null;
    }

    @Override public Observable<List<CommentModel>> getComments() {
        return null;
    }

    @Override public Observable<List<ChapterStoryModel>> getChapterStories() {
        return null;
    }
}

