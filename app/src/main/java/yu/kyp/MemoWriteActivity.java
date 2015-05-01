package yu.kyp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import yu.kyp.bluno.BlunoLibrary;
import yu.kyp.image.Note;
import yu.kyp.image.NoteManager;

public class MemoWriteActivity extends BlunoLibrary {

    private static final String TAG = MemoWriteActivity.class.getSimpleName();
    private StringBuffer strBuffer = new StringBuffer();
    private NoteManager noteManager = null;
    private static HorizontalScrollView Scroll_Horizontal;
    private static ScrollView Scroll_Vertical;

    protected static int currentX = 0;
    protected static int currentY = 0;

    PaintBoard paintboard;
    LinearLayout addedLayout;
    Button colorLegendBtn;
    TextView sizeLegendTxt;

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

    int mColor = 0xff000000;
    int mSize = 2;
    int oldColor = 0;
    int oldSize =0;
    int temp_size;
    int temp_color;
    boolean eraserSelected = false;
    boolean scrollSelected = false;
    boolean dragSelected = false;



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

        if(theString.contains("ZOM01")==true)
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
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_write);
        onCreateProcess();
        serialBegin(115200);

        Scroll_Vertical = (ScrollView) findViewById(R.id.scrollView);
        Scroll_Horizontal = (HorizontalScrollView) findViewById(R.id.horScrollView);
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


        final LinearLayout boardLayout = (LinearLayout) findViewById(R.id.boardLayout);


        paintboard = new PaintBoard(this);



        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                800,
                1000);                  //스크롤을 위한 캔버스의 크기 조절 부분

        paintboard.setLayoutParams(params);
        paintboard.setPadding(2, 2, 2, 2);

        boardLayout.addView(paintboard);                 //BestPaintActivity add

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
                Note note = noteManager.getNote(noteNo);
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
                    }

                }

            };
        }catch (Exception e) {
                e.printStackTrace();
            }
        }
//            penBtn.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    PenPaletteActivity.penlistener = new PenPaletteActivity.OnPenSelectedListener() {
//                        public void onPenSelected(int size) {
//                            mSize = size;
//                            oldSize = mSize;
//                            paintboard.updatePaintProperty(mColor, mSize);
//                            displayPaintProperty();
//                        }
//                    };
//                    PenPaletteActivity.colorlistener = new PenPaletteActivity.OnColorSelectedListener() {
//                        public void onColorSelected(int color) {
//                            mColor = color;
//                            oldColor = mColor;
//                            paintboard.updatePaintProperty(mColor, mSize);
//                            displayPaintProperty();
//                        }
//                    };
//                    PenPaletteActivity.completelistener = new PenPaletteActivity.OnCompleteSelectedListener() {
//                        public void onCompleteSelected() {
//                            mColor = oldColor;
//                            mSize = oldSize;
//                            paintboard.updatePaintProperty(mColor, mSize);
//                            displayPaintProperty();
//                        }
//                    };
//                    Log.d("!!!!!!!!!!","펜 선택 color 값"+mColor);
//                    Log.d("!!!!!!!!!!","펜 선택 size 값"+mSize);
//                    Intent intent = new Intent(getApplicationContext(), PenPaletteActivity.class);
//                    startActivity(intent);
//                }
//            });
//            eraserBtn.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//
//                    eraserSelected = !eraserSelected;
//
//                    if (eraserSelected) {
//
//                        penBtn.setEnabled(false);
//                        undoBtn.setEnabled(false);
//
//                       // penBtn.invalidate();
//                       // undoBtn.invalidate();
//
//                        oldColor = mColor;
//                        oldSize = mSize;
//
//                        //mColor = Color.WHITE;
//
//                        EraserPaletteActivity.listener = new EraserPaletteActivity.OnEraserSelectedListener() {
//                            public void onEraserSelected(int size) {
//                                mSize = size;
//                                paintboard.setEraserPaint(mSize);
//                                displayPaintProperty();
//                            }
//                        };
//                        penBtn.invalidate();
//                        undoBtn.invalidate();
//
//
//                        Intent intent = new Intent(getApplicationContext(), EraserPaletteActivity.class);
//
//                        startActivity(intent);
//
//                    }
//                    else {
//
//                        penBtn.setEnabled(true);
//                        undoBtn.setEnabled(true);
//
//                        penBtn.invalidate();
//                        undoBtn.invalidate();
//
//                        mColor = oldColor;
//                        mSize = oldSize;
//                        Log.d("!!!!!!!!!!","color 값"+mColor);
//                        Log.d("!!!!!!!!!!","size 값"+mSize);
//
//                        paintboard.updatePaintProperty(mColor, mSize);
//                        displayPaintProperty();
//                    }
//                }
//            });
//
//            undoBtn.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    paintboard.undo();
//
//                }
//            });


//        TextView textPointData = (TextView)findViewById(R.id.textViewPointData);
//        textPointData.setMovementMethod(new ScrollingMovementMethod());
//        textPointData.setText(sb.toString());



    /**
     * 스크롤 할 때 좌표값을 이동
     * @param x
     * @param y
     */
    public static void scrollBy(int x, int y)
    {
        Scroll_Horizontal.scrollBy(x, 0);
        Scroll_Vertical.scrollBy(0, y);
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

    public static void temp(){

    }

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
        // 확대 테스트
        paintboard.zoomInBitmap();
    }

    /**
     * 키보드
     * @param v
     */
    public void buttonText_OnClick(View v)
    {
        // 축소 테스트
        //Toast.makeText(this,"텍스트",Toast.LENGTH_SHORT).show();
        paintboard.zoomOutBitmap();
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

            //펜 사이즈, 크기  저장
            oldColor = mColor;
            oldSize = mSize;

            //mColor = Color.WHITE;

            //선택된 크기로 지우개 기능 활성화
            EraserPaletteActivity.listener = new EraserPaletteActivity.OnEraserSelectedListener() {
                public void onEraserSelected(int size) {
                    mSize = size;
                    paintboard.setEraserPaint(mSize);
                    //화면의 좌측 상단에 선택한 색상을 표시한다.
                    displayPaintProperty();
                }
            };

            //지우개 크기 선택 화면 띄우기
            Intent intent = new Intent(getApplicationContext(), EraserPaletteActivity.class);

            startActivity(intent);

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
        //펜 굵기 선택 팔레트를 눌렀을 때
        PenPaletteActivity.penlistener = new PenPaletteActivity.OnPenSelectedListener() {
            public void onPenSelected(int size) {
                mSize = size;
                oldSize = mSize;
                //선택되어진 굵기를 적용한다.
                paintboard.updatePaintProperty(mColor, mSize);
                //화면 좌측 상단에 선택한 굵기를 표시한다.
                displayPaintProperty();
            }
        };
        //펜 색상 선택 팔레트를 눌렀을 때
        PenPaletteActivity.colorlistener = new PenPaletteActivity.OnColorSelectedListener() {
            public void onColorSelected(int color) {
                mColor = color;
                oldColor = mColor;
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
        startActivity(intent);

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

            paintboard.setOnTouchListener(new View.OnTouchListener() {

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



            });
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

            paintboard.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });

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
        paintboard.undo.clearList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
    }
}

