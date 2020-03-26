package com.zakariazarrouki.map.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.ferfalk.simplesearchview.SimpleSearchView;
import com.google.firebase.auth.FirebaseAuth;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.common.util.LogUtils;
import com.tomtom.online.sdk.map.Icon;
import com.tomtom.online.sdk.map.MapFragment;
import com.tomtom.online.sdk.map.MarkerBuilder;
import com.tomtom.online.sdk.map.OnMapReadyCallback;
import com.tomtom.online.sdk.map.RouteBuilder;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.routing.data.FullRoute;
import com.tomtom.online.sdk.routing.data.RouteResponse;
import com.zakariazarrouki.map.R;
import com.zakariazarrouki.map.databinding.ActivityMainBinding;
import com.zakariazarrouki.map.utility.RecyclerViewItemDecoration;
import com.zakariazarrouki.map.utility.onLoadingUserLocation;
import com.zakariazarrouki.map.view.adapter.SearchAdapter;
import com.zakariazarrouki.map.viewModel.MainViewModel;

import java.util.ArrayList;

import static com.zakariazarrouki.map.utility.Functions.showInfoToast;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, onLoadingUserLocation {

    private TomtomMap tomtomMap;
    private SimpleSearchView searchView;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    private ProgressBar progressBar;
    private Icon departureIcon;
    private Icon destinationIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.enableLogs(Log.VERBOSE);

        ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        activityMainBinding.setMainViewModel(mainViewModel);
        mainViewModel.setContext(this);
        searchView = activityMainBinding.searchView;
        toolbar = activityMainBinding.toolbar;
        recyclerView = activityMainBinding.recyclerView;
        progressBar = activityMainBinding.progressBar;
        setSupportActionBar(toolbar);

        adapter = new SearchAdapter(mainViewModel,this::onLoading);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new RecyclerViewItemDecoration(1,3,false));
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        //MapFragment mapFragment =(MapFragment) activityMainBinding.mapFragment;
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getAsyncMap(this);

        departureIcon = Icon.Factory.fromResources(MainActivity.this, R.drawable.ic_map_route_departure);
        destinationIcon = Icon.Factory.fromResources(MainActivity.this, R.drawable.ic_map_route_destination);

        mainViewModel.getRouteResponseLiveData().observe(this, routeResponse -> {
            toolbar.setVisibility(View.VISIBLE);
            searchView.closeSearch();
            recyclerView.setVisibility(View.GONE);
            adapter.setListLocation(new ArrayList<>());
            tomtomMap.removeMarkers();
            tomtomMap.clearRoute();

            for (FullRoute fullRoute : routeResponse.getRoutes()) {
                tomtomMap.addRoute(new RouteBuilder(fullRoute.getCoordinates()).startIcon(departureIcon).endIcon(destinationIcon));
            }
            tomtomMap.displayRoutesOverview();
        });

        mainViewModel.getListResultLivedata().observe(this, fuzzySearchResults -> {
            adapter.setListLocation(fuzzySearchResults);
            progressBar.setVisibility(View.GONE);
        });

        mainViewModel.getErrorMsg().observe(this, s -> showInfoToast(MainActivity.this,s));

        searchView.setOnQueryTextListener(new SimpleSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length()>0){
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    mainViewModel.searchQuery(newText);
                }
                return false;
            }

            @Override
            public boolean onQueryTextCleared() {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new SimpleSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                toolbar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSearchViewClosed() {
                toolbar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                adapter.setListLocation(new ArrayList<>());
            }

            @Override
            public void onSearchViewShownAnimation() {
            }

            @Override
            public void onSearchViewClosedAnimation() {
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this,LoginActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public LatLng onLoading() {
        return new LatLng(tomtomMap.getUserLocation().getLatitude(),tomtomMap.getUserLocation().getLongitude());
    }
}
