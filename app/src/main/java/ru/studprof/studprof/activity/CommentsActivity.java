package ru.studprof.studprof.activity;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.studprof.studprof.R;
import ru.studprof.studprof.adapter.Comments;
import ru.studprof.studprof.adapter.CommentsAdapter;

public class CommentsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {


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
    ListView lvComments;
    CommentsAdapter commentsAdapter;
    ArrayList<Comments> commentsArrayList = new ArrayList<>();
    Button btnSend;
    Button btnAuth;
    ProgressBar progressBar;
    ProgressBar progressBarToolbar;
    EditText etComment;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView tvLoadingMain;

    Boolean replyMode = false;
    Boolean editMode = false;
    Boolean webViewLoad = false;
    Boolean commentSending = false;
    Boolean commentfirstLoad = true;

    String item_id = "";
    String item_id_vote = "";
    String item_id_edit = "";
    String commentItem_type = "";
    String commentReply_to = "";
    String commentParent = "";
    String commentContent = "";

    CircleImageView circleImageView;

    static final String TAG = "CommentsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


        webView = (WebView) findViewById(R.id.webView);
        //webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        commentsAdapter = new CommentsAdapter(getApplicationContext(), commentsArrayList);

        lvComments = (ListView) findViewById(R.id.lv);
        lvComments.setAdapter(commentsAdapter);

        lvComments.setOnItemClickListener(this);

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
            getSupportActionBar().setTitle("Комментарии");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_send_white_24dp, 0 ,0 ,0);

        //btnSend.setImageDrawable(getResources().getDrawable(R.drawable.ic_send_white_24dp));

        btnAuth = (Button) findViewById(R.id.ivAuth);
        btnAuth.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_account_circle_white_24dp, 0 ,0 ,0);
        //btnAuth.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_circle_white_36dp));
        btnAuth.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBarToolbar = (ProgressBar) findViewById(R.id.progressBarToolbar);
        progressBarToolbar.setVisibility(View.GONE);


        etComment = (EditText) findViewById(R.id.etComment);

        //webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);

        //webView.loadUrl("http://xn--d1aucecghm.xn--p1ai/");
        //webView.loadUrl("http://xn--d1aucecghm.xn--p1ai");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(false);

        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlHandler");

        //webView.loadUrl("Auth.doRequest('vkontakte', '20eeda412653a4036a3be5da9ed209e99f5c8c1c')");
        //webView.loadUrl("javascript:(function(){teamUserDialog(5);})()");

        //webView.loadUrl("javascript:window.HtmlHandler.handleHtml");

        cookiees = new HashMap<>();

        //cookies = CookieManager.getInstance().getCookie("http://xn--d1aucecghm.xn--p1ai");

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                    cookies = CookieManager.getInstance().getCookie(url);
                    webView.loadUrl("javascript:window.HtmlHandler.handleHtml" +
                            "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                    //Log.d("Кукилар белэт", "All the cookies in a string:" + cookies);
                    //Log.d("ЧТО СЕЙЧАС ГРУЗИТСЯ", url);

                //Log.d(TAG, "Загружается: " + url);
                //Log.d(TAG, "Все куки " + cookies);


            }
        });

        String request = "http://oauth.vk.com/authorize?client_id=4017314&redirect_uri=" +
                "http://xn--d1aucecghm.xn--p1ai/auth/vkontakte/login/&response_type=code&v=5.4&state=7a1d47e464aff4ef0ac00ac51f73b4994a7702e5";

        //webView.loadUrl(request);
        //webView.loadUrl("http://xn--d1aucecghm.xn--p1ai");


        Intent intent = getIntent();
        commentsUrl = intent.getStringExtra(Constants.COMMENTS_URL);
        if(commentsUrl.toLowerCase().contains("feed")) {
            item_id = commentsUrl.toLowerCase().replace("http://xn--d1aucecghm.xn--p1ai/feed/", "");
            commentItem_type = "D";
        }
        else if(commentsUrl.toLowerCase().contains("afisha")) {
            item_id = commentsUrl.toLowerCase().replace("http://xn--d1aucecghm.xn--p1ai/afisha/", "");
            commentItem_type = "L";
        }
        try {
            item_id = item_id.substring(0, item_id.indexOf("-"));
            Log.d(TAG, "item_id " + item_id);
        } catch (Exception e) {

        }

        cookies = CookieManager.getInstance().getCookie(commentsUrl);


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

            //Log.d(TAG, "Cookiees " + cookiees);
        } catch (Exception e) {

        }

        parseComments();

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

    public void parseComments() {
        //Intent intent = getIntent();

        //commentsUrl = intent.getStringExtra(Constants.COMMENTS_URL);
        if(commentsUrl.toLowerCase().contains("replies")) new ParseAnswers().execute();
        else new ParseComments().execute();
    }

    public void sendComment(View view) {
        if(!commentSending) {
            commentContent = etComment.getText().toString();
            if (commentContent.length() > 0) {
                commentSending = true;
                if (replyMode) {
                    progressBarToolbar.setVisibility(View.VISIBLE);
                    new ReplyComment().execute();
                } else if (editMode) {
                    progressBarToolbar.setVisibility(View.VISIBLE);
                    new EditComment().execute();
                } else {
                    progressBarToolbar.setVisibility(View.VISIBLE);
                    new SendComment().execute();
                }
                parseComments();
            } else {
                Toast.makeText(getApplicationContext(), "Введите комментарий!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void authActivity(View view) {
        Intent intent = new Intent(this, AuthorizationActivity.class);
        intent.setAction(Constants.ACTION_AUTH);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        new MaterialDialog.Builder(CommentsActivity.this)
                //.title(R.string.title)
                .theme(Theme.LIGHT)
                .items(R.array.comments_dialog_items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                            switch (which) {
                                case 0:
                                    replyMode = true;
                                    editMode = false;
                                    if (commentsArrayList.get(position).commentIdParent.equals("0")) {
                                        commentReply_to = commentsArrayList.get(position).commentId;
                                        commentParent = commentsArrayList.get(position).commentId;
                                    } else {
                                        commentReply_to = commentsArrayList.get(position).commentId;
                                        commentParent = commentsArrayList.get(position).commentIdParent;
                                    }
                                    String[] nameReply = commentsArrayList.get(position).name.split(" ");
                                    etComment.setText(nameReply[0] + ", ");
                                    etComment.setSelection(etComment.length());
                                    etComment.setHint("Ответ для: " + nameReply[0]);
                                    break;
                                case 1:
                                    editMode = true;
                                    replyMode = false;
                                    etComment.setHint("Режим редактирования");
                                    item_id_edit = commentsArrayList.get(position).commentId;
                                    etComment.setText(commentsArrayList.get(position).comment);
                                    etComment.setSelection(etComment.length());

                                    break;
                                case 2:
                                    editMode = false;
                                    replyMode = false;

                                    item_id_vote = commentsArrayList.get(position).commentId;

                                    new Vote().execute();
                                    break;
                            }
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        if(etComment.getHint().toString().contains("Ответ для")) {
            etComment.setHint("Комментарий");
            replyMode = false;
        } else if(etComment.getHint().toString().contains("Режим")) {
            etComment.setHint("Комментарий");
            editMode = false;
        }
        else super.onBackPressed();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                new ParseComments().execute();
            }
        });
    }

    public class SendComment extends AsyncTask<String, Void, String> {

        Document doc2 = null;
        Document document = null;
        String commentResponce = "";
        String postStatusCode;

        @Override
        protected void onPreExecute() {


            //String request = "http://oauth.vk.com/authorize?client_id=4017314&redirect_uri=" +
                  //  "http://xn--d1aucecghm.xn--p1ai/auth/vkontakte/login/&response_type=code&v=5.4&state=7a1d47e464aff4ef0ac00ac51f73b4994a7702e5" + CSRF_TOKEN;

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String useragent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36 OPR/35.0.2066.92";


            if(cookiees.get("auth")!=null) {
                try {
                    res = Jsoup.connect("http://xn--d1aucecghm.xn--p1ai/comment/post/")
                            //.header("Accept", "application/json, text/javascript, */*; q=0.01")
                            //.header("Accept-Encoding", "gzip, deflate, lzma")
                            //.header("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4")
                            //.header("Connection", "keep-alive")
                            //.header("Content-Length", "207")
                            //.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                            //.header("Cookie", cookies)
                            //.header("Host", "xn--d1aucecghm.xn--p1ai")
                            //.header("Origin", "http://xn--d1aucecghm.xn--p1ai")
                            //.header("Referer", "http://xn--d1aucecghm.xn--p1ai/feed/9207-v-preddverii-lyubimykh-prazdnikov.html")
                            //.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36 OPR/35.0.2066.92")
                            //.header("X-Requested-With", "XMLHttpRequest")
                            .userAgent(useragent)
                            .referrer(commentsUrl)
                            .cookie("CSRF_TOKEN", cookiees.get("CSRF_TOKEN"))
                            .cookie("PHPSESSID", cookiees.get("PHPSESSID"))
                                    //.cookie("_ym_isad", cookiees.get("_ym_isad"))
                                    //.cookie("_ym_uid", cookiees.get("_ym_uid"))
                                    //.cookie("_ym_visorc_24318637", cookiees.get("_ym_visorc_24318637"))
                            .cookie("ad22f67ed99112ca7ab6bb2e2a9cef00", cookiees.get("ad22f67ed99112ca7ab6bb2e2a9cef00"))
                            .cookie("auth", cookiees.get("auth"))
                            .data("Comment[item_id]", item_id)
                            .data("Comment[item_type]", commentItem_type)
                            .data("CSRF_TOKEN", CSRF_TOKEN)
                            .data("Comment[author_name]", "")
                            .data("Comment[verifyCode]", "")
                            .data("Comment[attachment]", "")
                            .data("Comment[content]", commentContent)
                            .method(Connection.Method.POST)
                            .execute();
                } catch (IOException e) {
                    e.printStackTrace();

                    runOnUiThread(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.err_msg_sendComment, Toast.LENGTH_SHORT).show();

                        }
                    }));

                    Log.d("Блять", "блэт");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                commentContent = "";


                try {
                    commentResponce = res.parse().text();
                } catch (NullPointerException e) {
                    e.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Log.d(TAG, "Status message " + res.statusMessage());
                //Log.d(TAG, "Status code " + res.statusCode());
                //Log.d(TAG, "res text " + commentResponce);

            } else {
                runOnUiThread(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.err_msg_sendComment_authorisation, Toast.LENGTH_SHORT).show();

                    }
                }));
            }

            /*try {
                doc2 = Jsoup.connect("http://xn--d1aucecghm.xn--p1ai")
                        .cookie("CSRF_TOKEN", cookiees.get("CSRF_TOKEN"))
                        .cookie("PHPSESSID", cookiees.get("PHPSESSID"))
                        .cookie("_ym_isad", cookiees.get("_ym_isad"))
                        .cookie("_ym_uid", cookiees.get("_ym_uid"))
                        .cookie("_ym_visorc_24318637", cookiees.get("_ym_visorc_24318637"))
                        .cookie("ad22f67ed99112ca7ab6bb2e2a9cef00", cookiees.get("ad22f67ed99112ca7ab6bb2e2a9cef00"))
                        .cookie("auth", cookiees.get("auth"))
                        .data("CSRF_TOKEN", CSRF_TOKEN)
                        .userAgent(useragent)
                        .post();
            } catch (IOException e) {
                e.printStackTrace();
            }*/



            //Log.d(TAG, "Headers " + res.headers().toString());
            // Log.d(TAG, "Headers " + res.charset());


            /*try {
                res = Jsoup.connect("http://www.deeproute.com/")
                        .data("cookieexists", "false")
                        .data("name", user)
                        .data("password", pswd)
                        .method(Connection.Method.POST)
                        .execute();

            } catch (IOException e) {

                e.printStackTrace();
            }*/

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

//            Log.d("Глянем что мы напарсили", doc2.select("h4").text());

            /*webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    webView.loadUrl("javascript:(function() { " +
                            "document.getElementsByTagName('header')[0].style.display=\nnone\n; " +
                            "})()");
                }
            });
            webView.loadUrl("");*/
            //textView.setText(doc2.text());
            //webView.setVisibility(View.GONE);



            Log.d(TAG, "СommentResponce " + commentResponce);

            postStatusCode = "";

            try {
                JsonObject responseJsonObjects = new JsonParser().parse(commentResponce).getAsJsonObject();
                postStatusCode = responseJsonObjects.get("status").getAsString();

                Log.d(TAG, "Status code " + postStatusCode);

                switch (postStatusCode) {
                    case "0":
                        Toast.makeText(getApplicationContext(), "Что то пошло не так!", Toast.LENGTH_SHORT).show();

                        break;
                    case "1":

                        break;
                    case "2":
                        Toast.makeText(getApplicationContext(), R.string.success_msg_sendComment, Toast.LENGTH_SHORT).show();
                        etComment.setText("");
                        etComment.clearFocus();
                        InputMethodManager imm = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(
                                etComment.getWindowToken(), 0);
                        break;
                }
            } catch (IllegalStateException e) {
                //Toast.makeText(getApplicationContext(), R.string.err_msg_sendComment_authorisation, Toast.LENGTH_LONG).show();
            }
            catch (Exception e) {
                /*if(cookiees.get("ad22f67ed99112ca7ab6bb2e2a9cef00")==null & !postStatusCode.equals(""))
                     Toast.makeText(getApplicationContext(), R.string.err_msg_sendComment_authorisation, Toast.LENGTH_LONG).show();
                else Toast.makeText(getApplicationContext(), R.string.err_msg_sendComment, Toast.LENGTH_LONG).show();*/
                e.printStackTrace();
            }
            commentSending = false;


            super.onPostExecute(s);
        }
    }
    public class ReplyComment extends AsyncTask<String, Void, String> {

        Document doc2 = null;
        Document document = null;
        String commentResponce = "";
        String postStatusCode;

        @Override
        protected void onPreExecute() {


            //String request = "http://oauth.vk.com/authorize?client_id=4017314&redirect_uri=" +
                    //"http://xn--d1aucecghm.xn--p1ai/auth/vkontakte/login/&response_type=code&v=5.4&state=7a1d47e464aff4ef0ac00ac51f73b4994a7702e5" + CSRF_TOKEN;

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String useragent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36 OPR/35.0.2066.92";


            if(cookiees.get("auth")!=null) {

                try {
                    res = Jsoup.connect("http://xn--d1aucecghm.xn--p1ai/comment/post/")
                            .userAgent(useragent)
                            .referrer(commentsUrl)
                            .cookie("CSRF_TOKEN", cookiees.get("CSRF_TOKEN"))
                            .cookie("PHPSESSID", cookiees.get("PHPSESSID"))
//                        .cookie("_ym_isad", cookiees.get("_ym_isad"))
                                    //.cookie("_ym_uid", cookiees.get("_ym_uid"))
                                    //.cookie("_ym_visorc_24318637", cookiees.get("_ym_visorc_24318637"))
                            .cookie("ad22f67ed99112ca7ab6bb2e2a9cef00", cookiees.get("ad22f67ed99112ca7ab6bb2e2a9cef00"))
                            .cookie("auth", cookiees.get("auth"))
                            .data("Comment[item_id]", item_id)
                            .data("Comment[item_type]", commentItem_type)
                            .data("CSRF_TOKEN", CSRF_TOKEN)
                            .data("Comment[reply_to]", commentReply_to)
                            .data("Comment[parent]", commentParent)
                            .data("Comment[author_name]", "")
                            .data("Comment[verifyCode]", "")
                            .data("Comment[attachment]", "")
                            .data("Comment[content]", commentContent)
                            .method(Connection.Method.POST)
                            .execute();
                } catch (IOException e) {
                    e.printStackTrace();

                    runOnUiThread(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.err_msg_sendComment, Toast.LENGTH_SHORT).show();

                        }
                    }));

                    Log.d("Блять", "блэт");
                } catch (Exception e) {
                    e.printStackTrace();

                }

                commentContent = "";


                try {
                    commentResponce = res.parse().text();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                runOnUiThread(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.err_msg_sendComment_authorisation, Toast.LENGTH_SHORT).show();

                    }
                }));
            }

            //Log.d(TAG, "Status message " + res.statusMessage());
            //Log.d(TAG, "Status code " + res.statusCode());
            //Log.d(TAG, "res text " + commentResponce);



            /*try {
                doc2 = Jsoup.connect("http://xn--d1aucecghm.xn--p1ai")
                        .cookie("CSRF_TOKEN", cookiees.get("CSRF_TOKEN"))
                        .cookie("PHPSESSID", cookiees.get("PHPSESSID"))
                        .cookie("_ym_isad", cookiees.get("_ym_isad"))
                        .cookie("_ym_uid", cookiees.get("_ym_uid"))
                        .cookie("_ym_visorc_24318637", cookiees.get("_ym_visorc_24318637"))
                        .cookie("ad22f67ed99112ca7ab6bb2e2a9cef00", cookiees.get("ad22f67ed99112ca7ab6bb2e2a9cef00"))
                        .cookie("auth", cookiees.get("auth"))
                        .data("CSRF_TOKEN", CSRF_TOKEN)
                        .userAgent(useragent)
                        .post();
            } catch (IOException e) {
                e.printStackTrace();
            }*/



            //Log.d(TAG, "Headers " + res.headers().toString());
            // Log.d(TAG, "Headers " + res.charset());


            /*try {
                res = Jsoup.connect("http://www.deeproute.com/")
                        .data("cookieexists", "false")
                        .data("name", user)
                        .data("password", pswd)
                        .method(Connection.Method.POST)
                        .execute();

            } catch (IOException e) {

                e.printStackTrace();
            }*/

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

//            Log.d("Глянем что мы напарсили", doc2.select("h4").text());

            /*webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    webView.loadUrl("javascript:(function() { " +
                            "document.getElementsByTagName('header')[0].style.display=\nnone\n; " +
                            "})()");
                }
            });
            webView.loadUrl("");*/
            //textView.setText(doc2.text());
            //webView.setVisibility(View.GONE);


            postStatusCode = "";

            try {
                JsonObject responseJsonObjects = new JsonParser().parse(commentResponce).getAsJsonObject();
                postStatusCode = responseJsonObjects.get("status").getAsString();

                Log.d(TAG, "Status code reply " + postStatusCode);

                switch (postStatusCode) {
                    case "0":
                        Toast.makeText(getApplicationContext(), "Необходимо авторизоваться", Toast.LENGTH_SHORT).show();

                        break;
                    case "1":

                        break;
                    case "2":
                        Toast.makeText(getApplicationContext(), R.string.success_msg_sendComment, Toast.LENGTH_SHORT).show();
                        etComment.setHint("Комментарий");
                        replyMode = false;
                        etComment.setText("");
                        etComment.clearFocus();
                        InputMethodManager imm = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(
                                etComment.getWindowToken(), 0);
                        break;
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), R.string.err_msg_sendComment, Toast.LENGTH_SHORT).show();
            }

            commentSending = false;


            super.onPostExecute(s);
        }
    }
    public class EditComment extends AsyncTask<String, Void, String> {

        Document doc2 = null;
        Document document = null;
        String commentResponce = "";
        String postStatusCode;

        @Override
        protected void onPreExecute() {


            String request = "http://oauth.vk.com/authorize?client_id=4017314&redirect_uri=" +
                    "http://xn--d1aucecghm.xn--p1ai/auth/vkontakte/login/&response_type=code&v=5.4&state=7a1d47e464aff4ef0ac00ac51f73b4994a7702e5" + CSRF_TOKEN;

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String useragent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36 OPR/35.0.2066.92";



            if(cookiees.get("auth")!=null) {
                //if(cookiees.get("_ym_uid")!=null & cookiees.get("_ym_isad")!=null) {
                    try {
                        res = Jsoup.connect("http://xn--d1aucecghm.xn--p1ai/comment/edit/")
                                //.header("Accept", "application/json, text/javascript, */*; q=0.01")
                                //.header("Accept-Encoding", "gzip, deflate, lzma")
                                //.header("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4")
                                //.header("Connection", "keep-alive")
                                //.header("Content-Length", "207")
                                //.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                                //.header("Cookie", cookies)
                                //.header("Host", "xn--d1aucecghm.xn--p1ai")
                                //.header("Origin", "http://xn--d1aucecghm.xn--p1ai")
                                //.header("Referer", "http://xn--d1aucecghm.xn--p1ai/feed/9207-v-preddverii-lyubimykh-prazdnikov.html")
                                //.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36 OPR/35.0.2066.92")
                                //.header("X-Requested-With", "XMLHttpRequest")
                                .userAgent(useragent)
                                .referrer(commentsUrl)
                                .cookie("CSRF_TOKEN", cookiees.get("CSRF_TOKEN"))
                                .cookie("PHPSESSID", cookiees.get("PHPSESSID"))
                                //.cookie("_ym_uid", cookiees.get("_ym_uid"))
                                //.cookie("_ym_isad", cookiees.get("_ym_isad"))
//                                .cookie("_ym_visorc_24318637", cookiees.get("_ym_visorc_24318637"))
                                .cookie("ad22f67ed99112ca7ab6bb2e2a9cef00", cookiees.get("ad22f67ed99112ca7ab6bb2e2a9cef00"))
                                .cookie("auth", cookiees.get("auth"))
                                .data("CSRF_TOKEN", CSRF_TOKEN)
                                .data("content", commentContent)
                                .data("attachment", "")
                                .data("id", item_id_edit)
                                .method(Connection.Method.POST)
                                .execute();

                        Log.d(TAG, "item_id " + item_id_edit);

                    } catch (IOException e) {
                        e.printStackTrace();

                        runOnUiThread(new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), R.string.err_msg_sendComment, Toast.LENGTH_SHORT).show();

                            }
                        }));

                        Log.d("Блять", "блэт");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        commentResponce = res.parse().text();
                    } catch (NullPointerException e) {
                        e.printStackTrace();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //Log.d(TAG, "Status message " + res.statusMessage());
                    //Log.d(TAG, "Status code " + res.statusCode());
                    //Log.d(TAG, "res text " + commentResponce);
                //}

            } else {
                runOnUiThread(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.err_msg_sendComment_authorisation, Toast.LENGTH_SHORT).show();

                    }
                }));
            }

            //Log.d(TAG, "Status message " + res.statusMessage());
            //Log.d(TAG, "Status code " + res.statusCode());
            //Log.d(TAG, "res text " + commentResponce);



            /*try {
                doc2 = Jsoup.connect("http://xn--d1aucecghm.xn--p1ai")
                        .cookie("CSRF_TOKEN", cookiees.get("CSRF_TOKEN"))
                        .cookie("PHPSESSID", cookiees.get("PHPSESSID"))
                        .cookie("_ym_isad", cookiees.get("_ym_isad"))
                        .cookie("_ym_uid", cookiees.get("_ym_uid"))
                        .cookie("_ym_visorc_24318637", cookiees.get("_ym_visorc_24318637"))
                        .cookie("ad22f67ed99112ca7ab6bb2e2a9cef00", cookiees.get("ad22f67ed99112ca7ab6bb2e2a9cef00"))
                        .cookie("auth", cookiees.get("auth"))
                        .data("CSRF_TOKEN", CSRF_TOKEN)
                        .userAgent(useragent)
                        .post();
            } catch (IOException e) {
                e.printStackTrace();
            }*/



            //Log.d(TAG, "Headers " + res.headers().toString());
            // Log.d(TAG, "Headers " + res.charset());


            /*try {
                res = Jsoup.connect("http://www.deeproute.com/")
                        .data("cookieexists", "false")
                        .data("name", user)
                        .data("password", pswd)
                        .method(Connection.Method.POST)
                        .execute();

            } catch (IOException e) {

                e.printStackTrace();
            }*/

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

//            Log.d("Глянем что мы напарсили", doc2.select("h4").text());

            /*webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    webView.loadUrl("javascript:(function() { " +
                            "document.getElementsByTagName('header')[0].style.display=\nnone\n; " +
                            "})()");
                }
            });
            webView.loadUrl("");*/
            //textView.setText(doc2.text());
            //webView.setVisibility(View.GONE);

            postStatusCode = "";

            try {
                JsonObject responseJsonObjects = new JsonParser().parse(commentResponce).getAsJsonObject();
                postStatusCode = responseJsonObjects.get("status").getAsString();

                Log.d(TAG, "Status code " + postStatusCode);

                switch (postStatusCode) {
                    case "0":
                        Toast.makeText(getApplicationContext(), "Эй, так нельзя!", Toast.LENGTH_SHORT).show();

                        break;
                    case "1":

                        break;
                    case "2":
                        Toast.makeText(getApplicationContext(), R.string.success_msg_editComment, Toast.LENGTH_SHORT).show();
                        etComment.setText("");
                        etComment.clearFocus();
                        InputMethodManager imm = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(
                                etComment.getWindowToken(), 0);
                        break;
                }
            } catch (IllegalStateException e) {
                //Toast.makeText(getApplicationContext(), R.string.err_msg_sendComment_authorisation, Toast.LENGTH_LONG).show();
            }
            catch (Exception e) {
                /*if(cookiees.get("ad22f67ed99112ca7ab6bb2e2a9cef00")==null & !postStatusCode.equals(""))
                     Toast.makeText(getApplicationContext(), R.string.err_msg_sendComment_authorisation, Toast.LENGTH_LONG).show();
                else Toast.makeText(getApplicationContext(), R.string.err_msg_sendComment, Toast.LENGTH_LONG).show();*/
                e.printStackTrace();
            }

            commentSending = false;


            super.onPostExecute(s);
        }
    }
    public class Vote extends AsyncTask<String, Void, String> {

        Document doc2 = null;
        Document document = null;
        String commentResponce = "";
        String postStatusCode;
        String voteItemType = "C";

        @Override
        protected void onPreExecute() {


            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String useragent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36 OPR/36.0.2130.46";


            if(cookiees.get("auth")!=null) {
                try {
                    res = Jsoup.connect("http://xn--d1aucecghm.xn--p1ai/rating/vote/")
                            //.header("Accept", "application/json, text/javascript, */*; q=0.01")
                            //.header("Accept-Encoding", "gzip, deflate, lzma")
                            //.header("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4")
                            //.header("Connection", "keep-alive")
                            //.header("Content-Length", "207")
                            //.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                            //.header("Cookie", cookies)
                            //.header("Host", "xn--d1aucecghm.xn--p1ai")
                            //.header("Origin", "http://xn--d1aucecghm.xn--p1ai")
                            //.header("Referer", "http://xn--d1aucecghm.xn--p1ai/feed/9207-v-preddverii-lyubimykh-prazdnikov.html")
                            //.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36 OPR/35.0.2066.92")
                            //.header("X-Requested-With", "XMLHttpRequest")
                            .userAgent(useragent)
                            .referrer(commentsUrl)
                            .cookie("CSRF_TOKEN", cookiees.get("CSRF_TOKEN"))
                            .cookie("PHPSESSID", cookiees.get("PHPSESSID"))
                                    //.cookie("_ym_uid", cookiees.get("_ym_uid"))
//                                    .cookie("_ym_isad", cookiees.get("_ym_isad"))
//                                .cookie("_ym_visorc_24318637", cookiees.get("_ym_visorc_24318637"))
                            //.cookie("ad22f67ed99112ca7ab6bb2e2a9cef00", cookiees.get("ad22f67ed99112ca7ab6bb2e2a9cef00"))
                            .cookie("auth", cookiees.get("auth"))
                            .data("CSRF_TOKEN", CSRF_TOKEN)
                            .data("item_id", item_id_vote)
                            .data("item_type", voteItemType)
                            .method(Connection.Method.POST)
                            .execute();

                    Log.d(TAG, "item_id_vote " + item_id_vote);

                } catch (IOException e) {
                    e.printStackTrace();

                    runOnUiThread(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.err_msg_sendComment, Toast.LENGTH_LONG).show();

                        }
                    }));

                    Log.d("Блять", "блэт");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    commentResponce = res.parse().text();
                    Log.d(TAG, "commentResponce " + res.toString());

                } catch (NullPointerException e) {
                    e.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Log.d(TAG, "Status message " + res.statusMessage());
                //Log.d(TAG, "Status code " + res.statusCode());
                //Log.d(TAG, "res text " + commentResponce);
                //}

            } else {
                runOnUiThread(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.err_msg_sendComment_authorisation, Toast.LENGTH_LONG).show();

                    }
                }));
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            postStatusCode = "";

            try {
                JsonObject responseJsonObjects = new JsonParser().parse(commentResponce).getAsJsonObject();
                postStatusCode = responseJsonObjects.get("v").getAsString();

                Log.d(TAG, "Status code reply " + postStatusCode);

                switch (postStatusCode) {
                    case "0":
                        Toast.makeText(getApplicationContext(), "Необходимо авторизоваться", Toast.LENGTH_LONG).show();

                        break;
                    case "1":

                        break;
                    case "2":
                        Toast.makeText(getApplicationContext(), "Бро лайкнут", Toast.LENGTH_LONG).show();
                        break;
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Сервер не вернул ответ", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            Log.d(TAG, String.valueOf(res.statusCode()));
            Log.d(TAG, res.statusMessage());



            super.onPostExecute(s);
        }
    }

    public class ParseComments extends AsyncTask<String, Void, String> {

        Document commentsDoc = null;
        Document document = null;
        Elements commentsArea;
        Elements name;
        Elements vkUrl;
        Elements imgUrl;
        Elements commentContent;
        Elements commentContentImg;
        Elements commentTime;
        Elements commentId;
        Elements commentIdParent;
        Elements likeCount;
        Elements whoLiked;

        @Override
        protected void onPreExecute() {

            //tvLoadingMain.setVisibility(View.VISIBLE);


            //String request = "http://oauth.vk.com/authorize?client_id=4017314&redirect_uri=" +
                   // "http://xn--d1aucecghm.xn--p1ai/auth/vkontakte/login/&response_type=code&v=5.4&state=7a1d47e464aff4ef0ac00ac51f73b4994a7702e5" + CSRF_TOKEN;


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

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String useragent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36 OPR/35.0.2066.92";

            if(cookiees.get("auth")!=null & CSRF_TOKEN==null) {

                try {
                    commentsDoc = Jsoup.connect(commentsUrl)
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
                try {
                    commentsDoc = Jsoup.connect(commentsUrl)
                            //.cookie("CSRF_TOKEN", cookiees.get("CSRF_TOKEN"))
                            //.cookie("PHPSESSID", cookiees.get("PHPSESSID"))
                            //.cookie("_ym_isad", cookiees.get("_ym_isad"))
                            //.cookie("_ym_uid", cookiees.get("_ym_uid"))
                            //.cookie("_ym_visorc_24318637", cookiees.get("_ym_visorc_24318637"))
                            //.cookie("ad22f67ed99112ca7ab6bb2e2a9cef00", cookiees.get("ad22f67ed99112ca7ab6bb2e2a9cef00"))
                            // .cookie("auth", cookiees.get("auth"))
                            //.data("CSRF_TOKEN", CSRF_TOKEN)
                            .userAgent(useragent)
                            .get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            String commentIdMainString = "";
            String commentIdParentString = "";
            String commentsTimeString = "";
            String[] commentsTimeStringArr;
            String likeCountString;

            if(commentsDoc!=null) {

                commentsArrayList.clear();

                commentId = commentsDoc.getElementsByAttributeValueStarting("id", "comment-");
                for (int i = 0; i < commentId.size(); i++) {
                    commentIdMainString = commentId.get(i).id();
                    commentIdMainString = commentIdMainString.replace("comment-", "");

                    try {
                        String comment = commentId.get(i).toString();
                        //Log.d(TAG, "Comment " + comment);

                        //Находим id родителя
                        comment = comment.substring(comment.indexOf("data-parent=\"") + 13);
                        comment = comment.substring(0, comment.indexOf("\""));
                        commentIdParentString = comment;

                        //Находим время комментария
                        comment = commentId.get(i).toString();
                        //Log.d(TAG, "Comment " + comment);
                        comment = comment.substring(comment.indexOf("</span></a>") + 11);
                        comment = comment.substring(0, comment.indexOf("<a class="));
                        commentsTimeString = comment;


                    } catch (Exception e) {

                    }
                    commentsArea = commentsDoc.select("div#comment-" + commentIdMainString);
                    name = commentsArea.select("h4.media-heading"); //Имя
                    commentContent = commentsArea.select("div.comment-item-content");
                    commentContentImg = commentsArea.select("a.comment-image-large");
                    imgUrl = commentsArea.select("img.media-object");
                    likeCount = commentsArea.select("span.comment-voter-count");
                    vkUrl = commentsArea.select("a.pull-left");

                    //commentsTimeStringArr = commentTime.text().split(" ");
                    //int length = commentsTimeStringArr.length;
                    //commentsTimeString =
                    //commentsTimeStringArr[length - 5] + " " + commentsTimeStringArr[length - 4] + " " +
                    //commentsTimeStringArr[length - 3] + " " + commentsTimeStringArr[length - 2];
                /*Log.d(TAG, "Имя комментатора " + name.text());
                Log.d(TAG, "Комментарий " + commentContent.text());
                Log.d(TAG, "Ссылка на картинку " + imgUrl.attr("src"));
                Log.d(TAG, "Время комментария " + commentsTimeString);
                Log.d(TAG, "Id комментария " + commentIdMainString);
                Log.d(TAG, "Id родителя " + commentIdParentString);*/
                Log.d(TAG, "VkUrl " + vkUrl.attr("href"));
                    commentsArrayList.add(new Comments(name.text(), commentContent.text(), commentsTimeString, imgUrl.attr("src"), commentIdMainString, commentIdParentString, likeCount.text(), null, vkUrl.attr("href"), commentContentImg.attr("data-image")));
                }
            }

            if(cookiees.get("auth")!=null & CSRF_TOKEN==null) {
                try {
                    CSRF_TOKEN = commentsDoc.toString().substring(commentsDoc.toString().indexOf("CSRF_TOKEN = \""));
                    CSRF_TOKEN = CSRF_TOKEN.substring(0, CSRF_TOKEN.indexOf("\";"));
                    CSRF_TOKEN = CSRF_TOKEN.replace("CSRF_TOKEN = \"", "");

                    Log.d(TAG, "CSRF_TOKEN " + CSRF_TOKEN);
                } catch (Exception e) {

                }
            }


            //Log.d(TAG, "Headers " + res.headers().toString());
            // Log.d(TAG, "Headers " + res.charset());


            return null;
        }

        @Override
        protected void onPostExecute(String s) {


            commentfirstLoad = false;

            if(commentsDoc == null) {
                Toast.makeText(getApplicationContext(), R.string.err_msg_updateComments, Toast.LENGTH_SHORT).show();
            }

            progressBarToolbar.setVisibility(View.GONE);

            tvLoadingMain.setVisibility(View.INVISIBLE);
            if(commentId.size()==0) {
                tvLoadingMain.setVisibility(View.VISIBLE);
                tvLoadingMain.setGravity(Gravity.CENTER);
                tvLoadingMain.setText("Комментариев пока никто не оставлял. Будьте первым!");
            }

            swipeRefreshLayout.setRefreshing(false);

            try {
                lvComments.invalidateViews();
            } catch (Exception e) {

            }

            ((CommentsAdapter) commentsAdapter).setOnItemClickListener(new CommentsAdapter.MyClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    Log.d(TAG, "Item clicked by position: " + position);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(((CommentsAdapter) commentsAdapter).getComments(position).getVkUrl()));
                    startActivity(i);
                }
            });



            //textView.setText(doc2.text());
            //webView.setVisibility(View.GONE);

            super.onPostExecute(s);
        }
    }
    public class ParseAnswers extends AsyncTask<String, Void, String> {

        Document commentsDoc = null;
        Document document = null;
        Elements commentsArea;
        Elements name;
        Elements vkUrl;
        Elements imgUrl;
        Elements commentContent;
        Elements commentContentImg;
        Elements commentTime;
        Elements commentId;
        Elements commentIdParent;
        Elements likeCount;
        Elements whoLiked;

        @Override
        protected void onPreExecute() {

            //tvLoadingMain.setVisibility(View.VISIBLE);


            //String request = "http://oauth.vk.com/authorize?client_id=4017314&redirect_uri=" +
                   // "http://xn--d1aucecghm.xn--p1ai/auth/vkontakte/login/&response_type=code&v=5.4&state=7a1d47e464aff4ef0ac00ac51f73b4994a7702e5" + CSRF_TOKEN;


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

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String useragent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36 OPR/35.0.2066.92";

            if(cookiees.get("auth")!=null & CSRF_TOKEN==null) {

                try {
                    commentsDoc = Jsoup.connect(commentsUrl)
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
                try {
                    commentsDoc = Jsoup.connect(commentsUrl)
                            //.cookie("CSRF_TOKEN", cookiees.get("CSRF_TOKEN"))
                            //.cookie("PHPSESSID", cookiees.get("PHPSESSID"))
                            //.cookie("_ym_isad", cookiees.get("_ym_isad"))
                            //.cookie("_ym_uid", cookiees.get("_ym_uid"))
                            //.cookie("_ym_visorc_24318637", cookiees.get("_ym_visorc_24318637"))
                            //.cookie("ad22f67ed99112ca7ab6bb2e2a9cef00", cookiees.get("ad22f67ed99112ca7ab6bb2e2a9cef00"))
                            // .cookie("auth", cookiees.get("auth"))
                            //.data("CSRF_TOKEN", CSRF_TOKEN)
                            .userAgent(useragent)
                            .get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            String commentIdMainString = "";
            String commentIdParentString = "";
            String commentsTimeString = "";
            String[] commentsTimeStringArr;
            String likeCountString;

            if(commentsDoc!=null) {

                commentsArrayList.clear();

                commentId = commentsDoc.getElementsByAttributeValueStarting("id", "comment-");
                for (int i = 0; i < commentId.size(); i++) {
                    commentIdMainString = commentId.get(i).id();
                    commentIdMainString = commentIdMainString.replace("comment-", "");

                    try {
                        String comment = commentId.get(i).toString();
                        //Log.d(TAG, "Comment " + comment);

                        //Находим id родителя
                        comment = comment.substring(comment.indexOf("data-parent=\"") + 13);
                        comment = comment.substring(0, comment.indexOf("\""));
                        commentIdParentString = comment;

                        //Находим время комментария
                        comment = commentId.get(i).toString();
                        //Log.d(TAG, "Comment " + comment);
                        comment = comment.substring(comment.indexOf("</span></a>") + 11);
                        comment = comment.substring(0, comment.indexOf("<a class="));
                        commentsTimeString = comment;


                    } catch (Exception e) {

                    }
                    commentsArea = commentsDoc.select("div#comment-" + commentIdMainString);
                    name = commentsArea.select("h4.media-heading"); //Имя
                    commentContent = commentsArea.select("h4.media-heading");
                    commentContentImg = commentsArea.select("a.comment-image-large");
                    imgUrl = commentsArea.select("img.media-object");
                    likeCount = commentsArea.select("span.comment-voter-count");
                    vkUrl = commentsArea.select("a.pull-left");

                    String comment = commentsArea.toString();
                    comment = comment.substring(comment.indexOf("<span class=\"brown\">") + 20, comment.lastIndexOf("</span>"));

                    //commentsTimeStringArr = commentTime.text().split(" ");
                    //int length = commentsTimeStringArr.length;
                    //commentsTimeString =
                    //commentsTimeStringArr[length - 5] + " " + commentsTimeStringArr[length - 4] + " " +
                    //commentsTimeStringArr[length - 3] + " " + commentsTimeStringArr[length - 2];
                Log.d(TAG, "Имя комментатора " + name.text());
                Log.d(TAG, "Комментарий " + comment);
                Log.d(TAG, "Ссылка на картинку " + imgUrl.attr("src"));
                Log.d(TAG, "Время комментария " + commentsTimeString);
                Log.d(TAG, "Id комментария " + commentIdMainString);
                Log.d(TAG, "Id родителя " + commentIdParentString);
                Log.d(TAG, "VkUrl " + vkUrl.attr("href"));
                    commentsArrayList.add(new Comments(name.text(), comment, commentsTimeString, imgUrl.attr("src"), commentIdMainString, commentIdParentString, likeCount.text(), null, vkUrl.attr("href"), commentContentImg.attr("data-image")));
                }
            }

            if(cookiees.get("auth")!=null & CSRF_TOKEN==null) {
                try {
                    CSRF_TOKEN = commentsDoc.toString().substring(commentsDoc.toString().indexOf("CSRF_TOKEN = \""));
                    CSRF_TOKEN = CSRF_TOKEN.substring(0, CSRF_TOKEN.indexOf("\";"));
                    CSRF_TOKEN = CSRF_TOKEN.replace("CSRF_TOKEN = \"", "");

                    Log.d(TAG, "CSRF_TOKEN " + CSRF_TOKEN);
                } catch (Exception e) {

                }
            }


            //Log.d(TAG, "Headers " + res.headers().toString());
            // Log.d(TAG, "Headers " + res.charset());


            return null;
        }

        @Override
        protected void onPostExecute(String s) {


            commentfirstLoad = false;

            if(commentsDoc == null) {
                Toast.makeText(getApplicationContext(), R.string.err_msg_updateComments, Toast.LENGTH_SHORT).show();
            }

            progressBarToolbar.setVisibility(View.GONE);

            tvLoadingMain.setVisibility(View.INVISIBLE);

            swipeRefreshLayout.setRefreshing(false);

            try {
                lvComments.invalidateViews();
            } catch (Exception e) {

            }

            ((CommentsAdapter) commentsAdapter).setOnItemClickListener(new CommentsAdapter.MyClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    Log.d(TAG, "Item clicked by position: " + position);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(((CommentsAdapter) commentsAdapter).getComments(position).getVkUrl()));
                    startActivity(i);
                }
            });



            //textView.setText(doc2.text());
            //webView.setVisibility(View.GONE);

            super.onPostExecute(s);
        }
    }

    public class MyJavaScriptInterface {


        @JavascriptInterface
        public void handleHtml(String html) {
            // Use jsoup on this String here to search for your content.
            doc = Jsoup.parse(html);
            CSRF_TOKEN = doc.toString().substring(doc.toString().indexOf("CSRF_TOKEN = \""));
            CSRF_TOKEN = CSRF_TOKEN.substring(0, CSRF_TOKEN.indexOf("\";"));
            CSRF_TOKEN = CSRF_TOKEN.replace("CSRF_TOKEN = \"", "");

            Log.d(TAG,"CSRF_TOKEN " + CSRF_TOKEN);

            String[] temp1;
            String[] temp=cookies.split(";");
            for (String ar1 : temp ){
                if(ar1.contains("CSRF_TOKEN")
                        | ar1.contains("_ym_uid")
                        | ar1.contains("_ym_isad")
                        | ar1.contains("auth")
                        | ar1.contains("PHPSESSID")
                        | ar1.contains("_ym_visorc_24318637")
                        | ar1.contains("ad22f67ed99112ca7ab6bb2e2a9cef00")){
                    temp1 = ar1.split("=");
                    temp1[0]=temp1[0].replace(" ", "");
                    cookiees.put(temp1[0], temp1[1]);
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarToolbar.setVisibility(View.INVISIBLE);
                }
            });

            Log.d(TAG,"Cookiees " + cookiees);
            webViewLoad = true;


        }
    }

}
