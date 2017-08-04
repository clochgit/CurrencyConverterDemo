package cloch.demo.currencyconverter.business;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Chhorvorn on 8/2/2017.
 */

public interface CurrencyRateService
{
    @GET("/latest")
    Observable<CurrencyRate> getCurrency(@Query("base") String baseUnit);
}
