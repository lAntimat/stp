package ru.studprof.studprof.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.IOException;

import ru.studprof.studprof.R;

public class AboutActivity extends AppCompatActivity implements FloatingActionButton.OnClickListener {

    private AccountHeader headerResult;
    private Drawer drawerBuilder;
    Toolbar toolbar;
    ProgressBar progressBar;
    HtmlTextView text;
    SharedPreferences sharedPreferences;
    FloatingActionButton fab;

    final String SAVED_TEXT = "saved_text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        progressBar = (ProgressBar) findViewById(R.id.progressBarActually);
        fab = (FloatingActionButton) findViewById(R.id.floating_action_button);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_email_white_24dp));

        text = (HtmlTextView) findViewById(R.id.html_text);

        //initAccountHeader();
        //initDrawer();

        if(text!=null || !text.getText().equals("")) {
            new TextViewUpdate().execute();
            new Task().execute();
        }

        fab.setOnClickListener(this);

    }



    @Override
    public void onBackPressed() {
        if(text.isTextSelectable()) text.setTextIsSelectable(false);
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }
    }

    private void initAccountHeader() {
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.drawer_bg)
                        //.withCompactStyle(true)
                /*.addProfiles(
                        new ProfileDrawerItem()
                                .withName("Mike Penz")
                                .withEmail("mikepenz@gmail.com"))*/

                .build();
    }

    private void initDrawer() {
        drawerBuilder = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName(R.string.nav_menu_item_actually_news)
                                .withIdentifier(1)
                                .withIcon(R.drawable.ic_newspaper_grey600_24dp)
                        ,
                        new PrimaryDrawerItem()
                                .withName(R.string.nav_menu_item_developments)
                                .withIdentifier(1)
                                .withIcon(R.drawable.ic_book_multiple_variant_grey600_24dp)
                        ,
                        new PrimaryDrawerItem()
                                .withName(R.string.nav_menu_item_photo)
                                .withIcon(R.drawable.ic_image_multiple_grey600_24dp)
                        ,
                        new PrimaryDrawerItem()
                                .withName(R.string.nav_menu_item_video)
                                .withIcon(R.drawable.ic_file_video_grey600_24dp)
                        ,
                        new DividerDrawerItem()
                        ,
                        new SecondaryDrawerItem()
                                .withName(R.string.nav_menu_item_setting)
                        ,
                        new SecondaryDrawerItem()
                                .withName(R.string.nav_menu_item_about)



                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                        switch (i) {
                            case 1:
                                Intent intent0 = new Intent(AboutActivity.this, ActivityActually.class);
                                if (drawerBuilder != null && drawerBuilder.isDrawerOpen())
                                    drawerBuilder.closeDrawer();
                                startActivity(intent0);

                                break;

                            case 2:
                                Intent intent2 = new Intent(AboutActivity.this, MainActivity.class);
                                if (drawerBuilder != null && drawerBuilder.isDrawerOpen())
                                    drawerBuilder.closeDrawer();
                                startActivity(intent2);
                                finish();
                                break;
                            case 3:
                                Intent intent3 = new Intent(AboutActivity.this, ActivityPhoto.class);
                                if (drawerBuilder != null && drawerBuilder.isDrawerOpen())
                                    drawerBuilder.closeDrawer();
                                startActivity(intent3);
                                finish();

                                break;
                            case 4:
                                Intent intent4 = new Intent(AboutActivity.this, VideoActivity.class);
                                startActivity(intent4);
                                finish();

                                break;
                            case 6:
                                Intent intent6 = new Intent(AboutActivity.this, SettingsActivity.class);
                                startActivity(intent6);

                                break;
                            case 7:
                                break;
                            case 10:
                                /*Intent intent6 = new Intent(MainActivity.this, AuthorizationActivity.class);
                                startActivity(intent6);*/


                        }
                        return false;
                    }
                })
                .withSelectedItemByPosition(7)
                .build();

    }


    void saveText(String text) {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(SAVED_TEXT, text);
        ed.commit();
        //Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show();
    }

    String loadText() {
        sharedPreferences= getPreferences(MODE_PRIVATE);
        String savedText = sharedPreferences.getString(SAVED_TEXT, "");
        //Toast.makeText(this, "Text loaded", Toast.LENGTH_SHORT).show();
        if(savedText!=null) return savedText;
        else return "";

    }


    //Fab click
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"studprof.rf@yandex.ru"});
        //intent.putExtra(Intent.EXTRA_SUBJECT, "");
        //intent.putExtra(Intent.EXTRA_TEXT   , "");
        startActivity(Intent.createChooser(intent, "studprof.rf@yandex.ru"));
    }

    public class Task extends AsyncTask<String, Void, String> {

        String newPage = null;
        Elements newsRawTag = null;

        @Override
        protected String doInBackground(String... params) {

            Document doc = null;
            String url = "http://xn--d1aucecghm.xn--p1ai/site/contact/";
            try {
                doc = Jsoup.connect("http://xn--d1aucecghm.xn--p1ai/site/contact").get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(doc!=null) {
                //newsRawTag = doc.select("section.span16");
                newsRawTag = doc.select("div");
                Log.d("AboutHtml", newsRawTag.get(35).html());
                newPage = newsRawTag.get(35).html();
                newPage = newPage.replace("<p><img alt=\"Kontakt\" height=\"169\" src=\"http://a.studprof.info/images/Kontakt.jpg\" style=\"margin:5px;float:left;\" width=\"300\">", "");
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {


            //mWebView.loadDataWithBaseURL(null, newsRawTag.html() ,
            //"text/html", "utf-8", null);

            /*mWebView.loadDataWithBaseURL(null, newsRawTag.html(),
                    "text/html", "utf-8", null);*/
            progressBar.setVisibility(View.GONE);


            if(newsRawTag == null & !loadText().equals("")) {
                //Toast.makeText(getApplicationContext(), R.string.err_msg_updateListView, Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    String html = newPage;
                    saveText(html);
                    text.setHtmlFromString(html, new HtmlTextView.RemoteImageGetter());
                } catch (NullPointerException ex) {
                    Toast.makeText(getApplicationContext(), R.string.err_msg_updateListView, Toast.LENGTH_LONG).show();

                }
            }


            //Log.d("ActivityActuallyNews", html);

            super.onPostExecute(s);
        }
    }

    public class TextViewUpdate extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            CollapsingToolbarLayout.LayoutParams lp = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
            lp.height = lp.height;
            //+ UIUtils.getStatusBarHeight(this);
            toolbar.setLayoutParams(lp);

            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar1);
            collapsingToolbarLayout.setTitle("Контакты");
            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.transparent));

            if(!loadText().equals("")) {
                text.setHtmlFromString(loadText(), new HtmlTextView.RemoteImageGetter());
                progressBar.setVisibility(View.GONE);
            }
            else  text.setText("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" +
                    "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" +
                    "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" +
                    "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

            super.onPostExecute(aVoid);
        }
    }
}
