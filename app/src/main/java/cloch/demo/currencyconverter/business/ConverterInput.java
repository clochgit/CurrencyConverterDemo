package cloch.demo.currencyconverter.business;

/**
 * Created by Chhorvorn on 8/4/2017.
 */

public class ConverterInput
{
    public final int SourceID;
    public final String FromCurrencyUnit;
    public final String ToCurrencyUnit;
    public final float Amount;

    public ConverterInput(int sourceid, String fromUnit, String toUnit, float amount)
    {
        this.SourceID = sourceid;
        this.FromCurrencyUnit = fromUnit;
        this.ToCurrencyUnit = toUnit;
        this.Amount = amount;
    }
}
