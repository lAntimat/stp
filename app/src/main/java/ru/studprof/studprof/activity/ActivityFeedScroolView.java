package ru.studprof.studprof.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.IOException;
import java.util.ArrayList;

import ru.studprof.studprof.FillGap.FillGap2BaseActivity;
import ru.studprof.studprof.R;
import ru.studprof.studprof.adapter.BoxAdapter;

public class ActivityFeedScroolView extends FillGap2BaseActivity<ObservableScrollView> implements SwipeRefreshLayout.OnRefreshListener, ObservableScrollViewCallbacks {
    TextView textView;
    Drawer drawerBuilder;
    // благодоря этому классу мы будет разбирать данные на куски
    public Elements title;
    public Elements shortDescribe;
    public Elements tvDate;
    public Elements picture;
    public Elements videoUrl;
    public Elements galleryUrl;
    public Elements profilePicture;
    public Elements profilePicUrl;
    public Elements urlAndNameAndFunctional;
    public Elements nameForParsing;
    public Elements xzForParcing;
    public String profileNameForParsing = "";
    Document doc;
    // то в чем будем хранить данные пока не передадим адаптеру
    public ArrayList<String> titleList = new ArrayList<String>();

    // Listview Adapter для вывода данных
    private ArrayAdapter<String> adapter;
    // List view
    private ListView lv;
    ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    Context ctx;
    BoxAdapter boxAdapter;
    Toolbar toolbar;
    AccountHeader headerResult;
    ListView lvMain;
    TextView mTextView;
    TextView mTitleView;
    TextView tvFunctional;
    TextView tvName;
    HtmlTextView text;
    ProgressBar progressBarFeedBG;
    String youTubeText = "http://youtu.be/";
    String vimeoUrl = "https://vimeo.com/";
    String videoId = "";
    String galleryUrlString = "";


    ImageView ivPlayButton;
    ImageView ivTitlePicture;
    Button btnPhoto;
    Button btnBack;



    final static String youTube = "1";
    final static String vimeo = "3";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_feed_scrool_view);

        //gvMain = (GridView) findViewById(R.id.gridView2);


        //lv=(ListView) findViewById(R.id.listView_feed);

        //swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

//        swipeRefreshLayout.setOnRefreshListener(this);
        // настраиваем список
        //lvMain=(ListView) findViewById(R.id.listView_feed);

        //lvMain.setAdapter(boxAdapter);

        mTextView = (TextView) findViewById(R.id.tvFeed);
        mTitleView = (TextView) findViewById(R.id.title);
        tvFunctional = (TextView) findViewById(R.id.tvFunctional);
        tvName = (TextView) findViewById(R.id.tvHeader);
        progressBarFeedBG = (ProgressBar) findViewById(R.id.progressBarFeedBG);
        ivPlayButton = (ImageView) findViewById(R.id.ivPlay);
        ivTitlePicture = (ImageView) findViewById(R.id.image);

        text = (HtmlTextView) findViewById(R.id.html_text);

        btnPhoto = (Button) findViewById(R.id.btnPhoto);
        btnPhoto.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_image_multiple_white_36dp, 0, 0, 0);
        //btnBack = (Button) findViewById(R.id.btnBack);
        //btnBack.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_toolbal_arrow_white, 0, 0, 0);

        mTitleView.setText("");


        initToolbar();

        //initAccountHeader();

        //initDrawer();
        listeners();
        parseFeed();

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_feed_fill_gap_layout;
    }

    @Override
    protected ObservableScrollView createScrollable() {
        ObservableScrollView scrollView = (ObservableScrollView) findViewById(R.id.scroll);
        scrollView.setScrollViewCallbacks(this);
        return scrollView;
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
                .withHeaderBackground(R.drawable.drawer_bg)
                .build();
    }

    private void initDrawer() {
        drawerBuilder = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName(R.string.nav_menu_item_main)
                                .withIdentifier(1)
                        //.withIcon(GoogleMaterial.Icon.gmd_wb_sunny)
                        ,
                        new PrimaryDrawerItem()
                                .withName(R.string.nav_menu_item_developments)
                                .withIdentifier(1)
                        //.withIcon(R.drawable.ic_favorite_black_18dp)
                        ,
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem()
                                .withName(R.string.nav_menu_item_photo)
                        //.withIcon(R.drawable.ic_accessibility_black_18dp)
                        ,
                        new SecondaryDrawerItem()
                                .withName(R.string.nav_menu_item_team)
                        //.withIcon(R.drawable.ic_history_black_18dp
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                        switch (drawerBuilder.getCurrentSelection()) {
                            case 1:
                                Toast.makeText(getApplicationContext(), "Избранное в разработке",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                //Intent intent2 = new Intent(Main.this, Phylosophs.class);
                                //startActivity(intent2);
                                break;
                            case 4:
                                Toast.makeText(getApplicationContext(), "История Философии в разработке",
                                        Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                })
                .build();
    }


    @Override
    public void onRefresh() {
        parseFeed();
    }


    public void parseFeed() {
        new ParseFeedTask().execute();

    }

    public void openVideo(View view) {

        String url;
        Intent i = new Intent(Intent.ACTION_VIEW);
        switch (videoUrl.get(0).attr("data-type")) {
            case youTube:
                url = youTubeText + videoId;
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            case vimeo:
                url = vimeoUrl + videoId;
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
        }
    }

    public void listeners() {
        ivPlayButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ivTitlePicture.setAlpha(0.6F);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ivTitlePicture.setAlpha(1.0F);
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    ivTitlePicture.setAlpha(0.6F);
                }
                return false;
            }
        });
    }

    public void galleryButton(View view) {
        Intent intent = new Intent(ActivityFeedScroolView.this, PhotoGallery.class);
        intent.putExtra(Constants.FEED_URL_FOR_GALLERY, galleryUrlString);
        startActivity(intent);


    }



    public class ParseFeedTask extends AsyncTask<String, Void, String> {

        String url;
        Boolean docIsNull = true;
        Elements newsRawTag;


        @Override
        protected void onPreExecute() {
            //progressBar = (ProgressBar) findViewById(R.id.progressBar);
            //progressBar.setVisibility(View.VISIBLE);
            Intent intent = getIntent();
            url = intent.getStringExtra(Constants.FEED_URL);
            progressBarFeedBG.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            parseMainInfoBg();


            return null;
        }


        @Override
        protected void onPostExecute(String result) {


            progressBarFeedBG.setVisibility(View.GONE);

            if(doc != null) {
                parseMainInfoPostExecute();
            } else Toast.makeText(getApplicationContext(), R.string.err_msg_updateListView, Toast.LENGTH_LONG).show();


        }

        private void parseMainInfoBg() {
            //Тут пишем основной код
            doc = null;//Здесь хранится будет разобранный html документ
            try {
                //Считываем заглавную страницу http://harrix.org
                doc = Jsoup.connect(url).ignoreContentType(true).get();
            } catch (IOException e) {
                //Если не получилось считать
                e.printStackTrace();

            }

            //Если всё считалось, что вытаскиваем из считанного html документа заголовок
            if (doc != null) {
                docIsNull = false;
                // задаем с какого места, выбратьзаголовки статей
                title = doc.select("h1");  //Заголоков
                shortDescribe = doc.select("p[style]"); //Краткое описание
                tvDate = doc.select("p"); //Дата
                picture = doc.select("img"); //Картинка
                videoUrl = doc.select("div.video-clicker"); //Адрес видео
                galleryUrl = doc.select("a.span8"); //Адрес галереи

                newsRawTag = doc.select("div.text-content");


                nameForParsing = doc.select("div.row.user-short-info.brown");
                profilePicUrl = doc.select("img[src*=\"_mic\"]");

                xzForParcing = doc.select("a");

                if (galleryUrl.size() > 0) galleryUrlString = galleryUrl.get(0).attr("href");
                Log.d("galleryUrlString", galleryUrlString);
                //profilePicture = doc.select("img");
                //feedUrl = doc.select("a[title]");

            } else docIsNull = true;
        }

        private void parseMainInfoPostExecute() {

            String html = newsRawTag.html();

            text.setHtmlFromString(html, new HtmlTextView.RemoteImageGetter());

            /*mWebView.loadDataWithBaseURL(null, newsRawTag.html(),
                    "text/html", "utf-8", null);*/

            if(galleryUrl.size()>0) btnPhoto.setVisibility(View.VISIBLE);
            else btnPhoto.setVisibility(View.GONE);

            String functionalFullText = "";
            String nameFullText = "";

            if (docIsNull == false) {
                String fullText = "";

                // после запроса обновляем листвью
//            swipeRefreshLayout.setRefreshing(false);
                //lv.setAdapter(boxAdapter);
                //progressBar.setVisibility(View.INVISIBLE);
                //Log.d("videoUrl", videoUrl.get(0).attr("data-video"));

                if (videoUrl.size() > 0) {
                    ivPlayButton.setVisibility(View.VISIBLE);
                    videoId = videoUrl.get(0).attr("data-video");
                }

                mTitleView.setText(title.get(0).text());

                for (int i = 0; i < shortDescribe.size(); i++) {
                    if(i==shortDescribe.size() - 1) fullText += shortDescribe.get(i).text();
                    else fullText += shortDescribe.get(i).text() + "\n" + "\n";
                }


                for (int i = 0; i < nameForParsing.size(); i++) {
                    /*profileUrl = new String[nameForParsing.size()];
                    profileName = new String[nameForParsing.size()];
                    profileFunctional = new String[nameForParsing.size()];
                    profilePictureUrl = new String[nameForParsing.size()];*/

                    String name;

                    name = nameForParsing.get(i).text();
                    //name = name.substring(0, name.indexOf(" — "));


                    Log.d("profileName" + i, name);

                    //functionalFullText += name.substring(0, name.indexOf(" ")) + "\n";
                    nameFullText += name + "\n";

                    }

                mTextView.setText(fullText);
                //tvName.setText(nameFullText);
                tvFunctional.setText(nameFullText);

                Uri uri = Uri.parse(picture.get(6).attr("src"));
                Picasso.with(ctx)
                        .load(uri)
                        .into(((ImageView) findViewById(R.id.image)));

                }

            }
        }



}

