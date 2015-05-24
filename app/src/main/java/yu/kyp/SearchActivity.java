package yu.kyp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import yu.kyp.common.activity.ActivityBase;
import yu.kyp.common.database.DB;
import yu.kyp.image.NoteManager;

/**
 * Created by subin on 2015-05-18.
 */
public class SearchActivity extends ActivityBase{
    private NoteManager noteManager = null;
    private DB db = null;
    private SimpleCursorAdapter adapterListNote = null;
    private static final String TAG = SearchActivity.class.getSimpleName();

    ImageButton buttonTitleSearch;
    EditText titleSearchEdit;
    ListView search_list;
    TextView titlenotsearch;
    ListView listviewNote;
    Cursor c;

    private AdapterView.OnItemClickListener listenerListNote = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent i = new Intent(getApplicationContext(),MemoWriteActivity2.class);
            i.putExtra("NOTE_NO",(int)id);
            startActivity(i);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_search);

        buttonTitleSearch = (ImageButton) findViewById(R.id.buttonTitleSearch);
        titleSearchEdit = (EditText) findViewById(R.id.titleSearchEdit);
        search_list = (ListView) findViewById(R.id.search_list);
        titlenotsearch = (TextView) findViewById(R.id.titlenotsearch);
        search_list.setOnItemClickListener(listenerListNote);

        noteManager = new NoteManager(this);

        buttonTitleSearch.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                adapterListNote = null;
                //edittext에 입력한 제목이 포함되어있는 db들을 불러온다.
                noteManager.titlegetNoteList(titleSearchEdit.getText().toString());

                //위에서 불러온 db들을 listview에 뿌려준다.
                bindNote();
            }
        });

        // 배경설정
        LinearLayout layoutTop = (LinearLayout)findViewById(R.id.layoutTop);
        setBackground(layoutTop);
    }
    private void bindNote() {
        c = noteManager.titlegetNoteList(titleSearchEdit.getText().toString());
        if(adapterListNote==null) {
            adapterListNote = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, c, new String[]{"TITLE", "LAST_MOD_DT"}, new int[]{android.R.id.text1, android.R.id.text2});
            listviewNote = (ListView) findViewById(R.id.search_list);
            listviewNote.setAdapter(adapterListNote);
            Toast.makeText(SearchActivity.this,"검색된 메모는 " + c.getCount() + "개입니다.",Toast.LENGTH_SHORT).show();
            titlenotsearch.setVisibility(View.GONE);
        }
        else
        {
            adapterListNote.changeCursor(c);
        }
        //검색 결과가 없을 때
        if(c.moveToNext() == false){
            //검색결과가 없다는 텍스트뷰를 띄워줌
            titlenotsearch.setVisibility(View.VISIBLE);
        }

    }

}
