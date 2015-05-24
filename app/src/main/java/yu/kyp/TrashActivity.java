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
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import yu.kyp.common.activity.ActivityBase;
import yu.kyp.image.Note;
import yu.kyp.image.NoteManager;

public class TrashActivity extends ActivityBase {

    private Context context = null;
    private NoteManager noteManager = null;
    private Note note;
    private CustomCursorAdapter adapterListNote = null;
    private ListView listviewNote;
    private CheckBox alldelete;
    //전체 선택 버튼 체크됬는지 확인
    private boolean allchecked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
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


    /**
     * 영구 삭제
     * @param v
     */
    public void buttonDelete_OnClick(View v) throws Exception {

        DialogInterface.OnClickListener dialogClickListener = null;


        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //체크된 목록의 id를 영구삭제
                        for (int i = 0; i < adapterListNote.checked.length; i++) {
                            if (adapterListNote.checked[i] == true) {
                                long id = adapterListNote.getItemId(i);
                                if (id > 0) {
                                    db.execDelete("NOTE", "NOTE_NO=" + id);
                                }
                                adapterListNote.checked[i] = false;
                            }
                        }
                        //전체 체크 버튼이 눌려져있다면 해제
                        if(allchecked==true) {
                            alldelete.setChecked(!alldelete.isChecked());
                            allchecked = false;
                        }
                        bindNote();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };



        //체크 버튼이 선택되었을 때 영구삭제 버튼을 누르면 삭제 yes/no dialog를 띄운다.
        //체크 버튼이 선택되지 않았을 때는 토스트를 띄운다.
        for (int i = 0; i < adapterListNote.checked.length; i++) {
            if (adapterListNote.checked[i] == true) {
                Log.i("!!!!", "i " + i);
                long id = adapterListNote.getItemId(i);
                Log.i("!!!!", "id " + id);
                Intent msg = new Intent(context, MemoWriteActivity.class);
                int noteNo = msg.getIntExtra("NOTE_NO", (int) id);
                if (noteNo > 0) {
                    note = noteManager.getNote(noteNo);
                    Log.e("!!!", "note " + noteNo);

                }
            }

        }


        if (note == null) {
            Toast.makeText(context, "목록을 선택해주세요", Toast.LENGTH_SHORT).show();
            if(allchecked==true) {
            alldelete.setChecked(!alldelete.isChecked());
            allchecked = false;
            }
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("삭제 하시겠습니까?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
        note = null;
//        for (int i = 0; i < adapterListNote.checked.length; i++) {
//            if (adapterListNote.checked[i] == false)
//                checkcount1++;
//        }
//        if(checkcount1==adapterListNote.checked.length)
//        {
//            Toast.makeText(context, "목록을 선택해주세요", Toast.LENGTH_SHORT).show();
//        }
//
//        else
//        {
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setMessage("영구삭제 하시겠습니까?").setPositiveButton("Yes", dialogClickListener)
//                    .setNegativeButton("No", dialogClickListener).show();
//        }
//        if(allchecked==true) {
//            alldelete.setChecked(!alldelete.isChecked());
//            allchecked = false;
//        }


    }

    /**
     * 전체 선택
     * @param v
     */
    public void buttonAllDelete_OnClick(View v)
    {

       alldelete = (CheckBox)findViewById(R.id.chkAll);
       allchecked=alldelete.isChecked();
       adapterListNote.setAllChecked(alldelete.isChecked());
       adapterListNote.notifyDataSetChanged();


        bindNote();

    }

    /**
     * 휴지통 복원 기능
     * @param v
     */
    public void buttonRestore_OnClick(View v)
    {
        int checkcount=0;
        //선택된 노트를 노트 목록에 다시 나타나게 한다.
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
            else
                checkcount++;
        }
        if(checkcount==adapterListNote.checked.length)
        {
            Toast.makeText(context, "목록을 선택해주세요", Toast.LENGTH_SHORT).show();
            if(allchecked==true) {
            alldelete.setChecked(!alldelete.isChecked());
            allchecked = false;
            }
        }
        if(allchecked==true) {
            alldelete.setChecked(!alldelete.isChecked());
            allchecked = false;
        }
        bindNote();

    }



}



