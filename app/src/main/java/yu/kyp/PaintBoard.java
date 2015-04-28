package yu.kyp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Stack;

/**
 * 페인트보드에 기능 추가
 * Path를 사용하는 방식으로 수정
 *
 * @author Mike
 *
 */
public class PaintBoard extends View {


    private static final String TAG = PaintBoard.class.getSimpleName();
    /**
     * Changed flag
     */
    public boolean changed = false;

    //추가

    private boolean mEraserMode = false;
    /**
     * Undo data
     */
    Stack undos = new Stack();

    //stack -> Arraylist로 변경
    ArrayList<Bitmap> undo = new ArrayList<Bitmap>();
    /**
     * Maximum Undos
     */
    public static int maxUndos = 11;
    private int index=0;
    /**
     * Canvas instance
     */
    Canvas mCanvas;
    Canvas c;
    /**
     * Bitmap for double buffering(즐겨찾기 참고할 것)
     */
    Bitmap mBitmap;

    /**
     * Paint instance
     */
    Paint mPaint;

    /**
     * X coordinate
     */
    float lastX;

    /**
     * Y coordinate
     */
    float lastY;

    private Path mPath = new Path();

    private float mCurveEndX;
    private float mCurveEndY;

    private int mInvalidateExtraBorder = 10;

    static final float TOUCH_TOLERANCE = 8;

    private static final boolean RENDERING_ANTIALIAS = true;
    private static final boolean DITHER_FLAG = true;

    private int mCertainColor = Color.BLACK;
    private float mStrokeWidth = 2.0f;

    static int temp_color;
    static int temp_thickness;



    class Stroke{
        //int stroke_no;
        int color;
        int thickness;
        ArrayList<PointData> listPoint = new ArrayList<PointData>();
        Stroke(){};
        Stroke(int temp_color, int temp_thickness, ArrayList<PointData> temp_pointdata){
            color=temp_color;
            thickness= temp_thickness;
            listPoint = temp_pointdata;
        }

    }
    class PointData{
        //int point_no;
        //int stroke_no;
        float x;
        float y;
        PointData(){};
        PointData(float temp_x,float temp_y){
            x=temp_x;
            y=temp_y;
        }
    }

    ArrayList<Stroke> stroke = new ArrayList<Stroke>();
    Stroke s = new Stroke();
//    Path for_draw = new Path();



    /**
     * Initialize paint object and coordinates
     *
     */
    public PaintBoard(Context context) {
        super(context);

        // create a new paint object
        mPaint = new Paint();
        mPaint.setAntiAlias(RENDERING_ANTIALIAS);       //경계에 중간색 설정
        mPaint.setColor(mCertainColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setDither(DITHER_FLAG);      //이미지보다 장비의 표현력이 떨어질때 이미지 색상을 낮추어 출력


        lastX = -1;
        lastY = -1;

        //Log.i("GoodPaintBoard", "initialized.");
    }


    /**
     * Undo
     */
    public void undo()
    {
        Bitmap prev = null;
        try {
            //prev = (Bitmap)undos.pop();
            prev = (Bitmap)undo.get(index-1);
            undo.remove(index-1);
            index--;
        } catch(Exception ex) {
            //Log.e("GoodPaintBoard", "Exception : " + ex.getMessage());
        }

        if (prev != null){
            drawBackground(mCanvas);
            mCanvas.drawBitmap(prev, 0, 0, mPaint);
            invalidate();

            prev.recycle();
        }

        //Log.i("GoodPaintBoard", "undo() called.");
    }

    /**
     * Paint background
     *
     */
    public void drawBackground(Canvas canvas)
    {
       // if (canvas != null) {
        //    canvas.drawColor(Color.BLACK);                       //캔버스의 배경색 설정
       // }
        canvas.drawColor(Color.YELLOW);
       //bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);

       // canvas.drawBitmap(bitmap,0,0,null);
    }

    /***
     * Update paint properties
     * @param color
     * @param size
     */
    public void updatePaintProperty(int color, int size)
    {
        mEraserMode = false;
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xFF);

        mPaint.setColor(color);
        Log.v("!!!","pen color"+color);
        mPaint.setStrokeWidth(size);
        temp_color=color;
        temp_thickness=size;
        //Log.d("!!!!!!!!!!","값 나오는 중"+temp_color);
    }

    public void setEraserPaint(int size) {
        Log.d("!!!!","지우개모드들어옴");
        mEraserMode=true;
        mPaint.setXfermode(null);
        mPaint.setAlpha(0);
        mPaint.setXfermode(new PorterDuffXfermode(
                PorterDuff.Mode.CLEAR));
        mPaint.setStrokeWidth(size);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(size);
        //temp_thickness=size;
    }
    /**
     * Create a new image
     */
    public void newImage(int width, int height)
    {
        if(mBitmap != null){
            mBitmap.recycle();
        }
        c = new Canvas();
        drawBackground(c);

        Bitmap img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(img);

        mBitmap = img;
        mCanvas = canvas;



        changed = false;

        Log.i("!!!!!!!!!!", "newImage");
        invalidate();
    }

    /**
     * Set image
     *
     * @param newImage
     */
    public void setImage(Bitmap newImage)
    {
        changed = false;


        setImageSize(newImage.getWidth(), newImage.getHeight(), newImage);
        Log.d("!!!!!!!!!!","setImage");
        invalidate();
    }

    /**
     * Set image size
     *
     * @param width
     * @param height
     * @param newImage
     */
    public void setImageSize(int width, int height, Bitmap newImage)
    {
        if (mBitmap != null){
            if (width < mBitmap.getWidth()) width = mBitmap.getWidth();
            if (height < mBitmap.getHeight()) height = mBitmap.getHeight();
        }

        if (width < 1 || height < 1) return;
        c = new Canvas();
        drawBackground(c);

        Bitmap img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();


        if (newImage != null) {
            canvas.setBitmap(newImage);
        }

        if (mBitmap != null) {
            mBitmap.recycle();
            mCanvas.restore();
        }

        mBitmap = img;
        mCanvas = canvas;

        while(true) {
            //Bitmap prev = (Bitmap)undos.pop();
            Bitmap prev = (Bitmap)undo.get(index-1);
            undo.remove(index-1);
            index--;
            if (prev == null) return;

            prev.recycle();
        }
    }



    /**
     * onSizeChanged
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w > 0 && h > 0) {
            newImage(w, h);
        }
    }

    /**
     * Draw the bitmap
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        Log.i("onDraw","");
        Log.d("!!!!!!!!!!","ondraw");

        //c.drawBitmap(mBitmap, 0, 0, null);
        //canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawColor(Color.YELLOW);



        if(!mEraserMode)
            canvas.drawPath(mPath, mPaint);

        canvas.drawBitmap(mBitmap, 0, 0, null);


    }

    /**
     * Handles touch event, UP, DOWN and MOVE                        (for drawing)
     */
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_UP:
//                Log.i("draw", "actionup called.");
                changed = true;

                this.getParent().requestDisallowInterceptTouchEvent(false);
                Rect rect = touchUp(event, false);      //터치 메소드 호출
                s = null;   // Stroke 인스턴스 삭제
                if (rect != null) {
                    invalidate(rect);
                }
               /* this.getParent().requestDisallowInterceptTouchEvent(true);
                touchUp(event,false);
                invalidate();*/
                mPath.rewind();

                return true;

            case MotionEvent.ACTION_DOWN:
//                Log.i("draw", "actiondown called.");

                if (mBitmap == null){
                    ;
                }



                Bitmap img = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas();
                canvas.setBitmap(img);
                canvas.drawBitmap(mBitmap, 0, 0, null);

                //undos.push(img);

                undo.add(img);
                index++;
                Log.i("!!!", "push 됨?");

                int i=0;
                while (undo.size() >= maxUndos){
                    //Bitmap i = (Bitmap)undos.get(undos.size()-1);
                    //Bitmap i = (Bitmap)undo.get();
                    //i.recycle();
                    //undos.remove(i);
                    undo.remove(i);
                    i++;
                    index--;
                    // Log.i("saveundo","" +);
                }

                this.getParent().requestDisallowInterceptTouchEvent(true);
                rect = touchDown(event);


                if (rect != null) {
                    invalidate(rect);
                }
               /* this.getParent().requestDisallowInterceptTouchEvent(true);
                touchDown(event);
                invalidate();*/
                return true;

            case MotionEvent.ACTION_MOVE:
//                Log.i("draw", "actionmove called.");

                this.getParent().requestDisallowInterceptTouchEvent(true);
                rect = touchMove(event);


                if (rect != null) {
                    invalidate(rect);
                }
               /* this.getParent().requestDisallowInterceptTouchEvent(true);
                touchMove(event);
                invalidate();*/

                return true;
        }

        return false;
    }



    /**
     * Process event for touch down
     *
     * @param event
     * @return
     */
    private Rect touchDown(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int i;

        s = new Stroke();

//        temp_x = x;
//        temp_y = y;


        s.listPoint.add(new PointData(x, y));


        lastX = x;
        lastY = y;

        mPath.reset();

        Rect mInvalidRect = new Rect();
        mPath.moveTo(x, y);             //현재 좌표값 추가

        /**********************/

        final int border = mInvalidateExtraBorder;
        mInvalidRect.set((int) x - border, (int) y - border, (int) x + border, (int) y + border);
        //다시 그려질 영역으로 현재 이동한 좌표 추가

        mCurveEndX = x;
        mCurveEndY = y;


        mCanvas.drawPath(mPath, mPaint);

        return mInvalidRect;
    }


    /**
     * Process event for touch move
     *
     * @param event
     * @return
     */
    private Rect touchMove(MotionEvent event) {

        Rect rect = processMove(event);

        return rect;
    }

    private Rect touchUp(MotionEvent event, boolean cancel) {
        Rect rect = processMove(event);
        int i,j;
        int size;

        stroke.add(new Stroke(temp_color,temp_thickness,s.listPoint));

            for(i=0; i<stroke.size(); i++) {
//            Log.i("i는","? " + i);

                size = stroke.get(i).listPoint.size();
                Log.i("color", ", size" + stroke.get(i).color + ", " + stroke.get(i).thickness);

//            Log.i("size는","? " + size);
                for (j = 0; j < size; j++) {
//                Log.i("터치업","" + stroke.get(i).listPoint.get(j).x + ", " + stroke.get(i).listPoint.get(j).y);

                }
            }


//        s.listPoint.clear();
//        stroke.clear();
        return rect;
    }

    /**
     * Process Move Coordinates
     * @param event
     * @return
     */
    private Rect processMove(MotionEvent event) {            /******************************/

        final float x = event.getX();
        final float y = event.getY();
        PointData p = new PointData(x, y);
        s.listPoint.add(p);
        Rect mInvalidRect = drawPointData(p, mPath, mPaint);

        return mInvalidRect;
    }

    /**
     * x,y값을 mPath에 넣어서 라인을 quadTo를 사용해서 그린다
     * lastX,lastY값을 사용한다
     *
     */
    private Rect drawPointData(PointData p, Path path, Paint paint) {

        float x = p.x;
        float y = p.y;

        final float dx = Math.abs(x - lastX);
        final float dy = Math.abs(y - lastY);

        Rect mInvalidRect = new Rect();
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            final int border = mInvalidateExtraBorder;
            mInvalidRect.set((int) mCurveEndX - border, (int) mCurveEndY - border,      //이동좌표 추가
                    (int) mCurveEndX + border, (int) mCurveEndY + border);

            float cX = mCurveEndX = (x + lastX) / 2;
            float cY = mCurveEndY = (y + lastY) / 2;


            path.quadTo(lastX, lastY, cX, cY);     //패스 객체에 현재 좌표값을 곡선으로 추가

            // union with the control point of the new curve
            mInvalidRect.union((int) lastX - border, (int) lastY - border,
                    (int) lastX + border, (int) lastY + border);

            // union with the end point of the new curve
            mInvalidRect.union((int) cX - border, (int) cY - border,
                    (int) cX + border, (int) cY + border);

            lastX = x;
            lastY = y;

            mCanvas.drawPath(path, paint);

        }
        return mInvalidRect;
    }

    /**
     * Save this contents into a Jpeg image
     *
     * @param outstream
     * @return
     */
    public boolean Save(OutputStream outstream) {
        try {
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
            invalidate();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
