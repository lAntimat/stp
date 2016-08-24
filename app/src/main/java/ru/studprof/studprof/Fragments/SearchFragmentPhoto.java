package ru.studprof.studprof.Fragments;

import android.app.Fragment;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.dexafree.materialList.card.Card;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import ru.studprof.studprof.R;
import ru.studprof.studprof.activity.Constants;
import ru.studprof.studprof.activity.PhotoGallery;
import ru.studprof.studprof.adapter.CardViewAdapter;
import ru.studprof.studprof.adapter.DataObject;


public class SearchFragmentPhoto extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    ArrayList<String> arrayListUrl = new ArrayList<>();
    ArrayList<String> albumUrlsArrayList = new ArrayList();
    TextView tvLoading;
    ProgressBar progressBar;

    String urlStudProf = "http://XN--D1AUCECGHM.XN--P1AI";
    String urlStudProfRussian = "http://студпроф.рф";
    String urlFeedWithoutPage = "/feed/index/?page=";
    int page = 1;

    String urlSearchBeforeName = "/photo/search/?query=";
    String urlSearchBeforePage = "&title_only=0&sort=2&date_from=&date_to=&page=";

    String searchRequest = "";
    String searchRequestFull = "";

    boolean pageLoading = false;

    private RecyclerView mRecyclerViewSearch;
    private RecyclerView.Adapter mAdapterSearch;
    private RecyclerView.LayoutManager mLayoutManagerSearch;

    ArrayList results;
    ArrayList resultsSearch;

    Boolean docIsNull = true;
    Elements title;
    Elements date;
    Elements imageUrl;
    Elements albumUrl;
    List<Card> cards;
    String[] albumUrls;
    String fullText;
    String data;
    String countOfPhotos;
    String countOfVisit;
    String shortDescription = "";
    int a = -1;
    int i2 = 0;
    String[] urls;
    DataObject obj;








    public static SearchFragmentPhoto newInstance(String param1, String param2) {
        SearchFragmentPhoto fragment = new SearchFragmentPhoto();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragmentPhoto() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);



        resultsSearch = new ArrayList<DataObject>();

        tvLoading = (TextView) view.findViewById(R.id.tvLoading);
        tvLoading.setVisibility(View.VISIBLE);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);


        mRecyclerViewSearch = (RecyclerView) view.findViewById(R.id.my_recycler_view_search);
        mRecyclerViewSearch.setHasFixedSize(true);
        mLayoutManagerSearch = new LinearLayoutManager(getActivity());
        mRecyclerViewSearch.setLayoutManager(mLayoutManagerSearch);


        mAdapterSearch = new CardViewAdapter(resultsSearch);
        mRecyclerViewSearch.setAdapter(mAdapterSearch);


        return view;
    }

    public void mRecyclerViewListener() {
        mRecyclerViewSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                int firstVisible = layoutManager.findFirstVisibleItemPosition();
                int visibleCount = Math.abs(firstVisible - layoutManager.findLastVisibleItemPosition());
                int itemCount = recyclerView.getAdapter().getItemCount();
//                totalItemCount = mLayoutManager.getItemCount();

                Log.d("firstVisible", String.valueOf(firstVisible));
                Log.d("visibleCount", String.valueOf(visibleCount));
                Log.d("itemCount", String.valueOf(itemCount));

                if ((firstVisible + visibleCount + 3) >= itemCount) {



                    if (!pageLoading & firstVisible>2) {
                        pageLoading = true;
                        new ParseFeedTask().execute();
                    }


                }
            }
        });
    }



    public void setSearchRequest(String str, String searchTitleOnly, String sort) {

        mRecyclerViewListener();

        String urlSearchBeforeName = "/photo/search/?query=";
        String urlSearchBeforeTitleOnly = "&title_only=";
        String urlSearchBeforeSort = "&sort=";
        String urlSearchBeforePage = "&date_from=&date_to=&page=";

        page = 1;
        tvLoading.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        resultsSearch.clear();
        mAdapterSearch.notifyDataSetChanged();

        String searchString = "";
        try {
            searchString = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        searchRequestFull = urlStudProf + urlSearchBeforeName + searchString + urlSearchBeforeTitleOnly + searchTitleOnly +
                urlSearchBeforeSort + sort + urlSearchBeforePage;
        Log.d("FullUrl", searchRequestFull);
        new ParseFeedTask().execute();

    }

    public void clearAdapter() {

        tvLoading.setVisibility(View.VISIBLE);
        resultsSearch.clear();
        obj = null;
        mAdapterSearch.notifyDataSetChanged();

    }



    public class ParseFeedTask extends AsyncTask<String, Void, String> {




        @Override
        protected void onPreExecute() {
            //progressBar = (ProgressBar) findViewById(R.id.progressBar);
            //progressBar.setVisibility(View.VISIBLE);
//            progressBarPhotoBg.setVisibility(View.VISIBLE);
        }




        @Override
        protected String doInBackground(String... params) {

            //Тут пишем основной код
            Document doc = null;//Здесь хранится будет разобранный html документ
            try {
                //Считываем заглавную страницу http://harrix.org
                doc = Jsoup.connect(searchRequestFull +  String.valueOf(page)).ignoreContentType(true).get();
            } catch (IOException e) {
                //Если не получилось считать
                e.printStackTrace();

            }

            //Если всё считалось, что вытаскиваем из считанного html документа заголовок
            if (doc != null) {

                docIsNull = false;

                title = doc.select("h3");
                date = doc.select("div.tbg.photo-item-description");

                imageUrl = doc.select("img[alt]");
                albumUrl = doc.select("a.span8.photo-item-image");


                for(int i = 0; i<albumUrl.size(); i++) {

                    albumUrlsArrayList.add(albumUrl.get(i).attr("href"));
                }




                /*cards = new ArrayList<>();
                for (int i = 0, ii = 1; i < title.size(); i++, ii +=9) {
                    cards.add(bigImageCard("", title.get(i).text(), imageUrl.get(ii).attr("src"), albumUrl.get(i).attr("href")));
                }*/

                urls = new String[imageUrl.size()];

                for(int i = 0; i< imageUrl.size(); i++) {
                    Log.d("urls", imageUrl.get(i).attr("src"));
                    if (imageUrl.get(i).attr("src").contains("min_")) {
                        arrayListUrl.add(imageUrl.get(i).attr("src"));
                    }
                }



                for (int i2 = 1; i2 < (imageUrl.size() / 8) - 1; i2++) {
                    for (int i = -8 + 8 * i2; i < 8 * i2; i++) {

                        try {
                            urls[i2-1] += arrayListUrl.get(i) + "trim";
                        }
                        catch (Exception e) {

                            Log.d("Expection", String.valueOf(e));
                        }
                        //urlPicture += imageUrl.get(i + 1).attr("src") + "trim";

                    }
                }


                for (int i = 0; i < title.size(); i++) {

                    if(date.size()>i) fullText = date.get(i).text();


                    String[] parts = fullText.split(" ");



                    //Число
                    data = parts[0] + " " + parts[1] + " " + parts[2];

                    // Количество фотографий
                    countOfPhotos = parts[3];

                    if(data.length()>5) {
                        // Количество фотографий
                        countOfPhotos = parts[3];
                        // Количество посещений
                        countOfVisit = parts[5];
                    }

                    //Собираем краткое описание
                    for(int j = 6; j<parts.length; j++) {
                        shortDescription += parts[j] + " ";
                    }


                    String whoPhoto = "";
                    int indexWhoPhoto = fullText.indexOf("Фото");
                    if(indexWhoPhoto!=-1) {
                        whoPhoto = fullText.substring(indexWhoPhoto);
                    }

                    int indexForSubstring = shortDescription.indexOf("Фото");
                    String shortDescriptionClear = shortDescription;

                    if(indexForSubstring!=-1)
                        shortDescriptionClear = shortDescriptionClear.substring(0, indexForSubstring);

                    try {
                        obj = new DataObject(whoPhoto, title.get(i).text(), shortDescriptionClear, data, urls[i], countOfPhotos, countOfVisit, albumUrl.get(i).attr("href"));
                        resultsSearch.add(obj);
                    }
                    catch (Exception e) {

                    }

                    shortDescription = "";

                }
                page++;
                arrayListUrl.clear();



            } else docIsNull = true;


            return null;
        }

        @Override
        protected void onPostExecute(String result) {




            mAdapterSearch.notifyDataSetChanged();

            pageLoading = false;

            progressBar.setVisibility(View.INVISIBLE);

            if(resultsSearch.size()<1) {
                tvLoading.setVisibility(View.VISIBLE);
                tvLoading.setText("Ничего не найдено(");
            }


            if (mAdapterSearch != null) {
                ((CardViewAdapter) mAdapterSearch).setOnItemClickListener(new CardViewAdapter
                        .MyClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {
                        //Log.i(LOG_TAG, " Clicked on Item " + position);

                        Intent intent = new Intent(getActivity(), PhotoGallery.class);
                        //Url нужно отправлять без основного домена
                        // /photo/.....
                        intent.putExtra(Constants.FEED_URL_FOR_GALLERY, albumUrlsArrayList.get(position));
                        startActivity(intent);

                    }
                });

                ((CardViewAdapter) mAdapterSearch).setOnButtonClickListener(new CardViewAdapter.MyClickListener() {
                    @Override
                    public void onItemClick(final int position, View v) {
                        //Log.i(LOG_TAG, " Clicked on Button " + position);

                        new MaterialDialog.Builder(getActivity())
                                //.title(R.string.title)
                                .theme(Theme.LIGHT)
                                .items(R.array.share_items_main)
                                .itemsCallback(new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                        switch (which) {
                                            case 0:
                                                ClipboardManager _clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                                _clipboard.setText(albumUrlsArrayList.get(position));

                                                Toast.makeText(getActivity().getApplicationContext(), "Скопировано в буфер обмена", Toast.LENGTH_LONG)
                                                        .show();
                                                break;

                                            case 1:
                                                Intent i = new Intent(Intent.ACTION_VIEW);
                                                i.setData(Uri.parse(urlStudProfRussian + albumUrlsArrayList.get(position)));
                                                startActivity(i);
                                                break;

                                            case 2:

                                                Intent intent = new Intent(Intent.ACTION_SEND);
                                                intent.setType("text/plain");
                                                String textToSend = (urlStudProfRussian + albumUrlsArrayList.get(position));
                                                textToSend = textToSend.replace(urlStudProf, urlStudProfRussian);
                                                intent.putExtra(Intent.EXTRA_TEXT, textToSend);
                                                try {
                                                    startActivity(Intent.createChooser(intent, "Поделиться"));
                                                } catch (android.content.ActivityNotFoundException ex) {
                                                    Toast.makeText(getActivity().getApplicationContext(), "Some error", Toast.LENGTH_SHORT).show();
                                                }

                                                break;
                                        }
                                    }
                                })
                                .show();

                    }
                });
            }

        }


    }


}
