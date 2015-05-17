package yu.kyp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import yu.kyp.common.activity.ActivityBase;
import yu.kyp.common.database.DataTable;
import yu.kyp.common.database.DatabaseHelper;
import yu.kyp.image.NoteManager;


public class MemoListActivity extends ActivityBase {

    private static final String TAG = MemoListActivity.class.getSimpleName();
    private NoteManager noteManager = null;
    private Context context = null;
    private AdapterView.OnItemClickListener listenerListNote = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i = new Intent(context,MemoWriteActivity2.class);
            i.putExtra("NOTE_NO",(int)id);
            startActivity(i);
        }
    };
    private SimpleCursorAdapter adapterListNote = null;
    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch(which)
            {
                case DialogInterface.BUTTON_POSITIVE:
                    ContentValues values = new ContentValues();
                    values.put("IS_DEL","1");
                    int cnt = db.execUpdate("NOTE", values, "NOTE_NO=" + deleteId);
                    Log.i(TAG,"cnt:"+cnt);
                    bindNote();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    private long deleteId;
    private AdapterView.OnItemLongClickListener longClickListenerListNote = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            deleteId  = id;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("삭제하시겠습니까?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_list);
        context = this;
        noteManager = new NoteManager(this);
        /*// 노트 SEED데이터 입력
        try {
            noteManager.insertSeedData();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        // ListView OnItemClickLIstener 설정
        ListView listviewNote = (ListView) findViewById(R.id.listViewNote);
        listviewNote.setOnItemClickListener(listenerListNote);
        listviewNote.setOnItemLongClickListener(longClickListenerListNote);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // ListView에 노트 내용 뿌려주기.
        bindNote();
        Log.d(TAG, "onResume");
        Log.d(TAG, "Settings.getDefaultFactor():" + settings.getDefaultFactor());
        Log.i(TAG,"Setting.getFontType():"+settings.getFontType());
        Log.i(TAG,"Setting.getZoomFactor():"+settings.getZoomFactor());
        Log.i(TAG,"Setting.getBackgroundType():"+settings.getBackgroundType());
        Log.i(TAG,"Setting.getAlarmType():"+settings.getAlarmType());
        Log.i(TAG,"Setting.getListType():"+settings.getListType());
    }

    /**
     * ListView에 노트 내용 뿌려주기.
     */
    private void bindNote() {
        Cursor c = noteManager.getNoteList();
        if(adapterListNote==null) {
            adapterListNote = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, c, new String[]{"TITLE", "LAST_MOD_DT"}, new int[]{android.R.id.text1, android.R.id.text2});
            ListView listviewNote = (ListView) findViewById(R.id.listViewNote);
            listviewNote.setAdapter(adapterListNote);
        }
        else
        {
            adapterListNote.changeCursor(c);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_memo_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void buttonNewMemo_OnClick(View v)
    {
        Intent i = new Intent(context,MemoWriteActivity2.class);
        startActivity(i);
    }

    public void buttonTrash_OnClick(View v)
    {
        startActivity(new Intent(this,TrashActivity.class));
    }
}
