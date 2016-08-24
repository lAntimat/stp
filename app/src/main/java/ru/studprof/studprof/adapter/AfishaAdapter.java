package ru.studprof.studprof.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.studprof.studprof.R;

public class AfishaAdapter extends BaseAdapter implements View.OnClickListener {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Afisha> objects;
    CircleImageView circleImageView;
    private static MyClickListener profileClickListener;

    TextView tvData;

    public AfishaAdapter(Context context, ArrayList<Afisha> feedBacksArrayList) {
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
            view = lInflater.inflate(R.layout.list_view_item_my_ivents, parent, false);
        }

        try {

            tvData = (TextView) view.findViewById(R.id.tvData);

            circleImageView = (CircleImageView) view.findViewById(R.id.ivDataBg);
            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profileClickListener.onItemClick(position, v);
                }
            });

            Afisha afisha = getComments(position);

            ((TextView) view.findViewById(R.id.tvHeader)).setText(afisha.title);
            ((TextView) view.findViewById(R.id.tvSubHeader)).setText(afisha.subTitle);
            ((TextView) view.findViewById(R.id.tvFunc)).setText(afisha.func);
            ((TextView) view.findViewById(R.id.tvPlace)).setText(afisha.place);
            ((TextView) view.findViewById(R.id.tvData)).setText(afisha.data);
            ((TextView) view.findViewById(R.id.tvDayOfWeek)).setText(afisha.dayOfWeek);

            if(afisha.data.equals("99")) tvData.setVisibility(View.INVISIBLE);
            else tvData.setVisibility(View.VISIBLE); //Это делается, что бы карточки не съехали


        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    // ����� �� �������
    public Afisha getComments(int position) {
        return ((Afisha) getItem(position));
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
