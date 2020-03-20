package com.zakariazarrouki.map.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tomtom.online.sdk.common.util.LogUtils;
import com.tomtom.online.sdk.location.LocationSource;
import com.tomtom.online.sdk.map.GpsIndicator;
import com.tomtom.online.sdk.map.MapFragment;
import com.tomtom.online.sdk.map.MarkerBuilder;
import com.tomtom.online.sdk.map.OnMapReadyCallback;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.search.OnlineSearchApi;
import com.tomtom.online.sdk.search.SearchApi;
import com.tomtom.online.sdk.search.api.SearchError;
import com.tomtom.online.sdk.search.api.fuzzy.FuzzySearchResultListener;
import com.tomtom.online.sdk.search.data.fuzzy.FuzzySearchQueryBuilder;
import com.tomtom.online.sdk.search.data.fuzzy.FuzzySearchResponse;
import com.tomtom.online.sdk.search.data.fuzzy.FuzzySearchResult;
import com.tomtom.online.sdk.search.extensions.SearchService;
import com.tomtom.online.sdk.search.extensions.SearchServiceConnectionCallback;
import com.tomtom.online.sdk.search.extensions.SearchServiceManager;
import com.zakariazarrouki.map.R;
import com.zakariazarrouki.map.databinding.ActivityMainBinding;
import com.zakariazarrouki.map.viewModel.MainViewModel;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TomtomMap tomtomMap;
    private ServiceConnection searchServiceConnection;
    private Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.enableLogs(Log.VERBOSE);

        ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        activityMainBinding.setMainViewModel(mainViewModel);
        mainViewModel.setContext(this);
        btnSearch = activityMainBinding.btnSearch;

        //MapFragment mapFragment =(MapFragment) activityMainBinding.mapFragment;
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getAsyncMap(this);

        mainViewModel.getResultLivedata().observe(this, new Observer<FuzzySearchResult>() {
            @Override
            public void onChanged(FuzzySearchResult fuzzySearchResult) {
                MarkerBuilder  markerBuilder = new MarkerBuilder(fuzzySearchResult.getPosition());
                tomtomMap.addMarker(markerBuilder);
            }
        });


        btnSearch.setOnClickListener(view -> mainViewModel.searchQuery());
    }

    @Override
    public void onMapReady(@NonNull TomtomMap map) {
        this.tomtomMap = map;
        tomtomMap.setMyLocationEnabled(true);
        this.tomtomMap.getMarkerSettings().setMarkersClustering(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        tomtomMap.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
