package cloch.demo.currencyconverter.business;

import java.util.Date;
import io.reactivex.Observable;
import okhttp3.internal.Util;

/**
 * Created by Chhorvorn on 8/2/2017.
 */

public class CurrencyConverterService
{
    private CurrencyRate _exchangeRates;
    private CurrencyRateServiceWrapper _wrapper;

    public CurrencyConverterService(CurrencyRateServiceWrapper wrapper)
    {
        _wrapper = wrapper;
    }
    public CurrencyValue convert(String fromCurrencyUnit, float fromAmount, String toCurrencyUnit)
    {
        CurrencyValue result = new CurrencyValue();
        result.FromCurrencyUnit = fromCurrencyUnit;
        result.ToCurrencyUnit = toCurrencyUnit;
        if(result.FromCurrencyUnit.equalsIgnoreCase(result.ToCurrencyUnit))
        {
            result.Date = Utility.truncateTime(new Date());
            result.ToCurrencyRate = 1;
            result.Value = fromAmount;
        }
        else
        {
            CurrencyRate rates = get_exchangeRates(fromCurrencyUnit);
            float value = rates.rates.get(toCurrencyUnit);
            result.Value = fromAmount * value;
            result.Date = rates.date;
            result.ToCurrencyRate = value;
        }

        return result;
    }

    public Observable<CurrencyRate> getCurrencyExchangeRates(String baseUnit)
    {
        return _wrapper.getCurrencyExchangeRates(baseUnit)
                .doOnNext(this::cacheExchangeRates);
    }

//    public Observable<CurrencyRate> getCurrencyExchangeRates(String baseUnit)
//    {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://api.fixer.io")
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        return retrofit.create(CurrencyRateService.class)
//                .getCurrency(baseUnit)
//                .doOnNext(this::cacheExchangeRates);
//    }


    private CurrencyRate get_exchangeRates(String baseUnit)
    {
        Date today = Utility.truncateTime(new Date());

        if(_exchangeRates == null || !_exchangeRates.base.equalsIgnoreCase(baseUnit) || _exchangeRates.date.compareTo(today) != 0)
        {
            CurrencyRate latestRates = getCurrencyExchangeRates(baseUnit).blockingFirst();
            cacheExchangeRates(latestRates);
        }

        return _exchangeRates;
    }

    synchronized void cacheExchangeRates(CurrencyRate value)
    {
        _exchangeRates = value;
    }

}
