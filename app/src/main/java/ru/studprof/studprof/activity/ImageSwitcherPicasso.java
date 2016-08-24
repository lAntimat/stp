package ru.studprof.studprof.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import ru.studprof.studprof.R;

/**
 * Created by Ильназ on 22.12.2015.
 */
public class ImageSwitcherPicasso implements Target {

    private ImageSwitcher mImageSwitcher;
    private Context mContext;
    private ProgressBar mProgressBar;
    private ImageView mImageView;

    public ImageSwitcherPicasso(Context context, ImageSwitcher imageSwitcher){
        mImageSwitcher = imageSwitcher;
        mContext = context;
    }

    public void ImageSwitcherPicassoProgressBar(ProgressBar progressBar) {
        mProgressBar = progressBar;
    }

    public void onPreStart() {
        mImageSwitcher.setImageResource(R.drawable.malevich);
    }

    public void ImageSwitcherPicassoImageView(ImageView imageView) {
        mImageView = imageView;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mImageSwitcher.setBackgroundColor(0xFF000000);
        mImageSwitcher.setImageDrawable(new BitmapDrawable(mContext.getResources(), bitmap));
    }

    @Override
    public void onBitmapFailed(Drawable drawable) {

    }

    @Override
    public void onPrepareLoad(Drawable drawable) {

    }

}
