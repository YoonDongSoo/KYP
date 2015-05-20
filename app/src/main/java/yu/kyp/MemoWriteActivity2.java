package yu.kyp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import yu.kyp.bluno.BlunoLibrary;
import yu.kyp.image.Note;
import yu.kyp.image.NoteManager;
import yu.kyp.image.TouchImageView;


public class MemoWriteActivity2 extends BlunoLibrary {

    private static final String TAG = MemoWriteActivity2.class.getSimpleName();
    private StringBuffer strBuffer = new StringBuffer();
    private NoteManager noteManager = null;
    /**
     * 노트 객체
     */
    private Note note;
    private Bitmap bitmapWrite = null;
    private TouchImageView touchViewPaint = null;
    private Canvas canvasWrite = null;
    Button textBtn;
    Button penBtn;
    Button eraserBtn;
    Button alarmBtn;
    Button undoBtn;
    Button saveBtn;
    Button settingBtn;
    Button backBtn;
    Button colorBtn;
    Button scrollBtn;
    private boolean scrollSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_write_activity2);

        // 1. 기본 변수
        noteManager = new NoteManager(this);
        textBtn = (Button) findViewById(R.id.buttonText);
        penBtn = (Button) findViewById(R.id.buttonPen);
        eraserBtn = (Button) findViewById(R.id.buttonEraser);
        alarmBtn = (Button) findViewById(R.id.buttonAlarm);
        undoBtn = (Button) findViewById(R.id.buttonUndo);
        saveBtn = (Button) findViewById(R.id.buttonSave);
        settingBtn = (Button) findViewById(R.id.buttonSetting);
        backBtn = (Button) findViewById(R.id.buttonBack);
        colorBtn = (Button) findViewById(R.id.buttoncolor);
        scrollBtn = (Button) findViewById(R.id.buttonScroll);

        // 2. 노트데이터 불러오기
        getNoteData();
        bitmapWrite = note.NOTE_DATA.copy(Bitmap.Config.ARGB_8888,true);    // mutable로 copy해야 함.

        // 3. TouchImageView 설정
        // 손글씨용 터치 리스너를 붙이기.
        touchViewPaint =  (TouchImageView)findViewById(R.id.touchViewPaint);
        touchViewPaint.setImageBitmap(bitmapWrite);
        touchViewPaint.setPaintTouchListener(); // 손글씨용 터치 리스너
        canvasWrite = new Canvas(bitmapWrite);
        touchViewPaint.setWriteCanvas(canvasWrite);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //Log.e(TAG,"onWindowFocusChanged");
        super.onWindowFocusChanged(hasFocus);
        int top = touchViewPaint.getTop();
        int bottom = touchViewPaint.getBottom();
        int left = touchViewPaint.getLeft();
        int right = touchViewPaint.getRight();
        int width = touchViewPaint.getWidth();
        int height = touchViewPaint.getHeight();
        if(bitmapWrite==null) {
            bitmapWrite = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            canvasWrite = new Canvas(bitmapWrite);
            touchViewPaint.setWriteCanvas(canvasWrite);
        }
    }

    private void getNoteData() {
        try
        {

            Intent i = getIntent();
            int noteNo = i.getIntExtra("NOTE_NO", 0);
            if (noteNo > 0) {
                note = noteManager.getNote(noteNo);
                //paintboard.undo.addList(note.NOTE_DATA);;

            }
            else
            {
                note = new Note();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_memo_write_activity2, menu);
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
    public void onConectionStateChange(connectionStateEnum theConnectionState) {
        switch (theConnectionState) {											//Four connection state
            case isConnected:
                Log.d(TAG, "Connected");
                //buttonScan.setText("Connected");
                break;
            case isConnecting:
                Log.d(TAG, "Connecting");
                //buttonScan.setText("Connecting");
                break;
            case isToScan:
                Log.d(TAG, "Scan");
                //buttonScan.setText("Scan");
                break;
            case isScanning:
                Log.d(TAG, "Scanning");
                //buttonScan.setText("Scanning");
                break;
            case isDisconnecting:
                Log.d(TAG, "isDisconnecting");
                //buttonScan.setText("isDisconnecting");
                break;
            default:
                break;
        }
    }

    @Override
    public void onSerialReceived(String theString) {
        //serialReceivedText.append(theString);							//append the text into the EditText
        //The Serial data from the BLUNO may be sub-packaged, so using a recvBuffer to hold the String is a good choice.
        Log.d(TAG,"onSerialReceived:"+theString);
        strBuffer.append(theString);

        // 임시로 주석처리
        /*if(theString.contains("ZOM01")==true)
        {
            paintboard.zoomInBitmap();
        }
        else if(theString.contains("ZOM02")==true)
        {
            paintboard.zoomOutBitmap();
        }
        else if(theString.contains("ZOM03")==true)
        {
            paintboard.zoomResetBitmap();
        }*/
    }

    public void buttonScroll_OnClick(View v)
    {

        scrollSelected=!scrollSelected;

        //=====================================================
        // 1. 줌스크롤용 터치리스너 붙이기
        //스크롤 버튼이 눌렸을 경우
        //스크롤 버튼을 제외한 나머지 버튼들을 비활성화인 false 상태로 만듦
        if (scrollSelected) {
            Log.i("scrollBtn", "clicked.");
            colorBtn.setEnabled(false);
            penBtn.setEnabled(false);
            eraserBtn.setEnabled(false);
            undoBtn.setEnabled(false);
            alarmBtn.setEnabled(false);
//            scrollBtn.setEnabled(false);

            colorBtn.invalidate();
            penBtn.invalidate();
            eraserBtn.invalidate();
            undoBtn.invalidate();
            alarmBtn.invalidate();
//                        scrollBtn.invalidate();
            // 줌스크롤용 터치리스너 붙이기
            touchViewPaint.setScrollTouchListener();

        }
        //=====================================================
        // 2. 손글씨용 터치리스너 붙이기
        //스크롤 버튼이 한번 더 눌렸을 경우
        //스크롤 이외의 버튼을 활성화인 true를 해줌
        else {
            Log.i("scrollBtn", "unclicked.");
            colorBtn.setEnabled(true);
            penBtn.setEnabled(true);
            eraserBtn.setEnabled(true);
            undoBtn.setEnabled(true);
            alarmBtn.setEnabled(true);
//                        scrollBtn.setEnabled(true);

            colorBtn.invalidate();
            penBtn.invalidate();
            eraserBtn.invalidate();
            undoBtn.invalidate();
            alarmBtn.invalidate();
//                        scrollBtn.invalidate();

            // 손글씨용 터치리스너 붙이기
            touchViewPaint.setPaintTouchListener();

            /*paintboard.updatePaintProperty(mColor, mSize);
            displayPaintProperty();*/
        }
    }
    public void buttonSetting_OnClick(View v)
    {
//        Toast.makeText(MemoWriteActivity2.this,"설정 눌렀음",Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getApplicationContext(),SettingsActivity.class);
        startActivity(i);
    }
    public void buttonAlarm_OnClick(View v){
        Intent i = new Intent(getApplicationContext(),AlarmActivity.class);
        startActivity(i);
    }
}
