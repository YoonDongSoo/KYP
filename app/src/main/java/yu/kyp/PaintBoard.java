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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.OutputStream;
import java.util.ArrayList;

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
    private boolean mTextMode = false;
    /**
     * Undo data
     */
    //Stack undos = new Stack();

    //stack -> Arraylist로 변경
    UndoList undo = new UndoList();
    /**
     * Maximum Undos
     */
    public static int maxUndos = 11;
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

    //테스트
    static boolean iszoom = false;
    static Bitmap resize;
    //테스트
    int testwidth;
    int testheight;
    int originwidth;
    int originheight;
    MemoWriteActivity m = new MemoWriteActivity();

    /**
     * View의 크기는 onResume에서 구하면 안된다.
     * 유저에게 액티비티가 보여지는 시점에 이 메소드가 호출된다.
     * 이 함수 안에서 view의 크기를 구해서 입력해둔다.
     * @param hasWindowFocus
     */

    TextDialog textdialog;
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        top = this.getTop();
        bottom = this.getBottom();
        left = this.getLeft();
        right = this.getRight();

        width = right-left;
        height = bottom-top;

        //테스트
        DisplayMetrics metrics = new DisplayMetrics();

        testwidth = getContext().getResources().getDisplayMetrics().widthPixels;
        testheight = getContext().getResources().getDisplayMetrics().heightPixels;;

        originwidth = testwidth;
        originheight = testheight-m.topviewh-m.belowtopviewh-m.bottomviewh;
        Log.i("!!!","origin width"+originwidth);
        Log.i("!!!","origin height"+originheight);

        Log.i("!!!!","bottom"+bottom);
        Log.i("!!!!","testwidth"+testwidth);
        Log.i("!!!!","testheight"+testheight);
        Log.i("!!!","canvas width"+canvasWrite.getWidth());
        Log.i("!!!","canvas height"+canvasWrite.getHeight());
        Log.i("!!!","bitmap width"+mBitmap.getWidth());
        Log.i("!!!","bitmap height"+mBitmap.getHeight());
        Log.i("!!!!","testw"+testwidth);
        Log.i("!!!!","testh"+y);
    }

    private float getFixedX(float x, float scaleX) {

        return (x-(1-scaleX)*lastXF)/scaleX;

    }
    private float getFixedY(float y, float scaleY) {

        return (y-(1-scaleY)*lastYF)/scaleY;
    }
    /**
     * PaintBoard View를 원래상태로 리셋한다.
     */
    public void zoomResetBitmap() {
        globalX=0f; globalY=top;
        scalewidth=width; scaleheight=height;
        istouched = false;


        Paint paintLine = new Paint();  // 선을 긋기 위한 페인트 생성
        paintLine.setARGB(70, 255, 0, 0);
        paintLine.setStrokeWidth(5);  // 굵기

        sx = 1f;
        sy = 1f;

        //매트릭스를 만들어 화면 중앙을 중심으로 100% 배율로 확대한다.
        Matrix zoom = new Matrix();
        zoom.postScale(sx, sy, right / 2, (bottom - top) / 2);

        //매트릭스를 사용하여 마지막에 저장된 비트맵에 그린다.
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
        int scaledSize = getResources().getDimensionPixelSize(R.dimen.font_size);

        // create a new paint object
        mPaint = new Paint();
        mPaint.setAntiAlias(RENDERING_ANTIALIAS);       //경계에 중간색 설정
        mPaint.setColor(mCertainColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setDither(DITHER_FLAG);      //이미지보다 장비의 표현력이 떨어질때 이미지 색상을 낮추어 출력
        mPaint.setTextSize(scaledSize);
        textdialog = new TextDialog();


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
        //캔버스의 배경색 설정
        canvas.drawColor(Color.WHITE);

       // if (canvas != null) {
        //    canvas.drawColor(Color.BLACK);                       //캔버스의 배경색 설정
       // }
        canvas.drawColor(Color.WHITE);
       //bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);

       // canvas.drawBitmap(bitmap,0,0,null);
    }

    /**
     * print EditText's string
     * @param x
     * @param y
     */
    public void drawText(String str,float x, float y){
//        super.onDraw(canvas);

//        Log.i("onDraw","");
//        Log.d("!!!!!!!!!!","ondraw");

        //canvasBackground.drawBitmap(mBitmap, 0, 0, null);
        //canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        mPaint.setStyle(Paint.Style.FILL);
        canvasWrite.drawColor(Color.TRANSPARENT);
//
////        Rect rt = new Rect();
////        mPaint.getTextBounds(str,0,str.length(),rt);
////        rt.set((int) x, (int) y + rt.top, (int) x + rt.width(), (int) y + rt.bottom);
////        canvasWrite.drawRect(rt,ptRound);
        canvasWrite.drawText(str,x,y,mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
//        mTextMode = true;
    }

    /***
     * Update paint properties
     * @param color
     * @param size
     */
    public void updatePaintProperty(int color, int size)
    {
        //지우개 모드를 false로 변경
        mEraserMode = false;
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xFF);

        //전달받은 색상과 크기 적용
        mPaint.setColor(color);
        Log.v("!!!","pen color"+color);
        mPaint.setStrokeWidth(size);
        temp_color=color;
        temp_thickness=size;
        //Log.d("!!!!!!!!!!","값 나오는 중"+temp_color);
    }

    /**
     * 지우개 기능
     * @param size
     */
    public void setEraserPaint(int size) {
        Log.d("!!!!","지우개모드들어옴");
        //지우개 모드를 true로 바꾼다.
        mEraserMode=true;

        //setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR)) -> 검은색 펜
        // 을 이용하여 지우개와 동일한 기능을 사용할 수 있다.
        mPaint.setXfermode(null);
        mPaint.setAlpha(0);
        mPaint.setXfermode(new PorterDuffXfermode(
                PorterDuff.Mode.CLEAR));
        //지우개 크기 적용
        mPaint.setStrokeWidth(size);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(size);
        //temp_thickness=size;
    }
    /**
     * Create a new image
     * 배경을 위한 canvas를 제일 밑부분에 깔고 그위에 손글씨를 작성할 canvas를 생성한다.
     * 이렇게 해야 지우개를 사용하였을 때 배경이 지워지지 않는다.
     */
    public void newImage(int width, int height)
    {
        //비트맵이 존재하면 비트맵 이미지를 해제
        //(메모리 최적화를 위해 사용)
        if(mBitmap != null){
            mBitmap.recycle();
        }
        //배경 canvas를 생성한다.
        canvasBackground = new Canvas();
        //배경 canvas의 배경색을 설정한다.
        drawBackground(canvasBackground);

        //비트맵 생성(Config.ARGB_8888: 투명값 지정)

        // 2015-05-04 윤동수: 저장된 이미지 불러오기
        // 저장된 이미지가 없으면 새로 생성
        Bitmap img = null;
        if(undo.size()>0)
            img = undo.getLast().copy(Bitmap.Config.ARGB_8888, true);
        else
            img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        /*비트맵에 직접 그림을 그리거나 다른 이미지를 그릴려고 하면 새로운 canvas를 만들어야 canvas에
        그리는 모든 작업이 bitmap에 반영된다.*/
        //캔버스 생성
        Canvas canvas = new Canvas();
        //캔버스에 비트맵 이미지 적용
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
        canvas.drawColor(Color.WHITE);


        //지우개 모드가 아닐 경우 Path객체를 그린다.
        //지우개는 검은색 선이라서 지우개 모드일 경우 Path객체 그리면 안됨
        if(!mEraserMode)
            canvas.drawPath(mPath, mPaint);
        //비트맵을 화면에 그린다.
        canvas.drawBitmap(mBitmap, 0, 0, null);


    }

    /**
     * Handles touch event, UP, DOWN and MOVE(for drawing)
     */
    public boolean  onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            //손을 떼었을 때
            case MotionEvent.ACTION_UP:
//                Log.i("draw", "actionup called.");
                changed = true;

                this.getParent().requestDisallowInterceptTouchEvent(false);
                //touchUp 메소드 호출
                Rect rect = touchUp(event, false);
                //s = null;   // Stroke 인스턴스 삭제

                //화면을 갱신한다.
                if (rect != null) {
                invalidate(rect);
            }
               /* this.getParent().requestDisallowInterceptTouchEvent(true);
                touchUp(event,false);
                invalidate();*/

                //Path 객체 초기화
                mPath.rewind();

                // undo 목록에 넣기
                Bitmap img = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas();
                canvas.setBitmap(img);
                canvas.drawBitmap(mBitmap, 0, 0, null);
                undo.addList(img);

                return true;
            //화면에 손을 댔을 때
            case MotionEvent.ACTION_DOWN:
//                Log.i("draw", "actiondown called.");

                if (mBitmap == null){
                    ;
                }
                //scrollview에 영향을 안받고 draw 기능 적용
                this.getParent().requestDisallowInterceptTouchEvent(true);
                //touchDown()메소드 호출
                rect = touchDown(event);


                //화면을 갱신한다.
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
                //좌표값 저장
                x = event.getRawX();
                y = event.getRawY();
                //터치 상태
                istouched = true;
                touchx = x; touchy =y;
                String msg = "터치를 입력받음 : " + x + " / " + y;
                Log.d(TAG,msg);
                return true;
            //움직일 때
            case MotionEvent.ACTION_MOVE:
//                Log.i("draw", "actionmove called.");
                //scrollview에 영향을 안받고 draw 기능 적용
                this.getParent().requestDisallowInterceptTouchEvent(true);
                //touchMove() 메소드 호출
                rect = touchMove(event);

                //화면을 갱신한다.
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

        //s = new Stroke();

//        temp_x = x;
//        temp_y = y;


       // s.listPoint.add(new PointData(x, y));


        lastX = x;
        lastY = y;

        //Path 정보를 초기화
        mPath.reset();

        Rect mInvalidRect = new Rect();
        //Path객체에 현재 좌표값 추가
        mPath.moveTo(x, y);
        /**********************/

        final int border = mInvalidateExtraBorder;
        //다시 그려질 영역으로 현재 이동한 좌표 추가
        mInvalidRect.set((int) x - border, (int) y - border, (int) x + border, (int) y + border);

        mCurveEndX = x;
        mCurveEndY = y;

        //Path객체를 그린다.
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
        return rect;
//        int i,j;
//        int size;
//
//        stroke.add(new Stroke(temp_color,temp_thickness,s.listPoint));
//
//            for(i=0; i<stroke.size(); i++) {
////            Log.i("i는","? " + i);
//
//                size = stroke.get(i).listPoint.size();
//                Log.i("color", ", size" + stroke.get(i).color + ", " + stroke.get(i).thickness);
//
////            Log.i("size는","? " + size);
//                for (j = 0; j < size; j++) {
////                Log.i("터치업","" + stroke.get(i).listPoint.get(j).x + ", " + stroke.get(i).listPoint.get(j).y);
//
//                }
//            }
//
//
////        s.listPoint.clear();
////        stroke.clear();
    }

    /**
     * Process Move Coordinates
     * x,y값을 mPath에 넣어서 라인을 quadTo를 사용해서 그린다
     * lastX,lastY값을 사용한다
     * @param event
     * @return
     */
    private Rect processMove(MotionEvent event) {            /******************************/

//        final float x = event.getX();
//        final float y = event.getY();
//        PointData p = new PointData(x, y);
//        s.listPoint.add(p);
//        Rect mInvalidRect = drawPointData(p, mPath, mPaint);
//
        final float x = event.getX();
        final float y = event.getY();

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

            //Path객체를 그린다.
            canvasWrite.drawPath(mPath, mPaint);
        }

        return mInvalidRect;
    }

//    /**
//     * x,y값을 mPath에 넣어서 라인을 quadTo를 사용해서 그린다
//     * lastX,lastY값을 사용한다
//     *
//     */
//    private Rect drawPointData(PointData p, Path path, Paint paint) {
//
//        float x = p.x;
//        float y = p.y;
//
//        final float dx = Math.abs(x - lastX);
//        final float dy = Math.abs(y - lastY);
//
//        Rect mInvalidRect = new Rect();
//        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
//            final int border = mInvalidateExtraBorder;
//            mInvalidRect.set((int) mCurveEndX - border, (int) mCurveEndY - border,      //이동좌표 추가
//                    (int) mCurveEndX + border, (int) mCurveEndY + border);
//
//            float cX = mCurveEndX = (x + lastX) / 2;
//            float cY = mCurveEndY = (y + lastY) / 2;
//
//
//            path.quadTo(lastX, lastY, cX, cY);     //패스 객체에 현재 좌표값을 곡선으로 추가
//
//            // union with the control point of the new curve
//            mInvalidRect.union((int) lastX - border, (int) lastY - border,
//                    (int) lastX + border, (int) lastY + border);
//
//            // union with the end point of the new curve
//            mInvalidRect.union((int) cX - border, (int) cY - border,
//                    (int) cX + border, (int) cY + border);
//
//            lastX = x;
//            lastY = y;
//
//            canvasWrite.drawPath(path, paint);
//
//        }
//        return mInvalidRect;
//    }

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
