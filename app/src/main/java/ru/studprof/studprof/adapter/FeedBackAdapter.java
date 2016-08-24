package ru.studprof.studprof.adapter;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.studprof.studprof.R;

public class FeedBackAdapter extends BaseAdapter implements View.OnClickListener {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<FeedBack> objects;
    int positionGlobal;
    CircleImageView circleImageView;
    private static MyClickListener profileClickListener;

    public FeedBackAdapter(Context context, ArrayList<FeedBack> feedBacksArrayList) {
        ctx = context;
        objects = feedBacksArrayList;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return objects.size();
    }


    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // ���������� ���������, �� �� ������������ view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.list_view_item_feed_back, parent, false);
        }

        try {

            circleImageView = (CircleImageView) view.findViewById(R.id.ivDataBg);
            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profileClickListener.onItemClick(position, v);
                }
            });

            FeedBack feedBack = getComments(position);
            positionGlobal = position;


                ((TextView) view.findViewById(R.id.tvHeader)).setText(feedBack.name);
        ((TextView) view.findViewById(R.id.tvSubHeader)).setText(feedBack.comment);
        ((TextView) view.findViewById(R.id.tvPath)).setText(feedBack.feedPath);
        ((TextView) view.findViewById(R.id.tvTime)).setText(feedBack.date);
        //((ImageView) view.findViewById(R.id.ivPic)).setImageURI(Uri.parse(p.image));


            /*if(!feedBack.getCommentContentImg().equals("")) {
                view.findViewById(R.id.ivCommentContentImg).setVisibility(View.VISIBLE);
                Uri uri = Uri.parse(feedBack.getCommentContentImg());
                Picasso.with(ctx) //�������� �������� ����������
                        .load(uri)
                        .into(((ImageView) view.findViewById(R.id.ivCommentContentImg))); //������ �� ImageView

            } else view.findViewById(R.id.ivCommentContentImg).setVisibility(View.GONE);*/



            /*if(feedBack.getLikeCount().equals(""))
                view.findViewById(R.id.ivHeart).setVisibility(View.GONE);
            else view.findViewById(R.id.ivHeart).setVisibility(View.VISIBLE);

            ((TextView) view.findViewById(R.id.tvLikeCount)).setText(feedBack.likeCount);*/



            Uri uri = Uri.parse(feedBack.getImageUrl());
        Picasso.with(ctx) //�������� �������� ����������
                .load(uri)
                .into(((CircleImageView) view.findViewById(R.id.ivDataBg))); //������ �� ImageView
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    // ����� �� �������
    public FeedBack getComments(int position) {
        return ((FeedBack) getItem(position));
    }


    @Override
    public void onClick(View v) {

    }

    public void setOnItemClickListener(MyClickListener profileClickListener) {
        this.profileClickListener = profileClickListener;
    }




    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
