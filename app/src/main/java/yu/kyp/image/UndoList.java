package yu.kyp.image;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by DONGSOO on 2015-04-28.
 */
public class UndoList extends ArrayList<Bitmap> {
    private static final int MAX_UNDOS = 50;
    private static final String TAG = UndoList.class.getSimpleName();


    /**
     * 마지막 Bitmap을 리턴하고 삭제한다.
     * 마지막 bitmap은 삭제하지 않는다.
     * @return
     */
    public Bitmap pop()
    {
        Bitmap bmp = getLast();
        if(bmp!=null && size()>1) {
            remove(size()-1);
        }
        Log.d(TAG, "SIZE:" + size());
        return bmp;
    }

    /**
     * 마지막 Bitmap을 리턴한다.
     * @returns
     */
    public Bitmap getLast()
    {
        if(size()==0)
            return null;

        Bitmap bmp = get(size()-1);
        Log.d("!!!", "SIZE:" + size());
        Log.d("!!!", "bmpsize:" + bmp.getWidth());
        Log.d("!!!", "bmpSIZE:" + bmp.getHeight());
        return bmp;
    }

//    /**
//     * 비트맵을 JPEG로 변환한다.
//     * @param src
//     * @param format
//     * @param quality
//     * @return
//     */
//    public Bitmap codec(Bitmap src, Bitmap.CompressFormat format, int quality) {
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        src.compress(format, quality, os);
//
//        byte[] array = os.toByteArray();
//        return BitmapFactory.decodeByteArray(array, 0, array.length);
//    }


    /**
     * Bitmap을 undo 목록에 추가한다.
     * 최대 MAX_UNDOS 까지 저장된다.
     * @param bitmap
     */
    public void addList(Bitmap bitmap)
    {
        add(bitmap);
        int w = bitmap.getWidth();
        Log.d("!!!","에드리스트사이즈" +bitmap.getWidth());
        Log.d("!!!","에드리스트사이즈" +bitmap.getHeight());

        // 최대개수(MAX_UNDOS)보다 많으면 앞에서 부터 삭제한다.
//        while (size() >= MAX_UNDOS){
//            remove(0);
//        }
        int delCount = size()-MAX_UNDOS;
        if(delCount>0)
        {
            ArrayList<Bitmap> tempList = new ArrayList<>();
            for (int i=0; i<delCount; i++)
            {
                Bitmap b = get(i);
                tempList.add(b);
            }
            removeAll(tempList);
            for(Bitmap b: tempList)
                b.recycle();
        }
        Log.d(TAG, "SIZE:" + size());

    }

    /**
     * Bitmap을 recycle처리하고 리스트 항목을 제거한다.
     */
    public void clearList() {
        for(Bitmap b:this)
        {
            b.recycle();
        }
        clear();
    }
}
