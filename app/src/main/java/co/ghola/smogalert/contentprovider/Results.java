package co.ghola.smogalert.contentprovider;

/**
 * Created by gholadr on 4/18/16.
 */
public class Results {

    int mResult;
    String mResultValue;

    Results(int resultCode, String resultValue) {
        mResult = resultCode;
        mResultValue = resultValue;
    }

    public int getResult() {
        return mResult;
    }

    public String getResultValue() {
        return mResultValue;
    }
}
