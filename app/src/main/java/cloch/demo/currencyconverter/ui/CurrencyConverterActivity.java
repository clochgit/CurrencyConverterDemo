package cloch.demo.currencyconverter.ui;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import cloch.demo.currencyconverter.R;
import cloch.demo.currencyconverter.business.CurrencyRate;
import cloch.demo.currencyconverter.business.CurrencyValue;
import cloch.demo.currencyconverter.business.AmountTextWatcher;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CurrencyConverterActivity extends AppCompatActivity
{

    private final CompositeDisposable _compositeDisposable = new CompositeDisposable();
    private final DateFormat _dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    CurrencyConverterViewModel _model;
    TextInputEditText _textInputAmount1;
    TextInputEditText _textInputAmount2;
    AppCompatSpinner _spinnerCurrency1;
    AppCompatSpinner _spinnerCurrency2;
    AppCompatTextView _textViewRateInfo;

    AmountTextWatcher _textWatcher1;
    AmountTextWatcher _textWatcher2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);

        if(_model == null)
        {
            _model = new CurrencyConverterViewModel();
        }

        _textInputAmount1 = findViewById(R.id.textInputAmount1);
        _textInputAmount2 = findViewById(R.id.textInputAmount2);
        _spinnerCurrency1 = findViewById(R.id.spinnerCurrency1);
        _spinnerCurrency2 = findViewById(R.id.spinnerCurrency2);
        _textViewRateInfo = findViewById(R.id.textViewRateInfo);

//        _textInputAmount1.setFilters(new InputFilter[]{new DecimalFilter()});
//        _textInputAmount2.setFilters(new InputFilter[]{new DecimalFilter()});


        _textWatcher1 = createTextWatcher(_textInputAmount1, _spinnerCurrency1, _spinnerCurrency2);
        _textWatcher2 = createTextWatcher(_textInputAmount2, _spinnerCurrency2, _spinnerCurrency1);
        _textInputAmount1.addTextChangedListener(_textWatcher1);
        _textInputAmount2.addTextChangedListener(_textWatcher2);

        _spinnerCurrency2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                _textWatcher1.setIgnoreChange(true);
                _textWatcher2.setIgnoreChange(true);
                _textWatcher1.triggerTextChange(_textInputAmount1.getText());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){}
        });

        _spinnerCurrency1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                _textWatcher1.setIgnoreChange(true);
                _textWatcher2.setIgnoreChange(true);
                _textWatcher2.triggerTextChange(_textInputAmount2.getText());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        _compositeDisposable.add(
                _model.getCurrentExchangeRates(CurrencyConverterViewModel.DEFAULT_CURRENCY)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::populateControls,
                                throwable -> {})
        );

    }

    private AmountTextWatcher createTextWatcher(TextInputEditText editText, AppCompatSpinner fromSpinner, AppCompatSpinner toSpinner)
    {
        AmountTextWatcher textWatcher = new AmountTextWatcher(this, editText, fromSpinner, toSpinner);
        _compositeDisposable.add(
                textWatcher.TextChange()
                        .debounce(200, TimeUnit.MILLISECONDS)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(Schedulers.io())
                        .subscribe(fromValue->
                                _compositeDisposable.add(
                                        _model.convert(fromValue.FromCurrencyUnit, fromValue.Value, fromValue.ToCurrencyUnit)
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(this::updateUIWithConversionResult))
                        )
        );

        return textWatcher;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(_compositeDisposable != null && !_compositeDisposable.isDisposed())
        {
            _compositeDisposable.dispose();
        }
    }

    private void populateControls(CurrencyRate currencyRates)
    {
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter1.addAll(currencyRates.rates.keySet());
        adapter1.insert(currencyRates.base, 0);
        _spinnerCurrency1.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter2.addAll(currencyRates.rates.keySet());
        _spinnerCurrency2.setAdapter(adapter2);
    }

    private void updateUIWithConversionResult(CurrencyValue result)
    {
        _textViewRateInfo.setText(String.format("1 %s = %s %s on %s", result.FromCurrencyUnit, result.ToCurrencyRate, result.ToCurrencyUnit, _dateFormat.format(result.Date)));
        String spinner1Selection = (String)_spinnerCurrency1.getSelectedItem();
        String spinner2Selection = (String)_spinnerCurrency2.getSelectedItem();
        String value = String.format("%.2f", result.Value);

        if(spinner1Selection.equalsIgnoreCase(result.ToCurrencyUnit))
        {
            _textInputAmount1.setText(value);
        }
        else if(spinner1Selection.equalsIgnoreCase(spinner2Selection))
        {
            //TODO:
        }
        {
            _textInputAmount2.setText(value);
        }
    }
}
