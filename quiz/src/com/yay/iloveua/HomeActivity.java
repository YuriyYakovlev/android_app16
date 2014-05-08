package com.yay.iloveua;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.google.android.gms.appstate.AppStateClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.plus.PlusClient;
import com.yay.iloveua.core.Categories;
import com.yay.iloveua.core.PreferencesManager;
import com.yay.iloveua.core.ServiceLocator;
import com.yay.iloveua.util.UIUtils;


public class HomeActivity extends SherlockActivity implements OnClickListener, GameHelper.GameHelperListener  {
    // The game helper object. This class is mainly a wrapper around this object.
    protected GameHelper mHelper;

    // We expose these constants here because we don't want users of this class
    // to have to know about GameHelper at all.
    public static final int CLIENT_GAMES = GameHelper.CLIENT_GAMES;
    public static final int CLIENT_APPSTATE = GameHelper.CLIENT_APPSTATE;
    public static final int CLIENT_PLUS = GameHelper.CLIENT_PLUS;
    public static final int CLIENT_ALL = GameHelper.CLIENT_ALL;

    // Requested clients. By default, that's just the games client.
    protected int mRequestedClients = CLIENT_GAMES;

    // request codes we use when invoking an external activity
    final int RC_RESOLVE = 5000, RC_UNUSED = 5001;

    // achievements and scores we're pending to push to the cloud
    // (waiting for the user to sign in, for instance)
    AccomplishmentsOutbox mOutbox = new AccomplishmentsOutbox();

    private int idp;
	private int score;

    private State state = State.DEFAULT;
    enum State {
        DEFAULT, ACHIEVEMENTS, LEADERBOARDS;
    }

    /** Constructs a BaseGameActivity with default client (GamesClient). */
    public HomeActivity() {
        super();
        mHelper = new GameHelper(this);
    }

    /**
     * Constructs a BaseGameActivity with the requested clients.
     * @param requestedClients The requested clients (a combination of CLIENT_GAMES,
     *         CLIENT_PLUS and CLIENT_APPSTATE).
     */
    public HomeActivity(int requestedClients) {
        super();
        setRequestedClients(requestedClients);
    }

    /**
     * Sets the requested clients. The preferred way to set the requested clients is
     * via the constructor, but this method is available if for some reason your code
     * cannot do this in the constructor. This must be called before onCreate in order to
     * have any effect. If called after onCreate, this method is a no-op.
     *
     * @param requestedClients A combination of the flags CLIENT_GAMES, CLIENT_PLUS
     *         and CLIENT_APPSTATE, or CLIENT_ALL to request all available clients.
     */
    protected void setRequestedClients(int requestedClients) {
        mRequestedClients = requestedClients;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SubMenu subMenu = menu.addSubMenu("Action Item");
        /*for(Categories category: Categories.values()) {
        	subMenu.add(0, category.getId(), Menu.NONE, category.getName());
        }*/
        subMenu.add(0, 1, Menu.NONE, getResources().getString(R.string.achievement));
        subMenu.add(0, 2, Menu.NONE, getResources().getString(R.string.leaderboards));

        MenuItem subMenuItem = subMenu.getItem();
        subMenuItem.setIcon(R.drawable.ic_menu_more_selected);
        subMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        score = ServiceLocator.getInstance(this).getService(PreferencesManager.class).getInt(Config.SCORE + idp, 0);
        TextView txtPrompt = (TextView) findViewById(R.id.txtPrompt); 
		txtPrompt.setTypeface(UIUtils.getTypefaceDefault());
		
		score = ServiceLocator.getInstance(this).getService(PreferencesManager.class).getInt(Config.SCORE + idp, 0);

        final SpannableStringBuilder sb = new SpannableStringBuilder(String.format(getResources().getString(R.string
                .interview_prompt), score));
        final ForegroundColorSpan fcs1 = new ForegroundColorSpan(Color.rgb(71, 168, 214)); // Span to set text color to some RGB value
        final ForegroundColorSpan fcs2 = new ForegroundColorSpan(Color.rgb(71, 168, 214)); // Span to set text color to some RGB value
        final ForegroundColorSpan ocs = new ForegroundColorSpan(Color.rgb(255, 102, 0)); // Span to set text color to some RGB value
        sb.setSpan(fcs1, 16, 18, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // Set the text color for first 4 characters
        sb.setSpan(fcs2, 107, 110, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // Set the text color for first 4 characters
        sb.setSpan(ocs, 149, 152, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // Set the text color for first 4 characters
        txtPrompt.setText(sb);

        // check for achievements
        checkForAchievements(score);

        // update leaderboards
        updateLeaderboards(score);

        // push those accomplishments to the cloud, if signed in
        pushAccomplishments();
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mHelper = new GameHelper(this);
        mHelper.setup(this, mRequestedClients);

		setContentView(R.layout.home_screen);
		UIUtils.setTitle(this);
		
		idp = getIntent().getIntExtra(Config.CATEGORY, 1);
		
		TextView txtPrompt = (TextView) findViewById(R.id.txtPrompt); 
		txtPrompt.setTypeface(UIUtils.getTypefaceDefault());
		
		score = ServiceLocator.getInstance(this).getService(PreferencesManager.class).getInt(Config.SCORE + idp, 0);

        final SpannableStringBuilder sb = new SpannableStringBuilder(String.format(getResources().getString(R.string
                .interview_prompt), score));
        final ForegroundColorSpan fcs1 = new ForegroundColorSpan(Color.rgb(71, 168, 214)); // Span to set text color to some RGB value
        final ForegroundColorSpan fcs2 = new ForegroundColorSpan(Color.rgb(71, 168, 214)); // Span to set text color to some RGB value
        final ForegroundColorSpan ocs = new ForegroundColorSpan(Color.rgb(255, 102, 0)); // Span to set text color to some RGB value
        sb.setSpan(fcs1, 16, 18, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // Set the text color for first 4 characters
        sb.setSpan(fcs2, 107, 110, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // Set the text color for first 4 characters
        sb.setSpan(ocs, 149, 152, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // Set the text color for first 4 characters
        txtPrompt.setText(sb);

   		Button btnReady = (Button)findViewById(R.id.btnReady);
   		btnReady.setTypeface(UIUtils.getTypefaceDefault());
		btnReady.setOnClickListener(this);

        // load outbox from file
        mOutbox.loadLocal(this);
        setSignInMessages(getString(R.string.signing_in), "");

        // check for achievements
        //checkForAchievements(score);

        // update leaderboards
        //updateLeaderboards(score);

        // push those accomplishments to the cloud, if signed in
        //pushAccomplishments();
	}

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnReady:
                Intent intent = new Intent(HomeActivity.this, QuestionActivity.class);
                intent.putExtra(Config.CATEGORY, idp);
                startActivity(intent);
                break;
            /*case R.id.sign_in_button:
                onSignInButtonClicked();
                break;*/
        }
    }

    @Override
   	public boolean onOptionsItemSelected(MenuItem item) {
   		switch(item.getItemId()) {
            case 1:
                if (isSignedIn()) {
                    startActivityForResult(getGamesClient().getAchievementsIntent(), RC_UNUSED);
                } else {
                    state = State.ACHIEVEMENTS;
                    beginUserInitiatedSignIn();
                }
                break;
            case 2:
                if (isSignedIn()) {
                    startActivityForResult(getGamesClient().getAllLeaderboardsIntent(), RC_UNUSED);
                } else {
                    state = State.LEADERBOARDS;
                    beginUserInitiatedSignIn();
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

        /*Categories category = Categories.findById(item.getItemId());
   		if(category!=null) {
	   		Intent intent = new Intent(HomeActivity.this, WikiActivity.class);
			intent.putExtra(Config.CATEGORY, category.getId());
			startActivity(intent);
			return true;
   		} else {
   			return super.onOptionsItemSelected(item);
   		}*/
   	}

    @Override
    public void onSignInFailed() {
        // Sign-in failed, so show sign-in button on main menu
        //mMainMenuFragment.setGreeting(getString(R.string.signed_out_greeting));
        //findViewById(R.id.sign_in_bar).setVisibility(View.VISIBLE);
    }

    @Override
    public void onSignInSucceeded() {
        // Show sign-out button on main menu
        //findViewById(R.id.sign_in_bar).setVisibility(View.GONE);

        Player p = getGamesClient().getCurrentPlayer();
        String displayName;
        if (p != null) {
            TextView txtPrompt = (TextView) findViewById(R.id.txtPrompt);
            final StringBuilder sb = new StringBuilder(String.format(getResources().getString(R.string.greeting),
                    p.getDisplayName()));
            sb.append(String.format(getResources().getString(R.string.interview_prompt), score));
            txtPrompt.setText(sb);
        }

        // if we have accomplishments to push, push them
        if (!mOutbox.isEmpty()) {
            pushAccomplishments();
        }

        switch(state) {
            case ACHIEVEMENTS:
                startActivityForResult(getGamesClient().getAchievementsIntent(), RC_UNUSED);
                break;
            case LEADERBOARDS:
                startActivityForResult(getGamesClient().getAllLeaderboardsIntent(), RC_UNUSED);
                break;
        }
        state = State.DEFAULT;
    }

    /*public void onSignInButtonClicked() {
        // start the sign-in flow
        beginUserInitiatedSignIn();
    }*/

    /**
     * Check for achievements and unlock the appropriate ones.
     *
     * @param score the score the user requested.
     */
    void checkForAchievements(int score) {
        // Check if each condition is met; if so, unlock the corresponding
        // achievement.
        if (score > 0 && score <= 20) {
            mOutbox.mKazapAchievement = true;
            achievementToast(getString(R.string.achievement_kazap_toast_text));
            return;
        }
        if (score > 0 && score <= 40) {
            mOutbox.mMoscalAchievement = true;
            achievementToast(getString(R.string.achievement_moscal_toast_text));
            return;
        }
        if (score > 0 && score <= 60) {
            mOutbox.mHoholAchievement = true;
            achievementToast(getString(R.string.achievement_hohol_toast_text));
            return;
        }
        if (score > 0 && score <= 80) {
            mOutbox.mBanderivecAchievement = true;
            achievementToast(getString(R.string.achievement_banderivec_toast_text));
            return;
        }
        if (score > 0 && score <= 90) {
            mOutbox.mPatriotAchievement = true;
            achievementToast(getString(R.string.achievement_patriot_toast_text));
            return;
        }
    }

    void unlockAchievement(int achievementId, String fallbackString) {
        if (isSignedIn()) {
            getGamesClient().unlockAchievement(getString(achievementId));
        } else {
            Toast.makeText(this, fallbackString,
                    Toast.LENGTH_LONG).show();
        }
    }

    void achievementToast(String achievement) {
        // Only show toast if not signed in. If signed in, the standard Google Play
        // toasts will appear, so we don't need to show our own.
        if (!isSignedIn()) {
            Toast.makeText(this, achievement, Toast.LENGTH_LONG).show();
        }
    }

    void pushAccomplishments() {
        if (!isSignedIn()) {
            // can't push to the cloud, so save locally
            mOutbox.saveLocal(this);
            return;
        }
        if (mOutbox.mKazapAchievement) {
            getGamesClient().unlockAchievement(getString(R.string.achievement_kazap));
            mOutbox.mKazapAchievement = false;
        }
        if (mOutbox.mMoscalAchievement) {
            getGamesClient().unlockAchievement(getString(R.string.achievement_moscal));
            mOutbox.mMoscalAchievement = false;
        }
        if (mOutbox.mHoholAchievement) {
            getGamesClient().unlockAchievement(getString(R.string.achievement_hohol));
            mOutbox.mHoholAchievement = false;
        }
        if (mOutbox.mBanderivecAchievement) {
            getGamesClient().unlockAchievement(getString(R.string.achievement_banderivec));
            mOutbox.mBanderivecAchievement = false;
        }

        if (mOutbox.mPatriotAchievement) {
            getGamesClient().unlockAchievement(getString(R.string.achievement_patriot));
            mOutbox.mPatriotAchievement = false;

            getGamesClient().submitScore(getString(R.string.leaderboard_patriot), score);
        }
        mOutbox.saveLocal(this);
    }

    /**
     * Update leaderboards with the user's score.
     *
     * @param finalScore The score the user got.
     */
    void updateLeaderboards(int finalScore) {
       /* if (mOutbox.mEasyModeScore < finalScore) {
            mOutbox.mEasyModeScore = finalScore;
        }*/
    }

    class AccomplishmentsOutbox {
        boolean mKazapAchievement = false;
        boolean mMoscalAchievement = false;
        boolean mHoholAchievement = false;
        boolean mBanderivecAchievement = false;
        boolean mPatriotAchievement = false;
        //int mPatriotModeScore = -1;

        boolean isEmpty() {
            return !mKazapAchievement && !mMoscalAchievement && !mHoholAchievement &&
                    !mBanderivecAchievement && !mPatriotAchievement/* && mPatriotModeScore < 50*/;
        }

        public void saveLocal(Context ctx) {
            /* TODO: This is left as an exercise. To make it more difficult to cheat,
             * this data should be stored in an encrypted file! And remember not to
             * expose your encryption key (obfuscate it by building it from bits and
             * pieces and/or XORing with another string, for instance). */
        }

        public void loadLocal(Context ctx) {
            /* TODO: This is left as an exercise. Write code here that loads data
             * from the file you wrote in saveLocal(). */
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHelper.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHelper.onStop();
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        mHelper.onActivityResult(request, response, data);
    }

    protected GamesClient getGamesClient() {
        return mHelper.getGamesClient();
    }

    protected AppStateClient getAppStateClient() {
        return mHelper.getAppStateClient();
    }

    protected PlusClient getPlusClient() {
        return mHelper.getPlusClient();
    }

    protected boolean isSignedIn() {
        return mHelper.isSignedIn();
    }

    protected void beginUserInitiatedSignIn() {
        mHelper.beginUserInitiatedSignIn();
    }

    protected void signOut() {
        mHelper.signOut();
    }

    protected void showAlert(String title, String message) {
        mHelper.showAlert(title, message);
    }

    protected void showAlert(String message) {
        mHelper.showAlert(message);
    }

    protected void enableDebugLog(boolean enabled, String tag) {
        mHelper.enableDebugLog(enabled, tag);
    }

    protected String getInvitationId() {
        return mHelper.getInvitationId();
    }

    protected void reconnectClients(int whichClients) {
        mHelper.reconnectClients(whichClients);
    }

    protected String getScopes() {
        return mHelper.getScopes();
    }

    protected boolean hasSignInError() {
        return mHelper.hasSignInError();
    }

    protected ConnectionResult getSignInError() {
        return mHelper.getSignInError();
    }

    protected void setSignInMessages(String signingInMessage, String signingOutMessage) {
        mHelper.setSigningInMessage(signingInMessage);
        mHelper.setSigningOutMessage(signingOutMessage);
    }
}
