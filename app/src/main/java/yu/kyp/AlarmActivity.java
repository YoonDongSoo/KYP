package yu.kyp;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import java.util.Date;

/**
 * Created by subin on 2015-05-19.
 */
public class AlarmActivity extends Activity {
    Uri eventUriString;
    ContentValues eventValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventValues = new ContentValues();


        if (Build.VERSION.SDK_INT >= 8)
            eventUriString = Uri.parse("content://com.android.calendar");
        else
            eventUriString = Uri.parse("content://calendar/events");

        eventValues.put("calendar_id", 1); // id, We need to choose from our mobile for primary its 1
        eventValues.put("title", "제목없음");
        eventValues.put("description", "내용");
        eventValues.put("dtstart", Date.UTC(77, 2, 26, 00, 00, 00));
        eventValues.put("duration", "P1D");
        eventValues.put("allDay", 1);
        eventValues.put("rrule", "FREQ=YEARLY;WKST=SU");
        getContentResolver().insert(Uri.parse(eventUriString + "/events"), eventValues);


        Uri eventUri = getContentResolver().insert(eventUriString, eventValues);

    //to get the last inserted event id
        long eventID = Long.parseLong(eventUri.getLastPathSegment());

    }
}
