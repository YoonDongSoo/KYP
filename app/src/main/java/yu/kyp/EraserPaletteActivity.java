package yu.kyp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;


/**
 * 지우개의 굵기를 선택하는 대화상자용 액티비티
 *
 */
public class EraserPaletteActivity extends Activity {
    Button closeBtn;
    private static SharedPreferences sp1;
    private static SharedPreferences sp2;
    SeekBar eraserSeekBar;
    static int e_size_value = 0;
    private static final int REQUEST_ERASER_SIZE = 4;
    static int progress_state = 0;
    static int progress_state2 =0;
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

        //취소버튼
        closeBtn = (Button) findViewById(R.id.closeBtn);
        //지우개 사이즈 선택 시크바
        eraserSeekBar = (SeekBar) findViewById(R.id.eraserSeekBar);
        eraserSeekBar.setMax(50);

        sp2 = getSharedPreferences("currnt_e_size",MODE_PRIVATE);
        e_size_value = sp2.getInt("e_size_value",0);
        Toast.makeText(EraserPaletteActivity.this, "지우개에서의 사이즈" + e_size_value, Toast.LENGTH_SHORT).show();
//        if(e_size_value != 2) {
            eraserSeekBar.setProgress(e_size_value);
//        }

        //시크바가 움직이지 않았을 경우
        Intent i = new Intent();
        progress_state2 = 0;
        i.putExtra("e_size",progress_state2);
        setResult(REQUEST_ERASER_SIZE,i);

        //시크바가 터치되었을 경우
        eraserSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                // Seekbar의 움직임이 멈춘다면 실행될 사항
                // seekbar는 해당 Seekbar를 의미함.
                Intent i = new Intent();
//                int current_progress = sizeSeekBar.getProgress();
                progress_state = eraserSeekBar.getProgress();
                i.putExtra("e_size",progress_state);

                Toast.makeText(EraserPaletteActivity.this,"seekbar: " + eraserSeekBar.getProgress(), Toast.LENGTH_SHORT).show();

                setResult(REQUEST_ERASER_SIZE,i);
//

//                sp1 = getSharedPreferences("current_e_size",MODE_PRIVATE);
//                SharedPreferences.Editor editor2 = sp1.edit();
//                editor2.putInt("e_size_value",progress_state);
//                editor2.commit();
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

        //닫기 버튼을 눌렀을 때
        closeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //지우개 액티비티 종료
                finish();
            }
        });

    }

}


