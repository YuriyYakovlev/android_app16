package com.yay.iloveua.core;

import java.util.HashMap;

import android.content.Context;
import android.preference.PreferenceManager;


public final class ServiceLocator {
	private volatile static ServiceLocator _instance;
	private final static Object _syncRoot = new Object();

	@SuppressWarnings("rawtypes")
	private HashMap<Class, Object> _services = new HashMap<Class, Object>();

	private ServiceLocator() {
	}

	public static ServiceLocator getInstance(Context context) {
		synchronized (_syncRoot) {
			if (_instance == null) {
				//Log.d(Config.LOG_TAG, "Application started");
				_instance = new ServiceLocator();
				//init services
				_instance.initServices(context);
			}
		}
		return _instance;
	}

	public void addService(Object service) {
		if (!_services.containsKey(service.getClass())) {
			_services.put(service.getClass(), service);
		}
	}

	private void initServices(Context context) {
		addService(new PreferencesManager(PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext())));
		addService(new DatabaseController(context.getApplicationContext()));
	}

	public void removeServices() {
		if (!_services.isEmpty()) {
			_services.clear();
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getService(Class<T> serviceClass) {
		if (_services.containsKey(serviceClass)) {
			return (T) _services.get(serviceClass);
		}
		return null;
	}
}
