package yu.kyp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;



public class WriteBackgoundSelectActivity extends Activity {


    private static final String TAG = WriteBackgoundSelectActivity.class.getSimpleName();
    private static final int REQUEST_WRITE_BG = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_backgound_select);

        // 1. 변수 설정
        ListView listView = (ListView)findViewById(R.id.listViewBackground);

        // 2. 이미지 리스트뷰 설정
        Integer[] items = new Integer[] {R.drawable.icon_note_line, R.drawable.icon_note_empty, R.drawable.icon_note_conf};
        WriteBackgroundAdapter adapter = new WriteBackgroundAdapter(this,R.layout.write_background_select_list_item,items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listenerWriteBgSelect);
    }

    /**
     * listViewBackground 아이템 클릭 리스너
     */
    private AdapterView.OnItemClickListener listenerWriteBgSelect = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Log.e(TAG,"position:"+position);
            Intent i = new Intent();
            i.putExtra("position",position);
            setResult(RESULT_OK,i);

            finish();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_write_backgound_select, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class WriteBackgroundAdapter extends ArrayAdapter<Integer>
    {
        private final Integer[] items;

        public WriteBackgroundAdapter(Context context, int resource, Integer[] items) {
            super(context, resource, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Log.e(TAG, "getView position:" + position);
            View v = convertView;
            if(v==null)
            {
                LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = li.inflate(R.layout.write_background_select_list_item,null);
            }
            int resId = items[position];
            ImageView imageView = (ImageView)v.findViewById(R.id.imageView1);
            if(imageView!=null)
            {
                imageView.setImageDrawable(getResources().getDrawable(resId));
            }
            return v;
        }
    }

    public void buttonBack_OnClick(View v)
    {
        // 1.저장하지 않고 나갈때는 물어보기 팝업
        // 2. activity 종료
        finish();
    }
}
