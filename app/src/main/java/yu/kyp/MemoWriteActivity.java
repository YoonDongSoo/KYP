package yu.kyp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

        DisplayMetrics outMetrics = new DisplayMetrics();    getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

        int sum = outMetrics.densityDpi;

        Log.d("sum","**********************************************" + sum);



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
                2000,
                2400);                  //스크롤을 위한 캔버스의 크기 조절 부분



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
            int noteNo = i.getIntExtra("NOTE_NO",0);
            if(noteNo>0) {
                Note note = noteManager.getNote(noteNo);


            }
            penBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    PenPaletteActivity.penlistener = new PenPaletteActivity.OnPenSelectedListener() {
                        public void onPenSelected(int size) {
                            mSize = size;
                            oldSize = mSize;
                            paintboard.updatePaintProperty(mColor, mSize);
                            displayPaintProperty();
                        }
                    };
                    PenPaletteActivity.colorlistener = new PenPaletteActivity.OnColorSelectedListener() {
                        public void onColorSelected(int color) {
                            mColor = color;
                            oldColor = mColor;
                            paintboard.updatePaintProperty(mColor, mSize);
                            displayPaintProperty();
                        }
                    };
                    PenPaletteActivity.completelistener = new PenPaletteActivity.OnCompleteSelectedListener() {
                        public void onCompleteSelected() {
                            mColor = oldColor;
                            mSize = oldSize;
                            paintboard.updatePaintProperty(mColor, mSize);
                            displayPaintProperty();
                        }
                    };
                    Log.d("!!!!!!!!!!","펜 선택 color 값"+mColor);
                    Log.d("!!!!!!!!!!","펜 선택 size 값"+mSize);
                    Intent intent = new Intent(getApplicationContext(), PenPaletteActivity.class);
                    startActivity(intent);
                }
            });
            eraserBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    eraserSelected = !eraserSelected;

                    if (eraserSelected) {

                        penBtn.setEnabled(false);
                        undoBtn.setEnabled(false);

                       // penBtn.invalidate();
                       // undoBtn.invalidate();

                        oldColor = mColor;
                        oldSize = mSize;

                        //mColor = Color.WHITE;

                        EraserPaletteActivity.listener = new EraserPaletteActivity.OnEraserSelectedListener() {
                            public void onEraserSelected(int size) {
                                mSize = size;
                                paintboard.setEraserPaint(mSize);
                                displayPaintProperty();
                            }
                        };
                        penBtn.invalidate();
                        undoBtn.invalidate();


                        Intent intent = new Intent(getApplicationContext(), EraserPaletteActivity.class);

                        startActivity(intent);

                    }
                    else {

                        penBtn.setEnabled(true);
                        undoBtn.setEnabled(true);

                        penBtn.invalidate();
                        undoBtn.invalidate();

                        mColor = oldColor;
                        mSize = oldSize;
                        Log.d("!!!!!!!!!!","color 값"+mColor);
                        Log.d("!!!!!!!!!!","size 값"+mSize);

                        paintboard.updatePaintProperty(mColor, mSize);
                        displayPaintProperty();
                    }
                }
            });

            undoBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    paintboard.undo();

                }
            });
            scrollBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    scrollSelected=!scrollSelected;

                    if (scrollSelected) {
                        Log.i("scrollBtn", "clicked.");
                        colorBtn.setEnabled(false);
                        penBtn.setEnabled(false);
                        eraserBtn.setEnabled(false);
                        undoBtn.setEnabled(false);
                        alarmBtn.setEnabled(false);
//                        scrollBtn.setEnabled(false);

                        colorBtn.invalidate();
                        penBtn.invalidate();
                        eraserBtn.invalidate();
                        undoBtn.invalidate();
                        alarmBtn.invalidate();
//                        scrollBtn.invalidate();

                        paintboard.setOnTouchListener(new View.OnTouchListener() {

                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                Scroll_Vertical.setOnTouchListener(this);
                                Scroll_Horizontal.setOnTouchListener(this);


                                switch (event.getAction())
                                {
                                    case MotionEvent.ACTION_DOWN:
                                        Scroll_Vertical.requestDisallowInterceptTouchEvent(true);
                                        Log.i("scroll", "down");
                                        currentX = (int)event.getRawX();
                                        currentY = (int)event.getRawY();

                                        break;

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
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

//        TextView textPointData = (TextView)findViewById(R.id.textViewPointData);
//        textPointData.setMovementMethod(new ScrollingMovementMethod());
//        textPointData.setText(sb.toString());

    }

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

    public void buttonBack_OnClick(View v)
    {
        // 확대 테스트
        paintboard.zoomInBitmap();
    }

    public void buttonText_OnClick(View v)
    {
        // 축소 테스트
        //Toast.makeText(this,"텍스트",Toast.LENGTH_SHORT).show();
        paintboard.zoomOutBitmap();
    }

    public void buttonPicture_OnClick(View v)
    {
        // reset
        paintboard.zoomResetBitmap();
    }

    public void buttonAlarm_OnClick(View v)
    {
        // 블루투스 연결
        buttonScanOnClickProcess();
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

