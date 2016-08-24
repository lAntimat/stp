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


/**
 * Created by Ильназ on 06.11.2015.
 */
public class GalleryGridViewAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater inflator;
    ArrayList<ImagesArrayList> objects;
    String duration;
    Holder holder;
    //View rowView;
    ImagesArrayList ar;

    public GalleryGridViewAdapter(Context context, ArrayList<ImagesArrayList> ImagesArrayLists) {
        // TODO Auto-generated constructor stub
        ctx = context;
        objects = ImagesArrayLists;
        inflator = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView tv;
        ImageView img;
    }

    public ImagesArrayList getImagesArrayList(int position) {
        return ((ImagesArrayList) getItem(position));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
            Holder holder=new Holder();
            View myView = convertView;

            ar = getImagesArrayList(position);

            //if (rowView == null) {
                myView = inflator.inflate(R.layout.gallery_item_layout, parent, false);
                //rowView = lInflater.inflate(R.layout.gallery_item_layout, null);
                holder.tv = (TextView) myView.findViewById(R.id.textView1);
                holder.img = (ImageView) myView.findViewById(R.id.imageView1);



        //holder.tv.setText(ar.title);

                Uri uri = Uri.parse(ar.photoUrl);
                Picasso.with(ctx) //�������� �������� ����������
                        .load(uri)
                        .placeholder(R.drawable.imagecap)
                        .into(((ImageView) myView.findViewById(R.id.imageView1)));

        //Log.d("Хули сегенэ инде?", "Кем секкэн");





                /*rowView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Toast.makeText(ctx, "You Clicked " + String.valueOf(position), Toast.LENGTH_LONG).show();
                    }
                });

        /*ar = getImagesArrayList(position);
        View myView = convertView;
        if (convertView == null) {
            myView = inflator.inflate(R.layout.gallery_item_layout, parent, false);
            holder = new Holder();
            holder.tv = (TextView) myView.findViewById(R.id.textView1);
            holder.img = (ImageView) myView.findViewById(R.id.imageView1);
            myView.setTag(holder);
        } else {
            holder = (Holder) myView.getTag();
        }

        holder.tv.setText(ar.title);
        if (!ar.urlImage.equals(ServiceManager.NULL)) {
            Uri uri = Uri.parse(ar.urlImage);
            Picasso.with(ctx) //�������� �������� ����������
                    .load(uri)
                    .into(((ImageView) myView.findViewById(R.id.imageView1)));
        }*/

        return myView;
    }
}

