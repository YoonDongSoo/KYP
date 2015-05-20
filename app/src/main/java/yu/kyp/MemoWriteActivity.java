package yu.kyp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import yu.kyp.bluno.BlunoLibrary;
import yu.kyp.image.Note;
import yu.kyp.image.NoteManager;
import yu.kyp.image.Thumbnail;

public class MemoWriteActivity extends BlunoLibrary {

    private static final String TAG = MemoWriteActivity.class.getSimpleName();
    private static final int REQUEST_DRAW_TEXT = 2;
    private static final int REQUEST_PEN_SIZE = 3;
    private static final int REQUEST_ERASER_SIZE = 4;
    private static final int REQUEST_INPUT_TITLE = 5;
    private static SharedPreferences sp;
    private static SharedPreferences sp2;
    private static SharedPreferences for_alpha;

    private StringBuffer strBuffer = new StringBuffer();
    private NoteManager noteManager = null;

    protected static int currentX = 0;
    protected static int currentY = 0;

    PaintBoard paintboard;
    static RecentColorAdapter recentcoloradapter;
    SeekBar sizeSeekBar;
    static int alpha_temp_value=0;

    static GridView recent_color_grid;


    //    Button pictureBtn;
    Button textBtn;
    Button penBtn;
    Button eraserBtn;
    Button alarmBtn;
    Button undoBtn;
    Button saveBtn;
    Button settingBtn;
    Button backBtn;
    Button colorBtn;
    TextView sizetextview;
    Button scrollBtn;
    Button textOKBtn;
    Button textCacleBtn;
    Canvas canvas;
    Button cancelBtn;

    int mColor = 0xff000000;
    int mSize = 2;
    int oldColor = 0;
    int oldSize =0;
    int temp_size;
    int temp_color;
    boolean eraserSelected = false;
    boolean scrollSelected = false;
    boolean dragSelected = false;
    boolean textSelected  = false;
    float x = 0;
    float y = 0;
    boolean text_flag = true;
    static int pen_size = 0;
    static int eraser_size = 0;
    static ArrayList<Integer> color_save = new ArrayList<Integer>();

    //테스트
    static int topviewh;
    static int belowtopviewh;
    static int bottomviewh;

    /**
     * 노트 객체
     */
    private Note note;


    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
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
    public void onSerialReceived(String theString) {							//Once connection data received, this function will be called
        //serialReceivedText.append(theString);							//append the text into the EditText
        //The Serial data from the BLUNO may be sub-packaged, so using a recvBuffer to hold the String is a good choice.
        Log.d(TAG,"onSerialReceived:"+theString);
        strBuffer.append(theString);

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_write);
        onCreateProcess();
        serialBegin(115200);

//      pictureBtn = (Button) findViewById(R.id.buttonPic);
        textBtn = (Button) findViewById(R.id.buttonText);
        penBtn = (Button) findViewById(R.id.buttonPen);
        eraserBtn = (Button) findViewById(R.id.buttonEraser);
        alarmBtn = (Button) findViewById(R.id.buttonAlarm);
        undoBtn = (Button) findViewById(R.id.buttonUndo);
        saveBtn = (Button) findViewById(R.id.buttonSave);
        settingBtn = (Button) findViewById(R.id.buttonSetting);
        backBtn = (Button) findViewById(R.id.buttonBack);
        colorBtn = (Button) findViewById(R.id.buttoncolor);
        sizetextview = (TextView) findViewById(R.id.textviewsize);
        scrollBtn = (Button) findViewById(R.id.buttonScroll);
        recent_color_grid = (GridView) findViewById(R.id.recent_color_grid);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        sizeSeekBar = (SeekBar) findViewById(R.id.sizeSeekBar);
//        sizeSeekBar.setMax(100);



        paintboard = (PaintBoard) findViewById(R.id.paintBoard);
        recentcoloradapter = new RecentColorAdapter(this);




        sizetextview.setText("Size:" + mSize + "      ");      //버튼 오른쪽에 현재 펜의 사이즈 표시
        sizetextview.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        sizetextview.setTextColor(Color.BLACK);

        colorBtn.setText(" ");
        colorBtn.setHeight(20);
        colorBtn.setBackgroundColor(mColor);            //현재 색깔을 나타냄

        StringBuilder sb = new StringBuilder();
        try {
            noteManager = new NoteManager(this);
            Intent i = getIntent();
            int noteNo = i.getIntExtra("NOTE_NO", 0);
            if (noteNo > 0) {
                note = noteManager.getNote(noteNo);
                paintboard.undo.addList(note.NOTE_DATA);;
            }
            else
            {
                note = new Note();
            }

            View.OnClickListener buttonListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.buttonPen:
                            buttonPen_OnClick(v);
                        case R.id.buttonEraser:
                            buttonEraser_OnClick(v);
                        case R.id.buttonUndo:
                            buttonUndo_OnClick(v);
                        case R.id.buttonScroll:
                            buttonScroll_OnClick(v);
                        case R.id.buttonBack:
                            buttonBack_OnClick(v);
                        case R.id.buttonAlarm:
                            buttonAlarm_OnClick(v);
                        case R.id.buttonSetting:
                            buttonSetting_OnClick(v);
                        case R.id.buttonPicture:
                            buttonPicture_OnClick(v);
                        case R.id.buttonSave:
                            buttonSave_OnClick(v);
                    }

                }

            };
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void buttonSave_OnClick(View v) {
        // DB에 저장
        saveNote();
    }

    /**
     * 노트를 DB에 저장한다.
     * 변경된 사항이 없으면 저장하지 않는다.
     */
    private void saveNote() {
        // 변경된 사항이 없으면 DB에 저장하지 않는다.
        if(paintboard.undo.size()<=1)
            return;

        if(note.TITLE==null || note.TITLE.equals("")==true)
            note.TITLE = "제목 없음";
        //note.NOTE_DATA = paintboard.undo.getLast();
//        note.NOTE_DATA = paintboard.mBitmapWrite;
        note.NOTE_DATA = null;//paintboard.mBitmapWrite;
        note.thumbnail = new Thumbnail(note.NOTE_DATA);
        noteManager.saveNoteData(note);
    }


    /**
     * 스크롤 할 때 좌표값을 이동
     * @param x
     * @param y
     */
    public static void scrollBy(int x, int y)
    {
        //Scroll_Horizontal.scrollBy(x, 0);
        //Scroll_Vertical.scrollBy(0, y);
    }


    public int getChosenColor() {
        return mColor;
    }

    public int getPenThickness() {
        return mSize;
    }

    /**
     * 좌측 상단에 선택된 색상, 크기를 표시한다.
     */
    private void displayPaintProperty() {
        colorBtn.setBackgroundColor(mColor);
        sizetextview.setText("Size : " + mSize);

        //addedLayout.invalidate();
    }

//    public static void displayRecentColor(){
//        int i=0;
//
//        if(color_save.size() != 0) {
//            Log.i("displayRecentColor","출력 " + color_save.size());
//            for (i = 0; i < color_save.size(); i++) {
//                recentcoloradapter.recent_color_arraylist = color_save;
//            }
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_memo_write, menu);
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
        else if (id==R.id.action_scan)
        {
            buttonScanOnClickProcess();
            return true;
        }
        else if (id==R.id.action_show_result)
        {
            TokenCommands();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void TokenCommands() {
        Log.d(TAG,"strBuffer:"+ strBuffer.toString());

        int outStatus = 0;
        StringBuffer temp = new StringBuffer();
        for (int i=0; i<strBuffer.length(); i++)
        {
            int ch = strBuffer.charAt(i);
            if(outStatus==0 && ch==2)
            {
                outStatus = 1;
                temp = new StringBuffer();
            }
            else if (outStatus==1)
            {
                if(ch==3)
                {
                    outStatus = 0;
                    Log.d(TAG,temp.toString());
                    continue;
                }
                temp.append(strBuffer.charAt(i));
            }
        }
        if(temp.length()>0)
            Log.d(TAG,temp.toString());


    }

    public void buttonSetting_OnClick(View v)
    {
        startActivity(new Intent(this,SettingsActivity.class));
    }

    /**
     * 뒤로가기
     * @param v
     */
    public void buttonBack_OnClick(View v)
    {
        finish();
        sp = getSharedPreferences("current_p_size",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("p_size_value");
        editor.commit();

        sp2 = getSharedPreferences("current_e_size",MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sp2.edit();
        editor2.remove("e_size_value");
        editor2.commit();

        for_alpha = getSharedPreferences("alpha_value",MODE_PRIVATE);
        SharedPreferences.Editor editor3 = for_alpha.edit();
        editor3.putInt("alpha_value_is",255);
        editor3.commit();
    }

    /**
     * 키보드
     * @param v
     */
    public void buttonText_OnClick(View v)
    {
        textSelected = !textSelected;
        if (textSelected) {
            penBtn.setEnabled(false);
            eraserBtn.setEnabled(false);
            undoBtn.setEnabled(false);
            alarmBtn.setEnabled(false);
            scrollBtn.setEnabled(false);

            penBtn.invalidate();
            eraserBtn.invalidate();
            undoBtn.invalidate();
            alarmBtn.invalidate();
            scrollBtn.invalidate();

            //화면이 터치 되었을때
            paintboard.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    x = event.getX();
                    y = event.getY();

//                    Intent i = MemoWriteActivity.this.getIntent();
//                    boolean flag = i.getBooleanExtra("flag",false);

                    Toast.makeText(MemoWriteActivity.this, x + "," + y, Toast.LENGTH_SHORT).show();

                    if(text_flag == true) {
                        Intent intent = new Intent(getApplicationContext(), TextDialog.class);
                        intent.putExtra("x", x);
                        intent.putExtra("y", y);
                        //startActivity(intent);
                        startActivityForResult(intent, REQUEST_DRAW_TEXT);
                        text_flag = false;
                    }

                    return true;
                }
                //back키를 눌렀을 때
                public boolean onKeyDown(int keyCode, KeyEvent event){
                    boolean endBack = false;
                    if(keyCode == KeyEvent.KEYCODE_BACK){
                        if(!endBack) {
                            finish();
                            endBack = true;
                        }
                    }
                    return true;

                }
            });
        }
        else{
            penBtn.setEnabled(true);
            eraserBtn.setEnabled(true);
            undoBtn.setEnabled(true);
            alarmBtn.setEnabled(true);
            scrollBtn.setEnabled(true);

            penBtn.invalidate();
            eraserBtn.invalidate();
            undoBtn.invalidate();
            alarmBtn.invalidate();
            scrollBtn.invalidate();

            paintboard.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });

            paintboard.updatePaintProperty(mColor, mSize);
            displayPaintProperty();
            text_flag = true;
        }
    }

    /**
     * 지우개 기능을 활성화한다.
     * @param v
     */
    public void buttonEraser_OnClick(View v)
    {

        eraserSelected = !eraserSelected;
        // 지우개 버튼이 선택되면
        if (eraserSelected) {
            //펜 버튼, undo 버튼 비활성화
            penBtn.setEnabled(false);
            undoBtn.setEnabled(false);

            penBtn.invalidate();
            undoBtn.invalidate();

//            //펜 사이즈, 크기  저장
            oldColor = mColor;
            oldSize = mSize;
//
//            //mColor = Color.WHITE;

            sp2 = getSharedPreferences("currnt_e_size",MODE_PRIVATE);
            int e_size_value = sp2.getInt("e_size_value",0);
//
            //선택된 크기로 지우개 기능 활성화
//            EraserPaletteActivity.listener = new EraserPaletteActivity.OnEraserSelectedListener() {
//                public void onEraserSelected(int size) {
//                    mSize = size;
            paintboard.setEraserPaint(e_size_value);
            //화면의 좌측 상단에 선택한 것을 표시한다.
            displayPaintProperty();
//                }
//            };

            //지우개 크기 선택 화면 띄우기
            Intent intent = new Intent(getApplicationContext(), EraserPaletteActivity.class);

            startActivityForResult(intent,REQUEST_ERASER_SIZE);

        }
        //지우개 버튼 선택이 해제되면
        else {
            //펜, undo 버튼 활성화
            penBtn.setEnabled(true);
            undoBtn.setEnabled(true);

            penBtn.invalidate();
            undoBtn.invalidate();

            //이전에 저장해놓은 색상, 크기 값을 가져온다.
            mColor = oldColor;
            mSize = oldSize;
            Log.d("!!!!!!!!!!","color 값"+mColor);
            Log.d("!!!!!!!!!!","size 값"+mSize);

            //선택되어진 색상과 크기를 적용한다.
            paintboard.updatePaintProperty(mColor, mSize);
            displayPaintProperty();
        }

    }

    /**
     * 손글씨를 위한 펜 기능을 활성화한다.
     * @param v
     */
    public void buttonPen_OnClick(View v)
    {
        //펜 색상 선택 팔레트를 눌렀을 때
        PenPaletteActivity.colorlistener = new PenPaletteActivity.OnColorSelectedListener() {
            public void onColorSelected(int color) {
                mColor = color;
                oldColor = mColor;

                //최근 사용한 색상을 저장
                color_save.add(mColor);
                Toast.makeText(MemoWriteActivity.this, "색상" + mColor, Toast.LENGTH_SHORT).show();

//                recentcoloradapter.recent_color_list = color_save;
//                displayRecentColor();
                for_alpha = getSharedPreferences("alpha_value",MODE_PRIVATE);
                alpha_temp_value = for_alpha.getInt("alpha_value_is",0);
                paintboard.set_alpha(alpha_temp_value);

                //선택되어진 색상을 적용한다.
                paintboard.updatePaintProperty(mColor, mSize);
                //화면의 좌측 상단에 선택한 색상을 표시한다.
                displayPaintProperty();
            }
        };
        //최근 사용한 색 선택 팔레트를 눌렀을 때
        PenPaletteActivity.recentcolorlistener = new PenPaletteActivity.OnRecentColorSelectedListener() {
            public void onRecentColorSelected(int color){
                mColor = color;
                oldColor = mColor;

                //최근 사용한 색상을 저장
                color_save.add(mColor);

                for_alpha = getSharedPreferences("alpha_value",MODE_PRIVATE);
                alpha_temp_value = for_alpha.getInt("alpha_value_is",0);
                paintboard.set_alpha(alpha_temp_value);

                //선택되어진 색상을 적용한다.
                paintboard.updatePaintProperty(mColor, mSize);
                //화면의 좌측 상단에 선택한 색상을 표시한다.
                displayPaintProperty();
            }
        };
        //형광펜 팔레트를 눌렀을 때
        PenPaletteActivity.neoncolorlistener = new PenPaletteActivity.OnNeonColorSelectedListener() {
            @Override
            public void onNeonColorSelected(int color) {
                mColor = color;
                oldColor = mColor;

                for_alpha = getSharedPreferences("alpha_value",MODE_PRIVATE);
                alpha_temp_value = for_alpha.getInt("alpha_value_is",0);

                paintboard.set_alpha(100);

                //선택되어진 색상을 적용한다.
                paintboard.updatePaintProperty(mColor, mSize);
                //화면의 좌측 상단에 선택한 색상을 표시한다.
                displayPaintProperty();
            }
        };
        //완료 버튼을 눌렀을 때
        PenPaletteActivity.completelistener = new PenPaletteActivity.OnCompleteSelectedListener() {
            public void onCompleteSelected() {
                mColor = oldColor;
                mSize = oldSize;

//                //최근 사용한 색상을 저장
//                color_save.add(mColor);
//                Toast.makeText(MemoWriteActivity.this, "색상" + mColor, Toast.LENGTH_SHORT).show();
//                displayRecentColor();

                //색상과 굵기를 적용한다.
                paintboard.updatePaintProperty(mColor, mSize);
                //화면의 좌측상단에 선택한 색상과 굵기를 표시한다.
                displayPaintProperty();
            }
        };
        Log.d("!!!!!!!!!!","펜 선택 color 값"+mColor);
        Log.d("!!!!!!!!!!","펜 선택 size 값"+mSize);

        //펜 색상, 굵기변경 팔레트 띄우기
        Intent intent = new Intent(getApplicationContext(), PenPaletteActivity.class);
        startActivityForResult(intent, REQUEST_PEN_SIZE);
    }

    /**
     * 사진 (보류)
     * @param v
     */
    public void buttonPicture_OnClick(View v)
    {
        // reset
        paintboard.zoomResetBitmap();
    }

    /**
     * 알람 설정
     * @param v
     */
    public void buttonAlarm_OnClick(View v)
    {
        // 블루투스 연결
        buttonScanOnClickProcess();
    }

    /**
     * Undo 기능을 활성화한다.
     * @param v
     */
    public void buttonUndo_OnClick(View v)
    {
        paintboard.undo();
    }

    /**
     * 스크롤 기능을 활성화한다.
     * 차후에 펜 자체의 기능이 될 것으로 임시로 버튼을 생성함
     * @param v
     */
    public void buttonScroll_OnClick(View v)
    {
        scrollSelected=!scrollSelected;

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
//            paintboard.setScrollTouchListener();
            /*paintboard.setOnTouchListener(new View.OnTouchListener() {

                //스크롤을 위해 화면을 터치하였을 때
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Scroll_Vertical.setOnTouchListener(this);
                    Scroll_Horizontal.setOnTouchListener(this);


                    switch (event.getAction())
                    {
                        //처음 눌렀을 때 좌표값을 저장한다.
                        case MotionEvent.ACTION_DOWN:
                            Scroll_Vertical.requestDisallowInterceptTouchEvent(true);
                            Log.i("scroll", "down");
                            currentX = (int)event.getRawX();
                            currentY = (int)event.getRawY();

                            break;

                        //처음 좌표값 - 움직인 후의 좌표값만큼 이동한다.
                        case MotionEvent.ACTION_MOVE:
                            Scroll_Vertical.requestDisallowInterceptTouchEvent(true);
                            Log.i("scroll", "move");
                            int x2 = (int)event.getRawX();
                            int y2 = (int)event.getRawY();
                            scrollBy(currentX - x2, currentY - y2);
                            currentX = x2;
                            currentY = y2;
                            break;

                        case MotionEvent.ACTION_UP:
                            Scroll_Vertical.requestDisallowInterceptTouchEvent(true);
                            Log.i("scroll", "up");
                            break;

                        default:
                            Scroll_Vertical.requestDisallowInterceptTouchEvent(true);
                            Log.i("scroll", "default");
                            currentX = (int)event.getRawX();
                            currentY = (int)event.getRawY();
                            break;
                    }
                    currentX = (int)event.getRawX();
                    currentY = (int)event.getRawY();

                    return true;
                }



            });*/
        }

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

            // 손글씨용 터치리스너 붙이긴
//            paintboard.setPaintTouchListener();

            paintboard.updatePaintProperty(mColor, mSize);
            displayPaintProperty();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume Process");
        onResumeProcess();
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();
    }

    @Override
    protected void onStop() {
        super.onStop();
        onStopProcess();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();
        // 노트를 디비에 저장하고
        // undo리스트를 삭제.
//        MemoTitle.saveNote();
        paintboard.undo.clearList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_DRAW_TEXT)
        {
            int isCancel = data.getIntExtra("isCancel",0);
            if(isCancel==0) {
                int count = 0;
                DisplayMetrics outMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

                float density = outMetrics.density;

                // ok버튼 눌렀을 때.
                String text[] = data.getStringExtra("text").toString().split("\n");
                float x = data.getFloatExtra("x", 0.0f);
                float y = data.getFloatExtra("y", 0.0f);
                Log.i(TAG, "text:" + text);


                for(count = 0; count<text.length; count++) {
                    //drawText를 위해 y의 위치를 옮겨줘야함(하지않을 경우 같은 자리에 써짐)
                    y += 60.0f;
                    paintboard.drawText(text[count], x, y);
                }
                text_flag = true;
            }
            else
            {
                // 취소 버튼 눌렀을 때
                Log.i(TAG,"취소 버튼 눌렀네~");
                text_flag = true;
            }
        }
        if(requestCode == REQUEST_PEN_SIZE){
            if(data.getIntExtra("p_size",0) != 0) {
                pen_size = data.getIntExtra("p_size", 0);
                Log.i("펜의 사이즈", "" + pen_size);
                mSize = pen_size;
                oldSize = mSize;
                Toast.makeText(MemoWriteActivity.this, "펜 사이즈 넘어왔네~" + pen_size, Toast.LENGTH_SHORT).show();

                sp = getSharedPreferences("current_p_size",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("p_size_value",pen_size);
                editor.commit();




                paintboard.updatePaintProperty(mColor, pen_size);
                //화면의 좌측 상단에 선택한 색상을 표시한다.
                displayPaintProperty();

            }
        }
        if(requestCode == REQUEST_ERASER_SIZE){
            if(data.getIntExtra("e_size",0) != 0){
                eraser_size = data.getIntExtra("e_size",0);



                oldSize = mSize;
                mSize = eraser_size;
                Toast.makeText(MemoWriteActivity.this, "지우개 사이즈 넘어왔네~" + eraser_size, Toast.LENGTH_SHORT).show();

                sp2 = getSharedPreferences("current_e_size",MODE_PRIVATE);
                SharedPreferences.Editor editor2 = sp2.edit();
                editor2.putInt("e_size_value",eraser_size);
                editor2.commit();

                //펜 사이즈, 크기  저장
                oldColor = mColor;

                paintboard.setEraserPaint(mSize);

                //화면의 좌측 상단에 선택한 사이즈를 표시한다.
                displayPaintProperty();
            }
        }
        if(requestCode == REQUEST_INPUT_TITLE){
            Toast.makeText(MemoWriteActivity.this, "종료값은 들어옴", Toast.LENGTH_SHORT).show();
            MemoWriteActivity.this.finish();
        }
    }

//    @Override
//    public void onBackPressed() {
//        Toast.makeText(this,"뒤로 가기 키 터치",Toast.LENGTH_SHORT);
//        super.onBackPressed();
//        finish();
//    }
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event){
//        switch(keyCode){
//            case android.view.KeyEvent.KEYCODE_BACK:
//                Toast.makeText(MemoWriteActivity.this,"뒤로 가기 키 터치",Toast.LENGTH_SHORT);
//                break;
//        }
//        finish();
//        return true;
//    }

}

