package com.zakariazarrouki.map.viewModel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ServiceConnection;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.collect.ImmutableList;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.routing.OnlineRoutingApi;
import com.tomtom.online.sdk.routing.data.RouteQuery;
import com.tomtom.online.sdk.routing.data.RouteQueryBuilder;
import com.tomtom.online.sdk.routing.data.RouteResponse;
import com.tomtom.online.sdk.routing.data.RouteType;
import com.tomtom.online.sdk.search.data.fuzzy.FuzzySearchQueryBuilder;
import com.tomtom.online.sdk.search.data.fuzzy.FuzzySearchResponse;
import com.tomtom.online.sdk.search.data.fuzzy.FuzzySearchResult;
import com.tomtom.online.sdk.search.extensions.SearchService;
import com.tomtom.online.sdk.search.extensions.SearchServiceConnectionCallback;
import com.tomtom.online.sdk.search.extensions.SearchServiceManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel implements SearchServiceConnectionCallback {

    private String TAG = getClass().getName();

    private SearchService service;
    private Context context;
    private MutableLiveData<String> errorMsg;
    private MutableLiveData<ImmutableList<FuzzySearchResult>> listResultLivedata;
    private MutableLiveData<RouteResponse> routeResponseLiveData;

    public void setContext(Context context){
        this.context = context;
        listResultLivedata = new MutableLiveData<>();
        routeResponseLiveData = new MutableLiveData<>();
        errorMsg = new MutableLiveData<>();

        ServiceConnection searchServiceConnection = SearchServiceManager.createAndBind(context, this);
    }

    public MutableLiveData<ImmutableList<FuzzySearchResult>> getListResultLivedata() {
        return listResultLivedata;
    }

    public MutableLiveData<String> getErrorMsg() {
        return errorMsg;
    }

    public MutableLiveData<RouteResponse> getRouteResponseLiveData() {
        return routeResponseLiveData;
    }

    @SuppressLint("CheckResult")
    public void searchQuery(String location){
         service.search(FuzzySearchQueryBuilder.create(location).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResults,this::handleError);
    }

    private void handleResults(FuzzySearchResponse response) {
        listResultLivedata.setValue(response.getResults());
    }

    public void handleError(Throwable error){
        error.printStackTrace();
        Log.e(TAG,error.getMessage(),error);
        errorMsg.setValue(error.getMessage());
    }

    public void routingQuery(LatLng start,LatLng destination){
        RouteQuery routeQuery = createRouteQuery(start, destination);
        OnlineRoutingApi.create(context).planRoute(routeQuery)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<RouteResponse>() {

                    @Override
                    public void onSuccess(RouteResponse routeResponse) {
                        routeResponseLiveData.setValue(routeResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorMsg.setValue(e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    private RouteQuery createRouteQuery(LatLng start, LatLng destination) {
        return new RouteQueryBuilder(start, destination).withRouteType(RouteType.FASTEST).build();
    }

    @Override
    public void onBindSearchService(SearchService service) {
        this.service = service;
    }
}
