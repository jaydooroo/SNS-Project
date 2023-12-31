package edu.byu.cs.tweeter.client.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.presenter.FollowPresenter;
import edu.byu.cs.tweeter.client.presenter.GetFollowersCountPresenter;
import edu.byu.cs.tweeter.client.presenter.GetFollowingCountPresenter;
import edu.byu.cs.tweeter.client.presenter.IsFollowPresenter;
import edu.byu.cs.tweeter.client.presenter.LogoutPresenter;

import edu.byu.cs.tweeter.client.presenter.PostStatusPresenter;
import edu.byu.cs.tweeter.client.presenter.UnfollowPresenter;
import edu.byu.cs.tweeter.client.view.login.LoginActivity;
import edu.byu.cs.tweeter.client.view.login.StatusDialogFragment;
import edu.byu.cs.tweeter.client.view.view_interface.CountView;
import edu.byu.cs.tweeter.client.view.view_interface.FollowAndUnfollowView;
import edu.byu.cs.tweeter.client.view.view_interface.IsFollowerView;
import edu.byu.cs.tweeter.client.view.view_interface.LogoutView;
import edu.byu.cs.tweeter.client.view.view_interface.PostStatusView;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * The main activity for the application. Contains tabs for feed, story, following, and followers.
 */
public class MainActivity extends AppCompatActivity implements StatusDialogFragment.Observer, LogoutView, FollowAndUnfollowView, CountView, IsFollowerView, PostStatusView {

    private static final String LOG_TAG = "MainActivity";

    public static final String CURRENT_USER_KEY = "CurrentUser";

    private Toast logOutToast;
    private Toast postingToast;
    private User selectedUser;
    private TextView followeeCount;
    private TextView followerCount;
    private Button followButton;

    private IsFollowPresenter isFollowPresenter = new IsFollowPresenter(this);
    private PostStatusPresenter postStatusPresenter = new PostStatusPresenter(this);
    private GetFollowersCountPresenter getFollowersCountPresenter = new GetFollowersCountPresenter(this);
    private GetFollowingCountPresenter getFollowingCountPresenter = new GetFollowingCountPresenter(this);
    private UnfollowPresenter unfollowPresenter = new UnfollowPresenter(this, getFollowersCountPresenter.getObserver(),getFollowingCountPresenter.getObserver());
    private FollowPresenter followPresenter = new FollowPresenter(this, getFollowersCountPresenter.getObserver(),getFollowingCountPresenter.getObserver());
    private LogoutPresenter logoutPresenter = new LogoutPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedUser = (User) getIntent().getSerializableExtra(CURRENT_USER_KEY);
        if (selectedUser == null) {
            throw new RuntimeException("User not passed to activity");
        }

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), selectedUser);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StatusDialogFragment statusDialogFragment = new StatusDialogFragment();
                statusDialogFragment.show(getSupportFragmentManager(), "post-status-dialog");
            }
        });

        followPresenter.updateSelectedUserFollowingAndFollowers(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser);


        TextView userName = findViewById(R.id.userName);
        userName.setText(selectedUser.getName());

        TextView userAlias = findViewById(R.id.userAlias);
        userAlias.setText(selectedUser.getUser_alias());

        ImageView userImageView = findViewById(R.id.userImage);
        Picasso.get().load(selectedUser.getImageUrl()).into(userImageView);

        followeeCount = findViewById(R.id.followeeCount);
        followeeCount.setText(getString(R.string.followeeCount, "..."));

        followerCount = findViewById(R.id.followerCount);
        followerCount.setText(getString(R.string.followerCount, "..."));

        followButton = findViewById(R.id.followButton);

        if (selectedUser.compareTo(Cache.getInstance().getCurrUser()) == 0) {
            followButton.setVisibility(View.GONE);
        } else {
            followButton.setVisibility(View.VISIBLE);

            isFollowPresenter.initiateIsFollower(Cache.getInstance().getCurrUserAuthToken(),
                    Cache.getInstance().getCurrUser(), selectedUser);

        }

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followButton.setEnabled(false);

                if (followButton.getText().toString().equals(v.getContext().getString(R.string.following))) {


                    unfollowPresenter.initiateUnfollow(Cache.getInstance().getCurrUserAuthToken(),selectedUser);

                } else {

                    followPresenter.initiateFollow(Cache.getInstance().getCurrUserAuthToken(),
                            selectedUser);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutMenu) {

            logoutPresenter.initiateLogout(Cache.getInstance().getCurrUserAuthToken());
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void logoutUser() {
        //Revert to login screen.
        Intent intent = new Intent(this, LoginActivity.class);
        //Clear everything so that the main activity is recreated with the login page.
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Clear user data (cached data).
        Cache.getInstance().clearCache();
        startActivity(intent);
    }

    @Override
    public void displayLogoutMessage(String message) {
        logOutToast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
        logOutToast.show();
    }

    @Override
    public void clearLogoutMessage() {
        logOutToast.cancel();
    }

    @Override
    public void showFollowersCount(int count) {
        followerCount.setText(getString(R.string.followerCount, String.valueOf(count)));
    }

    @Override
    public void showFollowingCount(int count) {
        followeeCount.setText(getString(R.string.followeeCount, String.valueOf(count)));
    }

    @Override
    public void isFollower(boolean isFollower) {
//         If logged in user if a follower of the selected user, display the follow button as "following"

                if (isFollower) {
                    followButton.setText(R.string.following);
                    followButton.setBackgroundColor(getResources().getColor(R.color.white));
                    followButton.setTextColor(getResources().getColor(R.color.lightGray));
                } else {
                    followButton.setText(R.string.follow);
                    followButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }
    }

    @Override
    public void followSucceeded() {
        updateFollowButton(false);
        followButton.setEnabled(true);
    }

    @Override
    public void unfollowSucceeded() {
        updateFollowButton(true);
        followButton.setEnabled(true);
    }

    @Override
    public void displayFollowAndUnFollowMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        followButton.setEnabled(true);
    }

    @Override
    public void postStatusSucceeded() {
        postingToast.cancel();
    }
    @Override
    public void displayPostToast(String message) {
        postingToast =  Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
        postingToast.show();
    }

    @Override
    public void onStatusPosted(String post) {

        postStatusPresenter.initiatePostStatus(post,Cache.getInstance().getCurrUser(),Cache.getInstance().getCurrUserAuthToken(),LOG_TAG);

    }



    public void updateFollowButton(boolean removed) {
        // If follow relationship was removed.
        if (removed) {
            followButton.setText(R.string.follow);
            followButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            followButton.setText(R.string.following);
            followButton.setBackgroundColor(getResources().getColor(R.color.white));
            followButton.setTextColor(getResources().getColor(R.color.lightGray));
        }
    }


    @Override
    public void displayMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }
}
