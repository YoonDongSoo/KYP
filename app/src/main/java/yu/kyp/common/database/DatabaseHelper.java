package yu.kyp.common.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DONGSOO on 2015-03-24.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static DatabaseHelper helper;
    public static final String[] SESSION_COLS = new String[]
            {
                    "id",
                    "description"
            };
    private Context ctx = null;


    public static synchronized DatabaseHelper getInstance(Context context, String DB_NAME)
    {
        if(helper == null)
        {
            helper = new DatabaseHelper(context, DB_NAME);
        }

        return helper;
    }

    public DatabaseHelper(Context context, String DB_NAME)
    {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        String createSessionTable =
                "CREATE TABLE `session` " +
                        "(" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "`description` VARCHAR" +
                        ") ";
        sqLiteDatabase.execSQL(createSessionTable);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL("drop table session");
        onCreate(sqLiteDatabase);
    }

    public void createSession(String desc)
    {
        final SQLiteDatabase writableDatabase = getWritableDatabase();
        final ContentValues contentValues = new ContentValues();

        contentValues.put("description", desc);

        writableDatabase.insertOrThrow("session", null, contentValues);
    }

    public int countSessions()
    {
        Cursor cursor = getReadableDatabase().rawQuery("select count(*) from session", null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public void updateSession(Integer id, String desc)
    {
        final SQLiteDatabase writableDatabase = getWritableDatabase();
        try
        {
            final ContentValues contentValues = new ContentValues();

            contentValues.put("description", desc);

            writableDatabase.update("session", contentValues, "id = ?", new String[]{id.toString()});
        }
        finally
        {
        }
    }

    public List<Session> loadAllSessions()
    {
        final SQLiteDatabase readableDatabase = getReadableDatabase();
        List<Session> sessions;
        final Cursor sessionCursor = readableDatabase.query("session", SESSION_COLS, null, null, null, null, "id", null);

        try
        {
            sessions = new ArrayList<Session>();

            while (sessionCursor.moveToNext())
            {
                final Session session = sessionFromCursor(sessionCursor);

                sessions.add(session);
            }
        }
        finally
        {
            sessionCursor.close();
        }

        return sessions;
    }

    public static class Session
    {
        private Integer id;
        private String description;

        public Session(Integer id, String description)
        {
            this.description = description;
            this.id = id;
        }

        public String getDescription()
        {
            return description;
        }

        public Integer getId()
        {
            return id;
        }
    }

    public Session loadSession(Integer id)
    {
        final SQLiteDatabase readableDatabase = getReadableDatabase();

        final Session session;
        final Cursor cursor = readableDatabase.query("session",
                SESSION_COLS,
                "id = ?",
                new String[]{id.toString()},
                null,
                null,
                null,
                null
        );
        try
        {


            cursor.moveToNext();

            session = sessionFromCursor(cursor);
        }
        finally
        {
            cursor.close();
        }

        return session;
    }

    private Session sessionFromCursor(Cursor cursor)
    {
        final Session session = new Session(cursor.getInt(0), cursor.getString(1));
        return session;
    }
}