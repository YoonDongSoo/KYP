package yu.kyp.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by DONGSOO on 2015-03-31.
 */
public class Utils {

    /**
     * 현재 일시를 구해서 yyyy-MM-dd HH:mm:ss으로 리턴한다.
     * @return
     * yyyy-MM-dd HH:mm:ss
     */
    public static String getYYYYMMDDHHMMSS()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String dt = dateFormat.format(cal.getTime());
        return dt;
    }

    /**
     * 현재 일시를 구해서 yyyy-MM-dd으로 리턴한다.
     * @return
     * yyyy-MM-dd
     */
    public static String getYYYYMMDD()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        String dt = dateFormat.format(cal.getTime());
        return dt;
    }

    /**
     * min~max까지 숫자를 리턴한다.
     * @param min
     * 최소값
     * @param max
     * 최대값
     * @return
     */
    public static int getRandomNumber(int min, int max)
    {
        Random rand = new Random();

        return rand.nextInt(max) + min;
    }

    /**
     * bitmap을 byte배열로 변환한다
     * @param bitmap
     * @return
     * bitmap이 null이면 null을 리턴한다.
     */
   public static byte[] getBytes(Bitmap bitmap)
   {
       if(bitmap==null)
           return null;
       ByteArrayOutputStream stream = new ByteArrayOutputStream();
       bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
       return stream.toByteArray();
   }


    /**
     * byte배열의 이미지를 bitmap으로 변환한다.
     * @param image
     * @return
     * image가 null이면 null을 리턴한다.
     */
    public static Bitmap getImage(byte[] image) {
        if(image==null)
            return null;
        else
            return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
