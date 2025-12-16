import org.junit.Test;
import static org.junit.Assert.*;

public class DummyTest {

    @Test
    public void testSum() {
        int a = 5;
        int b = 2;

        assertEquals(10, a + b);
    }

    @Test
    public void testLogic() {
        boolean isTrue = true;

        assertTrue(isTrue);

        if (isTrue) {
            assert isTrue: "Native error message";
        }
    }
}
