package com.example.leochris.launcher.weather;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leochris.launcher.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import zh.wang.android.yweathergetter4a.WeatherInfo;
import zh.wang.android.yweathergetter4a.YahooWeather;
import zh.wang.android.yweathergetter4a.YahooWeatherInfoListener;

public class WeatherTab extends Fragment implements YahooWeatherInfoListener {

    Typeface weatherFont;

    private TextView mTvTitle;
    private TextView weatherIcon;

    private TextView date;
    private TextView temperature;
    private TextView weatherStatus;
    private TextView humidity;
    private TextView windspeed;
    private TextView icon1;
    private TextView icon2;
    private TextView icon3;
    private TextView icon4;
    private TextView icon5;
    private TextView icon6;

    private LineChart mLineChart;
    float[][] mValues = new float[2][6];

    //weather updates every 5s
    private YahooWeather mYahooWeather = YahooWeather.getInstance(5000, true);

    private ProgressDialog mProgressDialog;

    public WeatherTab() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_weather, container, false);

        //default weather font
        weatherFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/weather.ttf");

        //progress dialog
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        //update textview
        mTvTitle = (TextView) v.findViewById(R.id.textview_title);
        date = (TextView) v.findViewById(R.id.date_and_time);
        temperature = (TextView) v.findViewById(R.id.temperature);
        weatherStatus = (TextView) v.findViewById(R.id.weather_status);
        humidity = (TextView) v.findViewById(R.id.humidity_index);
        windspeed = (TextView) v.findViewById(R.id.windspped_index);

        //forecast icons
        icon1 = (TextView) v.findViewById(R.id.icon1);
        icon1.setTypeface(weatherFont);

        icon2 = (TextView) v.findViewById(R.id.icon2);
        icon2.setTypeface(weatherFont);

        icon3 = (TextView) v.findViewById(R.id.icon3);
        icon3.setTypeface(weatherFont);

        icon4 = (TextView) v.findViewById(R.id.icon4);
        icon4.setTypeface(weatherFont);

        icon5 = (TextView) v.findViewById(R.id.icon5);
        icon5.setTypeface(weatherFont);

        icon6 = (TextView) v.findViewById(R.id.icon6);
        icon6.setTypeface(weatherFont);

        //forecast line graph
        mLineChart = new LineChart((CardView) v.findViewById(R.id.chart_card), getContext());
        mLineChart.init();

        //location
        String _location = "Singapore, SG";
        if (!TextUtils.isEmpty(_location)) {
            searchByPlaceName(_location);
            showProgressDialog();
        } else {
            Toast.makeText(getContext(), "location is not inputted", Toast.LENGTH_SHORT).show();
        }

        //today forecast
        weatherIcon = (TextView) v.findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);
        return v;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        hideProgressDialog();
        mProgressDialog = null;
        super.onDestroy();
    }

    @Override
    public void gotWeatherInfo(final WeatherInfo weatherInfo, YahooWeather.ErrorType errorType) {
        // TODO Auto-generated method stub
        hideProgressDialog();
        if (weatherInfo != null) {
            setNormalLayout();

            SimpleDateFormat sdf = new SimpleDateFormat("EE, dd MMM HH:mm",
                    Locale.getDefault());
            Date mDate = new Date();

            mTvTitle.setText("Clementi New Town");

            date.setText(sdf.format(mDate));
            temperature.setText(String.valueOf(weatherInfo.getCurrentTemp()) + "\u2103");
            weatherStatus.setText(weatherInfo.getCurrentText());
            humidity.setText(weatherInfo.getAtmosphereHumidity() + "%");
            windspeed.setText(weatherInfo.getWindSpeed() + "km/h");

            mValues[0][0] = (float) weatherInfo.getCurrentTemp() - 2;
            mValues[1][0] = (float) weatherInfo.getCurrentTemp() + 2;

            if (weatherInfo.getCurrentCode() != 3200) {
                //update weather icons
                setWeatherIcon(weatherInfo.getCurrentCode(), weatherIcon);
                setWeatherIcon(weatherInfo.getCurrentCode(), icon1);
            }
            for (int i = 0; i < YahooWeather.FORECAST_INFO_MAX_SIZE; i++) {
                //parse infos into graph values
                final WeatherInfo.ForecastInfo forecastInfo = weatherInfo.getForecastInfoList().get(i);
                mValues[0][i+1] = (float) forecastInfo.getForecastTempLow();
                mValues[1][i+1] = (float) forecastInfo.getForecastTempHigh();
                switch(i) {
                    case 0:
                        setWeatherIcon(forecastInfo.getForecastCode(), icon2);
                        break;
                    case 1:
                        setWeatherIcon(forecastInfo.getForecastCode(), icon3);
                        break;
                    case 2:
                        setWeatherIcon(forecastInfo.getForecastCode(), icon4);
                        break;
                    case 3:
                        setWeatherIcon(forecastInfo.getForecastCode(), icon5);
                        break;
                    case 4:
                        setWeatherIcon(forecastInfo.getForecastCode(), icon6);
                        break;
                }
            }
            mLineChart.setmValues(mValues);
            mLineChart.setRange((int) getMinValue(mValues[0])-2, (int) getMaxValue(mValues[1]));
            mLineChart.update();
        } else {
            setNoResultLayout(errorType.name());
        }
    }

    private void setNormalLayout() {
        mTvTitle.setVisibility(View.VISIBLE);
    }

    private void setNoResultLayout(String errorMsg) {
        mTvTitle.setVisibility(View.INVISIBLE);
        mProgressDialog.cancel();
    }

    private void searchByPlaceName(String location) {
        mYahooWeather.setNeedDownloadIcons(true);
        mYahooWeather.setUnit(YahooWeather.UNIT.CELSIUS);
        mYahooWeather.setSearchMode(YahooWeather.SEARCH_MODE.PLACE_NAME);
        mYahooWeather.queryYahooWeatherByPlaceName(getContext(), location, WeatherTab.this);
    }

    private void showProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private void setWeatherIcon(int actualId, TextView mTextView){
        String icon = "";

        switch(actualId) {
            case 0:
                icon = getString(R.string.wi_tornado);
                break;

            case 1:
                icon = getString(R.string.wi_wu_tstorms);
                break;

            case 2:
                icon = getString(R.string.wi_hurricane);
                break;

            case 3:
                icon = getString(R.string.wi_thunderstorm);
                break;

            case 4:
                icon = getString(R.string.wi_thunderstorm);
                break;

            case 5:
                icon = getString(R.string.wi_day_rain_mix);
                break;

            case 6:
                icon = getString(R.string.wi_sleet);
                break;

            case 7:
                icon = getString(R.string.wi_day_snow);
                break;

            case 8:
                icon = getString(R.string.weather_drizzle);
                break;

            case 9:
                icon = getString(R.string.weather_drizzle);
                break;

            case 10:
                icon = getString(R.string.wi_rain);
                break;

            case 11:
                icon = getString(R.string.wi_showers);
                break;

            case 12:
                icon = getString(R.string.wi_showers);
                break;

            case 13:
                icon = getString(R.string.wi_snow);
                break;

            case 14:
            case 15:
            case 16:
                icon = getString(R.string.wi_snow);
                break;

            case 17:
                icon = getString(R.string.wi_hail);
                break;

            case 18:
                icon = getString(R.string.wi_sleet);
                break;

            case 19:
                icon = getString(R.string.wi_dust);
                break;

            case 20:
                icon = getString(R.string.weather_foggy);
                break;

            case 21:
                icon = getString(R.string.wi_day_haze);
                break;

            case 22:
                icon = getString(R.string.wi_smoke);
                break;

            case 23:
            case 24:
                icon = getString(R.string.wi_windy);
                break;

            case 25:
                icon = getString(R.string.wi_snowflake_cold);
                break;

            case 26:
                icon = getString(R.string.weather_cloudy);
                break;

            case 27:
                icon = getString(R.string.wi_night_cloudy);
                break;

            case 28:
                icon = getString(R.string.wi_day_cloudy);
                break;

            case 29:
                icon = getString(R.string.wi_night_partly_cloudy);
                break;

            case 30:
                icon = getString(R.string.wi_wu_partlycloudy);
                break;

            case 31:
                icon = getString(R.string.wi_night_clear);
                break;

            case 32:
                icon = getString(R.string.weather_sunny);
                break;

            case 33:
                icon = getString(R.string.wi_night_clear);
                break;

            case 34:
                icon = getString(R.string.wi_wu_clear);
                break;

            case 35:
                icon = getString(R.string.wi_rain_mix);
                break;

            case 36:
                icon = getString(R.string.wi_hot);
                break;

            case 37:
            case 38:
            case 39:
                icon = getString(R.string.wi_thunderstorm);
                break;

            case 40:
                icon = getString(R.string.wi_showers);
                break;

            case 41:
            case 42:
                icon = getString(R.string.wi_snow);
                break;

            case 44:
                icon = getString(R.string.wi_wu_partlycloudy);
                break;

            case 45:
                icon = getString(R.string.wi_thunderstorm);
                break;

            case 46:
                icon = getString(R.string.wi_snow);
                break;

            case 47:
                icon = getString(R.string.wi_thunderstorm);
                break;

            case 3200:
                icon = getString(R.string.wi_na);
                break;
        }

        mTextView.setText(icon);
    }

    // getting the maximum value
    public static float getMaxValue(float[] array) {
        float maxValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];
            }
        }
        return maxValue;
    }

    // getting the miniumum value
    public static float getMinValue(float[] array) {
        float minValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
            }
        }
        return minValue;
    }
}
