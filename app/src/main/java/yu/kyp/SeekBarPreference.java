package yu.kyp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;


public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener {
    private SeekBar bar;
    private TextView text;
    private Context mContext;
    private static SharedPreferences sp;
    private static final String OPT_SEEKBAR_KEY = "button_percent_setting";
    private static final int OPT_SEEKBAR_DEF = 30;
    private static final int LAYOUT_PADDING = 10;
    private int currentvalue;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {

        // TODO Auto-generated method stub
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    protected View onCreateDialogView() {
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(LAYOUT_PADDING, LAYOUT_PADDING, LAYOUT_PADDING, LAYOUT_PADDING);
        bar = new SeekBar(mContext);
        bar.setOnSeekBarChangeListener(this);
        bar.setProgress(getValue());
        text = new TextView(mContext);
        currentvalue = bar.getProgress() + 100;
        text.setText(Integer.toString(currentvalue));
        text.setGravity(Gravity.CENTER);
        layout.addView(bar, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        layout.addView(text,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        this.bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                @Override
                                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                    currentvalue = progress + 100;
                                                    text.setText(Integer.toString(currentvalue));
                                                }

                                                @Override
                                                public void onStartTrackingTouch(SeekBar seekBar) {

                                                }

                                                @Override
                                                public void onStopTrackingTouch(SeekBar seekBar) {

                                                }
                                            }
        );
        return layout;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            setValue(bar.getProgress());

        }
    }

    private void setValue(int value) {
        Editor ed = sp.edit();
        ed.putInt(OPT_SEEKBAR_KEY, value);
        ed.commit();
    }

    private int getValue() {
        return sp.getInt(OPT_SEEKBAR_KEY, OPT_SEEKBAR_DEF);
    }
}

