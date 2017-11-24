package ru.studprof.studprof.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import ru.studprof.studprof.Fragments.MyPreferenceFragment;
import ru.studprof.studprof.Fragments.NestedPreferenceFragment;
import ru.studprof.studprof.R;

public class SettingsActivity extends AppCompatActivity implements MyPreferenceFragment.Callback {

    private static final String TAG_NESTED = "TAG_NESTED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(android.R.style.Theme_Holo_Light_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }



        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.contentSettings, new MyPreferenceFragment())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        // this if statement is necessary to navigate through nested and main fragments
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onNestedPreferenceSelected(int key) {
        getFragmentManager().beginTransaction().replace(R.id.contentSettings, NestedPreferenceFragment.newInstance(key), TAG_NESTED).addToBackStack(TAG_NESTED).commit();
    }
}