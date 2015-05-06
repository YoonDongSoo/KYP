
package yu.kyp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

/**
 * 선굵기를 선택하는 대화상자용 액티비티
 *
 * @author Mike
 *
 */
public class PenPaletteActivity extends Activity {

    private static final String TAG = ColorPickerDialog.class.getSimpleName();
    GridView colorgrid;
    GridView sizegrid;
    Button othersBtn;
    Button selectBtn;
    PenDataAdapter penadapter;
    ColorDataAdapter coloradapter;
    Paint mPaint;

    public static OnPenSelectedListener penlistener;
    public static OnColorSelectedListener colorlistener;
    public static OnCompleteSelectedListener completelistener;

    public ColorPickerDialog.OnColorChangedListener colorChangedListener = new ColorPickerDialog.OnColorChangedListener() {
        @Override
        public void colorChanged(int color) {
            if (PenPaletteActivity.colorlistener != null) {
                PenPaletteActivity.colorlistener.onColorSelected(color);
            }

            finish();
            Log.d(TAG, "color:" + color);
        }
    };

    public interface OnPenSelectedListener {
        public void onPenSelected(int pen);
    }

    public interface OnColorSelectedListener{
        public void onColorSelected(int color);

    }
//    public interface OnCacleSelectedListener{
//        public void onCancleSelected();
//    }
    public interface OnCompleteSelectedListener{
        public void onCompleteSelected();
    }

    /**
     * 펜 색상과 두께 선택을 위한 팔레트
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pendialog);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

        mPaint.setXfermode(null);
        mPaint.setAlpha(0xFF);

        this.setTitle("선굵기 및 색상 선택");

        //색상 그리드
        colorgrid = (GridView) findViewById(R.id.colorGrid);
        //사이즈 그리드
        sizegrid = (GridView) findViewById(R.id.sizeGrid);

        othersBtn = (Button) findViewById(R.id.othersBtn);
        selectBtn = (Button) findViewById(R.id.selectBtn);

        colorgrid.setColumnWidth(14);
        colorgrid.setBackgroundColor(Color.GRAY);
        colorgrid.setVerticalSpacing(4);
        colorgrid.setHorizontalSpacing(4);

        //컬러데이터어댑터와 연결
        coloradapter = new ColorDataAdapter(this);
        colorgrid.setAdapter(coloradapter);
        colorgrid.setNumColumns(coloradapter.getNumColumns());

        sizegrid.setColumnWidth(14);
        sizegrid.setBackgroundColor(Color.GRAY);
        sizegrid.setVerticalSpacing(4);
        sizegrid.setHorizontalSpacing(4);

        //펜데이터어댑터와 연결
        penadapter = new PenDataAdapter(this);
        sizegrid.setAdapter(penadapter);
        sizegrid.setNumColumns(penadapter.getNumColumns());

//        closeBtn.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//
//                // dispose this activity
//                finish();
//            }
//        });

        //다른색 버튼을 눌렀을 때
        othersBtn.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                System.out.println("click otherBtn");
                //컬러피커 다이얼로그 생성
                ColorPickerDialog dlg =  new ColorPickerDialog(PenPaletteActivity.this,colorChangedListener, mPaint.getColor());
                //컬러피커 다이얼로그 show
                dlg.show();

            }
        });

        //완료 버튼을 눌렀을 때
        selectBtn.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                System.out.println("click selectBtn");

                //펜팔레트 액티비티 종료
                finish();
            }
        });
    }
//    public void colorChanged(int color) {
//        System.out.println("color : " + color);
//        mPaint.setColor(color);
//    }
}

/**
 * Adapter for Pen Data
 *
 * @author Mike
 */
class PenDataAdapter extends BaseAdapter {
    /**
     * Application Context
     */
    Context mContext;
    int temp_size;


    /**
     * Pens defined
     * 펜의 사이즈 선택을 위한 Int형 사이즈 배열 선언
     */
    public static final int [] pens = new int[] {
            1,2,3,4,5,
            6,7,8,9,10,
            11,13,15,17,20
    };

    int rowCount;
    int columnCount;


    public PenDataAdapter(Context context) {
        super();

        mContext = context;

        //3*5 그리드
        rowCount = 3;
        columnCount = 5;

    }

    /**
     * 펜 사이즈를 선택하는 그리드뷰에서
     * 선택한 부분의 column 값을 리턴
     * @return
     */
    public int getNumColumns() {
        return columnCount;
    }

    /**
     * 펜 사이즈를 선택하는 그리드뷰에서
     * 펜 사이즈의 갯수를 리턴
     * @return
     */
    public int getCount() {
        return rowCount * columnCount;
    }

    /**
     * 펜 사이즈를 선택하는 그리드뷰에서
     * 펜 사이즈의 포지션을 리턴
     * @param position
     * @return
     */
    public Object getItem(int position) {
        return pens[position];
    }

    /**
     * 펜 사이즈를 선택하는 그리드뷰에서
     * 선택된 값을 확인
     * @param position
     * @return
     */
    public long getItemId(int position) {
        return 0;
    }

    /**
     * 펜 사이즈를 선택하는 그리드뷰를
     * 만드는 함수
     * @param position
     * @param view
     * @param group
     * @return
     */
    public View getView(int position, View view, ViewGroup group) {

        //Log.d("PenDataAdapter", "getView(" + position + ") called.");

        // calculate position
        int rowIndex = position / rowCount;
        int columnIndex = position % rowCount;
        //Log.d("PenDataAdapter", "Index : " + rowIndex + ", " + columnIndex);

        //펜 사이즈를 나타낼 그리드뷰 생성
        GridView.LayoutParams params = new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT,
                GridView.LayoutParams.MATCH_PARENT);

        //펜 사이즈를 나타낼 가로, 세로 높이 지정
        int areaWidth = 10;
        int areaHeight = 20;

        Bitmap penBitmap = Bitmap.createBitmap(areaWidth, areaHeight, Bitmap.Config.ARGB_8888);
        Canvas penCanvas = new Canvas();
        penCanvas.setBitmap(penBitmap);

        Paint mPaint = new Paint();
        //펜 사이즈 선택 그리드뷰의 배경 색
        mPaint.setColor(Color.WHITE);
        penCanvas.drawRect(0, 0, areaWidth, areaHeight, mPaint);

        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth((float)pens[position]);
        penCanvas.drawLine(0, areaHeight/2, areaWidth-1, areaHeight/2, mPaint);
        BitmapDrawable penDrawable;
        penDrawable = new BitmapDrawable(mContext.getResources(), penBitmap);

        // create a Button with the color
        Button aItem = new Button(mContext);
        aItem.setText(" ");
        aItem.setLayoutParams(params);
        aItem.setPadding(4, 4, 4, 4);
        aItem.setBackgroundDrawable(penDrawable);
        aItem.setHeight(64);
        aItem.setTag(pens[position]);

        //펜 사이즈 그리드뷰에서
        //하나의 값을 선택(클릭)하였을 때
        aItem.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (PenPaletteActivity.penlistener != null) {
                    PenPaletteActivity.penlistener.onPenSelected(((Integer)v.getTag()).intValue());
//                    Log.i("pen thickness","clicked");
                }

//                ((PenPaletteDialog)mContext).finish();
            }
        });
        //Log.d("aItem ","getView(" +aItem+") called");

        //선택한 것을 리턴
        return aItem;
    }
}

/**
 * Adapter for Color Data
 *
 * @author Mike
 */
class ColorDataAdapter extends BaseAdapter {

    /**
     * Application Context
     */
    Context mContext;

    /**
     * Colors defined
     */

    //펜의 색상 선택을 위한 Int형 색상 배열 생성
    public static final int [] colors = new int[] {
            0xff000000,0xff00007f,0xff0000ff,0xff007f00,0xff007f7f,0xff00ff00,0xff00ff7f,
            0xff00ffff,0xff7f007f,0xff7f00ff,0xff7f7f00,0xff7f7f7f,0xffff0000,0xffff007f,
            0xffff00ff,0xffff7f00,0xffff7f7f,0xffff7fff,0xffffff00,0xffffff7f,0xff4682b4
    };

    int rowCount;
    int columnCount;


    public ColorDataAdapter(Context context) {
        super();

        mContext = context;

        // create test data
        rowCount = 3;
        columnCount = 7;

    }

    /**
     * 펜 색상을 선택하는 그리드뷰에서
     * 선택한 부분의 column 값을 리턴
     * @return
     */
    public int getNumColumns() {
        return columnCount;
    }

    /**
     * 펜 색상을 선택하는 그리드뷰에서
     * 펜 색상의 갯수를 리턴
     * @return
     */
    public int getCount() {
        return rowCount * columnCount;
    }

    /**
     *펜 색상을 선택하는 그리드뷰에서
     * 펜 색상의 포지션을 리턴
     * @param position
     * @return
     */
    public Object getItem(int position) {
        return colors[position];
    }

    /**
     * 펜 색상을 선택하는 그리드뷰에서
     * 선택된 값을 확인
     * @param position
     * @return
     */
    public long getItemId(int position) {
        return 0;
    }

    /**
     * 펜 색상을 선택하는 그리드뷰를
     * 만드는 함수
     * @param position
     * @param view
     * @param group
     * @return
     */
    public View getView(int position, View view, ViewGroup group) {
        //Log.d("ColorDataAdapter", "getView(" + position + ") called.");

        // calculate position
        int rowIndex = position / rowCount;
        int columnIndex = position % rowCount;
        //Log.d("ColorDataAdapter", "Index : " + rowIndex + ", " + columnIndex);

        //펜 색상을 나타낼 그리드뷰 생성
        GridView.LayoutParams params = new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT,
                GridView.LayoutParams.MATCH_PARENT);

        // create a Button with the color
        Button aItem = new Button(mContext);
        aItem.setText(" ");
        aItem.setLayoutParams(params);
        aItem.setPadding(4, 4, 4, 4);
        aItem.setBackgroundColor(colors[position]);
        aItem.setHeight(64);
        aItem.setTag(colors[position]);

        //펜 색상 그리드뷰에서
        //하나의 값을 선택(클릭)하였을 때
        aItem.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (PenPaletteActivity.colorlistener != null) {
                    PenPaletteActivity.colorlistener.onColorSelected(((Integer) v.getTag()).intValue());
                }

//                ((PenPaletteDialog)mContext).finish();
            }
        });


        //선택한 것을 리턴
        return aItem;
    }
}