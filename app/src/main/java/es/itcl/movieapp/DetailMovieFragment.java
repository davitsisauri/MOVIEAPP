package es.itcl.movieapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;



public class DetailMovieFragment extends Fragment {

    private ArrayList<Trailer> trailers;
    private ArrayList<Review> reviews;
    AdapterMovieTrailer mMovieTrailerAdapter;
    AdapterMovieReview mMovieReviewAdapter;

    private int idMovie;

    private ProgressDialog pDialog;

    private TextView txtTitle;
    private TextView txtVoteAverage;
    private TextView txtYear;
    private TextView txtOverview;
    private ImageView imgPoster;
    private RatingBar rtbFavorite;

    private TabHost tabs;

    private int selectedTab=0;

    public DetailMovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_movie, container, false);
        txtTitle = (TextView) rootView.findViewById(R.id.txtTitleMovie);
        txtVoteAverage = (TextView) rootView.findViewById(R.id.txtVoteAverage);
        txtYear = (TextView) rootView.findViewById(R.id.txtYear);
        txtOverview = (TextView) rootView.findViewById(R.id.txtOverview);
        imgPoster = (ImageView) rootView.findViewById(R.id.imgPoster);
        rtbFavorite = (RatingBar) rootView.findViewById(R.id.rtbFavorite);
        tabs = (TabHost) rootView.findViewById(android.R.id.tabhost);

        if (savedInstanceState != null) {
            selectedTab = savedInstanceState.getInt("selectedTab");
        }

        Resources res = getResources();
        tabs.setup();

        TabHost.TabSpec spec = tabs.newTabSpec("mitab1");
        spec.setContent(R.id.tab1);
        spec.setIndicator(getResources().getString(R.string.label_trailers));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("mitab2");
        spec.setContent(R.id.tab2);
        spec.setIndicator(getResources().getString(R.string.label_reviews));
        tabs.addTab(spec);

        tabs.setCurrentTab(selectedTab);

        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                selectedTab = tabs.getCurrentTab();
            }
        });

        rtbFavorite.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float favorite;
                    if (rtbFavorite.getRating() == 0)
                        favorite = 1;
                    else
                        favorite = 0;

                    if (getActivity().getSupportFragmentManager().findFragmentById(R.id.frgMovieList) != null && getActivity().getSupportFragmentManager().findFragmentById(R.id.frgMovieDetail) != null && favorite==0 ) {
                        getActivity().getSupportFragmentManager().findFragmentById(R.id.frgMovieList).onResume();
                    }


                    rtbFavorite.setRating(favorite);
                    SharedPreferences prefs =
                            getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putFloat(String.valueOf(idMovie), favorite);
                    editor.commit();
                }
                return true;
            }
        });

        trailers = new ArrayList<>();


        if (trailers == null)
            trailers = new ArrayList<>();

        mMovieTrailerAdapter = new AdapterMovieTrailer(getActivity());

        ListView listView = (ListView) rootView.findViewById(R.id.lstTrailers);
        listView.setAdapter(mMovieTrailerAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Trailer trailer = ((Trailer) a.getAdapter().getItem(position));
                Intent intVideoYoutube = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                startActivity(intVideoYoutube);
            }
        });


        reviews = new ArrayList<>();

        if (reviews == null)
            reviews = new ArrayList<>();

        mMovieReviewAdapter = new AdapterMovieReview(getActivity());

        ListView listViewReviews = (ListView) rootView.findViewById(R.id.lstReviews);
        listViewReviews.setAdapter(mMovieReviewAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity().getSupportFragmentManager().findFragmentById(R.id.frgMovieDetail) != null) {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.show(manager.findFragmentById(R.id.frgMovieDetail));
            transaction.commit();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedTab", selectedTab);
    }


    class AdapterMovieTrailer extends BaseAdapter {

        Activity context;

        AdapterMovieTrailer(Activity context) {
            this.context = context;
        }

        public int getCount() {
            return trailers.size();
        }

        public Object getItem(int position) {
            return trailers.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View item = convertView;
            TextView txtTitle;

            if (item == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                item = inflater.inflate(R.layout.listitem_trailer, null);
                txtTitle = (TextView) item.findViewById(R.id.txtTrailer);
                item.setTag(txtTitle);
            } else {
                txtTitle = (TextView) item.getTag();
            }

            txtTitle.setText(trailers.get(position).getName());
            return item;
        }
    }



    class AdapterMovieReview extends BaseAdapter {

        Activity context;

        AdapterMovieReview(Activity context) {
            this.context = context;
        }

        public int getCount() {
            return reviews.size();
        }

        public Object getItem(int position) {
            return reviews.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View item = convertView;
            ViewHolder holder;

            if (item == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                item = inflater.inflate(R.layout.listitem_review, null);
                holder = new ViewHolder();
                holder.txtAuthor = (TextView) item.findViewById(R.id.txtAuthor);
                holder.txtContent = (TextView) item.findViewById(R.id.txtReview);
                item.setTag(holder);
            } else {
                holder = (ViewHolder) item.getTag();
            }


            holder.txtAuthor.setText(reviews.get(position).getAuthor() + " :");
            holder.txtAuthor.setPaintFlags(holder.txtAuthor.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
            holder.txtContent.setText(reviews.get(position).getContent());

            return item;
        }
    }

    static class ViewHolder {
        TextView txtContent;
        TextView txtAuthor;
    }


    public class FetchMovieTrailerTask extends AsyncTask<Void, Integer, ArrayList<Trailer>> {


        @Override
        protected void onPostExecute(ArrayList<Trailer> result) {


            if (result != null) {
                trailers = result;
                mMovieTrailerAdapter.notifyDataSetChanged();
            }
            pDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();
            pDialog.setProgress(progreso);
        }

        @Override
        protected void onPreExecute() {
            pDialog.setProgress(0);
            pDialog.show();
        }


        protected ArrayList<Trailer> doInBackground(Void... urls) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieTrailerJsonStr = null;

            try {

                final String URL = "http://api.themoviedb.org/3/movie/" + idMovie + "/videos?";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(URL).buildUpon()
                        .appendQueryParameter(API_KEY, "2ccef3b0048491e18f7c3765dd8762c8").build();


                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                movieTrailerJsonStr = buffer.toString();
                try {
                    return getMovieTrailerDataFromJson(movieTrailerJsonStr);


                } catch (Exception ex) {
                    Log.i("MOVIE", ex.getMessage());
                }


            } catch (IOException e) {
                Log.e("MOVIE", "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MOVIE", "Error closing stream", e);
                    }
                }
            }
            return null;
        }


        private ArrayList<Trailer> getMovieTrailerDataFromJson(String movieTrailerJsonStr)
                throws JSONException {

            final String OWM_RESULTS = "results";
            final String OWN_ID = "id";
            final String OWN_KEY = "key";
            final String OWN_NAME = "name";


            JSONObject movieTrailerJson = new JSONObject(movieTrailerJsonStr);
            JSONArray movieTrailerArray = movieTrailerJson.getJSONArray(OWM_RESULTS);


            ArrayList<Trailer> resultTrailerMovie = new ArrayList<Trailer>();
            for (int i = 0; i < movieTrailerArray.length(); i++) {
                JSONObject movieTrailerObject = movieTrailerArray.getJSONObject(i);
                Trailer trailer = new Trailer();

                trailer.setID(movieTrailerObject.getString(OWN_ID));
                trailer.setKey(movieTrailerObject.getString(OWN_KEY));
                trailer.setName(movieTrailerObject.getString(OWN_NAME));

                resultTrailerMovie.add(trailer);
            }

            return resultTrailerMovie;

        }

    }


    public class FetchMovieReviewTask extends AsyncTask<Void, Integer, ArrayList<Review>> {


        @Override
        protected void onPostExecute(ArrayList<Review> result) {


            if (result != null) {
                reviews = result;
                mMovieReviewAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();
        }



        protected ArrayList<Review> doInBackground(Void... urls) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieReviewJsonStr = null;

            try {

                final String URL = "http://api.themoviedb.org/3/movie/" + idMovie + "/reviews?";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(URL).buildUpon()
                        .appendQueryParameter(API_KEY, "2ccef3b0048491e18f7c3765dd8762c8").build();


                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                movieReviewJsonStr = buffer.toString();
                try {
                    return getMovieReviewrDataFromJson(movieReviewJsonStr);


                } catch (Exception ex) {
                    Log.i("MOVIE", ex.getMessage());
                }

            } catch (IOException e) {
                Log.e("MOVIE", "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MOVIE", "Error closing stream", e);
                    }
                }
            }
            return null;
        }


        private ArrayList<Review> getMovieReviewrDataFromJson(String movieReviewJsonStr)
                throws JSONException {

            final String OWM_RESULTS = "results";
            final String OWN_AUTHOR = "author";
            final String OWN_CONTENT = "content";
            final String OWN_URL = "url";


            JSONObject movieReviewJson = new JSONObject(movieReviewJsonStr);
            JSONArray movieReviewArray = movieReviewJson.getJSONArray(OWM_RESULTS);


            ArrayList<Review> resultReviewMovie = new ArrayList<Review>();
            for (int i = 0; i < movieReviewArray.length(); i++) {
                JSONObject movieReviewObject = movieReviewArray.getJSONObject(i);
                Review review = new Review();

                review.setAuthor(movieReviewObject.getString(OWN_AUTHOR));
                review.setContent(movieReviewObject.getString(OWN_CONTENT));
                review.setUrl(movieReviewObject.getString(OWN_URL));

                resultReviewMovie.add(review);
            }

            return resultReviewMovie;

        }

    }

    public void setMovie(Movie mov) {
        txtTitle.setText(mov.getOriginalTitle());
        txtVoteAverage.setText(mov.getVoteAvarage() + "/10");
        txtYear.setText(mov.getReleaseDate().split("-")[0]);
        txtOverview.setText(mov.getOverview());
        idMovie = mov.getIdMovie();
        if (mov.getPosterPath().endsWith("null"))
            Picasso.with(getActivity()).load(R.drawable.noposter).into(imgPoster);
        else
            Picasso.with(getActivity()).load(mov.getPosterPath()).into(imgPoster);

        SharedPreferences movieDetails = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);

        float fav = movieDetails.getFloat(String.valueOf(idMovie), 0);
        rtbFavorite.setRating(fav);

        if (Utility.checkConnectionInternet(getActivity())) {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Iniciando...");
            pDialog.setCancelable(true);
            pDialog.setMax(100);
            new FetchMovieTrailerTask().execute();
            new FetchMovieReviewTask().execute();
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_notConnection), Toast.LENGTH_SHORT).show();
        }

    }


}
