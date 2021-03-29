package learning;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jeongjin Kim
 * @since 2021-02-22
 */
class MapTest {
    @Test
    void map() {
        Map<String, Integer> data = new HashMap<>();
        data.put("1", null);

        Integer integer = data.get("1");
        Integer integer1 = data.get("2");

        System.out.println(integer);
        System.out.println(integer1);

        boolean b = data.containsKey("1");
        boolean b1 = data.containsKey("2");

        System.out.println(b);
        System.out.println(b1);
    }
}
