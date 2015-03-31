package yu.kyp.image;

import java.util.ArrayList;

/**
 * Created by DONGSOO on 2015-03-31.
 * 획 클래슨
 */
public class Stroke {
    /**
     * 획 고유번호
     */
    public Integer STROKE_NO = null;
    /**
     * 노트 고유번호 (FK)
     */
    public Integer NOTE_NO = null;
    /**
     * 펜색상
     */
    public Integer COLOR = null;
    /**
     * 펜두께
     */
    public Integer THICKNESS = null;
    public ArrayList<PointData> listPointData = new ArrayList<PointData>();
}
