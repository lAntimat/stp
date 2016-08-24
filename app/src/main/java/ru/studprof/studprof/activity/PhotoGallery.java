package ru.studprof.studprof.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import ru.studprof.studprof.R;
import ru.studprof.studprof.adapter.GalleryGridViewAdapter;
import ru.studprof.studprof.adapter.ImagesArrayList;

public class PhotoGallery extends AppCompatActivity {


    ArrayList<ImagesArrayList> arrayListImages = new ArrayList<>();
    ArrayList<String> arrayListFullSizeImageUrl = new ArrayList<>();
    ArrayList<String> arrayListImageUrl = new ArrayList<>();
    GalleryGridViewAdapter galleryGridViewAdapter;
    GridView gvMain;
    Elements photoUrl;
    String PhotoBigSizeUrl;
    String urlStudProf = "http://XN--D1AUCECGHM.XN--P1AI";
    String urlPhotos = "/photo/";
    String urlFeed = "";
    String title = "";
    String data;
    WebView webView;

    SharedPreferences sharedPreferences;
    String feedId = "";

    int i = 0;
    int lastValueOfI = 0;
    Document doc;
    boolean imageLoading = false;
    boolean singlePhotoShow = false;
    boolean htmlHandled = false;
    boolean loadFromSaved = false;
    ImageSwitcherPicasso mImageSwitcherPicasso;
    ProgressBar progressBarSingleImage;
    ProgressBar progressBarBg;
    ProgressBar progressBarToolbar;
    ImageView ivRefreshToolbar;
    TextView tvPosition;
    Picasso.Builder picassoBuilder;
    ImageView imgview;
    Toolbar toolbar;
    Timer timer;

    int loadedPhotosCount = 0;
    int countOfPhotos = 0;

    int position = 0;

    GestureDetector gd;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;

    final String TAG = "PhotoGallery";
    final String ARRAY_LIST_IMAGE_URL = "imageUrl";
    final String ARRAY_LIST_FULL_SIZE_IMAGE_URL = "fullSizeImageUrl";
    final String COUNT_OF_PHOTOS = "countOfPhotos";

    //int i = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);
        progressBarSingleImage = (ProgressBar) findViewById(R.id.progressBar);
        progressBarSingleImage.setVisibility(View.INVISIBLE);
        progressBarBg = (ProgressBar) findViewById(R.id.progressBarBg);
        progressBarToolbar = (ProgressBar) findViewById(R.id.progressBarToolbar);
        ivRefreshToolbar = (ImageView) findViewById(R.id.ivRefresh);
        tvPosition = (TextView) findViewById(R.id.tvPosition);
        picassoBuilder = new Picasso.Builder(this);


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
            getSupportActionBar().setTitle(getIntent().getStringExtra(Constants.PHOTO_GALLERY_NAME));
            //getSupportActionBar().setSubtitle(getIntent().getStringExtra(Constants.PHOTO_GALLERY_DATA));
        }

        Animation inAnim = new AlphaAnimation(0, 1);
        inAnim.setDuration(200);
        Animation outAnim = new AlphaAnimation(1, 0);
        outAnim.setDuration(200);


        Intent intent = getIntent();
        urlFeed = intent.getStringExtra(Constants.FEED_URL_FOR_GALLERY);
        Log.d(TAG, urlFeed);

        try {
            feedId = urlFeed.substring(urlFeed.indexOf("photo/") + 6);
            feedId = feedId.substring(0, feedId.indexOf("-"));
            feedId += "photo";

        } catch (Exception e) {

        }





        webView = (WebView) findViewById(R.id.webView);
        //webView.setWebViewClient(new MeWebViewClient());
        webView.getSettings().setLoadsImagesAutomatically(false);
        webView.getSettings().setJavaScriptEnabled(true);


        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlHandler");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(webView, url);
                //webView.loadUrl("javascript:(function(){loadPhotos().click();})()");
                webView.loadUrl("javascript:window.HtmlHandler.handleHtml" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                //parseFeed();
            }
        });

        Boolean dontWork = false;

        galleryGridViewAdapter = new GalleryGridViewAdapter(this, arrayListImages);

        initListView();

        listeners();

        Log.d(TAG, "countOfPhotosIntent " + intent.getStringExtra(Constants.PHOTO_GALLERY_PHOTO_COUNT));
        Log.d(TAG, "countOfPhotosSave " + loadTextString(COUNT_OF_PHOTOS));


        try {
            if (intent.getStringExtra(Constants.PHOTO_GALLERY_PHOTO_COUNT).equals(loadTextString(COUNT_OF_PHOTOS))) {


                ArrayList<String> photoUrl = read(getApplicationContext(), "url");
                ArrayList<String> photoUrlFullSize = read(getApplicationContext(), "fullSizeUrl");

                //og.d(TAG, "photoUrlSize " + photoUrl.size());

                if(loadTextString(COUNT_OF_PHOTOS)!=null) {
                    countOfPhotos = Integer.parseInt(loadTextString(COUNT_OF_PHOTOS));
                } else countOfPhotos = 0;
                arrayListFullSizeImageUrl = photoUrlFullSize;


                for (int i = 0; i < photoUrl.size(); i++) {
                    arrayListImages.add(new ImagesArrayList(photoUrl.get(i), photoUrlFullSize.get(i)));
                }

                loadFromSaved = true;

            } else {
                progressBarBg.setVisibility(View.VISIBLE);
                progressBarToolbar.setVisibility(View.VISIBLE);
                ivRefreshToolbar.setVisibility(View.GONE);
                webView.loadUrl(urlStudProf + urlFeed);
                timer();
            }
        } catch (Exception e) {
            e.printStackTrace();
            progressBarBg.setVisibility(View.VISIBLE);
            progressBarToolbar.setVisibility(View.VISIBLE);
            ivRefreshToolbar.setVisibility(View.GONE);
            webView.loadUrl(urlStudProf + urlFeed);
            timer();
        }



        //"javascript:(function(){loadPhotos().click();})()"


    }

    public void refreshPhotoGallery (View view) {
        /*loadFromSaved = false;
        imageLoading = true;
        //if(timer!=null) timer.cancel();
        arrayListImages.clear();
        progressBarBg.setVisibility(View.VISIBLE);
        progressBarToolbar.setVisibility(View.VISIBLE);
        ivRefreshToolbar.setVisibility(View.GONE);
        //webView.reload();
        webView.loadUrl(urlStudProf + urlFeed);
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(getApplicationContext(), "Загрузка...", Toast.LENGTH_SHORT).show();
                webView.loadUrl("javascript:window.HtmlHandler.handleHtml" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }
        });
        timer();
        //listeners();*/

        saveText(String.valueOf(0), COUNT_OF_PHOTOS);
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);

    }
    public void initListView() {
        gvMain = (GridView) findViewById(R.id.gridView);
        gvMain.setAdapter(galleryGridViewAdapter);
        gvMain.setNumColumns(3);
    }


    void saveText(String text, String whatSave) {
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(feedId + whatSave, text);
        ed.commit();
        //Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show();
    }
    void saveText(Set<String> text, String whatSave) {
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putStringSet(feedId + whatSave, text);
        ed.commit();
        //Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show();
    }

    String loadTextString(String whatLoad) {
        sharedPreferences= getPreferences(MODE_PRIVATE);
        String savedText = sharedPreferences.getString(feedId + whatLoad, null);
//        Toast.makeText(this, "Text loaded", Toast.LENGTH_SHORT).show();
        if(savedText!=null) return savedText;
        else return null;

    }
    Set<String> loadText(String whatLoad) {
        sharedPreferences= getPreferences(MODE_PRIVATE);
        Set<String> savedText = sharedPreferences.getStringSet(feedId + whatLoad, null);
//        Toast.makeText(this, "Text loaded", Toast.LENGTH_SHORT).show();
        if(savedText!=null) return savedText;
        else return null;

    }

    public void write(Context context, Object nameOfClassGetterSetter, String _filename) {
        File directory = new File(context.getFilesDir().getAbsolutePath()
                + File.separator + "studprofTemp");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        //String filename = "AnyName.srl";
        String filename = _filename + feedId + ".srl";
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

    public ArrayList<String> read(Context context, String _filename) {

        ObjectInputStream input = null;
        ArrayList<String> ReturnClass = null;
        String filename = _filename + feedId + ".srl";
        //String filename = "123.srl";
        //filename += ;
        File directory = new File(context.getFilesDir().getAbsolutePath()
                + File.separator + "studprofTemp");
        try {

            Log.i("directoryToStringRead", directory
                    + File.separator + Uri.parse(filename));
            input = new ObjectInputStream(new FileInputStream(directory
                    + File.separator + Uri.parse(filename)));


            ReturnClass = (ArrayList<String>) input.readObject();
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


    public void timer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Your logic here...
                Log.d("TimerTaskJson", "TimerTask");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(getApplicationContext(), "Загрузка...", Toast.LENGTH_SHORT).show();
                        webView.loadUrl("javascript:window.HtmlHandler.handleHtml" +
                                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                    }
                });

                }
        }, 1000, 5000); // End of your timer code.
    }

    public void listeners() {

        gvMain.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                //Log.d(TAG, "Загружено:" + loadedPhotosCount);
                //Log.d(TAG, "Кол-во фоток:" + countOfPhotos);


                /*if(countOfPhotos!=loadedPhotosCount & !imageLoading) {
                    webView.loadUrl("javascript:(function(){loadPhotos().click();})()");
                    webView.loadUrl("javascript:window.HtmlHandler.handleHtml" +
                            "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                    imageLoading = true;
                }*/

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


                loadedPhotosCount = totalItemCount;
                getSupportActionBar().setSubtitle("Загружено " + (loadedPhotosCount) + " из " + countOfPhotos);


                if (firstVisibleItem + visibleItemCount >= totalItemCount & totalItemCount != 0 & !imageLoading &!loadFromSaved) {
                    if(countOfPhotos!=(loadedPhotosCount)) {
                        /*new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                progressBarToolbar.setVisibility(View.VISIBLE);
                            }
                        };*/

                        progressBarToolbar.setVisibility(View.VISIBLE);
                        ivRefreshToolbar.setVisibility(View.GONE);


                        loadedPhotosCount = totalItemCount;

                        webView.loadUrl("javascript:(function(){loadPhotos().click();})()");

                        webView.loadUrl("javascript:window.HtmlHandler.handleHtml" +
                                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");

                        //Log.d("page", String.valueOf(page));
                        imageLoading = true;

                        Log.d("firstVisibleItem", String.valueOf(firstVisibleItem));
                        Log.d("visibleItemCount", String.valueOf(visibleItemCount));
                        Log.d("totalItemCount", String.valueOf(totalItemCount));

                    }
                }


                /*webView.loadUrl("javascript:window.HtmlHandler.handleHtml" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");*/
            }
        });

        gvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //showSinglePhoto();
                //Picasso.with(PhotoGallery.this).load(arrayListImages.get(position).photoBigSizeUrl).into(mImageSwitcherPicasso);
                Intent i = new Intent(PhotoGallery.this, FullScreenViewActivity.class);
                //i.putExtra("position", _postion);
                i.putExtra("position", position);
                i.putStringArrayListExtra("date", arrayListFullSizeImageUrl);
                startActivity(i);
            }
        });

        picassoBuilder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }


        });

        /*progressBarBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("javascript:window.HtmlHandler.handleHtml" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }
        });*/
    }


    public void parseFeed() {
        if(!loadFromSaved)
        new ParseFeedTask().execute();
    }

    public class ParseFeedTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            //progressBar = (ProgressBar) findViewById(R.id.progressBar);
            //progressBar.setVisibility(View.VISIBLE);

            //progressBarFeedBG.setVisibility(View.VISIBLE);
        }


        @Override
        protected String doInBackground(String... params) {
            //Тут пишем основной код


                try {
                    // Now you can, for example, retrieve a div with id="username" here
                    Elements photoUrl = doc.select("img[alt=\"\"]");

                    String[] pullRightListItemIcons = doc.select("div.pull-right.list-item-icons").text().split(" ");

                    countOfPhotos = Integer.parseInt(pullRightListItemIcons[0]);

                    for (; i < photoUrl.size(); i++) {

                        //Ссылка на миниатюру
                        String photoMiniSize = photoUrl.get(i).attr("src");


                        //Переделываем ссылку на оригинал
                        String photoBigSize = photoMiniSize.replace("min_", "");

                        Log.d("Да ну нафиг" + i, photoBigSize);
                        // записываем в аррей лист
                        arrayListImages.add(new ImagesArrayList(photoMiniSize, photoBigSize));
                        arrayListImageUrl.add(photoMiniSize);
                        arrayListFullSizeImageUrl.add(photoBigSize);
//Set the values

                        lastValueOfI = i;
                    }


                    //Set<String> set = new HashSet<String>();
                    //Set<String> set2 = new HashSet<String>();
                    //set.addAll(arrayListImageUrl);
                    //set2.addAll(arrayListFullSizeImageUrl);
                    //saveText(set, ARRAY_LIST_IMAGE_URL);
                    //saveText(set2, ARRAY_LIST_FULL_SIZE_IMAGE_URL);





                    title = doc.select("h1").text();
                    data = doc.select("span.brown.ubuntu.fs18").text();
                    htmlHandled = true;

                }
                catch (NumberFormatException e) {


                }catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.err_msg_updateListView, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            // после запроса обновляем листвью
            //swipeRefreshLayout.setRefreshing(false);
            if(timer!=null)
            timer.cancel();
            imageLoading = false;


                getSupportActionBar().setSubtitle("Загружено " + (loadedPhotosCount) + " из " + countOfPhotos);

                if (loadedPhotosCount == countOfPhotos & loadedPhotosCount!=0) {
                    //Toast.makeText(getApplicationContext(), "Фотографии успешно сохранены в кэш", Toast.LENGTH_LONG).show();
                    write(getApplicationContext(), arrayListImageUrl, "url");
                    write(getApplicationContext(), arrayListFullSizeImageUrl, "fullSizeUrl");
                    saveText(String.valueOf(countOfPhotos), COUNT_OF_PHOTOS);
                }

                galleryGridViewAdapter.notifyDataSetChanged();

                //pageLoading = false;
                //page++;
                //progressBar.setVisibility(View.INVISIBLE);

            if(loadedPhotosCount != 0) {
                progressBarBg.setVisibility(View.INVISIBLE);
                progressBarToolbar.setVisibility(View.GONE);
                ivRefreshToolbar.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle(title);

            }
        }

    }

    public class MyJavaScriptInterface {


        @JavascriptInterface
        public void handleHtml(String html) {
            // Use jsoup on this String here to search for your content.
                doc = Jsoup.parse(html);
                parseFeed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}

