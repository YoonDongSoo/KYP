package yu.kyp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.print.PrintHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.OutputStream;

import yu.kyp.common.activity.ActivityBase;
import yu.kyp.image.Note;
import yu.kyp.image.NoteManager;




public class SelectActivity extends ActivityBase {
    private int PICK_IMAGE_REQUEST = 1;

    private static final String TAG = MemoListActivity.class.getSimpleName();
    private NoteManager noteManager = null;
    private Context context = null;
    private CustomCursorAdapter adapterSelectlist = null;
    private static SharedPreferences sp;
    static SharedPreferences sp2;
    CheckBox buttonCheck;
    private boolean checkend = false;
    Note note;
    private AdapterView.OnItemClickListener listenerListNote = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            sp = getSharedPreferences("current_p_size",MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("p_size_value",2);
            editor.commit();

            sp2 = getSharedPreferences("current_e_size",MODE_PRIVATE);
            SharedPreferences.Editor editor2 = sp2.edit();
            editor2.putInt("e_size_value",2);
            editor2.commit();

            Intent i = new Intent(context,SelectActivity.class);
            i.putExtra("NOTE_NO",(int)id);
            startActivity(i);
        }
    };
    //private SimpleCursorAdapter adapterListNote = null;

    ListView selectlistNote;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        context = this;
        noteManager = new NoteManager(this);

        selectlistNote = (ListView) findViewById(R.id.listViewSelectNote);
        selectlistNote.setAdapter(adapterSelectlist);
        buttonCheck = (CheckBox)findViewById(R.id.buttonCheck);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(checkend==true)
        {
            for (int i = 0; i < adapterSelectlist.checked.length; i++)
                adapterSelectlist.checked[i]=false;

        }
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

        if ( adapterSelectlist == null) {
            adapterSelectlist = new CustomCursorAdapter(this,c);
            selectlistNote.setAdapter(adapterSelectlist);

        } else {
            adapterSelectlist.changeCursor(c);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select, menu);
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

    public void buttonDelete_OnClick(View v)
    {

        for (int i = 0; i < adapterSelectlist.checked.length; i++) {
            if(adapterSelectlist.checked[i]==true)
            {
                Log.i("!!!!","i "+i);
               long id=adapterSelectlist.getItemId(i);
                Log.i("!!!!", "id " + id);
                if (id > 0)
                {

                    //db.execDelete("NOTE", "NOTE_NO=" + id);
                    //long id=adapterSelectlist.getItemId(i);
                    ContentValues values = new ContentValues();
                    values.put("IS_DEL","1");
                    int cnt = db.execUpdate("NOTE", values, "NOTE_NO=" + id);



                }
                adapterSelectlist.checked[i]=false;
            }
        }
        if(buttonCheck.isChecked())
            buttonCheck.setChecked(!buttonCheck.isChecked());
        bindNote();
    }

    public void buttonCheck_OnClick(View v)
    {

        adapterSelectlist.setAllChecked(buttonCheck.isChecked());
        adapterSelectlist.notifyDataSetChanged();
    }




    public void buttonShare_OnClick(View v) throws Exception {
        checkend = false;
        int noteNo = 0;
        long id;
        for (int i = 0; i < adapterSelectlist.checked.length; i++) {
            if (adapterSelectlist.checked[i] == true) {
                Log.i("!!!!", "i " + i);
                id = adapterSelectlist.getItemId(i);
                Log.i("!!!!", "id " + id);
                Intent msg = new Intent(context, MemoWriteActivity.class);
                noteNo = msg.getIntExtra("NOTE_NO", (int) id);
                if (noteNo > 0) {
                    note = noteManager.getNote(noteNo);

                    Log.e("!!!", "note " + noteNo);

                }
            }

        }

        if(note==null)
        {
            Toast.makeText(context,"목록을 선택해주세요",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Bitmap icon = note.NOTE_DATA;
            //Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.cat2);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Title");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values);

            Log.e("!!!!", "uri  " + uri);


            OutputStream outstream;
            try {
                outstream = getContentResolver().openOutputStream(uri);
                icon.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                outstream.close();
            } catch (Exception e) {
                System.err.println(e.toString());
            }

            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, "Share Image"));


            ImageView imageView = (ImageView) findViewById(R.id.imageView3);
            imageView.setImageURI(uri);

            checkend = true;
        }


    }



    public void buttonPrint_OnClick(View v)
    {
        checkend = false;
        doPhotoPrint();
        checkend = true;
    }
    private void doPhotoPrint() {
        PrintHelper photoPrinter = new PrintHelper(this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_launcher);
        photoPrinter.printBitmap("droids.jpg - test print", bitmap);
    }
}
