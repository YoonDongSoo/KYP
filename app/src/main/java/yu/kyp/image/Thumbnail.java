package yu.kyp.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

/**
 * Created by dong on 2015-05-02.
 */
public class Thumbnail {
    private static final int THUMBSIZE = 128;
    /**
     * 썸네일 고유번호
     */
    public Integer THUM_NO;
    /**
     * 노트 고유번호
     */
    public int NOTE_NO;
    /**
     * 썸네일 이미지 데이터
     */
    public Bitmap THUM_DATA=null;

    public Thumbnail() {

    }

    public Thumbnail(Bitmap bitmap) {

        ThumbnailUtils.extractThumbnail(bitmap, THUMBSIZE, THUMBSIZE);
    }


}
