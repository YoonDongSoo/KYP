package yu.kyp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import yu.kyp.bluno.BlunoLibrary;
import yu.kyp.image.Note;
import yu.kyp.image.NoteManager;
import yu.kyp.image.TouchImageView;


public class MemoWriteActivity2 extends BlunoLibrary {

    private static final String TAG = MemoWriteActivity2.class.getSimpleName();
    private StringBuffer strBuffer = new StringBuffer();
    private NoteManager noteManager = null;
    /**
     * 노트 객체
     */
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_write_activity2);

        // 1. 인스턴스 생성
        noteManager = new NoteManager(this);

        // 2. Find View
        TouchImageView touchViewPaint =  (TouchImageView)findViewById(R.id.touchViewPaint);
        touchViewPaint.setImageBitmap(note.NOTE_DATA);

        // 3. 기본값 불러오기
        getNoteData();


    }

    private void getNoteData() {
        try
        {

            Intent i = getIntent();
            int noteNo = i.getIntExtra("NOTE_NO", 0);
            if (noteNo > 0) {
                note = noteManager.getNote(noteNo);
                //paintboard.undo.addList(note.NOTE_DATA);;

            }
            else
            {
                note = new Note();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_memo_write_activity2, menu);
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

    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {
        switch (theConnectionState) {											//Four connection state
            case isConnected:
                Log.d(TAG, "Connected");
                //buttonScan.setText("Connected");
                break;
            case isConnecting:
                Log.d(TAG, "Connecting");
                //buttonScan.setText("Connecting");
                break;
            case isToScan:
                Log.d(TAG, "Scan");
                //buttonScan.setText("Scan");
                break;
            case isScanning:
                Log.d(TAG, "Scanning");
                //buttonScan.setText("Scanning");
                break;
            case isDisconnecting:
                Log.d(TAG, "isDisconnecting");
                //buttonScan.setText("isDisconnecting");
                break;
            default:
                break;
        }
    }

    @Override
    public void onSerialReceived(String theString) {
        //serialReceivedText.append(theString);							//append the text into the EditText
        //The Serial data from the BLUNO may be sub-packaged, so using a recvBuffer to hold the String is a good choice.
        Log.d(TAG,"onSerialReceived:"+theString);
        strBuffer.append(theString);

        // 임시로 주석처리
        /*if(theString.contains("ZOM01")==true)
        {
            paintboard.zoomInBitmap();
        }
        else if(theString.contains("ZOM02")==true)
        {
            paintboard.zoomOutBitmap();
        }
        else if(theString.contains("ZOM03")==true)
        {
            paintboard.zoomResetBitmap();
        }*/
    }
}
