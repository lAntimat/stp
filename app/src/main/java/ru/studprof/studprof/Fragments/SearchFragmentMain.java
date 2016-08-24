package ru.studprof.studprof.Fragments;

import android.app.Fragment;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.dexafree.materialList.view.MaterialListView;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import ru.studprof.studprof.R;
import ru.studprof.studprof.activity.ActivityFeedCollapsingToolbar;
import ru.studprof.studprof.activity.CommentsActivity;
import ru.studprof.studprof.activity.Constants;
import ru.studprof.studprof.adapter.BoxAdapter;
import ru.studprof.studprof.adapter.CardViewAdapterForFeed;
import ru.studprof.studprof.adapter.DataObject;
import ru.studprof.studprof.adapter.Product;


public class SearchFragmentMain extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    TextView tvLoading;
    TextView tvLoadingSearch;
    Drawer drawerBuilder;
    // благодоря этому классу мы будет разбирать данные на куски
    public Elements title;
    public Elements shortDescribe;
    public Elements tvDate;
    public Elements picture;
    public Elements profilePicture;
    public Elements feedUrl;
    public Elements titleSearch;
    public Elements shortDescribeSearch;
    public Elements tvDateSearch;
    public Elements pictureSearch;
    public Elements profilePictureSearch;
    public Elements feedUrlSearch;
    public Elements countOfVisit;
    // то в чем будем хранить данные пока не передадим адаптеру
    public ArrayList<String> titleList = new ArrayList<String>();
    // Listview Adapter для вывода данных
    private ArrayAdapter<String> adapter;
    // List view
    private ListView lv;
    ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout swipeRefreshLayoutSearch;
    Context ctx;
    ArrayList<Product> products = new ArrayList<Product>();
    ArrayList<Product> productsCard = new ArrayList<Product>();
    BoxAdapter boxAdapter;
    Toolbar toolbar;
    AccountHeader headerResult;
    ListView lvMain;
    String urlStudProf = "http://XN--D1AUCECGHM.XN--P1AI";
    String urlStudProfRussian = "http://студпроф.рф";
    String urlFeedWithoutPage = "/feed/index/?page=";



    String searchRequest = "";
    String searchRequestFull = "";

    int page;
    boolean pageLoading = false;
    boolean firstLoading = true;
    int lastTotalItemCount = 0;

    int lastPositionCardView;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private RecyclerView mRecyclerViewSearch;
    private RecyclerView.Adapter mAdapterSearch;
    private RecyclerView.LayoutManager mLayoutManagerSearch;

    private Context mContext;
    private MaterialListView mListView;
    ArrayList results;
    ArrayList resultsSearch;

    SharedPreferences prefs;

    private SearchView mSearchView;

    private MenuItem editMenuItem;
    private MenuItem microMenuItem;
    private MenuItem removeMenuItem;
    private MenuItem searchMenuItem;




    static final int SEARCH_QUERY = 0;
    static final int SEARCH_CALENDAR = 1;

    int whatSearchRequest = -1;

    final int LIST_VIEW = 0;
    final int CARD_VIEW = 1;

    final int WITHOUT_TOOLBAR = 0;
    final int COLLAPSING_TOOLBAR= 1;

    int whatViewNow;// = CARD_VIEW;





    public static SearchFragmentMain newInstance(String param1, String param2) {
        SearchFragmentMain fragment = new SearchFragmentMain();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragmentMain() {
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
        try {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_search, container, false);


            tvLoading = (TextView) view.findViewById(R.id.tvLoading);
            tvLoading.setVisibility(View.VISIBLE);

            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

            resultsSearch = new ArrayList<DataObject>();

            boxAdapter = new BoxAdapter(view.getContext(), products);
            lvMain = (ListView) view.findViewById(R.id.listView5);
            lvMain.setAdapter(boxAdapter);


            mRecyclerViewSearch = (RecyclerView) view.findViewById(R.id.my_recycler_view_search);
            mRecyclerViewSearch.setHasFixedSize(true);
            mLayoutManagerSearch = new LinearLayoutManager(getActivity());
            mRecyclerViewSearch.setLayoutManager(mLayoutManagerSearch);

            mAdapterSearch = new CardViewAdapterForFeed(productsCard);
            mRecyclerViewSearch.setAdapter(mAdapterSearch);

            prefs = PreferenceManager
                    .getDefaultSharedPreferences(view.getContext());
            whatViewNow = Integer.parseInt(prefs.getString("listPref", "1"));
            whatViewNow = CARD_VIEW;

            if (whatViewNow == LIST_VIEW) {
                mRecyclerViewSearch.setVisibility(View.GONE);
                lvMain.setVisibility(View.VISIBLE);
            } else if (whatViewNow == CARD_VIEW) {
                lvMain.setVisibility(View.GONE);
                mRecyclerViewSearch.setVisibility(View.VISIBLE);
            }

            return view;

        } catch (Exception e) {

            Toast.makeText(getActivity().getApplicationContext(), "Ты не должен был это видеть, забудь! p.s Фрагмент", Toast.LENGTH_LONG).show();
            return null;

        }
    }

    public void listViewListener() {



        lvMain.setAdapter(boxAdapter);
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(getActivity().getBaseContext());

                Intent intent;

                /*if(Integer.parseInt(prefs.getString("feedPref", "0")) == COLLAPSING_TOOLBAR)
                    intent = new Intent(getActivity().getApplicationContext(), ActivityFeedCollapsingToolbar.class);
                else intent = new Intent(getActivity().getApplicationContext(), ActivityFeedScroolView.class);*/

                intent = new Intent(getActivity().getApplicationContext(), ActivityFeedCollapsingToolbar.class);
                intent.putExtra(Constants.FEED_URL, ((CardViewAdapterForFeed) mAdapterSearch).getUrl(position));
                startActivity(intent);

                /*Toast.makeText(getApplicationContext(), "Был выбран пункт " + boxAdapter.getItemId(position),
                        Toast.LENGTH_SHORT).show();*/
            }
        });

        lvMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new MaterialDialog.Builder(view.getContext() )
                        //.title(R.string.title)
                        .theme(Theme.LIGHT)
                        .items(R.array.share_items_main)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                switch (which) {
                                    case 0:
                                        ClipboardManager _clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                        _clipboard.setText(((CardViewAdapterForFeed) mAdapterSearch).getUrl(position));

                                        Toast.makeText(view.getContext(), "Скопировано в буфер обмена", Toast.LENGTH_LONG)
                                                .show();
                                        break;

                                    case 1:
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(((CardViewAdapterForFeed) mAdapterSearch).getUrl(position)));
                                        startActivity(i);
                                        break;

                                    case 2:

                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.setType("text/plain");
                                        String textToSend = ((CardViewAdapterForFeed) mAdapterSearch).getUrl(position);
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

                return true;
            }
        });


        lvMain.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                Log.d("firstVisibleItem", String.valueOf(firstVisibleItem));
                Log.d("visibleItemCount", String.valueOf(visibleItemCount));
                Log.d("totalItemCount", String.valueOf(totalItemCount));

                if (firstVisibleItem + visibleItemCount + 2 > totalItemCount & totalItemCount != 0 & !pageLoading) {
                    parseFeed();
                    lastTotalItemCount = totalItemCount;
                    Log.d("page", String.valueOf(page));
                    pageLoading = true;
                }


            }
        });


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


                        if (!pageLoading & firstVisible>0) {
                            pageLoading = true;

                            parseFeed();

                        }
                    }

            }
        });
    }


    public void parseFeed() {
        if(whatSearchRequest==SEARCH_QUERY) new ParseFeedTaskCardSearch().execute();
        if(whatSearchRequest==SEARCH_CALENDAR) new ParseFeedTaskCardSearchCalendar();
    }

    public void setSearchRequest(String str, String searchTitleOnly, String sort) {

        whatSearchRequest = SEARCH_QUERY;

        listViewListener();
        mRecyclerViewListener();

        String urlSearchBeforeName = "/feed/search/?query=";
        String urlSearchBeforeTitleOnly = "&title_only=";
        String urlSearchBeforeSort = "&sort=";
        String urlSearchBeforePage = "&date_from=&date_to=&page=";

        page = 1;
        tvLoading.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        products.clear();
        productsCard.clear();
        resultsSearch.clear();
        boxAdapter.notifyDataSetChanged();
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


        new ParseFeedTaskCardSearch().execute();

    }

    public void clearAdapter() {

        page = 1;
        products.clear();
        productsCard.clear();
        resultsSearch.clear();
        boxAdapter.notifyDataSetChanged();
        mAdapterSearch.notifyDataSetChanged();
        tvLoading.setVisibility(View.VISIBLE);
        tvLoading.setText("Еще не ввёл запрос ты, юный падаван");

    }

    public void setSearchRequestCalendar(String str) {

        whatSearchRequest = SEARCH_CALENDAR;

        listViewListener();
        mRecyclerViewListener();

        page = 1;
        tvLoading.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        products.clear();
        productsCard.clear();
        resultsSearch.clear();
        mAdapterSearch.notifyDataSetChanged();
        searchRequestFull = urlStudProf + "/feed/" + str;


        new ParseFeedTaskCardSearchCalendar().execute();

    }



    public class ParseFeedTaskCardSearch extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {

            whatViewNow = Integer.parseInt(prefs.getString("listPref", "1"));
            whatViewNow = CARD_VIEW;

            if(whatViewNow==LIST_VIEW) {
                mRecyclerViewSearch.setVisibility(View.GONE);
                lvMain.setVisibility(View.VISIBLE);
            }
            else if(whatViewNow==CARD_VIEW) {
                lvMain.setVisibility(View.GONE);
                mRecyclerViewSearch.setVisibility(View.VISIBLE);
            }

        }

        @Override
        protected String doInBackground(String... params) {
            //Тут пишем основной код
            Document doc = null;//Здесь хранится будет разобранный html документ
            Product product2 = null;

            try {
                //Считываем заглавную страницу http://harrix.org

                doc = Jsoup.connect(searchRequestFull + String.valueOf(page)).ignoreContentType(true).get();
                Log.d("FullUrl", searchRequestFull);
            } catch (IOException e) {
                //Если не получилось считать
                e.printStackTrace();

            }

            //Log.d("DOC", doc.text());


            //Если всё считалось, что вытаскиваем из считанного html документа заголовок
            if (doc != null) {

                Elements listItemRow = doc.select("article.list-item.row");

                // задаем с какого места, я выбрал заголовке статей
                titleSearch = listItemRow.select("h3");
                shortDescribeSearch = listItemRow.select("p.mt10");
                tvDateSearch = listItemRow.select("span.list-item-subtitle");
                pictureSearch = listItemRow.select("img");
                //profilePicture = doc.select("img");
                feedUrlSearch = listItemRow.select("a[title]");

                countOfVisit = listItemRow.select("div.span10.list-item-body");



                try {

                    // и в цикле захватываем все данные какие есть на странице
                for (int i = 0; i < titleSearch.size(); i++) {

                    String[] counts = countOfVisit.get(i).text().split(" ");
                    String visitCount = counts[1];
                    String commentsCount = counts[0];

                    Log.d("feedUrl", titleSearch.text());

                    String feedUrlString = urlStudProf + feedUrlSearch.get(i).attr("href");
                    String pictureBig = pictureSearch.get(i).attr("src");
                    if(pictureBig.contains("min"))
                    pictureBig = pictureBig.replace("_min", "");

                        products.add(new Product(titleSearch.get(i).text(), shortDescribeSearch.get(i).text(), tvDateSearch.get(i).text(),
                                pictureSearch.get(i).attr("src"), feedUrlString, visitCount, commentsCount));

                        productsCard.add(new Product(titleSearch.get(i).text(), shortDescribeSearch.get(i).text(), tvDateSearch.get(i).text(),
                                pictureBig, feedUrlString, visitCount, commentsCount));

                    pageLoading = true;

                    resultsSearch.add(products);
                }
                } catch (Exception e) {

                }

                page++;

            }
            return null;
        }

        @Override
        protected void onPostExecute(final String result) {


            pageLoading = false;

            if(resultsSearch.size()<1) {
                tvLoading.setVisibility(View.VISIBLE);
                tvLoading.setText("Ничего не найдено(");
            }


            progressBar.setVisibility(View.INVISIBLE);
            boxAdapter.notifyDataSetChanged();
            mAdapterSearch.notifyDataSetChanged();


            if (mAdapterSearch != null) {
                ((CardViewAdapterForFeed) mAdapterSearch).setOnItemClickListener(new CardViewAdapterForFeed.MyClickListener() {

                    @Override
                    public void onItemClick(int position, View v) {
                        SharedPreferences prefs = PreferenceManager
                                .getDefaultSharedPreferences(getActivity().getBaseContext());

                        Intent intent;

                        /*if(Integer.parseInt(prefs.getString("feedPref", "0")) == COLLAPSING_TOOLBAR)
                            intent = new Intent(getActivity().getApplicationContext(), ActivityFeedCollapsingToolbar.class);
                        else intent = new Intent(getActivity().getApplicationContext(), ActivityFeedScroolView.class);*/
                        intent = new Intent(getActivity().getApplicationContext(), ActivityFeedCollapsingToolbar.class);


                        intent.putExtra(Constants.FEED_URL, ((CardViewAdapterForFeed) mAdapterSearch).getUrl(position));
                        startActivity(intent);

                    }


                });

                ((CardViewAdapterForFeed) mAdapterSearch).setOnButtonClickListener(new CardViewAdapterForFeed.MyClickListener() {
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
                                                _clipboard.setText(((CardViewAdapterForFeed) mAdapterSearch).getUrl(position));

                                                Toast.makeText(getActivity().getApplicationContext(), "Скопировано в буфер обмена", Toast.LENGTH_LONG)
                                                        .show();
                                                break;

                                            case 1:
                                                Intent i = new Intent(Intent.ACTION_VIEW);
                                                i.setData(Uri.parse(((CardViewAdapterForFeed) mAdapterSearch).getUrl(position)));
                                                startActivity(i);
                                                break;

                                            case 2:

                                                Intent intent = new Intent(Intent.ACTION_SEND);
                                                intent.setType("text/plain");
                                                String textToSend = ((CardViewAdapterForFeed) mAdapterSearch).getUrl(position);
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

                ((CardViewAdapterForFeed) mAdapterSearch).setOnBtnCommentClickListener(new CardViewAdapterForFeed.MyClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), CommentsActivity.class);
                        intent.putExtra(Constants.COMMENTS_URL, ((CardViewAdapterForFeed) mAdapterSearch).getUrl(position));
                        startActivity(intent);
                    }
                });


            }
        }
    }
    public class ParseFeedTaskCardSearchCalendar extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            //progressBar = (ProgressBar) findViewById(R.id.progressBar);
            //progressBar.setVisibility(View.VISIBLE);

            whatViewNow = Integer.parseInt(prefs.getString("listPref", "1"));
            whatViewNow = CARD_VIEW;

            if(whatViewNow==LIST_VIEW) {
                mRecyclerViewSearch.setVisibility(View.GONE);
                lvMain.setVisibility(View.VISIBLE);
            }
            else if(whatViewNow==CARD_VIEW) {
                lvMain.setVisibility(View.GONE);
                mRecyclerViewSearch.setVisibility(View.VISIBLE);
            }

        }

        @Override
        protected String doInBackground(String... params) {
            //Тут пишем основной код
            Document doc = null;//Здесь хранится будет разобранный html документ
            try {
                //Считываем заглавную страницу http://harrix.org

                doc = Jsoup.connect(searchRequestFull).ignoreContentType(true).get();
                Log.d("FullUrl", searchRequestFull);
            } catch (IOException e) {
                //Если не получилось считать
                e.printStackTrace();

            }

            //Log.d("DOC", doc.text());


            //Если всё считалось, что вытаскиваем из считанного html документа заголовок
            if (doc != null) {

                Elements listItemRow = doc.select("article.list-item.row");

                // задаем с какого места, я выбрал заголовке статей
                titleSearch = listItemRow.select("h3");
                shortDescribeSearch = listItemRow.select("p.mt10");
                tvDateSearch = listItemRow.select("span.list-item-subtitle");
                pictureSearch = listItemRow.select("img");
                //profilePicture = doc.select("img");
                feedUrlSearch = listItemRow.select("a[title]");

                countOfVisit = listItemRow.select("div.span10.list-item-body");



                // и в цикле захватываем все данные какие есть на странице
                for (int i = 0; i < titleSearch.size(); i++) {

                    String[] counts = countOfVisit.get(i).text().split(" ");
                    String visitCount = counts[1];
                    String commentsCount = counts[0];
                    Log.d("feedUrl", titleSearch.text());

                    try {
                        String feedUrlString = urlStudProf + feedUrlSearch.get(i).attr("href");
                        String pictureBig = pictureSearch.get(i).attr("src");
                        if(pictureBig.contains("min"))
                            pictureBig = pictureBig.replace("_min", "");
                        products.add(new Product(titleSearch.get(i).text(), shortDescribeSearch.get(i).text(), tvDateSearch.get(i).text(),
                                pictureSearch.get(i).attr("src"), feedUrlString, visitCount, commentsCount));
                        productsCard.add(new Product(titleSearch.get(i).text(), shortDescribeSearch.get(i).text(), tvDateSearch.get(i).text(),
                                pictureBig, feedUrlString, visitCount, commentsCount));
                        pageLoading = true;

                        resultsSearch.add(products);
                    }
                    catch (Exception e) {

                    }
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(final String result) {


            pageLoading = false;

            if(resultsSearch.size()<1) {
                tvLoading.setVisibility(View.VISIBLE);
                tvLoading.setText("Ничего не найдено(");
            }


            progressBar.setVisibility(View.INVISIBLE);
            boxAdapter.notifyDataSetChanged();
            mAdapterSearch.notifyDataSetChanged();


            if (mAdapterSearch != null) {
                ((CardViewAdapterForFeed) mAdapterSearch).setOnItemClickListener(new CardViewAdapterForFeed.MyClickListener() {

                    @Override
                    public void onItemClick(int position, View v) {
                        SharedPreferences prefs = PreferenceManager
                                .getDefaultSharedPreferences(getActivity().getBaseContext());

                        Intent intent;

                        /*if(Integer.parseInt(prefs.getString("feedPref", "0")) == COLLAPSING_TOOLBAR)
                            intent = new Intent(getActivity().getApplicationContext(), ActivityFeedCollapsingToolbar.class);
                        else intent = new Intent(getActivity().getApplicationContext(), ActivityFeedScroolView.class);*/

                        intent = new Intent(getActivity().getApplicationContext(), ActivityFeedCollapsingToolbar.class);

                        intent.putExtra(Constants.FEED_URL, ((CardViewAdapterForFeed) mAdapterSearch).getUrl(position));
                        startActivity(intent);

                    }


                });

                ((CardViewAdapterForFeed) mAdapterSearch).setOnButtonClickListener(new CardViewAdapterForFeed.MyClickListener() {
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
                                                _clipboard.setText(((CardViewAdapterForFeed) mAdapterSearch).getUrl(position));

                                                Toast.makeText(getActivity().getApplicationContext(), "Скопировано в буфер обмена", Toast.LENGTH_LONG)
                                                        .show();
                                                break;

                                            case 1:
                                                Intent i = new Intent(Intent.ACTION_VIEW);
                                                i.setData(Uri.parse(((CardViewAdapterForFeed) mAdapterSearch).getUrl(position)));
                                                startActivity(i);
                                                break;

                                            case 2:

                                                Intent intent = new Intent(Intent.ACTION_SEND);
                                                intent.setType("text/plain");
                                                String textToSend = ((CardViewAdapterForFeed) mAdapterSearch).getUrl(position);
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

                ((CardViewAdapterForFeed) mAdapterSearch).setOnBtnCommentClickListener(new CardViewAdapterForFeed.MyClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), CommentsActivity.class);
                        intent.putExtra(Constants.COMMENTS_URL, ((CardViewAdapterForFeed) mAdapterSearch).getUrl(position));
                        startActivity(intent);
                    }
                });

            }
        }
    }

}
