package com.yay.iloveua;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.yay.iloveua.core.Categories;
import com.yay.iloveua.core.PreferencesManager;
import com.yay.iloveua.core.ServiceLocator;
import com.yay.iloveua.util.UIUtils;


public class DashboardActivity extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard_screen);

        initCategories();
        
        TextView webLink = (TextView) findViewById(R.id.webLink);
        webLink.setTypeface(UIUtils.getTypefaceDefault());
        webLink.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse(Config.GPLUS_URL)));
			}
        });

	}

	private CategoriesAdapter adapter;

	private CategoriesAdapter getCategoriesAdapter() {
		if(adapter == null) {
			adapter = new CategoriesAdapter();
		}
		return adapter;
	}
	
	private void initCategories() {
    	GridView gridView = (GridView) findViewById(R.id.categories);
 		gridView.setAdapter(getCategoriesAdapter());
    }

    @Override
    public void onResume() {
    	super.onResume();
    	getCategoriesAdapter().notifyDataSetChanged();
    }
    
    class CategoriesAdapter extends BaseAdapter {
        public int getCount() {
            return Categories.values().length;
        }

        public Categories getItem(int position) {
            return Categories.values()[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
        	final Categories category = Categories.values()[position];

        	Button button = new Button(DashboardActivity.this);
        	Drawable img = DashboardActivity.this.getResources().getDrawable(category.getResId());
        	img.setBounds(0, 0, 150, 150);
        	button.setCompoundDrawables(null, img, null, null);
        	int score = ServiceLocator.getInstance(DashboardActivity.this).getService(PreferencesManager.class).getInt(Config.SCORE + category.getId(), 0);
        	if(category.getId() == 1) {
        		button.setText(category.getName() + " - " + score + "%");
        	} else {
        		button.setText(category.getName());
        	}
        	button.setTypeface(UIUtils.getTypefaceDefault());
        	//button.setBackgroundColor(getResources().getColor(R.color.orange));
        	//button.setBackgroundDrawable(DashboardActivity.this.getResources().getDrawable(R.drawable.button));
        	button.setBackgroundDrawable(null);
        	button.setTextColor(DashboardActivity.this.getResources().getColor(R.color.black));
        	button.setTextSize(16);
        	button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if(category.getId() == 1) {
						Intent intent = new Intent(DashboardActivity.this, HomeActivity.class);
				        intent.putExtra(Config.CATEGORY, category.getId());
				        startActivity(intent);
					} else {
						switch(category.getId()) {
							default:
								Intent intent = new Intent(DashboardActivity.this, WikiActivity.class);
								intent.putExtra(Config.CATEGORY, category.getId());
								startActivity(intent);
								break;
						}
					}
				}
			});
            return button;
        }
    }
    
    /*public boolean onCreateOptionsMenu(Menu menu) {
	   MenuInflater inflater = getMenuInflater();
	   inflater.inflate(R.menu.menu_main, menu); 
	   return true;
	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		    case R.id.profile:
		    	intent = new Intent(DashboardActivity.this, RegistrationActivity.class);
		        startActivity(intent);
		    	return true;
		    default:
		    	return true;
	    }
	}*/

    public void onHomeClick(View v) {
    }
    
}