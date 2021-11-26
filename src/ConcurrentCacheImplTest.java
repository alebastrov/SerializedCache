import com.nikondsl.cache.CompactingException;
import com.nikondsl.cache.ConcurrentCacheImpl;

import java.nio.charset.StandardCharsets;

public class ConcurrentCacheImplTest {

    public static void main(String[] args) throws CompactingException {
        ConcurrentCacheImpl cache = new ConcurrentCacheImpl();
        Holder holder1 = new Holder();
        holder1.setVeryBigText("Nikon DSL jhzkjhdskfjhsdzkfjhSDfkjhSDkfjhSKDJfhkjdzxvhhdb=");
        holder1.setSomeNumber(11);
        holder1.setUsual("Igor1");
        holder1.setSomeBytes("new updated value for byte array".getBytes(StandardCharsets.UTF_8));

        Holder holder2 = new Holder();
        holder2.setVeryBigText("XPorter");
        holder2.setSomeNumber(22);
        holder2.setUsual("Igor2");
        holder2.setSomeBytes("byte array to check".getBytes(StandardCharsets.UTF_8));


        cache.put("a", holder1);
        cache.put("b", holder2);
        System.err.println(cache.get("a"));
        System.err.println(cache.get("b"));
    }


}