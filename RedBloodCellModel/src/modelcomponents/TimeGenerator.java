package modelcomponents;
import java.util.Random;

public class TimeGenerator {
    private final Double inVal;
    private final Double[] vals = {
        0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6,
        1.7, 1.8, 1.9, 2.0, 2.1, 2.2, 2.3, 2.4, 2.5};
    public TimeGenerator(Double inVal) {
        this.inVal = inVal;
    }
    public Double getTime() {
        // Generate a time value
        Double timeVal = 0.0;
        if (inVal > 0.0) {
            timeVal = this.inVal;
        }else if (inVal == -1.0) {
            int idx = new Random().nextInt(vals.length);
            return vals[idx];
        }
        return timeVal;
    }
}


