package modelcomponents;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeGenerator {
    public static Double getTime(String request) {
        String regex = "random\\s?\\(\\s?(.*)?\\s?,\\s?(.*)\\s?\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(request);
        matcher.find();
        Double minVal = Double.parseDouble(matcher.group(1));
        Double maxVal = Double.parseDouble(matcher.group(2));
        System.out.println("Sampling time, min = " + minVal + " max = " + maxVal);
        // Generate a time value
        Double timeVal = new Random().nextDouble(); // 0 to 1
        timeVal *= (maxVal - minVal);
        timeVal += minVal;
        return timeVal;
    }
}
