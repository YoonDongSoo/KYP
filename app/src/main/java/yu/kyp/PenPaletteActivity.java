
package yu.kyp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
//    Button closeBtn;
    Button othersBtn;
    Button selectBtn;
    PenDataAdapter penadapter;
    ColorDataAdapter coloradapter;
    Paint mPaint;
    int temp_color2;
    int temp_size2;

    public static OnPenSelectedListener penlistener;
    public static OnColorSelectedListener colorlistener;
//    public static OnCacleSelectedListener canclelistener;
    public static OnCompleteSelectedListener completelistener;

//    public ColorPickerDialog.OnColorChangedListener colorChangedListener = new ColorPickerDialog.OnColorChangedListener() {
//        @Override
//        public void colorChanged(int color) {
//            if (PenPaletteDialog.colorlistener != null) {
//                PenPaletteDialog.colorlistener.onColorSelected(color);
//            }
//
//            finish();
//            Log.d(TAG,"color:"+color);
//        }
//    };

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

        colorgrid = (GridView) findViewById(R.id.colorGrid);
        sizegrid = (GridView) findViewById(R.id.sizeGrid);
//        closeBtn = (Button) findViewById(R.id.closeBtn);
        othersBtn = (Button) findViewById(R.id.othersBtn);
        selectBtn = (Button) findViewById(R.id.selectBtn);

        colorgrid.setColumnWidth(14);                   //색상 선택을 위한
        colorgrid.setBackgroundColor(Color.GRAY);
        colorgrid.setVerticalSpacing(4);
        colorgrid.setHorizontalSpacing(4);

        coloradapter = new ColorDataAdapter(this);
        colorgrid.setAdapter(coloradapter);
        colorgrid.setNumColumns(coloradapter.getNumColumns());

        sizegrid.setColumnWidth(14);            //펜의 사이즈 선택을 위한
        sizegrid.setBackgroundColor(Color.GRAY);
        sizegrid.setVerticalSpacing(4);
        sizegrid.setHorizontalSpacing(4);

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

//        othersBtn.setOnClickListener(new OnClickListener(){
//            public void onClick(View v) {
//                System.out.println("click otherBtn");
//                ColorPickerDialog dlg =  new ColorPickerDialog(PenPaletteDialog.this,colorChangedListener, mPaint.getColor());
//                dlg.show();
//
//            }
//        });
        selectBtn.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                System.out.println("click selectBtn");

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
     */
    public static final int [] pens = new int[] {
            1,2,3,4,5,
            6,7,8,9,10,
            11,13,15,17,20
    };

    int rowCount;
    int columnCount;


    public void temp1(int size){


        temp_size = size;
    }

    public PenDataAdapter(Context context) {
        super();

        mContext = context;

        rowCount = 3;
        columnCount = 5;

    }

    public int getNumColumns() {
        return columnCount;
    }

    public int getCount() {
        return rowCount * columnCount;
    }

    public Object getItem(int position) {
        return pens[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup group) {

        //Log.d("PenDataAdapter", "getView(" + position + ") called.");

        // calculate position
        int rowIndex = position / rowCount;
        int columnIndex = position % rowCount;
        //Log.d("PenDataAdapter", "Index : " + rowIndex + ", " + columnIndex);

        GridView.LayoutParams params = new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT,
                GridView.LayoutParams.MATCH_PARENT);

        // create a Pen Image
        int areaWidth = 10;
        int areaHeight = 20;

        Bitmap penBitmap = Bitmap.createBitmap(areaWidth, areaHeight, Bitmap.Config.ARGB_8888);
        Canvas penCanvas = new Canvas();
        penCanvas.setBitmap(penBitmap);

        Paint mPaint = new Paint();
        mPaint.setColor(Color.WHITE);        //펜 굵기 선택 그리드뷰의 배경 색
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

        // set listener
        aItem.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (PenPaletteActivity.penlistener != null) {
                    PenPaletteActivity.penlistener.onPenSelected(((Integer)v.getTag()).intValue());
//                    Log.i("pen thickness","clicked");
                    temp1(((Integer)v.getTag()).intValue());
                }

//                ((PenPaletteDialog)mContext).finish();
            }
        });
        //Log.d("aItem ","getView(" +aItem+") called");
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

    int temp_color;
    /**
     * Colors defined
     */
    public static final int [] colors = new int[] {
            0xff000000,0xff00007f,0xff0000ff,0xff007f00,0xff007f7f,0xff00ff00,0xff00ff7f,
            0xff00ffff,0xff7f007f,0xff7f00ff,0xff7f7f00,0xff7f7f7f,0xffff0000,0xffff007f,
            0xffff00ff,0xffff7f00,0xffff7f7f,0xffff7fff,0xffffff00,0xffffff7f,0xff4682b4
    };

    int rowCount;
    int columnCount;

    public void temp2(int color){


        temp_color = color;
    }

    public ColorDataAdapter(Context context) {
        super();

        mContext = context;

        // create test data
        rowCount = 3;
        columnCount = 7;

    }

    public int getNumColumns() {
        return columnCount;
    }

    public int getCount() {
        return rowCount * columnCount;
    }

    public Object getItem(int position) {
        return colors[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup group) {
        //Log.d("ColorDataAdapter", "getView(" + position + ") called.");

        // calculate position
        int rowIndex = position / rowCount;
        int columnIndex = position % rowCount;
        //Log.d("ColorDataAdapter", "Index : " + rowIndex + ", " + columnIndex);

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

        // set listener
        aItem.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (PenPaletteActivity.colorlistener != null) {
                    PenPaletteActivity.colorlistener.onColorSelected(((Integer)v.getTag()).intValue());
                    temp2(((Integer)v.getTag()).intValue());
                }

//                ((PenPaletteDialog)mContext).finish();
            }
        });



        return aItem;
    }
}