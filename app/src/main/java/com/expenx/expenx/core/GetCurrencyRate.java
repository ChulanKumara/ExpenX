package com.expenx.expenx.core;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by skaveesh on 2017-05-07.
 */

public interface GetCurrencyRate {

    @GET("api/v3/convert")
    Call<ResponseBody> getCurrencyRate(
            @Query("q") String currencies,
            @Query("compact") String compact);

}
