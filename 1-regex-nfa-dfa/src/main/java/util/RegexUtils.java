package util;

public class RegexUtils {

    public static String addConcat(String regex) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < regex.length() - 1; i++) {
            sb.append(regex.charAt(i));

            if ((TokenUtils.isLiteral(regex.charAt(i)) && TokenUtils.isLiteral(regex.charAt(i + 1)))
                    || (TokenUtils.isLiteral(regex.charAt(i)) && regex.charAt(i + 1) == '(')
                    || (regex.charAt(i) == ')' && TokenUtils.isLiteral(regex.charAt(i + 1)))
                    || (regex.charAt(i) == '*' && TokenUtils.isLiteral(regex.charAt(i + 1)))
                    || (regex.charAt(i) == '*' && regex.charAt(i + 1) == '(')
                    || (regex.charAt(i) == '+' && TokenUtils.isLiteral(regex.charAt(i + 1)))
                    || (regex.charAt(i) == '+' && regex.charAt(i + 1) == '(')
                    || (regex.charAt(i) == ')' && regex.charAt(i + 1) == '(')) {

                sb.append(TokenUtils.CONCAT_TOKEN);
            }
        }

        sb.append(regex.charAt(regex.length() - 1));
        return sb.toString();
    }
}
