package yu.kyp.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.text.method.Touch;
import android.view.MotionEvent;
import android.view.View;

import yu.kyp.common.Utils;

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
    static final float TOUCH_TOLERANCE = 4;
    private static final boolean RENDERING_ANTIALIAS = true;
    private static final boolean DITHER_FLAG = true;
    public Paint mPaint = null;
    /**
     * Undo 목록
     */
    UndoList undo = new UndoList();
    public PointF pointCanvas = new PointF();

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
        Matrix matrix = ((TouchImageView)v).getImageMatrix();
        pointCanvas = Utils.TransformTouchPointToCanvasPoint(matrix, event.getX(), event.getY());


        //Log.e(TAG,String.format("(%.0f,%.0f)->(%.0f,%.0f)",event.getX(),event.getY(),x,y));

        //======================================================
        // 2. ACTION_UP ACTION_DOWN ACTION_MOVE 처리
        switch (event.getAction()) {
            //손을 떼었을 때
            case MotionEvent.ACTION_UP:
                // 1.touchUp 메소드 호출
                Rect rect = touchUp(pointCanvas.x, pointCanvas.y, v);

                //화면을 갱신한다.
                if (rect != null) {
                    v.invalidate(rect);
                }
                else
                    v.invalidate();

                //2015-05-23 윤동수 주석
                //Path 객체 초기화
                //mPath.rewind();
                //mPath.reset();
                // undo 목록에 넣기
                undo.addList(((TouchImageView)v).getWriteBitmap());


                return true;
            //화면에 손을 댔을 때
            case MotionEvent.ACTION_DOWN:
                //touchDown()메소드 호출
                rect = touchDown(pointCanvas.x,pointCanvas.y);

                //화면을 갱신한다.
                if (rect != null) {
                    v.invalidate(rect);
                }

                // 젤 처음 화면으로 undo하기 위해서 필요함.
                // undo 목록에 넣기
                if(undo.size()==0)
                {
                    undo.addList(((TouchImageView)v).getWriteBitmap());
                }
                return true;
            //움직일 때
            case MotionEvent.ACTION_MOVE:
                //touchMove() 메소드 호출
                rect = touchMove(pointCanvas.x,pointCanvas.y,v);

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
        mPath.lineTo(x,y);
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
    private Rect touchMove(final float x, final float y, final View v) {

        Rect rect = processMove(x,y,v);

        return rect;
    }

    private Rect touchUp(final float x, final float y, final View v) {
        mPath.lineTo(x,y);
        // 2015-05-23 추가: 기존에 그은 것을 초기화하고 다시 그린다.
        drawPath((TouchImageView) v);
        //Rect rect = processMove(x,y);
        //return rect;
        return null;
    }

    /**
     * 기존에 그은 것을 삭제하고 mPath를 캔버스에 다시 drawPath한다.
     * @param v
     */
    private void drawPath(TouchImageView v) {
        Canvas canvas = v.getWriteCanvas();
        Bitmap prev = undo.getLast();
        if (prev != null){
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            canvas.drawBitmap(prev, 0, 0, null);
        }
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * Process Move Coordinates
     * x,y값을 mPath에 넣어서 라인을 quadTo를 사용해서 그린다
     * lastX,lastY값을 사용한다
     *
     * @return
     */
    private Rect processMove(final float x, final float y,final View v) {            /******************************/


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

            // 2015-05-23 추가: 기존에 그은 것을 삭제하고 다시 그린다.
            drawPath((TouchImageView) v);


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
