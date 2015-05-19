package yu.kyp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

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

    Button buttonTitleSearch;
    EditText titleSearchEdit;
    ListView search_list;

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

        buttonTitleSearch = (Button) findViewById(R.id.buttonTitleSearch);
        titleSearchEdit = (EditText) findViewById(R.id.titleSearchEdit);
        search_list = (ListView) findViewById(R.id.search_list);
        search_list.setOnItemClickListener(listenerListNote);

        noteManager = new NoteManager(this);

        buttonTitleSearch.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //edittext에 입력한 제목이 포함되어있는 db들을 불러온다.
                noteManager.titlegetNoteList(titleSearchEdit.getText().toString());
                //위에서 불러온 db들을 listview에 뿌려준다.
                bindNote();
            }
        });
    }
    private void bindNote() {
        Cursor c = noteManager.titlegetNoteList(titleSearchEdit.getText().toString());
        if(adapterListNote==null) {
            adapterListNote = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, c, new String[]{"TITLE", "LAST_MOD_DT"}, new int[]{android.R.id.text1, android.R.id.text2});
            ListView listviewNote = (ListView) findViewById(R.id.search_list);
            listviewNote.setAdapter(adapterListNote);
        }
        else
        {
            adapterListNote.changeCursor(c);
        }

    }

}
