package yu.kyp.image;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
        // 3.Stroke, PointData를 DB에 입력한다.
        insertStroke(note);

    }

    /**
     * 획정보를 디비에 insert한다.
     * Stroke.listPointData로 디비에 입력한다.
     * @param note
     */
    private void insertStroke(Note note) {
        for(Stroke s : note.listStroke)
        {

            ContentValues strokeValues = new ContentValues();
            strokeValues.put("NOTE_NO",note.NOTE_NO);
            strokeValues.put("COLOR",s.COLOR);
            strokeValues.put("THICKNESS",s.THICKNESS);
            long strokeNoCurrent = db.execInsert("STROKE",strokeValues);
            insertPointData(s, (int) strokeNoCurrent);
        }
    }

    /**
     * 좌표정보를 입력한다.
     * @param s
     * 획 정보
     * @param strokeNo
     * 획 고유번호
     */
    private void insertPointData(Stroke s, int strokeNo) {
        for(PointData p : s.listPointData)
        {
            ContentValues pointValues = new ContentValues();
            pointValues.put("STROKE_NO",strokeNo);
            pointValues.put("X",p.X);
            pointValues.put("Y",p.Y);
            db.execInsert("POINT_DATA", pointValues);
        }
    }

    /**
     * 기존 note정보가 존재한다면 update, 없다면 insert를 한다.
     * @param note
     */
    private void insertOrUpdateNote(Note note) {
        if(note.NOTE_NO!=null)
        {
            String whereClause = String.format("NOTE_NO=%d",note.NOTE_NO);
            if(db.is("NOTE",whereClause)==true)
            {
                ContentValues values = new ContentValues();
                values.put("TITLE",note.TITLE);
                values.put("CONTENT",note.CONTENT);
                values.put("LAST_MOD_DT", Utils.getYYYYMMDDHHMMSS());
                db.execUpdate("NOTE",values,whereClause);

                //==========================================================
                // 2.기존 note정보가 존재한다면 stroke정보와 PointData정보는 삭제한다.
                removeStroke(note.NOTE_NO);
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
            ContentValues values = new ContentValues();
            values.put("TITLE",note.TITLE);
            values.put("CONTENT",note.CONTENT);
            values.put("LAST_MOD_DT", Utils.getYYYYMMDDHHMMSS());
            note.NOTE_NO = (int)db.execInsert("NOTE",values);

        }
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
        // **좌표->획->노트 순서로 삭제해야 한다.**

        // 1. 좌표정보 삭제
        removePointData(noteNo);

        // 2. 획정보 삭제
        removeStroke(noteNo);

        // 3. 노트 정보 삭제
        String table = "NOTE";
        String whereClause = "NOTE_NO"+noteNo;
        int removed = db.execDelete(table,whereClause);
        Log.v(TAG,"삭제된 노트record수:"+removed);
        return removed;
    }

    /**
     * 획정보 삭제
     * 좌표정보도 삭제한다.
     * @param noteNo
     */
    private int removeStroke(int noteNo) {
        // 1. 좌표정보 삭제
        removePointData(noteNo);
        String table = "STROKE A JOIN NOTE B ON B.NOTE_NO = A.NOTE_NO";
        String whereClause = "A.NOTE_NO"+noteNo;
        int removed = db.execDelete(table,whereClause);
        Log.v(TAG,"삭제된 획record수:"+removed);
        return removed;
    }

    /**
     * 좌표정보 삭제
     * @param noteNo
     */
    private int removePointData(int noteNo) {
        String table = "POINT_DATA A JOIN STROKE B ON B.STROKE_NO = A.STROKE_NO JOIN NOTE C ON C.NOTE_NO = B.NOTE_NO";
        String whereClause = "C.NOTE_NO="+noteNo;
        int removed = db.execDelete(table, whereClause);
        Log.v(TAG,"삭제된 좌표record수:"+removed);
        return removed;
    }

    /**
     * 해당 번호의 노트 불러오기
     * @param noteNo
     * @return
     */
    public Note getNote(int noteNo) throws Exception {
        String sql = String.format(
                "SELECT A.NOTE_NO" +
                        ",A.TITLE" +
                        ",A.CONTENT" +
                        ",A.LAST_MOD_DT" +
                        ",B.STROKE_NO" +
                        ",B.COLOR" +
                        ",B.THICKNESS" +
                        ",C.POINT_NO" +
                        ",C.X" +
                        ",C.Y " +
                "FROM NOTE A " +
                "LEFT JOIN STROKE B " +
                "   ON B.NOTE_NO = A.NOTE_NO " +
                "LEFT JOIN POINT_DATA C " +
                "   ON C.STROKE_NO = B.STROKE_NO " +
                "WHERE A.NOTE_NO=%d",noteNo);
        DataTable rs = db.execDataTable(sql);
        Note note = null;
        Stroke stroke = null;
        PointData pointData = null;
        while(rs.next())
        {
            //=======================================
            // 1.노트 객체 생성 또는 가져오기
            note = getNoteInstance(rs, note);

            //=======================================
            // 2.스트로크 객체 생성 또는 가져오기 -> note.listStroke에 추가
            // STROKE_NO 가 달라지면 객체를 새로 생성한다.
            stroke = getStrokeInstance(rs, note, stroke);

            //=======================================
            // 3.좌표 객체 생성 -> stroke.listPointData에 추가
            if(stroke!=null && rs.getInt(0,"POINT_NO")!=0)
            {
                pointData = new PointData();
                pointData.POINT_NO = rs.getInt("POINT_NO");
                pointData.X = rs.getFloat("X");
                pointData.Y = rs.getFloat("Y");
                stroke.listPointData.add(pointData);
            }
        }//end while(rs.next())


        return note;
    }

    /**
     * 스트로크 객체 생성 또는 가져오기 -> note.listStroke에 추가
     * STROKE_NO 가 달라지면 객체를 새로 생성한다.
     * @param rs
     * @param note
     * @param stroke
     * @return
     * @throws Exception
     */
    private Stroke getStrokeInstance(DataTable rs, Note note, Stroke stroke) throws Exception {
        int strokeNoCurrent = rs.getInt(0, "STROKE_NO");
        if(stroke == null || stroke.STROKE_NO != strokeNoCurrent ) {
            if (rs.getInt(0, "STROKE_NO") != 0)
            {
                stroke = new Stroke();
                stroke.STROKE_NO = rs.getInt("STROKE_NO");
                stroke.COLOR = rs.getInt("COLOR");
                stroke.THICKNESS = rs.getInt("THICKNESS");
                stroke.COLOR = rs.getInt("COLOR");
                note.listStroke.add(stroke);
            }
        }
        return stroke;
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
            note.CONTENT = rs.getString("CONTENT");
            note.LAST_MOD_DT = rs.getString("LAST_MOD_DT");
        }
        return note;
    }

    /**
     * NOTE테이블 을 cursor로 리턴한다.
     * @return
     */
    public Cursor getNoteList()
    {
        return db.execCursor("SELECT NOTE_NO AS _id, * FROM NOTE"); // cursorAdapter를 사용하려면 _id컬럼이 있어야함.
    }

    /**
     * 노트 SEED데이터 입력
     */
    public void insertSeedData() throws Exception {
        // 이미 디비에 데이터가 들어있으면 추가로 insert를 하지 않는다.
        /*if(db.is("SELECT * FROM NOTE LIMIT 1")==true)
            return;*/


        Note note = new Note();
        note.TITLE = "'제목'"+Utils.getRandomNumber(1,100);
        note.CONTENT = "'메모 내용'"+Utils.getRandomNumber(1,100);
        note.LAST_MOD_DT = Utils.getYYYYMMDDHHMMSS();
        for (int i = 0; i < 5; i++) {
            Stroke stroke = new Stroke();
            stroke.COLOR = Utils.getRandomNumber(1,1000);
            stroke.THICKNESS = Utils.getRandomNumber(1,100);
            for (int j = 0; j < 10; j++) {
                PointData pointData = new PointData();
                pointData.X = Utils.getRandomNumber(1,100);
                pointData.Y = Utils.getRandomNumber(1,100);
                stroke.listPointData.add(pointData);
            }
            note.listStroke.add(stroke);
        }


        saveNoteData(note);
    }
}
