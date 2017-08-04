package cloch.demo.currencyconverter.ui;

import cloch.demo.currencyconverter.business.CurrencyConverterService;
import cloch.demo.currencyconverter.business.CurrencyRate;
import cloch.demo.currencyconverter.business.CurrencyRateServiceWrapper;
import cloch.demo.currencyconverter.business.CurrencyValue;
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

    public Single<CurrencyValue> convert(String fromCurrencyUnit, float fromAmount, String toCurrencyUnit)
    {
        return Single.defer(()-> {
            return Single.just(_service.convert(fromCurrencyUnit, fromAmount, toCurrencyUnit));
        });
    }

    public Observable<CurrencyRate> getCurrentExchangeRates(String baseCurrencyUnit)
    {
        return _service.getCurrencyExchangeRates(baseCurrencyUnit);
    }
}
