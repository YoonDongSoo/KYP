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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;


/**
 * 지우개의 굵기를 선택하는 대화상자용 액티비티
 *
 * @author Mike
 *
 */
public class EraserPaletteActivity extends Activity {

    GridView grid;
    Button closeBtn;
    EraserDataAdapter adapter;

    public static OnEraserSelectedListener listener;

    public interface OnEraserSelectedListener { //지우개 사이즈 누른것 체크
        public void onEraserSelected(int eraser);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eraserdialog);

        this.setTitle("지우개굵기 선택");

        grid = (GridView) findViewById(R.id.colorGrid);
        closeBtn = (Button) findViewById(R.id.closeBtn);

        grid.setColumnWidth(14);
        grid.setBackgroundColor(Color.GRAY);
        grid.setVerticalSpacing(4);
        grid.setHorizontalSpacing(4);

        adapter = new EraserDataAdapter(this);
        grid.setAdapter(adapter);
        grid.setNumColumns(adapter.getNumColumns());

        closeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // dispose this activity
                finish();
            }
        });

    }

}

/**
 * Adapter for Pen Data
 *
 * @author Mike
 */
class EraserDataAdapter extends BaseAdapter {

    /**
     * Application Context
     */
    Context mContext;

    /**
     * Erasers defined
     */
    public static final int[] erasers = new int[]{
            3, 6, 9, 12, 15,
            18, 21, 24, 27, 30,
            33, 36, 39, 42, 45
    };

    int rowCount;
    int columnCount;


    public EraserDataAdapter(Context context) {
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
        return erasers[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup group) {
        //Log.d("EraserDataAdapter", "getView(" + position + ") called.");

        // calculate position
        int rowIndex = position / rowCount;
        int columnIndex = position % rowCount;
        // Log.d("EraserDataAdapter", "Index : " + rowIndex + ", " + columnIndex);

        GridView.LayoutParams params = new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT,
                GridView.LayoutParams.MATCH_PARENT);

        // create a eraser Image
        int areaWidth = 10;
        int areaHeight = 50;

        Bitmap eraserBitmap = Bitmap.createBitmap(areaWidth, areaHeight, Bitmap.Config.ARGB_8888);
        Canvas eraserCanvas = new Canvas();
        eraserCanvas.setBitmap(eraserBitmap);

        Paint mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        eraserCanvas.drawRect(0, 0, areaWidth, areaHeight, mPaint);

        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth((float) erasers[position]);
        eraserCanvas.drawLine(0, areaHeight / 2, areaWidth - 1, areaHeight / 2, mPaint);
        BitmapDrawable eraserDrawable;
        eraserDrawable = new BitmapDrawable(mContext.getResources(), eraserBitmap);

        // create a Button with the color
        Button aItem = new Button(mContext);
        aItem.setText(" ");
        aItem.setLayoutParams(params);
        aItem.setPadding(4, 4, 4, 4);
        aItem.setBackgroundDrawable(eraserDrawable);
        aItem.setHeight(64);
        aItem.setTag(erasers[position]);

        // set listener
        aItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (EraserPaletteActivity.listener != null) {
                    EraserPaletteActivity.listener.onEraserSelected(((Integer) v.getTag()).intValue());
                }

                ((EraserPaletteActivity) mContext).finish();
            }
        });

        return aItem;


    }


}


