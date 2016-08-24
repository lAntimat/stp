package ru.studprof.studprof.adapter;


import java.util.ArrayList;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ru.studprof.studprof.R;

public class BoxAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Product> objects;

    public BoxAdapter(Context context, ArrayList<Product> products) {
        ctx = context;
        objects = products;
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
            view = lInflater.inflate(R.layout.list_view_item_main, parent, false);
        }

        Product p = getProduct(position);

        // ��������� View � ������ ������ ������� �� �������: ������������, ����
        // � ��������
        ((TextView) view.findViewById(R.id.tvHeader)).setText(p.name);
        ((TextView) view.findViewById(R.id.tvShortDescription)).setText(p.price + "");
        ((TextView) view.findViewById(R.id.tvDate)).setText(p.date + "");
        //((ImageView) view.findViewById(R.id.ivPic)).setImageURI(Uri.parse(p.image));
        Uri uri = Uri.parse(p.image);
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
    public Product getProduct(int position) {
        return ((Product) getItem(position));
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
