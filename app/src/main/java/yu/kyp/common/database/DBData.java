package yu.kyp.common.database;

/**
 * Created by dong on 2015-05-02.
 */
public class DBData {
    private byte[] buffer;

    public DBData(String str)
    {
        if(str==null)
            buffer = null;
        else
            buffer = str.getBytes();
    }

    public DBData(byte[] data) {
        buffer = data;
    }

    public byte[] getBytes()
    {
        return buffer;
    }

    public String getString()
    {
        if(buffer==null)
            return null;

        return new String(buffer);
    }

    public void setData(byte[] buffer) {
        this.buffer = buffer;
    }
}
