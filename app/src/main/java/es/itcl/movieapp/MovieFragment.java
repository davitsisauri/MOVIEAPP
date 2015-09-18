package es.itcl.movieapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
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
import java.util.Map;

import es.itcl.movieapp.data.MovieContract;



public class MovieFragment extends Fragment {


    private AdapterMovie mMovieAdapter;

    private ArrayList<Movie> movies;

    private ProgressDialog pDialog;

    private GridView listView;

    private MoviesListener listener;

    private int indexItemSelected = -1;
    private int indexItemVisible = 0;

    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        listView = (GridView) rootView.findViewById(R.id.gridview_movies);

        if (savedInstanceState != null) {
            indexItemSelected = savedInstanceState.getInt("indexItemSelected");
            indexItemVisible = savedInstanceState.getInt("indexItemVisible");
        }
        return rootView;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("indexItemVisible", listView.getFirstVisiblePosition());
        outState.putInt("indexItemSelected", indexItemSelected);
    }


    @Override
    public void onResume() {
        super.onResume();

        movies = new ArrayList<>();
        String sortBy = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_sort_order_key), getString(R.string.pref_sort_order_default));


        if (Utility.checkConnectionInternet(getActivity())) {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage(getResources().getString(R.string.msg_loading));
            pDialog.setCancelable(true);
            pDialog.setMax(100);
            new FetchMovieTask().execute(sortBy);
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_notConnection), Toast.LENGTH_SHORT).show();
        }


        if (movies == null)
            movies = new ArrayList<>();

        mMovieAdapter = new AdapterMovie(getActivity());

        listView.setAdapter(mMovieAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                if (listener != null) {
                    indexItemSelected = position;
                    listener.onMovieSelected(movies.get(position));
                }


            }
        });
    }



    private void executeDetailMovie(Movie mov) {

        Bundle args = new Bundle();
        args.putString(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, mov.getOriginalTitle());
        args.putDouble(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, mov.getVoteAvarage());
        args.putString(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mov.getReleaseDate().split("-")[0]);
        args.putString(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "http://image.tmdb.org/t/p/w185" + mov.getPosterPath());

        if (mov.getOverview() == "null")
            args.putString(MovieContract.MovieEntry.COLUMN_OVERVIEW, getResources().getString(R.string.msg_notOverview));
        else
            args.putString(MovieContract.MovieEntry.COLUMN_OVERVIEW, mov.getOverview());

        args.putInt(MovieContract.MovieEntry.COLUMN_ID_MOVIE, mov.getIdMovie());


        Intent intDetailMovie = new Intent(getActivity(), DetailMovieActivity.class);
        intDetailMovie.putExtras(args);
        startActivity(intDetailMovie);
    }


    class AdapterMovie extends BaseAdapter {

        Activity context;

        AdapterMovie(Activity context) {
            this.context = context;
        }


        public int getCount() {
            return movies.size();
        }

        public Object getItem(int position) {
            return movies.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }


        public View getView(int position, View convertView, ViewGroup parent) {
            View item = convertView;
            ViewHolder holder;

            if (item == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                item = inflater.inflate(R.layout.listitem_movie, null);
                holder = new ViewHolder();
                holder.imagen = (ImageView) item.findViewById(R.id.imagePoster);
                item.setTag(holder);
            } else {
                holder = (ViewHolder) item.getTag();
            }

            if (!movies.get(position).getPosterPath().endsWith("null"))
                Picasso.with(context).load(movies.get(position).getPosterPath()).into(holder.imagen);
            else
                Picasso.with(context).load(R.drawable.noposter).into(holder.imagen);
            return item;
        }
    }

    static class ViewHolder {
        ImageView imagen;
    }




    public class FetchMovieTask extends AsyncTask<String, Integer, ArrayList<Movie>> {


        @Override
        protected void onPostExecute(ArrayList<Movie> result) {


            if (result != null) {
                movies = result;
                mMovieAdapter.notifyDataSetChanged();

                //For Tablet
                if (getActivity().getSupportFragmentManager().findFragmentById(R.id.frgMovieDetail) != null) {
                    if (listener != null) {

                        if (movies.size() > 0) {
                            if (movies.size() > indexItemSelected) {
                                if (indexItemSelected == -1)
                                    indexItemSelected = 0;
                                listener.onMovieSelected(movies.get(indexItemSelected));
                            }
                        }
                        else{
                            FragmentManager manager =getActivity().getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.hide(manager.findFragmentById(R.id.frgMovieDetail));
                            transaction.commit();

                        }
                    }
                }
                listView.setSelection(indexItemVisible);
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


        protected ArrayList<Movie> doInBackground(String... urls) {

            ArrayList<Movie> arrMovies = new ArrayList<Movie>();

            if (urls[0].toString() == getResources().getString(R.string.pref_sort_order_favorites)) {
                SharedPreferences movieDetails = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
                Map<String, ?> collFavoritos = movieDetails.getAll();
                ArrayList<Movie> auxMovies = getMovies(getResources().getString(R.string.pref_sort_order_popularity));
                if (auxMovies != null && auxMovies.size() > 0) {
                    for (Movie mov : auxMovies) {
                        if (collFavoritos.containsKey(String.valueOf(mov.getIdMovie())) && movieDetails.getFloat(String.valueOf(mov.getIdMovie()), 0) == 1) {
                            arrMovies.add(mov);
                        }
                    }
                }

                auxMovies = getMovies(getResources().getString(R.string.pref_sort_order_high_rated));
                if (auxMovies != null && auxMovies.size() > 0) {
                    for (Movie mov : auxMovies) {
                        if (collFavoritos.containsKey(String.valueOf(mov.getIdMovie())) && movieDetails.getFloat(String.valueOf(mov.getIdMovie()), 0) == 1) {
                            arrMovies.add(mov);
                        }
                    }
                }
            } else {
                arrMovies = getMovies(urls[0].toString());
            }
            return arrMovies;
        }


        private ArrayList<Movie> getMovies(String sortby) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;

            try {

                final String URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY = "sort_by";
                final String API_KEY = "api_key";


                Uri builtUri = Uri.parse(URL).buildUpon()
                        .appendQueryParameter(SORT_BY, sortby)
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

                movieJsonStr = buffer.toString();
                try {
                    return getMovieDataFromJson(movieJsonStr);


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

        private ArrayList<Movie> getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            final String OWM_RESULTS = "results";
            final String OWN_ADULT = "adult";
            final String OWN_BACKDROP_PATH = "backdrop_path";
            final String OWN_ID = "id";
            final String OWN_ORIGINAL_LANGUAGE = "original_language";
            final String OWN_ORIGINAL_TITLE = "original_title";
            final String OWN_OVERVIEW = "overview";
            final String OWN_RELEASE_DATE = "release_date";
            final String OWN_POSTER_PATH = "poster_path";
            final String OWN_TITLE = "title";
            final String OWN_POPULARITY = "popularity";
            final String OWN_VIDEO = "video";
            final String OWN_VOTE_AVERAGE = "vote_average";
            final String OWN_VOTE_COUNT = "vote_count";


            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_RESULTS);


            ArrayList<Movie> resultMovie = new ArrayList<Movie>();
            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieObject = movieArray.getJSONObject(i);
                Movie movie = new Movie();

                movie.setAdult(movieObject.getBoolean(OWN_ADULT));
                movie.setBackdropPath(movieObject.getString(OWN_BACKDROP_PATH));
                movie.setIdMovie(movieObject.getInt(OWN_ID));
                movie.setOriginalLanguage(movieObject.getString(OWN_ORIGINAL_LANGUAGE));
                movie.setOriginalTitle(movieObject.getString(OWN_ORIGINAL_TITLE));
                movie.setOverview(movieObject.getString(OWN_OVERVIEW));
                movie.setReleaseDate(movieObject.getString(OWN_RELEASE_DATE));
                movie.setPosterPath("http://image.tmdb.org/t/p/w185" + movieObject.getString(OWN_POSTER_PATH));
                movie.setTitle(movieObject.getString(OWN_TITLE));
                movie.setPopularity(movieObject.getDouble(OWN_POPULARITY));
                movie.setVideo(movieObject.getBoolean(OWN_VIDEO));
                movie.setVoteAverage(movieObject.getDouble(OWN_VOTE_AVERAGE));
                movie.setVoteCount(movieObject.getInt(OWN_VOTE_COUNT));

                resultMovie.add(movie);
            }

            return resultMovie;

        }

    }


    public interface MoviesListener {
        void onMovieSelected(Movie mov);
    }

    public void setMoviesListener(MoviesListener listener) {
        this.listener = listener;
    }


}
