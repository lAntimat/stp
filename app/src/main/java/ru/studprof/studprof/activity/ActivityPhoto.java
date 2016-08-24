package ru.studprof.studprof.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
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
import com.squareup.picasso.RequestCreator;

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
import java.util.List;

import ru.studprof.studprof.Fragments.SearchFragmentPhoto;
import ru.studprof.studprof.R;
import ru.studprof.studprof.adapter.CardViewAdapter;
import ru.studprof.studprof.adapter.DataObject;
import ru.studprof.studprof.util.AuthChecker;
import ru.studprof.studprof.util.CustomLoadingListItemCreator;

public class ActivityPhoto extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener, Paginate.Callbacks {

    private Context mContext;
    private MaterialListView mListView;
    private ProgressBar progressBarPhotoBg;
    String urlStudProf = "http://XN--D1AUCECGHM.XN--P1AI";
    String urlStudProfRussian = "http://студпроф.рф";
    Toolbar toolbar;
    AccountHeader headerResult;
    Drawer drawerBuilder;
    ArrayList<String> arrayListUrl = new ArrayList<>();
    ArrayList<String> albumUrlsArrayList = new ArrayList();
    TextView tvLoading;


    String urlWithOutPage = urlStudProf + "/photo/index/?page=";
    Boolean docIsNull = true;
    Elements title;
    Elements date;
    Elements imageUrl;
    Elements albumUrl;
    List<Card> cards;
    String[] albumUrls;
    String fullText;
    String data;
    String countOfPhotos;
    String countOfVisit;
    String shortDescription = "";
    int a = -1;
    int i2 = 0;
    String[] urls;
    DataObject obj;

    AuthChecker authChecker;

    Fragment frag2 = new SearchFragmentPhoto();
    FragmentTransaction ft;
    SearchFragmentPhoto searchFragmentPhoto;

    SwipeRefreshLayout swipeRefreshLayout;


    //Параметры поиска
    String searchTitleOnly = "0";  //0 - обычно, 1 - только в заголовках
    String searchSort = "2"; //1 - по совпадениям, 2 - по дате

    String lastValueQuery = "";
    int lastValueChoice = 0;


    int page = 1;
    int index = 0;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

    private boolean loading = false;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    ArrayList<DataObject> results;

    String[] myDataset = new String[] {
            "Один","Два","Три"
    };


    String urlPicture = "";


    private SearchView mSearchView;

    private MenuItem editMenuItem;
    private MenuItem microMenuItem;
    private MenuItem removeMenuItem;
    private MenuItem searchMenuItem;

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {

        searchFragmentPhoto.clearAdapter();

        ft.show(frag2);
        startMode(Mode.SEARCH);
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {

        searchFragmentPhoto.clearAdapter();
        startMode(Mode.RETURN);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        mSearchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.search_hint) + "</font>"));


        //SearchFragmentPhoto searchFragment = (SearchFragmentPhoto) getFragmentManager().findFragmentById(R.id.fragment2);
        searchFragmentPhoto.setSearchRequest(query, searchTitleOnly, searchSort);
        lastValueQuery = query;

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onRefresh() {

        //tvLoading.setVisibility(View.VISIBLE);
        page = 1;
        //results.clear();
        //mAdapter.notifyDataSetChanged();
        parseFeed();
    }

    @Override
    public void onBackPressed() {
        if (drawerBuilder!= null && drawerBuilder.isDrawerOpen()) {
            drawerBuilder.closeDrawer();
        } else {
            super.onBackPressed();
            //overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

        }
    }

    @Override
    public void onLoadMore() {

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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_cards);


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setVisibility(View.VISIBLE);


        setupTaskFragment();




        // Save a reference to the context
        mContext = this;

        results = new ArrayList<DataObject>();

        tvLoading = (TextView) findViewById(R.id.tvLoadingPhoto);
        tvLoading.setVisibility(View.VISIBLE);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        mAdapter = new CardViewAdapter(results);
        mRecyclerView.setAdapter(mAdapter);



        // Code to Add an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).addItem(obj, index);

        // Code to remove an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).deleteItem(index);

        // Bind the MaterialListView to a variable
        //mListView = (MaterialListView) findViewById(R.id.material_listview);

        //progressBarPhotoBg = (ProgressBar) findViewById(R.id.progressBarPhotoBg);


        //mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
       // mLayoutManager = new LinearLayoutManager(this);
       // mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        //mAdapter = new CardViewAdapter(myDataset);
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

                /*Log.d("firstVisible", String.valueOf(firstVisible));
                Log.d("visibleCount", String.valueOf(visibleCount));
                Log.d("itemCount", String.valueOf(itemCount));*/

                if ((firstVisible + visibleCount + 3) >= itemCount) {


                    if (!loading) {
                        loading = true;
                        new ParseFeedTask().execute();
                    }


                }
            }
        });








        initToolbar();
        initAccountHeader();
        initDrawer();
        initSwipeRefreshLayout();

        try {
            if (read(getApplicationContext(), "DataObject") != null) {
                results = read(getApplicationContext(), "DataObject");
                mAdapter = new CardViewAdapter(results);
                mRecyclerView.setAdapter(mAdapter);
                swipeRefreshLayout.setRefreshing(false);
                tvLoading.setVisibility(View.INVISIBLE);
            } else {
                swipeRefreshLayout.setRefreshing(true);
                tvLoading.setVisibility(View.VISIBLE);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator())
                        //.setLoadingListItemSpanSizeLookup(new CustomLoadingListItemSpanLookup())
                .build();

        cardsListener();
        parseFeed();

    }

    @Override
    protected void onStart() {
        drawerBuilder.setSelectionAtPosition(3);

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
        super.onStart();
    }

    @Override
    protected void onResume() {
        StringHolder stringHolderExit = new StringHolder("Выйти");
        StringHolder stringHolderAuth = new StringHolder("Авторизация");

        authChecker = new AuthChecker(ActivityPhoto.this);
        if(authChecker.isAuthed()) {
            drawerBuilder.updateName(10, stringHolderExit);
        } else drawerBuilder.updateName(10, stringHolderAuth);
        drawerBuilder.setSelectionAtPosition(3);
        super.onResume();
    }

    /*private ArrayList<DataObject> getDataSet() {
        results = new ArrayList<DataObject>();
        return results;
    }*/

    protected void setupTaskFragment() {
        // It would be great if we could just do this in onCreate, but setting up the task fragment
        // before calling setContentView() triggers the bug described here:
        //
        // https://code.google.com/p/android/issues/detail?id=22564
        //
        // So we just need to make sure Activities that run async tasks call this setup function
        // before they do.

        ft = getFragmentManager().beginTransaction();



        searchFragmentPhoto = (SearchFragmentPhoto) getFragmentManager().findFragmentById(R.id.fragment2);


        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (searchFragmentPhoto == null) {
            searchFragmentPhoto = new SearchFragmentPhoto();
            ft.add(R.id.fragment2, frag2);
            ft.commit();
            ft.hide(frag2);
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
                            case 4:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(getApplicationContext(), VideoActivity.class);
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
                                drawerBuilder.setSelectionAtPosition(3);
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
                            case 4:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent2 = new Intent(getApplicationContext(), VideoActivity.class);
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
                                drawerBuilder.setSelectionAtPosition(3);
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
                            case 4:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent4 = new Intent(getApplicationContext(), VideoActivity.class);
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
                                drawerBuilder.setSelectionAtPosition(3);
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
                .withSelectedItemByPosition(2)
                .build();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_activity, menu);

        microMenuItem = menu.findItem(R.id.action_micro);
        removeMenuItem = menu.findItem(R.id.action_remove);
        searchMenuItem = menu.findItem(R.id.action_search);
        editMenuItem = menu.findItem(R.id.action_edit);
        //sortAbMenuItem = menu.findItem(R.id.action_sort);
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

            Toast.makeText(getApplicationContext(), "Tadam", Toast.LENGTH_SHORT).show();

        } else if (item.equals(editMenuItem)) {


        }

        else if (item.equals(removeMenuItem)) {

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
                                searchFragmentPhoto.setSearchRequest(lastValueQuery, searchTitleOnly, searchSort);


                            return true;
                        }
                    })
                    .positiveText("Выбрать")
                    .show();



        }

        return super.onOptionsItemSelected(item);
    }


    private void startMode(Mode modeToStart) {

        if (modeToStart == Mode.FIRST_CREATE) {
            //srQuotesRefresher.setEnabled(true);
            removeMenuItem.setVisible(false);
            microMenuItem.setVisible(false);
            //sortAbMenuItem.setVisible(false);
            searchMenuItem.setVisible(true);
            editMenuItem.setVisible(false);


            mode = modeToStart;
        }

        else if (modeToStart == Mode.RETURN) {
            //srQuotesRefresher.setEnabled(true);
            removeMenuItem.setVisible(false);
            microMenuItem.setVisible(false);
            //sortAbMenuItem.setVisible(false);
            searchMenuItem.setVisible(true);
            editMenuItem.setVisible(false);

            tvLoading.setVisibility(View.INVISIBLE);

            swipeRefreshLayout.setVisibility(View.VISIBLE);



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
            microMenuItem.setVisible(false);
            //tvLoadingSearch.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setVisibility(View.INVISIBLE);

        }
        mode = modeToStart;


    }

    public void initSwipeRefreshLayout() {
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setColorSchemeResources(R.color.swipeRefreshColorYellow);
                                    }
                                }
        );

    }

    public void cardsListener() {
        if (mAdapter != null) {
            ((CardViewAdapter) mAdapter).setOnItemClickListener(new CardViewAdapter
                    .MyClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    //Log.i(LOG_TAG, " Clicked on Item " + position);

                    DataObject obj2 = (DataObject) results.get(position);

                    Intent intent = new Intent(ActivityPhoto.this, PhotoGallery.class);
                    intent.putExtra(Constants.FEED_URL_FOR_GALLERY, results.get(position).getAlbumUrls());
                    intent.putExtra(Constants.PHOTO_GALLERY_NAME, obj2.getTitle());
                    intent.putExtra(Constants.PHOTO_GALLERY_DATA, obj2.getmData());
                    intent.putExtra(Constants.PHOTO_GALLERY_PHOTO_COUNT, obj2.getmCountOfPhotos());
                    startActivity(intent);

                }
            });

            ((CardViewAdapter) mAdapter).setOnButtonClickListener(new CardViewAdapter.MyClickListener() {
                @Override
                public void onItemClick(final int position, View v) {
                    //Log.i(LOG_TAG, " Clicked on Button " + position);

                    new MaterialDialog.Builder(ActivityPhoto.this)
                            //.title(R.string.title)
                            .theme(Theme.LIGHT)
                            .items(R.array.share_items_main)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                    switch (which) {
                                        case 0:
                                            ClipboardManager _clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                            _clipboard.setText(urlStudProfRussian + results.get(position).getAlbumUrls());

                                            Toast.makeText(getApplicationContext(), "Скопировано в буфер обмена", Toast.LENGTH_LONG)
                                                    .show();
                                            break;

                                        case 1:
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.setData(Uri.parse(urlStudProfRussian + results.get(position).getAlbumUrls()));
                                            startActivity(i);
                                            break;

                                        case 2:

                                            Intent intent = new Intent(Intent.ACTION_SEND);
                                            intent.setType("text/plain");
                                            String textToSend=(urlStudProfRussian + results.get(position).getAlbumUrls());
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

    public ArrayList<DataObject> read(Context context, String _filename) {

        ObjectInputStream input = null;
        ArrayList<DataObject> ReturnClass = null;
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


            ReturnClass = (ArrayList<DataObject>) input.readObject();
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


    public void parseFeed() {
        new ParseFeedTask().execute();
    }


    public class ParseFeedTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            //progressBar = (ProgressBar) findViewById(R.id.progressBar);
            //progressBar.setVisibility(View.VISIBLE);
//            progressBarPhotoBg.setVisibility(View.VISIBLE);

        }


        @Override
        protected String doInBackground(String... params) {

            //Тут пишем основной код
            Document doc = null;//Здесь хранится будет разобранный html документ
            try {
                //Считываем заглавную страницу http://harrix.org
                doc = Jsoup.connect(urlWithOutPage + page).ignoreContentType(true).get();
            } catch (IOException e) {
                //Если не получилось считать
                e.printStackTrace();

            }

            //Если всё считалось, что вытаскиваем из считанного html документа заголовок
            if (doc != null) {

                docIsNull = false;

                title = doc.select("h3");
                date = doc.select("div.tbg.photo-item-description"); //Дата, просмотры, посещения, краткое описание

                imageUrl = doc.select("img[alt]");
                albumUrl = doc.select("a.span8.photo-item-image");

                if(page==1) {
                    results.clear();
                }

                for (int i = 0; i < albumUrl.size(); i++) {

                    albumUrlsArrayList.add(albumUrl.get(i).attr("href"));
                }




                /*cards = new ArrayList<>();
                for (int i = 0, ii = 1; i < title.size(); i++, ii +=9) {
                    cards.add(bigImageCard("", title.get(i).text(), imageUrl.get(ii).attr("src"), albumUrl.get(i).attr("href")));
                }*/

                urls = new String[imageUrl.size()];

                for (int i = 0; i < imageUrl.size(); i++) {
                    Log.d("urls", imageUrl.get(i).attr("src"));
                    if (imageUrl.get(i).attr("src").contains("min_")) {
                        arrayListUrl.add(imageUrl.get(i).attr("src"));
                    }
                }


                for (int i2 = 1; i2 < (imageUrl.size() / 8) - 1; i2++) {
                    for (int i = -8 + 8 * i2; i < 8 * i2; i++) {


                        urls[i2 - 1] += arrayListUrl.get(i) + "trim";
                        //urlPicture += imageUrl.get(i + 1).attr("src") + "trim";

                    }
                }

                //arrayListUrl.add(urlPicture);

//                    Log.d("urls", String.valueOf(urls[i2-1]));


                for (int i = 0; i < title.size(); i++) {

                    if (date.size() > i) fullText = date.get(i).text();

                    //Разделяем текст на части
                    String[] parts = fullText.split(" ");


                    //Собираем дату
                    data = parts[0] + " " + parts[1] + " " + parts[2];

                    if (data.length() > 5) {
                        // Количество фотографий
                        countOfPhotos = parts[3];
                        // Количество посещений
                        countOfVisit = parts[5];
                    }

                    //Собираем краткое описание
                    for (int j = 6; j < parts.length; j++) {
                        shortDescription += parts[j] + " ";
                    }

                    //Находим из текста кто фотографировал
                    String whoPhoto = "";
                    int indexWhoPhoto = fullText.indexOf("Фото");
                    if (indexWhoPhoto != -1) {
                        whoPhoto = fullText.substring(indexWhoPhoto);
                    }

                    //Решем краткое описание от тех кто фотографировал
                    int indexForSubstring = shortDescription.indexOf("Фото");
                    String shortDescriptionClear = shortDescription;

                    if (indexForSubstring != -1)
                        shortDescriptionClear = shortDescriptionClear.substring(0, indexForSubstring);

                    obj = new DataObject(whoPhoto, title.get(i).text(), shortDescriptionClear, data, urls[i], countOfPhotos, countOfVisit, albumUrl.get(i).attr("href"));
                    results.add(obj);


                    shortDescription = "";
                }
                page++;
                arrayListUrl.clear();

                write(getApplicationContext(), results, "DataObject");


            } else docIsNull = true;


            return null;
        }

        @Override
        protected void onPostExecute(final String result) {


            if (title == null)
                Toast.makeText(getApplicationContext(), R.string.err_msg_updateListView, Toast.LENGTH_LONG).show();

            swipeRefreshLayout.setRefreshing(false);
            loading = false;
            tvLoading.setVisibility(View.INVISIBLE);

            mAdapter.notifyDataSetChanged();

            cardsListener();
        }
    }



    private Card bigImageCard(String title, String description, String photoUrl, final String albumUrl) {

        final CardProvider provider = new Card.Builder(this)
                .setTag("BIG_IMAGE_BUTTONS_CARD")
                .withProvider(new CardProvider())
                .setLayout(R.layout.main_cards_row)
                .setTitle(title)
                .setDescription(description)
                .setDrawable(photoUrl)
                .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                    @Override
                    public void onImageConfigure(@NonNull RequestCreator requestCreator) {
                        //requestCreator.fit();
                    }
                })
                .addAction(R.id.left_text_button, new TextViewAction(this)
                        .setText("Открыть")
                        .setTextResourceColor(R.color.black_button)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                Log.d("ADDING", "CARD");

                                Intent intent = new Intent(ActivityPhoto.this, PhotoGallery.class);
                                intent.putExtra(Constants.FEED_URL_FOR_GALLERY, albumUrl);
                                startActivity(intent);
                                //mListView.getAdapter().add(generateNewCard("ss", "ss"));
                                Toast.makeText(mContext, "Открыть альбом", Toast.LENGTH_SHORT).show();
                            }
                        }));
                /*.addAction(R.id.right_text_button, new TextViewAction(this)
                        .setText("right button")
                        .setTextResourceColor(R.color.accent_material_dark)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                Toast.makeText(mContext, "You have pressed the right button", Toast.LENGTH_SHORT).show();
                            }
                        }));*/
        provider.setDividerVisible(true);
        return provider.endConfig().build();

    }
}
