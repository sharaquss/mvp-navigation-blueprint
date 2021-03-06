package com.android.szparag.colortv.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.szparag.colortv.ColorTVApplication;
import com.android.szparag.colortv.R;
import com.android.szparag.colortv.adapters.MovieAdapter;
import com.android.szparag.colortv.adapters.RecyclerOnPosClickListener;
import com.android.szparag.colortv.backend.models.Movie;
import com.android.szparag.colortv.presenters.contracts.MovieListBasePresenter;
import com.android.szparag.colortv.utils.Utils;
import com.android.szparag.colortv.views.contracts.MovieListBaseView;

import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.android.szparag.colortv.utils.Constants.MOVIE_ID_RESPONSE_OK;
import static com.android.szparag.colortv.utils.Constants.MOVIE_LIST_INTENT_EXTRA_KEY;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieListFragment extends Fragment implements MovieListBaseView {

    @Inject
    MovieListBasePresenter presenter;

    @BindView(R.id.recycler_movie_list)
    RecyclerView recyclerMovieList;

    private MovieAdapter adapter;
    private Unbinder viewUnbinder;


    //static builder (for parameters):
    public static MovieListFragment newInstance(int movieGroupId) {
        MovieListFragment fragment = new MovieListFragment();
        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putInt(MOVIE_LIST_INTENT_EXTRA_KEY, movieGroupId);
        fragment.setArguments(fragmentBundle);
        return fragment;
    }

    //android lifecycle callbacks:
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utils.getDagger(this).inject(this);
        viewUnbinder = ButterKnife.bind(this, getView());
        presenter.setView(this);
        presenter.populateViewWithMovies(getMovieGroupIndex());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewUnbinder.unbind();
    }

    //base view methods implementation:
    @Override
    public void buildRecycler() {
        recyclerMovieList.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerMovieList.setHasFixedSize(true);
        adapter = new MovieAdapter(new RecyclerOnPosClickListener() {
            @Override
            public void OnPosClick(View v, int position) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(MOVIE_LIST_INTENT_EXTRA_KEY, presenter.queryMovieFromGroup(position).getVideoId());
                getActivity().setResult(MOVIE_ID_RESPONSE_OK, resultIntent);
                getActivity().finish();
            }
        });
        recyclerMovieList.setAdapter(adapter);
    }

    @Override
    public void updateRecycler(List<Movie> movies) {
        adapter.updateItems(movies);
    }

    @Override
    public int getMovieGroupIndex() {
        return getArguments().getInt(MOVIE_LIST_INTENT_EXTRA_KEY);
    }

    @Override
    public String getPackageName() {
        return getActivity().getPackageName();
    }


    //BaseAndroidView methods implementation:
    @Override
    public InputStream getRawResource(int rawResId) {
        return getResources().openRawResource(rawResId);
    }

    @Override
    public ColorTVApplication getAndroidApplication() {
        return ((ColorTVApplication) getActivity().getApplication());
    }

    @Override
    public Fragment getAndroidView() {
        return this;
    }
}
