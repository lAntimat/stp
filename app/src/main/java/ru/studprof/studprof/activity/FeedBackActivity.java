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

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.studprof.studprof.R;
import ru.studprof.studprof.adapter.FeedBack;
import ru.studprof.studprof.adapter.FeedBackAdapter;

public class FeedBackActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {


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
    ListView lvFeedBack;
    FeedBackAdapter feedBackAdapter;
    ArrayList<FeedBack> feedBackArrayList = new ArrayList<>();
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
        setContentView(R.layout.activity_feed_back);


        feedBackAdapter = new FeedBackAdapter(getApplicationContext(), feedBackArrayList);

        lvFeedBack = (ListView) findViewById(R.id.lv);
        lvFeedBack.setAdapter(feedBackAdapter);

        lvFeedBack.setOnItemClickListener(this);

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
            getSupportActionBar().setTitle("Оповещения");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_send_white_24dp, 0, 0, 0);

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
        commentsUrl = "http://xn--d1aucecghm.xn--p1ai/user/replies";


        cookies = CookieManager.getInstance().getCookie("http://xn--d1aucecghm.xn--p1ai/user/replies");


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
        new ParseFeedBack().execute();
    }


    public void authActivity(View view) {
        Intent intent = new Intent(this, AuthorizationActivity.class);
        intent.setAction(Constants.ACTION_AUTH);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        new MaterialDialog.Builder(FeedBackActivity.this)
                //.title(R.string.title)
                .theme(Theme.LIGHT)
                .items(R.array.feedback_dialog_items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                            switch (which) {
                                case 0:
                                    Intent intent = new Intent(FeedBackActivity.this, ActivityFeedCollapsingToolbar.class);
                                    intent.putExtra(Constants.FEED_URL, feedBackArrayList.get(position).feedUrl);
                                    startActivity(intent);
                                    break;
                                case 1:
                                    Intent intent1 = new Intent(FeedBackActivity.this, CommentsActivity.class);
                                    intent1.putExtra(Constants.COMMENTS_URL, feedBackArrayList.get(position).feedUrl);
                                    startActivity(intent1);
                                    break;
                                case 2:

                                    break;
                            }
                    }
                })
                .show();
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
                new ParseFeedBack().execute();
            }
        });
    }


    public class ParseFeedBack extends AsyncTask<String, Void, String> {

        Document commentsDoc = null;
        Document document = null;
        Elements commentsArea;
        Elements name;
        Elements vkUrl;
        Elements imgUrl;
        Elements commentContent;
        Elements commentContentLike;
        Elements whenReplyCommentText;
        Elements whenLikeCommentText;
        Elements commentContentImg;
        Elements parentUrl;
        Elements parentUrlText;
        Elements commentTime;
        Elements commentId;
        Elements commentIdParent;
        Elements commentParentUrl;
        Elements likeCount;
        Elements whoLiked;
        Elements feedPath;

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
            String commentParentUrlString = "";
            String commentsTimeString = "";
            String[] commentsTimeStringArr;
            String likeCountString;

            if(commentsDoc!=null) {

                //feedBackArrayList.clear();

                commentId = commentsDoc.getElementsByAttributeValueStarting("id", "comment-");
                commentParentUrl = commentsDoc.getElementsByAttributeValueStarting("data-url", "/");
                for (int i = 0; i < commentId.size(); i++) {
                    commentIdMainString = commentId.get(i).id();
                    commentIdMainString = commentIdMainString.replace("comment-", "");
                    //commentParentUrlString = commentId.get(i).data("url");

                    try {
                        String comment = commentId.get(i).toString();
                        //Log.d(TAG, "Comment " + comment);

                        //Находим id родителя
                        comment = comment.substring(comment.indexOf("data-parent=\"") + 13);
                        comment = comment.substring(0, comment.indexOf("\""));
                        commentIdParentString = comment;

                        //Находим время комментария
                        //comment = commentId.get(i).toString();
                        //Log.d(TAG, "Comment " + comment);
                        //comment = comment.substring(comment.indexOf("</span></a>") + 11);
//                        comment = comment.substring(0, comment.indexOf("<a class="));
                        //commentsTimeString = comment;


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    commentsArea = commentsDoc.select("div#comment-" + commentIdMainString);
                    name = commentsArea.select("h4.media-heading"); //Имя
                    whenReplyCommentText = commentsArea.select("em.media-overflow");
                    whenLikeCommentText = commentsArea.select("p");
                    commentContent = commentsArea.select("h4.media-heading");
                    commentContentLike = commentsArea.select("div.media-overflow");
                    commentContentImg = commentsArea.select("a.comment-image-large");
                    imgUrl = commentsArea.select("img.media-object");
                    likeCount = commentsArea.select("span.comment-voter-count");
                    vkUrl = commentsArea.select("a.pull-left");
                    parentUrl = commentsArea.select("div.fs12");
                    parentUrlText = commentsArea.select("div.fs12");
                    feedPath = commentsDoc.select("div.fs12");

                    String comment = commentsArea.toString();
                    comment = comment.substring(comment.indexOf("</h4>") + 5, comment.lastIndexOf("<p>"));
                    comment = comment.replace("\n", "");

                    String whenReplyText = whenReplyCommentText.text();
                    if(whenReplyText.contains("Ваш комментарий")) {
                        whenReplyText = whenReplyText.replace("Ваш комментарий ", "Ваш комментарий:\n«");
                        whenReplyText = whenReplyText + "»";
                    }

                    String comment2 = commentContentLike.text();
                    if(comment2.contains("Ваш комментарий")) {
                        comment2 = comment2.replace("Ваш комментарий ", "Ваш комментарий:\n«");
                        comment2 = comment2 + "»";
                    }

                    String feedUrl = commentsArea.toString();
                    feedUrl = feedUrl.substring(feedUrl.indexOf("data-url") + 10);
                    feedUrl = "http://xn--d1aucecghm.xn--p1ai" + feedUrl.substring(0, feedUrl.indexOf("html") + 4);

                    //commentsTimeStringArr = commentTime.text().split(" ");
                    //int length = commentsTimeStringArr.length;
                    //commentsTimeString =
                    //commentsTimeStringArr[length - 5] + " " + commentsTimeStringArr[length - 4] + " " +
                    //commentsTimeStringArr[length - 3] + " " + commentsTimeStringArr[length - 2];
                Log.d(TAG, "Имя комментатора " + name.text());
                Log.d(TAG, "Комментарий " + comment);
                Log.d(TAG, "Ссылка на картинку " + imgUrl.attr("src"));
                //Log.d(TAG, "Время комментария " + commentsTimeString);
                Log.d(TAG, "whenReplyCommentText " + whenReplyCommentText.text());
                Log.d(TAG, "whenLikeCommentText " + whenLikeCommentText.text());
                Log.d(TAG, "VkUrl " + vkUrl.attr("href"));
                Log.d(TAG, "parentUrl " + parentUrl.after("<a href="));
                Log.d(TAG, "parentUrl " + parentUrlText);
                Log.d(TAG, "feedUrl " + feedUrl);
                Log.d(TAG, "feedPath " + feedPath.text());
                    Log.d(TAG, "");
                    if(whenReplyCommentText.hasText())
                    feedBackArrayList.add(new FeedBack(name.text(), comment, whenReplyText, imgUrl.attr("src"), commentIdMainString, commentIdParentString, feedPath.get(i).text(), null, vkUrl.attr("href"), commentContentImg.attr("data-image"), feedUrl));
                    else feedBackArrayList.add(new FeedBack(name.text(), comment2, whenLikeCommentText.text(), imgUrl.attr("src"), commentIdMainString, commentIdParentString, feedPath.get(i).text(), null, vkUrl.attr("href"), commentContentImg.attr("data-image"), feedUrl));

                    whenReplyCommentText = null;
                    whenLikeCommentText = null;
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

            if(commentId.size()==0) {
                tvLoadingMain.setGravity(Gravity.CENTER);
                tvLoadingMain.setVisibility(View.VISIBLE);
                tvLoadingMain.setTextSize(tvLoadingMain.getTextSize() + 3);
                tvLoadingMain.setText("¯\\_(ツ)_/¯");
            }


            try {
                lvFeedBack.invalidateViews();
            } catch (Exception e) {

            }

            ((FeedBackAdapter) feedBackAdapter).setOnItemClickListener(new FeedBackAdapter.MyClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    Log.d(TAG, "Item clicked by position: " + position);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(((FeedBackAdapter) feedBackAdapter).getComments(position).getVkUrl()));
                    startActivity(i);
                }
            });



            //textView.setText(doc2.text());
            //webView.setVisibility(View.GONE);

            super.onPostExecute(s);
        }
    }
}
