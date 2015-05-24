package yu.kyp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by subin on 2015-05-19.
 */
public class AlarmActivity extends Activity {
    Uri eventUriString;
    ContentValues eventValues;
    Calendar mCalendar = Calendar.getInstance();
    private static SharedPreferences sp;
    String memo_title_is;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /**
         * 캘린더에 추가하는 부분
         */
        eventValues = new ContentValues();

//        String curDateStr = String.valueOf(curDate.getTime());

        //memowrite에서 메모 제목값을 받아온다.
        sp = getSharedPreferences("memo_title",MODE_PRIVATE);
        memo_title_is = sp.getString("title_is",null);

        if(Build.VERSION.SDK_INT >= 8)
            eventUriString  = Uri.parse("content://com.android.calendar/events");
        else
            eventUriString  = Uri.parse("content://calendar/events");


        //UTC 사용법은 http://www.cyworld.com/didtmdblog/14276095 참고
        //캘린더 코딩은 http://zeph1e.tistory.com/34와
        //http://www.kmshack.kr/android-%EC%9D%BC%EC%A0%95-%EC%96%B4%ED%94%8C%EB%A6%AC%EC%BC%80%EC%9D%B4%EC%85%98-%ED%98%B8%ED%99%98%EB%90%98%EB%8A%94-%EC%95%B1-%EA%B0%9C%EB%B0%9C%ED%95%98%EA%B8%B0-%ED%8C%81/
        //를 참고
        eventValues.put("calendar_id", 1); // id, We need to choose from our mobile for primary its 1
        eventValues.put("title", memo_title_is);
        eventValues.put("description", "");
        eventValues.put("eventLocation", "");
        //year값 +1900 = 원하는 년도
        //month값 + 1 = 원하는 월
        eventValues.put("dtstart", Date.UTC(115, 4, 21, 00, 00, 00));
        eventValues.put("dtend", Date.UTC(115, 4, 21, 00, 00, 00));
        eventValues.put("eventTimezone", TimeZone.getDefault().getID());
        eventValues.put("eventStatus", 1); // This information is sufficient for most entries tentative (0), confirmed (1) or canceled (2):
        eventValues.put("hasAlarm", 1); // 0 for false, 1 for true

        //캘린더에 넣기
        getContentResolver().insert(eventUriString, eventValues);
        /**
         * 캘린더에 추가하는 부분 끝
         */


        Date curDate = new Date();
        String curDateStr = String.valueOf(curDate.getTime());
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);

        setCalendar();
        setMemoDate();

        new DatePickerDialog(
                AlarmActivity.this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();


    }
    private void setCalendar(){
                Toast.makeText(AlarmActivity.this, "캘린더버튼이야", Toast.LENGTH_SHORT).show();
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();

                calendar.setTime(date);

                new TimePickerDialog(
                        AlarmActivity.this,
                        timeSetListener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                ).show();


        Date curDate = new Date();
        mCalendar.setTime(curDate);

        int year = mCalendar.get(Calendar.YEAR);
        int monthOfYear = mCalendar.get(Calendar.MONTH);
        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        String monthStr = String.valueOf(monthOfYear+1);
        if (monthOfYear < 9) {
            monthStr = "0" + monthStr;
        }

        String dayStr = String.valueOf(dayOfMonth);
        if (dayOfMonth < 10) {
            dayStr = "0" + dayStr;
        }

        int hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);

        String hourStr = String.valueOf(hourOfDay);
        if (hourOfDay < 10) {
            hourStr = "0" + hourStr;
        }

        String minuteStr = String.valueOf(minute);
        if (minute < 10) {
            minuteStr = "0" + minuteStr;
        }

    }
    private void setMemoDate() {
        Date date = new Date();

        //Calendar calendar = Calendar.getInstance();
        mCalendar.setTime(date);

        int year = mCalendar.get(Calendar.YEAR);
        int monthOfYear = mCalendar.get(Calendar.MONTH);
        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        String monthStr = String.valueOf(monthOfYear+1);
        if (monthOfYear < 9) {
            monthStr = "0" + monthStr;
        }

        String dayStr = String.valueOf(dayOfMonth);
        if (dayOfMonth < 10) {
            dayStr = "0" + dayStr;
        }

        int hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);

        String hourStr = String.valueOf(hourOfDay);
        if (hourOfDay < 10) {
            hourStr = "0" + hourStr;
        }

        String minuteStr = String.valueOf(minute);
        if (minute < 10) {
            minuteStr = "0" + minuteStr;
        }

    }
    /**
     * 날짜 설정 리스너
     */
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Toast.makeText(AlarmActivity.this,"날짜 설정이 눌렸음",Toast.LENGTH_SHORT).show();
            mCalendar.set(year, monthOfYear, dayOfMonth);

            String monthStr = String.valueOf(monthOfYear+1);
            if (monthOfYear < 9) {
                monthStr = "0" + monthStr;
            }

            String dayStr = String.valueOf(dayOfMonth);
            if (dayOfMonth < 10) {
                dayStr = "0" + dayStr;
            }

        }
    };

    /**
     * 시간 설정 리스너
     */
    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hour_of_day, int minute) {
            Toast.makeText(AlarmActivity.this,"시간 설정이 눌렸음",Toast.LENGTH_SHORT).show();
            mCalendar.set(Calendar.HOUR_OF_DAY, hour_of_day);
            mCalendar.set(Calendar.MINUTE, minute);

            String hourStr = String.valueOf(hour_of_day);
            if (hour_of_day < 10) {
                hourStr = "0" + hourStr;
            }

            String minuteStr = String.valueOf(minute);
            if (minute < 10) {
                minuteStr = "0" + minuteStr;
            }
        }
    };
}
