package es.itcl.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import es.itcl.movieapp.data.MovieContract;


public class MainActivity extends AppCompatActivity implements MovieFragment.MoviesListener {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

        MovieFragment frgMovieList =
                (MovieFragment)getSupportFragmentManager()
                        .findFragmentById(R.id.frgMovieList);

        frgMovieList.setMoviesListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent inSettings = new Intent(this, SettingsActivity.class);
                startActivity(inSettings);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




    private void executeDetailMovie(Movie mov) {

        Bundle args = new Bundle();
        args.putString(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, mov.getOriginalTitle());
        args.putDouble(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, mov.getVoteAvarage());
        args.putString(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mov.getReleaseDate().split("-")[0]);
        args.putString(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "http://image.tmdb.org/t/p/w185"+mov.getPosterPath());

        if (mov.getOverview()=="null")
            args.putString(MovieContract.MovieEntry.COLUMN_OVERVIEW, getResources().getString(R.string.msg_notOverview));
        else
            args.putString(MovieContract.MovieEntry.COLUMN_OVERVIEW, mov.getOverview());

        args.putInt(MovieContract.MovieEntry.COLUMN_ID_MOVIE, mov.getIdMovie());

        Intent intDetailMovie = new Intent(MainActivity.this,DetailMovieActivity.class);
        intDetailMovie.putExtras(args);
        startActivity(intDetailMovie);
    }


    @Override
    public void onMovieSelected(Movie mov) {
        boolean isDetail =
                (getSupportFragmentManager().findFragmentById(R.id.frgMovieDetail) != null);

        if(isDetail) {
            ((DetailMovieFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.frgMovieDetail)).setMovie(mov);
        }
        else {
            executeDetailMovie(mov);
        }
    }



}
