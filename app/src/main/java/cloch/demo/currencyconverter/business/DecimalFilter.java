package cloch.demo.currencyconverter.business;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;

/**
 * Created by Chhorvorn on 8/2/2017.
 */

public class DecimalFilter extends DigitsKeyListener
{
    private final int _maxDecimalDigits;

    public DecimalFilter(int maxDecimalDigits)
    {
        _maxDecimalDigits = maxDecimalDigits;
    }
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
    {

        int sourceLength = end - start;
        if(sourceLength == 0)
        {
            return source;
        }

        int destLength = dest.length();

        for(int i = 0; i < dstart; i++)
        {
            if(dest.charAt(i) == '.')
            {
                return destLength - (i+1) + sourceLength > _maxDecimalDigits ? "" : new SpannableStringBuilder(source, start, end);
            }
        }

        for(int i = start; i < end; i++)
        {
            if (source.charAt(i) == '.')
            {
                return (destLength - dend)+ (end-(i+1)) > _maxDecimalDigits ? "" : new SpannableStringBuilder(source, start, end);
            }
        }

        return new SpannableStringBuilder(source, start, end);
    }
}
