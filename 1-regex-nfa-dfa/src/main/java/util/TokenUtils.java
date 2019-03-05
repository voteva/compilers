package util;

public class TokenUtils {

    public static final char EPSILON_TOKEN = 'ε';
    public static final char CONCAT_TOKEN = '.';

    public static boolean isLiteral(char token) {
        return token >= 'a' && token <= 'z';
    }
}
