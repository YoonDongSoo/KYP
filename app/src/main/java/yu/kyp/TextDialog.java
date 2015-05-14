package yu.kyp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 수빈 on 2015-05-13.
 */
public class TextDialog extends Activity{
    public static OnTextSelectedListener textlistener;
    private static final int REQUEST_DRAW_TEXT = 2;
    int mAvailableWidth =0;
    Paint mPaint;
    List<String> mCutStr = new ArrayList<String>();
    int LineHeight = 0;
    int PaddingLeft = 0;
    int PaddingRight = 0;
    int PaddingBottom = 0;
    int PaddingTop = 0;

    private static class OnTextSelectedListener {
        public void OnTextSelected(String str){

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_popup);

//        this.setTitle("텍스트 입력");

        mPaint = new Paint();
//        canvas = new Canvas();
//
        final EditText textEdit = (EditText) findViewById(R.id.textEdit);
        Button Ok = (Button) findViewById(R.id.Ok);
        Button Cancel = (Button) findViewById(R.id.cancel);



        //확인 버튼을 눌렀을 때
        Ok.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //적힌 글자를 string 변수 str에 넣기
                String str = textEdit.getText().toString();

                LineHeight = textEdit.getLineHeight();
                PaddingLeft = textEdit.getPaddingLeft();
                PaddingRight = textEdit.getPaddingRight();
                PaddingBottom = textEdit.getPaddingBottom();
                PaddingTop = textEdit.getPaddingTop();

                //글자적히는거 테스트
                Toast.makeText(TextDialog.this, str, Toast.LENGTH_LONG).show();

                Intent intent = TextDialog.this.getIntent();
                float x_coor = intent.getExtras().getFloat("x");
                float y_coor = intent.getExtras().getFloat("y");

//                paintboard.draw(canvas,x_coor, y_coor, str);


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
                String str = "";


                Intent intent = TextDialog.this.getIntent();
                //float x_coor = intent.getExtras().getFloat("x");
                //float y_coor = intent.getExtras().getFloat("y");

                Intent i = new Intent();
                i.putExtra("isCancel",1);
                //i.putExtra("text",str);
                //i.putExtra("x",x_coor);
                //i.putExtra("y",y_coor);
                setResult(REQUEST_DRAW_TEXT,i);

                boolean flag = true;
                //Intent i2 = new Intent();
                //Toast.makeText(TextDialog.this,"flag: " + flag, Toast.LENGTH_SHORT).show();
                //i2.putExtra("flag",flag);
                finish();
            }
        });


    }
    public int setTextInfo(String text, int textWidth, int textHeight){
        int mTextHeight = textHeight;

        if(textWidth > 0){
            mAvailableWidth = textWidth;

            mCutStr.clear();
            int end = 0;
            while(end >0){
                // 글자가 width 보다 넘어가는지 체크
                end = mPaint.breakText(text, true, mAvailableWidth, null);
                if (end > 0) {
                    // 자른 문자열을 문자열 배열에 담아 놓는다.
                    mCutStr.add(text.substring(0, end));
                    // 넘어간 글자 모두 잘라 다음에 사용하도록 세팅
                    text = text.substring(end);
                    // 다음라인 높이 지정
                    if (textHeight == 0) mTextHeight += LineHeight;
                }
            }
        }
        mTextHeight += PaddingTop + PaddingBottom;
        return mTextHeight;
    }



}
