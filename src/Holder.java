import com.nikondsl.cache.MayBeCompacted;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@MayBeCompacted
public class Holder {
    private int someNumber = 1;
    @MayBeCompacted(ifMoreThen = 120)
    private String veryBigText = "default value";
    private String usual = "default value";
    @MayBeCompacted
    private byte[] someBytes = "new byte[] values".getBytes(StandardCharsets.UTF_8);

    public int getSomeNumber() {
        return someNumber;
    }

    public void setSomeNumber(int someNumber) {
        this.someNumber = someNumber;
    }

    public String getVeryBigText() {
        return veryBigText;
    }

    public void setVeryBigText(String veryBigText) {
        this.veryBigText = veryBigText;
    }

    public String getUsual() {
        return usual;
    }

    public void setUsual(String usual) {
        this.usual = usual;
    }

    public byte[] getSomeBytes() {
        return someBytes;
    }

    public void setSomeBytes(byte[] someBytes) {
        this.someBytes = someBytes;
    }

    @Override
    public String toString() {
        return "Holder{" +
                "someNumber=" + someNumber +
                ", veryBigText='" + veryBigText + '\'' +
                ", usual='" + usual + '\'' +
                ", someBytes=" + Arrays.toString(someBytes) +
                '}';
    }
}
