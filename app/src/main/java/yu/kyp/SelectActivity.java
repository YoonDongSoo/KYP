package yu.kyp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.print.PrintHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
    CheckBox buttonCheck;
    Note note;
    ListView selectlistNote;

    //목록체크 후 기능을 실행한 후 체크 처리 해제
    private boolean checkend = false;
    //전체 선택 버튼 체크됬는지 확인
    private boolean allchecked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        context = this;
        noteManager = new NoteManager(this);

        selectlistNote = (ListView) findViewById(R.id.listViewSelectNote);
        selectlistNote.setAdapter(adapterSelectlist);
        buttonCheck = (CheckBox) findViewById(R.id.buttonCheck);

        // 배경설정
        RelativeLayout layoutTop = (RelativeLayout)findViewById(R.id.layoutTop);
        setBackground(layoutTop);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkend == true) {
            for (int i = 0; i < adapterSelectlist.checked.length; i++)
                adapterSelectlist.checked[i] = false;

        }
        if(allchecked==true) {
            buttonCheck.setChecked(!buttonCheck.isChecked());
            allchecked = false;
        }
        // ListView에 노트 내용 뿌려주기.
        bindNote();
        Log.d(TAG, "onResume");
        Log.d(TAG, "Settings.getDefaultFactor():" + settings.getDefaultFactor());
        Log.i(TAG, "Setting.getFontType():" + settings.getFontType());
        Log.i(TAG, "Setting.getZoomFactor():" + settings.getZoomFactor());
        Log.i(TAG, "Setting.getBackgroundType():" + settings.getBackgroundType());
        Log.i(TAG, "Setting.getAlarmType():" + settings.getAlarmType());
        Log.i(TAG, "Setting.getListType():" + settings.getListType());
    }

    /**
     * ListView에 노트 내용 뿌려주기.
     */
    private void bindNote() {
        Cursor c = noteManager.getNoteList();

        if (adapterSelectlist == null) {
            adapterSelectlist = new CustomCursorAdapter(this, c);
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


    /**
     * 선택화면에서 휴지통 버튼을 눌렀을 때
     * @param v
     *
     */
    public void buttonDelete_OnClick(View v) throws Exception {



        DialogInterface.OnClickListener dialogClickListener = null;


        dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {

                    case DialogInterface.BUTTON_POSITIVE:
                        //체크된 목록의 id를 노트목록에서 삭제(휴지통에 들어감)
                        for (int i = 0; i < adapterSelectlist.checked.length; i++) {
                            if (adapterSelectlist.checked[i] == true) {
                                long id = adapterSelectlist.getItemId(i);
                                if (id > 0) {
                                    ContentValues values = new ContentValues();
                                    values.put("IS_DEL", "1");
                                    int cnt = db.execUpdate("NOTE", values, "NOTE_NO=" + id);
                                }
                                adapterSelectlist.checked[i] = false;

                            }

                        }

                        //전체 체크 버튼이 눌려져있다면 해제
                        if(allchecked==true) {
                            buttonCheck.setChecked(!buttonCheck.isChecked());
                            allchecked = false;
                        }
//                        if (buttonCheck.isChecked())
//                            buttonCheck.setChecked(!buttonCheck.isChecked());


                        bindNote();

                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        //int checkcount2=0;

        //체크 버튼이 선택되었을 때 삭제 버튼을 누르면 삭제 yes/no dialog를 띄운다.
        //체크 버튼이 선택되지 않았을 때는 토스트를 띄운다.
        for (int i = 0; i < adapterSelectlist.checked.length; i++) {
            if (adapterSelectlist.checked[i] == true) {
                Log.i("!!!!", "i " + i);
                long id = adapterSelectlist.getItemId(i);
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
                buttonCheck.setChecked(!buttonCheck.isChecked());
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

//        for (int i = 0; i < adapterSelectlist.checked.length; i++) {
//
//            if (adapterSelectlist.checked[i] == false)
//                checkcount2++;
//        }
//        if(checkcount2==adapterSelectlist.checked.length)
//        {
//            Toast.makeText(context, "목록을 선택해주세요", Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setMessage("삭제 하시겠습니까?").setPositiveButton("Yes", dialogClickListener)
//                    .setNegativeButton("No", dialogClickListener).show();
//        }
//        if(allchecked==true) {
//            buttonCheck.setChecked(!buttonCheck.isChecked());
//            allchecked = false;
//        }

    }

    //전체 선택버튼을 눌렀을 경우
    public void buttonCheck_OnClick(View v) {
        allchecked=buttonCheck.isChecked();
        adapterSelectlist.setAllChecked(buttonCheck.isChecked());
        adapterSelectlist.notifyDataSetChanged();
    }


    //공유버튼을 눌렀을 경우
    public void buttonShare_OnClick(View v) throws Exception {
        checkend = false;
        Paint paint = new Paint();
        int noteNo = 0;
        long id;

        //체크된 노트의 id를 이용해 노트 내용 이미지와 배경이미지를 불러온다.
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


        if (note == null) {

            Toast.makeText(context,"목록을 선택해주세요",Toast.LENGTH_SHORT).show();
            if(allchecked==true) {
                buttonCheck.setChecked(!buttonCheck.isChecked());
                allchecked = false;
            }

        } else {
            Bitmap notedata = note.NOTE_DATA;
            int background = note.BACKGROUND;
            Bitmap backgroundimg;
            switch (background)
            {
                case 0:
                    backgroundimg = BitmapFactory.decodeResource(getResources(),R.drawable.note_line_500);
                    break;
                case 1:
                    backgroundimg = BitmapFactory.decodeResource(getResources(),R.drawable.note_clean_500);
                    break;
                case 2:
                    backgroundimg = BitmapFactory.decodeResource(getResources(),R.drawable.note_clean_500);
                    break;
                default:
                    backgroundimg = BitmapFactory.decodeResource(getResources(),R.drawable.note_line_500);
                    break;
            }

            //노트 배경에 노트 내용 이미지 적용
            //Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.cat2);
            Bitmap noteimg = Bitmap.createBitmap(notedata.getWidth(), notedata.getHeight(), Bitmap.Config.ARGB_8888);
            //Bitmap noteimg = Bitmap.createBitmap(backgroundimg.getWidth(), backgroundimg.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(noteimg);
            canvas.drawBitmap(backgroundimg,null,new Rect(0,0,notedata.getWidth(),notedata.getHeight()),paint);
            canvas.drawBitmap(notedata,0,0,paint);

            //공유하기 부분
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Title");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            //이미지가 저장될 파일 경로 설정
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values);




            OutputStream outstream;
            try {
                //bitmap이미지를 jpeg로 변경 후 파일로 저장
                outstream = getContentResolver().openOutputStream(uri);
                noteimg.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                outstream.close();
            } catch (Exception e) {
                System.err.println(e.toString());
            }

            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, "Share Image"));


//            ImageView imageView = (ImageView) findViewById(R.id.imageView3);
//            imageView.setImageURI(uri);

            checkend = true;
            note=null;


        }


    }


    //프린트 버튼을 눌렀을 경우
    public void buttonPrint_OnClick(View v) throws Exception {
        checkend = false;
        doPhotoPrint();
        checkend = true;

    }

    //뒤로가기 버튼을 눌렀을 경우
    public void buttonback_OnClick(View v) {
        finish();
    }


    private void doPhotoPrint() throws Exception {
        PrintHelper photoPrinter = new PrintHelper(this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        int noteNo = 0;
        long id;

        //선택한 노트 id를 이용해 note 내용 이미지를 불러온다.
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

        if (note == null) {
            Toast.makeText(context,"목록을 선택해주세요", Toast.LENGTH_SHORT).show();
            if(allchecked==true) {
                buttonCheck.setChecked(!buttonCheck.isChecked());
                allchecked = false;
            }

        } else {
            Bitmap bitmap = note.NOTE_DATA;
            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
            //    R.drawable.ic_launcher);
            photoPrinter.printBitmap("note"+noteNo + ".jpg - test print", bitmap);
        }
        note=null;
    }
}



