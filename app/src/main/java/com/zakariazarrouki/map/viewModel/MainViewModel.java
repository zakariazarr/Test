package com.zakariazarrouki.map.viewModel;

import android.content.Context;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.View;

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

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel implements SearchServiceConnectionCallback {

    private SearchService service;
    private ServiceConnection searchServiceConnection;
    private Context context;
    private String textQuery;
    private MutableLiveData<FuzzySearchResult> resultLivedata;

    public void setContext(Context context){
        this.context = context;
        searchServiceConnection = SearchServiceManager.createAndBind(context,this);
        SearchApi searchApi = OnlineSearchApi.create(context);
        resultLivedata = new MutableLiveData<>();
    }

    public MutableLiveData<FuzzySearchResult> getResultLivedata() {
        return resultLivedata;
    }

    public void afterSearchTextChanged(CharSequence s) {
        textQuery = s.toString();
    }

    public void onBtnSearchClicked(View view){
        Log.i("MainViewModel","btnSearch is clicked !");
        searchQuery();
    }

    public void searchQuery(){
        service.search(FuzzySearchQueryBuilder.create(textQuery).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResults,this::handleError);
    }

    private void handleResults(FuzzySearchResponse response) {
        Log.i("MainViewModel","handleResults method is called !");

        for(FuzzySearchResult result:response.getResults()){
            Log.i("SearchResult","Country "+result.getAddress().getCountry());
            Log.i("SearchResult","latitude "+result.getPosition().getLatitude());
            Log.i("SearchResult","longitude "+result.getPosition().getLongitude());
            resultLivedata.setValue(result);
        }
    }

    public void handleError(Throwable error){
        Log.i("MainViewModel","handleError method is called !");
        error.printStackTrace();
        Log.e("SearchThrowable",error.getMessage(),error);
    }

    @Override
    public void onBindSearchService(SearchService service) {
        this.service = service;
    }
}
