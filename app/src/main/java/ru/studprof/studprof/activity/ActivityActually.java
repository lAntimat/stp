package ru.studprof.studprof.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class ActivityActually extends AppCompatActivity implements SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {

    private AccountHeader headerResult;
    private Drawer drawerBuilder;
    Toolbar toolbar;
    ProgressBar progressBar;
    HtmlTextView text;
    SharedPreferences sharedPreferences;
    ImageView ivS;
    int sCount = 0;
    private MenuItem searchMenuItem;
    private SearchView mSearchView;



    final String SAVED_TEXT = "saved_text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actually);

        toolbar = (Toolbar) findViewById(R.id.toolbar1);
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

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar1);
        collapsingToolbarLayout.setTitle(" ");

        ivS = (ImageView) findViewById(R.id.backdrop);

        progressBar = (ProgressBar) findViewById(R.id.progressBarActually);

        text = (HtmlTextView) findViewById(R.id.html_text);

        //initAccountHeader();
        //initDrawer();

        if(text!=null || !text.getText().equals("")) {
            new TextViewUpdate().execute();
            new Task().execute();
        }

        //our toolbar's height has to include the padding of the statusBar so the ColapsingToolbarLayout and the Toolbar can position
        //the arrow/title/... correct

        //fillFab();
        //loadBackdrop();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_actually, menu);

        searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, this);
        MenuItemCompat.setActionView(searchMenuItem, mSearchView);
        mSearchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.search_hint) + "</font>"));
        searchMenuItem.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
//        drawerBuilder.setSelectionAtPosition(1);
        super.onStart();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (drawerBuilder != null && drawerBuilder.isDrawerOpen()) {
            drawerBuilder.closeDrawer();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }
    }

    public void sMenu(View view) {
        try {
            if (sCount == -1)
                Toast.makeText(getApplicationContext(), "Я вижу ты знаешь чуть больше, чем другие", Toast.LENGTH_SHORT).show();
            if (sCount == -1)
                Toast.makeText(getApplicationContext(), "Парам пиду", Toast.LENGTH_SHORT).show();
            if (sCount == 19) {
                //Toast.makeText(getApplicationContext(), "Лучше остановиться, пока не поздно", Toast.LENGTH_SHORT).show();
                ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.ThemeOverlay_AppCompat_Dark);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctw);
                alertDialog.setTitle("ПУПСООПРЕДЕЛИТЬ 2000");
                alertDialog.setMessage("Кто здесь пупсик?");

                final EditText input = new EditText(ActivityActually.this);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("Да",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String str = input.getText().toString();
                                switch (str.toLowerCase()) {
                                    case "рн" :
                                    Toast.makeText(getApplicationContext(),
                                        "А я смотрю ты храбрый", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ты":
                                        Toast.makeText(getApplicationContext(),
                                                "Ой, ну брось ^^", Toast.LENGTH_LONG).show();
                                        break;
                                    case "ринета":
                                        Toast.makeText(getApplicationContext(),
                                                "Самый пупсятый пупсик ^^", Toast.LENGTH_LONG).show();
                                        break;

                                    default:
                                        Toast.makeText(getApplicationContext(),
                                                str + " здесь пупсик ^^", Toast.LENGTH_LONG).show();
                                        break;

                            }
                                /*if (str.toLowerCase().equals("рн")) {
                                    Toast.makeText(getApplicationContext(),
                                            "А я смотрю ты храбрый", Toast.LENGTH_LONG).show();
                                } else if (str.toLowerCase().equals("ты")) {
                                    Toast.makeText(getApplicationContext(),
                                            "Ой, ну брось ^^", Toast.LENGTH_LONG).show();
                                } else
                                    Toast.makeText(getApplicationContext(),
                                            str + " здесь пупсик ^^", Toast.LENGTH_LONG).show();
                            /*if (pass.equals(password)) {
                                Toast.makeText(getApplicationContext(),
                                        "Password Matched", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Wrong Password!", Toast.LENGTH_SHORT).show();
                            }*/
                                sCount = 0;
                            }
                        });

                alertDialog.setNegativeButton("Нет",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                sCount = 0;

                            }
                        });

                alertDialog.show();
            }
            sCount++;
        } catch (Exception e) {
            e.printStackTrace();
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

                                break;

                            case 2:
                                finishActivity(107);
                                Intent intent2 = new Intent(ActivityActually.this, MainActivity.class);
                                if (drawerBuilder != null && drawerBuilder.isDrawerOpen())
                                    drawerBuilder.closeDrawer();
                                startActivity(intent2);
                                finish();

                                break;
                            case 3:
                                Intent intent3 = new Intent(ActivityActually.this, ActivityPhoto.class);
                                if (drawerBuilder != null && drawerBuilder.isDrawerOpen())
                                    drawerBuilder.closeDrawer();
                                startActivity(intent3);
                                finish();

                                break;
                            case 4:
                                Intent intent0 = new Intent(ActivityActually.this, VideoActivity.class);
                                if (drawerBuilder != null && drawerBuilder.isDrawerOpen())
                                    drawerBuilder.closeDrawer();
                                startActivity(intent0);
                                finish();
                                break;
                            case 6:
                                Intent intent6 = new Intent(ActivityActually.this, SettingsActivity.class);
                                startActivity(intent6);

                                break;
                            case 7:
                                Intent intent7 = new Intent(ActivityActually.this, AboutActivity.class);
                                startActivity(intent7);
                                finish();

                                break;
                            case 10:
                                /*Intent intent6 = new Intent(MainActivity.this, AuthorizationActivity.class);
                                startActivity(intent6);*/


                        }
                        return false;
                    }
                })
                .withSelectedItemByPosition(1)
                .build();

    }


    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        //Glide.with(this).load("https://unsplash.it/600/300/?random").centerCrop().into(imageView);
        //Picasso.with(this).load("http://img.studprof.info/feed/2014-09/media/8117_ddd80ca5a6.jpg").into(imageView);
    }

    private void fillFab() {
        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating_action_button);
        //fab.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_favorite).actionBar().color(Color.WHITE));
        //fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_multiple_grey600_24dp));
        //fab.setVisibility(View.GONE);

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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }

    public class Task extends AsyncTask<String, Void, String> {

        String newPage = null;
        Elements newsRawTag = null;

        @Override
        protected String doInBackground(String... params) {

            Document doc = null;
            String url = "http://xn--d1aucecghm.xn--p1ai/feed/8117-aktualochka-365-ot-studprof-rf.html";
            try {
                doc = Jsoup.connect("http://xn--d1aucecghm.xn--p1ai/feed/8117-aktualochka-365-ot-studprof-rf.html").get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(doc!=null) {
                newsRawTag = doc.select("div.text-content");
                newPage = newsRawTag.html();
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
                    String html = newsRawTag.html();
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
            collapsingToolbarLayout.setTitle("Актуалочка 365");
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
