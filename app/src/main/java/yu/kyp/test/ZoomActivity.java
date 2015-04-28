package yu.kyp.test;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import yu.kyp.R;


public class ZoomActivity extends Activity {
    private MainView mainView;
    private Canvas cacheCanvas;
    private Paint mPaint;
    private Bitmap cacheBitmap;
    private Bitmap bitmap;
    private Matrix zoom;


    private float globalX, globalY;

     // zoom x배율


    private float sx = 1f;
    // zoom y배율


    private float sy = 1f;

    private float x, y;
    private float touchx, touchy;
    private boolean istouched = false;

    private float top, bottom, left, right;
    private float width, height, scalewidth, scaleheight;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x= 0f; y=0f;
        setContentView(R.layout.activity_zoom);

        mainView = new MainView(this);
        LinearLayout linear = (LinearLayout) findViewById(R.id.linearLayout1);
        linear.addView(mainView,0);

    }


    public class MainView extends View {
        private float lastXF = 0f;
        private float lastYF  = 0f;

        public MainView(Context context) {
            super(context);
            mPaint = new Paint();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1200);

            setLayoutParams(params);

        }

        //화면에 보여지기 전에 호출되는 메소드
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {

            createCacheBitmap(w, h);
            testDrawing();

        }

        //새로운 비트맵 이미지를 메모리에 생성
        private void createCacheBitmap(int w, int h) {
            cacheBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            cacheCanvas = new Canvas();
            cacheCanvas.setBitmap(cacheBitmap);


        }


        private void testDrawing() {


            cacheCanvas.drawColor(Color.WHITE);
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cat2);

            cacheCanvas.drawBitmap(bitmap, 0, 0, mPaint);

            top = mainView.getTop();
            bottom = mainView.getBottom();
            left = mainView.getLeft();
            right = mainView.getRight();


            width = right-left; //bitmap.getWidth();
            height = bottom-top; //bitmap.getHeight();

            globalX=0f; globalY=top;
            scalewidth=width; scaleheight=height;

            //invalidate();
        }

        protected void onDraw(Canvas canvas) {
            if (cacheBitmap != null) {
                canvas.drawBitmap(cacheBitmap, 0, 0, null);
            }
        }


        public boolean onTouchEvent(MotionEvent event) {
            // TODO Auto-generated method stub
            super.onTouchEvent(event);

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                x = event.getRawX();
                y = event.getRawY();

                istouched = true;
                touchx = x; touchy =y;

                String msg = "터치를 입력받음 : " + x + " / " + y;

                Toast.makeText(ZoomActivity.this, msg, Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        }


        private void zoomInBitmap() {

            zoomBitmap(0.25f);
        }

        private void zoomOutBitmap() {
            zoomBitmap(-0.25f);
        }
        private void zoomBitmap(float additionalFactor) {
            // 배율이 25% 밑으로 떨어지지 않도록 처리.
            if(sx+additionalFactor <= 0f || sy+additionalFactor<= 0f)
                return;
 // 배율이 100%보다 작을 때에는 이미지를 center에 놓이도록 한다.
            if(sx+additionalFactor < 1f || sy+additionalFactor< 1f)
            {
                x = right/2;
                y = bottom/2;

            }


            zoom = new Matrix();
            Paint paintLine = new Paint();  // 선을 긋기 위한 페인트 생성
            paintLine.setARGB(70, 255, 0, 0);
            paintLine.setStrokeWidth(5);  // 굵기

            float[] values = new float[9];
            int x1, y1, x2, y2;


            sx += additionalFactor;
            sy += additionalFactor;


            cacheCanvas.drawColor(Color.BLACK);

            //터치값이 없을 경우 화면의 중앙을 중심으로 줌인/아웃
            if(istouched==false)
            {
                x= right/2;
                y = bottom/2;
            }
            float xF = getFixedX(x, sx - additionalFactor);
            float yF = getFixedY(y, sy - additionalFactor);


            // 배율이 100%보다 작을 때에는 이미지를 center에 놓이도록 한다.
            if(sx+additionalFactor < 1f || sy+additionalFactor< 1f)
            {
                xF = right/2;
                yF = bottom/2;
            }

            x1 =(int) ((1-sx)*xF);
            y1 = (int) (0*sy + (1-sy)*(yF));
            x2=(int)(width*sx +(1-sx)*xF);
            y2=(int)((height+0)*sy + (1-sy)*(yF));


            //이미지 좌표 값
            zoom.getValues(values);
            globalX = x1;
            globalY = y1;
            scalewidth = x2-x1;
            scaleheight = y2-y1;

            cacheCanvas.drawBitmap(bitmap, new Rect(0 ,0,(int)width, (int)height), new Rect(x1, y1, x2, y2), mPaint);
            cacheCanvas.drawLine(0, (bottom - 0) / 2, right, (bottom - 0) / 2, paintLine);
            cacheCanvas.drawLine(right / 2, 0, right / 2, bottom, paintLine);
            cacheCanvas.drawRect(globalX, globalY, globalX+scalewidth, globalY+scaleheight, paintLine);   // 사각형
            invalidate();

            lastXF = xF;
            lastYF = yF;

        }

        private float getFixedX(float x, float scaleX) {

            return (x-(1-scaleX)*lastXF)/scaleX;

        }
        private float getFixedY(float y, float scaleY) {

            return (y-(1-scaleY)*lastYF)/scaleY;
        }



        private void zoomResetBitmap() {
            globalX=0f; globalY=top;
            scalewidth=width; scaleheight=height;
            istouched = false;

            zoom = new Matrix();
            Paint paintLine = new Paint();  // 선을 긋기 위한 페인트 생성
            paintLine.setARGB(70, 255, 0, 0);
            paintLine.setStrokeWidth(5);  // 굵기

            sx = 1f;
            sy = 1f;


            cacheCanvas.drawColor(Color.BLACK);
            zoom.postScale(sx, sy, right / 2, (bottom - top) / 2);

            cacheCanvas.drawBitmap(bitmap, zoom, mPaint);
            cacheCanvas.drawLine(0, (bottom - top) / 2, right, (bottom - top) / 2, paintLine);
            cacheCanvas.drawLine(right / 2, 0, right / 2, bottom, paintLine);

            invalidate();

        }


        private void Zoom_Percent(int flag) {

            Paint paintLine = new Paint();  // 선을 긋기 위한 페인트 생성
            paintLine.setARGB(70, 255, 0, 0);
            paintLine.setStrokeWidth(5);  // 굵기

            cacheCanvas.drawColor(Color.BLACK);

            switch (flag) {
                case 1://100%확대
                    sx = 1f;
                    sy = 1f;
                    zoomBitmap(0f);
                    break;
                case 2://125%확대
                    sx = 1.25f;
                    sy = 1.25f;
                    zoomBitmap(0f);
                    break;
                case 3://150%확대
                    sx = 1.5f;
                    sy = 1.5f;
                    zoomBitmap(0f);
                    break;
                case 4://175%확대
                    sx = 1.75f;
                    sy = 1.75f;
                    zoomBitmap(0f);
                    break;
                case 5://200%확대
                    sx = 2f;
                    sy = 2f;
                    zoomBitmap(0f);
                    break;
                case 6://75%축소
                    sx = 0.75f;
                    sy = 0.75f;
                    zoomBitmap(0f);
                    break;
                case 7://50%축소
                    sx = 0.5f;
                    sy = 0.5f;
                    zoomBitmap(0f);
                    break;
                case 8://25%축소
                    sx = 0.25f;
                    sy = 0.25f;
                    zoomBitmap(0f);
                    break;
            }
        }
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upscale:

                if((globalX < touchx && touchx < globalX+scalewidth) && (globalY < (touchy-top) && (touchy-top) < globalY+scaleheight))
                    mainView.zoomInBitmap();

                else if(istouched == false)
                    mainView.zoomInBitmap();

                break;
            case R.id.downscale:

                if((globalX < touchx && touchx < globalX+scalewidth) && (globalY < (touchy-top) && (touchy-top) < globalY+scaleheight))
                    mainView.zoomOutBitmap();

                else if(istouched==false)
                    mainView.zoomOutBitmap();

                break;
            case R.id.reset:
                mainView.zoomResetBitmap();
                break;
            case R.id.zoomin100:
                mainView.Zoom_Percent(1);
                break;
            case R.id.zoomin125:
                mainView.Zoom_Percent(2);
                break;
            case R.id.zoomin150:
                mainView.Zoom_Percent(3);
                break;
            case R.id.zoomin175:
                mainView.Zoom_Percent(4);
                break;
            case R.id.zoomin200:
                mainView.Zoom_Percent(5);
                break;
            case R.id.zoomout75:
                mainView.Zoom_Percent(6);
                break;
            case R.id.zoomout50:
                mainView.Zoom_Percent(7);
                break;
            case R.id.zoomout25:
                mainView.Zoom_Percent(8);
                break;
        }

    }
}




