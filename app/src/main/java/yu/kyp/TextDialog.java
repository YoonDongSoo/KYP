package yu.kyp;

import android.app.Activity;
import android.content.Intent;
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
    public static OnTextSelectedListener textlistener;
    private static final int REQUEST_DRAW_TEXT = 2;
    Paint mPaint;

    private static class OnTextSelectedListener {
        public void OnTextSelected(String str){
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_popup);

        this.setTitle("텍스트 입력");

        mPaint = new Paint();

        final EditText textEdit = (EditText) findViewById(R.id.textEdit);
        Button Ok = (Button) findViewById(R.id.Ok);
        Button Cancel = (Button) findViewById(R.id.cancel);

        //확인 버튼을 눌렀을 때
        Ok.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //적힌 글자를 string 변수 str에 넣기
                String str = textEdit.getText().toString();

                //글자적히는거 테스트
                Toast.makeText(TextDialog.this, str, Toast.LENGTH_LONG).show();

                Intent intent = TextDialog.this.getIntent();
                float x_coor = intent.getExtras().getFloat("x");
                float y_coor = intent.getExtras().getFloat("y");

                Intent i = new Intent();
                i.putExtra("isCancel",0);
                i.putExtra("text",str);
                i.putExtra("x",x_coor);
                i.putExtra("y",y_coor);
                setResult(REQUEST_DRAW_TEXT,i);

                finish();

            }
        });

        //취소 버튼을 눌렀을 때
        Cancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i = new Intent();
                i.putExtra("isCancel",1);

                setResult(REQUEST_DRAW_TEXT,i);

                finish();
            }
        });


    }
}
