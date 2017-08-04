package cloch.demo.currencyconverter.business;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by Chhorvorn on 8/2/2017.
 */

public class NumericTextWatcher implements TextWatcher
{
    private final Subject<CurrencyValue> _textChangedSubject;
    private final AppCompatActivity _activity;
    private final  TextInputEditText _parent;
    private final AppCompatSpinner _fromCurrencySpinner;
    private final AppCompatSpinner _toCurrencySpinner;
    private boolean _ignoreChange = false;

    public NumericTextWatcher(AppCompatActivity activity, TextInputEditText parent, AppCompatSpinner fromCurrencySpinner, AppCompatSpinner toCurrencySpinner)
    {
        _textChangedSubject = PublishSubject.create();
        _activity = activity;
        _parent = parent;
        _fromCurrencySpinner = fromCurrencySpinner;
        _toCurrencySpinner = toCurrencySpinner;
    }

    public Subject<CurrencyValue> TextChange()
    {
        return _textChangedSubject;
    }

    public void setIgnoreChange(boolean ignore)
    {
        _ignoreChange = ignore;
    }

    private boolean ignoreChange()
    {
        return _ignoreChange || _activity.getCurrentFocus() != _parent;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
    {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count)
    {
    }

    @Override
    public void afterTextChanged(Editable editable)
    {
        if(ignoreChange())
        {
            if(_ignoreChange)
            {
                _ignoreChange = false;
            }

            return;
        }

        triggerTextChange(editable);
    }

    public void triggerTextChange(Editable editable)
    {
        String text = editable.toString();
        if(!validate(text))
        {
            return;
        }

        CurrencyValue value = new CurrencyValue();
        value.FromCurrencyUnit = (String)_fromCurrencySpinner.getSelectedItem();
        value.ToCurrencyUnit = (String)_toCurrencySpinner.getSelectedItem();
        value.Value = getValue(text);
        _textChangedSubject.onNext(value);
    }

    private boolean validate(String value)
    {
        Float result = 0.00f;
        try
        {
            result = value == null || value.equalsIgnoreCase("") ? 0 : Float.valueOf(value);
            return result >= 0;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    private float getValue(String value)
    {
        return value == null || value.equalsIgnoreCase("") ? 0 : Float.valueOf(value);
    }
}
