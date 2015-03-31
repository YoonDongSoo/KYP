package yu.kyp.common.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by DONGSOO on 2015-03-24.
 */
public class DB // implements Closeable
{
    /// - 정의 ---------------------------------------------------------------------------------------------------------------------------------------------------
    final private static String DB_NAME = "KYP";
    private static final String TAG = "DB";
    // DB 버전
    //final private static int DATABASE_VERSION = 1;
    // DB
    private static SQLiteDatabase db;



    /// - 생성자 ---------------------------------------------------------------------------------------------------------------------------------------------------
    public DB(Context ctx)
    {
        if (db == null || (!db.isOpen()))
        {
            db = DatabaseHelper.getInstance(ctx, DB_NAME).getWritableDatabase();
        }
    }



    /// - 쿼리 ---------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * @설명 SQLiteDatabase 객체를 반환한다.
     * @참조 벌크 처리같은 특정 대량 처리에서 속도를 올릴만한 기능을 쓰기위해 사용한다.
     */
    public SQLiteDatabase getSQLiteDatabase()
    {
        return db;
    }
    /**
     * @설명 기존의 테이블 정보를 clear한뒤 붙여넣기한다.
     * @인자 "테이블=복사할테이블"...
     * @예제 copyData("CUST=CUST_TMP");
     */
    public void copyData(String... table)
    {
        String[] name;

        for (int i = 0 ; i < table.length ; i++)
        {
            name = table[i].split("=");
            this.clearTable(name[0]);
            db.execSQL("INSERT INTO `"+name[0]+"` SELECT * FROM `"+name[1]+"`;");
        }
    }

    /**
     * @설명 기존의 테이블 정보를 clear한뒤 붙여넣기한다.
     * @참고 테이블이 존재하지 않을 경우 새로 만든다.
     * @인자 "테이블=복사할테이블"...
     * @예제 copyData("CUST=CUST_TMP");
     */
    public void copyDataWithCreate(String... table)
    {
        String[] name;

        for (int i = 0 ; i < table.length ; i++)
        {
            name = table[i].split("=");
            if (this.isTable(name[0]))
            {
                this.clearTable(name[0]);
                db.execSQL("INSERT INTO `"+name[0]+"` SELECT * FROM `"+name[1]+"`;");
            }
            else
            {
                this.copyTable(name);
            }

        }
    }

    /**
     * @설명 테이블을 삭제한 후 복제한다.
     * @인자 "테이블=복사할테이블"...
     * @예제 copyData("CUST=CUST_TMP");
     */
    public void copyTable(String... table)
    {
        String[] name;

        for (int i = 0 ; i < table.length ; i++)
        {
            name = table[i].split("=");
            this.dropTable(name[0]);
            db.execSQL("CREATE TABLE `"+name[0]+"` AS SELECT * FROM `"+name[1]+"`;");
        }
    }

    /**
     * @설명 테이블을 삭제한 후 복제한다. 데이터는 가져오지 않는다.
     * @인자 "테이블=복사할테이블"...
     * @예제 copyData("CUST=CUST_TMP");
     */
    public void copyTableOnlyDefine(String... table)
    {
        String[] name;

        for (int i = 0 ; i < table.length ; i++)
        {
            name = table[i].split("=");
            this.dropTable(name[0]);
            db.execSQL("CREATE TABLE `"+name[0]+"` AS SELECT * FROM `"+name[1]+"` LIMIT 0;");
        }
    }

    /**
     * @설명 sql문의 결과 row의 존재여부.
     */
    public boolean is(String sql)
    {
        boolean rv = false;
        Cursor c = db.rawQuery(sql, null);
        rv = c.getCount() > 0;
        c.close();

        return rv;
    }

    /**
     * @설명 sql문의 결과 row의 존재여부.
     * @주의1 where 절이 없을경우 null을 사용한다!!
     * @주의2 limit는 1로 잡혀있다 limit를 따로 쓰지말자.
     * @주의3 order by group by같은걸 쓰고싶을때에는 db.is(String sql)을 쓰자!!
     */
    public boolean is(String tb, String where)
    {
        return this.is("SELECT * FROM `"+tb+"` "+(where != null ? "WHERE " + where : "")+" LIMIT 1");
    }


    /**
     * @설명 쿼리를 실행하고 커서를 반환
     */
    public Cursor execCursor(String sql)
    {
        return db.rawQuery(sql, null);
    }

    /**
     * @설명 쿼리를 실행하고 커서출력을 로그로 반환
     */
    public void execLog(String sql)
    {
        execDataTable(sql).log();
    }

    /**
     * @설명 쿼리를 실행하고 커서출력을 로그로 반환
     */
    public static void execLog(Context ctx, String sql)
    {
        execDataTable(ctx, sql).log();
    }

    /**
     * @설명 인설트를 실행합니다.
     * @반환 삽입된 row ID
     */
    public long execInsert(String table, ContentValues values)
    {
        return db.insert(table, null, values);
    }
    /**
     * @설명 인설트를 실행합니다.
     * @반환 삽입된 row ID
     */
    public static long execInsert(Context ctx, String table, ContentValues values)
    {
        DB db = new DB(ctx);
        long rv = db.execInsert(table, values);
        //db.close();
        return rv;
    }

    /**
     * @설명 삭제합니다.
     * @반환 삭제된 갯수
     */
    public int execDelete(String table, String whereClause)
    {
        return db.delete(table, whereClause, null);
    }
    /**
     * @설명 삭제합니다.
     * @반환 삭제된 갯수
     */
    public static int execDelete(Context ctx, String table, String whereClause)
    {
        DB db = new DB(ctx);
        return db.execDelete(table, whereClause);
    }

    /**
     * @설명 업데이트를 실행합니다.
     * @반환 영향받은 행의 갯수.
     */
    public int execUpdate(String table, ContentValues values, String whereClause)
    {
        return db.update(table, values, whereClause, null);
    }
    /**
     * @설명 업데이트를 실행합니다.
     * @반환 영향받은 행의 갯수.
     */
    public static int execUpdate(Context ctx, String table, ContentValues values, String whereClause)
    {
        DB db = new DB(ctx);
        int rv = db.execUpdate(table, values, whereClause);
        //db.close();
        return rv;
    }

    /**
     * @설명 쿼리를 실행하고 DataTable을 반환
     */
    public DataTable execDataTable(String sql)
    {
        Cursor c = db.rawQuery(sql, null);
        DataTable rv = new DataTable(c);
        c.close();
        return rv;
    }
    /**
     * @설명 쿼리를 실행하고 DataTable을 반환
     */
    public static DataTable execDataTable(Context ctx, String sql)
    {
        DB db = new DB(ctx);
        DataTable rv = db.execDataTable(sql);
        //db.close();
        return rv;
    }


    /// - 테이블 ---------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * @설명 테이블을 만든다.
     * @인자
     * String table : 테이블 이름<br/>
     * HashMap<String, String> fields : <필드명, 자료형 + 속성>
     * @String_fields_단축_대소문_관계없음
     * PK : PRIMARY KEY<br/>
     * AUTO : AUTOINCREMENT<br/>
     * BOOL : BOOLEAN<br/>
     * VARCHAR : NVARCHAR<br/>
     * CHAR : CHARACTER
     * @반환
     * [true:정상] [false:이미테이블이 있다던가 만들기에 실패한경우]
     */
    public boolean createTable(String table, HashMap<String, String> field)
    {
        boolean rv = false;
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS `").append(table).append("` ( _id INTEGER PRIMARY KEY AUTOINCREMENT");

        Object keys[] = field.keySet().toArray().clone();
        for (int i = 0 ; i < keys.length ; i++)
        {
            //if (i != 0) { sb.append(','); }
            sb.append(',');
            sb.append(((String)keys[i]));
            sb.append(' ');
            sb.append(field.get(keys[i]).toUpperCase().replace("PK", "PRIMARY KEY").replace("AUTO", "AUTOINCREMENT").replace("BOOL", "CHARACTER(1)").replace("CHAR(", "CHARACTER(").replace("VARCHARACTER(", "VARCHAR("));
        }

        sb.append(");");

        try
        {
            db.execSQL(sb.toString());
            rv = true;
        } catch (Exception e) {}

        return rv;
    }

    /**
     * @설명 테이블을 만든다.
     * @인자
     * String table : 테이블 이름<br/>
     * HashMap<String, String> fields : <필드명, 자료형 + 속성>
     * @String_fields_단축_대소문_관계없음
     * PK : PRIMARY KEY<br/>
     * AUTO : AUTOINCREMENT<br/>
     * BOOL : BOOLEAN<br/>
     * VARCHAR : NVARCHAR<br/>
     * CHAR : CHARACTER
     * @반환
     * [true:정상] [false:이미테이블이 있다던가 만들기에 실패한경우]
     */
    public boolean createTableWithoutDefaultPK(String table, HashMap<String, String> field)
    {
        boolean rv = false;
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS `").append(table).append("` (");


        Object keys[] = field.keySet().toArray().clone();
        for (int i = 0 ; i < keys.length ; i++)
        {
            if (i != 0) { sb.append(','); }
            sb.append(((String)keys[i]));
            sb.append(' ');
            sb.append(field.get(keys[i]).toUpperCase().replace("PK", "PRIMARY KEY").replace("AUTO", "AUTOINCREMENT").replace("BOOL", "CHARACTER(1)").replace("CHAR(", "CHARACTER(").replace("VARCHARACTER(", "VARCHAR("));
        }

        sb.append(");");

        try
        {
            db.execSQL(sb.toString());
            rv = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, sb.toString());
        }

        return rv;
    }

    /**
     * _id PK로 사용하지 않는 테이블을 만든다.
     * @param table
     * @param listNameValue
     * 컬럼,
     * @return
     */
    public boolean createTableWithoutDefaultPK(String table,
                                               List<BasicNameValuePair> listNameValue) {
        boolean rv = false;
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS `").append(table).append("` (");

        boolean isFirst = true;
        for(BasicNameValuePair item : listNameValue)
        {
            if (isFirst==false) { sb.append(','); }
            String name = item.getName();
            String value = item.getValue();
            sb.append(name);
            sb.append(' ');
            sb.append(value.toUpperCase().replace("PK", "PRIMARY KEY").replace("AUTO", "AUTOINCREMENT").replace("BOOL", "CHARACTER(1)").replace("CHAR(", "CHARACTER(").replace("VARCHARACTER(", "VARCHAR("));
            isFirst = false;
        }
		/*Object keys[] = field.keySet().toArray().clone();
		for (int i = 0 ; i < keys.length ; i++)
		{
			if (i != 0) { sb.append(','); }
			sb.append(((String)keys[i]));
			sb.append(' ');
			sb.append(field.get(keys[i]).toUpperCase().replace("PK", "PRIMARY KEY").replace("AUTO", "AUTOINCREMENT").replace("BOOL", "CHARACTER(1)").replace("CHAR(", "CHARACTER(").replace("VARCHARACTER(", "VARCHAR("));
		}*/

        sb.append(");");

        try
        {
            db.execSQL(sb.toString());
            rv = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG,sb.toString());
        }

        return rv;

    }

    /**
     * @설명 강제 테이블을 만든다.
     * @주의 기존의 동일한 이름의 테이블이 있을경우 지우고 만든다.
     * @인자
     * String table : 테이블 이름<br/>
     * HashMap<String, String> fields : <필드명, 자료형 + 속성>
     * @fields단축
     * int : INTEGER<br/>
     * pk : PRIMARY KEY<br/>
     * auto : AUTOINCREMENT
     */
    public void createTableForce(String table, HashMap<String, String> field)
    {
        dropTable(table);
        createTable(table, field);
    }
    /**
     * @설명 인덱스 생성
     * @예제
     * .createIndex("CUST_SCHE", "SCHE_NO");<br/>
     * .createIndex("CUST_SCHE", "SCHE_DT DESC,SCHE_TM,SCHE_TYPE_CD,SCHE_NO");<br/>
     * .createIndex("CUST_SCHE", "CUST_NO, SCHE_DT DESC,SCHE_TM,SCHE_TYPE_CD,SCHE_NO");
     */
    public boolean createIndex(String table, String colName)
    {
        String idxName = table + "_IDX_" + colName.toUpperCase().replace("ASC", "").replace("DESC", "").replace(" ", "").replace(',', '_');
        try
        {
            db.execSQL("CREATE INDEX IF NOT EXISTS `"+idxName+"` ON `"+table+"`("+colName+");");
            return true;
        }
        catch (SQLException e)
        {
            //Log.v("test", e.getMessage());
            return false;
        }
    }

    /**
     * @설명 유니크 키 생성 사용법은 createIndex 동일
     */
    public boolean createIndexUni(String table, String colName)
    {
        String idxName = table + "_IDX_" + colName.toUpperCase().replace("ASC", "").replace("DESC", "").replace(" ", "").replace(',', '_');
        try
        {
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `"+idxName+"` ON `"+table+"`("+colName+");");
            return true;
        }
        catch (SQLException e)
        {
            //Log.v("test", e.getMessage());
            return false;
        }
    }
    /**
     * @설명 테이블 제거
     */
    public void dropTable(String... table)
    {
        for (int i = 0 ; i < table.length ; i++)
        {
            db.execSQL("DROP TABLE IF EXISTS `"+table[i]+"`;");
        }
    }
    /**
     * @설명 테이블 내용 비우기
     */
    public void clearTable(String table)
    {
        if(isTable(table))
        {
            db.execSQL("DELETE FROM `"+table+"`;");
        }
    }
    /**
     * @설명 테이블 존재 여부
     */
    public boolean isTable(String table)
    {
        boolean rv;
        Cursor c = db.rawQuery("SELECT * FROM sqlite_master WHERE name='"+table+"'", null);
        rv = c.getCount() > 0;
        c.close();
        return rv;
    }
    /**
     * @설명 테이블 존재 여부
     */
    public static boolean isTable(Context ctx, String table)
    {
        DB db = new DB(ctx);
        boolean rv = db.isTable(table);
        //db.close();
        return rv;
    }

    /**
     * @설명 JSONArray 객체를 db에 맞게 삽입합니다.
     * @반환 성공실패 여부.
     */
    public void jsonCtrl(String table, JSONArray jsa, JsonCtrl jsonCtrl) throws Exception
    {
        // 이상
        if (jsa == null || jsonCtrl == null) { throw new Exception("JSONArray jsa, JsonCtrl jsonCtrl 인자가 null로 넘어왔습니다."); }

        if (jsa.length() == 0)
        {
            return;
        }

        // 에러
        Exception e = null;
        // db 매핑
        jsonCtrl.db = db;
        jsonCtrl.table = table;
        // 컬럼 매치여부를 확인 후 계산 후 일치하는 경우에만 넣음.
        String[] tmpColsName = getTableCols(table);
        JSONObject tmpJson = jsa.getJSONObject(0);
        ArrayList<String> tmpCtrlCols = new ArrayList<String>();
        for (String col : tmpColsName)
        {
            if (tmpJson.isNull(col)) { tmpCtrlCols.add(col); }
        }
        jsonCtrl.colsName = (String[])tmpCtrlCols.toArray(new String[tmpCtrlCols.size()]);
        // json 갯수
        final int jsonLen = jsonCtrl.idxMax = jsa.length();

        db.beginTransaction();
        try
        {
            for (int i = 0 ; i < jsonLen ; i++)
            {
                jsonCtrl.idx = i;
                jsonCtrl.json = jsa.getJSONObject(i);
                jsonCtrl.ctrl();
            }

            db.setTransactionSuccessful();
        }
        catch (Exception eSql)
        {
            e = eSql;
        }
        finally
        {
            db.endTransaction();
        }

        if (e != null)
        {
            throw e;
        }

        return;
    }


    public static abstract class JsonCtrl
    {
        final public static int CTRL_INSERT = 0;
        final public static int CTRL_UPDATE = 0;
        final public static int CTRL_DELETE = 0;
        private SQLiteDatabase db = null;
        private String table = null;
        private String[] colsName = null;
        protected int idxMax = 0;
        protected int idx = 0;
        protected JSONObject json = null;
        public abstract int ctrl();
        protected void insert() throws JSONException
        {
            ContentValues cv = new ContentValues();
            for (String col : colsName)
            {
                cv.put(col, json.getString(col));
            }
            db.insert(table, null, cv);
        }
        protected void update(String where) throws JSONException
        {
            ContentValues cv = new ContentValues();
            for (String col : colsName)
            {
                cv.put(col, json.getString(col));
            }
            db.update(table, cv, where, null);
        }
        protected void delete(String where)
        {
            db.delete(table, where, null);
        }
    }

    /**
     * @설명 JSONArray 객체를 db에 맞게 삽입합니다.
     * @반환 성공실패 여부.
     */
    public boolean insertJson(String table, JSONArray jss) throws Exception
    {
        // 컬럼명을 알기위해서.
        Cursor cur = db.rawQuery("SELECT * FROM `"+table+"` LIMIT 0", null);

        // 인설트를 위한.
        //InsertHelper ins = new InsertHelper(db, table);

        // 선언
        boolean rv = true;
        JSONObject js; // json 오브젝트
        int jssLen = jss.length(); // json array의 길이
        String[] cols = cur.getColumnNames(); cur.close(); // 컬럼명
        ContentValues cv = new ContentValues();

        db.beginTransaction();
        try
        {
            for (int i = 0 ; i < jssLen ; i++)
            {
                js = jss.getJSONObject(i);

                for (String colName : cols) { if (!js.isNull(colName)) { cv.put(colName, js.getString(colName)); }}

                db.insert(table, null, cv);
            }
            db.setTransactionSuccessful();
        }
        catch (Exception e) { rv = false; } finally
        {
            db.endTransaction();
        }

        return rv;
    }

    /**
     * @설명 테이블 컬럼을 가져온다.
     * @주의 _id도 포함된다.
     */
    public String[] getTableCols(String table)
    {
        Cursor cur = this.execCursor("SELECT * FROM `"+table+"` LIMIT 0");
        String[] colsName = cur.getColumnNames();
        cur.close();

        return colsName;
    }






    /// - 소멸 ---------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * @설명 현재 아무런 의미가 없다.
     */
	/*
	public void close()
	{

		if (db != null)
		{
			try
			{
				db.close();
				db = null;
			}
			catch (Exception e)
			{
				///오류 : 닫히는중 오류가 발생하였습니다.
			}
		}
	}
	*/

	/*
	 * @설명 XML 형태의 문서를 모두 삽입합니다.

	@Deprecated
	public int insertXMLOld(String table, NodeList xmlNodeList, String matchTableAndXml)
	{
		// 준비
		int rv = -1;
		String match[] = matchTableAndXml.replaceAll("\\s", "").split(",");
		HashMap<String, Integer> col = new HashMap<String, Integer>();
		String keys[] = new String[match.length];

		String tmp;
		InsertHelper ins = new InsertHelper(db, table);
		for (int i = 0, sub ; i < match.length ; i++)
		{
			sub = (tmp = match[i]).indexOf('=');
			keys[i] = tmp.substring(sub+1);
			col.put(keys[i], ins.getColumnIndex(tmp.substring(0, sub)));
		}

		db.beginTransaction();
		try
		{
			int len = xmlNodeList.getLength();
			int keyLen = keys.length;
			Element el;
			for (int i = 0 ; i < len ; i++)
			{
				el = (Element)xmlNodeList.item(i);

				ins.prepareForInsert();
				for (int j = 0 ; j < keyLen ; j++)
				{
					ins.bind(col.get(keys[j]), el.getElementsByTagName(keys[j]).item(0).getTextContent());
				}
				ins.execute();
			}
			db.setTransactionSuccessful();
			rv = len;
		}
		finally
		{
			db.endTransaction();
		}

		return rv;
	}


	 * @설명 JSONArray 객체를 db에 맞게 삽입합니다.
	 * @반환 성공실패 여부.

	public boolean insertJson(String table, JSONArray jsa)
	{
		if (jsa == null || (!isTable(table))) { return false; }

		// 컬럼명을 알기위해서.
		Cursor cur = this.execCursor("SELECT * FROM `"+table+"` LIMIT 0");
		String[] colsName = cur.getColumnNames();
		cur.close();
		String paramName = "";
		String paramValue = "";
		for (int i = 0 ; i < colsName.length ; i++)
		{
			if (i != 0) { paramName+=','; paramValue+=','; }
			paramName += colsName[i];
			paramValue += '?';
		}

		try
		{
			SQLiteStatement stat = db.compileStatement("INERT INTO `"+table+"` ("+paramName+") VALUES ("+paramValue+")");

			for (int i = 0 ; i < jsa.length() ; i++)
			{
				JSONObject js = jsa.getJSONObject(i);
				for (int j = 0 ; j < colsName.length ; j++)
				{
					if (js.get(colsName[j]) != null)
					{
						stat.bindString(j+1, js.getString(colsName[j]));
					}
					else
					{
						stat.bindNull(j+1);
					}
				}
				stat.executeInsert();
			}
	        stat.close();
		}
		catch (Exception e)
		{
			// 오류사항을 전달해야함!!!
			return false;
		}
		return true;
	}
	*/
}
