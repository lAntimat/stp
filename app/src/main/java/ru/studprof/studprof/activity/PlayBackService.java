package ru.studprof.studprof.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import ru.lantimat.studprof.UpdateService;
import ru.studprof.studprof.R;

public class PlayBackService extends Service implements MediaPlayer.OnPreparedListener, AudioManager.OnAudioFocusChangeListener{

    BroadcastReceiver broadcastReceiver;
    MediaPlayer player;
    String url = "http://31.28.27.21:8000/live"; // your URL here
    NotificationManager notificationManager;
    Timer timer;
    PendingIntent pi;
    Boolean audioFocusLossTransient = false;
    Notification notification;
    RemoteViews notificationView;


    private String currentSong = "";


    static final String PAUSE_BUTTON = "PAUSE_BUTTON";
    static final String REMOVE_BUTTON = "REMOVE_BUTTON";
    static final String TAG = "PlayBackService";

    private Integer notification_text_color = null;
    private float notification_text_size = 11;
    private final String COLOR_SEARCH_RECURSE_TIP = "SOME_SAMPLE_TEXT";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new UpdateService.Stub() {
            public String UpdateSrv(String strTest) throws RemoteException {
                switch (strTest) {
                    case Constants.PLAYER_START:
                        initMediaPlayer();
                        break;
                    case Constants.PLAYER_PLAY:
                        playerPlay();
                        break;
                    case Constants.PLAYER_PAUSE:
                        playerPause();
                        break;
                    case Constants.PLAYER_STOP:
                        playerStop();
                        break;
                    case Constants.PLAYER_PLAY_STATUS:
                        if(player!=null) {
                            if(player.isPlaying()) {
                                if(!currentSong.equals("")) sendResult(Constants.PLAYER_CURRENT_SONG, currentSong);
                                return Constants.PLAYER_IS_PLAYING;
                            }
                            else return Constants.PLAYER_IS_PAUSED;
                        } else return Constants.PLAYER_IS_STOPPED;
                }
                return null;
            }

            @Override
            public String UpdateSrv2String(String key, String str) throws RemoteException {
                return null;
            }
        };
    }

    public void sendResult(String tag, String message) {
        Intent in = new Intent(tag);
        if (message != null)
            in.putExtra(tag, message);
        sendBroadcast(in);
        //Log.d(tag, message);
    }

    @Override
    public void onCreate() {


        initReceiver();

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // could not get audio focus.
            Log.d(TAG, "not get audio focus");
        }

    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(PlayBackService.PAUSE_BUTTON);
        filter.addAction(PlayBackService.REMOVE_BUTTON);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "OnReceive");
                switch (intent.getAction()){
                    case PlayBackService.PAUSE_BUTTON:
                        if(player!=null) {
                            if(player.isPlaying()) {
                                playerPause();
                            }
                            else {
                                playerPlay();
                            }
                        }
                        break;
                    case PlayBackService.REMOVE_BUTTON:
                        playerStop();
                        break;
                }

                sendResult(Constants.PLAYER_ACTIVITY_UPDATE, "");

            }
        };

        registerReceiver(broadcastReceiver, filter);
    }

    public void initMediaPlayer() {
        if (player != null) {
            try {
                player.stop();
                player.release();
            } catch (Exception e) {

            }
        }
        createPlayingMusicTimer(Constants.TIMER_START);
        player = new MediaPlayer();
        player.setLooping(false); // Set looping
        try {
            player.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.setOnPreparedListener(this);

        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                //Toast.makeText(getBaseContext(), "what" + String.valueOf(what) + "extra" + String.valueOf(extra), Toast.LENGTH_LONG).show();
                //playerStop();
                //sendResult(ServiceManager.ACTIVITY_UPDATE, "Буду Даш");
                //sendResult(ServiceManager.PLAYER_STOP, "Буду Даш");
                return false;
            }
        });
        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                //sendResult(ServiceManager.PLAYER_BUFFERING_POSITION, String.valueOf(percent));
                if (percent > 0 & percent < 100 ) {
                    //Toast.makeText(getApplicationContext(), String.valueOf(percent) + "%", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, String.valueOf(percent));
                }
            }
        });

        player.prepareAsync();

        Log.d(TAG, "initMediaPlayer");
    }

    private void playerPlay() {
        if(player!=null) {
            player.start();
            updateNotificationRadio();
        }
    }

    private void playerPause() {
        if(player!=null) {
            player.pause();
            updateNotificationRadio();

        }
    }

    public void playerStop() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        createPlayingMusicTimer(Constants.TIMER_STOP);
        deleteNotification();
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        player.start();
        startNotificationRadio();
        sendResult(Constants.PLAYER_START, "");
        sendResult(Constants.PLAYER_CURRENT_SONG, currentSong);

    }



    private void startNotificationRadio() {
        String ns = Context.NOTIFICATION_SERVICE;
        notificationManager =
                (NotificationManager) getSystemService(ns);

        notification = new Notification(getNotificationIcon(), null,
                System.currentTimeMillis());


        notificationView = new RemoteViews(getPackageName(),
                R.layout.notification_layout_radio);


        //the intent that is started when the notification is clicked (works)
        Intent notificationIntent = new Intent(PlayBackService.this, ActivityMradio.class);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        notification.contentView = notificationView;
        notification.contentIntent = pendingNotificationIntent;
        notification.flags |= Notification.FLAG_NO_CLEAR;

        //notificationView.setImageViewResource(R.id.ivNotificationImage, R.drawable.mradio);
        notificationView.setTextViewText(R.id.tvNotivicationTitle, "MRADIO ON AIR");
        notificationView.setTextViewText(R.id.tvNotificationSongName, currentSong);
        if (player!=null) {
            if (!player.isPlaying())
                notificationView.setTextViewText(R.id.btnNotificationPause, "►");
            else notificationView.setTextViewText(R.id.btnNotificationPause, "II");
        }

        //this is the intent that is supposed to be called when the
        //button is clicked
        Intent switchIntent = new Intent(PlayBackService.REMOVE_BUTTON);
        PendingIntent removeButtonIntent = PendingIntent.getBroadcast(this, 0,
                switchIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.btnNotificationRemove,
                removeButtonIntent);

        Intent switchIntent2 = new Intent(PlayBackService.PAUSE_BUTTON);
        PendingIntent pauseButtonIntent = PendingIntent.getBroadcast(this, 0,
                switchIntent2, 0);
        notificationView.setOnClickPendingIntent(R.id.btnNotificationPause,
                pauseButtonIntent);

        notificationManager.notify(99, notification);

    }

    private void updateNotificationRadio() {

        if(notificationView!=null) {
            notificationView.setTextViewText(R.id.tvNotificationSongName, currentSong);
            if (player != null) {
                if (!player.isPlaying())
                    notificationView.setTextViewText(R.id.btnNotificationPause, "►");
                else notificationView.setTextViewText(R.id.btnNotificationPause, "II");
            }
            notificationManager.notify(99, notification);
        }

    }

    private void deleteNotification() {
        String ns = Context.NOTIFICATION_SERVICE;
        notificationManager =
                (NotificationManager) getSystemService(ns);
        if(notificationManager!=null) notificationManager.cancel(99);
        try {
            notificationManager.cancel(5);
            //notificationManager.cancelAll();
        }
        catch(Exception e) {

        }
        //notificationManager.cancelAll();
        stopForeground(true);
        Log.d("deleteNotifi", "tryOut");
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_radio_white_24dp: R.drawable.mradio;
    }

    public void createPlayingMusicTimer(String a) {
        switch (a) {
            case Constants.TIMER_START:
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Your logic here...

                            try {
                                timerTaskJson();
                            } catch (Exception e) {
                                Log.d("TimerTaskJson", e.toString());
                            }
                    }
                }, 1, 10000); // End of your timer code.
                break;
            case Constants.TIMER_STOP:
                if (timer != null) timer.cancel();
                break;
        }
    }


    public void timerTaskJson() {
        Document doc = null;
        String[] titleString;
        String[] bitrateString;
        //JSONObject jsonObject;
        try {
            //Парсим сайт
            doc = Jsoup.connect("http://31.28.27.21:8000/status-json.xsl").ignoreContentType(true).get();
        } catch (IOException e) {
            e.printStackTrace();
        }


        JSONObject jsonObject;
        String json;
        json = doc.select("body").toString();
        json = json.substring(8, json.lastIndexOf("</body>"));
        Log.d("бэтэч JSON эшли чтоли? ", json);
        //Вытягиваем нужное из json
        JsonParser parser = new JsonParser();
        JsonObject mainObject = parser.parse(json).getAsJsonObject();
        mainObject = mainObject.getAsJsonObject("icestats");
        Log.d("JSON_iceStats", mainObject.toString());
        //mainObject = mainObject.getAsJsonObject("source");
        //Log.d("JSON_source", mainObject.toString());
        JsonArray pItem = mainObject.getAsJsonArray("source");
        titleString = new String[pItem.size()];
        bitrateString = new String[pItem.size()];
        //String title = mainObject.getAsJsonObject("admin").toString();
        //Log.d("JSON", title);
        int i = 0;
        for (JsonElement user : pItem) {
            JsonObject userObject = user.getAsJsonObject();
            //System.out.println(userObject.get("title"));
            Log.d("JSON_title", String.valueOf(userObject.get("title")));
            Log.d("JSON_bitrate", String.valueOf(userObject.get("bitrate")));
            titleString[i] = String.valueOf(userObject.get("title"));
            bitrateString[i] = String.valueOf(userObject.get("bitrate"));
            i++;
        }
        if (titleString.length >= 2) {
            if (!bitrateString[0].equals("null")) {
                currentSong = "Живой эфир";
            } else {
                currentSong = titleString[titleString.length - 2];
                currentSong = currentSong.substring(1, currentSong.lastIndexOf("\""));
                currentSong = currentSong.replace("&amp;", "&");
            }


                pi = PendingIntent.getActivity(getApplicationContext(), 0,
                        new Intent(getApplicationContext(), ActivityMradio.class),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                //createNotificationRadio();
                //createNotification();
                if(player!=null) {
                    updateNotificationRadio();
                    sendResult(Constants.PLAYER_CURRENT_SONG, currentSong);
                }
            }

        }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
                //playerStop();
                playerPause();
                Log.d(TAG, "AUDIOFOCUS_LOSS");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                playerPause();
                audioFocusLossTransient = true;
                Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                if(audioFocusLossTransient) {
                    //playerPlay();
                    audioFocusLossTransient = false;
                    Log.d(TAG, "AUDIOFOCUS_GAIN");
                }
                break;
        }
    }
}
