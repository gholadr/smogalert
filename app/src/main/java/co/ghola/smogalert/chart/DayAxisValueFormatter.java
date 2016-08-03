package co.ghola.smogalert.chart;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import java.text.DecimalFormat;

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
        mFormat = new DecimalFormat("###,###,##0.0");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int days = (int) value;
        return days + " Day"; // e.g. append a dollar-sign
    }


    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
