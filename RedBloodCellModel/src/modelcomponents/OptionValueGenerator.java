package modelcomponents;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OptionValueGenerator {
    private static final String regex = "random\\s?\\(\\s?(.*)?\\s?,\\s?(.*)\\s?\\)";
    public static Double getRandomValue(String request) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(request);
        matcher.find();
        Double minVal = Double.parseDouble(matcher.group(1));
        Double maxVal = Double.parseDouble(matcher.group(2));
        // Generate a time value
        Double randomVal = new Random().nextDouble(); // 0 to 1
        randomVal *= (maxVal - minVal);
        randomVal += minVal;
        System.out.println("Sampled value, (min = " + minVal + " max = " + maxVal + ") = " + randomVal);
        return randomVal;
    }
    public static Boolean checkString(String request) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(request);
        matcher.find();
        try {
            Double.parseDouble(matcher.group(1));
            Double.parseDouble(matcher.group(2));    
            return(true);
        } catch (Exception e) {
            return(false);
        }
    }
    public static Double processRequest(String request) {
        // Try and parse as a double
        // it not, work out if it is processable
        // as a random(a,b)
        // if it is, do it. If not, re-raise
        // the exception from the double
        try {
            return(Double.parseDouble(request));
        } catch(NumberFormatException e) {
            Boolean isRandom = checkString(request);
            if(isRandom) {
                return getRandomValue(request);
            } else {
                throw(e);
            }
        }
    }
    public static void main(String[] args) {
        System.out.println("hello");
    }
}
