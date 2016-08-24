package ru.studprof.studprof.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import ru.studprof.studprof.R;

public class OpenWithStudprofActivty extends AppCompatActivity {

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_with_studprof);
        url = String.valueOf(getIntent().getData());
        Log.d("Open", url);
        url = url.toLowerCase();

        Intent intent;
        if(url.contains("feed")) {
            intent = new Intent(OpenWithStudprofActivty.this, ActivityFeedCollapsingToolbar.class);
            url = url.substring(url.indexOf("/feed"));
            url = "http://xn--d1aucecghm.xn--p1ai" + url;
            intent.putExtra(Constants.FEED_URL, url);
            intent.putExtra(Constants.OPEN_LAST_ACTIVITY, true);
            startActivity(intent);
        }
        else if(url.contains("photo/") & url.contains(".html")) {
            intent = new Intent(OpenWithStudprofActivty.this, PhotoGallery.class);
            url = url.substring(url.indexOf("/photo"));

            intent.putExtra(Constants.FEED_URL_FOR_GALLERY, url);
            startActivity(intent);
        }
        else if(url.equals("студпроф.рф")
                |url.equals("http://студпроф.рф")
                |url.equals("www.студпроф.рф")
                |url.equals("http://www.студпроф.рф")
                |url.equals("xn--d1aucecghm.xn--p1ai")
                |url.equals("http://xn--d1aucecghm.xn--p1ai")
                |url.equals("www.xn--d1aucecghm.xn--p1ai")
                |url.equals("http://www.xn--d1aucecghm.xn--p1ai")
                ) {
            intent = new Intent(OpenWithStudprofActivty.this, MainActivity.class);
            startActivity(intent);
        } else if(url.contains("http://студпроф.рф/afisha")) {
            intent = new Intent(OpenWithStudprofActivty.this, ActivityFeedCollapsingToolbar.class);
            url = url.substring(url.indexOf("/afisha"));
            url = "http://xn--d1aucecghm.xn--p1ai" + url;
            intent.putExtra(Constants.FEED_URL, url);
            intent.putExtra(Constants.OPEN_LAST_ACTIVITY, true);
            startActivity(intent);

        } else {
            Toast.makeText(getApplicationContext(), "Cсылки такого типа не поддерживаются", Toast.LENGTH_LONG).show();
            }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_open_with_studprof_activty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
