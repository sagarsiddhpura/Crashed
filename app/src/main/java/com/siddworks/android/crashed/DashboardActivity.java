package com.siddworks.android.crashed;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class DashboardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dashboard);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showCrash();
            }
        }, 1500);

        Window window = this.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    private void showCrash() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this)
                .setMessage("Unfortunately, the application has stopped.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DashboardActivity.this.finish();
                    }
                })
//	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//	        public void onClick(DialogInterface dialog, int which) {
//	            // do nothing
//	        }
//	     })
                .setIcon(android.R.drawable.ic_dialog_alert);
        AlertDialog dialog = alert.create();
//	     .show();

        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                DashboardActivity.this.finish();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                DashboardActivity.this.finish();
            }
        });
        dialog.show();

    }
}
