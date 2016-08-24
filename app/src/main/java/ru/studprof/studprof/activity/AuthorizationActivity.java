package ru.studprof.studprof.activity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ru.studprof.studprof.R;

public class AuthorizationActivity extends AppCompatActivity {

    Connection.Response res = null;
    WebView webView;
    Document doc;
    String CSRF_TOKEN = "";
    String cookies;
    Map<String, String> cookiees;
    Map<String, String> loginCookies = null;
    Document docWithCookies;
    TextView textView;
    Connection connectionJsoup;

    ProgressBar progressBar;

    static final String TAG = "AutorizationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);



        textView = (TextView) findViewById(R.id.textView);

        //new ParseCookes().execute();

        webView = (WebView) findViewById(R.id.webView2);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSaveFormData(true);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //webView.loadUrl("http://xn--d1aucecghm.xn--p1ai/");
        String request = "";
        if(getIntent().getAction().equals(Constants.ACTION_AUTH)) {
            request = "http://oauth.vk.com/authorize?client_id=4017314&redirect_uri=" +
                    "http://xn--d1aucecghm.xn--p1ai/auth/vkontakte/login/&response_type=code&v=5.4&state=7a1d47e464aff4ef0ac00ac51f73b4994a7702e5" + CSRF_TOKEN;
        } else if(getIntent().getAction().equals(Constants.ACTION_EXIT)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    Log.d(TAG, "Using ClearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
                    CookieManager.getInstance().removeAllCookies(null);
                    CookieManager.getInstance().flush();
                } else
                {
                    Log.d(TAG, "Using ClearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
                    CookieSyncManager cookieSyncMngr= CookieSyncManager.createInstance(getApplicationContext());
                    cookieSyncMngr.startSync();
                    CookieManager cookieManager=CookieManager.getInstance();
                    cookieManager.removeAllCookie();
                    cookieManager.removeSessionCookie();
                    cookieSyncMngr.stopSync();
                    cookieSyncMngr.sync();
                }
            request = "https://vk.com";
            finish();
        }
        webView.loadUrl(request);

        //webView.loadUrl("http://xn--d1aucecghm.xn--p1ai");



        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlHandler");

        //webView.loadUrl("Auth.doRequest('vkontakte', '20eeda412653a4036a3be5da9ed209e99f5c8c1c')");
        //webView.loadUrl("javascript:(function(){teamUserDialog(5);})()");

        //webView.loadUrl("javascript:window.HtmlHandler.handleHtml");

        cookiees = new HashMap<>();

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                cookies = CookieManager.getInstance().getCookie(url);
                webView.loadUrl("javascript:window.HtmlHandler.handleHtml" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                Log.d("Кукилар белэт", "All the cookies in a string:" + cookies);
                //Log.d("ЧТО СЕЙЧАС ГРУЗИТСЯ", url);

                Log.d(TAG, "Загружается: " + url);
                //if(url.contains("http://xn--d1aucecghm.xn--p1ai/auth/vkontakte"))
                progressBar.setVisibility(View.INVISIBLE);
                super.onPageFinished(view, url);
            }
        });


        //cookiees.put("SESSIONID", cookies);


    }


    public void btnClick(View view) {

        //webView.loadUrl("javascript:(function(){commentAuthDialog().click();})()");
        //webView.loadUrl("javascript:(function(){authDialog().click();})()");
       // webView.loadUrl("javascript:(function(){teamUserDialog(\"15455\").click();})()");
        String request = "http://oauth.vk.com/authorize?client_id=4017314&redirect_uri=" +
                "http://xn--d1aucecghm.xn--p1ai/auth/vkontakte/login/&response_type=code&v=5.4&state=7a1d47e464aff4ef0ac00ac51f73b4994a7702e5" + CSRF_TOKEN;
        webView.loadUrl(request);
        //new ParseCookes().execute();
        Log.d("CSRF_TOKEN", CSRF_TOKEN);



    }
    public void btnClick2(View view) {


        new CommentPage().execute();



    }

    @Override
    protected void onPause() {
        webView.stopLoading();
        super.onPause();
    }

    public void loadMainPage() {
        webView.loadUrl("http://xn--d1aucecghm.xn--p1ai");
    }


    public class ParseCookes extends AsyncTask<String, Void, String> {

        Document document = null;
        String loginURL = "http://xn--d1aucecghm.xn--p1ai/";
        String itemURL = "";
        String useragent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36";
        String login = "username";
        String pass = "password";
        Connection.Response response1;
        Connection.Response response2;

        @Override
        protected String doInBackground(String... params) {

            /*try {
                res = Jsoup.connect("http://oauth.vk.com/authorize?client_id=4017314&redirect_uri=http://xn--d1aucecghm.xn--p1ai/auth/vkontakte/login/&response_type=code&v=5.4&state=40fa1dc605be5cf3d7c22d6649ebf60a34f6f8f6")
                        .data("email", "ilnaz1997@rambler.ru", "pass", "AnTi89600747198")
                        .method(Connection.Method.POST)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            //получаем страницу входа
            Connection connection1 = HttpConnection.connect(loginURL)
                    .ignoreHttpErrors(true)
                    .userAgent(useragent);
            try {
                response1 = connection1
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //цапаем оттуда токен

            /*try {
                res = Jsoup.connect("http://xn--d1aucecghm.xn--p1ai/").ignoreContentType(true).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            //Log.d("DOC", document.toString().substring(document.toString().indexOf("CSRF_TOKEN")));
           /* CSRF_TOKEN = response1.body().toString().substring(response1.body().toString().indexOf("CSRF_TOKEN = \""));
            CSRF_TOKEN = CSRF_TOKEN.substring(0, CSRF_TOKEN.indexOf("\";"));
            CSRF_TOKEN = CSRF_TOKEN.replace("CSRF_TOKEN = \"", "");
            Log.d("CSRF_TOKEN", CSRF_TOKEN);*/


            String request = "http://oauth.vk.com/authorize?client_id=4017314&redirect_uri=" +
                    "http://xn--d1aucecghm.xn--p1ai/auth/vkontakte/login/&response_type=code&v=5.4&state=7a1d47e464aff4ef0ac00ac51f73b4994a7702e5" + CSRF_TOKEN;

            /*try {
                res = Jsoup.connect(request)
                        .data("email", "ilnaz1997@rambler.ru")
                        .data("pass", "AnTi89600747198")
                                // and other hidden fields which are being passed in post request.
                        .method(Connection.Method.POST)
                        .cookies(res.cookies())
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            //loginCookies = res.cookies();
//            Log.d(TAG, "Status message" + res.statusMessage());

            /*Connection connection2 = connection1.url("http://xn--d1aucecghm.xn--p1ai/feed/9185-miss-i-mister-obshhezhitiya-2016.html")
                    // .timeout(7000)
                    //.cookies(response1.cookies())
                    .ignoreHttpErrors(true)
                    //.data("email", "ilnaz1997@rambler.ru")
                    //.data("pass", "AnTi89600747198")
                    //.userAgent(useragent)
                    .method(Connection.Method.POST)
                    .followRedirects(true);

            try {
                response2 = connection2.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }*/


            /*try {
                document = Jsoup.connect("http://xn--d1aucecghm.xn--p1ai/feed/9185-miss-i-mister-obshhezhitiya-2016.html")
                        .ignoreContentType(true)
                        //.cookies(loginCookies)
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            //Log.d(TAG, response2.parse().select("div.page-comments").html());
            return null;

        }

        @Override
        protected void onPostExecute(String s) {

            //new ParseFeedWithCookes().execute();
            //String request = "javascript:(function(){Auth.doRequest('vkontakte', '" + CSRF_TOKEN + "');})()";

            //textView.setText(response2.body());


                webView.loadData(document.select("div.page-comments").html(), "text/html; charset=UTF-8", null);

            super.onPostExecute(s);
        }
    }

    public class CommentPage extends AsyncTask<String, Void, String> {

        Document doc2 = null;
        Document document = null;

        @Override
        protected void onPreExecute() {


            String request = "http://oauth.vk.com/authorize?client_id=4017314&redirect_uri=" +
                    "http://xn--d1aucecghm.xn--p1ai/auth/vkontakte/login/&response_type=code&v=5.4&state=7a1d47e464aff4ef0ac00ac51f73b4994a7702e5" + CSRF_TOKEN;


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

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String useragent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36 OPR/35.0.2066.92";



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
                        .referrer("http://xn--d1aucecghm.xn--p1ai/feed/9207-v-preddverii-lyubimykh-prazdnikov.html")
                        .cookie("CSRF_TOKEN", cookiees.get("CSRF_TOKEN"))
                        .cookie("PHPSESSID", cookiees.get("PHPSESSID"))
                        .cookie("_ym_isad", cookiees.get("_ym_isad"))
                        .cookie("_ym_uid", cookiees.get("_ym_uid"))
                        .cookie("_ym_visorc_24318637", cookiees.get("_ym_visorc_24318637"))
                        .cookie("ad22f67ed99112ca7ab6bb2e2a9cef00", cookiees.get("ad22f67ed99112ca7ab6bb2e2a9cef00"))
                        .cookie("auth", cookiees.get("auth"))
                        /*.data(URLDecoder.decode("item_id", "UTF-8"), URLDecoder.decode("40713", "UTF-8"))
                        .data(URLDecoder.decode("item_type", "UTF-8"), URLDecoder.decode("C", "UTF-8"))
                        .data(URLDecoder.decode("CSRF_TOKEN", "UTF-8"), URLDecoder.decode(CSRF_TOKEN, "UTF-8"))
                        /*.data(URLDecoder.decode("Comment[author_name]", "UTF-8"), URLDecoder.decode("", "UTF-8"))
                        .data(URLDecoder.decode("Comment[verifyCode]", "UTF-8"), URLDecoder.decode("", "UTF-8"))
                        .data(URLDecoder.decode("Comment[attachment]", "UTF-8"), URLDecoder.decode("", "UTF-8"))
                        .data(URLDecoder.decode("Comment[content]", "UTF-8"), URLDecoder.decode("Падам пиду", "UTF-8"))*/
                        .data("Comment[item_id]", "9207")
                        .data("Comment[item_type]", "D")
                        .data("CSRF_TOKEN", CSRF_TOKEN)
                        .data("Comment[author_name]", "")
                        .data("Comment[verifyCode]", "")
                        .data("Comment[attachment]", "")
                        .data("Comment[content]", "test")
                        .method(Connection.Method.POST)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Блять", "блэт");
            }

            Log.d(TAG, "Status message " + res.statusMessage());
            Log.d(TAG, "Status code " + res.statusCode());
            try {
                Log.d(TAG, "res text " + res.parse().text());
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
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
            }



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
                textView.setText(doc2.text());
            webView.setVisibility(View.GONE);
            super.onPostExecute(s);
        }
    }
    public class Post extends AsyncTask<String, Void, String> {

        Document doc2 = null;
        Document document = null;

        @Override
        protected void onPreExecute() {


            String request = "http://oauth.vk.com/authorize?client_id=4017314&redirect_uri=" +
                    "http://xn--d1aucecghm.xn--p1ai/auth/vkontakte/login/&response_type=code&v=5.4&state=7a1d47e464aff4ef0ac00ac51f73b4994a7702e5" + CSRF_TOKEN;


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

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String useragent = "Mozilla/5.0 (X11; Linux i686 (x86_64)) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36";
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            //textView.setText(doc2.text());
            webView.setVisibility(View.GONE);
            super.onPostExecute(s);
        }
    }

        public class MyJavaScriptInterface {


            @JavascriptInterface
            public void handleHtml(String html) {
                // Use jsoup on this String here to search for your content.
                doc = Jsoup.parse(html);
                Log.d(TAG, doc.toString());
                if(doc.toString().contains("http:\\/\\/vk.com"))  {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            webView.loadUrl("http://xn--d1aucecghm.xn--p1ai");
                        }
                    });
                    Toast.makeText(getApplicationContext(), "Вы успешно авторизовались", Toast.LENGTH_LONG).show();
                    finish();
                }



                /*CSRF_TOKEN = doc.toString().substring(doc.toString().indexOf("CSRF_TOKEN = \""));
                CSRF_TOKEN = CSRF_TOKEN.substring(0, CSRF_TOKEN.indexOf("\";"));
                CSRF_TOKEN = CSRF_TOKEN.replace("CSRF_TOKEN = \"", "");*/

                //Log.d("CSRF_TOKEN", CSRF_TOKEN);

                String request = "http://oauth.vk.com/authorize?client_id=4017314&redirect_uri=" +
                        "http://xn--d1aucecghm.xn--p1ai/auth/vkontakte/login/&response_type=code&v=5.4&state=7a1d47e464aff4ef0ac00ac51f73b4994a7702e5" + CSRF_TOKEN;
                //webView.loadUrl(request);
            }
        }

    }
