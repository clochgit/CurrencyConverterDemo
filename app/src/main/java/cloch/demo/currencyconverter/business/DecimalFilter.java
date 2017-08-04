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

        if(dstart < dest.length())
        {
            return new SpannableStringBuilder(source, start, end);
        }

        int destLength = dest.length();

        int descimalPoint = -1;

        for(int i = 0; i < dstart; i++)
        {
            if(dest.charAt(i) == '.')
            {
                descimalPoint = i;
                break;
            }
        }

        int sourceDecimalPoint = -1;
        for(int i = start; i < end; i++)
        {
            if (source.charAt(i) == '.')
            {
                sourceDecimalPoint = i;
                break;
            }
        }

        if(descimalPoint >= 0 && sourceDecimalPoint >= 0)
        {
            return "";
        }

//        if(descimalPoint < 0 && sourceDecimalPoint == 0)
//        {
//            return "";
//        }

        if(descimalPoint < 0 && sourceDecimalPoint < 0)
        {
            return new SpannableStringBuilder(source, start, end);
        }

        if(descimalPoint >= 0)
        {
            int digitsAfterDecimal = dstart -descimalPoint;
            if(digitsAfterDecimal > 2)
            {
                return "";
            }
        }

        return new SpannableStringBuilder(source, start, end);
    }
}
