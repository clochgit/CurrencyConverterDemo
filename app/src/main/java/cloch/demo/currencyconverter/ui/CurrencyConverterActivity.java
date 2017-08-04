package cloch.demo.currencyconverter.ui;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import cloch.demo.currencyconverter.R;
import cloch.demo.currencyconverter.business.ConverterOutput;
import cloch.demo.currencyconverter.business.CurrencyRate;
import cloch.demo.currencyconverter.business.AmountTextWatcher;
import cloch.demo.currencyconverter.business.DecimalFilter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CurrencyConverterActivity extends AppCompatActivity
{
    private final String TEXTINPUT1_TEXT = "1";
    private final String TEXTINPUT2_TEXT = "2";
    private final String SPINNER1_SELECTION = "3";
    private final String SPINNER2_SELECTION = "4";

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

        if(savedInstanceState != null)
        {
            String value = savedInstanceState.getString(TEXTINPUT1_TEXT);
            _textInputAmount1.setText(value == null ? "" : value);
            value = savedInstanceState.getString(TEXTINPUT2_TEXT);
            _textInputAmount2.setText(value == null ? "" : value);

        }

        InputFilter[] filters = new InputFilter[]{new DecimalFilter(2)};
        _textInputAmount1.setFilters(filters);
        _textInputAmount2.setFilters(filters);


        _textWatcher1 = createTextWatcher(_textInputAmount1, _spinnerCurrency1, _spinnerCurrency2);
        _textWatcher2 = createTextWatcher(_textInputAmount2, _spinnerCurrency2, _spinnerCurrency1);
        _textInputAmount1.addTextChangedListener(_textWatcher1);
        _textInputAmount2.addTextChangedListener(_textWatcher2);

        setupSpinner(_spinnerCurrency2, _textWatcher1);
        setupSpinner(_spinnerCurrency1, _textWatcher2);

        _compositeDisposable.add(
                _model.getCurrentExchangeRates(CurrencyConverterViewModel.DEFAULT_CURRENCY)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(rate->{populateControls(rate, savedInstanceState);},
                                throwable -> {})
        );

    }

    private void setupSpinner(AppCompatSpinner spinner, AmountTextWatcher textWatcher)
    {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                _textWatcher1.setIgnoreChange(true);
                _textWatcher2.setIgnoreChange(true);

                textWatcher.triggerTextChange(textWatcher.getParent().getText());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private AmountTextWatcher createTextWatcher(TextInputEditText editText, AppCompatSpinner fromSpinner, AppCompatSpinner toSpinner)
    {
        AmountTextWatcher textWatcher = new AmountTextWatcher(this, editText, fromSpinner, toSpinner);
        _compositeDisposable.add(
                textWatcher.TextChange()
                        .debounce(200, TimeUnit.MILLISECONDS)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(Schedulers.io())
                        .subscribe(input->
                                _compositeDisposable.add(
                                        _model.convert(input)
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(this::updateUIWithConversionResult))
                        )
        );

        return textWatcher;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putString(TEXTINPUT1_TEXT, _textInputAmount1.getText().toString());
        outState.putString(TEXTINPUT2_TEXT, _textInputAmount2.getText().toString());
        outState.putString(SPINNER1_SELECTION, (String)_spinnerCurrency1.getSelectedItem());
        outState.putString(SPINNER2_SELECTION, (String)_spinnerCurrency2.getSelectedItem());

        super.onSaveInstanceState(outState);
    }
    @Override
    public void onDestroy()
    {
        if(_compositeDisposable != null && !_compositeDisposable.isDisposed())
        {
            _compositeDisposable.dispose();
        }

        super.onDestroy();
    }

    private void populateControls(CurrencyRate currencyRates, Bundle saveInstanceState)
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.addAll(currencyRates.rates.keySet());
        adapter.insert(currencyRates.base, 0);
        _spinnerCurrency1.setAdapter(adapter);
        _spinnerCurrency2.setAdapter(adapter);
        if(saveInstanceState != null)
        {
            String value = saveInstanceState.getString(SPINNER1_SELECTION);
            int selectPos = adapter.getPosition(value);
            _spinnerCurrency1.setSelection(selectPos >= 0 ? selectPos : 0);

            value = saveInstanceState.getString(SPINNER2_SELECTION);
            selectPos = adapter.getPosition(value);
            _spinnerCurrency2.setSelection(selectPos >= 0 ? selectPos : 0);
        }
        else
        {
            _spinnerCurrency1.setSelection(0);
            _spinnerCurrency2.setSelection(0);
        }
    }

    private void updateUIWithConversionResult(ConverterOutput result)
    {
        _textViewRateInfo.setText(String.format("1 %s = %s %s on %s", result.Input.FromCurrencyUnit, result.ToCurrencyRate, result.Input.ToCurrencyUnit, _dateFormat.format(result.Date)));
        String value = String.format("%.2f", result.Output);

        if(result.Input.SourceID == _textInputAmount1.getId())
        {
            _textInputAmount2.setText(value);
        }
        else
        {
            _textInputAmount1.setText(value);
        }
    }
}
