package ru.studprof.studprof.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.IOException;

import ru.studprof.studprof.R;

public class ActivityFeedCollapsingToolbar extends AppCompatActivity {

    private AccountHeader headerResult;
    private Drawer result;

    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;

    public Elements title;
    public Elements shortDescribe;
    public Elements tvDate;
    public Elements picture;
    public Elements videoUrl;
    public Elements videoAfterText;
    public Elements galleryUrl;
    public Elements date;
    public Elements profilePicture;
    public Elements profilePicUrl;
    public Elements urlAndNameAndFunctional;
    public Elements nameForParsing;
    public Elements xzForParcing;
    public String profileNameForParsing = "";
    Document doc;
    HtmlTextView text;

    TextView tvFunctional;
    TextView tvName;

    ImageView ivPlayButton;
    Button btnVideoAfterText;
    Button btnRefresh;

    FloatingActionButton fab;

    ImageView imageViewBg;
    Button btnMenu;
    Button btnComments;

    ProgressBar progressBarFeedBG;
    String url;
    String youTubeText = "http://youtu.be/";
    String vimeoUrl = "https://vimeo.com/";
    String videoId = "";
    String videoUrlDataType = "";
    String galleryUrlString = "";
    String feedId = "";
    String countOfPhotos;
    String countOfVisit;

    Boolean openLastActivity = false;

    SharedPreferences sharedPreferences;

    final static String youTube = "1";
    final static String vimeo = "3";

    public static final String FULL_TEXT = "full_text";
    public static final String VIDEO_URL = "video_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_collapsing_toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        fillFab();

        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        btnComments = (Button) findViewById(R.id.btnComments);


        //our toolbar's height has to include the padding of the statusBar so the ColapsingToolbarLayout and the Toolbar can position
        //the arrow/title/... correct
        CollapsingToolbarLayout.LayoutParams lp = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
        lp.height = lp.height;
                //+ UIUtils.getStatusBarHeight(this);
        toolbar.setLayoutParams(lp);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(" ");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.transparent));

        btnMenu = (Button) findViewById(R.id.btnMenu);
        btnMenu.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dotsvertical, 0, 0, 0);
        btnMenu.setClickable(false);
        imageViewBg = (ImageView) findViewById(R.id.backdrop);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        progressBarFeedBG = (ProgressBar) findViewById(R.id.progressBarFeedBG);
        text = (HtmlTextView) findViewById(R.id.html_text);

        tvFunctional = (TextView) findViewById(R.id.tvFunctional);
        tvName = (TextView) findViewById(R.id.tvHeader);

        ivPlayButton = (ImageView) findViewById(R.id.ivPlay);

        btnVideoAfterText = (Button) findViewById(R.id.btnVideoAfterText);
        btnVideoAfterText.setVisibility(View.GONE);



        Intent intent = getIntent();
        url = intent.getStringExtra(Constants.FEED_URL);
        //btnComments.setText("Комментарии (" + intent.getStringExtra(Constants.COMMENTS_COUNT) + ")" );

        if(intent.getBooleanExtra(Constants.OPEN_LAST_ACTIVITY, false)) {
            openLastActivity = true;
        }

        try {
            feedId = url.toLowerCase().replace("http://xn--d1aucecghm.xn--p1ai/feed/", "");
            feedId = feedId.substring(0, feedId.indexOf("-"));
        } catch (Exception e) {

        }




        /*if (Pushbots.sharedInstance().isAnalyticsEnabled()) {
            Pushbots.sharedInstance().reportPushOpened((String) intent.getStringExtra(Constants.PUSHBOTS_ANALYTICS));
        }*/


        listeners();

        if(loadText("text") != null) new ParseFeedTaskOffline().execute();
        new ParseFeedTask().execute();
    }

    /*private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Picasso.with(this).load("https://unsplash.it/600/300/?random").into(imageView);
    }*/


    void saveText(String text, String whatSave) {
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(feedId+whatSave, text);
        ed.commit();
        //Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show();
    }

    String loadText(String whatLoad) {
        sharedPreferences= getPreferences(MODE_PRIVATE);
        String savedText = sharedPreferences.getString(feedId + whatLoad, "");
//        Toast.makeText(this, "Text loaded", Toast.LENGTH_SHORT).show();
        if(savedText!="") return savedText;
        else return null;

    }


    private void fillFab() {
        fab = (FloatingActionButton) findViewById(R.id.floating_action_button);

        /*CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        p.setBehavior(null);
        p.setAnchorId(View.NO_ID);
        fab.setLayoutParams(p);
        fab.setVisibility(View.GONE);*/

        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_multiple_white_24dp));
        //fab.setDrawingCacheBackgroundColor(getResources().getColor(R.color.primary_dark));
        //fab.setBackgroundColor(getResources().getColor(R.color.primary_dark));
        //fab.setRippleColor(getResources().getColor(R.color.primary));

    }

    public void openVideo(View view) {

        try {
            String url;
            Intent i = new Intent(Intent.ACTION_VIEW);
            switch (videoUrlDataType) {
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
        } catch (Exception e) {

        }
    }

    public void openShareMenu(View view) {

        new MaterialDialog.Builder(ActivityFeedCollapsingToolbar.this)
                //.title(R.string.title)
                .theme(Theme.LIGHT)
                .items(R.array.share_items_main)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        switch (which) {
                            case 0:
                                ClipboardManager _clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                _clipboard.setText(url.toLowerCase());

                                Toast.makeText(getApplicationContext(), "Скопировано в буфер обмена", Toast.LENGTH_LONG)
                                        .show();
                                break;

                            case 1:
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                                break;

                            case 2:

                                String urlStudProf = "http://XN--D1AUCECGHM.XN--P1AI";
                                String urlStudProfRussian = "http://студпроф.рф";

                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                String textToSend = (url);
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

    public void refreshFeed(View view) {
        new ParseFeedTask().execute();
    }

    public void openComments(View view) {

        String urlComments = "";
        Intent intent = getIntent();
        urlComments = intent.getStringExtra(Constants.FEED_URL);
        Intent intent2 = new Intent(ActivityFeedCollapsingToolbar.this, CommentsActivity.class);
        intent2.putExtra(Constants.COMMENTS_URL, urlComments);
        startActivity(intent2);

    }

    public void listeners() {
        ivPlayButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    imageViewBg.setAlpha(0.6F);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    imageViewBg.setAlpha(1.0F);
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    imageViewBg.setAlpha(0.6F);
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        /*if(openLastActivity) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }*/
        super.onBackPressed();
    }

    public class ParseFeedTaskOffline extends AsyncTask<String, Void, String> {

        Boolean docIsNull = true;
        Elements newsRawTag;


        @Override
        protected void onPreExecute() {
            //progressBar = (ProgressBar) findViewById(R.id.progressBar);
            //progressBar.setVisibility(View.VISIBLE);

            progressBarFeedBG.setVisibility(View.VISIBLE);
            btnRefresh.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {

            //parseMainInfoBg();


            return null;
        }


        @Override
        protected void onPostExecute(String result) {

            if (loadText("text") != null) {
                String html = loadText("text");

//            Log.d("htmlText", html);
                text.setHtmlFromString(html, new HtmlTextView.RemoteImageGetter());

                btnMenu.setClickable(true);


                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (loadText("galleryUrl") != null) {
                            Intent intent = new Intent(ActivityFeedCollapsingToolbar.this, PhotoGallery.class);
                            intent.putExtra(Constants.FEED_URL_FOR_GALLERY, loadText("galleryUrl"));
                            intent.putExtra(Constants.PHOTO_GALLERY_NAME, loadText("title"));
                            intent.putExtra(Constants.PHOTO_GALLERY_PHOTO_COUNT, loadText("countOfPhotos"));
                            //intent.putExtra(Constants.PHOTO_GALLERY_DATA, data);
                            startActivity(intent);
                        } else
                            Toast.makeText(getApplicationContext(), "Фотографий нет", Toast.LENGTH_SHORT).show();
                    }
                });


                if (loadText("videoId") != null) {
                    ivPlayButton.setVisibility(View.VISIBLE);
                    videoId = loadText("videoId");
                    videoUrlDataType = loadText("data-type");
                }


                /*if(loadText("videoAfterTextUrl")!=null) {
                    btnVideoAfterText.setVisibility(View.VISIBLE);
                    btnVideoAfterText.setText("Смотреть: " + loadText("title"));
                    btnVideoAfterText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(loadText("videoAfterTextUrl")));
                                startActivity(i);
                            } catch (Exception e) {

                            }
                        }
                    });
                }*/

                countOfPhotos = loadText("countOfPhotos");


                collapsingToolbarLayout.setTitle(loadText("title"));

                tvFunctional.setText(loadText("nameFullText"));

                if(loadText("commentsCount")!=null) {
                    if (loadText("commentsCount").equals("0")) btnComments.setText("Комментарии");
                    else btnComments.setText("Комментарии (" + loadText("commentsCount") + ")");
                }


                if (loadText("pictureUrl") != null) {
                    Uri uri = Uri.parse(loadText("pictureUrl"));
                    Picasso.with(getApplicationContext())
                            .load(uri)
                            .into(((ImageView) findViewById(R.id.backdrop)));
                } else {
                    Picasso.with(getApplicationContext())
                            .load(R.drawable.studprofdrawerbg)
                            .into(((ImageView) findViewById(R.id.backdrop)));
                }

                progressBarFeedBG.setVisibility(View.GONE);

            }


        }

        }

    public class ParseFeedTask extends AsyncTask<String, Void, String> {

        Boolean docIsNull = true;
        Elements newsRawTag;
        String videoAfterTextUrl = "";
        String videoAfterTextPictureUrl = "";
        String videoAfterTextTitle = "";
        String commentsCountString[];


        @Override
        protected void onPreExecute() {
            //progressBar = (ProgressBar) findViewById(R.id.progressBar);
            //progressBar.setVisibility(View.VISIBLE);

            if (loadText("text") == null) {
                progressBarFeedBG.setVisibility(View.VISIBLE);
                btnRefresh.setVisibility(View.GONE);
            }
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
            } else {
                if(loadText("text")==null) {
                    Toast.makeText(getApplicationContext(), R.string.err_msg_updateListView, Toast.LENGTH_LONG).show();
                    btnRefresh.setVisibility(View.VISIBLE);
                }
            }

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
                Elements eventSlider = doc.select("div.event-slider-wrap");
                commentsCountString = doc.select("div.pull-right.list-item-icons").text().split(" ");

                // задаем с какого места, выбратьзаголовки статей
                title = doc.select("h1");  //Заголоков
                shortDescribe = doc.select("p[style]"); //Краткое описание
                tvDate = doc.select("p"); //Дата
                picture = eventSlider.select("img"); //Картинка
                videoUrl = doc.select("div.video-clicker"); //Адрес видео
                videoAfterText = doc.select("iframe");
                galleryUrl = doc.select("a.span8"); //Адрес галереи
                date = doc.select("div.tbg.photo-item-description"); //Дата, просмотры, посещения, краткое описание


                newsRawTag = doc.select("div.text-content");


                nameForParsing = doc.select("div.row.user-short-info.brown");
                profilePicUrl = doc.select("img[src*=\"_mic\"]");

                xzForParcing = doc.select("a");




                if (galleryUrl.size() > 0) galleryUrlString = galleryUrl.get(0).attr("href");
                Log.d("galleryUrlString", galleryUrlString);
                //profilePicture = doc.select("img");
                //feedUrl = doc.select("a[title]");

                if(date.size()>0) {
                    String[] parts = date.get(0).text().split(" ");
                    if(parts.length>5) {
                        // Количество фотографий
                        countOfPhotos = parts[3];
                        saveText(countOfPhotos, "countOfPhotos");
                        // Количество посещений
                        countOfVisit = parts[5];
                    }
                }

            } else docIsNull = true;
        }

        private void parseMainInfoPostExecute() {

            String html = newsRawTag.html();
            saveText(html, "text");

            Log.d("htmlText", html);
            text.setHtmlFromString(html, new HtmlTextView.RemoteImageGetter());

            if(commentsCountString.length>1) {
                if(commentsCountString.equals("0")) btnComments.setText("Комментарии" );
                else {
                    btnComments.setText("Комментарии (" + commentsCountString[0] + ")");
                    saveText(commentsCountString[0], "commentsCount");
                }
            }

            /*mWebView.loadDataWithBaseURL(null, newsRawTag.html(),
                    "text/html", "utf-8", null);*/



                /*CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                p.anchorGravity = Gravity.BOTTOM | Gravity.END;
                p.setMargins(16,16,16,16);
                p.setAnchorId(R.id.appbar);
                fab.setLayoutParams(p);*/
                //fab.setColorNormal(getResources().getColor(R.color.primary));
                //fab.setColorPressed(getResources().getColor(R.color.primary_dark));

            btnMenu.setClickable(true);


            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (galleryUrl.size() > 0) {
                        Intent intent = new Intent(ActivityFeedCollapsingToolbar.this, PhotoGallery.class);
                        intent.putExtra(Constants.FEED_URL_FOR_GALLERY, galleryUrlString);
                        intent.putExtra(Constants.PHOTO_GALLERY_NAME, title.get(0).text());
                        intent.putExtra(Constants.PHOTO_GALLERY_PHOTO_COUNT, countOfPhotos);
                        //intent.putExtra(Constants.PHOTO_GALLERY_DATA, data);
                        startActivity(intent);
                    } else
                        Toast.makeText(getApplicationContext(), "Фотографий нет", Toast.LENGTH_SHORT).show();
                }
            });

            saveText(galleryUrlString, "galleryUrl");



            String functionalFullText = "";
            String nameFullText = "";

            if (docIsNull == false) {
                String fullText = "";

                // после запроса обновляем листвью
//            swipeRefreshLayout.setRefreshing(false);
                //lv.setAdapter(boxAdapter);
                //progressBar.setVisibility(View.INVISIBLE);
                //Log.d("videoUrl", videoUrl.get(0).attr("data-video"));


                /*if(videoAfterText.size()>0) {


                    videoAfterTextUrl = videoAfterText.attr("src");
                    videoAfterTextTitle = doc.select("p.tbg").get(doc.select("p.tbg").size()-2).text();
                    btnVideoAfterText.setText("Смотреть: " + title.get(0).text());

                    if(videoAfterTextUrl.contains("https://www.youtube.com/embed")) {
                        videoAfterTextUrl = videoAfterTextUrl.replace("https://www.youtube.com/embed/", youTubeText);
                    } else if(videoAfterTextUrl.contains("www.youtube.com/embed"))
                        videoAfterTextUrl = videoAfterTextUrl.replace("www.youtube.com/embed/", youTubeText);


                    //btnVideoAfterText.setVisibility(View.VISIBLE);
                    btnVideoAfterText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent i = new Intent(Intent.ACTION_VIEW);

                                i.setData(Uri.parse(videoAfterTextUrl));
                                startActivity(i);
                            } catch (Exception e) {

                            }
                        }
                    });

                    //saveText(videoAfterTextUrl, "videoAfterTextUrl");

                    Log.d("TAG", "videoAfterText " + videoAfterTextTitle);


                }*/

                if (videoUrl.size() > 0) {
                    ivPlayButton.setVisibility(View.VISIBLE);
                    videoId = videoUrl.get(0).attr("data-video");
                    saveText(videoId, "videoId");
                    videoUrlDataType = videoUrl.get(0).attr("data-type");
                    saveText(videoUrlDataType, "data-type");
                }



                //mTitleView.setText(title.get(0).text());
                collapsingToolbarLayout.setTitle(title.get(0).text());
                saveText(title.get(0).text(), "title");


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

                    functionalFullText = name.substring(0, name.indexOf(" "));
                    nameFullText += functionalFullText + ":" + name.substring(name.indexOf(" ")) + "\n";

                }

                //mTextView.setText(fullText);
                //tvName.setText(nameFullText);
                tvFunctional.setText(nameFullText);
                saveText(nameFullText, "nameFullText");

                if(picture.size()>0) {
                    Uri uri = Uri.parse(picture.get(0).attr("src"));
                    Picasso.with(getApplicationContext())
                            .load(uri)
                            .into(((ImageView) findViewById(R.id.backdrop)));
                    saveText(picture.get(0).attr("src"), "pictureUrl");

                } else {
                    Picasso.with(getApplicationContext())
                            .load(R.drawable.studprofdrawerbg)
                            .into(((ImageView) findViewById(R.id.backdrop)));
                }

            }

        }

    }

}