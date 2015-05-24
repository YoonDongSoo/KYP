package yu.kyp;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

//import android.annotation.TargetApi;
//import android.content.Context;
//import android.content.res.Configuration;
//import android.media.Ringtone;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.preference.ListPreference;
//import android.preference.Preference;
//import android.preference.PreferenceActivity;
//import android.preference.PreferenceFragment;
//import android.preference.PreferenceManager;
//import android.preference.RingtonePreference;
//import android.text.TextUtils;
//
//import java.util.List;
//
///**
//* A {@link PreferenceActivity} that presents a set of application settings. On
//* handset devices, settings are presented as a single list. On tablets,
//* settings are split by category, with category headers shown to the left of
//* the list of settings.
//* <p/>
//* See <a href="http://developer.android.com/design/patterns/settings.html">
//* Android Design: Settings</a> for design guidelines and the <a
//* href="http://developer.android.com/guide/topics/ui/settings.html">Settings
//* API Guide</a> for more information on developing a Settings UI.
//*/
//public class SettingsActivity extends PreferenceActivity {
//    /**
//     * Determines whether to always show the simplified settings UI, where
//     * settings are presented in a single list. When false, settings are shown
//     * as a master/detail two-pane view on tablets. When true, a single pane is
//     * shown on tablets.
//     */
//    private static final boolean ALWAYS_SIMPLE_PREFS = false;
//
//
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//
//        setupSimplePreferencesScreen();
//    }
//
//    /**
//     * Shows the simplified settings UI if the device configuration if the
//     * device configuration dictates that a simplified, single-pane UI should be
//     * shown.
//     */
//    private void setupSimplePreferencesScreen() {
//        if (!isSimplePreferences(this)) {
//            return;
//        }
//
//        // In the simplified UI, fragments are not used at all and we instead
//        // use the older PreferenceActivity APIs.
//
//        // Add 'general' preferences.
////        addPreferencesFromResource(R.xml.pref_general);
////
////        // Add 'notifications' preferences, and a corresponding header.
////        PreferenceCategory fakeHeader = new PreferenceCategory(this);
////        fakeHeader.setTitle(R.string.pref_header_notifications);
////        getPreferenceScreen().addPreference(fakeHeader);
////        addPreferencesFromResource(R.xml.pref_notification);
////
////        // Add 'data and sync' preferences, and a corresponding header.
////        fakeHeader = new PreferenceCategory(this);
////        fakeHeader.setTitle(R.string.pref_header_data_sync);
////        getPreferenceScreen().addPreference(fakeHeader);
////        addPreferencesFromResource(R.xml.pref_data_sync);
////
////        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
////        // their values. When their values change, their summaries are updated
////        // to reflect the new value, per the Android Design guidelines.
////        bindPreferenceSummaryToValue(findPreference("example_text"));
////        bindPreferenceSummaryToValue(findPreference("example_list"));
////        bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
////        bindPreferenceSummaryToValue(findPreference("sync_frequency"));
//        addPreferencesFromResource(R.xml.pref_settings);
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public boolean onIsMultiPane() {
//        return isXLargeTablet(this) && !isSimplePreferences(this);
//    }
//
//    /**
//     * Helper method to determine if the device has an extra-large screen. For
//     * example, 10" tablets are extra-large.
//     */
//    private static boolean isXLargeTablet(Context context) {
//        return (context.getResources().getConfiguration().screenLayout
//                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
//    }
//
//    /**
//     * Determines whether the simplified settings UI should be shown. This is
//     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
//     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
//     * doesn't have an extra-large screen. In these cases, a single-pane
//     * "simplified" settings UI should be shown.
//     */
//    private static boolean isSimplePreferences(Context context) {
//        return ALWAYS_SIMPLE_PREFS
//                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
//                || !isXLargeTablet(context);
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public void onBuildHeaders(List<Header> target) {
//        if (!isSimplePreferences(this)) {
//            loadHeadersFromResource(R.xml.pref_headers, target);
//        }
//    }
//
//    /**
//     * A preference value change listener that updates the preference's summary
//     * to reflect its new value.
//     */
//    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
//        @Override
//        public boolean onPreferenceChange(Preference preference, Object value) {
//            String stringValue = value.toString();
//
//            if (preference instanceof ListPreference) {
//                // For list preferences, look up the correct display value in
//                // the preference's 'entries' list.
//                ListPreference listPreference = (ListPreference) preference;
//                int index = listPreference.findIndexOfValue(stringValue);
//
//                // Set the summary to reflect the new value.
//                preference.setSummary(
//                        index >= 0
//                                ? listPreference.getEntries()[index]
//                                : null);
//
//            } else if (preference instanceof RingtonePreference) {
//                // For ringtone preferences, look up the correct display value
//                // using RingtoneManager.
//                if (TextUtils.isEmpty(stringValue)) {
//                    // Empty values correspond to 'silent' (no ringtone).
//                    preference.setSummary(R.string.pref_ringtone_silent);
//
//                } else {
//                    Ringtone ringtone = RingtoneManager.getRingtone(
//                            preference.getContext(), Uri.parse(stringValue));
//
//                    if (ringtone == null) {
//                        // Clear the summary if there was a lookup error.
//                        preference.setSummary(null);
//                    } else {
//                        // Set the summary to reflect the new ringtone display
//                        // name.
//                        String name = ringtone.getTitle(preference.getContext());
//                        preference.setSummary(name);
//                    }
//                }
//
//            } else {
//                // For all other preferences, set the summary to the value's
//                // simple string representation.
//                preference.setSummary(stringValue);
//            }
//            return true;
//        }
//    };
//
//    /**
//     * Binds a preference's summary to its value. More specifically, when the
//     * preference's value is changed, its summary (line of text below the
//     * preference title) is updated to reflect the value. The summary is also
//     * immediately updated upon calling this method. The exact display format is
//     * dependent on the type of preference.
//     *
//     * @see #sBindPreferenceSummaryToValueListener
//     */
//    private static void bindPreferenceSummaryToValue(Preference preference) {
//        // Set the listener to watch for value changes.
//        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
//
//        // Trigger the listener immediately with the preference's
//        // current value.
//        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
//                PreferenceManager
//                        .getDefaultSharedPreferences(preference.getContext())
//                        .getString(preference.getKey(), ""));
//    }
//
//    /**
//     * This fragment shows general preferences only. It is used when the
//     * activity is showing a two-pane settings UI.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static class GeneralPreferenceFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.pref_general);
//
//            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
//            // to their values. When their values change, their summaries are
//            // updated to reflect the new value, per the Android Design
//            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("example_text"));
//            bindPreferenceSummaryToValue(findPreference("example_list"));
//        }
//    }
//
//    /**
//     * This fragment shows notification preferences only. It is used when the
//     * activity is showing a two-pane settings UI.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static class NotificationPreferenceFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.pref_notification);
//
//            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
//            // to their values. When their values change, their summaries are
//            // updated to reflect the new value, per the Android Design
//            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
//        }
//    }
//
//    /**
//     * This fragment shows data and sync preferences only. It is used when the
//     * activity is showing a two-pane settings UI.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static class DataSyncPreferenceFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.pref_data_sync);
//
//            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
//            // to their values. When their values change, their summaries are
//            // updated to reflect the new value, per the Android Design
//            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
//        }
//    }
//}
public class SettingsActivity extends PreferenceActivity{

    public static String ThemeBackGround = null;
    public static String Alarm = null;
    public static String ListSetting = null;
    RelativeLayout memoListRelativeLayout;
    MemoListActivity memolistactivity;
    static SharedPreferences sp;
    static SharedPreferences list_select;

      protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        memoListRelativeLayout = (RelativeLayout)findViewById(R.id.memoListRelativeLayout);

        addPreferencesFromResource(R.xml.pref_settings);
          memolistactivity = new MemoListActivity();

        final ListPreference button_background_setting = (ListPreference)findPreference("button_background_setting");
          final ListPreference button_list_setting = (ListPreference)findPreference("button_list_setting");
          final Preference button_alarm_setting = (Preference)findPreference("button_alarm_setting");
        setOnPreferenceChange(findPreference("button_percent_setting"));
        setOnPreferenceChange(findPreference("button_zoominout_percent_setting"));
        setOnPreferenceChange(findPreference("button_font_setting"));
        setOnPreferenceChange(findPreference("button_background_setting"));
        setOnPreferenceChange(findPreference("button_alarm_setting"));
        setOnPreferenceChange(findPreference("button_list_setting"));

          ThemeBackGround = button_background_setting.getValue();
          //설정화면에서 테마 설정 버튼이 눌렸을 때
          button_background_setting.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
              @Override
              public boolean onPreferenceChange(Preference preference, Object newValue) {
                  StringBuffer result = new StringBuffer();
                  result.append("설정이 완료되었습니다.");
                  ThemeBackGround = newValue.toString();

                  sp = getSharedPreferences("setbackground", MODE_PRIVATE);
                  SharedPreferences.Editor editor = sp.edit();
                  editor.putInt("notheme", 1);
                  editor.commit();

                  //A instanceof B란 A의 객체가 B에 속해있는지(상속) 여부를 판단
                  //만약 속해있다면 TRUE를 출력한다.
                  if(preference instanceof ListPreference) {

                      ListPreference listPreference = (ListPreference) preference;
                      int index = listPreference.findIndexOfValue(ThemeBackGround);
                      preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
                      /*Toast.makeText(SettingsActivity.this,"선택한 테마는? " + listPreference.getEntries()[index],Toast.LENGTH_SHORT).show();
                      if(listPreference.getEntries()[index].equals("테마1")){
                          sp = getSharedPreferences("setbackground",MODE_PRIVATE);
                          SharedPreferences.Editor editor2 = sp.edit();
                          editor2.putInt("theme", 1);
                          editor2.commit();
                      }
                      else if(listPreference.getEntries()[index].equals("테마2")){
                          sp = getSharedPreferences("setbackground",MODE_PRIVATE);
                          SharedPreferences.Editor editor2 = sp.edit();
                          editor2.putInt("theme",2);
                          editor2.commit();
                      }
                      else if(listPreference.getEntries()[index].equals("테마3")){
                          sp = getSharedPreferences("setbackground",MODE_PRIVATE);
                          SharedPreferences.Editor editor2 = sp.edit();
                          editor2.putInt("theme",3);
                          editor2.commit();
                      }
                      else if(listPreference.getEntries()[index].equals("테마4")){
                          sp = getSharedPreferences("setbackground",MODE_PRIVATE);
                          SharedPreferences.Editor editor2 = sp.edit();
                          editor2.putInt("theme",4);
                          editor2.commit();
                      }
                      else if(listPreference.getEntries()[index].equals("테마5")){
                          sp = getSharedPreferences("setbackground",MODE_PRIVATE);
                          SharedPreferences.Editor editor2 = sp.edit();
                          editor2.putInt("theme",5);
                          editor2.commit();
                      }
                      else if(listPreference.getEntries()[index].equals("테마6")){
                          sp = getSharedPreferences("setbackground",MODE_PRIVATE);
                          SharedPreferences.Editor editor2 = sp.edit();
                          editor2.putInt("theme",6);
                          editor2.commit();
                      }
                      else if(listPreference.getEntries()[index].equals("테마7")){
                          sp = getSharedPreferences("setbackground",MODE_PRIVATE);
                          SharedPreferences.Editor editor2 = sp.edit();
                          editor2.putInt("theme",7);
                          editor2.commit();
                      }*/
                  }
                  return true;
              }
          });
          ListSetting = button_list_setting.getValue();
          //리스트 세팅 버튼이 눌렸을 때
          button_list_setting.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
              @Override
              public boolean onPreferenceChange(Preference preference, Object newValue) {
                  StringBuffer result = new StringBuffer();
                  result.append("설정이 완료되었습니다.");
                  ListSetting = newValue.toString();

                  if(preference instanceof ListPreference) {
                      ListPreference listPreference = (ListPreference) preference;
                      int index = listPreference.findIndexOfValue(ListSetting);
                      preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
//                      Toast.makeText(SettingsActivity.this,"선택한 리스트는? " + listPreference.getEntries()[index],Toast.LENGTH_SHORT).show();
                     /* if(listPreference.getEntries()[index].equals("바둑판 배열")){
                          Toast.makeText(SettingsActivity.this,"1",Toast.LENGTH_SHORT).show();
                          list_select = getSharedPreferences("list_select",MODE_PRIVATE);
                          SharedPreferences.Editor editor3 = list_select.edit();
                          editor3.putInt("list_num", 1);
                          editor3.commit();
                      }
                      else if(listPreference.getEntries()[index].equals("Timeline")){
                          Toast.makeText(SettingsActivity.this,"2",Toast.LENGTH_SHORT).show();
                          list_select = getSharedPreferences("list_select",MODE_PRIVATE);
                          SharedPreferences.Editor editor3 = list_select.edit();
                          editor3.putInt("list_num",2);
                          editor3.commit();
                      }
                      else if(listPreference.getEntries()[index].equals("List")){
                          Toast.makeText(SettingsActivity.this,"3",Toast.LENGTH_SHORT).show();
                          list_select = getSharedPreferences("list_select",MODE_PRIVATE);
                          SharedPreferences.Editor editor3 = list_select.edit();
                          editor3.putInt("list_num",3);
                          editor3.commit();
                      }*/
                  }
                  return true;
              }
          });
      }


    private void setOnPreferenceChange(Preference mPreference) {
        mPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);

        onPreferenceChangeListener.onPreferenceChange(mPreference,
                PreferenceManager.getDefaultSharedPreferences(mPreference.getContext()).
                        getString(mPreference.getKey(), ""));
    }

    private Preference.OnPreferenceChangeListener onPreferenceChangeListener = new
            Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
//            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences();
//            int value = sharedPreferences.getInt("button_percent_setting",30);


            String stringValue = newValue.toString();

            if(preference instanceof ListPreference) {

                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

            }
//            else if(preference instanceof SeekBarPreference)
//            {
//
//                preference.setSummary(value);
//            }
            return true;
        }

    };





}
