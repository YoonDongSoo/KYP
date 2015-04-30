package yu.kyp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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

import yu.kyp.image.UndoList;

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
    UndoList undo = new UndoList();
    /**
     * Maximum Undos
     */
    public static int maxUndos = 5;
    private int index=0;
    /**
     * Canvas instance
     */
    Canvas canvasWrite;
    Canvas canvasBackground;
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

    private float sx = 1f;
    private float sy = 1f;
    private float x, y;
    private float width, height;
    private float lastXF = 0f;
    private float lastYF  = 0f;
    private float top, bottom, left, right;
    /**
     * 화면 터치가 되면? 터치한 위치를 중심으로 줌인아웃
     * 화면 터기가 되지 않으면? 화면 중앙을 중심으로 줌인아웃
     */
    private boolean istouched = false;
    /**
     * 터치한 좌표가 글쓰기 화면을 벗어나는지 확인해서
     * 화면안에 들어왔을 때에만 줌인이 적용되도록 처리할 때 사용하는 x,y좌표값.
     */
    private float touchx, touchy;
    private float globalX, globalY;
    private float scalewidth, scaleheight;

    /**
     * View의 크기는 onResume에서 구하면 안된다.
     * 유저에게 액티비티가 보여지는 시점에 이 메소드가 호출된다.
     * 이 함수 안에서 view의 크기를 구해서 입력해둔다.
     * @param hasWindowFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        top = this.getTop();
        bottom = this.getBottom();
        left = this.getLeft();
        right = this.getRight();

        width = right-left;
        height = bottom-top;


    }

    /**
     * PaintBoard View를 25% 확대한다.
     */
    public void zoomInBitmap() {
        zoomBitmap(0.25f);
    }

    public void zoomOutBitmap() {
        zoomBitmap(-0.25f);
    }

    public void zoomBitmap(float additionalFactor) {
        // 배율이 25% 밑으로 떨어지지 않도록 처리.
        if(sx+additionalFactor <= 0f || sy+additionalFactor<= 0f)
            return;
        // 배율이 100%보다 작을 때에는 이미지를 center에 놓이도록 한다.
        if(sx+additionalFactor < 1f || sy+additionalFactor< 1f)
        {
            x = right/2;
            y = bottom/2;

        }

        Paint paintLine = new Paint();  // 선을 긋기 위한 페인트 생성
        paintLine.setARGB(70, 255, 0, 0);
        paintLine.setStrokeWidth(5);  // 굵기

        float[] values = new float[9];
        int x1, y1, x2, y2;


        sx += additionalFactor;
        sy += additionalFactor;


        drawBackground(canvasWrite);


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
        globalX = x1;
        globalY = y1;
        int scalewidth = x2-x1;
        int scaleheight = y2-y1;

        Bitmap bitmap = undo.getLast();
        canvasWrite.drawBitmap(bitmap, new Rect(0 ,0,(int)width, (int)height), new Rect(x1, y1, x2, y2), null/*mPaint*/);
        canvasWrite.drawLine(0, (bottom - 0) / 2, right, (bottom - 0) / 2, paintLine);
        canvasWrite.drawLine(right / 2, 0, right / 2, bottom, paintLine);
        //canvasWrite.drawRect(globalX, globalY, globalX+scalewidth, globalY+scaleheight, paintLine);   // 사각형
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

    public void zoomResetBitmap() {
        globalX=0f; globalY=top;
        scalewidth=width; scaleheight=height;
        istouched = false;


        Paint paintLine = new Paint();  // 선을 긋기 위한 페인트 생성
        paintLine.setARGB(70, 255, 0, 0);
        paintLine.setStrokeWidth(5);  // 굵기

        sx = 1f;
        sy = 1f;


        Matrix zoom = new Matrix();
        zoom.postScale(sx, sy, right / 2, (bottom - top) / 2);

        Bitmap bitmap = undo.getLast();
        drawBackground(canvasWrite);
        canvasWrite.drawBitmap(bitmap, zoom, mPaint);
        canvasWrite.drawLine(0, (bottom - top) / 2, right, (bottom - top) / 2, paintLine);
        canvasWrite.drawLine(right / 2, 0, right / 2, bottom, paintLine);

        invalidate();
    }



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
     * undo 개수가
     */
    public void undo()
    {
        undo.pop();
        Bitmap prev = undo.getLast();
        if (prev != null){
            drawBackground(canvasWrite);

            canvasWrite.drawBitmap(prev, 0, 0, null/*mPaint*/);
            invalidate();


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
        Log.v("!!!", "pen color" + color);
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
        canvasBackground = new Canvas();
        drawBackground(canvasBackground);

        Bitmap img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(img);

        mBitmap = img;
        canvasWrite = canvas;



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
        canvasBackground = new Canvas();
        drawBackground(canvasBackground);

        Bitmap img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();


        if (newImage != null) {
            canvas.setBitmap(newImage);
        }

        if (mBitmap != null) {
            mBitmap.recycle();
            canvasWrite.restore();
        }

        mBitmap = img;
        canvasWrite = canvas;

        undo.clearList();
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

        //canvasBackground.drawBitmap(mBitmap, 0, 0, null);
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

                // undo 목록에 넣기
                Bitmap img = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas();
                canvas.setBitmap(img);
                canvas.drawBitmap(mBitmap, 0, 0, null);
                undo.addList(img);

                return true;

            case MotionEvent.ACTION_DOWN:
//                Log.i("draw", "actiondown called.");

                if (mBitmap == null){
                    ;
                }

                this.getParent().requestDisallowInterceptTouchEvent(true);
                rect = touchDown(event);



                if (rect != null) {
                    invalidate(rect);
                }


                Log.i("!!!", "push 됨?");

               /* this.getParent().requestDisallowInterceptTouchEvent(true);
                touchDown(event);
                invalidate();*/

                if(undo.size()==0)
                {
                    // undo 목록에 넣기
                    img = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    canvas = new Canvas();
                    canvas.setBitmap(img);
                    canvas.drawBitmap(mBitmap, 0, 0, null);
                    undo.addList(img);
                }

                //===================================
                //터치 관련 처리
                //===================================
                x = event.getRawX();
                y = event.getRawY();
                istouched = true;
                touchx = x; touchy =y;
                String msg = "터치를 입력받음 : " + x + " / " + y;
                Log.d(TAG,msg);
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


        canvasWrite.drawPath(mPath, mPaint);

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

            canvasWrite.drawPath(path, paint);

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
