package cloch.demo.currencyconverter.business;

import java.util.Date;
import io.reactivex.Observable;

import static cloch.demo.currencyconverter.business.UtilityKt.truncateTime;

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
    public ConverterOutput convert(ConverterInput input)
    {
        ConverterOutput result = new ConverterOutput(input);
        if(input.FromCurrencyUnit.equalsIgnoreCase(input.ToCurrencyUnit))
        {
            result.Date = truncateTime(new Date());
            result.ToCurrencyRate = 1;
            result.Output = input.Amount;
        }
        else
        {
            CurrencyRate rates = get_exchangeRates(input.FromCurrencyUnit);
            float rateValue = rates.rates.get(input.ToCurrencyUnit);
            result.Date = rates.date;
            result.ToCurrencyRate = rateValue;
            result.Output = input.Amount * rateValue;
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
        Date today = truncateTime(new Date());

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
