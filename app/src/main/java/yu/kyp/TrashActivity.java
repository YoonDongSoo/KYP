package yu.kyp;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import java.util.ArrayList;

import yu.kyp.common.activity.ActivityBase;
import yu.kyp.image.NoteManager;

public class TrashActivity extends ActivityBase {
    CheckedTextView tv;
    ArrayList<Long> check = new ArrayList<>();
    CheckBox alldelete;
    boolean allchecked = false;
    private Context context = null;
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

//    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
//    {
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//            switch(which)
//            {
//                case DialogInterface.BUTTON_POSITIVE:
//                    ContentValues values = new ContentValues();
//                    db.execDelete("NOTE", "NOTE_NO=" + id);
//                    adapterListNote.checked[i]=false;
////                    values.put("IS_DEL","1");
////                    int cnt = db.execUpdate("NOTE", values, "NOTE_NO=" + deleteId);
////                    Log.i("!!","cnt:"+cnt);
//                    bindNote();
//                    break;
//                case DialogInterface.BUTTON_NEGATIVE:
//                    break;
//            }
//        }
//    };

//    private AdapterView.OnItemLongClickListener longClickListenerListNote = new AdapterView.OnItemLongClickListener() {
//        @Override
//        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//            deleteId  = id;
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setMessage("삭제하시겠습니까?").setPositiveButton("Yes", dialogClickListener)
//                    .setNegativeButton("No", dialogClickListener).show();
//
//            return true;
//        }
//    };


    private ListView listviewNote;

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

    public void buttonDelete_OnClick(View v) {

        DialogInterface.OnClickListener dialogClickListener = null;




        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        for (int i = 0; i < adapterListNote.checked.length; i++) {
                            if (adapterListNote.checked[i] == true) {
                                long id = adapterListNote.getItemId(i);
                                if (id > 0) {
                                    db.execDelete("NOTE", "NOTE_NO=" + id);
                                }
                                adapterListNote.checked[i] = false;
                            }
                        }
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

        int checkcount=0;

        for (int i = 0; i < adapterListNote.checked.length; i++) {
            if (adapterListNote.checked[i] == false)
                checkcount++;
        }
        if(checkcount==adapterListNote.checked.length)
        {
            Toast.makeText(context, "목록을 선택해주세요", Toast.LENGTH_SHORT).show();
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("영구삭제 하시겠습니까?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }

//        for (int i = 0; i < adapterListNote.checked.length; i++) {
//            if (adapterListNote.checked[i] == true) {
////                long id = adapterListNote.getItemId(i);
////                if (id > 0) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setMessage("영구삭제 하시겠습니까?").setPositiveButton("Yes", dialogClickListener)
//                            .setNegativeButton("No", dialogClickListener).show();
//                }
//            else
//                checkcount++;
//
//            }






//        for (i = 0; i < adapterListNote.checked.length; i++) {
//            if(adapterListNote.checked[i]==true) {
//
//                id = adapterListNote.getItemId(i);
//                if (id > 0) {


//
//                }
//
//
//
//
//              //  adapterListNote.checked[i] = false;
//            }
//
//        }

//        if(allchecked==true) {
//            alldelete.setChecked(!alldelete.isChecked());
//            allchecked = false;
//        }
//        bindNote();


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
        if(allchecked==true) {
            alldelete.setChecked(!alldelete.isChecked());
            allchecked = false;
        }
        bindNote();

    }



}



