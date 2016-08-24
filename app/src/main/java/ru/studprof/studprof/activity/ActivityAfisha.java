package ru.studprof.studprof.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.studprof.studprof.R;
import ru.studprof.studprof.adapter.Afisha;
import ru.studprof.studprof.adapter.AfishaAdapter;

public class ActivityAfisha extends AppCompatActivity implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {


    Toolbar toolbar;
    Connection.Response res = null;
    WebView webView;
    Document doc;
    String CSRF_TOKEN;
    String cookies;
    Map<String, String> cookiees;
    Map<String, String> loginCookies = null;
    Document docWithCookies;
    TextView textView;
    Connection connectionJsoup;
    String commentsUrl = "http://xn--d1aucecghm.xn--p1ai/feed/9209-subbota-05-03-2016.html";
    ListView lvAfisha;
    AfishaAdapter afishaAdapter;
    ArrayList<Afisha> afishaArrayList = new ArrayList<>();
    Button btnSend;
    Button btnAuth;
    ProgressBar progressBar;
    ProgressBar progressBarToolbar;
    EditText etComment;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView tvLoadingMain;

    Boolean commentfirstLoad = true;

    String item_id = "";
    String commentItem_type = "";

    CircleImageView circleImageView;

    static final String TAG = "CommentsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afisha);


        afishaAdapter = new AfishaAdapter(getApplicationContext(), afishaArrayList);

        lvAfisha = (ListView) findViewById(R.id.lv);
        lvAfisha.setAdapter(afishaAdapter);

        lvAfisha.setOnItemClickListener(this);

        circleImageView = (CircleImageView) findViewById(R.id.ivDataBg);
        tvLoadingMain = (TextView) findViewById(R.id.tvLoadingMain);
        tvLoadingMain.setText("Загрузка...");

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.swipeRefreshColorYellow);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Мои мероприятия");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        //btnSend.setImageDrawable(getResources().getDrawable(R.drawable.ic_send_white_24dp));

        btnAuth = (Button) findViewById(R.id.ivAuth);
        btnAuth.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_account_circle_white_24dp, 0 ,0 ,0);
        //btnAuth.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_circle_white_36dp));
        btnAuth.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBarToolbar = (ProgressBar) findViewById(R.id.progressBarToolbar);
        progressBarToolbar.setVisibility(View.GONE);


        etComment = (EditText) findViewById(R.id.etComment);


        cookiees = new HashMap<>();



        Intent intent = getIntent();
        commentsUrl = "http://xn--d1aucecghm.xn--p1ai/afisha/my";


        cookies = CookieManager.getInstance().getCookie("http://xn--d1aucecghm.xn--p1ai");


        try {
            String[] temp1;
            String[] temp = cookies.split(";");
            for (String ar1 : temp) {
                if (ar1.contains("CSRF_TOKEN")
                        | ar1.contains("_ym_uid")
                        | ar1.contains("_ym_isad")
                        | ar1.contains("auth")
                        | ar1.contains("PHPSESSID")
                        | ar1.contains("_ym_visorc_24318637")
                        | ar1.contains("ad22f67ed99112ca7ab6bb2e2a9cef00")) {
                    temp1 = ar1.split("=");
                    temp1[0] = temp1[0].replace(" ", "");
                    cookiees.put(temp1[0], temp1[1]);
                }
            }

        } catch (Exception e) {

        }

        parseAfisha();

    }

    @Override
    protected void onResume() {
        super.onResume();
        cookies = CookieManager.getInstance().getCookie("http://xn--d1aucecghm.xn--p1ai");


        try {
            String[] temp1;
            String[] temp = cookies.split(";");
            for (String ar1 : temp) {
                if (ar1.contains("CSRF_TOKEN")
                        | ar1.contains("_ym_uid")
                        | ar1.contains("_ym_isad")
                        | ar1.contains("auth")
                        | ar1.contains("PHPSESSID")
                        | ar1.contains("_ym_visorc_24318637")
                        | ar1.contains("ad22f67ed99112ca7ab6bb2e2a9cef00")) {
                    temp1 = ar1.split("=");
                    temp1[0] = temp1[0].replace(" ", "");
                    cookiees.put(temp1[0], temp1[1]);
                }
            }

            Log.d(TAG, "Cookiees " + cookiees);
        } catch (Exception e) {

        }
        if(cookiees.get("auth")==null) {
            btnAuth.setVisibility(View.VISIBLE);
        } else {
            btnAuth.setVisibility(View.GONE);
        }
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

    public void parseAfisha() {
        new ParseAfisha().execute();
    }


    public void authActivity(View view) {
        Intent intent = new Intent(this, AuthorizationActivity.class);
        intent.setAction(Constants.ACTION_AUTH);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        /*new MaterialDialog.Builder(FeedBackActivity.this)
                //.title(R.string.title)
                .theme(Theme.LIGHT)
                .items(R.array.comments_dialog_items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                            switch (which) {
                                case 0:

                                    break;
                                case 1:

                                    break;
                                case 2:

                                    break;
                            }
                    }
                })
                .show();*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                new ParseAfisha().execute();
            }
        });
    }


    public class ParseAfisha extends AsyncTask<String, Void, String> {

        Document document = null;  //Суды парсим страницу
        Elements iventsArea;       //Суды участок с мероприятиями
        Elements iventsRow;       //Суды участок с мероприятием
        Elements title;
        Elements subTitle;
        Elements func;
        Elements func2;
        Elements place;
        Elements data;
        Elements dayOfWeek;

        @Override
        protected void onPreExecute() {



            cookies = CookieManager.getInstance().getCookie("http://xn--d1aucecghm.xn--p1ai");


            try {
                String[] temp1;
                String[] temp = cookies.split(";");
                for (String ar1 : temp) {
                    if (ar1.contains("CSRF_TOKEN")
                            | ar1.contains("_ym_uid")
                            | ar1.contains("_ym_isad")
                            | ar1.contains("auth")
                            | ar1.contains("PHPSESSID")
                            | ar1.contains("_ym_visorc_24318637")
                            | ar1.contains("ad22f67ed99112ca7ab6bb2e2a9cef00")) {
                        temp1 = ar1.split("=");
                        temp1[0] = temp1[0].replace(" ", "");
                        cookiees.put(temp1[0], temp1[1]);
                    }
                }

                Log.d(TAG, "Cookiees " + cookiees);
            } catch (Exception e) {

            }

            afishaArrayList.clear();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String useragent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36 OPR/35.0.2066.92";

            if(cookiees.get("auth")!=null & CSRF_TOKEN==null) {

                try {
                    document = Jsoup.connect(commentsUrl)
                            .userAgent(useragent)
                            .cookie("CSRF_TOKEN", cookiees.get("CSRF_TOKEN"))
                            .cookie("PHPSESSID", cookiees.get("PHPSESSID"))
                            .cookie("ad22f67ed99112ca7ab6bb2e2a9cef00", cookiees.get("ad22f67ed99112ca7ab6bb2e2a9cef00"))
                            .cookie("auth", cookiees.get("auth"))
                            .get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
//               Toast.makeText(getApplicationContext(), "Для начала авторизуйся, пупсик :)", Toast.LENGTH_LONG).show();
            }



          if(document!=null) {

              iventsArea = document.select("section.span16.page");
              iventsRow = iventsArea.select("div.row");

              for (int i = 0; i < iventsRow.size(); i++) {
                  title = iventsRow.get(i).select("h3");
                  subTitle = iventsRow.get(i).select("p.mt10");
                  func = iventsRow.get(i).select("div.row");
                  place = iventsRow.get(i).select("p.mt10.brown");
                  data = iventsRow.get(i).select("div.afisha-date-day");

                  if (title.size()>0) {

                      for (int k = 0; k < title.size(); k++) {
                          func2 = func.get(k+1).select("div.span1");

                          String dataString[] = data.text().split(" ");
                          //[0] День недели
                          //[1] Число
                          String dataStringDay = "";  //Число 99, потому что я киворукий кодер
                          String dataStringData = "99"; //В адаптере есть проверка, чтобы не съехали карточки, а число, чтобы не было пусто
                          if(k==0) {                      //Если одинаковое число, пишеться только один раз
                              if (dataString.length > 0) {
                                  dataStringDay = dataString[0];
                                  dataStringData = dataString[1];
                              }
                          }


                          //Приводим в читабельный вид людей из списка функционала
                          String funcString = "";
                          String forCutFunc = "";
                          for (int j = 0; j < func2.size(); j++) {
                              forCutFunc += func2.get(j).toString();
                              forCutFunc = forCutFunc.substring(forCutFunc.indexOf("title=\"") + 7);
                              forCutFunc = forCutFunc.substring(0, forCutFunc.indexOf("\""));
                              funcString += forCutFunc + "\n";
                          }


                          Log.d(TAG, "Title " + title.text());
                          Log.d(TAG, "SubTitle " + subTitle);
                          Log.d(TAG, "Функционал " + func.text());
                          Log.d(TAG, "Место " + place.text());
                          Log.d(TAG, "Дата" + data.text());

                          afishaArrayList.add(new Afisha(title.get(k).text(), subTitle.get(k).text().replace(place.text(), ""), funcString, place.get(k).text(), dataStringData, dataStringDay));

                      }
                  }
              }
          }


            //Log.d(TAG, "Headers " + res.headers().toString());
            // Log.d(TAG, "Headers " + res.charset());


            return null;
        }

        @Override
        protected void onPostExecute(String s) {


            commentfirstLoad = false;

            if(document == null) {
                Toast.makeText(getApplicationContext(), R.string.err_msg_updateComments, Toast.LENGTH_SHORT).show();
            }

            progressBarToolbar.setVisibility(View.GONE);

            tvLoadingMain.setVisibility(View.INVISIBLE);

            if(iventsArea.size()== 1) {
                tvLoadingMain.setVisibility(View.VISIBLE);
                tvLoadingMain.setGravity(Gravity.CENTER);
                tvLoadingMain.setText("На собрания что ль не ходишь?");
            }

            swipeRefreshLayout.setRefreshing(false);

            try {
                lvAfisha.invalidateViews();
            } catch (Exception e) {

            }

            ((AfishaAdapter) afishaAdapter).setOnItemClickListener(new AfishaAdapter.MyClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    Log.d(TAG, "Item clicked by position: " + position);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(((AfishaAdapter) afishaAdapter).getComments(position).data));
                    startActivity(i);
                }
            });



            //textView.setText(doc2.text());
            //webView.setVisibility(View.GONE);

            super.onPostExecute(s);
        }
    }
}
