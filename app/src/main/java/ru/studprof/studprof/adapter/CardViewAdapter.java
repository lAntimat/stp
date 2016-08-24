package ru.studprof.studprof.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ru.studprof.studprof.R;

public class CardViewAdapter extends RecyclerView
        .Adapter<CardViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<DataObject> mDataset;
    private static MyClickListener myClickListener;
    private static MyClickListener btnClickListener;
    Context ctx;


    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView label;
        TextView shortDescribe;
        TextView dateTime;
        TextView data;
        ImageView ivView;
        ImageView ivView2;
        ImageView ivView3;
        ImageView ivView4;
        ImageView ivView5;
        ImageView ivView6;
        ImageView ivView7;
        ImageView ivView8;
        ImageView ivView9;
        Button btnOpen;
        Layout layoutDiider;
        TextView tvPhotoCount;
        TextView tvVisitCount;

        public DataObjectHolder(View itemView) {
            super(itemView);



            label = (TextView) itemView.findViewById(R.id.textView);

            shortDescribe = (TextView) itemView.findViewById(R.id.textViewShortDescribe);

            dateTime = (TextView) itemView.findViewById(R.id.textView2);

            data = (TextView) itemView.findViewById(R.id.tvData);

            ivView = (ImageView) itemView.findViewById(R.id.ivView_cardView);
            ivView2 = (ImageView) itemView.findViewById(R.id.imageView2);
            ivView3 = (ImageView) itemView.findViewById(R.id.imageView3);
            ivView4 = (ImageView) itemView.findViewById(R.id.imageView4);
            ivView5 = (ImageView) itemView.findViewById(R.id.imageView5);
            ivView6 = (ImageView) itemView.findViewById(R.id.imageView6);
            ivView7 = (ImageView) itemView.findViewById(R.id.imageView7);
            ivView8 = (ImageView) itemView.findViewById(R.id.imageView8);
            ivView9 = (ImageView) itemView.findViewById(R.id.imageView9);
            tvPhotoCount = (TextView) itemView.findViewById(R.id.tvPhotoCount);
            tvVisitCount = (TextView) itemView.findViewById(R.id.tvVisitCount);


            btnOpen = (Button) itemView.findViewById(R.id.btn_card_photo);
            btnOpen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dots_vertical_grey600_24dp, 0, 0, 0);


            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
            btnOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnClickListener.onItemClick(getAdapterPosition(), v);
                }
            });
        }



        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }




    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public void setOnButtonClickListener(MyClickListener buttonClickListener) {
        this.btnClickListener = buttonClickListener;
    }



    public CardViewAdapter(ArrayList<DataObject> myDataset) {
        mDataset = myDataset;
    }

    public String getUrl(int position) {
        return  mDataset.get(position).getmAlbumUrl();
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_photo_row, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        ctx = parent.getContext();
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        try {
            holder.label.setText(mDataset.get(position).getTitle());
            holder.shortDescribe.setText(mDataset.get(position).getShortDescription());
            holder.dateTime.setText(mDataset.get(position).getWhoPhoto());
            holder.data.setText(mDataset.get(position).getmData());
            holder.tvPhotoCount.setText((mDataset.get(position).getmCountOfPhotos()));
            holder.tvVisitCount.setText((mDataset.get(position).getmCountOfVisit()));
        } catch (Exception e) {

        }

        //if (mDataset.get(0).getmImgUrl(position) != null) {




        /*if(position<10) position2 = position * 8;
        if(position>=10) {
            position2 = position;

            do {
                position2 = position/10;
            }
            while(position2>=10);

        }*/

        String url = mDataset.get(position).getmImgUrl();
        if(url!=null) {
            if (!url.equals("")) {
                String urls[] = url.split("trim");


                Uri uri2 = Uri.parse(urls[0].substring(4));
                Uri uri3 = Uri.parse(urls[1]);
                Uri uri4 = Uri.parse(urls[2]);
                Uri uri5 = Uri.parse(urls[3]);
                Uri uri6 = Uri.parse(urls[4]);
                Uri uri7 = Uri.parse(urls[5]);
                Uri uri8 = Uri.parse(urls[6]);
                Uri uri9 = Uri.parse(urls[7]);
                //Uri uri10 = Uri.parse(mDataset.get(position).getmImgUrl(position+9));
                //Uri uri11 = Uri.parse(mDataset.get(position).getmImgUrl(position+10));
                //Uri uri12 = Uri.parse(mDataset.get(position).getmImgUrl(position+11));
                //Uri uri13 = Uri.parse(mDataset.get(position).getmImgUrl(position+12));

                Picasso.with(ctx).load(uri2).placeholder(R.drawable.imagecap164).into(holder.ivView2);
                Picasso.with(ctx).load(uri3).placeholder(R.drawable.imagecap164).into(holder.ivView3);
                Picasso.with(ctx).load(uri4).placeholder(R.drawable.imagecap164).into(holder.ivView4);
                Picasso.with(ctx).load(uri5).placeholder(R.drawable.imagecap164).into(holder.ivView5);
                Picasso.with(ctx).load(uri6).placeholder(R.drawable.imagecap164).into(holder.ivView6);
                Picasso.with(ctx).load(uri7).placeholder(R.drawable.imagecap164).into(holder.ivView7);
                Picasso.with(ctx).load(uri8).placeholder(R.drawable.imagecap164).into(holder.ivView8);
                Picasso.with(ctx).load(uri9).placeholder(R.drawable.imagecap164).into(holder.ivView9);
                // Picasso.with(ctx).load(uri10).into(holder.ivView10);
                //Picasso.with(ctx).load(uri11).into(holder.ivView11);
                //Picasso.with(ctx).load(uri12).into(holder.ivView12);
                //Picasso.with(ctx).load(uri13).into(holder.ivView13);

            }
        }
    }
    //}

    public void addItem(DataObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {

       if(mDataset!=null) return mDataset.size();
        else return 0;
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}