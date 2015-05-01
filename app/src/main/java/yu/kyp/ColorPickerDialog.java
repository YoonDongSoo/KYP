package yu.kyp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class ColorPickerDialog extends Dialog {

    public interface OnColorChangedListener {
        void colorChanged(int color);
    }

    private OnColorChangedListener mListener;
    private int mInitialColor;

    private static class ColorPickerView extends View {
        private Paint mPaint;
        private Paint mCenterPaint;
        private final int[] mColors;
        private OnColorChangedListener mListener;

        ColorPickerView(Context c, OnColorChangedListener l, int color) {
            super(c);
            mListener = l;
            mColors = new int[] {
                    0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00,
                    0xFFFFFF00, 0xFFFF0000
            };

            //위의 색상들을 사용해 원형 그라디언트 생성성
           Shader s = new SweepGradient(0, 0, mColors, null);

            //테두리 원
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);      //중간색 설정
            mPaint.setShader(s);
            mPaint.setStyle(Paint.Style.STROKE);        //테두리
            mPaint.setStrokeWidth(80);

            //중앙의 현재 색상을 표시하는 원원
            mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);        //중간색 설정
            mCenterPaint.setColor(color);
            mCenterPaint.setStrokeWidth(5);
        }

        private boolean mTrackingCenter;
        private boolean mHighlightCenter;

        @Override
        protected void onDraw(Canvas canvas) {
            float r = CENTER_X - mPaint.getStrokeWidth()*0.5f;

            //(0,0) 좌표값을 (CENTER_X, CENTER_X)로 바꿈
            canvas.translate(CENTER_X, CENTER_X);

            //new RectF(-r, -r, r, r)에  mPaint 객체를 사용하여
            //원형 그라디언트를 나타낼 바깥원을 그림(영역에 내접하는 원)
            canvas.drawOval(new RectF(-r, -r, r, r), mPaint);

            //CENTER_X, CENTER_X)에  mCenterPaint 객체를 사용하여
            //터치한 부분의 색상 값이 나타나게 하는 반지름이 CENTER_RADIUS인 중앙원을 그림
            canvas.drawCircle(0, 0, CENTER_RADIUS, mCenterPaint);

//            if (mTrackingCenter) {
//
//                int c = mCenterPaint.getColor();
                //외각선을 그림
//                mCenterPaint.setStyle(Paint.Style.STROKE);

//                if (mHighlightCenter) {
//                    mCenterPaint.setAlpha(0xFF);        //객체 투명도 설정
//                    Log.d("투명도1","");
//                } else {
//                    mCenterPaint.setAlpha(0x80);        //객체 투명도 설정
//                    Log.d("투명도222222","");
//                }

                //중앙원을 눌러 색상이 선택되었을 때
                //잠깐 나타나는 원
//                canvas.drawCircle(0, 0,
//                        CENTER_RADIUS + mCenterPaint.getStrokeWidth(),
//                        mCenterPaint);

//                mCenterPaint.setStyle(Paint.Style.FILL);
//                mCenterPaint.setColor(c);
//            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(CENTER_X*2, CENTER_Y*2);
        }

        private static final int CENTER_X = 200;
        private static final int CENTER_Y = 200;
        private static final int CENTER_RADIUS = 50;

        private int floatToByte(float x) {
            int n = java.lang.Math.round(x);
            return n;
        }
        private int pinToByte(int n) {
            if (n < 0) {
                n = 0;
            } else if (n > 255) {
                n = 255;
            }
            return n;
        }

        private int ave(int s, int d, float p) {
            return s + java.lang.Math.round(p * (d - s));
        }

        private int interpColor(int colors[], float unit) {
            if (unit <= 0) {
                return colors[0];
            }
            if (unit >= 1) {
                return colors[colors.length - 1];
            }

            float p = unit * (colors.length - 1);
            int i = (int)p;
            //p = p-i
            p -= i;

            // now p is just the fractional part [0...1) and i is the index
            int c0 = colors[i];
            int c1 = colors[i+1];
            int a = ave(Color.alpha(c0), Color.alpha(c1), p);
            int r = ave(Color.red(c0), Color.red(c1), p);
            int g = ave(Color.green(c0), Color.green(c1), p);
            int b = ave(Color.blue(c0), Color.blue(c1), p);

            return Color.argb(a, r, g, b);      //버튼 색 설정(a:투명도)
        }

        private int rotateColor(int color, float rad) {
            float deg = rad * 180 / 3.1415927f;
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);

            ColorMatrix cm = new ColorMatrix();
            ColorMatrix tmp = new ColorMatrix();

            cm.setRGB2YUV();
            tmp.setRotate(0, deg);
            cm.postConcat(tmp);
            tmp.setYUV2RGB();
            cm.postConcat(tmp);

            final float[] a = cm.getArray();

            int ir = floatToByte(a[0] * r +  a[1] * g +  a[2] * b);
            int ig = floatToByte(a[5] * r +  a[6] * g +  a[7] * b);
            int ib = floatToByte(a[10] * r + a[11] * g + a[12] * b);

            return Color.argb(Color.alpha(color), pinToByte(ir),
                    pinToByte(ig), pinToByte(ib));
        }

        private static final float PI = 3.1415926f;

        /**
         * 테두리 원 터치와 중앙의 현재 색상을 나타내는 원 터치 처리
         * @param event
         * @return
         */
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX() - CENTER_X;
            float y = event.getY() - CENTER_Y;

            //sqrt한 값이 CENTER_RADIUS보다 작거나 같다면 true
            //sqrt한 값이 CENTER_RADIUS보다 크다면 false
            boolean inCenter = java.lang.Math.sqrt(x*x + y*y) <= CENTER_RADIUS;

            //터치 액션 값을 인식
            switch (event.getAction()) {

                //1. 터치다운일때
                case MotionEvent.ACTION_DOWN:
                    mTrackingCenter = inCenter;
                    if (inCenter) {
                        mHighlightCenter = true;
                        invalidate();
                        break;
                    }

                    //2. 터치무브일때
                case MotionEvent.ACTION_MOVE:
                    if (mTrackingCenter) {
                        if (mHighlightCenter != inCenter) {
                            mHighlightCenter = inCenter;
                            invalidate();
                        }
                    } else {
                        //각도를 구하기 위해 arc tangent(높이, 밑변) 사용
                        float angle = (float)java.lang.Math.atan2(y, x);
                        // need to turn angle [-PI ... PI] into unit [0....1]
                        float unit = angle/(2*PI);
                        if (unit < 0) {
                            unit += 1;
                        }

                        //터치된 곳에 대응하는 색상을 중앙에 표시함
                        mCenterPaint.setColor(interpColor(mColors, unit));
                        invalidate();
                    }
                    break;

                //3. 터치업일때
                case MotionEvent.ACTION_UP:
                    if (mTrackingCenter) {
                        if (inCenter) {
                            //마지막 색상 값을 중앙에 표시
                            mListener.colorChanged(mCenterPaint.getColor());
                        }
                        mTrackingCenter = false;    // so we draw w/o halo
                        invalidate();
                    }
                    break;
            }
            return true;
        }
    }

    public ColorPickerDialog(Context context,
                             OnColorChangedListener listener,
                             int initialColor) {
        super(context);

        mListener = listener;
        mInitialColor = initialColor;
    }

    /**
     * 컬러피커 중앙의 색을 선택(클릭)하여 다양한 색의 펜 사용 가능
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //2. 만약 컬러피커 중앙의 색이 선택된 경우
        OnColorChangedListener l = new OnColorChangedListener() {
            public void colorChanged(int color) {
//                Log.d("picker","색상 변경");
                mListener.colorChanged(color);
                dismiss();
            }
        };

        //1. 다양색을 선택할 수 있는 컬러피커 팔레트가 뜸
        setContentView(new ColorPickerView(getContext(), l, mInitialColor));
        setTitle("Pick a Color");
    }
}