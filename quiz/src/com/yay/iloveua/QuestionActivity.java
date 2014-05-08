package com.yay.iloveua;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.yay.iloveua.core.NotifyingAsyncQueryHandler;
import com.yay.iloveua.core.NotifyingAsyncQueryHandler.AsyncQueryListener;
import com.yay.iloveua.core.PreferencesManager;
import com.yay.iloveua.core.ServiceLocator;
import com.yay.iloveua.models.Question;
import com.yay.iloveua.provider.QuestionQuery;
import com.yay.iloveua.util.UIUtils;


public class QuestionActivity extends SherlockActivity implements AsyncQueryListener, OnClickListener, OnCheckedChangeListener {
	private int idp;
	private NotifyingAsyncQueryHandler mHandler;
	public static CursorAdapter mAdapter;
	private CustomProgressDialog progressDialog;
	private List<Question> questions;
	private int position = -1;
	private int[] answers; // values: 2 - expected & checked, 0 - unexpected & unchecked, 1 - unexpected & checked (incorrect)
	private Count cdt;
	private TextView mTimer;
	private int timerDuration;
	private Button proceed;
	private String separator;
	private int score;
	private String wrongAnswers = "";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_screen);
		if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            UIUtils.setTitle(this);
        }

		idp = getIntent().getIntExtra(Config.CATEGORY, 1);
		
		proceed  = (Button)findViewById(R.id.footer_proceed);
        proceed.setEnabled(false);
        proceed.setOnClickListener(this);
        
        score = 0;
        updateProgress();

        timerDuration = ServiceLocator.getInstance(this).getService(PreferencesManager.class).getInt(PreferencesManager.TIMER_DURATION, 60*1000*30);
        mTimer = (TextView) findViewById(R.id.timer);
        
        mHandler = new NotifyingAsyncQueryHandler(getContentResolver(), this);
            
    	idp = getIntent().getIntExtra(Config.CATEGORY, 1);
		mHandler.startQuery(QuestionQuery._TOKEN, Question.buildQuestionsUri("" + idp), QuestionQuery.PROJECTION, Question.DEFAULT_SORT);
        progressDialog = CustomProgressDialog.show(QuestionActivity.this, null, null);
	}

	@Override
	public void onClick(View view) {
		// Proceed button should be enabled only when at least one item is selected 
		boolean atLeastOneSelected = false;
		for(int answer : answers) {
			if(answer > 0) {
				atLeastOneSelected = true;
				break;
			}
		}
		if(!atLeastOneSelected)
	        return;
	
		if(view instanceof Button) {
			processAnswer();
			if(position+1 >= getQuestions().size()) {
				gameOver();
			} else {
				next();
				updateProgress();
			}
		}
	}
	
	@Override
	public void onQueryComplete(int token, Object cookie, Cursor cursor) {
		if(QuestionQuery._TOKEN == token && cursor != null && cursor.getCount() > 0) {
			getQuestions().clear();
 	    	while(cursor.moveToNext()) {
 	    		getQuestions().add(Question.buildFromCursor(cursor));
 	    	}
 	    	Collections.shuffle(getQuestions());
 	    	
 	    	if(getQuestions().size() > 30) {
 	    		questions = getQuestions().subList(0,  30);
 	    	}
 	    	
 	    	position = -1;
 	    	next();
 	    	
 	    	updateProgress();
 	    	 
 	        try {
 	        	if(progressDialog != null) progressDialog.dismiss();
 	        } catch(Exception e) {
 	        	Log.e(Config.LOG_TAG, e.toString());
 	        }
    	} else {
			if(mHandler != null) {
				mHandler.startQuery(QuestionQuery._TOKEN, Question.buildQuestionsUri("" + idp), QuestionQuery.PROJECTION, Question.DEFAULT_SORT);
			}
    	}
    	if(cursor != null) cursor.close();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked) {
			answers[(Integer)buttonView.getTag()]++;
		} else {
			answers[(Integer)buttonView.getTag()]--;
		}
		boolean atLeastOneSelected = false;
		for(int answer : answers) {
			if(answer > 0) {
				atLeastOneSelected = true;
				proceed.setEnabled(true);
				break;
			}
		}
		if(!atLeastOneSelected)
			proceed.setEnabled(false);
 	}
	
	private void next() {
		answers = new int[ServiceLocator.getInstance(this).getService(PreferencesManager.class).getInt(PreferencesManager.MAX_QUESTIONS, 10)];
		if(cdt == null) {
	     	cdt = new Count(timerDuration, 100);
			cdt.start();
		}
		Question question = getQuestions().get(++position);   
		
		proceed.setEnabled(false);
		
		TextView edtQuestion = (TextView) findViewById(R.id.question);
		edtQuestion.setText(question.getQuestion());
		
		ImageView image = (ImageView) findViewById(R.id.image);
		UIUtils.fetchDrawableOnThread(question.getImage(), image);
		
		String[] solutions = question.getSolutions().split(getSeparator());
		LinearLayout vSolutions = (LinearLayout) findViewById(R.id.solutions);
		vSolutions.removeAllViews();
		for(int i=0,j=solutions.length; i<j; i++) {
			String solution = solutions[i];
			LinearLayout vItem = new LinearLayout(this);
			vItem.setPadding(0, 5, 0, 5);
			vItem.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			vItem.setOrientation(LinearLayout.HORIZONTAL);
			
			CheckBox cb = new CheckBox(this);
			cb.setTag(i);
			cb.setOnCheckedChangeListener(this);
			cb.setText(solution);
			cb.setTextColor(Color.BLACK);
			vItem.addView(cb);
			
			vSolutions.addView(vItem);
		}
	}
	
	private void processAnswer() {
		proceed.setEnabled(true);
		
		Question question = getQuestions().get(position); 
		String[] solutions = question.getSolutions().split(getSeparator()); 
		String[] correctSolutions = question.getAnswer().split(",");
		StringBuilder sbCorrectSolutions = new StringBuilder(16);
		
		// Check user answers.
		for(int i=0,j=correctSolutions.length; i<j; i++) {
			int expected = Integer.parseInt(correctSolutions[i]);
			answers[expected]++;
			sbCorrectSolutions.append(solutions[expected]).append(";"); // Correct solutions to be displayed in case of incorrect answer
		}
		if(sbCorrectSolutions.length() > 0) {
			sbCorrectSolutions.deleteCharAt(sbCorrectSolutions.length()-1);
		}
		boolean correct = true;
		for(int i=0,j=answers.length; i<j; i++) {
			if(answers[i] != 0 && answers[i] != 2) { // either checked and unexpected or expected and unchecked 
				correct = false;
				break;
			}
		}
		
		if(!correct) {
			Toast.makeText(this, getResources().getString(R.string.wrong), Toast.LENGTH_SHORT).show();
			wrongAnswers += question.getId() + ";";
		} else {
			// increase points
			score++;
		}
	}
	
	private void updateProgress() {
		 TextView edtScore = (TextView) findViewById(R.id.score);
	     edtScore.setText((position+1) + "/" + getQuestions().size());
	}
	
	private void gameOver() {
		int currentScore = score * 100 / getQuestions().size();
	
		Intent intent = new Intent(QuestionActivity.this, ResultsActivity.class);
        intent.putExtra(Config.CATEGORY, idp);
        intent.putExtra(Config.SCORE, currentScore);
        intent.putExtra(Config.WRONG_ANSWERS, wrongAnswers);
        startActivity(intent);
        
		finish();
	}
	
	@Override
    protected void onDestroy() {
    	super.onDestroy();
    	if(mAdapter != null) {
        	try { mAdapter.changeCursor(null); } catch(Exception e) {}
        	mAdapter = null;
        }
    	if(cdt != null) {
    		cdt.cancel();
    		cdt = null;
    	}
        mHandler = null;
    }
	
	@Override
    protected void onPause() {
		super.onPause();
		if(cdt != null) {
    		cdt.cancel();
    		cdt = null;
    	}
	}

	private List<Question> getQuestions() {
		if(questions == null) {
			questions = new ArrayList<Question>();
		}
		return questions;
	}
	
	
	class Count extends CountDownTimer {
		public Count(long millisInFuture, long countDownInterval) {
	      super(millisInFuture, countDownInterval);
	    }
	
		public void onFinish() {
	    	try {
		    	processAnswer();
	    	} catch(Exception e) {
	    		Log.e(Config.LOG_TAG, e.toString());
	    	}
	    }
	
	    public void onTick(long millisUntilFinished) {
	    	try {
	            mTimer.setText(formatTime(millisUntilFinished));
	    	} catch(Exception e) {
	    		cancel();
	    	}
	    }
	}
	
	public static String formatTime(long millis) {
    	String output = "00:00";
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;
        String secondsD = String.valueOf(seconds);
        String minutesD = String.valueOf(minutes);

        if (seconds < 10)
        	secondsD = "0" + seconds;
        if (minutes < 10)
        	minutesD = "0" + minutes;
        output = minutesD + ":" + secondsD;
        return output;
    }

	
	private String getSeparator() {
		if(separator == null) {
			 separator = getResources().getString(R.string.separator);
		}
		return separator;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case android.R.id.home:
		        finish();
		        return true;
		    default:
		    	return super.onOptionsItemSelected(item);
	    }
	}
}
