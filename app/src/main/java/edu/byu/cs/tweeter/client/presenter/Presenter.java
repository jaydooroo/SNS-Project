package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.view.view_interface.View;

public abstract class Presenter {

   //protected T view;
   View view;

   Presenter(View view) {
      this.view = view;
   }

}
