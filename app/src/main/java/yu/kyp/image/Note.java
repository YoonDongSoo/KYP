package yu.kyp.image;

import java.util.ArrayList;

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
     * 내용
     */
    public String CONTENT;
    /**
     * 최종수정일시 yyyy-MM-dd HH:mm:ss
     */
    public String LAST_MOD_DT;
    /**
     * 획 목록
     */
    public ArrayList<Stroke> listStroke = new ArrayList<Stroke>();

}
