package yu.kyp.common.database;

import android.content.Context;

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
        col.put("CONTENT", "TEXT");
        col.put("LAST_MOD_DT","TEXT");
        if(dropTables==true)
            db.dropTable("NOTE");
        db.createTableWithoutDefaultPK("NOTE", col);


        // 획 Vector 정보
        col.clear();
        col.put("STROKE_NO", "INTEGER PK AUTO");
        col.put("NOTE_NO", "INTEGER");
        col.put("COLOR", "INTEGER");
        col.put("THICKNESS", "INTEGER");
        col.put("FOREIGN KEY(NOTE_NO) REFERENCES NOTE(NOTE_NO)","");
        if(dropTables==true)
            db.dropTable("STROKE");
        db.createTableWithoutDefaultPK("STROKE", col);

        // 좌표 Vector 정보
        col.clear();
        col.put("POINT_NO", "INTEGER PK AUTO");
        col.put("STROKE_NO", "INTEGER");
        col.put("X", "REAL");
        col.put("Y", "REAL");
        col.put("FOREIGN KEY(STROKE_NO) REFERENCES STROKE(STROKE_NO)","");
        if(dropTables==true)
            db.dropTable("POINT_DATA");
        db.createTableWithoutDefaultPK("POINT_DATA", col);
        db.createIndex("POINT_DATA", "STROKE_NO");
    }

}
