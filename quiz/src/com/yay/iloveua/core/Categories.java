package com.yay.iloveua.core;

import com.yay.iloveua.R;


public enum Categories {
	C2(2, "Конституція", R.drawable.b2, "Конституція_України"),
	C3(3, "Історія", R.drawable.b2, "Історія_України"),
	C4(4, "Діячі", R.drawable.b2, "Категорія:Українські_державні_діячі");
	/*C5(5, "C++", R.drawable.c), 
	C6(6, ".Net", R.drawable.net);*/
	
	private int id;
	private String name;
	private String searchUrl;
	private int resId;
	
    Categories(int id, String name, int resId, String searchUrl) {
    	this.id = id;
    	this.name = name;
    	this.resId = resId;
    	this.searchUrl = searchUrl;
    }
    
    public String getName() {
    	return name;
    }
    public int getId() {
    	return id;
    }
    public int getResId() {
    	return resId;
    }
    public String getSearchUrl() {
    	return searchUrl;
    }
    
    public static Categories findById(int id) {
    	for(Categories category : values()) {
			if(category.getId() == id) {
				return category;
			}
		}
    	return null;
    }
    
    public static Categories findByName(String name) {
    	for(Categories category : values()) {
			if(category.getName().equals(name)) {
				return category;
			}
		}
    	return null;
    }
    
}