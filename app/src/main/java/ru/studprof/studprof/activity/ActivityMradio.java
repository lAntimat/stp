package ru.studprof.studprof.activity;


import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;

import org.jsoup.select.Elements;

import ru.lantimat.studprof.UpdateService;
import ru.studprof.studprof.R;


public class ActivityMradio extends AppCompatActivity {


    Drawer drawerBuilder;
    boolean playStatus = false;
    boolean isStarted = false;
    boolean radioPlay = false;
    boolean radioPlayForCurrentSong = false;
    // благодоря этому классу мы будет разбирать данные на куски
    public Elements title;
    TextView tvNowPlayDrag;
    TextView tvTitleDrag;
    ProgressBar progressBar;
    Toolbar toolbar;
    String currentSongRadio;
    ImageView imageView;
    String content;
    Intent myServiceIntent;
    Button btnPlay;
    Button btnStop;



    private BroadcastReceiver brService = null;

    UpdateService iService = null;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
            iService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            iService = UpdateService.Stub.asInterface(service);
            buttonsUpdate();

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mradio);

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
        }

        initReceiver();
        myServiceIntent = new Intent(ActivityMradio.this, PlayBackService.class);
        startService(new Intent(myServiceIntent));
        //if(brService!=null) unregisterReceiver(brService);
        bindService(new Intent(myServiceIntent), mConnection, BIND_AUTO_CREATE);

        tvTitleDrag = (TextView) findViewById(R.id.tvTitle);
        tvNowPlayDrag = (TextView) findViewById(R.id.tvNowPlay);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageView = (ImageView) findViewById(R.id.imageViewMain);
        imageView.setVisibility(View.VISIBLE);
        //Buttons
        btnPlay = (Button) findViewById(R.id.btnDragOne);
        btnPlaySetIcon(true);
        btnStop = (Button) findViewById(R.id.btnDragTwo);
        btnStop.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stop_white_24dp,0,0,0);


        //sPref = getPreferences(MODE_PRIVATE);
        //savedText = sPref.getString(playStatusString, "");
        //tvNowPlay.setText(savedText);
        /*if (savedText.equals("true")) {
            playStatus = true;
            isStarted = true;
            createPlayingMusicTimer("start");
        } else playStatus = false;*/

        imageView.setImageDrawable(getResources().getDrawable(R.drawable.mradiobig));

        btnPlay();
        btnStop();

        /*//Интент от MyService
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                myServiceReceive = intent.getStringExtra(MyService.COPA_MESSAGE);
                if (myServiceReceive.equals("playerStart")) {
                    progressBar.setVisibility(View.INVISIBLE);
                    isStarted = true;
                    //tvNowPlay.setText(currentSongRadio);

                    try {
                        iService.UpdateSrv("nameRadio" + currentSongRadio);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

            }
        };*/

    }


    private void initReceiver() {
        //Регистрация приемника
        IntentFilter filter = new IntentFilter();
        filter.addAction("AppService");
        filter.addAction("NowDuration");
        filter.addAction("PLAYER_START");
        filter.addAction(Constants.PLAYER_START);
        filter.addAction(Constants.PLAYER_PLAY);
        filter.addAction(Constants.PLAYER_PAUSE);
        filter.addAction(Constants.PLAYER_STOP);
        filter.addAction(Constants.PLAYER_CURRENT_SONG);
        filter.addAction(Constants.PLAYER_ACTIVITY_UPDATE);

        brService = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case "AppService":
                        Log.i("AppService", intent.getStringExtra("Data"));
                        break;

                    case Constants.PLAYER_START:
                        progressBar.setVisibility(View.INVISIBLE);
                        break;
                        //Текущая песня
                    case Constants.PLAYER_CURRENT_SONG:
                        tvTitleDrag.setText("ON AIR");
                        tvNowPlayDrag.setText(intent.getStringExtra(Constants.PLAYER_CURRENT_SONG));
                        break;

                    //Плеер заиграл
                    case Constants.PLAYER_ACTIVITY_UPDATE:
                        buttonsUpdate();
                        break;
                }
            }
        };

        registerReceiver(brService, filter);
    }


    private void btnPlay() {

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String pStatus = iService.UpdateSrv(Constants.PLAYER_PLAY_STATUS);
                    switch (pStatus) {
                        case Constants.PLAYER_IS_STOPPED:
                            iService.UpdateSrv(Constants.PLAYER_START);
                            btnPlaySetIcon(false);
                            progressBar.setVisibility(View.VISIBLE);
                            break;
                        case Constants.PLAYER_IS_PLAYING:
                            iService.UpdateSrv(Constants.PLAYER_PAUSE);
                            btnPlaySetIcon(true);
                            break;
                        case Constants.PLAYER_IS_PAUSED:
                        iService.UpdateSrv(Constants.PLAYER_PLAY);
                        btnPlaySetIcon(false);
                            break;
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void btnStop() {
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    iService.UpdateSrv(Constants.PLAYER_STOP);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                buttonsUpdate();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void buttonsUpdate() {
        String pStatus = null;
        try {
            pStatus = iService.UpdateSrv(Constants.PLAYER_PLAY_STATUS);
        } catch (RemoteException e) {
            Log.e("ButtonsUpdate", e.toString());
        }
        if(pStatus.equals(Constants.PLAYER_IS_PLAYING)) btnPlaySetIcon(false); //Иконка pause
        else btnPlaySetIcon(true); //иконка play
    }

    private void btnPlaySetIcon(Boolean bool) {
        if(bool) btnPlay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_white_24dp,0,0,0);
        else btnPlay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_white_24dp,0,0,0);

    }
    @Override
    protected void onStart() {
        super.onStart();
        /*LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(MyService.COPA_RESULT)
        );*/
    }

    @Override
    protected void onStop() {
        //unregisterReceiver(brService);
        //createPlayingMusicTimer("stop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        //playStatus = false;
        /*if (timer != null) createPlayingMusicTimer(ServiceManager.TIMER_STOP);
        saveToSharedPreferences(ServiceManager.PLAY_STATUS, whatPlay);*/
        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }











    /*public void createPlayingMusicTimer(String a) {
        switch (a) {
            case Constants.TIMER_START:
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Your logic here...
                        //if (!radioPlay) getDuration();
                        *//*if(radioPlay) try {
                            iService.UpdateSrv("radioSongName");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }*//*


                        // When you need to modify a UI element, do so on the UI thread.
                        // 'getActivity()' is required as this is being ran from a Fragment.
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // This code will always run on the UI thread, therefore is safe to modify UI elements.
                                if (!radioPlay) seekBar.setVisibility(View.VISIBLE);
                                if (!radioPlay & !seekBar.isFocusableInTouchMode())
                                    tvNowDuration.setText(durationNow + "/" + durationMax);
                                if (!seekBar.isFocusableInTouchMode())
                                    seekBar.setProgress(durationNowForSeekBar);
                                    seekBar.setSecondaryProgress(bufferedDur);
                            }
                        });
                    }
                }, 0, 1000); // End of your timer code.
                break;
            case Constants.TIMER_STOP:
                if (timer != null) timer.cancel();
                break;
        }
    }*/


    public void copyToClipboard(View view) {
        tvNowPlayDrag.setText(currentSongRadio);
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text", tvNowPlayDrag.getText());
        Snackbar.make(view, "Скопировано в буфер обмена", Snackbar.LENGTH_LONG).show();
        clipboard.setPrimaryClip(clip);
    }

}





