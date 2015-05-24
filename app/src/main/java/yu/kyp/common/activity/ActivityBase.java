package yu.kyp.common.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import yu.kyp.R;
import yu.kyp.common.Settings;
import yu.kyp.common.database.CreateTables;
import yu.kyp.common.database.DB;

/**
 * Created by DONGSOO on 2015-03-24.
 */
public class ActivityBase extends Activity {
    private static final String TAG = ActivityBase.class.getSimpleName();
    /**
     * 데이터베이스
     */
    protected DB db = null;
    protected Settings settings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DB(this);
        settings = new Settings(this);
        // 테이블 생성
        CreateTables.statics(this, false);


    }

    protected void setBackground(View v) {

        Log.e(TAG, "set.getBackgroundType():" + settings.getBackgroundType());
        switch(settings.getBackgroundType())
        {
            case 0:
                v.setBackground(getResources().getDrawable(R.drawable.list_background));
                break;
            case 1:
                v.setBackground(getResources().getDrawable(R.drawable.list_background2));
                break;
            case 2:
                v.setBackground(getResources().getDrawable(R.drawable.list_background3));
                break;
            case 3:
                v.setBackground(getResources().getDrawable(R.drawable.list_background4));
                break;
            case 4:
                v.setBackground(getResources().getDrawable(R.drawable.list_background5));
                break;
            case 5:
                v.setBackground(getResources().getDrawable(R.drawable.list_background6));
                break;
            case 6:
                v.setBackground(getResources().getDrawable(R.drawable.list_background7));
                break;
            default:
                v.setBackground(getResources().getDrawable(R.drawable.list_background));

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
