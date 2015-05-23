package yu.kyp.image;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import yu.kyp.common.Utils;
import yu.kyp.common.database.DB;
import yu.kyp.common.database.DataTable;

/**
 * Created by DONGSOO on 2015-03-30.
 */
public class NoteManager {
    private static final String TAG = NoteManager.class.getSimpleName();
    private DB db = null;

    public NoteManager(Context context)
    {
        db = new DB(context);
    }

    /**
     * 노트 저장
     * @param note
     */
    public void saveNoteData(Note note)
    {
        //==========================================================
        // 1. 기존 note정보가 존재한다면 update, 없다면 insert를 한다.
        insertOrUpdateNote(note);

        //==========================================================
        // 2. Alarm, Thumbnail 을 DB에 입력한다.
        insertAlarm(note);
        insertThumbnail(note);

    }

    private void insertAlarm(Note note) {
        Alarm alarm = note.alarm;
        if(alarm!=null) {
            alarm.NOTE_NO = note.NOTE_NO;

            ContentValues values = new ContentValues();
            values.put("NOTE_NO", alarm.NOTE_NO);
            values.put("ALARM_DT", alarm.ALARM_DT);
            alarm.ALARM_NO = (int) db.execInsert("ALARM", values);
        }
    }

    private void insertThumbnail(Note note) {
        Thumbnail thumb = note.thumbnail;
        if(thumb!=null && thumb.THUM_DATA!=null) {
            thumb.NOTE_NO = note.NOTE_NO;

            ContentValues values = new ContentValues();
            values.put("NOTE_NO", thumb.NOTE_NO);
            values.put("THUM_DATA", Utils.getBytes(thumb.THUM_DATA));
            thumb.THUM_NO = (int) db.execInsert("THUMBNAIL", values);
        }
    }


    /**
     * 기존 note정보가 존재한다면 update, 없다면 insert를 한다.
     * @param note
     */
    private void insertOrUpdateNote(Note note) {
        ContentValues values = new ContentValues();
        values.put("TITLE",note.TITLE);
        values.put("NOTE_DATA",Utils.getBytes(note.NOTE_DATA) );
        values.put("LAST_MOD_DT", Utils.getYYYYMMDDHHMMSS());
        values.put("IS_DEL", note.IS_DEL);
        values.put("BACKGROUND", note.BACKGROUND);

        if(note.NOTE_NO!=null)
        {
            String whereClause = String.format("NOTE_NO=%d",note.NOTE_NO);
            if(db.is("NOTE",whereClause)==true)
            {

                db.execUpdate("NOTE",values,whereClause);

                //==========================================================
                // 2.기존 note정보가 존재한다면 alarm정보와 thumbnail정보는 삭제한다.
                removeAlarm(note.NOTE_NO);
                removeThumbnail(note.NOTE_NO);
            }
            else
            {
                note.NOTE_NO = null;    // DB에 없기때문에 insert를 한다.
            }
            //String q = String.format("SELECT COUNT(*) FROM NOTE WHERE NOTE_NO=%d",note.NOTE_NO);
        }
        // NOTE insert
        else
        {

            note.NOTE_NO = (int)db.execInsert("NOTE",values);

        }
    }

    /**
     * 썸네일 레코드 삭제
     * @param noteNo
     * @return
     */
    private int removeThumbnail(int noteNo) {
        String table = "THUMBNAIL ";
        String whereClause = "NOTE_NO="+noteNo;
        int removed = db.execDelete(table,whereClause);
        Log.v(TAG, "삭제된 획record수:" + removed);
        return removed;
    }

    /**
     * 알람 레코드 삭제
     * @param noteNo
     * @return
     */
    private int removeAlarm(int noteNo) {
        String table = "ALARM ";
        String whereClause = "NOTE_NO="+noteNo;
        int removed = db.execDelete(table,whereClause);
        Log.v(TAG,"삭제된 획record수:"+removed);
        return removed;
    }

    /**
     * 노트 삭제
     * 획정보와 좌표정보다 삭제한다.
     * @param noteNo
     * 노트 고유번호
     * @return
     * 삭제되면 1, 삭제되지않으면 0 리턴
     */
    public int removeNote(int noteNo)
    {
        // **알람,썸네일->노트 순서로 삭제해야 한다.**

        // 1. 알람 정보 삭제
        removeAlarm(noteNo);

        // 2. 썸네일정보 삭제
        removeThumbnail(noteNo);

        // 3. 노트 정보 삭제
        String table = "NOTE";
        String whereClause = "NOTE_NO="+noteNo;
        int removed = db.execDelete(table,whereClause);
        Log.v(TAG,"삭제된 노트record수:"+removed);
        return removed;
    }


    /**
     * 해당 번호의 노트 불러오기
     * @param noteNo
     * @return
     */
    public Note getNote(int noteNo) throws Exception {
        String sql = String.format(
                "SELECT A.NOTE_NO " +
                        ",A.TITLE " +
                        ",A.NOTE_DATA " +
                        ",A.LAST_MOD_DT " +
                        ",A.IS_DEL " +
                        ",A.BACKGROUND " +
                        ",B.THUM_NO " +
                        ",B.THUM_DATA " +
                        ",C.ALARM_NO " +
                        ",C.ALARM_DT " +
                        "FROM NOTE A " +
                        "LEFT JOIN THUMBNAIL B " +
                        "   ON B.NOTE_NO = A.NOTE_NO " +
                        "LEFT JOIN ALARM C " +
                        "   ON C.NOTE_NO = A.NOTE_NO " +
                        "WHERE A.NOTE_NO=%d ", noteNo);
        DataTable rs = db.execDataTable(sql);
        Note note = null;
        Alarm alarm = null;
        Thumbnail thumb = null;
        while(rs.next())
        {
            //=======================================
            // 1.노트 객체 생성 또는 가져오기
            note = getNoteInstance(rs, note);

            //=======================================
            // 2.알람 객체 생성기 -> note.alarm에 추가
            alarm = getAlarmInstance(rs, note);

            //=======================================
            // 3.썸네일 객체 생성 -> note.alarm에 추가
            thumb = getThumbnailInstance(rs,note);

        }//end while(rs.next())


        return note;
    }

    /**
     * 썸네일 객체 생성 -> note.alarm에 추가
     * @param rs
     * @param note
     * @return
     * @throws Exception
     */
    private Thumbnail getThumbnailInstance(DataTable rs, Note note) throws Exception {
        Thumbnail thumb = null;
        Integer thumNo = rs.getInteger("THUM_NO");
        if(thumNo!=null)
        {
            thumb = new Thumbnail();
            thumb.THUM_NO = rs.getInt("THUM_NO");
            thumb.NOTE_NO = note.NOTE_NO;
            thumb.THUM_DATA = Utils.getImage(rs.getBlob("THUM_DATA"));
            note.thumbnail = thumb;
        }
        return thumb;
    }

    /**
     * 알람 객체 생성 또는 가져오기 -> note.alarm에 추가
     * @param rs
     * @param note
     * @return
     * @throws Exception
     */
    private Alarm getAlarmInstance(DataTable rs, Note note) throws Exception {
        Alarm alarm = null;
        Integer alarmNo = rs.getInteger("ALARM_NO");
        if(alarmNo!=null) {
            alarm = new Alarm();
            alarm.ALARM_NO = rs.getInt("ALARM_NO");
            alarm.NOTE_NO = note.NOTE_NO;
            alarm.ALARM_DT = rs.getString("ALARM_DT");
            note.alarm = alarm;
        }
        return alarm;
    }


    /**
     *  노트 객체 생성 또는 가져오기
     * @param rs
     * @param note
     * @return
     * @throws Exception
     */
    private Note getNoteInstance(DataTable rs, Note note) throws Exception {
        if(note==null) {
            note = new Note();
            note.NOTE_NO = rs.getInt("NOTE_NO");
            note.TITLE = rs.getString("TITLE");
            note.NOTE_DATA = Utils.getImage(rs.getBlob("NOTE_DATA"));
            note.LAST_MOD_DT = rs.getString("LAST_MOD_DT");
            note.IS_DEL = rs.getBoolean("IS_DEL");
            note.BACKGROUND = rs.getInt("BACKGROUND");
        }
        return note;
    }

    /**
     * NOTE테이블 을 cursor로 리턴한다.
     * @return
     */
    public Cursor getNoteList()
    {
        return db.execCursor("SELECT NOTE_NO AS _id, * FROM NOTE WHERE IS_DEL=0 ORDER BY LAST_MOD_DT DESC"); // cursorAdapter를 사용하려면 _id컬럼이 있어야함.
    }

    /**
     * 검색하여 나온 NOTE테이블을 리턴한다.
     * @return
     */
    public Cursor titlegetNoteList(String title)
    {
        return db.execCursor("SELECT NOTE_NO AS _id, * FROM NOTE WHERE TITLE LIKE '%"+ title + "%' ORDER BY LAST_MOD_DT DESC"); // cursorAdapter를 사용하려면 _id컬럼이 있어야함.
    }

    /**
     * 검색하여 나온 제목의 갯수를 리턴한다.
     * @param title
     * @return
     */
    public Cursor titlegetCountNoteList(String title)
    {
        return db.execCursor("SELECT COUNT(NOTE_NO),NOTE_NO AS _id, * FROM NOTE WHERE TITLE LIKE '%"+ title + "%' ORDER BY LAST_MOD_DT DESC"); // cursorAdapter를 사용하려면 _id컬럼이 있어야함.
    }

    /**
     * 노트 SEED데이터 입력
     */
    public void insertSeedData() throws Exception {
        // 이미 디비에 데이터가 들어있으면 추가로 insert를 하지 않는다.
        /*if(db.is("SELECT * FROM NOTE LIMIT 1")==true)
            return;*/

        Bitmap bitmap = Bitmap.createBitmap(600,600,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        switch(Utils.getRandomNumber(1,10)%4)
        {
            case 0:
                canvas.drawColor(Color.WHITE);
                break;
            case 1:
                canvas.drawColor(Color.BLACK);
                break;
            case 2:
                canvas.drawColor(Color.BLUE);
                break;
            case 3:
                canvas.drawColor(Color.YELLOW);
                break;
            case 4:
                canvas.drawColor(Color.RED);
                break;
            default:
                canvas.drawColor(Color.GREEN);
        }


        Note note = new Note();
        note.TITLE = "'제목'"+Utils.getRandomNumber(1,100);
        note.NOTE_DATA = bitmap;
        note.LAST_MOD_DT = Utils.getYYYYMMDDHHMMSS();
        note.IS_DEL = false;
        note.BACKGROUND = 0;
        Thumbnail thumb = new Thumbnail(bitmap);
        note.thumbnail = thumb;
        note.alarm = null;

        saveNoteData(note);
    }

    /**
     * 삭제된 노트를 리턴한다.
     * @return
     * 삭제된 노트 목록 cursor
     */
    public Cursor getTrashList() {
        return db.execCursor("SELECT NOTE_NO AS _id, * FROM NOTE WHERE IS_DEL=1 ORDER BY LAST_MOD_DT DESC"); // cursorAdapter를 사용하려면 _id컬럼이 있어야함.
    }
}
