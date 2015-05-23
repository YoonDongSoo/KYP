package yu.kyp.common.database;

import android.content.Context;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by DONGSOO on 2015-03-24.
 */
public class CreateTables {
    /**
     * @설명 사용자의 폰에서 사용하는 테이블을 새로만든다.
     * @param dropTables
     * 기존 테이블을 지우려면 true, 그대로 유지하려면 false
     */
    public static void statics(Context ctx, boolean dropTables)
    {
        HashMap<String, String> col = new HashMap<>();
        DB db = new DB(ctx);

        // 노트 정보
        col.clear();
        col.put("NOTE_NO", "INTEGER PK AUTO");
        col.put("TITLE", "TEXT");
        col.put("NOTE_DATA", "BLOB");
        col.put("LAST_MOD_DT","TEXT NOT NULL");
        col.put("IS_DEL","INTEGER NOT NULL");
        col.put("BACKGROUND","INTEGER NOT NULL");
        if(dropTables==true)
            db.dropTable("NOTE");
        db.createTableWithoutDefaultPK("NOTE", col);

        // 썸네일
        ArrayList<BasicNameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("THUM_NO", "INTEGER PK AUTO"));
        list.add(new BasicNameValuePair("NOTE_NO", "INTEGER NOT NULL"));
        list.add(new BasicNameValuePair("THUM_DATA", "BLOB NOT NULL"));
        list.add(new BasicNameValuePair("FOREIGN KEY(NOTE_NO) REFERENCES NOTE(NOTE_NO)",""));
        /*col.clear();
        col.put("THUM_NO", "INTEGER PK AUTO");
        col.put("NOTE_NO", "INTEGER NOT NULL");
        col.put("THUM_DATA", "BLOB NOT NULL");
        col.put("FOREIGN KEY(NOTE_NO) REFERENCES NOTE(NOTE_NO)","");*/
        if(dropTables==true)
            db.dropTable("THUMBNAIL");
        db.createTableWithoutDefaultPK("THUMBNAIL", list);

        // 알람 정보
        list = new ArrayList<>();
        list.add(new BasicNameValuePair("ALARM_NO", "INTEGER PK AUTO"));
        list.add(new BasicNameValuePair("NOTE_NO", "INTEGER NOT NULL"));
        list.add(new BasicNameValuePair("ALARM_DT", "TEXT NOT NULL"));
        list.add(new BasicNameValuePair("FOREIGN KEY(NOTE_NO) REFERENCES NOTE(NOTE_NO)",""));
        /*col.clear();
        col.put("ALARM_NO", "INTEGER PK AUTO");
        col.put("NOTE_NO", "INTEGER NOT NULL");
        col.put("ALARM_DT", "TEXT NOT NULL");
        col.put("FOREIGN KEY(NOTE_NO) REFERENCES NOTE(NOTE_NO)","");*/
        if(dropTables==true)
            db.dropTable("ALARM");
        db.createTableWithoutDefaultPK("ALARM", list);
        //db.createIndex("POINT_DATA", "STROKE_NO");


    }

}
