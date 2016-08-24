package ru.studprof.studprof.adapter;


import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.studprof.studprof.R;

public class CommentsAdapter extends BaseAdapter implements View.OnClickListener {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Comments> objects;
    int positionGlobal;
    CircleImageView circleImageView;
    private static MyClickListener profileClickListener;

    public CommentsAdapter(Context context, ArrayList<Comments> commentsArrayList) {
        ctx = context;
        objects = commentsArrayList;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // ���-�� ���������
    @Override
    public int getCount() {
        return objects.size();
    }

    // ������� �� �������
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id �� �������
    @Override
    public long getItemId(int position) {
        return position;
    }

    // ����� ������
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // ���������� ���������, �� �� ������������ view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.list_view_item_comments, parent, false);
        }

        try {

            circleImageView = (CircleImageView) view.findViewById(R.id.ivDataBg);
            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profileClickListener.onItemClick(position, v);
                }
            });

            Comments comments = getComments(position);
            positionGlobal = position;

        // ��������� View � ������ ������ ������� �� �������: ������������, ����
        // � ��������
                ((TextView) view.findViewById(R.id.tvHeader)).setText(comments.name);
        ((TextView) view.findViewById(R.id.tvSubHeader)).setText(comments.comment);
        //((TextView) view.findViewById(R.id.tvDate)).setText(comments.date);
        ((TextView) view.findViewById(R.id.tvTime)).setText(comments.date);
        //((ImageView) view.findViewById(R.id.ivPic)).setImageURI(Uri.parse(p.image));

        //Показываем стрелку, если есть родитель
        if(comments.getCommentIdParent().equals("0"))
        view.findViewById(R.id.ivSubArrow).setVisibility(View.GONE);
        else view.findViewById(R.id.ivSubArrow).setVisibility(View.VISIBLE);

            Log.d("CommentsAdapter", comments.getCommentContentImg());

            if(!comments.getCommentContentImg().equals("")) {
                view.findViewById(R.id.ivCommentContentImg).setVisibility(View.VISIBLE);
                Uri uri = Uri.parse(comments.getCommentContentImg());
                Picasso.with(ctx) //�������� �������� ����������
                        .load(uri)
                        .into(((ImageView) view.findViewById(R.id.ivCommentContentImg))); //������ �� ImageView

            } else view.findViewById(R.id.ivCommentContentImg).setVisibility(View.GONE);



            /*if(comments.getLikeCount().equals(""))
                view.findViewById(R.id.ivHeart).setVisibility(View.GONE);
            else view.findViewById(R.id.ivHeart).setVisibility(View.VISIBLE);

            ((TextView) view.findViewById(R.id.tvLikeCount)).setText(comments.likeCount);*/



            Uri uri = Uri.parse(comments.getImageUrl());
        Picasso.with(ctx) //�������� �������� ����������
                .load(uri)
                .into(((CircleImageView) view.findViewById(R.id.ivDataBg))); //������ �� ImageView
        } catch (Exception e) {

        }

        //CheckBox cbBuy = (CheckBox) view.findViewById(R.id.cbBox);
        // ����������� �������� ����������
        //cbBuy.setOnCheckedChangeListener(myCheckChangList);
        // ����� �������
        //cbBuy.setTag(position);
        // ��������� ������� �� �������: � ������� ��� ���
        //cbBuy.setChecked(p.box);
        return view;
    }

    // ����� �� �������
    public Comments getComments(int position) {
        return ((Comments) getItem(position));
    }


    @Override
    public void onClick(View v) {

    }

    public void setOnItemClickListener(MyClickListener profileClickListener) {
        this.profileClickListener = profileClickListener;
    }

    // ���������� �������
    /*ArrayList<Product> getBox() {
        ArrayList<Product> box = new ArrayList<Product>();
        for (Product p : objects) {
            // ���� � �������
            if (p.box)
                box.add(p);
        }
        return box;
    }*/

    /*// ���������� ��� ���������
    OnCheckedChangeListener myCheckChangList = new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // ������ ������ ������ (� ������� ��� ���)
            getProduct((Integer) buttonView.getTag()).box = isChecked;
        }
    };*/

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
