package com.siddworks.android.crashed;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.siddworks.android.crashed.model.AppsModel;
import com.siddworks.android.crashed.ui.widgets.FastBitmapDrawable;
import com.siddworks.android.crashed.util.AppHelper;
import com.siddworks.android.crashed.util.CommonUtil;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class WidgetConfigurationActivity extends Activity {

    private int mAppWidgetId;
    private EditText nameEditText;
    private TextView namePreviewEditText;
    private static final String TAG = "WidgetConfigActivity";
    private ImageView iconPreviewImageView;
    private ImageView iconImageButton;
    private AppsModel appModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        setContentView(R.layout.activity_widget_configuration);

        nameEditText = (EditText) findViewById(R.id.widget_name_EditText);
        namePreviewEditText = (TextView) findViewById(R.id.widget_text_preview_ImageView);
        iconPreviewImageView = (ImageView) findViewById(R.id.widget_icon_preview_ImageView);
        iconImageButton = (ImageView) findViewById(R.id.widget_icon_Image);
        iconImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectIconClick();
            }
        });
        namePreviewEditText.setText(getResources().getString(R.string.app_name));
        nameEditText.setText(getResources().getString(R.string.app_name));

        TextWatcher textWatcher = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                namePreviewEditText.setText(CommonUtil.getText(nameEditText) + "");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) {
//                    Log.d(isLoggingEnabled, TAG, "afterTextChanged()");
            }
        };
        nameEditText.addTextChangedListener(textWatcher);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

            // If they gave us an intent without the widget id, just bail.
            if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                finish();
            }
        }
    }

    public void onSelectIconClick() {
        Intent i = new Intent(this, IconSelectionActivity.class);
        startActivityForResult(i, 1);
    }

    public void onDoneClick(View v) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        RemoteViews remoteView = new RemoteViews(getPackageName(),
                R.layout.layout_widget);
        remoteView.setTextViewText(R.id.textView, nameEditText.getText());
        if(appModel != null && appModel.getBitmap() !=null) {
            remoteView.setImageViewBitmap(R.id.imageView, appModel.getBitmap());
        }

        setOnClickIntent(remoteView, mAppWidgetId);

        appWidgetManager.updateAppWidget(mAppWidgetId, remoteView);

        saveToPrefs(nameEditText.getText(), appModel);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    private void setOnClickIntent(RemoteViews remoteView, int mAppWidgetId) {
        // Create an Intent to launch MainActivity
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        // This is needed to make this intent different from its previous intents
        intent.setData(Uri.parse("tel:/" + (int) System.currentTimeMillis()));
        // Creating a pending intent, which will be invoked when the user
        // clicks on the widget
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent,PendingIntent.FLAG_UPDATE_CURRENT);
        //  Attach an on-click listener to the clock
        remoteView.setOnClickPendingIntent(R.id.widget_parent_layout, pendingIntent);
    }

    private void saveToPrefs(Editable text, AppsModel appModel) {
        Log.d(TAG, "saveToPrefs() called with: " + "text = [" + text + "], appModel = [" + appModel + "]");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(mAppWidgetId + "_label", String.valueOf(text + ""));
        if (appModel != null && appModel.getBitmap() != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            appModel.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            String encoded = Base64.encodeToString(b, Base64.DEFAULT);

            edit.putString(mAppWidgetId+"_icon", encoded);
        }
        edit.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == Activity.RESULT_OK){
            String result = data.getStringExtra("PACKAGE_NAME");
            Log.d(TAG, " result:"+result);
            appModel = new AppsModel().getAppByPackage(result);
            if (appModel != null) {
                Bitmap bitmap = appModel.getBitmap();
                iconImageButton.setImageDrawable(new FastBitmapDrawable(bitmap));
                iconPreviewImageView.setImageDrawable(new FastBitmapDrawable(bitmap));
                nameEditText.setText(appModel.getAppName());
            } else {
                List<AppsModel> model = AppHelper.getModel();
                if (model != null) {
                    for (AppsModel appsModel : model) {
                        if(result.equals(appsModel.getPackageName())) {
                            appModel = appsModel;
                            Bitmap bitmap = appsModel.getBitmap();
                            iconImageButton.setImageDrawable(new FastBitmapDrawable(bitmap));
                            iconPreviewImageView.setImageDrawable(new FastBitmapDrawable(bitmap));
                            nameEditText.setText(appsModel.getAppName());
                            break;
                        }
                    }
                }
            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
        }
    }//onActivityResult
}
