package yu.kyp.common.activity;

import android.app.Activity;
import android.os.Bundle;

import yu.kyp.common.Settings;
import yu.kyp.common.database.CreateTables;
import yu.kyp.common.database.DB;

/**
 * Created by DONGSOO on 2015-03-24.
 */
public class ActivityBase extends Activity {
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

    @Override
    protected void onStart() {
        super.onStart();
    }
}
