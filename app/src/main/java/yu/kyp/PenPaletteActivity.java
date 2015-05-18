
package yu.kyp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * 선굵기를 선택하는 대화상자용 액티비티
 *
 * @author Mike
 *
 */
public class PenPaletteActivity extends Activity {

    private static final String TAG = ColorPickerDialog.class.getSimpleName();
    private static final int REQUEST_PEN_SIZE = 3;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CURRENT_SIZE = 4;
    private static final int REQUEST_ALPHA = 5;
    private static SharedPreferences sp;
    private static SharedPreferences for_alpha;
    private Context mainContext=this;

    GridView colorgrid;
    GridView sizegrid;
    GridView neonPen;
    Button othersBtn;
    Button selectBtn;
    SeekBar sizeSeekBar;
    SeekBar alphaSeekBar;
    PenDataAdapter penadapter;
    ColorDataAdapter coloradapter;
    RecentColorAdapter recentcoloradapter;
    NeonPenDataAdapter neonpenadapter;
    Paint mPaint;
    GridView recent_color_grid;
    MemoWriteActivity memowriteactivity;
    PaintBoard paintBoard;
    ArrayList<Integer> recent_color_list = new ArrayList<Integer>();
    static int current_size;
    static int progress_state = 0;          //펜사이즈
    static int progress_state2 = 0;         //알파값
    static int p_size_value = 0;
    static int alpha_value = 255;


    public static OnPenSelectedListener penlistener;
    public static OnColorSelectedListener colorlistener;
    public static OnCompleteSelectedListener completelistener;
    public static OnRecentColorSelectedListener recentcolorlistener;
    //    public static OnCancelSelectedListener cancellistner;
    public static OnNeonColorSelectedListener neoncolorlistener;

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

    public interface OnRecentColorSelectedListener{
        public void onRecentColorSelected(int color);
    }
    public interface OnNeonColorSelectedListener{
        public void onNeonColorSelected(int color);
    }


    /**
     * 펜 색상과 두께 선택을 위한 팔레트
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pendialog);

        memowriteactivity = new MemoWriteActivity();
        paintBoard = new PaintBoard(this);

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
//        sizegrid = (GridView) findViewById(R.id.sizeGrid);
        //최근 색상 그리드
        recent_color_grid = (GridView) findViewById(R.id.recent_color_grid);
        //형광펜 그리드
        neonPen = (GridView) findViewById(R.id.neonPen);
        //사이즈 시크바
        sizeSeekBar = (SeekBar) findViewById(R.id.sizeSeekBar);
        //투명도 시크바
        alphaSeekBar = (SeekBar) findViewById(R.id.alphaSeekBar);

        sp = getSharedPreferences("current_p_size",MODE_PRIVATE);
        p_size_value = sp.getInt("p_size_value",0);
        Toast.makeText(PenPaletteActivity.this,"펜팔레트에서의 사이즈" + p_size_value,Toast.LENGTH_SHORT).show();
        if(p_size_value != 2) {
            sizeSeekBar.setProgress(p_size_value);
        }
        alphaSeekBar.setProgress(255);

        for_alpha = getSharedPreferences("alpha_value",MODE_PRIVATE);
        alpha_value = for_alpha.getInt("alpha_value_is",0);
        Toast.makeText(PenPaletteActivity.this,"펜팔레트에서의 투명도" + alpha_value,Toast.LENGTH_SHORT).show();
        if(alpha_value != 255) {
            alphaSeekBar.setProgress(alpha_value);
        }

        //펜 사이즈 시크바가 움직이지 않았을 경우(터치가 아예 안되었을 경우)
        Intent i = new Intent();
        progress_state = 0;
        i.putExtra("p_size",progress_state);
        setResult(REQUEST_PEN_SIZE,i);

        Intent i2 = new Intent();
        progress_state2 = 255;
        i2.putExtra("alpha_size",progress_state2);
        setResult(REQUEST_ALPHA,i2);

        //펜 사이즈 시크바가 터치되었을 경우
        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                // Seekbar의 움직임이 멈춘다면 실행될 사항
                // seekbar는 해당 Seekbar를 의미함.
                Intent i = new Intent();
//                int current_progress = sizeSeekBar.getProgress();
                progress_state = sizeSeekBar.getProgress();
                i.putExtra("p_size",progress_state);

                Toast.makeText(PenPaletteActivity.this,"seekbar: " + sizeSeekBar.getProgress(), Toast.LENGTH_LONG).show();

                setResult(REQUEST_PEN_SIZE,i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
            }
        });

        //투명도 시크바가 터치되었을 경우
        alphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                // Seekbar의 움직임이 멈춘다면 실행될 사항
                // seekbar는 해당 Seekbar를 의미함.
                progress_state2 = alphaSeekBar.getProgress();

                Intent i2 = new Intent();
                i2.putExtra("alpha_size",progress_state2);
                setResult(REQUEST_ALPHA,i2);

                Toast.makeText(PenPaletteActivity.this,"투명도 seekbar: " + alphaSeekBar.getProgress(), Toast.LENGTH_SHORT).show();

                for_alpha = getSharedPreferences("alpha_value",MODE_PRIVATE);
                SharedPreferences.Editor editor2 = for_alpha.edit();
                editor2.putInt("alpha_value_is",progress_state2);
                editor2.commit();

                paintBoard.set_alpha(progress_state2);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
            }
        });

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

        //사이즈
        sizeSeekBar.setMax(50);


//        sizegrid.setColumnWidth(14);
//        sizegrid.setBackgroundColor(Color.GRAY);
//        sizegrid.setVerticalSpacing(4);
//        sizegrid.setHorizontalSpacing(4);

        //펜데이터어댑터와 연결
//        penadapter = new PenDataAdapter(this);
//        sizegrid.setAdapter(penadapter);
//        sizegrid.setNumColumns(penadapter.getNumColumns());

        //최근사용컬러어댑터와 연결
        recentcoloradapter = new RecentColorAdapter(this);
        recent_color_grid.setAdapter(recentcoloradapter);
        recent_color_grid.setNumColumns(recentcoloradapter.getNumColumns());

        //형광펜 어댑터와 연결
        neonpenadapter = new NeonPenDataAdapter(this);
        neonPen.setAdapter(neonpenadapter);
        neonPen.setNumColumns(neonpenadapter.getNumColumns());

        recent_color_list = memowriteactivity.color_save;
        recentcoloradapter.recent_color_arraylist = recent_color_list;
        Log.i("@@@@onCreate@@@@@@@@", "어레이갯수" + recent_color_list.size());
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
class PenDataAdapter implements SeekBar.OnSeekBarChangeListener{
    /**
     * Application Context
     */
    Context mContext;
    SharedPreferences sp;
    String OPT_SEEKBAR_KEY = "setting";
    int OPT_SEEKBAR_DEF = 30;
    int LAYOUT_PADDING = 10;
    int currentvalue;

    public PenDataAdapter(Context context) {
        super();

        mContext = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // TODO Auto-generated method stub
    }
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        Log.i("시크바","" + seekBar.getProgress());

    }
//    public View onCreate(){
//
//    }
//

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
    ArrayList<Integer> for_recent_color = new ArrayList<Integer>();

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
        int i=0;
        //Log.d("ColorDataAdapter", "Index : " + rowIndex + ", " + columnIndex);

        //펜 색상을 나타낼 그리드뷰 생성
        GridView.LayoutParams params = new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT,
                GridView.LayoutParams.MATCH_PARENT);

        // create a Button with the color
        final Button aItem = new Button(mContext);
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

                //선택한 값을 어레이 리스트에 add
                for_recent_color.add(((Integer) v.getTag()).intValue());
                Log.i("aItem","출력 : " + for_recent_color.get(for_recent_color.size()-1));

                if(for_recent_color.size() >=8){
                    for_recent_color.remove(0);
                }

            }
        });


        //선택한 것을 리턴
        return aItem;
    }
}

/**
 * Adapter for Color Data
 *
 * @author Mike
 */
class NeonPenDataAdapter extends BaseAdapter {

    /**
     * Application Context
     */
    Context mContext;

    /**
     * Colors defined
     */

    //형광펜의 색상 선택을 위한 Int형 색상 배열 생성
    public static final int [] neoncolors = new int[] {
            0xfffffc7f,0xfff06d61,0xffff9933,0xff8ed0d4,0xffe0b0cb,0xffc7f464,0xffcebfe0
    };

    int rowCount;
    int columnCount;


    public NeonPenDataAdapter(Context context) {
        super();

        mContext = context;

        // create test data
        rowCount = 1;
        columnCount = 7;

    }

    /**
     * 형광펜 색상을 선택하는 그리드뷰에서
     * 선택한 부분의 column 값을 리턴
     * @return
     */
    public int getNumColumns() {
        return columnCount;
    }

    /**
     * 형광펜 색상을 선택하는 그리드뷰에서
     * 펜 색상의 갯수를 리턴
     * @return
     */
    public int getCount() {
        return rowCount * columnCount;
    }

    /**
     * 형광펜 색상을 선택하는 그리드뷰에서
     * 펜 색상의 포지션을 리턴
     * @param position
     * @return
     */
    public Object getItem(int position) {
        return neoncolors[position];
    }

    /**
     * 형광펜 색상을 선택하는 그리드뷰에서
     * 선택된 값을 확인
     * @param position
     * @return
     */
    public long getItemId(int position) {
        return 0;
    }

    /**
     * 형광펜 색상을 선택하는 그리드뷰를
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
        int i=0;
        //Log.d("ColorDataAdapter", "Index : " + rowIndex + ", " + columnIndex);

        //펜 색상을 나타낼 그리드뷰 생성
        GridView.LayoutParams params = new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT,
                GridView.LayoutParams.MATCH_PARENT);

        // create a Button with the color
        final Button aItem = new Button(mContext);
        aItem.setText(" ");
        aItem.setAlpha(20);
        aItem.setLayoutParams(params);
        aItem.setPadding(4, 4, 4, 4);
        aItem.setBackgroundColor(neoncolors[position]);
        aItem.setHeight(64);
        aItem.setTag(neoncolors[position]);

        //형광펜 색상 그리드뷰에서
        //하나의 값을 선택(클릭)하였을 때
        aItem.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (PenPaletteActivity.neoncolorlistener != null) {
                    PenPaletteActivity.neoncolorlistener.onNeonColorSelected(((Integer) v.getTag()).intValue());
                }

//                ((PenPaletteDialog)mContext).finish();


            }
        });


        //선택한 것을 리턴
        return aItem;
    }
}


/**
 * Adapter for Recent Color Data
 */
class RecentColorAdapter extends BaseAdapter{
    //    ColorDataAdapter coloradapter;
    PenPaletteActivity penpaletteActivity;

    Context mContext;
    ArrayList<Integer> recent_color_arraylist = new ArrayList<Integer>();


    public static final int[] recent_colors = new int[7];

    int rowCount;
    int columnCount;

    public RecentColorAdapter(Context context) {
        super();

        penpaletteActivity = new PenPaletteActivity();
        mContext = context;

        //1*7 그리드
        rowCount = 1;
        columnCount = 7;

    }

    /**
     * 최근 사용한 색상을 보여주는 그리드뷰에서
     * 선택한 부분의 column 값을 리턴
     * @return
     */
    public int getNumColumns() {
        return columnCount;
    }

    /**
     * 최근 사용한 색상을 보여주는 그리드뷰에서
     * 색상 갯수를 리턴
     * @return
     */
    public int getCount() {
        return rowCount * columnCount;
    }

    /**
     * 최근 사용한 색상을 보여주는 그리드뷰에서
     * 색상의 포지션을 리턴
     * @param position
     * @return
     */
    public Object getItem(int position) {
        return recent_colors[position];
    }

    /**
     * 최근 사용한 색상을 보여주는 그리드뷰에서
     * 선택된 값을 확인
     * @param position
     * @return
     */
    public long getItemId(int position) {
        return 0;
    }

    /**
     * 최근 사용한 색상을 선택하는 그리드뷰를
     * 만드는 함수
     * @param position
     * @param view
     * @param group
     * @return
     */
    public View getView(int position, View view, ViewGroup group) {
        int i=0;
        if(recent_color_arraylist.size() != 0) {
            if(recent_color_arraylist.size() >= 8){
                recent_color_arraylist.remove(0);
            }
            Log.i("******최근색상그리드뷰*****","" + recent_color_arraylist.size());
            for (i = recent_color_arraylist.size()-1; i >=0 ; i--) {
                recent_colors[(recent_color_arraylist.size()-1)-i] = recent_color_arraylist.get(i);
            }
            for(i=recent_color_arraylist.size(); i<7; i++){
                recent_colors[i] = 0xffffffff;
            }
        }
        Log.i("******최근색상그리드뷰*****","***");

        //Log.d("PenDataAdapter", "getView(" + position + ") called.");

        // calculate position
        int rowIndex = position / rowCount;
        int columnIndex = position % rowCount;
        //Log.d("PenDataAdapter", "Index : " + rowIndex + ", " + columnIndex);

        //최근 사용한 색을 나타낼 그리드뷰 생성
        GridView.LayoutParams params = new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT,
                GridView.LayoutParams.MATCH_PARENT);

        // create a Button with the color
        Button aItem = new Button(mContext);
        aItem.setText(" ");
        aItem.setLayoutParams(params);
        aItem.setPadding(4, 4, 4, 4);
        aItem.setBackgroundColor(recent_colors[position]);
        aItem.setHeight(64);
        aItem.setTag(recent_colors[position]);

        //최근 사용한 색을 나타내는 그리드뷰에서
        //하나의 값을 선택(클릭)하였을 때
        aItem.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (PenPaletteActivity.recentcolorlistener != null) {
                    PenPaletteActivity.recentcolorlistener.onRecentColorSelected(((Integer)v.getTag()).intValue());
//                    Log.i("pen thickness","clicked");
                }

            }
        });
        //Log.d("aItem ","getView(" +aItem+") called");

        //선택한 것을 리턴
        return aItem;
    }

//    @Override
//    public void onBackPressed() {
//
//        super.onBackPressed();
//
//    }
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event){
//        switch(keyCode){
//            case KeyEvent.KEYCODE_BACK:
//                break;
//        }
//        finish();
//    }
}