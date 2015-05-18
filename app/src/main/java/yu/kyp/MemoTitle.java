package yu.kyp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import yu.kyp.image.Note;
import yu.kyp.image.NoteManager;
import yu.kyp.image.Thumbnail;

/**
 * Created by subin on 2015-05-18.
 */
public class MemoTitle extends Activity {
    Paint mPaint;
    private static final int REQUEST_INPUT_TITLE = 5;
    private static SharedPreferences sp;
    PaintBoard paintboard;
    private Note note;
    private NoteManager noteManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo_title_input);

        this.setTitle("Title");

        mPaint = new Paint();
        paintboard = new PaintBoard(this);

        final EditText titleEdit = (EditText) findViewById(R.id.titleEdit);
        Button titleOk = (Button) findViewById(R.id.titleOk);
        Button titleCancel = (Button) findViewById(R.id.titleCancel);

        //확인 버튼을 눌렀을 때
        titleOk.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //적힌 글자를 string 변수 str에 넣기
                String str = titleEdit.getText().toString();

                //글자적히는거 테스트
                Toast.makeText(MemoTitle.this, str, Toast.LENGTH_LONG).show();

                Intent i = new Intent();
//                i.putExtra("e_size",progress_state2);
                setResult(REQUEST_INPUT_TITLE, i);

                finish();

            }
        });

        //취소 버튼을 눌렀을 때
        titleCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });


    }
    /**
     * 노트를 DB에 저장한다.
     * 변경된 사항이 없으면 저장하지 않는다.
     */
    private void saveNote() {
        // 변경된 사항이 없으면 DB에 저장하지 않는다.
        if(paintboard.undo.size()<=1)
            return;

        if(note.TITLE==null || note.TITLE.equals("")==true)
            note.TITLE = "제목 없음";
        note.NOTE_DATA = paintboard.undo.getLast();
        note.thumbnail = new Thumbnail(note.NOTE_DATA);
        noteManager.saveNoteData(note);
    }


}
