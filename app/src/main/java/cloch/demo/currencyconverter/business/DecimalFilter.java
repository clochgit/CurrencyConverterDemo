package cloch.demo.currencyconverter.business;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;

/**
 * Created by Chhorvorn on 8/2/2017.
 */

public class DecimalFilter extends DigitsKeyListener
{
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
    {
        CharSequence out = super.filter(source, start, end, dest, dstart, dend);
        if(out != null && out.length() > 0)
        {
            source = out;
            start = 0;
            end = out.length();
        }

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
                return destLength - (i+1) + sourceLength > 2 ? "" : new SpannableStringBuilder(source, start, end);
            }
        }

        for(int i = start; i < end; i++)
        {
            if (source.charAt(i) == '.')
            {
                break;
            }
        }

        return new SpannableStringBuilder(source, start, end);
    }
}
