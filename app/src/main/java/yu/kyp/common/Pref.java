package yu.kyp.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by DONGSOO on 2015-05-23.
 */
public class Pref {
    private static final String PREF_NAME = "KYP_PREFERENCE";

    /**
     * PenPaletteActivity에서 alphaSeekBar가 onStopTrackingTouch 되었을 때 값
     * @param context
     * @param defValue
     * @return
     * sharedPreferences의 "alpha_value"값 리턴
     */
    public static int getAlpha(Context context, int defValue)
    {
        String key = "alpha_value_is";
        return context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE).getInt(key, defValue);
    }

    /**
     * PenPaletteActivity에서 alphaSeekBar의 onStopTrackingTouch값을 저장
     * @param context
     * @param value
     */
    public static void setAlpha(Context context, int value)
    {
        String key = "alpha_value_is";
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * SharedPreferences에서 지우개 두께값 가져오기
     * @param context
     * @param defValue
     * @return
     */
    public static int getEraserSize(Context context, int defValue) {
        String key = "e_size_value";
        return context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE).getInt(key, defValue);
    }

    /**
     * SharedPreferences에서 지우개 두께값 저장하기
     * @param context
     * @param value
     */
    public static void setEraserSize(Context context, int value)
    {
        String key = "e_size_value";
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * SharedPreferences에서 펜 두께값 가져오기
     * @param context
     * @param defValue
     * @return
     */
    public static int getPenSize(Context context, int defValue) {
        String key = "p_size_value";
        return context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE).getInt(key, defValue);
    }

    /**
     * SharedPreferences에서 v펜 두께값 저장하기
     * @param context
     * @param value
     */
    public static void setPenSize(Context context, int value)
    {
        String key = "p_size_value";
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * SharedPreferences에서 블루투스 장치주소 가져오기
     * @param context
     * @param defValue
     * @return
     */
    public static String getDeviceAddress(Context context, String defValue) {
        String key = "bluetooth_device_address";
        return context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE).getString(key, defValue);
    }

    /**
     * SharedPreferences에서 블루투스 장치주소 저장하기
     * @param context
     * @param value
     */
    public static void setDeviceAddress(Context context, String value)
    {
        String key = "bluetooth_device_address";
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * SharedPreferences에서 블루투스 이름 가져오기
     * @param context
     * @param defValue
     * @return
     */
    public static String getDeviceName(Context context, String defValue) {
        String key = "bluetooth_device_name";
        return context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE).getString(key, defValue);
    }

    /**
     * SharedPreferences에서 블루투스 이름 저장하기
     * @param context
     * @param value
     */
    public static void setDeviceName(Context context, String value)
    {
        String key = "bluetooth_device_name";
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }
}
