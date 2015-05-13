package yu.kyp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by 수빈 on 2015-05-13.
 */
public class TextDialog extends Activity{
    EditText textEdit;
    Button Ok;
    Button Cancle;
    Canvas canvas;
    Paint mPaint;
    PaintBoard paintboard;


    public static OnTextSelectedListener textlistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_popup);

        this.setTitle("텍스트 입력");

        mPaint = new Paint();

        textEdit = (EditText) findViewById(R.id.textEdit);
        Ok = (Button) findViewById(R.id.Ok);
        Cancle = (Button) findViewById(R.id.Cancle);
        paintboard = new PaintBoard(this);

        //확인 버튼을 눌렀을 때
        Ok.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //적힌 글자를 string 변수 str에 넣기
                final String str = textEdit.getText().toString();

                //글자적히는거 테스트
                Toast.makeText(TextDialog.this, str, Toast.LENGTH_LONG).show();

                Intent intent = TextDialog.this.getIntent();
                float x_coor = intent.getExtras().getFloat("x");
                float y_coor = intent.getExtras().getFloat("y");

                paintboard.printText(x_coor,y_coor,str);
            }
        });

        //취소 버튼을 눌렀을 때
        Cancle.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });


    }
    public interface OnTextSelectedListener{
        public void OnTextSelected(String str);
    }

//    //back키를 눌렀을 때
//    public boolean onKeyDown(int keyCode, KeyEvent event){
//        boolean endBack = false;
//        if(keyCode == KeyEvent.KEYCODE_BACK){
//            if(!endBack) {
//                finish();
//                endBack = true;
//            }
//        }
//        return true;
//    }

}
