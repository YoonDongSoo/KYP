package yu.kyp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import yu.kyp.image.NoteManager;

/**
 * Created by Chaejin on 2015-05-21.
 */
public class ListCursorAdapter extends CursorAdapter {

    String Title;
    String Time;
    String ListName;

    public int listItem[];
    private NoteManager noteManager = null;

    public ListCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        // noteManager = new NoteManager(context);
        listItem = new int[cursor.getCount()];
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.activity_memo_list_item, parent, false);

        return v;
    }

    public void bindView(View view, final Context context, final Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views
        final int position = cursor.getPosition();

        Log.i("!!!!", "position" + position);




//        listItem[position]=position;
        TextView memotext1 = (TextView) view.findViewById(R.id.listview1);
        TextView memotext2 = (TextView) view.findViewById(R.id.listview2);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.memolistLinearLayout);
        ImageView listicon = (ImageView)view.findViewById(R.id.listicon);
        //제목변경하면  "NOTE_NO"-> "TITLE" 로 바꾸기
        Title = cursor.getString(cursor.getColumnIndex("TITLE"));
        Time = cursor.getString(cursor.getColumnIndex("LAST_MOD_DT"));
        String strColor = "#FFFFFFFF";
        memotext1.setText(Title);
        memotext2.setText(Time);
        memotext1.setTextColor(Color.parseColor(strColor));
        memotext2.setTextColor(Color.parseColor(strColor));
        listicon.setBackground(context.getResources().getDrawable(R.drawable.icon));
        String strColor1 = "#77E4DDFA";
        if(position%2==0)
        {


            linearLayout.setBackgroundColor(Color.TRANSPARENT);
        }
        else
        {

            linearLayout.setBackgroundColor(Color.parseColor(strColor1));
        }










    }
}


