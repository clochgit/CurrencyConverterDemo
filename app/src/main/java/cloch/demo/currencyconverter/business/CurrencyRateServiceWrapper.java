package cloch.demo.currencyconverter.business;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Chhorvorn on 8/2/2017.
 */

public class CurrencyRateServiceWrapper
{
    private final String BASE_URL = "http://api.fixer.io";

    public Observable<CurrencyRate> getCurrencyExchangeRates(String baseUnit)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(CurrencyRateService.class)
                .getCurrency(baseUnit);
    }
}
