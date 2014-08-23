package com.ponyvillelive.app.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ponyvillelive.app.PvlApp;
import com.ponyvillelive.app.R;
import com.ponyvillelive.app.model.NowPlayingMeta;
import com.ponyvillelive.app.model.Station;
import com.ponyvillelive.app.net.API;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomDrawerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomDrawerFragment extends Fragment {

    @InjectView(R.id.icon)
    ImageView icon;
    @InjectView(R.id.text_title)
    TextView  title;
    @InjectView(R.id.text_artist)
    TextView  artist;
    @InjectView(R.id.bottom_drawer_list)
    ListView trackList;

    @Inject
    Picasso picasso;
    @Inject
    API     api;

    private TrackListAdapter adapter;
    private DrawerListener listener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BottomDrawerFragment.
     */
    public static BottomDrawerFragment newInstance() {
        BottomDrawerFragment fragment = new BottomDrawerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public BottomDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bottom_drawer, container, false);
        ButterKnife.inject(this, v);

        adapter = new TrackListAdapter();
        trackList.setAdapter(adapter);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(!(activity instanceof DrawerListener)) {
            throw new RuntimeException("Activity must implement DrawerListener");
        }

        this.listener = (DrawerListener) activity;
        PvlApp.get(activity).inject(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @OnClick(R.id.btn_drawer_cancel)
    public void handleStopClicked() {
        listener.handleStationCleared();
    }

    /**
     * Binds the view to the current station, displaying it's data in
     * the peek area, as well as its track history in the list
     * @param station The {@link com.ponyvillelive.app.model.Station} to bind to
     */
    public void showStationInfo(Station station) {
        api
                .getNowPlayingForStation(station.id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((nowPlayingStationResponse) -> {
                    if(getView() == null) return;

                    if(getView().getVisibility() != View.VISIBLE) {
                        getView().setVisibility(View.VISIBLE);
                    }

                    NowPlayingMeta data = nowPlayingStationResponse.result;
                    title.setText(data.currentSong.title);
                    artist.setText(data.currentSong.artist);
                    picasso
                            .load("http:" + data.station.imageUrl)
                            .into(icon);
                    adapter.setSongs(data.songHistory);
                });
    }

    public interface DrawerListener {
        public void handleStationCleared();
    }
}
