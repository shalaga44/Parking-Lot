import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testcase.TestCase;
import parking.MainKt;

import java.util.Arrays;
import java.util.List;


public class ParkingTest extends StageTest {
    public ParkingTest() {
        super(MainKt.class);
    }

    public List<TestCase> generate() {
        return Arrays.asList(
            new TestCase()
        );
    }

    public CheckResult check(String reply, Object clue) {
        String text =
            "White car has parked.\n" +
            "Yellow car left the parking lot.\n" +
            "Green car just parked here.";

        if (!reply.trim().equals(text.trim())) {
            return CheckResult.wrong(
                "You printed the wrong text! " +
                    "See the example.");
        } else {
            return CheckResult.correct();
        }
    }
}
