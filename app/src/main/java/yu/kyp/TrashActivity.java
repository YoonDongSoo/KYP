package yu.kyp;


import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListView;

import java.util.ArrayList;

import yu.kyp.common.activity.ActivityBase;
import yu.kyp.image.NoteManager;

public class TrashActivity extends ActivityBase {
    CheckedTextView tv;
    ArrayList<Long> check = new ArrayList<>();
    CheckBox alldelete;
    boolean allchecked = false;
    private NoteManager noteManager = null;
    private CustomCursorAdapter adapterListNote = null;
    private AdapterView.OnItemClickListener listenerItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.i("!!!!", "onitemclickid " + id);
            Log.i("!!!!", "onitemclick position " + position);
            CheckBox ch = (CheckBox) view;
            ch.setChecked(!ch.isChecked());


            if(ch.isChecked()) {
                check.add(id);
            }
            else
                check.remove(id);
        }
    };
    private ListView listviewNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);
        noteManager = new NoteManager(this);
        listviewNote = (ListView) findViewById(R.id.listViewNote);
        listviewNote.setAdapter(adapterListNote);



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
        if (adapterListNote == null) {
           adapterListNote = new CustomCursorAdapter(this,c);
            listviewNote.setAdapter(adapterListNote);

//           adapterListNote.notifyDataSetChanged();

        } else {
            adapterListNote.changeCursor(c);
        }

    }

    /**
     * 뒤로 가기
     *
     * @param v
     */
    public void buttonBack_OnClick(View v) {
        finish();
    }

    public void buttonDelete_OnClick(View v) {

        // 선택된 id 넘어오는지 확인 필요.
        Log.i("!!!!", "넘어옴? ");
        Log.i("!!!!", "checked position size" + adapterListNote.checked.length);
        for (int i = 0; i < adapterListNote.checked.length; i++) {
            if(adapterListNote.checked[i]==true)
            {
                Log.i("!!!!","i "+i);
                long id=adapterListNote.getItemId(i);
                Log.i("!!!!", "id " + id);
                if (id > 0)
                {
                    db.execDelete("NOTE", "NOTE_NO=" + id);

                }
                adapterListNote.checked[i]=false;
            }
        }

        if(allchecked=true) {
            alldelete.setChecked(!alldelete.isChecked());
            allchecked = false;
        }
        bindNote();


    }

    public void buttonAllDelete_OnClick(View v)
    {
       allchecked=true;
       alldelete = (CheckBox)findViewById(R.id.chkAll);
       adapterListNote.setAllChecked(alldelete.isChecked());
       adapterListNote.notifyDataSetChanged();


        bindNote();

    }

    public void buttonRestore_OnClick(View v)
    {

        for (int i = 0; i < adapterListNote.checked.length; i++) {
            if(adapterListNote.checked[i]==true)
            {
                Log.i("!!!!","i "+i);
                long id=adapterListNote.getItemId(i);
                ContentValues values = new ContentValues();
                values.put("IS_DEL","0");
                int cnt = db.execUpdate("NOTE", values, "NOTE_NO=" + id);

                adapterListNote.checked[i]=false;
            }
        }
        if(allchecked=true) {
            alldelete.setChecked(!alldelete.isChecked());
            allchecked = false;
        }
        bindNote();

    }



}



