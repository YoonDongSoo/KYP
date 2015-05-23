package yu.kyp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import yu.kyp.bluno.BlunoLibrary;
import yu.kyp.common.Pref;
import yu.kyp.common.Utils;
import yu.kyp.image.Note;
import yu.kyp.image.NoteManager;
import yu.kyp.image.Thumbnail;
import yu.kyp.image.TouchImageView;


public class MemoWriteActivity2 extends BlunoLibrary {

    private static final String TAG = MemoWriteActivity2.class.getSimpleName();
    private static final int REQUEST_PEN_SIZE = 3;
    private static final int REQUEST_DRAW_TEXT = 2;
    private static final int REQUEST_ERASER_SIZE = 4;
    private static final int REQUEST_INPUT_TITLE = 5;
    private StringBuffer strBuffer = new StringBuffer();
    private NoteManager noteManager = null;
    /**
     * 노트 객체
     */
    private Note note;
    private Bitmap bitmapWrite = null;
    private TouchImageView viewTouchPaint = null;
    private Canvas canvasWrite = null;
    ImageButton textBtn;
    ImageButton penBtn;
    ImageButton eraserBtn;
    ImageButton alarmBtn;
    ImageButton undoBtn;
    ImageButton saveBtn;
    ImageButton backBtn;
    Button colorBtn;
    ImageButton scrollBtn;
    private TextView textViewTitle;
    private boolean scrollSelected;
    // 2015-05-23 윤동수 주석처리
    //private Bitmap bitmapBackground;
    //private Canvas canvasBackground;
    //private SharedPreferences for_alpha;
    //private int alpha_temp_value;
    private int mSize = 10;
    private int mColor = Color.BLACK;
    private int oldColor;
    private ArrayList<Integer> color_save = new ArrayList<Integer>();
    private boolean isEraserMode;
    private Context context;
    private boolean textSelected;

    private ViewTreeObserver.OnGlobalLayoutListener touchViewPaint_OnGlobalLayoutLIstener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int height = viewTouchPaint.getHeight();
            int width = viewTouchPaint.getWidth();
            Log.e(TAG,String.format("width:%d height:%d",width,height));



            //=========================================
            // 1. width,height로 빈 비트맵을 만들거나
            //    기존 데이터를 DB에서 가져온다.
            if(note.NOTE_DATA==null) {
                bitmapWrite = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                canvasWrite = new Canvas(bitmapWrite);
                // 2015-05-22 윤동수 수정: 백그라운드는 그리지 않는다.
                //canvasWrite.drawBitmap(bitmapBackground,0,0,null);
            }
            else
            {
                bitmapWrite = note.NOTE_DATA.copy(Bitmap.Config.ARGB_8888,true);    // mutable로 copy해야 함.
                canvasWrite = new Canvas(bitmapWrite);
            }


            // 2. 글쓰기 비트맵&캔버스 설정
            viewTouchPaint.setImageBitmap(bitmapWrite);
            //viewTouchPaint.setBackgroundBitmap(bitmapBackground);
            viewTouchPaint.setWriteCanvas(canvasWrite);
            viewTouchPaint.setWriteBitmap(bitmapWrite);

            ViewTreeObserver obs = viewTouchPaint.getViewTreeObserver();
            obs.removeOnGlobalLayoutListener(this);

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_memo_write_activity2);

        //0. 블루투스
        onCreateProcess();
        serialBegin(115200);

        // 1. 기본 변수
        noteManager = new NoteManager(this);
        textBtn = (ImageButton) findViewById(R.id.buttonText);
        penBtn = (ImageButton) findViewById(R.id.buttonPen);
        eraserBtn = (ImageButton) findViewById(R.id.buttonEraser);
        alarmBtn = (ImageButton) findViewById(R.id.buttonAlarm);
        undoBtn = (ImageButton) findViewById(R.id.buttonUndo);
        saveBtn = (ImageButton) findViewById(R.id.buttonSave);
        backBtn = (ImageButton) findViewById(R.id.buttonBack);
        colorBtn = (Button) findViewById(R.id.buttonColor);
        scrollBtn = (ImageButton) findViewById(R.id.buttonScroll);
        viewTouchPaint =  (TouchImageView)findViewById(R.id.touchViewPaint);
        textViewTitle = (TextView)findViewById(R.id.textViewTitle);

        // 2. 노트데이터 불러오기
        getNoteData();


        // 3. 손글씨용 터치 리스너를 붙이기.
        viewTouchPaint.setPaintTouchListener(); // 손글씨용 터치 리스너

        // 4. TouchImageView size가 결정되었을 때 프로세스가 시작된다.
        ViewTreeObserver viewTree = viewTouchPaint.getViewTreeObserver();
        viewTree.addOnGlobalLayoutListener(touchViewPaint_OnGlobalLayoutLIstener);

        // 5. 노트배경
        // 2015-05-22 윤동수 수정: 배경을 직접 캔버스에 그리지 않고 drawable을 사용한다.
        int bt_type = getIntent().getIntExtra("bg_type", note.BACKGROUND);
        note.BACKGROUND=bt_type;
        switch (bt_type)
        {
            case 0:
                viewTouchPaint.setBackgroundResource(R.drawable.note_line_500); // 줄노트
                break;
            case 1:
                viewTouchPaint.setBackgroundResource(R.drawable.note_clean_500); // 무지노트
                break;
            case 2:
                viewTouchPaint.setBackgroundResource(R.drawable.note_clean_500); // 무지노트
                break;
            default:
                viewTouchPaint.setBackgroundResource(R.drawable.note_line_500); // 줄노트
                break;
        }


        // 6. 기본 글쓰기 설정
        mSize = Pref.getPenSize(this,20);

        //화면의 상단에 선택한 색상을 표시.
        displayPaintProperty();




    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //Log.e(TAG,"onWindowFocusChanged");
        super.onWindowFocusChanged(hasFocus);



    }

    private void getNoteData() {
        try
        {

            Intent i = getIntent();
            int noteNo = i.getIntExtra("NOTE_NO", 0);
            if (noteNo > 0) {
                note = noteManager.getNote(noteNo);

                if(note.TITLE==null || note.TITLE.equals("")==true)
                    note.TITLE = "제목 없음";
                textViewTitle.setText(note.TITLE);

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
            buttonScanOnClickProcess();
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
        if(theString.contains("ZMIN")==true)
        {
            viewTouchPaint.zoomInBitmap();

            Log.e(TAG,"ZMIN");
            strBuffer = new StringBuffer();
        }
        else if(theString.contains("ZMOT")==true)
        {
            viewTouchPaint.zoomOutBitmap();

            Log.e(TAG,"ZMOT");
            //viewTouchPaint.zoomInBitmap(2.0f);
            strBuffer = new StringBuffer();
        }
        else if(theString.contains("ERSE")==true)
        {
            setEraserMode();

            Log.e(TAG,"ERSE");
            //viewTouchPaint.zoomInBitmap(2.5f);
            //viewTouchPaint.zoomResetBitmap();
            strBuffer = new StringBuffer();
        }
        else if(theString.contains("SCRL")==true)
        {
            Log.e(TAG,"SCRL");
            strBuffer = new StringBuffer();

            setScrollMode();
        }
        else if(theString.contains("IDLE")==true)
        {
            setDrawPenMode();

            Log.e(TAG,"IDLE");
            strBuffer = new StringBuffer();
        }
    }

    public void buttonScroll_OnClick(View v)
    {
        //=====================================================
        // 1. 줌스크롤용 터치리스너 붙이기
        //스크롤 버튼이 눌렸을 경우
        //스크롤 버튼을 제외한 나머지 버튼들을 비활성화인 false 상태로 만듦
        if (scrollSelected==false) {
            setScrollMode();

        }
        //=====================================================
        // 2. 손글씨용 터치리스너 붙이기
        //스크롤 버튼이 한번 더 눌렸을 경우
        //스크롤 이외의 버튼을 활성화인 true를 해줌
        else {
            Log.i("scrollBtn", "unclicked.");
            textBtn.setEnabled(true);
            penBtn.setEnabled(true);
            eraserBtn.setEnabled(true);
            undoBtn.setEnabled(true);
            alarmBtn.setEnabled(true);
//                        scrollBtn.setEnabled(true);
            textBtn.invalidate();
            penBtn.invalidate();
            eraserBtn.invalidate();
            undoBtn.invalidate();
            alarmBtn.invalidate();
//                        scrollBtn.invalidate();

            // 손글씨용 터치리스너 붙이기
            viewTouchPaint.setPaintTouchListener();

            // 플래그 설정
            scrollSelected = false;
            /*paintboard.updatePaintProperty(mColor, mSize);
            displayPaintProperty();*/
        }
    }

    /**
     * 스크롤 모드로 전환한다.
     */
    private void setScrollMode() {
        Log.i("scrollBtn", "clicked.");
        textBtn.setEnabled(false);
        penBtn.setEnabled(false);
        eraserBtn.setEnabled(false);
        undoBtn.setEnabled(false);
        alarmBtn.setEnabled(false);
//            scrollBtn.setEnabled(false);
        textBtn.invalidate();
        penBtn.invalidate();
        eraserBtn.invalidate();
        undoBtn.invalidate();
        alarmBtn.invalidate();
//                        scrollBtn.invalidate();
        // 줌스크롤용 터치리스너 붙이기
        viewTouchPaint.setScrollTouchListener();

        // 플래그 변경
        scrollSelected = true;
    }

    /**
     * 저장 버튼 클릭시.
     * @param v
     */
    public void buttonSave_OnClick(View v)
    {
        // DB에 저장
        saveNote(true);
    }

    /**
     * 노트를 DB에 저장한다.
     * 변경된 사항이 없으면 저장하지 않는다.
     * @param force
     * true이면 변경사항이 없어도 DB저장한다.
     * false이면 변경사항이 있을 때에만 DB에 저장한다.
     */
    private void saveNote(boolean force) {
        // 변경된 사항이 없으면 DB에 저장하지 않는다.
        if(force==false && viewTouchPaint.getUndo().size()<=1)
            return;

        if(note.TITLE==null || note.TITLE.equals("")==true)
            note.TITLE = "제목 없음";
        note.NOTE_DATA = bitmapWrite;//.copy(Bitmap.Config.ARGB_8888,false);
        note.thumbnail = new Thumbnail(note.NOTE_DATA);
        noteManager.saveNoteData(note);
        Toast.makeText(this,"저장 됨.",Toast.LENGTH_SHORT).show();
    }

    public void buttonBack_OnClick(View v)
    {
        // 1.저장하지 않고 나갈때는 물어보기 팝업
        // 2. activity 종료
        finish();
    }

    public void buttonUndo_OnClick(View v)
    {
        viewTouchPaint.undo();
    }

    public void buttonPen_OnClick(View v)
    {
        //펜 색상 선택 팔레트를 눌렀을 때
        PenPaletteActivity.colorlistener = new PenPaletteActivity.OnColorSelectedListener() {

            public void onColorSelected(int color) {
                oldColor = mColor;
                mColor = color;

                //최근 사용한 색상을 저장
                color_save.add(mColor);

//                recentcoloradapter.recent_color_list = color_save;
//                displayRecentColor();
                viewTouchPaint.setPaintAlpha(Pref.getAlpha(context, 0));

                //선택되어진 색상을 적용한다.
                viewTouchPaint.updatePaintProperty(mColor, mSize);
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

                viewTouchPaint.setPaintAlpha(Pref.getAlpha(context, 0));

                //선택되어진 색상을 적용한다.
                viewTouchPaint.updatePaintProperty(mColor, mSize);
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

                viewTouchPaint.setPaintAlpha(Pref.getAlpha(context,100));

                //선택되어진 색상을 적용한다.
                viewTouchPaint.updatePaintProperty(mColor, mSize);
                //화면의 좌측 상단에 선택한 색상을 표시한다.
                displayPaintProperty();
            }
        };
        //완료 버튼을 눌렀을 때
        // 2015-05-19 윤동수 - 이함수를 호출하지 않아도 될것 같은데?
        /*PenPaletteActivity.completelistener = new PenPaletteActivity.OnCompleteSelectedListener() {
            public void onCompleteSelected() {
                mColor = oldColor;
                mSize = oldSize;

//                //최근 사용한 색상을 저장
//                color_save.add(mColor);
//                Toast.makeText(MemoWriteActivity.this, "색상" + mColor, Toast.LENGTH_SHORT).show();
//                displayRecentColor();

                //색상과 굵기를 적용한다.
                viewTouchPaint.updatePaintProperty(mColor, mSize);
                //화면의 좌측상단에 선택한 색상과 굵기를 표시한다.
                displayPaintProperty();
            }
        };*/
        //Log.e("!!!!!!!!!!","펜 선택 color 값"+mColor);
        //Log.e("!!!!!!!!!!", "펜 선택 size 값" + mSize);

        //펜 색상, 굵기변경 팔레트 띄우기
        Intent intent = new Intent(getApplicationContext(), PenPaletteActivity.class);
        startActivityForResult(intent, REQUEST_PEN_SIZE);
    }

    /**
     * 현재 선택된 색상을 표시
     */
    private void displayPaintProperty() {
        GradientDrawable bgShape = (GradientDrawable)colorBtn.getBackground();
        bgShape.setColor(mColor);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_DRAW_TEXT)
        {
            if(data==null)
                return;
            int isCancel = data.getIntExtra("isCancel",1);
            if(isCancel==0) {
                /*int count = 0;
                DisplayMetrics outMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

                float density = outMetrics.density;*/

                // ok버튼 눌렀을 때.
                //String text[] = data.getStringExtra("text").toString().split("\n");
                String text = data.getStringExtra("text").toString();
                float x = data.getFloatExtra("x", 0.0f);
                float y = data.getFloatExtra("y", 0.0f);
                Log.i(TAG, "text:" + text);


                viewTouchPaint.drawText(text, x, y);
            }
        }
        else if(requestCode == REQUEST_PEN_SIZE){
            if(data.getIntExtra("p_size",0) != 0) {
                setDrawPenMode();   // 펜쓰기 상태로 전환
            }
        }
        else if(requestCode == REQUEST_ERASER_SIZE){
            if(data.getIntExtra("e_size",0) != 0){
                setEraserMode();    // 지우개 상태로 전환
            }
        }
        else if(requestCode == REQUEST_INPUT_TITLE){
            Toast.makeText(MemoWriteActivity2.this, "종료값은 들어옴", Toast.LENGTH_SHORT).show();
            MemoWriteActivity2.this.finish();
        }

    }

    private void setDrawPenMode() {
        //int pen_size = data.getIntExtra("p_size", 0);
        /*Log.i("펜의 사이즈", "" + pen_size);
        //oldSize = mSize;
        mSize = pen_size;*/
        mSize = Pref.getPenSize(this,20);

        //Toast.makeText(MemoWriteActivity2.this, "펜 사이즈 넘어왔네~" + pen_size, Toast.LENGTH_SHORT).show();

        /*SharedPreferences sp = getSharedPreferences("current_p_size", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("p_size_value",pen_size);
        editor.commit();*/


        viewTouchPaint.updatePaintProperty(mColor, mSize);
        //화면의 좌측 상단에 선택한 색상을 표시한다.
        displayPaintProperty();
    }

    private void setEraserMode() {
        // 2015-05-23 윤동수 주석처리
                /*int eraser_size = data.getIntExtra("e_size",0);



                //oldSize = mSize;
                mSize = eraser_size;
                Toast.makeText(MemoWriteActivity2.this, "지우개 사이즈 넘어왔네~" + eraser_size, Toast.LENGTH_SHORT).show();

                SharedPreferences sp2 = getSharedPreferences("current_e_size", MODE_PRIVATE);
                SharedPreferences.Editor editor2 = sp2.edit();
                editor2.putInt("e_size_value",eraser_size);
                editor2.commit();*/

        mSize = Pref.getEraserSize(context, 50);

        //펜 사이즈, 크기  저장
        oldColor = mColor;

        viewTouchPaint.setEraserPaint(mSize);

        //화면의 좌측 상단에 선택한 사이즈를 표시한다.
        displayPaintProperty();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        viewTouchPaint.getUndo().clearList();
    }

    /**
     *
     * @param v
     */
    public void buttonEraser_OnClick(View v)
    {
        //지우개 크기 선택 화면 띄우기
        Intent intent = new Intent(getApplicationContext(), EraserPaletteActivity.class);
        startActivityForResult(intent, REQUEST_ERASER_SIZE);

        /*isEraserMode = !isEraserMode;
        // 지우개 버튼이 선택되면
        if (isEraserMode==false) {
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
        }*/
    }

    /**
     * 자판 클릭
     * @param v
     */
    public void buttonText_OnClick(View v)
    {
        textSelected = !textSelected;
        if (textSelected==true) {
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

            // 안내 내보내기
            Toast.makeText(context, "글자의 위치를 터치하세요.", Toast.LENGTH_SHORT).show();

            //화면이 터치 되었을때
            viewTouchPaint.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 1. 스케일과 offset을 가져와서 touchX,touchY값 맞추기
                    Matrix matrix = ((TouchImageView)v).getImageMatrix();
                    PointF pointCanvas = Utils.TransformTouchPointToCanvasPoint(matrix, event.getX(), event.getY());

                    if(event.getAction()==MotionEvent.ACTION_DOWN) {
                        Intent intent = new Intent(getApplicationContext(), TextDialog.class);
                        intent.putExtra("x", pointCanvas.x);
                        intent.putExtra("y", pointCanvas.y);
                        startActivityForResult(intent, REQUEST_DRAW_TEXT);
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

            /*for_alpha = getSharedPreferences("alpha_value", MODE_PRIVATE);
            alpha_temp_value = for_alpha.getInt("alpha_value_is", 0);
            viewTouchPaint.setPaintAlpha(alpha_temp_value);*/
            viewTouchPaint.setPaintAlpha(Pref.getAlpha(context, 0));
            viewTouchPaint.setPaintTouchListener();
            viewTouchPaint.updatePaintProperty(mColor, mSize);
            displayPaintProperty();
        }
    }

    public void textViewTitle_OnClick(View v)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //=======================================
        // 1. 제목
        alert.setTitle("제목");
        //=======================================
        // 2. 문장
        alert.setMessage("제목을 입력해주세요");
        //=======================================
        // 3. EditBox설정
        final EditText input = new EditText(this);
        input.setText(note.TITLE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alert.setView(input); // uncomment this line
        //=======================================
        // 4. 저장 버튼
        alert.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                note.TITLE = input.getText().toString();
                textViewTitle.setText(note.TITLE);
            }
        });
        //=======================================
        // 5. 취소 버튼
        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }
}
