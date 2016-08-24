package ru.studprof.studprof.activity;

/**
 * Created by Ильназ on 23.12.2015.
 */

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import ru.studprof.studprof.R;
import ru.studprof.studprof.adapter.FullScreenImageAdapter;

public class FullScreenViewActivity extends Activity {

    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;
    private ArrayList<String> arrayListUrl = new ArrayList<>();
    TextView textView;
    Button btnMenu;
    ImageButton imageButton;
    ProgressBar progressBar;
    int position;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    String imageName2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.full_screen_image_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_view);


        viewPager = (ViewPager) findViewById(R.id.pager);
        textView = (TextView) findViewById(R.id.textView);
        btnMenu = (Button) findViewById(R.id.btnMenu);
        progressBar = (ProgressBar) findViewById(R.id.progressBarActivityFullscreen);

        imageButton = (ImageButton) findViewById(R.id.imageButton);

        btnMenu.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dots_vertical_white_24dp, 0, 0, 0);
        imageButton.setImageResource(R.drawable.ic_dots_vertical_white_24dp);


        Intent i = getIntent();
        position = i.getIntExtra("position", 0);
        arrayListUrl = i.getStringArrayListExtra("date");

        adapter = new FullScreenImageAdapter(FullScreenViewActivity.this, arrayListUrl, textView, progressBar);
        //utils.getFilePaths());


        viewPager.setAdapter(adapter);

        // displaying selected image first
        viewPager.setCurrentItem(position);
        textView.setText((position + 1) + "/" + (arrayListUrl.size()));
        viewPager.getCurrentItem();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                progressBar.setVisibility(View.VISIBLE);
                textView.setText((position + 1) + "/" + (arrayListUrl.size()));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void openMenu(View view) {
        openOptionsMenu();
    }

    public void menu(View view) {
        new MaterialDialog.Builder(FullScreenViewActivity.this)
                //.title(R.string.title)
                .theme(Theme.LIGHT)
                .items(R.array.share_items_full_size_photo)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        int currentPosition = viewPager.getCurrentItem();


                        switch (which) {
                            case 0:
                                ClipboardManager _clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                _clipboard.setText(arrayListUrl.get(viewPager.getCurrentItem()));

                                Toast.makeText(getApplicationContext(), "Скопировано в буфер обмена", Toast.LENGTH_LONG)
                                        .show();
                                break;

                            case 1:
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(arrayListUrl.get(viewPager.getCurrentItem())));
                                startActivity(i);
                                break;

                            case 2:

                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                String textToSend = (arrayListUrl.get(viewPager.getCurrentItem()));
                                //textToSend = textToSend.replace(urlStudProf, urlStudProfRussian);
                                intent.putExtra(Intent.EXTRA_TEXT, textToSend);
                                try {
                                    startActivity(Intent.createChooser(intent, "Поделиться"));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(getApplicationContext(), "Some error", Toast.LENGTH_SHORT).show();
                                }

                                break;
                            case 3:

                                //imageDownload(FullScreenViewActivity.this, arrayListUrl.get(viewPager.getCurrentItem()));

                                imageName2 = arrayListUrl.get(viewPager.getCurrentItem());
                                imageName2 = imageName2.substring(imageName2.lastIndexOf("/") + 1);
                                new DownloadFile().execute(arrayListUrl.get(viewPager.getCurrentItem()));

                                break;
                        }
                    }
                })
                .show();
    }

    //save image
    public static void imageDownload(Context ctx, String url){

        Picasso.with(ctx)
                .load(Uri.parse(url))
                .into(getTarget(url));
    }

    //target to save
    private static Target getTarget(final String url){

        Target target = new Target(){

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        File file = new File(
                                Environment.getExternalStorageDirectory().getPath()
                                        + "/Download" + "/saved.jpg");
                        try {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                            ostream.close();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                Log.d("Picasso", "Start");


            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

                Log.d("Picasso", "Fail");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

                Log.d("Picasso", "Success");


            }
        };
        return target;
    }

    class DownloadFile extends AsyncTask<String,Integer,Long> {
        ProgressDialog mProgressDialog = new ProgressDialog(FullScreenViewActivity.this);// Change Mainactivity.this with your activity name.
        String strFolderName;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setMessage("Загрузка");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.show();

            int id = 1;

            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            String PATH = Environment.getExternalStorageDirectory()+ "/"+"СТУДПРОФ.РФ"+"/";
            File file = new File(PATH+imageName2); // set your audio path
            intent.setDataAndType(Uri.fromFile(file), "image/*");

            try {
                //MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file.getAbsoluteFile())));
            } catch (Exception e) {
                e.printStackTrace();
            }

            PendingIntent pIntent = PendingIntent.getActivity(FullScreenViewActivity.this, 0, intent, 0);

            mNotifyManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(getApplicationContext());
            mBuilder.setContentTitle(imageName2)
                    .setContentText("Загрузка изображения")
                    .setSmallIcon(R.drawable.ic_download_white_24dp)
                    .setAutoCancel(true)
                    .setContentIntent(pIntent);

            //Toast.makeText(getApplicationContext(), "Загрузка изображения", Toast.LENGTH_SHORT).show();


        }
        @Override
        protected Long doInBackground(String... aurl) {
            int count;
            try {
                URL url = new URL((String) aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                String imageName = (String) aurl[0];
                imageName = imageName.substring(imageName.lastIndexOf("/") + 1);
                Log.d("ImageName", imageName);
                String targetFileName= String.valueOf(imageName) ;//Change name and subname
                int lenghtOfFile = conexion.getContentLength();
                String PATH = Environment.getExternalStorageDirectory()+ "/"+"СТУДПРОФ.РФ"+"/";
                File folder = new File(PATH);
                if(!folder.exists()){
                    folder.mkdir();//If there is no folder it will be created.
                }
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(PATH+targetFileName);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress ((int)(total*100/lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Ошибка: " + e.toString(), Toast.LENGTH_LONG).show();
            }
            return null;
        }
        protected void onProgressUpdate(Integer... progress) {
            //mBuilder.setProgress(100, progress[0], false);
            // Displays the progress bar for the first time.
            //mNotifyManager.notify(1, mBuilder.build());
            //mBuilder.setProgress(100, progress[0], false);
                mProgressDialog.setProgress(progress[0]);
            if(progress[0]==100){
                mProgressDialog.dismiss();
                //Toast.makeText(getApplicationContext(), "File Downloaded", Toast.LENGTH_SHORT).show();
                mBuilder.setContentText("Загрузка завершена")
                        // Removes the progress bar
                        .setProgress(0,0,false);
                mNotifyManager.notify(1, mBuilder.build());
            }
        }
        protected void onPostExecute(String result) {
        }
    }
}
