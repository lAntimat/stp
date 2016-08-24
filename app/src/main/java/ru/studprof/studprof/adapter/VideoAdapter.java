package ru.studprof.studprof.adapter;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ru.studprof.studprof.R;

public class VideoAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<VideoDescribe> objects;

    public VideoAdapter(Context context, ArrayList<VideoDescribe> video) {
        ctx = context;
        objects = video;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        // ���������� ���������, �� �� ������������ view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.list_view_item_video, parent, false);
        }

        VideoDescribe video = getVideo(position);

        // ��������� View � ������ ������ ������� �� �������: ������������, ����
        // � ��������
        ((TextView) view.findViewById(R.id.tvHeader)).setText(video.name);
        ((TextView) view.findViewById(R.id.tvDuration)).setText(video.duration + "");
        ((TextView) view.findViewById(R.id.tvDate)).setText(video.date + "");
        //((ImageView) view.findViewById(R.id.ivPic)).setImageURI(Uri.parse(p.image));
        Uri uri = Uri.parse(video.getImageUrl());
        Picasso.with(ctx) //�������� �������� ����������
                .load(uri)
                .into(((ImageView) view.findViewById(R.id.ivPic))); //������ �� ImageView

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
    public VideoDescribe getVideo(int position) {
        return ((VideoDescribe) getItem(position));
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
}
