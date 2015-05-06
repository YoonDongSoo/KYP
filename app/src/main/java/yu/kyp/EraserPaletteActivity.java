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
 */
public class EraserPaletteActivity extends Activity {

    GridView grid;
    Button closeBtn;
    EraserDataAdapter adapter;

    public static OnEraserSelectedListener listener;

    public interface OnEraserSelectedListener { //지우개 사이즈 누른것 체크
        public void onEraserSelected(int eraser);
    }


    /**
     * 지우개 두께 선택을 위한 팔레트
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eraserdialog);

        this.setTitle("지우개굵기 선택");

        //지우개를 위한 그리드 그리드
        grid = (GridView) findViewById(R.id.colorGrid);
        //취소버튼
        closeBtn = (Button) findViewById(R.id.closeBtn);

        grid.setColumnWidth(14);
        grid.setBackgroundColor(Color.GRAY);
        grid.setVerticalSpacing(4);
        grid.setHorizontalSpacing(4);

        //지우개데이터어댑터와 연결
        adapter = new EraserDataAdapter(this);
        grid.setAdapter(adapter);
        grid.setNumColumns(adapter.getNumColumns());

        //닫기 버튼을 눌렀을 때
        closeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //지우개 액티비티 종료
                finish();
            }
        });

    }

}

/**
 * Adapter for Pen Data
 */
class EraserDataAdapter extends BaseAdapter {

    /**
     * Application Context
     */
    Context mContext;

    /**
     * Erasers defined
     * 지우개 사이즈 선택을 위한 Int형 사이즈 배열 선언
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

        //3*5 그리드
        rowCount = 3;
        columnCount = 5;

    }

    /**
     * 지우개 굵기를 선택하는 그리드뷰에서
     * 선택한 부분의 column 값을 리턴
     * @return
     */
    public int getNumColumns() {
        return columnCount;
    }

    /**
     * 지우개 굵기를 선택하는 그리드뷰에서
     * 지우개 사이즈 갯수를 리턴
     * @return
     */
    public int getCount() {
        return rowCount * columnCount;
    }

    /**
     * 지우개 굵기를 선택하는 그리드뷰에서
     * 지우개 굵기의 포지션을 리턴
     * @param position
     * @return
     */
    public Object getItem(int position) {
        return erasers[position];
    }

    /**
     * 지우개 굵기를 선택하는 그리드뷰에서
     * 선택된 값을 확인
     * @param position
     * @return
     */
    public long getItemId(int position) {
        return 0;
    }

    /**
     * 지우개 굵기를 선택하는 그리드뷰를
     * 만드는 함수
     * @param position
     * @param view
     * @param group
     * @return
     */
    public View getView(int position, View view, ViewGroup group) {
        //Log.d("EraserDataAdapter", "getView(" + position + ") called.");

        // calculate position
        int rowIndex = position / rowCount;
        int columnIndex = position % rowCount;
        // Log.d("EraserDataAdapter", "Index : " + rowIndex + ", " + columnIndex);

        //지우개 굵기를 나타낼 그리드뷰 생성
        GridView.LayoutParams params = new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT,
                GridView.LayoutParams.MATCH_PARENT);

        //지우개 굵기를 나타낼 가로, 세로 높이 지정
        int areaWidth = 10;
        int areaHeight = 50;

        Bitmap eraserBitmap = Bitmap.createBitmap(areaWidth, areaHeight, Bitmap.Config.ARGB_8888);
        Canvas eraserCanvas = new Canvas();
        eraserCanvas.setBitmap(eraserBitmap);

        Paint mPaint = new Paint();
        //지우개 굵기 선택 그리드뷰의 배경 색
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

        //지우개 사이즈 그리드뷰에서
        //하나의 값을 선택(클릭)하였을 때
        aItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (EraserPaletteActivity.listener != null) {
                    EraserPaletteActivity.listener.onEraserSelected(((Integer) v.getTag()).intValue());
                }

                ((EraserPaletteActivity) mContext).finish();
            }
        });

        //선택한 것을 리턴
        return aItem;
    }
}


