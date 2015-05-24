package yu.kyp;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import yu.kyp.image.NoteManager;

/**
 * Created by Chaejin on 2015-05-16.
 */
public class CustomCursorAdapter extends CursorAdapter {
    protected Context mContext;
    protected Cursor mCursor;
    private CheckBox trashcheck;
    private ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
    private NoteManager noteManager = null;
    boolean[] checked;
    int[] allchecked;
    private boolean[] isCheckedConfrim;
    String Title;
    String Time;
    int rowID;
    int i=0;
    final ArrayList<Integer> listItem = new ArrayList<Integer>();


    public CustomCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        noteManager = new NoteManager(context);
        for (int i = 0; i < this.getCount(); i++) {
            itemChecked.add(i, false); // initializes all items value with false
        }
        checked = new boolean[cursor.getCount()];
        allchecked = new int[cursor.getCount()];
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.activity_trash2, parent, false);



        return v;
    }

    public void bindView(View view, final Context context, final Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views
        final int position = cursor.getPosition();

        Log.i("!!!!", "position" + position);



        final TextView note = (TextView) view.findViewById(R.id.textview);
        final TextView notetime = (TextView) view.findViewById(R.id.textview2);

        Title = cursor.getString(cursor.getColumnIndex("TITLE"));
        note.setText(Title);
        Time = cursor.getString(cursor.getColumnIndex("LAST_MOD_DT"));
        notetime.setText(Time);

        trashcheck = (CheckBox) view.findViewById(R.id.checkbox);

        //체크박스가 null이 아니면...
        if (trashcheck != null) {
            // 체크박스의 상태 변화를 체크한다.
            trashcheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    checked[position] = isChecked;
                    Log.i("!!!!", "cursor position " + position);

                }
            });


            boolean isChecked = false;
            for (int i = 0; i < checked.length; i++) {
                // 만약 체크되었던 아이템이라면
                if (checked[i] == true) {
                    // 체크를 한다.
                    trashcheck.setChecked(true);
                    isChecked = true;
                    break;
                }
            }
            // 아니라면 체크 안함!
            if (!isChecked) {
                trashcheck.setChecked(false);
            }



        }


    }


    //전체 체크/해제
    public void setAllChecked(boolean ischecked) {

        for (int i = 0; i < checked.length; i++) {

            checked[i] = ischecked;
        }
    }






}

