package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.view.view_interface.StoryView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;


public class StoryPresenter extends PagedPresenter<Status> {

   private StatusService service;


   public StoryPresenter(StoryView view) {
      super(view, Cache.getInstance().getCurrUserAuthToken());
      this.service = new StatusService();
   }


   @Override
   void getItems(AuthToken authToken, User targetUser, int PAGE_SIZE, Status lastItem) {
      service.loadMoreStoryItems(Cache.getInstance().getCurrUserAuthToken(), targetUser, PAGE_SIZE, lastItem, getObserver());
   }

   @Override
   String getDescription() {
      return "story";
   }

   @Override
   protected StoryView getView() {
      return (StoryView) this.view;
   }


   public edu.byu.cs.tweeter.client.model.service.observer.PagedObserver<Status> getObserver() {
      return new PagedObserver();
   }


}