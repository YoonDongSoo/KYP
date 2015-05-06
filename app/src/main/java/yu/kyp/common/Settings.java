package yu.kyp.common;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
* Created by Chaejin on 2015-05-04.
*/

public class Settings {


    private SharedPreferences pref = null;
    Context mcontext;

    public Settings(Context context)
    {
        mcontext = context;
        pref = PreferenceManager.getDefaultSharedPreferences(context);

    }

    /**
     * 기본 배율 가져오기
     * @return
     */
    public float getDefaultFactor()
    {
        return Float.valueOf(pref.getString("button_percent_setting","1.00")) ;
    }

    /**
     * 확대/축소 배율 가져오기
     * @return
     */
    public float getZoomFactor()
    {
        return Float.valueOf(pref.getString("button_zoominout_percent_setting","0.25")) ;
    }
    /**
     * 폰트타입 가져오기
     * @return
     */
    public String getFontType()
    {
        return pref.getString("button_font_setting","0");
    }

    /**
     * 배경타입 가져오기
     * @return
     */
    public int getBackgroundType()
    {
        return Integer.valueOf(pref.getString("button_background_setting","0"));
    }

    /**
     * 알림 타입 가져오기
     */
    public int getAlarmType()
    {
        return Integer.valueOf(pref.getString("button_alarm_setting","0"));
    }

    /**
     *목록타입 가져오기
     * @return
     */
    public int getListType()
    {
        return Integer.valueOf(pref.getString("button_list_setting","2"));
    }

//    private int getArrayIndex(int array, String findIndex) {
//        String[] arrayString = mcontext.getResources().getStringArray(array);
//        for (int e = 0; e < arrayString.length; e++) {
//            if (arrayString[e].equals(findIndex))
//                return e;
//        }
//        return -1;
//    }
}
