package yu.kyp.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by DONGSOO on 2015-05-17.
 */
public class PaintOnTouchListener implements View.OnTouchListener {

    private static final String TAG = PaintOnTouchListener.class.getSimpleName();
    public Path mPath = new Path();
    public float lastX = -1;
    public float lastY = -1;
    private float mCurveEndX;
    private float mCurveEndY;
    private int mInvalidateExtraBorder = 10;
    static final float TOUCH_TOLERANCE = 8;
    private static final boolean RENDERING_ANTIALIAS = true;
    private static final boolean DITHER_FLAG = true;
    public Paint mPaint = null;
    /**
     * Undo 목록
     */
    UndoList undo = new UndoList();

    public PaintOnTouchListener()
    {
        mPaint = new Paint();
        mPaint.setAntiAlias(RENDERING_ANTIALIAS);       //경계에 중간색 설정
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2.0f);
        mPaint.setDither(DITHER_FLAG);      //이미지보다 장비의 표현력이 떨어질때 이미지 색상을 낮추어 출력

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //======================================================
        // 1. 스케일과 offset을 가져와서 touchX,touchY값 맞추기
        float[] mv = new float[9];
        Matrix matrix = ((TouchImageView)v).getImageMatrix();
        matrix.getValues(mv);
        float x = (event.getX()*(1/mv[Matrix.MSCALE_Y]) - (mv[Matrix.MTRANS_X]/mv[Matrix.MSCALE_Y]));
        float y = (event.getY()*(1/mv[Matrix.MSCALE_Y]) - (mv[Matrix.MTRANS_Y]/mv[Matrix.MSCALE_Y]));

        //Log.e(TAG,String.format("(%.0f,%.0f)->(%.0f,%.0f)",event.getX(),event.getY(),x,y));

        //======================================================
        // 2. ACTION_UP ACTION_DOWN ACTION_MOVE 처리
        switch (event.getAction()) {
            //손을 떼었을 때
            case MotionEvent.ACTION_UP:
                //Log.i("draw", "actionup called.");
                v.getParent().requestDisallowInterceptTouchEvent(false);
                //touchUp 메소드 호출
                Rect rect = touchUp(x,y, false);
                //s = null;   // Stroke 인스턴스 삭제

                //화면을 갱신한다.
                if (rect != null) {
                    v.invalidate(rect);
                }

                //Path 객체 초기화
                mPath.rewind();

                // undo 목록에 넣기
                undo.addList(((TouchImageView)v).getWriteBitmap());

                return true;
            //화면에 손을 댔을 때
            case MotionEvent.ACTION_DOWN:
                //Log.i("draw", "actiondown called.");

//                    if (mBitmapWrite == null){
//                        ;
//                    }
                    //scrollview에 영향을 안받고 draw 기능 적용
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    //touchDown()메소드 호출
                    rect = touchDown(x,y);


                    //화면을 갱신한다.
                    if (rect != null) {
                        v.invalidate(rect);
                    }



                    if(undo.size()==0)
                    {
                        // undo 목록에 넣기
                        undo.addList(((TouchImageView)v).getWriteBitmap());
                    }

                    //===================================
                    //터치 관련 처리
                    //===================================
                    /*//좌표값 저장
                    x = event.getRawX();
                    y = event.getRawY();
                    //터치 상태
                    istouched = true;
                    touchx = x; touchy =y;
                    String msg = "터치를 입력받음 : " + x + " / " + y;
                    Log.d(TAG,msg);*/
                return true;
            //움직일 때
            case MotionEvent.ACTION_MOVE:
                //Log.i("draw", "actionmove called.");
                    //scrollview에 영향을 안받고 draw 기능 적용
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    //touchMove() 메소드 호출
                    rect = touchMove(x,y);

                    //화면을 갱신한다.
                    if (rect != null) {
                        v.invalidate(rect);
                    }

                return true;
        }
        return false;
    }


    /**
     * Process event for touch down
     *
     * @return
     */
    private Rect touchDown(final float x, final float y) {





        lastX = x;
        lastY = y;

        //Path 정보를 초기화
        mPath.reset();

        Rect mInvalidRect = new Rect();
        //Path객체에 현재 좌표값 추가
        mPath.moveTo(x, y);
        /**********************/

        /*final int border = mInvalidateExtraBorder;
        //다시 그려질 영역으로 현재 이동한 좌표 추가
        mInvalidRect.set((int) x - border, (int) y - border, (int) x + border, (int) y + border);

        mCurveEndX = x;
        mCurveEndY = y;

        //Path객체를 그린다.
        canvasWrite.drawPath(mPath, mPaint);*/

        return mInvalidRect;
    }


    /**
     * Process event for touch move
     *
     * @return
     */
    private Rect touchMove(final float x, final float y) {

        Rect rect = processMove(x,y);

        return rect;
    }

    private Rect touchUp(final float x, final float y, boolean cancel) {
        Rect rect = processMove(x,y);
        return rect;
    }

    /**
     * Process Move Coordinates
     * x,y값을 mPath에 넣어서 라인을 quadTo를 사용해서 그린다
     * lastX,lastY값을 사용한다
     *
     * @return
     */
    private Rect processMove(final float x, final float y) {            /******************************/


        final float dx = Math.abs(x - lastX);
        final float dy = Math.abs(y - lastY);

        Rect mInvalidRect = new Rect();
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            final int border = mInvalidateExtraBorder;
            //다시 그려질 영역으로 현재 이동한 좌표 추가
            mInvalidRect.set((int) mCurveEndX - border, (int) mCurveEndY - border,
                    (int) mCurveEndX + border, (int) mCurveEndY + border);

            float cX = mCurveEndX = (x + lastX) / 2;
            float cY = mCurveEndY = (y + lastY) / 2;

            //Path 객체에 현재 좌표값을 곡선으로 추가
            mPath.quadTo(lastX, lastY, cX, cY);

            // union with the control point of the new curve
            mInvalidRect.union((int) lastX - border, (int) lastY - border,
                    (int) lastX + border, (int) lastY + border);

            // union with the end point of the new curve
            mInvalidRect.union((int) cX - border, (int) cY - border,
                    (int) cX + border, (int) cY + border);

            lastX = x;
            lastY = y;

            /*//Path객체를 그린다.
            canvasWrite.drawPath(mPath, mPaint);*/
        }

        return mInvalidRect;
    }
}
