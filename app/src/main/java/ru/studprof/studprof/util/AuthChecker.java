package ru.studprof.studprof.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

import ru.studprof.studprof.R;
import ru.studprof.studprof.activity.Constants;
import ru.studprof.studprof.activity.FeedBackActivity;

/**
 * Created by Ильназ on 04.04.2016.
 */
public class AuthChecker {

    String cookies;
    Map<String, String> cookiees;
    Context ctx;
    String ansCount;
    Boolean isStudprofMember = false;
    SharedPreferences sharedPreferences;



    final String TAG = "AuthChecker";
    final String MEMBER = "Member";


    public AuthChecker(Context context) {

        ctx = context;

        CookieSyncManager.createInstance(context);
        CookieSyncManager.getInstance().startSync();
        cookiees = new HashMap<>();
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

            //Log.d(TAG, "Cookiees " + cookiees);
        } catch (Exception e) {

        }

        new ParseAnswers().execute();


    }

    public boolean isAuthed() {
        if(cookiees.get("auth")!=null)
        return true;
        else return false;
    }
    public boolean isStudprofMember() {
        return loadText(true, MEMBER);

    }

    public void parse() {
        new ParseAnswers().execute();
    }


    void saveText(String text, String whatSave) {
        //sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(whatSave, text);
        ed.commit();
        //Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show();
    }
    void saveText(Boolean b, String whatSave) {
        //sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putBoolean(whatSave, b);
        ed.commit();
        //Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show();
    }

    String loadText(String whatLoad) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        String savedText = sharedPreferences.getString(whatLoad, "");
//        Toast.makeText(this, "Text loaded", Toast.LENGTH_SHORT).show();
        if(savedText!="") return savedText;
        else return null;

    }
    Boolean loadText(Boolean b, String whatLoad) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        Boolean savedText = sharedPreferences.getBoolean(whatLoad, false);
//        Toast.makeText(this, "Text loaded", Toast.LENGTH_SHORT).show();
        return savedText;

    }

    private void ansNotification() {
        try {
            NotificationManager mNotificationManager;


            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Log.d(TAG, "answerNotifi");
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx);
            mBuilder.setSmallIcon(getNotificationIcon());
            mBuilder.setContentTitle("СТУДПРОФ.РФ");
            mBuilder.setContentText("Новые ответы (" + ansCount + ")");
            mBuilder.setWhen(System.currentTimeMillis());
            mBuilder.setAutoCancel(true);
            mBuilder.setSound(alarmSound);
            //if (notificationSound) mBuilder.setSound(alarmSound);
            //if (notificationVibro) mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);


            Intent resultIntent = new Intent(ctx, FeedBackActivity.class);
            resultIntent.putExtra(Constants.COMMENTS_URL, "http://xn--d1aucecghm.xn--p1ai/user/replies/");
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
            stackBuilder.addParentStack(FeedBackActivity.class);

            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(77, mBuilder.build());
            Log.d(TAG, "ansNotification");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.studprof_notif_icon: R.mipmap.ic_launcher;
    }


    public class ParseAnswers extends AsyncTask<String, Void, String> {

        Document commentsDoc = null;

        @Override
        protected void onPreExecute() {

            //tvLoadingMain.setVisibility(View.VISIBLE);


            //String request = "http://oauth.vk.com/authorize?client_id=4017314&redirect_uri=" +
            // "http://xn--d1aucecghm.xn--p1ai/auth/vkontakte/login/&response_type=code&v=5.4&state=7a1d47e464aff4ef0ac00ac51f73b4994a7702e5" + CSRF_TOKEN;


            cookies = CookieManager.getInstance().getCookie("http://xn--d1aucecghm.xn--p1ai/user/replies/");


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


                try {
                    commentsDoc = Jsoup.connect("http://xn--d1aucecghm.xn--p1ai/user/replies")
                            .userAgent(useragent)
                            .cookie("CSRF_TOKEN", cookiees.get("CSRF_TOKEN"))
                            .cookie("PHPSESSID", cookiees.get("PHPSESSID"))
                            .cookie("ad22f67ed99112ca7ab6bb2e2a9cef00", cookiees.get("ad22f67ed99112ca7ab6bb2e2a9cef00"))
                            .cookie("auth", cookiees.get("auth"))
                            .get();
                } catch (Exception e) {
                    e.printStackTrace();
                }



            if(commentsDoc!=null) {


                //Log.d(TAG, String.valueOf(commentsDoc.select("li.active")));

                ansCount = commentsDoc.select("li.active").toString();

                if (ansCount.contains("<sup>")) {
                    ansCount = ansCount.substring(ansCount.indexOf("<sup>") + 5, ansCount.indexOf("</sup>"));
                    Log.d(TAG, ansCount);
                } else ansCount = null;

                if (commentsDoc.select("a").text().contains("Мероприятия")) {
                        isStudprofMember = true;
                } else {
                    isStudprofMember = false;
                }
                saveText(isStudprofMember, MEMBER);
            }


            //Log.d(TAG, "Headers " + res.headers().toString());
            // Log.d(TAG, "Headers " + res.charset());


            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            //textView.setText(doc2.text());
            //webView.setVisibility(View.GONE);

            if(ansCount!=null) ansNotification();

            super.onPostExecute(s);
        }
    }

}
