package ru.studprof.studprof.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import ru.studprof.studprof.R;
import ru.studprof.studprof.adapter.VideoAdapter;
import ru.studprof.studprof.adapter.VideoDescribe;
import ru.studprof.studprof.util.AuthChecker;

public class VideoActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    Toolbar toolbar;
    VideoAdapter videoAdapter;
    ArrayList<VideoDescribe> videoDescribes = new ArrayList<>();
    ListView listView;
    Button button;
    TextView textView;
    SwipeRefreshLayout swipeRefreshLayout;
    Drawer drawerBuilder;
    AccountHeader headerResult;
    AuthChecker authChecker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.swipeRefreshColorYellow);
        videoAdapter = new VideoAdapter(getApplicationContext(), videoDescribes);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });


        listView = (ListView) findViewById(R.id.lv);

        new ListViewTask().execute();

        initAccountHeader();
        initDrawer();


    }

    @Override
    protected void onResume() {
        StringHolder stringHolderExit = new StringHolder("Выйти");
        StringHolder stringHolderAuth = new StringHolder("Авторизация");

        try {
            authChecker = new AuthChecker(getApplicationContext());
            if(authChecker.isAuthed()) {
                //drawerBuilder.updateName(10, stringHolderExit);
                if(authChecker.isStudprofMember()) initDrawerAfterLoginIfStudprofmember();
                else initDrawerAfterLogin();
            } else {
                //drawerBuilder.updateName(10, stringHolderAuth);
                initDrawer();
            }
            //drawerBuilder.setSelectionAtPosition(2);
            //prefs.registerOnSharedPreferenceChangeListener(this);
        } catch (Exception e) {

        }
        super.onResume();
    }

    @Override
    protected void onStart() {
        drawerBuilder.setSelectionAtPosition(4);
        super.onStart();
    }

    private void initAccountHeader() {
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.studprofdrawerbg)
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
                        new PrimaryDrawerItem()
                                .withName(R.string.nav_menu_item_mradio)
                                .withIcon(R.drawable.ic_radio_grey600_24dp)

                        ,
                        new PrimaryDrawerItem()
                                .withName(R.string.nav_menu_item_setting)
                                .withIcon(R.drawable.ic_settings_grey600_24dp)
                        ,
                        new PrimaryDrawerItem()
                                .withName(R.string.nav_menu_item_about)
                        ,
                        new PrimaryDrawerItem()
                                .withName(R.string.nav_menu_item_more_soc_networks)
                        ,
                        new DividerDrawerItem()
                        ,
                        new SecondaryDrawerItem()
                                .withName("Авторизация")
                                .withIdentifier(10)
                       /* ,
                        new SecondaryDrawerItem()
                        .withName("Мои мероприятия")
                        ,
                        new SecondaryDrawerItem()
                                .withName("Оповещения")*/


                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                        switch (i) {
                            case 1:

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(getApplicationContext(), ActivityActually.class);
                                        startActivity(intent2);
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);
                                break;
                            case 2:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent2);
                                        finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                    }
                                }, 250);

                                break;
                            case 3:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(getApplicationContext(), ActivityPhoto.class);
                                        startActivity(intent2);
                                        finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);
                                break;
                            case 5:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(getApplicationContext(), ActivityMradio.class);
                                        startActivity(intent2);
                                        //finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);

                                break;
                            case 6:
                                Intent intent6 = new Intent(getApplicationContext(), SettingsActivity.class);
                                startActivity(intent6);
//
                                break;
                            case 7:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(getApplicationContext(), AboutActivity.class);
                                        startActivity(intent2);
                                        //finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);

                                break;
                            case 8:
                                new MaterialDialog.Builder(getApplicationContext())
                                        //.title(R.string.title)
                                        .theme(Theme.LIGHT)
                                        .title(R.string.nav_menu_item_more_soc_networks)
                                        .items(R.array.more_soc_networks)
                                        .itemsCallback(new MaterialDialog.ListCallback() {
                                            @Override
                                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                                switch (which) {
                                                    case 0:
                                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                                        i.setData(Uri.parse("http://vk.com/studprofrf"));
                                                        startActivity(i);
                                                        break;

                                                    case 1:
                                                        Intent i1 = new Intent(Intent.ACTION_VIEW);
                                                        i1.setData(Uri.parse("https://www.instagram.com/studprofrf"));
                                                        startActivity(i1);
                                                        break;

                                                    case 2:
                                                        Intent i2 = new Intent(Intent.ACTION_VIEW);
                                                        i2.setData(Uri.parse("https://www.youtube.com/user/AdamuAkeno"));
                                                        startActivity(i2);
                                                        break;
                                                    case 3:
                                                        Intent i3 = new Intent(Intent.ACTION_VIEW);
                                                        i3.setData(Uri.parse("https://twitter.com/studprofrf"));
                                                        startActivity(i3);
                                                        break;
                                                }

                                            }
                                        })
                                        .show();
                                drawerBuilder.setSelectionAtPosition(4);
                                break;
                            case 10:
                                /*if (drawerBuilder != null && drawerBuilder.isDrawerOpen())
                                    drawerBuilder.closeDrawer();*/
                                Intent intent7;

                                if (authChecker.isAuthed()) {
                                    intent7 = new Intent(getApplicationContext(), AuthorizationActivity.class);
                                    intent7.setAction(Constants.ACTION_EXIT);
                                    startActivity(intent7);
                                } else {
                                    intent7 = new Intent(getApplicationContext(), AuthorizationActivity.class);
                                    intent7.setAction(Constants.ACTION_AUTH);
                                    startActivity(intent7);
                                }

                                /*StringHolder stringHolderExit = new StringHolder("Выйти");
                                StringHolder stringHolderAuth = new StringHolder("Авторизация");

                                if(authChecker.isAuthed()) {
                                    drawerBuilder.updateName(10, stringHolderExit);
                                } else drawerBuilder.updateName(10, stringHolderAuth);*/

                                       /* new MaterialDialog.Builder(MainActivity.this)
                                                 .title(R.string.title_windows_auth)
                                                .theme(Theme.LIGHT)
                                                .items(R.array.auth_enterAndExit)
                                                .itemsCallback(new MaterialDialog.ListCallback() {
                                                    @Override
                                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                        Intent intent7;

                                                        switch (which) {
                                                            case 0:
                                                                intent7 = new Intent(MainActivity.this, AuthorizationActivity.class);
                                                                intent7.setAction(Constants.ACTION_AUTH);
                                                                startActivity(intent7);
                                                                break;
                                                            case 1:
                                                                intent7 = new Intent(MainActivity.this, AuthorizationActivity.class);
                                                                intent7.setAction(Constants.ACTION_EXIT);
                                                                startActivity(intent7);
                                                                break;
                                                        }
                                                    }
                                                })
                                                .show();*/
                                break;
                        }

                        return false;
                    }
                })
                .withSelectedItemByPosition(2)
                .build();

    }
    private void initDrawerAfterLogin() {
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
                        new PrimaryDrawerItem()
                                .withName(R.string.nav_menu_item_mradio)
                                .withIcon(R.drawable.ic_radio_grey600_24dp)

                        ,
                        new PrimaryDrawerItem()
                                .withName(R.string.nav_menu_item_setting)
                                .withIcon(R.drawable.ic_settings_grey600_24dp)
                        ,
                        new PrimaryDrawerItem()
                                .withName(R.string.nav_menu_item_about)
                        ,
                        new PrimaryDrawerItem()
                                .withName(R.string.nav_menu_item_more_soc_networks)
                        ,
                        new DividerDrawerItem()
                        ,
                        new SecondaryDrawerItem()
                                .withName("Выйти")
                                .withIdentifier(10)
                       /* ,
                        new SecondaryDrawerItem()
                        .withName("Мои мероприятия")*/
                        ,
                        new SecondaryDrawerItem()
                                .withName("Оповещения")


                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                        switch (i) {
                            case 1:

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(getApplicationContext(), ActivityActually.class);
                                        startActivity(intent2);
                                        //finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);
                                break;
                            case 2:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent2);
                                        finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);
                                break;
                            case 3:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(getApplicationContext(), ActivityPhoto.class);
                                        startActivity(intent2);
                                        finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);
                                break;
                            case 5:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(getApplicationContext(), ActivityMradio.class);
                                        startActivity(intent2);
                                        //finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);
                                break;
                            case 6:
                                Intent intent6 = new Intent(getApplicationContext(), SettingsActivity.class);
                                startActivity(intent6);
//                                ft.remove(frag1);
                                break;
                            case 7:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(getApplicationContext(), AboutActivity.class);
                                        startActivity(intent2);
                                        //finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);
                                break;
                            case 8:
                                new MaterialDialog.Builder(getApplicationContext())
                                        //.title(R.string.title)
                                        .theme(Theme.LIGHT)
                                        .title(R.string.nav_menu_item_more_soc_networks)
                                        .items(R.array.more_soc_networks)
                                        .itemsCallback(new MaterialDialog.ListCallback() {
                                            @Override
                                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                                switch (which) {
                                                    case 0:
                                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                                        i.setData(Uri.parse("http://vk.com/studprofrf"));
                                                        startActivity(i);
                                                        break;

                                                    case 1:
                                                        Intent i1 = new Intent(Intent.ACTION_VIEW);
                                                        i1.setData(Uri.parse("https://www.instagram.com/studprofrf"));
                                                        startActivity(i1);
                                                        break;

                                                    case 2:
                                                        Intent i2 = new Intent(Intent.ACTION_VIEW);
                                                        i2.setData(Uri.parse("https://www.youtube.com/user/AdamuAkeno"));
                                                        startActivity(i2);
                                                        break;
                                                    case 3:
                                                        Intent i3 = new Intent(Intent.ACTION_VIEW);
                                                        i3.setData(Uri.parse("https://twitter.com/studprofrf"));
                                                        startActivity(i3);
                                                        break;
                                                }

                                            }
                                        })
                                        .show();
                                drawerBuilder.setSelectionAtPosition(2);
                                break;
                            case 10:
                                /*if (drawerBuilder != null && drawerBuilder.isDrawerOpen())
                                    drawerBuilder.closeDrawer();*/
                                Intent intent7;

                                if (authChecker.isAuthed()) {
                                    intent7 = new Intent(getApplicationContext(), AuthorizationActivity.class);
                                    intent7.setAction(Constants.ACTION_EXIT);
                                    startActivity(intent7);
                                } else {
                                    intent7 = new Intent(getApplicationContext(), AuthorizationActivity.class);
                                    intent7.setAction(Constants.ACTION_AUTH);
                                    startActivity(intent7);
                                }

                                break;
                            case 11:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(getApplicationContext(), FeedBackActivity.class);
                                        startActivity(intent2);
                                        //finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);
                        }

                        return false;
                    }
                })
                .withSelectedItemByPosition(4)
                .build();

    }
    private void initDrawerAfterLoginIfStudprofmember() {
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
                        new PrimaryDrawerItem()
                                .withName(R.string.nav_menu_item_mradio)
                                .withIcon(R.drawable.ic_radio_grey600_24dp)

                        ,
                        new PrimaryDrawerItem()
                                .withName(R.string.nav_menu_item_setting)
                                .withIcon(R.drawable.ic_settings_grey600_24dp)
                        ,
                        new PrimaryDrawerItem()
                                .withName(R.string.nav_menu_item_about)
                        ,
                        new PrimaryDrawerItem()
                                .withName(R.string.nav_menu_item_more_soc_networks)
                        ,
                        new DividerDrawerItem()
                        ,
                        new SecondaryDrawerItem()
                                .withName("Выйти")
                                .withIdentifier(10)
                        ,
                        new SecondaryDrawerItem()
                                .withName("Мои мероприятия")
                        ,
                        new SecondaryDrawerItem()
                                .withName("Оповещения")


                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                        switch (i) {
                            case 1:

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(getApplicationContext(), ActivityActually.class);
                                        startActivity(intent2);
                                        //finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);
                                break;
                            case 2:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent2);
                                        finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);
                                /*if (drawerBuilder != null && drawerBuilder.isDrawerOpen())
                                    drawerBuilder.closeDrawer();*/



                                break;
                            case 3:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent4 = new Intent(getApplicationContext(), ActivityPhoto.class);
                                        startActivity(intent4);
                                        finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);
                                break;
                            case 5:

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(getApplicationContext(), ActivityMradio.class);
                                        startActivity(intent2);
                                        //finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);

                                break;
                            case 6:
                                Intent intent6 = new Intent(getApplicationContext(), SettingsActivity.class);
                                startActivity(intent6);
                                break;
                            case 7:

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(getApplicationContext(), AboutActivity.class);
                                        startActivity(intent2);
                                        //finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);

                                break;
                            case 8:
                                new MaterialDialog.Builder(getApplicationContext())
                                        //.title(R.string.title)
                                        .theme(Theme.LIGHT)
                                        .title(R.string.nav_menu_item_more_soc_networks)
                                        .items(R.array.more_soc_networks)
                                        .itemsCallback(new MaterialDialog.ListCallback() {
                                            @Override
                                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                                switch (which) {
                                                    case 0:
                                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                                        i.setData(Uri.parse("http://vk.com/studprofrf"));
                                                        startActivity(i);
                                                        break;

                                                    case 1:
                                                        Intent i1 = new Intent(Intent.ACTION_VIEW);
                                                        i1.setData(Uri.parse("https://www.instagram.com/studprofrf"));
                                                        startActivity(i1);
                                                        break;

                                                    case 2:
                                                        Intent i2 = new Intent(Intent.ACTION_VIEW);
                                                        i2.setData(Uri.parse("https://www.youtube.com/user/AdamuAkeno"));
                                                        startActivity(i2);
                                                        break;
                                                    case 3:
                                                        Intent i3 = new Intent(Intent.ACTION_VIEW);
                                                        i3.setData(Uri.parse("https://twitter.com/studprofrf"));
                                                        startActivity(i3);
                                                        break;
                                                }

                                            }
                                        })
                                        .show();
                                drawerBuilder.setSelectionAtPosition(2);
                                break;
                            case 10:
                                /*if (drawerBuilder != null && drawerBuilder.isDrawerOpen())
                                    drawerBuilder.closeDrawer();*/
                                Intent intent7;

                                if (authChecker.isAuthed()) {
                                    intent7 = new Intent(getApplicationContext(), AuthorizationActivity.class);
                                    intent7.setAction(Constants.ACTION_EXIT);
                                    startActivity(intent7);
                                } else {
                                    intent7 = new Intent(getApplicationContext(), AuthorizationActivity.class);
                                    intent7.setAction(Constants.ACTION_AUTH);
                                    startActivity(intent7);
                                }

                                /*StringHolder stringHolderExit = new StringHolder("Выйти");
                                StringHolder stringHolderAuth = new StringHolder("Авторизация");

                                if(authChecker.isAuthed()) {
                                    drawerBuilder.updateName(10, stringHolderExit);
                                } else drawerBuilder.updateName(10, stringHolderAuth);*/

                                       /* new MaterialDialog.Builder(MainActivity.this)
                                                 .title(R.string.title_windows_auth)
                                                .theme(Theme.LIGHT)
                                                .items(R.array.auth_enterAndExit)
                                                .itemsCallback(new MaterialDialog.ListCallback() {
                                                    @Override
                                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                        Intent intent7;

                                                        switch (which) {
                                                            case 0:
                                                                intent7 = new Intent(MainActivity.this, AuthorizationActivity.class);
                                                                intent7.setAction(Constants.ACTION_AUTH);
                                                                startActivity(intent7);
                                                                break;
                                                            case 1:
                                                                intent7 = new Intent(MainActivity.this, AuthorizationActivity.class);
                                                                intent7.setAction(Constants.ACTION_EXIT);
                                                                startActivity(intent7);
                                                                break;
                                                        }
                                                    }
                                                })
                                                .show();*/
                                break;
                            case 11:

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent7 = new Intent(getApplicationContext(), ActivityAfisha.class);
                                        intent7.putExtra(Constants.COMMENTS_URL, "http://xn--d1aucecghm.xn--p1ai/afisha/592-nyus-djei-it-times.html");
                                        startActivity(intent7);
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                    }
                                }, 250);
                                break;

                            case 12:

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(getApplicationContext(), FeedBackActivity.class);
                                        startActivity(intent2);
                                        //finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                    }
                                }, 250);

                                break;
                        }

                        return false;
                    }
                })
                .withSelectedItemByPosition(4)
                .build();

    }

    @Override
    public void onRefresh() {

        videoDescribes.clear();
        new ListViewTask().execute();

    }

    @Override
    public void onBackPressed() {
        if (drawerBuilder!= null && drawerBuilder.isDrawerOpen()) {
            drawerBuilder.closeDrawer();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

        }

    }

    public class ListViewTask extends AsyncTask<Void, Void, Void> {

        Document doc = null;
        Elements name;
        Elements duration;
        Elements date;
        Elements imageUrl;
        Elements url;
        Elements urlType;

        String youTubeUrl = "http://youtu.be/";
        String vimeoUrl = "https://vimeo.com/";

        final static String youTube = "1";
        final static String vimeo = "3";

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                doc = Jsoup.connect("http://xn--d1aucecghm.xn--p1ai/video/").ignoreContentType(true).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(doc!=null) {
                name = doc.select("a.video-item-image.popup-play-video.js");
                duration = doc.select("a.video-item-image.popup-play-video.js");
                date = doc.select("span.fs12");
                imageUrl = doc.select("img[alt]");
                url = doc.select("a.video-item-image.popup-play-video.js");
                urlType = doc.select("a.video-item-image.popup-play-video.js");

                String fullUrl = "";

                for (int i = 0; i < name.size(); i++) {
                    if (urlType.get(i).attr("data-type").equals(youTube)) fullUrl = youTubeUrl + url.get(i).attr("data-video");
                    else if (urlType.get(i).attr("data-type").equals(vimeo)) fullUrl = vimeoUrl + url.get(i).attr("data-video");
                    videoDescribes.add(new VideoDescribe(name.get(i).attr("title"), duration.get(i).text(), date.get(i).text(),
                            imageUrl.get(i + 1).attr("src"), fullUrl));
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


            swipeRefreshLayout.setRefreshing(false);
            if(doc!=null & button==null) {
                ContextThemeWrapper newContext = new ContextThemeWrapper(getBaseContext(), R.style.moreButtonStyle);
                button = new Button(newContext);
                button.setTextColor(getResources().getColor(R.color.md_grey_600));
                button.setText("Больше...");

                ContextThemeWrapper newContext2 = new ContextThemeWrapper(getBaseContext(), R.style.Material_Card_Title);
                textView = new TextView(newContext2);
                textView.setTextColor(getResources().getColor(R.color.md_grey_800));
                textView.setText("Последние добавленные");

                try {
                    listView.addHeaderView(textView);
                    listView.addFooterView(button);
                    listView.setAdapter(videoAdapter);

                } catch (Exception e) {

                }

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(videoAdapter.getVideo(position - 1).getUrl()));
                            startActivity(intent);
                        }

                    }
                });

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/user/AdamuAkeno/videos"));
                        startActivity(intent);
                    }
                });

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            } else if(doc==null) Toast.makeText(getApplicationContext(), R.string.err_msg_updateListView, Toast.LENGTH_LONG).show();


            super.onPostExecute(aVoid);
        }
    }
}
