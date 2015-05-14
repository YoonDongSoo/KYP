package yu.kyp.common.database;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by DONGSOO on 2015-03-24.
 */
public class DataTable
{
    // 열
    final private HashMap<String, Integer> cols;
    // 열 갯수
    final private int colsCount;
    // 열 이름
    private String[] colsName;
    // 행
    //final private ArrayList<String[]> rows;
    // 행 데이터
    final private DBData[][] data;
    // 행 갯수
    final private int rowsCount;
    // 위치
    private int pos = -1;

    /**
     * @설명 생성자
     * @주의 클래스 DB를 통해 호출하세요.
     * @예제 DataTable dt = DB.execDataTable(ctx, sql);
     */
    public DataTable(Cursor c)
    {
        if (c != null)
        {
            // 열 정보 세팅
            this.cols = new HashMap<String, Integer>();
            int colCount = (this.colsCount = ((colsName = c.getColumnNames()).length));
            for (int i = 0 ; i < colCount ; i++)
            {
                this.cols.put(colsName[i], i);
            }

            // 행정보 세팅
            ArrayList<DBData[]> rows = new ArrayList<DBData[]>();
            while (c.moveToNext())
            {
                DBData[] row = new DBData[colCount];
                for (int i = 0 ; i < colCount ; i++)
                {
                    if(c.getType(i)==Cursor.FIELD_TYPE_BLOB)
                    {
                        row[i] = new DBData(c.getBlob(i));
                    }
                    else
                    {
                        row[i] = new DBData(c.getString(i));
                    }
                }
                rows.add(row);
            }
            rowsCount = rows.size();

            data = (DBData[][])rows.toArray(new DBData[rows.size()][]);
        }
        else
        {
            this.cols = new HashMap<String, Integer>();
            //this.rows = new ArrayList<String[]>();
            this.data = new DBData[0][0];
            this.colsCount = 0;
            this.rowsCount = 0;
        }
    }

    /**
     * @설명 열이름들을 반환합니다.
     */
    public String[] getColumnNames()
    {
        return colsName;
    }

    /**
     * @설명 열 갯수를 가져온다.
     */
    public int getColsCount()
    {
        return colsCount;
    }

    /**
     * @설명 행의 갯수를 가져온다.
     */
    public int getRowsCount()
    {
        return rowsCount;
    }

    /**
     * @설명 컬럼명을 가지고 컬럼인덱스를 가져온다.
     * @주의 존재하지 않는 컬럼의 경우 -1을 리턴한다.
     */
    public int getIdxCols(String name)
    {
        Object idx = this.cols.get(name);
        return idx != null ? (Integer)idx : -1;
    }

    /**
     * @설명 커서를 다음위치로 이동시킨다.
     */
    public boolean next()
    {
        return (++this.pos) < this.rowsCount;
    }

    /**
     * @설명 커서를 해당위치로 이동시킨다.
     */
    public boolean move(int rownum)
    {
        if (rownum > -1 && rownum < this.rowsCount)
        {
            this.pos = rownum;
            return true;
        }
        else
        {
            softError("행 범위 밖으로 커서 이동을 시도하였습니다.\n행갯수 : " + this.rowsCount + "\n이동시도 : "+rownum);
            return false;
        }
    }

    /**
     * @설명 커서를 처음위치로 이동시킨다.
     * @참고 next()처럼 boolean을 반환한다. 데이터가 없는경우를 대비해.
     */
    public boolean moveFirst()
    {
        return move(0);
    }

    /**
     * @설명 필드값을 String 형태로 가져온다.
     */
    public String getString(int colIdx)
    {
        try
        {
            return this.data[pos][colIdx].getString();
        }
        catch (Exception e)
        {
            return null; // 위에서 throw 됨으로 의미없음.
        }
    }

    /**
     * @설명 필드값을 String 형태로 가져온다.
     */
    public String getString(String colName)
    {
        try
        {
            return this.getString(this.getIdxCols(colName));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * @설명 필드값을 boolean 형태로 가져온다.
     */
    public boolean getBoolean(int colIdx) throws Exception
    {
        try
        {
            String val = this.data[pos][colIdx].getString();
            return val != null && val.equals("1");
        }
        catch (Exception e)
        {
            error("존재하지 않는 행이나 열을 선택하였습니다.", e);
            return false; // 위에서 throw 됨으로 의미없음.
        }
    }

    /**
     * @설명 필드값을 boolean 형태로 가져온다.
     */
    public boolean getBoolean(boolean defVal, int colIdx)
    {
        try
        {
            String val = this.data[pos][colIdx].getString();
            if (val != null)
            {
                if (val.equals("1"))
                {
                    return true;
                }
                else if (val.equals("0"))
                {
                    return false;
                }
            }
        }
        catch (Exception e) {}
        return defVal;
    }

    /**
     * @설명 필드값을 boolean 형태로 가져온다.
     */
    public boolean getBoolean(String colName) throws Exception
    {
        return this.getBoolean(this.getIdxCols(colName));
    }

    /**
     * @설명 필드값을 boolean 형태로 가져온다.
     */
    public boolean getBoolean(boolean defVal, String colName) throws Exception
    {
        return this.getBoolean(defVal, this.getIdxCols(colName));
    }

    /**
     * @설명 필드값을 int 형태로 가져온다.
     */
    public int getInt(int colIdx) throws Exception
    {
        return Integer.parseInt(this.data[pos][colIdx].getString());
    }

    /**
     * @설명 필드값을 int 형태로 가져온다.
     */
    public int getInt(int defVal, int colIdx) throws Exception
    {
        try
        {
            return Integer.parseInt(this.data[pos][colIdx].getString());
        }
        catch (Exception e)
        {
            return defVal;
        }
    }

    /**
     * @설명 필드값을 int 형태로 가져온다.
     */
    public int getInt(String colName) throws Exception
    {
        return this.getInt(this.getIdxCols(colName));
    }

    /**
     * @설명 필드값을 int 형태로 가져온다.
     */
    public int getInt(int defVal, String colName) throws Exception
    {
        try
        {
            return this.getInt(this.getIdxCols(colName));
        }
        catch (Exception e)
        {
            return defVal;
        }
    }

    /**
     * @설명 필드값을 int 형태로 가져온다.
     */
    public Integer getInteger(int columnIndex)
    {
        String intVal = this.data[pos][columnIndex].getString();
        if (intVal != null && !intVal.equals(""))
        {
            return new Integer(intVal);
        }
        return null;
    }

    /**
     * @설명 필드값을 int 형태로 가져온다.
     */
    public Integer getInteger(String colName) throws Exception
    {
        return this.getInteger(this.getIdxCols(colName));
    }


    /**
     * @설명 모든데이터를 ArrayList<String[]>형식으로 반환
     */
	/*
	public ArrayList<String[]> rowsToStringArrayList()
	{
		return rows;
	}*/

    /**
     * @설명 모든데이터를 String[][]형식으로 반환
     */
    public DBData[][] rowsToStringArrayTable()
    {
        return data;
    }

    /**
     * @설명 열에 해당하는 모든데이터를 String[]형식으로 반환
     */
    public String[] rowsToStringArray(final int colIdx)
    {
        if (rowsCount < 1) { return new String[0]; }

        String[] rv = new String[rowsCount];

        for (int i = 0 ; i < rowsCount ; i++)
        {
            rv[i] = this.data[i][colIdx].getString();
        }

        return rv;
    }
    /**
     * @설명 열에 해당하는 모든데이터를 String[]형식으로 반환
     */
    public String[] rowsToStringArray(String colName)
    {
        return this.rowsToStringArray(this.getIdxCols(colName));
    }

    /**
     * @설명 열에 해당하는 모든데이터를 int[]형식으로 반환
     */
    public int[] rowsToIntArray(final int colIdx)
    {
        if (rowsCount < 1) { return new int[0]; }

        int[] rv = new int[rowsCount];

        for (int i = 0 ; i < rowsCount ; i++)
        {
            rv[i] = Integer.parseInt(this.data[i][colIdx].getString());
        }

        return rv;
    }

    /**
     * @설명 열에 해당하는 모든데이터를 int[]형식으로 반환
     */
    public int[] rowsToIntArray(String colName)
    {
        return this.rowsToIntArray(this.getIdxCols(colName));
    }


    /**
     * @설명 에러출력
     * @주의 이녀석은 직접 사용하지 않는다.
     */
    private void errorBase(boolean isThrow, String msg, Exception e) throws Exception
    {
        /// 컬럼명 모와서 같이 보낸다.
        String text = msg;

        if (isThrow)
        {
            throw new Exception(text, e);
        }

    }
    /**
     * @설명 에러출력
     */
    private void error(String msg, Exception e) throws Exception
    {
        errorBase(true, msg, e);

    }
    /**
     * @설명 에러출력
     * @주의 isThrow는 심각한 경우에만사용한다 실제 Exception 을 던진다.
     */
    private void softError(String msg)
    {
        try
        {
            errorBase(false, msg, null);
        }
        catch (Exception e) {}
    }
    /**
     * @설명 로그표시
     */
    public void log(int limit)
    {
        Log.i("DataTable", "---------------------------------");
        Log.i("DataTable", "테이블의 로그를 기록합니다.");
        Log.i("DataTable", "---------------------------------");
        String line = "";
        String[] names = cols.keySet().toArray(new String[cols.size()]);
        for (String name : names)
        {
            line += "∬ " + name + " ";
        }
        Log.i("DataTable", line);
        Log.i("DataTable", "---------------------------------");
        for (int i = 0 ; i < data.length ; i++)
        {
            if (i >= limit) { break; }
            line = "";
            DBData row[] = data[i];
            for (String name : names)
            {
                line += "∬ " + row[cols.get(name)] + " ";
            }
            Log.i("DataTable", line);
        }
        Log.i("DataTable", "---------------------------------");
    }
    /**
     * @설명 로그표시
     */
    public void log()
    {
        log(99999);
    }

    public float getFloat(String colName) {
        return getFloat(this.getIdxCols(colName));
    }

    public float getFloat(int colIdx)
    {
        return Float.parseFloat(this.data[pos][colIdx].getString());
    }

    public byte[] getBlob(String colName) {
        return getBlog(this.getIdxCols(colName));
    }

    private byte[] getBlog(int colIdx) {
        return this.data[pos][colIdx].getBytes();
    }
}
