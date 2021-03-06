import com.nikondsl.cache.MayBeCompacted;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@MayBeCompacted
public class Holder implements Serializable {
    private int someNumber = 1;
    private AtomicInteger someAtomicNumber = new AtomicInteger();
    @MayBeCompacted(ifMoreThen = 120)
    private String veryBigText = "default value";
    private String usual = "default value";
    @MayBeCompacted(ifMoreThen = 200)
    private List<InnerHolder> inners = new ArrayList<>();
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

    public List<InnerHolder> getInners() {
        return inners;
    }

    public int getSomeAtomicNumber() {
        return someAtomicNumber.get();
    }

    public void setSomeAtomicNumber(int someAtomicNumber) {
        this.someAtomicNumber.set(someAtomicNumber);
    }

    @Override
    public String toString() {
        return "Holder{" +
                "someNumber=" + someNumber +
                ", someAtomicNumber=" + someAtomicNumber +
                ", veryBigText='" + veryBigText + '\'' +
                ", usual='" + usual + '\'' +
                ", inners=" + inners +
                ", someBytes=" + Arrays.toString(someBytes) +
                '}';
    }

    public void setInners(List<InnerHolder> inners) {
        this.inners = inners;
    }

    public static class InnerHolder implements Serializable {
        private Integer id;
        private String expression;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }

        @Override
        public String toString() {
            return "InnerHolder{" +
                    "id=" + id +
                    ", expression='" + expression + '\'' +
                    '}';
        }
    }
}
