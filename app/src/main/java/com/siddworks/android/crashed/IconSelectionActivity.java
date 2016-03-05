package com.siddworks.android.crashed;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.siddworks.android.crashed.model.AppsModel;
import com.siddworks.android.crashed.ui.adapter.AppsAdapter;
import com.siddworks.android.crashed.ui.loader.AppsLoader;
import com.siddworks.android.crashed.ui.widgets.EmptyRecyclerView;
import com.siddworks.android.crashed.ui.widgets.FontTextView;
import com.siddworks.android.crashed.util.AppHelper;
import com.siddworks.android.crashed.util.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IconSelectionActivity extends AppCompatActivity implements 
        LoaderManager.LoaderCallbacks<List<AppsModel>>, OnItemClickListener {

    @Bind(R.id.recycler)
    EmptyRecyclerView recycler;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.emptyText)
    FontTextView emptyText;
    private AppsAdapter adapter;
    public HashMap<Integer, AppsModel> selectedApps = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);
        adapter = new AppsAdapter(this, new ArrayList<AppsModel>());

        GridLayoutManager manager = new GridLayoutManager(this, getResources().getInteger(R.integer.num_row));
        recycler.setEmptyView(emptyText);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
        this.getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<AppsModel>> onCreateLoader(int id, Bundle args) {
        progressBar.setVisibility(View.VISIBLE);
        return new AppsLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<AppsModel>> loader, List<AppsModel> data) {
        progressBar.setVisibility(View.GONE);
        adapter.insert(data);
        AppHelper.setModel(data);
    }

    @Override
    public void onLoaderReset(Loader<List<AppsModel>> loader) {
        adapter.clearAll();
    }

    @Override
    public void onItemClickListener(View view, int position) {

    }

    @Override
    public void onItemLongClickListener(RecyclerView.ViewHolder viewHolder, View view, int position) {
        AppsModel appsModel = adapter.getModelList().get(position);
        if(appsModel != null) {
            ComponentName componentName = appsModel.getComponentName();
            String className = componentName.getClassName();
            String packageName = componentName.getPackageName();
            Intent intent=new Intent();
            intent.putExtra("PACKAGE_NAME", packageName);
            setResult(Activity.RESULT_OK,intent);
            finish();//finishing activity
        }
    }
}
