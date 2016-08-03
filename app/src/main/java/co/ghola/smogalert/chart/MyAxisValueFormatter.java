package co.ghola.smogalert.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.formatter.FormattedStringCache;

import java.text.DecimalFormat;

/**
 * Created by alecksjohansson on 7/28/16.
 */
public class MyAxisValueFormatter implements AxisValueFormatter {

    private DecimalFormat mFormat;
    private FormattedStringCache.PrimFloat mFormattedStringCache;

    public MyAxisValueFormatter() {
        mFormattedStringCache = new FormattedStringCache.PrimFloat(new DecimalFormat("###,###,###,##0"));
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mFormattedStringCache.getFormattedValue(value);
    }

    @Override
    public int getDecimalDigits() {
        return 1;
    }
}
