package es.itcl.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import es.itcl.movieapp.data.MovieContract;


public class DetailMovieActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        DetailMovieFragment detail =
                (DetailMovieFragment)getSupportFragmentManager()
                        .findFragmentById(R.id.frgMovieDetail);

        Movie m = new Movie();

        m.setOriginalTitle(getIntent().getExtras().getString(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE));
        m.setVoteAverage(getIntent().getExtras().getDouble(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
        m.setReleaseDate(getIntent().getExtras().getString(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
        m.setOverview(getIntent().getExtras().getString(MovieContract.MovieEntry.COLUMN_OVERVIEW));
        m.setIdMovie(getIntent().getExtras().getInt(MovieContract.MovieEntry.COLUMN_ID_MOVIE));
        m.setPosterPath(getIntent().getExtras().getString(MovieContract.MovieEntry.COLUMN_POSTER_PATH));

        detail.setMovie(m);
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
}
