package com.siddworks.android.crashed;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.widget.RemoteViews;

/**
 * Created by SIDD on 03-Nov-15.
 */
public class CrashedWidgetProvider extends AppWidgetProvider {
    private static final String ACTION_CLICK = "ACTION_CLICK";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        try{
            // Get all ids
            ComponentName thisWidget = new ComponentName(context,
                    CrashedWidgetProvider.class);
            int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            for (int widgetId : allWidgetIds) {
                RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                        R.layout.layout_widget);

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                String label = sharedPreferences.getString(widgetId + "_label", "Crashed");
                String iconString = sharedPreferences.getString(widgetId + "_icon", null);
                // Set the text
                remoteView.setTextViewText(R.id.textView, label);

                if (iconString != null) {
                    byte[] bytes = iconString.getBytes();
                    byte[] imageAsBytes = Base64.decode(bytes, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                    remoteView.setImageViewBitmap(R.id.imageView, bitmap);
                } else {
                    Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                            R.mipmap.ic_launcher);
                    remoteView.setImageViewBitmap(R.id.imageView, icon);
                }

                // Create an Intent to launch MainActivity
                Intent intent = new Intent(context, DashboardActivity.class);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                // This is needed to make this intent different from its previous intents
                intent.setData(Uri.parse("tel:/" + (int) System.currentTimeMillis()));
                // Creating a pending intent, which will be invoked when the user
                // clicks on the widget
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                        intent,PendingIntent.FLAG_UPDATE_CURRENT);
                //  Attach an on-click listener to the widget
                remoteView.setOnClickPendingIntent(R.id.widget_parent_layout, pendingIntent);

                // Tell the AppWidgetManager to perform an update on the current app widget
                appWidgetManager.updateAppWidget(widgetId, remoteView);
            }
        }
        catch (Exception e) {

        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        try {
            // Get all ids
            ComponentName thisWidget = new ComponentName(context,
                    CrashedWidgetProvider.class);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            for (int widgetId : appWidgetIds) {
                edit.remove(widgetId + "_label");
                edit.remove(widgetId + "_icon");
                edit.commit();
            }
        }
        catch (Exception e) {

        }
    }
}
