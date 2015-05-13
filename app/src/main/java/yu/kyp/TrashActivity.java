package yu.kyp;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import yu.kyp.common.activity.ActivityBase;
import yu.kyp.image.NoteManager;


public class TrashActivity extends ActivityBase {

    private NoteManager noteManager = null;
    private SimpleCursorAdapter adapterListNote = null;
    private AdapterView.OnItemClickListener listenerItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CheckedTextView tv = (CheckedTextView)view;
            tv.setChecked(!tv.isChecked());
        }
    };
    private ListView listviewNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);
        noteManager = new NoteManager(this);
        listviewNote = (ListView) findViewById(R.id.listViewNote);

        listviewNote.setOnItemClickListener(listenerItemClick);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trash, menu);
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

    @Override
    protected void onResume() {
        super.onResume();
        bindNote();
    }

    /**
     * ListView에 삭제된 노트 쀼려주기
     */
    private void bindNote() {
        Cursor c = noteManager.getTrashList();
        if(adapterListNote==null)
        {
            adapterListNote = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_checked, c,new String[]{"TITLE"}, new int[]{android.R.id.text1});
            listviewNote.setAdapter(adapterListNote);
        }
        else
        {
            adapterListNote.changeCursor(c);
        }

    }

    /**
     * 뒤로 가기
     * @param v
     */
    public void buttonTrash_OnClick(View v)
    {
        finish();
    }

    public void buttonDelete_OnClick(View v)
    {
        // 선택된 id 넘어오는지 확인 필요.
        long id = listviewNote.getSelectedItemId();
        if(id>0) {
            db.execDelete("NOTE", "NOTE_NO=" + id);
            bindNote();
        }
    }
}
