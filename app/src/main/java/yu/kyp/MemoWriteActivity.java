package yu.kyp;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import yu.kyp.bluno.BlunoLibrary;
import yu.kyp.image.Note;
import yu.kyp.image.NoteManager;
import yu.kyp.image.PointData;
import yu.kyp.image.Stroke;


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

    int mColor = 0xff000000;
    int mSize = 2;
    int oldColor = 0;
    int oldSize =0;
    int temp_size;
    int temp_color;
    boolean eraserSelected = false;
    boolean scrollSelected = false;
    boolean dragSelected = false;


    class Stroke{
        int stroke_no;
        int COLOR;
        int THICKNESS;
        ArrayList<PointData> listPointData;
    }
    class PointData{
        int point_no;
        int stroke_no;
        float X;
        float Y;
    }

    ArrayList<Stroke> stroke = new ArrayList<Stroke>();

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

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_write);
        onCreateProcess();
        serialBegin(115200);
        Scroll_Vertical = (ScrollView) findViewById(R.id.scrollView);
        Scroll_Horizontal = (HorizontalScrollView) findViewById(R.id.horScrollView);
//        pictureBtn = (Button) findViewById(R.id.buttonPic);
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


                for(Stroke s: stroke)
                {
                    sb.append("COLOR:"+s.COLOR+"\n");
                    sb.append("THICKNESS:"+s.THICKNESS+"\n");
                    for(PointData p: s.listPointData)
                    {
                        sb.append(p.X+","+p.Y+"\n");
                    }
                }

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

                        penBtn.invalidate();
                        undoBtn.invalidate();

                        oldColor = mColor;
                        oldSize = mSize;

                        mColor = Color.WHITE;

                        EraserPaletteActivity.listener = new EraserPaletteActivity.OnEraserSelectedListener() {
                            public void onEraserSelected(int size) {
                                mSize = size;
                                paintboard.updatePaintProperty(mColor, mSize);
                                displayPaintProperty();
                            }
                        };

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

//        addedLayout.invalidate();
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
    }
}
