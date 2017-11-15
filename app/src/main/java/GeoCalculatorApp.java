import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by Eldridge on 11/4/2017.
 */

public class GeoCalculatorApp extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
