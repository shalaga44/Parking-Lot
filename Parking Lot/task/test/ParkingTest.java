import org.hyperskill.hstest.stage.StageTest;
import parking.MainKt;

public abstract class ParkingTest<T> extends StageTest<T> {
    public ParkingTest() {
        super(MainKt.class);
    }
}
