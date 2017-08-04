package cloch.demo.currencyconverter.ui;

import cloch.demo.currencyconverter.business.ConverterInput;
import cloch.demo.currencyconverter.business.ConverterOutput;
import cloch.demo.currencyconverter.business.CurrencyConverterService;
import cloch.demo.currencyconverter.business.CurrencyRate;
import cloch.demo.currencyconverter.business.CurrencyRateServiceWrapper;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by Chhorvorn on 8/1/2017.
 */

public class CurrencyConverterViewModel
{
    public static final String DEFAULT_CURRENCY = "USD";
    public static final double DEFAULT_AMOUNT = 0.00;

    CurrencyConverterService _service;

    public CurrencyConverterViewModel()
    {
        _service = new CurrencyConverterService(new CurrencyRateServiceWrapper());
    }

    public Single<ConverterOutput> convert(ConverterInput input)
    {
        return Single.defer(()-> {
            return Single.just(_service.convert(input));
        });
    }

    public Observable<CurrencyRate> getCurrentExchangeRates(String baseCurrencyUnit)
    {
        return _service.getCurrencyExchangeRates(baseCurrencyUnit);
    }
}
