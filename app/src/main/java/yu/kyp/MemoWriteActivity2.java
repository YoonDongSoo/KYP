package yu.kyp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import yu.kyp.bluno.BlunoLibrary;
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
    Date curDate = new Date();
    Uri eventUriString;
    Calendar mCalendar = Calendar.getInstance();
    static int select_memo_year;
    static int select_memo_month;
    static int select_memo_day;
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
    Button scrollBtn;
    private boolean scrollSelected;
    private Bitmap bitmapBackground;
    private Canvas canvasBackground;
    private int mSize = 2;
    private SharedPreferences for_alpha;
    private int mColor;
    private int oldColor;
    private ArrayList<Integer> color_save = new ArrayList<Integer>();
    private int alpha_temp_value;
    private boolean text_flag;
    private static SharedPreferences memo_title;

    private ViewTreeObserver.OnGlobalLayoutListener touchViewPaint_OnGlobalLayoutLIstener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int height = viewTouchPaint.getHeight();
            int width = viewTouchPaint.getWidth();
            Log.e(TAG, String.format("width:%d height:%d", width, height));

            //=========================================
            // 1. 배경 비트맵&캔버스 설정
            bitmapBackground = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            canvasBackground = new Canvas(bitmapBackground);
            canvasBackground.drawColor(Color.WHITE);

            //=========================================
            // 2. width,height로 빈 비트맵을 만들거나
            //    기존 데이터를 DB에서 가져온다.
            if (note.NOTE_DATA == null) {
                bitmapWrite = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                canvasWrite = new Canvas(bitmapWrite);
                canvasWrite.drawBitmap(bitmapBackground, 0, 0, null);
            } else {
                bitmapWrite = note.NOTE_DATA.copy(Bitmap.Config.ARGB_8888, true);    // mutable로 copy해야 함.
                canvasWrite = new Canvas(bitmapWrite);
            }


            // 3. 글쓰기 비트맵&캔버스 설정
            viewTouchPaint.setImageBitmap(bitmapWrite);
            viewTouchPaint.setBackgroundBitmap(bitmapBackground);
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
        scrollBtn = (Button) findViewById(R.id.buttonScroll);


        // 2. 노트데이터 불러오기
        getNoteData();

        // 3. 손글씨용 터치 리스너를 붙이기.
        viewTouchPaint = (TouchImageView) findViewById(R.id.touchViewPaint);
        viewTouchPaint.setPaintTouchListener(); // 손글씨용 터치 리스너

        // 4. TouchImageView size가 결정되었을 때 프로세스가 시작된다.
        ViewTreeObserver viewTree = viewTouchPaint.getViewTreeObserver();
        viewTree.addOnGlobalLayoutListener(touchViewPaint_OnGlobalLayoutLIstener);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //Log.e(TAG,"onWindowFocusChanged");
        super.onWindowFocusChanged(hasFocus);


    }

    private void getNoteData() {
        try {

            Intent i = getIntent();
            int noteNo = i.getIntExtra("NOTE_NO", 0);
            if (noteNo > 0) {
                note = noteManager.getNote(noteNo);
                //paintboard.undo.addList(note.NOTE_DATA);;

            } else {
                note = new Note();
            }
        } catch (Exception e) {
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
        switch (theConnectionState) {                                            //Four connection state
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
        Log.d(TAG, "onSerialReceived:" + theString);
        strBuffer.append(theString);

        // 임시로 주석처리
        if (theString.contains("ZOM01") == true) {
            viewTouchPaint.zoomInBitmap();
            strBuffer = new StringBuffer();
        } else if (theString.contains("ZOM02") == true) {
            //viewTouchPaint.zoomOutBitmap();
            strBuffer = new StringBuffer();
        } else if (theString.contains("ZOM03") == true) {
            //viewTouchPaint.zoomResetBitmap();
            strBuffer = new StringBuffer();
        }
    }

    public void buttonScroll_OnClick(View v) {

        scrollSelected = !scrollSelected;

        //=====================================================
        // 1. 줌스크롤용 터치리스너 붙이기
        //스크롤 버튼이 눌렸을 경우
        //스크롤 버튼을 제외한 나머지 버튼들을 비활성화인 false 상태로 만듦
        if (scrollSelected) {
            Log.i("scrollBtn", "clicked.");
            penBtn.setEnabled(false);
            eraserBtn.setEnabled(false);
            undoBtn.setEnabled(false);
            alarmBtn.setEnabled(false);
//            scrollBtn.setEnabled(false);

            penBtn.invalidate();
            eraserBtn.invalidate();
            undoBtn.invalidate();
            alarmBtn.invalidate();
//                        scrollBtn.invalidate();
            // 줌스크롤용 터치리스너 붙이기
            viewTouchPaint.setScrollTouchListener();

        }
        //=====================================================
        // 2. 손글씨용 터치리스너 붙이기
        //스크롤 버튼이 한번 더 눌렸을 경우
        //스크롤 이외의 버튼을 활성화인 true를 해줌
        else {
            Log.i("scrollBtn", "unclicked.");
            penBtn.setEnabled(true);
            eraserBtn.setEnabled(true);
            undoBtn.setEnabled(true);
            alarmBtn.setEnabled(true);
//                        scrollBtn.setEnabled(true);

            penBtn.invalidate();
            eraserBtn.invalidate();
            undoBtn.invalidate();
            alarmBtn.invalidate();
//                        scrollBtn.invalidate();

            // 손글씨용 터치리스너 붙이기
            viewTouchPaint.setPaintTouchListener();

            /*paintboard.updatePaintProperty(mColor, mSize);
            displayPaintProperty();*/
        }
    }

    /**
     * 저장 버튼 클릭시.
     *
     * @param v
     */
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
        if (viewTouchPaint.getUndo().size() <= 1)
            return;

        if (note.TITLE == null || note.TITLE.equals("") == true)
            note.TITLE = "제목 없음";
        note.NOTE_DATA = bitmapWrite.copy(Bitmap.Config.ARGB_8888, false);
        note.thumbnail = new Thumbnail(note.NOTE_DATA);
        noteManager.saveNoteData(note);
        Toast.makeText(this, "저장 됨.", Toast.LENGTH_SHORT).show();
    }

    public void buttonBack_OnClick(View v) {
        // 1.저장하지 않고 나갈때는 물어보기 팝업
        // 2. activity 종료
        finish();
    }

    public void buttonUndo_OnClick(View v) {
        viewTouchPaint.undo();
    }

    public void buttonPen_OnClick(View v) {
        //펜 색상 선택 팔레트를 눌렀을 때
        PenPaletteActivity.colorlistener = new PenPaletteActivity.OnColorSelectedListener() {

            public void onColorSelected(int color) {
                oldColor = mColor;
                mColor = color;

                //최근 사용한 색상을 저장
                color_save.add(mColor);
                Toast.makeText(MemoWriteActivity2.this, "색상" + mColor, Toast.LENGTH_SHORT).show();

//                recentcoloradapter.recent_color_list = color_save;
//                displayRecentColor();
                for_alpha = getSharedPreferences("alpha_value", MODE_PRIVATE);
                alpha_temp_value = for_alpha.getInt("alpha_value_is", 0);
                viewTouchPaint.setPaintAlpha(alpha_temp_value);

                //선택되어진 색상을 적용한다.
                viewTouchPaint.updatePaintProperty(mColor, mSize);
                //화면의 좌측 상단에 선택한 색상을 표시한다.
                displayPaintProperty();
            }
        };
        //최근 사용한 색 선택 팔레트를 눌렀을 때
        PenPaletteActivity.recentcolorlistener = new PenPaletteActivity.OnRecentColorSelectedListener() {
            public void onRecentColorSelected(int color) {
                mColor = color;
                oldColor = mColor;

                //최근 사용한 색상을 저장
                color_save.add(mColor);

                for_alpha = getSharedPreferences("alpha_value", MODE_PRIVATE);
                alpha_temp_value = for_alpha.getInt("alpha_value_is", 0);
                viewTouchPaint.setPaintAlpha(alpha_temp_value);

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

                for_alpha = getSharedPreferences("alpha_value", MODE_PRIVATE);
                alpha_temp_value = for_alpha.getInt("alpha_value_is", 0);

                viewTouchPaint.setPaintAlpha(100);

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
        Log.e("!!!!!!!!!!", "펜 선택 color 값" + mColor);
        Log.e("!!!!!!!!!!", "펜 선택 size 값" + mSize);

        //펜 색상, 굵기변경 팔레트 띄우기
        Intent intent = new Intent(getApplicationContext(), PenPaletteActivity.class);
        startActivityForResult(intent, REQUEST_PEN_SIZE);
    }

    /**
     * 현재 선택된 색상을 표시
     */
    private void displayPaintProperty() {
        GradientDrawable bgShape = (GradientDrawable)colorBtn.getBackground();
        //bgShape.getPaint().setColor(mColor);
        bgShape.setColor(mColor);
        //colorBtn.setBackgroundColor(mColor);
        //sizetextview.setText("Size : " + mSize);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);                    //onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DRAW_TEXT) {
            int isCancel = data.getIntExtra("isCancel", 0);
            if (isCancel == 0) {
                int count = 0;
                DisplayMetrics outMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

                float density = outMetrics.density;

                // ok버튼 눌렀을 때.
                String text[] = data.getStringExtra("text").toString().split("\n");
                float x = data.getFloatExtra("x", 0.0f);
                float y = data.getFloatExtra("y", 0.0f);
                Log.i(TAG, "text:" + text);


                for (count = 0; count < text.length; count++) {
                    //drawText를 위해 y의 위치를 옮겨줘야함(하지않을 경우 같은 자리에 써짐)
                    y += 60.0f;
                    viewTouchPaint.drawText(text[count], x, y);
                }
                text_flag = true;
            } else {
                // 취소 버튼 눌렀을 때
                Log.i(TAG, "취소 버튼 눌렀네~");
                text_flag = true;
            }
        }
        if (requestCode == REQUEST_PEN_SIZE) {
            if (data.getIntExtra("p_size", 0) != 0) {
                int pen_size = data.getIntExtra("p_size", 0);
                Log.i("펜의 사이즈", "" + pen_size);
                //oldSize = mSize;
                mSize = pen_size;

                Toast.makeText(MemoWriteActivity2.this, "펜 사이즈 넘어왔네~" + pen_size, Toast.LENGTH_SHORT).show();

                SharedPreferences sp = getSharedPreferences("current_p_size", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("p_size_value", pen_size);
                editor.commit();


                viewTouchPaint.updatePaintProperty(mColor, pen_size);
                //화면의 좌측 상단에 선택한 색상을 표시한다.
                displayPaintProperty();

            }
        }
        if (requestCode == REQUEST_ERASER_SIZE) {
            if (data.getIntExtra("e_size", 0) != 0) {
                int eraser_size = data.getIntExtra("e_size", 0);


                //oldSize = mSize;
                mSize = eraser_size;
                Toast.makeText(MemoWriteActivity2.this, "지우개 사이즈 넘어왔네~" + eraser_size, Toast.LENGTH_SHORT).show();

                SharedPreferences sp2 = getSharedPreferences("current_e_size", MODE_PRIVATE);
                SharedPreferences.Editor editor2 = sp2.edit();
                editor2.putInt("e_size_value", eraser_size);
                editor2.commit();

                //펜 사이즈, 크기  저장
                oldColor = mColor;

                viewTouchPaint.setEraserPaint(mSize);

                //화면의 좌측 상단에 선택한 사이즈를 표시한다.
                displayPaintProperty();
            }
        }
        if (requestCode == REQUEST_INPUT_TITLE) {
            Toast.makeText(MemoWriteActivity2.this, "종료값은 들어옴", Toast.LENGTH_SHORT).show();
            MemoWriteActivity2.this.finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        onResumeProcess();
        //buttonScanOnClickProcess();
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

    public void buttonAlarm_OnClick(View v) {
        Toast.makeText(MemoWriteActivity2.this, "알람 눌렸다", Toast.LENGTH_SHORT).show();

        Date curDate = new Date();
        String curDateStr = String.valueOf(curDate.getTime());
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);

        setCalendar();
        setMemoDate();

        new DatePickerDialog(
                MemoWriteActivity2.this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setCalendar() {
        Toast.makeText(MemoWriteActivity2.this, "캘린더버튼이야", Toast.LENGTH_SHORT).show();
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();

        calendar.setTime(date);

        new TimePickerDialog(
                MemoWriteActivity2.this,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        ).show();


        Date curDate = new Date();
        mCalendar.setTime(curDate);

        int year = mCalendar.get(Calendar.YEAR);
        int monthOfYear = mCalendar.get(Calendar.MONTH);
        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        String monthStr = String.valueOf(monthOfYear + 1);
        if (monthOfYear < 9) {
            monthStr = "0" + monthStr;
        }

        String dayStr = String.valueOf(dayOfMonth);
        if (dayOfMonth < 10) {
            dayStr = "0" + dayStr;
        }

        int hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);

        String hourStr = String.valueOf(hourOfDay);
        if (hourOfDay < 10) {
            hourStr = "0" + hourStr;
        }

        String minuteStr = String.valueOf(minute);
        if (minute < 10) {
            minuteStr = "0" + minuteStr;
        }

    }

    private void setMemoDate() {
        Date date = new Date();

        //Calendar calendar = Calendar.getInstance();
        mCalendar.setTime(date);

        int year = mCalendar.get(Calendar.YEAR);
        int monthOfYear = mCalendar.get(Calendar.MONTH);
        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        String monthStr = String.valueOf(monthOfYear + 1);
        if (monthOfYear < 9) {
            monthStr = "0" + monthStr;
        }

        String dayStr = String.valueOf(dayOfMonth);
        if (dayOfMonth < 10) {
            dayStr = "0" + dayStr;
        }

        int hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);

        String hourStr = String.valueOf(hourOfDay);
        if (hourOfDay < 10) {
            hourStr = "0" + hourStr;
        }

        String minuteStr = String.valueOf(minute);
        if (minute < 10) {
            minuteStr = "0" + minuteStr;
        }

    }

    /**
     * 날짜 설정 리스너
     */
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            mCalendar.set(year, monthOfYear, dayOfMonth);

            select_memo_year = year-1900;
            select_memo_month = monthOfYear;
            select_memo_day = dayOfMonth;
            Toast.makeText(MemoWriteActivity2.this, "날짜 설정이 눌렸음" + select_memo_year
                    + "," + select_memo_month + ","
                    + select_memo_day, Toast.LENGTH_SHORT).show();

            String monthStr = String.valueOf(monthOfYear + 1);
            if (monthOfYear < 9) {
                monthStr = "0" + monthStr;
            }

            String dayStr = String.valueOf(dayOfMonth);
            if (dayOfMonth < 10) {
                dayStr = "0" + dayStr;
            }

        }
    };

    /**
     * 시간 설정 리스너
     */
    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hour_of_day, int minute) {
            String title = note.TITLE;
            Log.e(TAG, "title:" + title);
            Toast.makeText(MemoWriteActivity2.this, "시간 설정이 눌렸음", Toast.LENGTH_SHORT).show();
            mCalendar.set(Calendar.HOUR_OF_DAY, hour_of_day);
            mCalendar.set(Calendar.MINUTE, minute);

            String hourStr = String.valueOf(hour_of_day);
            if (hour_of_day < 10) {
                hourStr = "0" + hourStr;
            }

            String minuteStr = String.valueOf(minute);
            if (minute < 10) {
                minuteStr = "0" + minuteStr;
            }

            ContentValues eventValues = new ContentValues();

            if(Build.VERSION.SDK_INT >= 8)
                eventUriString  = Uri.parse("content://com.android.calendar/events");
            else
                eventUriString  = Uri.parse("content://calendar/events");

            eventValues.put("calendar_id", 1); // id, We need to choose from our mobile for primary its 1
            eventValues.put("title", title);
            eventValues.put("description", "");
            eventValues.put("eventLocation", "");

            //hour 값 - 9 = 한국 시간
            eventValues.put("dtstart", Date.UTC(select_memo_year, select_memo_month, select_memo_day, hour_of_day-9, minute, 00));
            eventValues.put("dtend", Date.UTC(select_memo_year, select_memo_month, select_memo_day, hour_of_day-9, minute, 00));
            eventValues.put("eventTimezone", TimeZone.getDefault().getID());
            eventValues.put("eventStatus", 1); // This information is sufficient for most entries tentative (0), confirmed (1) or canceled (2):
            eventValues.put("hasAlarm", 1); // 0 for false, 1 for true

            //캘린더에 넣기
            getContentResolver().insert(eventUriString, eventValues);
        }
    };
}

