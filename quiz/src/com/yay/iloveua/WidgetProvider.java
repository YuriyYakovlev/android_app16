package com.yay.iloveua;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import com.yay.iloveua.models.Event;
import com.yay.iloveua.provider.EventColumns;
import com.yay.iloveua.provider.EventQuery;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by yay on 5/20/13.
 */
public class WidgetProvider extends AppWidgetProvider {
    private static List<Event> events;
    private enum Period { DAYS, YEARS; }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        try {
            updateWidgetContent(context, appWidgetManager);
        } catch (Exception e) {
            Log.e(Config.LOG_TAG, "Failed", e);
        }
    }
    public static void updateWidgetContent(Context context,
                                           AppWidgetManager appWidgetManager) {
        Cursor cursor = context.getContentResolver().query(Event.buildEventsUri(), EventQuery.PROJECTION,
                null, null, EventColumns.NAME.toString());
        while(cursor.moveToNext()) {
            getEvents().add(Event.buildFromCursor(cursor));
        }
        cursor.close();
        Collections.shuffle(getEvents());

        Date currentDate = new Date();
        currentDate.setTime(System.currentTimeMillis());//-9118836000000L);//System.currentTimeMillis());
        Date eventDate = new Date();

        boolean found = false;
        for(Event event : events) {
           eventDate.setTime(event.getDate());
           long days = (long) (currentDate.getTime() - eventDate.getTime()) /  (1000 * 60 * 60 * 24); // difference
           long years = days / 365;
           if(days % 365 == 0) { // a round year
               processEvent(context, appWidgetManager, years, Period.YEARS, event);
               found = true;
               break;
           }
        }
        if(!found) { // year is not found, looking for round dates
            for(Event event : events) {
                eventDate.setTime(event.getDate());
                long days = (long) (currentDate.getTime() - eventDate.getTime()) /  (1000 * 60 * 60 * 24); // difference
                if(roundDays(days)) { // a multiple of 10
                    processEvent(context, appWidgetManager, days, Period.DAYS, event);
                    break;
                }
            }
        }
    }

    private static boolean roundDays(long days) {
        if(days%100==0 || days%1000==0 || days%10000==0 || days%100000==0 || days%1000000==0 ||
                days%200==0 || days%2000==0 || days%20000==0 || days%200000==0 || days%5000000==0 ||
                days%300==0 || days%3000==0 || days%30000==0 || days%300000==0 || days%3000000==0 ||
                days%400==0 || days%4000==0 || days%40000==0 || days%400000==0 || days%4000000==0 ||
                days%500==0 || days%5000==0 || days%50000==0 || days%500000==0 || days%5000000==0 ||
                days%600==0 || days%6000==0 || days%60000==0 || days%600000==0 || days%6000000==0 ||
                days%700==0 || days%7000==0 || days%70000==0 || days%700000==0 || days%7000000==0 ||
                days%800==0 || days%8000==0 || days%80000==0 || days%800000==0 || days%8000000==0 ||
                days%900==0 || days%9000==0 || days%90000==0 || days%900000==0 || days%9000000==0 ) {
            return true;
        }
        return false;
    }

    private static void processEvent(Context context, AppWidgetManager appWidgetManager, long delta, Period period,
                                     Event event) {
        StringBuilder name = new StringBuilder();
        name.append(delta).append(" ");
        switch(period) {
            case YEARS: name.append(context.getString(R.string.years_ago));
                break;
            case DAYS: name.append(context.getString(R.string.days_ago));
                break;
        }
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_screen);
        remoteView.setTextViewText(R.id.name, name.toString());
        if(event.getImage()!=null && !"".equals(event.getImage())) {
            remoteView.setImageViewBitmap(R.id.image, getImageBitmap(event.getImage()));
        }
        remoteView.setTextViewText(R.id.description, event.getName());
        if(event.getLink()!=null && !"".equals(event.getLink())) {
            Intent launchAppIntent = new Intent(context, WikiActivity.class);
            launchAppIntent.putExtra(Config.URL, event.getLink());
            PendingIntent launchAppPendingIntent = PendingIntent.getActivity(context,
                    0, launchAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteView.setOnClickPendingIntent(R.id.full_widget, launchAppPendingIntent);
        }
        ComponentName tutListWidget = new ComponentName(context, WidgetProvider.class);
        appWidgetManager.updateAppWidget(tutListWidget, remoteView);
    }

    private static Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e(Config.LOG_TAG, "Error getting bitmap", e);
        }
        return bm;
    }

    private static List<Event> getEvents() {
        if(events == null) {
            events = new ArrayList<Event>();
        }
        return events;
    }
}