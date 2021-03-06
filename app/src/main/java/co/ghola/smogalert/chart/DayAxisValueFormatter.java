package co.ghola.smogalert.chart;

import android.util.Log;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.google.api.client.util.DateTime;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by alecksjohansson on 7/28/16.
 */
public class DayAxisValueFormatter implements AxisValueFormatter {

    private BarLineChartBase<?> chart;
    private DecimalFormat mFormat;
    public DayAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    public DayAxisValueFormatter()
    {
        mFormat = new DecimalFormat("###,###,##0");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return String.valueOf((int) value);
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
