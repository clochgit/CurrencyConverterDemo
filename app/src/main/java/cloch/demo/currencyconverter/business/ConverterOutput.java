package cloch.demo.currencyconverter.business;

import java.util.Date;

/**
 * Created by Chhorvorn on 8/4/2017.
 */

public class ConverterOutput
{
    public final ConverterInput Input;
    public java.util.Date Date;
    public float ToCurrencyRate;
    public float Output;

    public ConverterOutput(ConverterInput input)
    {
        Input = input;
    }
}
