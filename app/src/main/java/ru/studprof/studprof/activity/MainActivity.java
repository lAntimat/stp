package ru.studprof.studprof.activity;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.dexafree.materialList.view.MaterialListView;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.paginate.Paginate;
import com.pushbots.push.Pushbots;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import ru.studprof.studprof.Fragments.SearchFragmentMain;
import ru.studprof.studprof.R;
import ru.studprof.studprof.adapter.BoxAdapter;
import ru.studprof.studprof.adapter.CardViewAdapterForFeed;
import ru.studprof.studprof.adapter.DataObject;
import ru.studprof.studprof.adapter.Product;
import ru.studprof.studprof.util.AuthChecker;
import ru.studprof.studprof.util.CustomLoadingListItemCreator;


public class MainActivity extends AppCompatActivity implements  SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener, DatePickerDialog.OnDateSetListener, SharedPreferences.OnSharedPreferenceChangeListener, Paginate.Callbacks {

    TextView tvLoading;
    TextView tvLoadingSearch;
    Drawer drawerBuilder;
    // благодоря этому классу мы будет разбирать данные на куски
    public Elements title;
    public Elements shortDescribe;
    public Elements tvDate;
    public Elements picture;
    public Elements profilePicture;
    public Elements feedUrl;
    public Elements dateWeek;
    public Elements countOfVisit;
    // то в чем будем хранить данные пока не передадим адаптеру
    public ArrayList<String> titleList = new ArrayList<String>();
    // Listview Adapter для вывода данных
    private ArrayAdapter<String> adapter;
    // List view
    private ListView lv;
    ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayoutList;
    private SwipeRefreshLayout swipeRefreshLayoutCard;
    Context ctx;
    ArrayList<Product> products = new ArrayList<Product>();
    ArrayList<Product> productsCard = new ArrayList<Product>();
    ArrayList<Product> productsCardSave = new ArrayList<Product>();
    Product product;
    BoxAdapter boxAdapter;
    Toolbar toolbar;
    AccountHeader headerResult;
    ListView lvMain;
    String urlStudProf = "http://XN--D1AUCECGHM.XN--P1AI";
    String urlStudProfRussian = "http://студпроф.рф";
    String urlFeedWithoutPage = "/feed/index/?page=";
    int page = 1;

    String urlSearchBeforeName = "/feed/search/?query=";
    String urlSearchBeforePage = "&title_only=0&sort=2&date_from=&date_to=&page=";

    String searchRequest = "";

    int searchPage = 1;
    boolean pageLoading = false;
    boolean startOnStart = false;
    boolean pushNotification = true;
    boolean feedFirstLoad = true;
    boolean loadFromSaved = false;
    boolean weAtFirstPosition = true;
    int lastTotalItemCount = 0;

    int lastPositionCardView;

    int firstVisibleItemPositionCardView;

    SearchFragmentMain searchFragment;
    SearchFragmentMain searchFragment2;
    Fragment frag1 = new SearchFragmentMain();
    FragmentTransaction ft;

    //Параметры поиска
    String searchTitleOnly = "0";  //0 - обычно, 1 - только в заголовках
    String searchSort = "1"; //1 - по совпадениям, 2 - по дате

    String lastValueQuery = "";
    int lastValueChoice = 0;

    String listPreference;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private RecyclerView mRecyclerViewSearch;
    private RecyclerView.Adapter mAdapterSearch;
    private RecyclerView.LayoutManager mLayoutManagerSearch;

    private Context mContext;
    private MaterialListView mListView;
    ArrayList results;
    ArrayList resultsSearch;

    private SearchView mSearchView;

    private MenuItem editMenuItem;
    private MenuItem microMenuItem;
    private MenuItem removeMenuItem;
    private MenuItem searchMenuItem;

    private CoordinatorLayout coordinatorLayout;

    SharedPreferences sp;
    SharedPreferences prefs;
    SharedPreferences sharedPreferences;

    Parcelable state;

    AuthChecker authChecker;



    int pastVisiblesItems, visibleItemCount, totalItemCount;

    final int LIST_VIEW = 0;
    final int CARD_VIEW = 1;

    final int WITHOUT_TOOLBAR = 0;
    final int COLLAPSING_TOOLBAR= 1;

    int whatViewNow;// = CARD_VIEW;

    public static final int FEED_REQUEST = 1;  // The request code

    public static final String TAG = "MainActivity";  // The request code



    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear = monthOfYear + 1;
        String month = String.valueOf(monthOfYear);
        String day = String.valueOf(dayOfMonth);
        if((monthOfYear)<10) month = "0" + (monthOfYear);
        if(dayOfMonth<10) day = "0" + dayOfMonth;
        String date = year+"-"+month+"-"+day;
        Log.d("ДАТА", date);
        //SearchFragmentMain searchFragment = (SearchFragmentMain) getFragmentManager().findFragmentById(R.id.fragment1);
        searchFragment.setSearchRequestCalendar(date);

        mSearchView.setQuery("События за " + date, false);
        //mSearchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + "События за " + date + "</font>"));

        searchMenuItem.getActionView().clearFocus();




    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        /*prefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        pushNotification = prefs.getBoolean("notification", true);

        if(pushNotification) {
            Pushbots.sharedInstance().init(this);
            Pushbots.sharedInstance().setRegStatus(true);
            Pushbots.sharedInstance().setNotificationEnabled(false);
            Pushbots.sharedInstance().setCustomHandler(CustomHandler.class);
            Toast.makeText(getApplicationContext(), "Включаем GCM", Toast.LENGTH_SHORT).show();
        } else {
            Pushbots.sharedInstance().setRegStatus(false);
            //Pushbots.sharedInstance().unregister();
            Toast.makeText(getApplicationContext(), "Отключаем GCM", Toast.LENGTH_SHORT).show();

        }*/

    }

    @Override
    public void onLoadMore() {
        //Toast.makeText(getApplicationContext(), "Пумпурум", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean isLoading() {
        return false;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return false;
    }

    public static enum Mode {
        FIRST_CREATE, RETURN, NORMAL, REMOVE, SEARCH, SORT
    }

    public static Mode mode;

    void saveText(String text) {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString("feed", text);
        ed.commit();
        //Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show();
    }

    String loadText() {
        sharedPreferences= getPreferences(MODE_PRIVATE);
        String savedText = sharedPreferences.getString("feed", "");
        //Toast.makeText(this, "Text loaded", Toast.LENGTH_SHORT).show();
        if(!savedText.equals("")) return savedText;
        else return null;

    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Log.d(LOG_TAG, "onRestoreInstanceState");
    }

    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        //Log.d(LOG_TAG, "onSaveInstanceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        //ft.remove(frag1);
        super.onStop();
        prefs.unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.main_cards);
            setTitle("События");

        ctx = getApplicationContext();

        swipeRefreshLayoutList = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_list);
        swipeRefreshLayoutList.setVisibility(View.VISIBLE);

        swipeRefreshLayoutCard = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_card);
        swipeRefreshLayoutCard.setVisibility(View.VISIBLE);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);


        results = new ArrayList<DataObject>();
        resultsSearch = new ArrayList<DataObject>();


        prefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        whatViewNow = Integer.parseInt(prefs.getString("listPref", "0"));
        pushNotification = prefs.getBoolean("notification", true);
        String path = null;
        Uri uri = getIntent().getData();
        if(uri!=null)
            path = uri.getPath();

        if(path!=null) {
            if (path.contains("feed")) {
                Intent intent = new Intent(this, ActivityFeedCollapsingToolbar.class);
                intent.putExtra(Constants.FEED_URL, "http://xn--d1aucecghm.xn--p1ai" + path);
                        startActivity(intent);
            }
        }


        listOnCreate();
        cardOnCreate();


        tvLoading = (TextView) findViewById(R.id.tvLoadingMain);
        tvLoading.setVisibility(View.VISIBLE);
        tvLoadingSearch = (TextView) findViewById(R.id.tvLoadingSearch);


        initToolbar();
        initAccountHeader();
        initDrawer();

        /*if (findViewById(R.id.fragment1) != null) {
            if (savedInstanceState != null) {
                return;
            }
            setupTaskFragment();
        }

        swipeRefreshLayoutListListener();
        swipeRefreshLayoutCardListener();

        forOnStart();*/

        Log.d(TAG, "OnCreate");

        /*} catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Ошибка 113" + e.toString(), Toast.LENGTH_SHORT).show();
        }*/


        forOnStart();

        mAdapter = new CardViewAdapterForFeed(productsCard);
        mRecyclerView.setAdapter(mAdapter);
        if(read(getApplicationContext(), "products") !=null) {
            productsCardSave = read(getApplicationContext(), "products");
            productsCard = read(getApplicationContext(), "products");
            mAdapter = new CardViewAdapterForFeed(productsCard);
            mRecyclerView.setAdapter(mAdapter);
            loadFromSaved = true;
            Log.d(TAG, "read succes");
            cardViewClickListener();
        }
        parseFeed();





        /**Сообщение о том, что это тестовая версия*/

        firstCreateMsg();

        /**Сообщение от PushBot*/

        if(pushNotification) {
            Pushbots.sharedInstance().init(this);
            Pushbots.sharedInstance().setRegStatus(true);
            Pushbots.sharedInstance().setNotificationEnabled(false);
            Pushbots.sharedInstance().setCustomHandler(CustomHandler.class);
        } else {
            Pushbots.sharedInstance().setRegStatus(false);
            //Pushbots.sharedInstance().unregister();
        }


        Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator())
                //.setLoadingListItemSpanSizeLookup(new CustomLoadingListItemSpanLookup())
                .build();
    }

    private void pushWithMessage() {

        Intent notificationIntent = new Intent(ctx, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = ctx.getResources();

        Notification.Builder builder = new Notification.Builder(this);
// оставим только самое необходимое
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Напоминание")
                .setContentText("Пора покормить кота"); // Текст уведомления

        Notification notification = builder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(2, notification);
    }


    @Override
    protected void onStart() {

        drawerBuilder.setSelectionAtPosition(2);

        Log.d(TAG, "OnStart");

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        //if(whatViewNow!=Integer.parseInt(prefs.getString("listPref", "1"))) forOnStart();
        //forOnStart();

        super.onStart();
    }

    @Override
    protected void onResume() {

        StringHolder stringHolderExit = new StringHolder("Выйти");
        StringHolder stringHolderAuth = new StringHolder("Авторизация");

        try {
            authChecker = new AuthChecker(MainActivity.this);
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

        authChecker.parse();


        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }



    public void forOnStart() {

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());



        whatViewNow = Integer.parseInt(prefs.getString("listPref", "1"));
        whatViewNow = CARD_VIEW;

        try {
            listOnCreate();
            cardOnCreate();
        }
        catch (Exception e) {

        }

            //tvLoading = (TextView) findViewById(R.id.tvLoadingMain);
            //tvLoading.setVisibility(View.VISIBLE);
            //tvLoadingSearch = (TextView) findViewById(R.id.tvLoadingSearch);

            if (products != null && products.size() > 0) tvLoading.setVisibility(View.INVISIBLE);

            if (whatViewNow == LIST_VIEW) {
                swipeRefreshLayoutList.setVisibility(View.VISIBLE);
                swipeRefreshLayoutCard.setVisibility(View.INVISIBLE);

            } else if (whatViewNow == CARD_VIEW) {
                swipeRefreshLayoutList.setVisibility(View.INVISIBLE);
                swipeRefreshLayoutCard.setVisibility(View.VISIBLE);
            }

        swipeRefreshLayoutListListener();
        swipeRefreshLayoutCardListener();



        setupTaskFragment();

        }

    private void firstCreateMsg() {
        sp = getSharedPreferences("FIRST_CREATE2",
                Context.MODE_PRIVATE);
        // проверяем, первый ли раз открывается программа
        boolean hasVisited = sp.getBoolean("hasVisited", false);

        if (!hasVisited) {
            // выводим нужную активность
            new MaterialDialog.Builder(MainActivity.this)
                    //.title(R.string.title)
                    .theme(Theme.LIGHT)
                    .content("Внимание: Это бета-версия, в которой возможны незначительные неполадки")
                    //.content("Внимание: Поздравляем Всех девушек, С праздником 8 Марта! Желаем счастья, долголетия и чтобы все было яхшы :)")
                    .contentGravity(GravityEnum.CENTER)
                    .show();
            SharedPreferences.Editor e = sp.edit();
            e.putBoolean("hasVisited", true);
            e.commit(); // не забудьте подтвердить изменения

        }
    }

    protected void setupTaskFragment() {
        // It would be great if we could just do this in onCreate, but setting up the task fragment
        // before calling setContentView() triggers the bug described here:
        //
        // https://code.google.com/p/android/issues/detail?id=22564
        //
        // So we just need to make sure Activities that run async tasks call this setup function
        // before they do.



        //searchFragment = new SearchFragmentMain();
       /* try {
            searchFragment = (SearchFragmentMain) getFragmentManager().findFragmentById(R.id.fragment1);

            ft = getFragmentManager().beginTransaction();


            // If the Fragment is non-null, then it is currently being
            // retained across a configuration change.

            //searchFragment = new SearchFragmentMain();
            ft.add(R.id.fragment1, frag1);
            ft.commit();
            ft.hide(frag1);

        } catch (Exception e) {

        }*/

        ft = getFragmentManager().beginTransaction();



        searchFragment = (SearchFragmentMain) getFragmentManager().findFragmentById(R.id.fragment1);



        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (searchFragment == null) {
            //searchFragment = new SearchFragmentMain();
            ft.add(R.id.fragment1, frag1);
            ft.commit();
            ft.hide(frag1);
        }


    }

    private void initToolbar() {
        //Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
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
                                        Intent intent2 = new Intent(MainActivity.this, ActivityActually.class);
                                        startActivity(intent2);
                                        //finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);
                                break;
                            case 3:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(MainActivity.this, ActivityPhoto.class);
                                        startActivity(intent2);
                                        finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                    }
                                }, 250);

                                break;
                            case 4:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(MainActivity.this, VideoActivity.class);
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
                                        Intent intent2 = new Intent(MainActivity.this, ActivityMradio.class);
                                        startActivity(intent2);
                                        //finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);

                                break;
                            case 6:
                                Intent intent6 = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(intent6);
//
                                break;
                            case 7:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(MainActivity.this, AboutActivity.class);
                                        startActivity(intent2);
                                        //finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);

                                break;
                            case 8:
                                new MaterialDialog.Builder(MainActivity.this)
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
                                    intent7 = new Intent(MainActivity.this, AuthorizationActivity.class);
                                    intent7.setAction(Constants.ACTION_EXIT);
                                    startActivity(intent7);
                                } else {
                                    intent7 = new Intent(MainActivity.this, AuthorizationActivity.class);
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
                                        Intent intent2 = new Intent(MainActivity.this, ActivityActually.class);
                                        startActivity(intent2);
                                        //finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);
                                break;
                            case 3:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(MainActivity.this, ActivityPhoto.class);
                                        startActivity(intent2);
                                        finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);
                                break;
                            case 4:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(MainActivity.this, VideoActivity.class);
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
                                        Intent intent2 = new Intent(MainActivity.this, ActivityMradio.class);
                                        startActivity(intent2);
                                        //finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);
                                break;
                            case 6:
                                Intent intent6 = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(intent6);
//                                ft.remove(frag1);
                                break;
                            case 7:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(MainActivity.this, AboutActivity.class);
                                        startActivity(intent2);
                                        //finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);
                                break;
                            case 8:
                                new MaterialDialog.Builder(MainActivity.this)
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
                                    intent7 = new Intent(MainActivity.this, AuthorizationActivity.class);
                                    intent7.setAction(Constants.ACTION_EXIT);
                                    startActivity(intent7);
                                } else {
                                    intent7 = new Intent(MainActivity.this, AuthorizationActivity.class);
                                    intent7.setAction(Constants.ACTION_AUTH);
                                    startActivity(intent7);
                                }

                                break;
                            case 11:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(MainActivity.this, FeedBackActivity.class);
                                        startActivity(intent2);
                                        //finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);
                        }

                        return false;
                    }
                })
                .withSelectedItemByPosition(2)
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
                                        Intent intent2 = new Intent(MainActivity.this, ActivityActually.class);
                                        startActivity(intent2);
                                        //finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);
                                break;
                            case 3:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(MainActivity.this, ActivityPhoto.class);
                                        startActivity(intent2);
                                        finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);
                                /*if (drawerBuilder != null && drawerBuilder.isDrawerOpen())
                                    drawerBuilder.closeDrawer();*/



                                break;
                            case 4:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent4 = new Intent(MainActivity.this, VideoActivity.class);
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
                                        Intent intent2 = new Intent(MainActivity.this, ActivityMradio.class);
                                        startActivity(intent2);
                                        //finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);

                                break;
                            case 6:
                                Intent intent6 = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(intent6);
                                break;
                            case 7:

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(MainActivity.this, AboutActivity.class);
                                        startActivity(intent2);
                                        //finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                    }
                                }, 250);

                                break;
                            case 8:
                                new MaterialDialog.Builder(MainActivity.this)
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
                                    intent7 = new Intent(MainActivity.this, AuthorizationActivity.class);
                                    intent7.setAction(Constants.ACTION_EXIT);
                                    startActivity(intent7);
                                } else {
                                    intent7 = new Intent(MainActivity.this, AuthorizationActivity.class);
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
                                        Intent intent7 = new Intent(MainActivity.this, ActivityAfisha.class);
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
                                        Intent intent2 = new Intent(MainActivity.this, FeedBackActivity.class);
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
                .withSelectedItemByPosition(2)
                                .build();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        microMenuItem = menu.findItem(R.id.action_micro);
        removeMenuItem = menu.findItem(R.id.action_remove);
        searchMenuItem = menu.findItem(R.id.action_search);
        editMenuItem = menu.findItem(R.id.action_edit);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, this);
        MenuItemCompat.setActionView(searchMenuItem, mSearchView);
        mSearchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.search_hint) + "</font>"));



        startMode(Mode.FIRST_CREATE);

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
        } else if (item.equals(microMenuItem)) {

        //Toast.makeText(getApplicationContext(), "Tadam", Toast.LENGTH_SHORT).show();

            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    MainActivity.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.show(getFragmentManager(), "Выберите дату");

        } else if (item.equals(editMenuItem)) {

            if(dateWeek!=null) {
                new MaterialDialog.Builder(this)
                        .theme(Theme.LIGHT)
                        .contentGravity(GravityEnum.CENTER)
                        .content(dateWeek.get(1).text() + ",\n" + dateWeek.get(0).text())
                                //.content("Начало эпохи Дурина")
                        .show();
            }

        } else if (item.equals(removeMenuItem)) {

            new MaterialDialog.Builder(this)
                    .theme(Theme.LIGHT)
                    .title("Выберите условия поиска")
                    .items(R.array.search_items)
                    .itemsCallbackSingleChoice(lastValueChoice, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            /**
                             * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                             * returning false here won't allow the newly selected radio button to actually be selected.
                             **/

                            lastValueChoice = which;
                            searchSort = String.valueOf(which + 1);
                            if(!lastValueQuery.equals(""))
                                searchFragment.setSearchRequest(lastValueQuery, searchTitleOnly, searchSort);


                            return true;
                        }
                    })
                    .positiveText("Выбрать")
                    .show();



        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (drawerBuilder!= null && drawerBuilder.isDrawerOpen()) {
            drawerBuilder.closeDrawer();
        } else {
            /*Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);*/
            super.onBackPressed();
        }
    }

    public void swipeRefreshLayoutListListener() {
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */

        swipeRefreshLayoutList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //tvLoading.setVisibility(View.VISIBLE);
                page = 1;
                if (products != null) products.clear();
                if (results != null) results.clear();
                //if (mAdapter != null) mAdapter.notifyDataSetChanged();
                //if (boxAdapter != null) boxAdapter.notifyDataSetChanged();
                parseFeed();
            }
        });
        swipeRefreshLayoutList.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            //swipeRefreshLayoutList.setRefreshing(true);
                                            swipeRefreshLayoutList.setColorSchemeResources(R.color.swipeRefreshColorYellow);
                                            //parseFeed();
                                        }
                                    }
        );

    }
    public void swipeRefreshLayoutCardListener() {
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */

        swipeRefreshLayoutCard.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //tvLoading.setVisibility(View.VISIBLE);
                page = 1;
                //if (products != null) products.clear();
                //if (productsCard != null) productsCard.clear();
                //if (results != null) results.clear();
                //if (mAdapter != null) mAdapter.notifyDataSetChanged();
                //if (boxAdapter != null) boxAdapter.notifyDataSetChanged();
                parseFeed();
            }
        });
        swipeRefreshLayoutCard.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            //swipeRefreshLayoutCard.setRefreshing(true);
                                            swipeRefreshLayoutCard.setColorSchemeResources(R.color.swipeRefreshColorYellow);
                                            //parseFeed();
                                        }
                                    }
        );

    }

    public void listOnCreate() {


        tvLoading = (TextView) findViewById(R.id.tvLoadingMain);


        // настраиваем список
        lvMain = (ListView) findViewById(R.id.listView2);
        lvMain.setVisibility(View.VISIBLE);


        // Добавляем данные для ListView
        //adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.product_name, titleList);
        boxAdapter = new BoxAdapter(this, products);
        lvMain.setAdapter(boxAdapter);
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                Intent intent;

                /*if(Integer.parseInt(prefs.getString("feedPref", "0")) == COLLAPSING_TOOLBAR)
                    intent = new Intent(MainActivity.this, ActivityFeedCollapsingToolbar.class);
                else intent = new Intent(MainActivity.this, ActivityFeedScroolView.class);*/
                intent = new Intent(MainActivity.this, ActivityFeedCollapsingToolbar.class);


                intent.putExtra(Constants.FEED_URL, ((CardViewAdapterForFeed) mAdapter).getUrl(position));
                startActivity(intent);

                /*Toast.makeText(getApplicationContext(), "Был выбран пункт " + boxAdapter.getItemId(position),
                        Toast.LENGTH_SHORT).show();*/
            }
        });

        lvMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new MaterialDialog.Builder(MainActivity.this)
                        //.title(R.string.title)
                        .theme(Theme.LIGHT)
                        .items(R.array.share_items_main)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                switch (which) {
                                    case 0:
                                        ClipboardManager _clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                        _clipboard.setText(((CardViewAdapterForFeed) mAdapter).getUrl(position));

                                        Toast.makeText(getApplicationContext(), "Скопировано в буфер обмена ", Toast.LENGTH_LONG)
                                                .show();
                                        break;

                                    case 1:
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(((CardViewAdapterForFeed) mAdapter).getUrl(position)));
                                        startActivity(i);
                                        break;

                                    case 2:

                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.setType("text/plain");
                                        String textToSend = ((CardViewAdapterForFeed) mAdapter).getUrl(position);
                                        textToSend = textToSend.replace(urlStudProf, urlStudProfRussian);
                                        intent.putExtra(Intent.EXTRA_TEXT, textToSend);
                                        try {
                                            startActivity(Intent.createChooser(intent, "Поделиться"));
                                        } catch (android.content.ActivityNotFoundException ex) {
                                            Toast.makeText(getApplicationContext(), "Some error", Toast.LENGTH_SHORT).show();
                                        }

                                        break;
                                }
                            }
                        })
                        .show();

                return true;
            }
        });


        lvMain.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                Log.d("firstVisibleItem", String.valueOf(firstVisibleItem));
                Log.d("visibleItemCount", String.valueOf(visibleItemCount));
                Log.d("totalItemCount", String.valueOf(totalItemCount));

                if (firstVisibleItem + visibleItemCount + 2 > totalItemCount & totalItemCount != 0 & !pageLoading) {
                    parseFeed();
                    lastTotalItemCount = totalItemCount;
                    Log.d("page", String.valueOf(page));
                    pageLoading = true;
                }


            }
        });


    }

    public void cardOnCreate() {



        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        //mAdapter = new CardViewAdapterForFeed(productsCard);
        //mRecyclerView.setAdapter(mAdapter);


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                int firstVisible = layoutManager.findFirstVisibleItemPosition();
                int visibleCount = Math.abs(firstVisible - layoutManager.findLastVisibleItemPosition());
                int itemCount = recyclerView.getAdapter().getItemCount();
                totalItemCount = mLayoutManager.getItemCount();

                if (firstVisible == 0) weAtFirstPosition = true;
                else weAtFirstPosition = false;

                /*Log.d("firstVisible", String.valueOf(firstVisible));
                Log.d("visibleCount", String.valueOf(visibleCount));
                Log.d("itemCount", String.valueOf(itemCount));*/

                lastPositionCardView = firstVisible;

                if ((firstVisible + visibleCount + 3) >= itemCount) {


                    if (!pageLoading & !loadFromSaved) {
                        pageLoading = true;
                        parseFeed();
                    }


                }
            }
        });

    }


    public void parseFeed() {
        //if (whatViewNow == LIST_VIEW) new ParseFeedTask().execute();
        //if (whatViewNow == CARD_VIEW) new ParseFeedTaskCards().execute();
        new ParseFeedTask().execute();
    }



    private void startMode(Mode modeToStart) {

        if (modeToStart == Mode.FIRST_CREATE) {
            //srQuotesRefresher.setEnabled(true);
            removeMenuItem.setVisible(false);
            microMenuItem.setVisible(false);
            searchMenuItem.setVisible(true);
            editMenuItem.setVisible(true);

            if(whatViewNow==LIST_VIEW) {
                swipeRefreshLayoutList.setVisibility(View.VISIBLE);
                swipeRefreshLayoutCard.setVisibility(View.GONE);

            }
            else  if(whatViewNow==CARD_VIEW) {
                swipeRefreshLayoutList.setVisibility(View.GONE);
                swipeRefreshLayoutCard.setVisibility(View.VISIBLE);
            }


            mode = modeToStart;
        }

        else if (modeToStart == Mode.RETURN) {

            mSearchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.search_hint) + "</font>"));

            //srQuotesRefresher.setEnabled(true);
            removeMenuItem.setVisible(false);
            microMenuItem.setVisible(false);
            //sortAbMenuItem.setVisible(false);
            searchMenuItem.setVisible(true);
            editMenuItem.setVisible(true);

            //ingSearch.setVisibility(View.INVISIBLE);

            if(whatViewNow==LIST_VIEW) {
                swipeRefreshLayoutList.setVisibility(View.VISIBLE);
                swipeRefreshLayoutCard.setVisibility(View.GONE);

            }
            else  if(whatViewNow==CARD_VIEW) {
                swipeRefreshLayoutList.setVisibility(View.GONE);
                swipeRefreshLayoutCard.setVisibility(View.VISIBLE);
            }


            cardViewClickListener();

            searchFragment.clearAdapter();


            mode = modeToStart;

        } else if (modeToStart == Mode.REMOVE) {
                searchMenuItem.setVisible(false);
                //editMenuItem.setVisible(false);
                microMenuItem.setVisible(false);
                removeMenuItem.setVisible(true);

            } else if (modeToStart == Mode.SEARCH) {
            removeMenuItem.setVisible(true);
            searchMenuItem.setVisible(false);
            editMenuItem.setVisible(false);
            microMenuItem.setVisible(true);
            //tvLoadingSearch.setVisibility(View.INVISIBLE);
            swipeRefreshLayoutList.setVisibility(View.INVISIBLE);
            swipeRefreshLayoutCard.setVisibility(View.INVISIBLE);
            //setupTaskFragment();
            ft.show(frag1);

            }
            mode = modeToStart;


        }


    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {

        searchFragment.clearAdapter();

        ft.show(frag1);


        startMode(Mode.SEARCH);

        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {

        startMode(Mode.RETURN);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        mSearchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.search_hint) + "</font>"));

        //SearchFragmentMain searchFragment = (SearchFragmentMain) getFragmentManager().findFragmentById(R.id.fragment1);
        searchFragment.setSearchRequest(query, searchTitleOnly, searchSort);
        lastValueQuery = query;


        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public void cardViewClickListener() {
        if (mAdapter != null) {
            ((CardViewAdapterForFeed) mAdapter).setOnItemClickListener(new CardViewAdapterForFeed.MyClickListener() {

                @Override
                public void onItemClick(int position, View v) {
                    SharedPreferences prefs = PreferenceManager
                            .getDefaultSharedPreferences(getBaseContext());

                    Intent intent;

                        /*if(Integer.parseInt(prefs.getString("feedPref", "0")) == COLLAPSING_TOOLBAR) {
                            Log.d("feedPref", prefs.getString("feedPref", "0"));
                            intent = new Intent(MainActivity.this, ActivityFeedCollapsingToolbar.class);
                        } else {
                            intent = new Intent(MainActivity.this, ActivityFeedScroolView.class);
                            Log.d("feedPref", prefs.getString("feedPref", "0"));
                        }*/

                    intent = new Intent(MainActivity.this, ActivityFeedCollapsingToolbar.class);
                    intent.putExtra(Constants.FEED_URL, ((CardViewAdapterForFeed) mAdapter).getUrl(position));
                    intent.putExtra(Constants.COMMENTS_COUNT, ((CardViewAdapterForFeed) mAdapter).getCommentCount(position));
                    startActivity(intent);
                }


            });

            ((CardViewAdapterForFeed) mAdapter).setOnButtonClickListener(new CardViewAdapterForFeed.MyClickListener() {
                @Override
                public void onItemClick(final int position, View v) {
                    //Log.i(LOG_TAG, " Clicked on Button " + position);

                    new MaterialDialog.Builder(MainActivity.this)
                            //.title(R.string.title)
                            .theme(Theme.LIGHT)
                            .items(R.array.share_items_main)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                    switch (which) {
                                        case 0:
                                            ClipboardManager _clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                            _clipboard.setText(((CardViewAdapterForFeed) mAdapter).getUrl(position));

                                            Toast.makeText(getApplicationContext(), "Скопировано в буфер обмена", Toast.LENGTH_LONG)
                                                    .show();
                                            break;

                                        case 1:
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.setData(Uri.parse(((CardViewAdapterForFeed) mAdapter).getUrl(position)));
                                            startActivity(i);
                                            break;

                                        case 2:

                                            Intent intent = new Intent(Intent.ACTION_SEND);
                                            intent.setType("text/plain");
                                            String textToSend = ((CardViewAdapterForFeed) mAdapter).getUrl(position);
                                            textToSend = textToSend.replace(urlStudProf, urlStudProfRussian);
                                            intent.putExtra(Intent.EXTRA_TEXT, textToSend);
                                            try {
                                                startActivity(Intent.createChooser(intent, "Поделиться"));
                                            } catch (android.content.ActivityNotFoundException ex) {
                                                Toast.makeText(getApplicationContext(), "Some error", Toast.LENGTH_SHORT).show();
                                            }

                                            break;
                                    }
                                }
                            })
                            .show();
                }
            });

            ((CardViewAdapterForFeed) mAdapter).setOnBtnCommentClickListener(new CardViewAdapterForFeed.MyClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    Intent intent = new Intent(MainActivity.this, CommentsActivity.class);
                    intent.putExtra(Constants.COMMENTS_URL, ((CardViewAdapterForFeed) mAdapter).getUrl(position));
                    startActivity(intent);
                }
            });

        }


    }


    public void write(Context context, Object nameOfClassGetterSetter, String _filename) {
        File directory = new File(context.getFilesDir().getAbsolutePath()
                + File.separator + "studprofTemp");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        //String filename = "AnyName.srl";
        String filename = _filename + ".srl";
        //String filename = "123.srl";
        ObjectOutput out = null;

        try {
            out = new ObjectOutputStream(new FileOutputStream(directory
                    + File.separator + filename));
            out.writeObject(nameOfClassGetterSetter);
            out.close();
            Log.i("directoryToString", directory
                    + File.separator + filename);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Product> read(Context context, String _filename) {

        ObjectInputStream input = null;
        ArrayList<Product> ReturnClass = null;
        String filename = _filename + ".srl";
        //String filename = "123.srl";
        //filename += ;
        File directory = new File(context.getFilesDir().getAbsolutePath()
                + File.separator + "studprofTemp");
        try {

            Log.i("directoryToStringRead", directory
                    + File.separator + Uri.parse(filename));
            input = new ObjectInputStream(new FileInputStream(directory
                    + File.separator + Uri.parse(filename)));


            ReturnClass = (ArrayList<Product>) input.readObject();
            input.close();

        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ReturnClass;
    }

    public class ParseFeedTask extends AsyncTask<String, Void, String> {

        /**
         * Класс отвечающий за парсинг новостей
         */

        @Override
        protected void onPreExecute() {
            //progressBar = (ProgressBar) findViewById(R.id.progressBar);
            //progressBar.setVisibility(View.VISIBLE);


            if(page==1) {
                swipeRefreshLayoutList.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    swipeRefreshLayoutList.setRefreshing(true);
                                                    //swipeRefreshLayoutList.setColorSchemeResources(R.color.swipeRefreshColorYellow);
                                                    //parseFeed();
                                                }
                                            }
                );
                swipeRefreshLayoutCard.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if(!loadFromSaved)
                                                        swipeRefreshLayoutCard.setRefreshing(true);
                                                    //swipeRefreshLayoutList.setColorSchemeResources(R.color.swipeRefreshColorYellow);
                                                    //parseFeed();
                                                }
                                            }
                );

            }

        }

        @Override
        protected String doInBackground(String... params) {
            //Тут пишем основной код
            Document doc = null;//Здесь хранится будет разобранный html документ
            try {
                //Считываем заглавную страницу http://harrix.org
                doc = Jsoup.connect(urlStudProf + urlFeedWithoutPage + String.valueOf(page)).ignoreContentType(true).get();
            } catch (Exception e) {
                //Если не получилось считать
                e.printStackTrace();

            }

            //Если всё считалось, что вытаскиваем из считанного html документа заголовок
            if (doc != null) {
                // задаем с какого места, я выбрал заголовке статей


                Elements listItemRow = doc.select("article.list-item.row");
                Elements listItemFixed = doc.select("article.list-item-fixed");

                title = listItemRow.select("h3");
                shortDescribe = listItemRow.select("p.mt10");
                tvDate = listItemRow.select("span.list-item-subtitle");
                //picture = doc.select("img[src$=.jpg]");
                picture = listItemRow.select("img");
                //profilePicture = doc.select("img");
                feedUrl = listItemRow.select("a[title]");
                //[0]-верхняя/нижняя, [1]-дата
                dateWeek = doc.select("div.top-date-week");

                countOfVisit = listItemRow.select("div.span10.list-item-body");


                if(page==1) {
                    productsCard.clear();

                    //Парсим закрепленную запись
                    parseListItemFixed(listItemFixed);
                }

                // и в цикле захватываем все данные какие есть на странице
                for (int i = 0; i < title.size(); i++) {

                    String[] counts = countOfVisit.get(i).text().split(" ");
                    String visitCount = counts[1];
                    String commentsCount = counts[0];

                    //Log.d("feedUrl", feedUrlString);
                    // записываем в аррей лист
                    //titleList.add(titles.text());
                    if (page == 1) {
                        pageLoading = true;
                        String feedUrlString = urlStudProf + feedUrl.get(i).attr("href");
                        products.add(new Product(title.get(i).text(), shortDescribe.get(i).text(), tvDate.get(i).text(),
                                picture.get(i).attr("src"), feedUrlString, visitCount, commentsCount));

                        String pictureBig = picture.get(i).attr("src");
                        if(pictureBig.contains("min"))
                            pictureBig = pictureBig.replace("_min", "");
                        productsCard.add(new Product(title.get(i).text(), shortDescribe.get(i).text(), tvDate.get(i).text(),
                                pictureBig, feedUrlString, visitCount, commentsCount));

                    }
                    if (page != 1) {
                        pageLoading = true;
                        String feedUrlString = urlStudProf + feedUrl.get(i).attr("href");
                        products.add(new Product(title.get(i).text(), shortDescribe.get(i).text(), tvDate.get(i).text(),
                                picture.get(i).attr("src"), feedUrlString, visitCount, commentsCount));

                        String pictureBig = picture.get(i).attr("src");
                        pictureBig = pictureBig.replace("_min", "");
                        productsCard.add(new Product(title.get(i).text(), shortDescribe.get(i).text(), tvDate.get(i).text(),
                                pictureBig, feedUrlString, visitCount, commentsCount));
                    }
                }

                write(getApplicationContext(), productsCard, "products");

                page++;

            }
            return null;
        }

        @Override
        protected void onPostExecute(final String result) {


            String[] array = getApplicationContext().getResources().getStringArray(R.array.err_msg_updateListView);
            String randomStr = array[new Random().nextInt(array.length)];

            if (title == null) {
                Toast.makeText(getApplicationContext(), randomStr, Toast.LENGTH_LONG).show();

            } else {

                    // после запроса обновляем листвью
                    swipeRefreshLayoutList.setRefreshing(false);
                    swipeRefreshLayoutCard.setRefreshing(false);
                    tvLoading.setVisibility(View.INVISIBLE);
                boxAdapter.notifyDataSetChanged();
                pageLoading = false;

                if(loadFromSaved) {

                    if (productsCardSave != null & productsCard != null) {
                        if (!productsCardSave.get(0).getName().equals(productsCard.get(0).getName())) {

                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.smoothScrollToPosition(0);
                            /*Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "Есть новые события!", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Показать?", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //mAdapter = new CardViewAdapterForFeed(productsCard);
                                            //mRecyclerView.setAdapter(mAdapter);
                                            //productsCardSave.clear();
                                            productsCardSave = productsCard;
                                            mAdapter.notifyDataSetChanged();
                                            mRecyclerView.smoothScrollToPosition(0);

                                        }
                                    });

                            snackbar.show();*/
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                } else mAdapter.notifyDataSetChanged();


                loadFromSaved = false;

                cardViewClickListener();

                //progressBar.setVisibility(View.INVISIBLE);


            }
        }

        public void parseListItemFixed(Elements _listItemFixed) {

            try {
                Elements _title = _listItemFixed.select("h3");
                Elements _shortDescribe = _listItemFixed.select("p");
                //Elements _tvDate = _listItemFixed.select("span.list-item-subtitle");
                //picture = doc.select("img[src$=.jpg]");
                Elements _picture = _listItemFixed.select("img");
                //profilePicture = doc.select("img");
                Elements _feedUrl = _listItemFixed.select("a[href$=.html]");

                pageLoading = true;
                String feedUrlString = urlStudProf + _feedUrl.get(0).attr("href");
                productsCard.add(new Product(_title.get(0).text(), _shortDescribe.get(0).text(), "Запись закреплена",
                        _picture.get(0).attr("src"), feedUrlString, "", ""));
            } catch (Exception e) {

            }
        }
    }


















    /*public class ParseFeedTaskCards extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            //progressBar = (ProgressBar) findViewById(R.id.progressBar);
            //progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... params) {
            //Тут пишем основной код
            Document doc = null;//Здесь хранится будет разобранный html документ
            try {
                //Считываем заглавную страницу http://harrix.org
                doc = Jsoup.connect(urlStudProf + urlFeedWithoutPage + String.valueOf(page)).ignoreContentType(true).get();

            } catch (IOException e) {
                //Если не получилось считать
                e.printStackTrace();

            }

            //Если всё считалось, что вытаскиваем из считанного html документа заголовок
            if (doc != null) {

                //if(title.size()==0) Toast.makeText(MainActivity.this, "Опс, что - то пошло не так", Toast.LENGTH_LONG).show();

                // задаем с какого места, я выбрал заголовке статей
                title = doc.select("h3");
                shortDescribe = doc.select("p.mt10");
                tvDate = doc.select("span.list-item-subtitle");
                picture = doc.select("img[src$=.jpg]");
                //profilePicture = doc.select("img");
                feedUrl = doc.select("a[title]");

                // и в цикле захватываем все данные какие есть на странице
                for (int i = 0; i < title.size() - 1; i++) {

                    //Log.d("feedUrl", feedUrlString);
                    // записываем в аррей лист
                    //titleList.add(titles.text());
                    if (page == 1) {
                        String feedUrlString = urlStudProf + feedUrl.get(i + 11).attr("href");
                        String pictureBig = picture.get(i + 6).attr("src");
                        pictureBig = pictureBig.replace("_min", "");
                        Product product = new Product(title.get(i + 1).text(), shortDescribe.get(i).text(), tvDate.get(i).text(),
                                pictureBig, feedUrlString);
                        pageLoading = true;

                        results.add(product);

                    }
                    if (page != 1) {
                        String feedUrlString = urlStudProf + feedUrl.get(i + 11).attr("href");
                        String pictureBig = picture.get(i + 5).attr("src");
                        pictureBig = pictureBig.replace("_min", "");

                        Product product = new Product(title.get(i).text(), shortDescribe.get(i).text(), tvDate.get(i).text(),
                                pictureBig, feedUrlString);
                        pageLoading = true;

                        results.add(product);
                    }
                }

                page++;

            }


            return null;
        }


        @Override
        protected void onPostExecute(final String result) {


            if(title == null)
                Toast.makeText(getApplicationContext(), R.string.err_msg_updateListView, Toast.LENGTH_LONG).show();

            swipeRefreshLayout.setRefreshing(false);
            pageLoading = false;
            //progressBar.setVisibility(View.INVISIBLE);
            tvLoading.setVisibility(View.INVISIBLE);

            // после запроса обновляем листвью
            mAdapter.notifyDataSetChanged();



            if (mAdapter != null) {
                ((CardViewAdapterForFeed) mAdapter).setOnItemClickListener(new CardViewAdapterForFeed.MyClickListener() {

                    @Override
                    public void onItemClick(int position, View v) {
                        Intent intent = new Intent(MainActivity.this, ActivityFeedScroolView.class);
                        intent.putExtra(Constants.FEED_URL, ((CardViewAdapterForFeed) mAdapter).getUrl(position));
                        startActivity(intent);

                    }


                });

                ((CardViewAdapterForFeed) mAdapter).setOnButtonClickListener(new CardViewAdapterForFeed.MyClickListener() {
                    @Override
                    public void onItemClick(final int position, View v) {
                        //Log.i(LOG_TAG, " Clicked on Button " + position);

                        new MaterialDialog.Builder(MainActivity.this)
                                //.title(R.string.title)
                                .theme(Theme.LIGHT)
                                .items(R.array.share_items_main)
                                .itemsCallback(new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                        switch (which) {
                                            case 0:
                                            ClipboardManager _clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                            _clipboard.setText(((CardViewAdapterForFeed) mAdapter).getUrl(position));

                                            Toast.makeText(getApplicationContext(), "Скопировано в буфер обмена", Toast.LENGTH_LONG)
                                                    .show();
                                                break;

                                            case 1:
                                                Intent i = new Intent(Intent.ACTION_VIEW);
                                                i.setData(Uri.parse(((CardViewAdapterForFeed) mAdapter).getUrl(position)));
                                                startActivity(i);
                                                break;

                                            case 2:

                                                Intent intent = new Intent(Intent.ACTION_SEND);
                                                intent.setType("text/plain");
                                                String textToSend=((CardViewAdapterForFeed) mAdapter).getUrl(position);
                                                textToSend = textToSend.replace(urlStudProf, urlStudProfRussian);
                                                intent.putExtra(Intent.EXTRA_TEXT, textToSend);
                                                try
                                                {
                                                    startActivity(Intent.createChooser(intent, "Поделиться"));
                                                }
                                                catch (android.content.ActivityNotFoundException ex)
                                                {
                                                    Toast.makeText(getApplicationContext(), "Some error", Toast.LENGTH_SHORT).show();
                                                }

                                                break;
                                        }
                                    }
                                })
                                .show();
                    }
                });


            }
        }

    }

    public class ParseFeedTaskCardSearch extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            //progressBar = (ProgressBar) findViewById(R.id.progressBar);
            //progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... params) {
            //Тут пишем основной код
            Document doc = null;//Здесь хранится будет разобранный html документ
            try {
                //Считываем заглавную страницу http://harrix.org
                String searchRequestAfterEncode = URLEncoder.encode(searchRequest, "UTF-8");

                Log.d("DOC", urlStudProf + urlSearchBeforeName + searchRequestAfterEncode + urlSearchBeforePage + String.valueOf(searchPage));
                doc = Jsoup.connect(urlStudProf + urlSearchBeforeName + searchRequestAfterEncode + urlSearchBeforePage + String.valueOf(searchPage)).ignoreContentType(true).get();
            } catch (IOException e) {
                //Если не получилось считать
                e.printStackTrace();

            }

            Log.d("DOC", doc.text());


            //Если всё считалось, что вытаскиваем из считанного html документа заголовок
            if (doc != null) {
                // задаем с какого места, я выбрал заголовке статей
                titleSearch = doc.select("h3");
                shortDescribeSearch = doc.select("p.mt10");
                tvDateSearch = doc.select("span.list-item-subtitle");
                pictureSearch = doc.select("img[src$=.jpg]");
                //profilePicture = doc.select("img");
                feedUrlSearch = doc.select("a[title]");

                // и в цикле захватываем все данные какие есть на странице
                for (int i = 0; i < titleSearch.size() - 1; i++) {

                    Log.d("feedUrl", titleSearch.text());
                    // записываем в аррей лист
                    //titleList.add(titles.text());

                        String feedUrlString = urlStudProf + feedUrlSearch.get(i + 11).attr("href");
                        String pictureBig = pictureSearch.get(i + 5).attr("src");
                        pictureBig = pictureBig.replace("_min", "");
                        Product product2 = new Product(titleSearch.get(i).text(), shortDescribeSearch.get(i).text(), tvDateSearch.get(i).text(),
                                pictureBig, feedUrlString);
                        pageLoading = true;

                        resultsSearch.add(product2);
                }

                searchPage++;

            }
            return null;
        }

        @Override
        protected void onPostExecute(final String result) {

            // после запроса обновляем листвью
            //swipeRefreshLayout.setRefreshing(false);
            //boxAdapter.notifyDataSetChanged();
            // после запроса обновляем листвью
            swipeRefreshLayout.setRefreshing(false);
            pageLoading = false;
            //progressBar.setVisibility(View.INVISIBLE);
            tvLoading.setVisibility(View.INVISIBLE);
            //tvLoadingSearch.setVisibility(View.VISIBLE);
            //if(resultsSearch.size()<7) tvLoadingSearch.setText("Ничего не найдено :(");

            mAdapterSearch.notifyDataSetChanged();


            if (mAdapterSearch != null) {
                ((CardViewAdapterForFeed) mAdapterSearch).setOnItemClickListener(new CardViewAdapterForFeed.MyClickListener() {

                    @Override
                    public void onItemClick(int position, View v) {
                        Intent intent = new Intent(MainActivity.this, ActivityFeedScroolView.class);
                        intent.putExtra(Constants.FEED_URL, ((CardViewAdapterForFeed) mAdapterSearch).getUrl(position));
                        startActivity(intent);

                    }


                });

                ((CardViewAdapterForFeed) mAdapterSearch).setOnButtonClickListener(new CardViewAdapterForFeed.MyClickListener() {
                    @Override
                    public void onItemClick(final int position, View v) {
                        //Log.i(LOG_TAG, " Clicked on Button " + position);

                        new MaterialDialog.Builder(MainActivity.this)
                                //.title(R.string.title)
                                .theme(Theme.LIGHT)
                                .items(R.array.share_items_main)
                                .itemsCallback(new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                        switch (which) {
                                            case 0:
                                                ClipboardManager _clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                                _clipboard.setText(((CardViewAdapterForFeed) mAdapterSearch).getUrl(position));

                                                Toast.makeText(getApplicationContext(), "Скопировано в буфер обмена", Toast.LENGTH_LONG)
                                                        .show();
                                                break;

                                            case 1:
                                                Intent i = new Intent(Intent.ACTION_VIEW);
                                                i.setData(Uri.parse(((CardViewAdapterForFeed) mAdapterSearch).getUrl(position)));
                                                startActivity(i);
                                                break;

                                            case 2:

                                                Intent intent = new Intent(Intent.ACTION_SEND);
                                                intent.setType("text/plain");
                                                String textToSend=((CardViewAdapterForFeed) mAdapterSearch).getUrl(position);
                                                textToSend = textToSend.replace(urlStudProf, urlStudProfRussian);
                                                intent.putExtra(Intent.EXTRA_TEXT, textToSend);
                                                try
                                                {
                                                    startActivity(Intent.createChooser(intent, "Поделиться"));
                                                }
                                                catch (android.content.ActivityNotFoundException ex)
                                                {
                                                    Toast.makeText(getApplicationContext(), "Some error", Toast.LENGTH_SHORT).show();
                                                }

                                                break;
                                        }
                                    }
                                })
                                .show();
                    }
                });


            }
        }
    }*/
}
