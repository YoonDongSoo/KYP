package yu.kyp.common;

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

    public static int getTest()
    {
        return 0;
    }

    public static int getTest1()
    {
        return 0;
    }
}
