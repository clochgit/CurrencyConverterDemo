package cloch.demo.currencyconverter;

import static org.mockito.BDDMockito.given;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Date;
import java.util.HashMap;

import cloch.demo.currencyconverter.business.CurrencyConverterService;
import cloch.demo.currencyconverter.business.CurrencyRate;
import cloch.demo.currencyconverter.business.CurrencyRateServiceWrapper;
import cloch.demo.currencyconverter.business.CurrencyValue;
import cloch.demo.currencyconverter.business.Utility;

import static org.junit.Assert.assertTrue;

/**
 * Created by Chhorvorn on 8/2/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class CurrencyConverterHelperUnitTest
{
    @Mock
    CurrencyRateServiceWrapper _wrapper;
    @InjectMocks
    CurrencyConverterService _converter;

    @Before
    public void init()
    {
        assertTrue(_wrapper != null);
        assertTrue(_converter != null);
    }
    @Test
    public void Given_Rates_When_Convert_Then_VerifyResult()
    {
        String fromUnit = "USD";
        String toUnit = "AUD";
        Float fromAmount = 20.0f;

        CurrencyRate currencyRates = createCurrencyRates();
        float resultAmount = fromAmount * currencyRates.rates.get(toUnit);

        given(_wrapper.getCurrencyExchangeRates("USD")).willReturn(io.reactivex.Observable.just(currencyRates));
        CurrencyValue result = _converter.convert(fromUnit, fromAmount, toUnit);
        assertTrue(result.FromCurrencyUnit.equalsIgnoreCase(fromUnit));
        assertTrue(result.ToCurrencyUnit.equalsIgnoreCase(toUnit));
        assertTrue(result.Value == resultAmount);

    }

    private CurrencyRate createCurrencyRates()
    {
        CurrencyRate currencyRates = new CurrencyRate();
        currencyRates.base = "USD";
        currencyRates.date = Utility.truncateTime(new Date());
        currencyRates.rates = new HashMap<String, Float>();
        currencyRates.rates.put("AUD", 1.2541f);
        currencyRates.rates.put("BGN", 1.6558f);
        currencyRates.rates.put("BRL", 3.1222f);
        currencyRates.rates.put("CAD", 1.2476f);

        return currencyRates;
    }
}
