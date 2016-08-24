package ru.studprof.studprof.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.pushbots.push.PBNotificationIntent;
import com.pushbots.push.Pushbots;
import com.pushbots.push.utils.PBConstants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import ru.studprof.studprof.R;

/**
 * Created by Ильназ on 07.02.2016.
 */
public class CustomHandler extends BroadcastReceiver {
    Context ctx;
    String[] push_message;
    String titleNotif = "";
    String url = "";
    String pushAnalytics;
    Boolean pushNotification;
    Boolean entertainmentEvents = true;
    Boolean importantEvents = true;
    Boolean studEvents = true;
    Boolean notificationSound = true;
    Boolean notificationVibro = true;
    NotificationManager mNotificationManager;
    SharedPreferences sp;

    int notificationId = 0;

    private static final String TAG = "customHandler";

    private static final String PUSH_WITH_URL_STUD_PROF = "1";
    private static final String PUSH_WITH_CUSTOM_URL_AND_MESSAGE = "2";
    private static final String PUSH_WITH_MESSAGE = "3";
    private static final String PUSH_WITH_URL_STUD_PROF_PHOTO = "4";

    private static final String PUSH_ALL = "1";
    private static final String PUSH_IMPORTANT = "2";
    private static final String PUSH_ENTERTAINMENT = "3";

    private static final int NOTIFICATION_ID_WITH_URL_STUD_PROF = 15;
    private static final int NOTIFICATION_ID_WITH_CUSTOM_URL_AND_MESSAGE = 16;
    private static final int NOTIFICATION_ID_WITH_MESSAGE = 17;

    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;
        String action = intent.getAction();
        Log.d(TAG, "action=" + action);
        // Handle Push Message when opened
        if (action.equals(PBConstants.EVENT_MSG_OPEN)) {
            //Check for Pushbots Instance
            Pushbots pushInstance = Pushbots.sharedInstance();
            if (!pushInstance.isInitialized()) {
                Log.d(TAG, "Initializing Pushbots.");
                Pushbots.sharedInstance().init(context.getApplicationContext());
            }

            //Clear Notification array
            if (PBNotificationIntent.notificationsArray != null) {
                PBNotificationIntent.notificationsArray = null;
            }

            HashMap<?, ?> PushdataOpen = (HashMap<?, ?>) intent.getExtras().get(PBConstants.EVENT_MSG_OPEN);
            Log.w(TAG, "User clicked notification with Message: " + PushdataOpen.get("message"));

            //Report Opened Push Notification to Pushbots
            if (Pushbots.sharedInstance().isAnalyticsEnabled()) {
                Pushbots.sharedInstance().reportPushOpened((String) PushdataOpen.get("PUSHANALYTICS"));
            }

            //Start lanuch Activity
            String packageName = context.getPackageName();
            Intent resultIntent = new Intent(context.getPackageManager().getLaunchIntentForPackage(packageName));
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            resultIntent.putExtras(intent.getBundleExtra("pushData"));
            Pushbots.sharedInstance().startActivity(resultIntent);

            // Handle Push Message when received
        } else if (action.equals(PBConstants.EVENT_MSG_RECEIVE)) {
            HashMap<?, ?> PushdataOpen = (HashMap<?, ?>) intent.getExtras().get(PBConstants.EVENT_MSG_RECEIVE);
            Log.w(TAG, "User Received notification with Message: " + PushdataOpen.get("message"));
            push_message = PushdataOpen.get("message").toString().split(" ");
            //pushTest();

            long time = new Date().getTime();
            String tmpStr = String.valueOf(time);
            String last4Str = tmpStr.substring(tmpStr.length() - 5);
            notificationId = Integer.valueOf(last4Str);
            Log.d(TAG, "Notification id: " + notificationId);

            PushdataOpen = (HashMap<?, ?>) intent.getExtras().get(PBConstants.EVENT_MSG_OPEN);
            if (PushdataOpen != null) {
                pushAnalytics = PushdataOpen.get("PUSHANALYTICS").toString();
            }


            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(ctx);

            pushNotification = prefs.getBoolean("notification", true);
            importantEvents = prefs.getBoolean("importantInformation", false);
            entertainmentEvents = prefs.getBoolean("entertainmentEvents", false);
            notificationSound = prefs.getBoolean("notificationSound", true);
            notificationVibro = prefs.getBoolean("notificationVibro", false);
            Log.d(TAG, String.valueOf(importantEvents));
            Log.d(TAG, String.valueOf(entertainmentEvents));


            try {
                switch (push_message[0].toLowerCase()) {
                    case PUSH_WITH_URL_STUD_PROF:
                        Log.d(TAG, "pushWithUrlTask");
                        url = push_message[2];
                        titleNotif = push_message[3] + " ";
                        for (int i = 4; i < push_message.length; i++) {
                            titleNotif += push_message[i] + " ";
                        }

                        if (!url.contains("http://xn--d1aucecghm.xn--p1ai/")) {
                            url = "http://xn--d1aucecghm.xn--p1ai/" + url;
                        } else if (!url.contains("http://xn--d1aucecghm.xn--p1ai")) {
                            url = "http://xn--d1aucecghm.xn--p1ai" + "/" + url;
                        }

                        if ((!importantEvents & !entertainmentEvents)
                                & (push_message[1].equals(CustomHandler.PUSH_ALL)
                                | push_message[1].equals(CustomHandler.PUSH_IMPORTANT)
                                | push_message[1].equals(CustomHandler.PUSH_ENTERTAINMENT))) {



                            pushWithUrlStudProf();

                        }


                            else if (importantEvents & push_message[1].equals(CustomHandler.PUSH_IMPORTANT))
                            pushWithUrlStudProf();
                            else if (entertainmentEvents & push_message[1].equals(CustomHandler.PUSH_ENTERTAINMENT))
                            pushWithUrlStudProf();
                        break;
                    case PUSH_WITH_CUSTOM_URL_AND_MESSAGE:
                        url = push_message[2];
                        titleNotif = push_message[3] + " ";
                        for (int i = 4; i < push_message.length; i++) {
                            titleNotif += push_message[i] + " ";
                        }

                        if ((!importantEvents & !entertainmentEvents)
                                & (push_message[1].equals(CustomHandler.PUSH_ALL)
                                | push_message[1].equals(CustomHandler.PUSH_IMPORTANT)
                                | push_message[1].equals(CustomHandler.PUSH_ENTERTAINMENT)))
                            pushWithCustomUrlAndMessage();

                        else if (importantEvents & push_message[1].equals(CustomHandler.PUSH_IMPORTANT))
                            pushWithCustomUrlAndMessage();

                        else if (entertainmentEvents & push_message[1].equals(CustomHandler.PUSH_ENTERTAINMENT))
                            pushWithCustomUrlAndMessage();

                        break;
                    case PUSH_WITH_MESSAGE:
                        for (int i = 2; i < push_message.length; i++) {
                            titleNotif += push_message[i] + " ";
                        }

                        if ((!importantEvents & !entertainmentEvents)
                                & (push_message[1].equals(CustomHandler.PUSH_ALL)
                                | push_message[1].equals(CustomHandler.PUSH_IMPORTANT)
                                | push_message[1].equals(CustomHandler.PUSH_ENTERTAINMENT))) {

                            pushWithMessage();
                        }

                        else if (importantEvents & push_message[1].equals(CustomHandler.PUSH_IMPORTANT))

                            pushWithMessage();

                        else if (entertainmentEvents & push_message[1].equals(CustomHandler.PUSH_ENTERTAINMENT))

                            pushWithMessage();

                        Log.d(TAG, titleNotif);
                        break;

                    case PUSH_WITH_URL_STUD_PROF_PHOTO:
                        url = push_message[2];
                        titleNotif = push_message[3] + " ";
                        for (int i = 4; i < push_message.length; i++) {
                            titleNotif += push_message[i] + " ";
                        }

                        if ((!importantEvents & !entertainmentEvents)
                                & (push_message[1].equals(CustomHandler.PUSH_ALL)
                                | push_message[1].equals(CustomHandler.PUSH_IMPORTANT)
                                | push_message[1].equals(CustomHandler.PUSH_ENTERTAINMENT)))

                            pushWithUrlStudProfPhoto();

                        else if (importantEvents & push_message[1].equals(CustomHandler.PUSH_IMPORTANT))

                            pushWithUrlStudProfPhoto();

                        else if (entertainmentEvents & push_message[1].equals(CustomHandler.PUSH_ENTERTAINMENT))

                            pushWithUrlStudProfPhoto();

                        Log.d(TAG, titleNotif);
                        break;
                }
            } catch (Exception ee) {

            }

            //Toast.makeText(ctx, titleNotif, Toast.LENGTH_LONG).show();

        }
    }

    private void pushWithUrlStudProf() {
        try {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx);
            mBuilder.setSmallIcon(getNotificationIcon());
            mBuilder.setContentTitle("Новое событие на сайте");
            mBuilder.setTicker(titleNotif);
            mBuilder.setContentText(titleNotif);
            mBuilder.setWhen(System.currentTimeMillis());
            if (notificationSound) mBuilder.setSound(alarmSound);
            if (notificationVibro) mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
            mBuilder.setAutoCancel(true);



            Intent resultIntent = new Intent(ctx, ActivityFeedCollapsingToolbar.class);
            resultIntent.putExtra(Constants.FEED_URL, url);
            resultIntent.putExtra(Constants.PUSHBOTS_ANALYTICS, pushAnalytics);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
            stackBuilder.addParentStack(ActivityFeedCollapsingToolbar.class);

// Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
            mNotificationManager.notify(notificationId, mBuilder.build());
        } catch (Exception e) {

        }

    }
    private void pushWithUrlStudProfPhoto() {
        try {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx);
            mBuilder.setSmallIcon(getNotificationIcon());
            mBuilder.setContentTitle("Новый фоторепортаж на сайте");
            mBuilder.setTicker(titleNotif);
            mBuilder.setContentText(titleNotif);
            mBuilder.setWhen(System.currentTimeMillis());
            if (notificationSound) mBuilder.setSound(alarmSound);
            if (notificationVibro) mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
            mBuilder.setAutoCancel(true);


            Intent resultIntent = new Intent(ctx, PhotoGallery.class);
            resultIntent.putExtra(Constants.FEED_URL_FOR_GALLERY, url);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
            stackBuilder.addParentStack(PhotoGallery.class);

// Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
            mNotificationManager.notify(notificationId, mBuilder.build());
        } catch (Exception e) {

        }

    }
    private void pushWithCustomUrlAndMessage() {
        try {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Log.d(TAG, "pushWithMessageStart");
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx);
            mBuilder.setSmallIcon(getNotificationIcon());
            mBuilder.setContentTitle("Новое событие");
            mBuilder.setTicker(titleNotif);
            mBuilder.setContentText(titleNotif);
            mBuilder.setWhen(System.currentTimeMillis());
            mBuilder.setAutoCancel(true);
            if (notificationSound) mBuilder.setSound(alarmSound);
            if (notificationVibro) mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);


            Intent resultIntent = new Intent(Intent.ACTION_VIEW);
            resultIntent.setData(Uri.parse(url));
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
            stackBuilder.addParentStack(ActivityFeedCollapsingToolbar.class);

// Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
            mNotificationManager.notify(notificationId, mBuilder.build());
            Log.d(TAG, "pushWithMessageFinish");
        } catch (Exception e) {

        }
    }
    private void pushWithMessage() {
        try {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Log.d(TAG, "pushWithMessageStart");
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx);
            mBuilder.setSmallIcon(getNotificationIcon());
            mBuilder.setContentTitle("Сообщение");
            mBuilder.setContentText(titleNotif);
            mBuilder.setWhen(System.currentTimeMillis());
            mBuilder.setAutoCancel(true);
            if (notificationSound) mBuilder.setSound(alarmSound);
            if (notificationVibro) mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);


            Intent resultIntent = new Intent(ctx, PushWithMesageActivity.class);
            resultIntent.putExtra(Constants.PUSH_MESSAGE, titleNotif);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
            stackBuilder.addParentStack(PushWithMesageActivity.class);

// Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
            mNotificationManager.notify(notificationId, mBuilder.build());
            Log.d(TAG, "pushWithMessageFinish");
        } catch (Exception e) {

        }

    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.studprof_notif_icon: R.mipmap.ic_launcher;
    }

    public class PushTask extends AsyncTask<Void, Void, Void> {

        Document doc;
        boolean docIsNull;
        Elements title;
        Elements shortDescribe;
        Elements tvDate;
        Elements picture;
        InputStream in;
        Bitmap myBitmap;

        @Override
        protected Void doInBackground(Void... params) {
            doc = null;//Здесь хранится будет разобранный html документ

            for(int i = 0; i<5; i++) {

                try {
                    //Считываем заглавную страницу http://harrix.org
                    doc = Jsoup.connect(url).ignoreContentType(true).get();
                } catch (Exception e) {
                    //Если не получилось считать
                    e.printStackTrace();
                }

            Log.d(TAG, "pushWithUrlTask");


            //Если всё считалось, что вытаскиваем из считанного html документа заголовок
                if (doc != null) {
                    docIsNull = false;
                    // задаем с какого места, выбратьзаголовки статей
                    title = doc.select("h1");  //Заголоков
                    shortDescribe = doc.select("p[style]"); //Краткое описание
                    tvDate = doc.select("p"); //Дата
                    picture = doc.select("img"); //Картинка
                    titleNotif = title.get(0).text();

                    try {
                        URL url = new URL(picture.get(6).attr("src"));
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        in = connection.getInputStream();
                        myBitmap = BitmapFactory.decodeStream(in);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "pushWithUrlTask");
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG, "OnPostExecute");
            //if(doc!=null) {

            if(title!=null) {
                Log.d(TAG, "pushWithUrlFinish");
                pushWithUrlStudProf();
            }
        }
    }
}
