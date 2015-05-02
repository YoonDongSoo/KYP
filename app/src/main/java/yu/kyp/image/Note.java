package yu.kyp.image;

import android.graphics.Bitmap;


/**
 * Created by DONGSOO on 2015-03-30.
 * 노트 클래스
 */
public class Note {
    /**
     * 노트고유번호
     */
    public Integer NOTE_NO = null;
    /**
     * 제목
     */
    public String TITLE;
    /**
     * 내용(이미지데이터)
     */
    public Bitmap NOTE_DATA;
    /**
     * 최종수정일시 yyyy-MM-dd HH:mm:ss
     */
    public String LAST_MOD_DT;
    /**
     * 삭제여부. 삭제된 메모일 때 true이고 휴지통에서 검색할 수 있다.
     * 보통은 false이다.
     */
    public boolean IS_DEL;
    /**
     * 배경
     */
    public String BACKGROUND;

    /**
     * 썸네일 객체
     */
    public Thumbnail thumbnail;
    /**
     * 알람 객체
     */
    public Alarm alarm;

}
