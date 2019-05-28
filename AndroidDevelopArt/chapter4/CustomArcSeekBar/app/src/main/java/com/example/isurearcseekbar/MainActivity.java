package com.example.isurearcseekbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ArcSeekBar.OnProgressChangedListener{

    //空调风量最高档位
    public static final int AIR_LEVEL_MAX_DRIVING = 8;
    //空调温度最高档位
    public static final int TEMP_LEVEL_MAX_DRIVING = 32;
    //空调温度最低值 16.5°C
    public static final int TEMP_MIN_VALUE_DRIVING = 165;
    //空调温度步进 0.5°C
    public static final int TEMP_STEP = 5;

    private ArcSeekBar mArcSeekBarAirLevel;
    private ArcSeekBar mArcSeekBarTempLevel;
    private TextView mTextAirLevelValue;
    private TextView mTextTemperatureValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mArcSeekBarAirLevel = (ArcSeekBar) findViewById(R.id.arc_seek_bar_air_level);
        mArcSeekBarAirLevel.setOnProgressChangedListener(this);
        mArcSeekBarTempLevel = (ArcSeekBar) findViewById(R.id.arc_seek_bar_temp);
        mArcSeekBarTempLevel.setOnProgressChangedListener(this);
        mTextAirLevelValue = (TextView) findViewById(R.id.text_air_level_value);
        mTextTemperatureValue = (TextView) findViewById(R.id.text_temperature_value);
        mArcSeekBarAirLevel.setMaxProgress(AIR_LEVEL_MAX_DRIVING);
        mArcSeekBarTempLevel.setMaxProgress(TEMP_LEVEL_MAX_DRIVING);
    }

    public void onProgressChanged(ArcSeekBar arcSeekBar, int progress, boolean isFinalProgress) {
        switch (arcSeekBar.getId()) {
            case R.id.arc_seek_bar_air_level:
                if(isFinalProgress) {
                    if(progress < 0 || progress > AIR_LEVEL_MAX_DRIVING) {
                        Toast.makeText(this, R.string.air_is_out_of_range, Toast.LENGTH_LONG).show();
                        return;
                    }
                    mTextAirLevelValue.setText(Integer.toString(progress));
                }
                break;
            case R.id.arc_seek_bar_temp:
                if(isFinalProgress) {
                    int tempValue = TEMP_MIN_VALUE_DRIVING + progress * TEMP_STEP;

                    if(tempValue < TEMP_MIN_VALUE_DRIVING || tempValue > (TEMP_MIN_VALUE_DRIVING + TEMP_STEP * TEMP_LEVEL_MAX_DRIVING)) {
                        Toast.makeText(this, R.string.temperature_is_out_of_range, Toast.LENGTH_LONG).show();
                        return;
                    }

                    String tempStr = tempValue / 10 + "." + tempValue % 10 + "°C";
                    mTextTemperatureValue.setText(tempStr);
                }
                break;
        }
    }
}
