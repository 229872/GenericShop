package pl.lodz.p.edu.shop.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextUtil {

    public static String toCamelCase(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) {
            return snakeCase;
        }

        StringBuilder camelCase = new StringBuilder();
        boolean nextUpperCase = false;

        for (int i = 0; i < snakeCase.length(); i++) {
            char currentChar = snakeCase.charAt(i);
            if (currentChar == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    camelCase.append(Character.toUpperCase(currentChar));
                    nextUpperCase = false;
                } else {
                    camelCase.append(currentChar);
                }
            }
        }

        return camelCase.toString();
    }

    public static String toSnakeCase(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }

        StringBuilder snakeCase = new StringBuilder();

        for (int i = 0; i < camelCase.length(); i++) {
            char currentChar = camelCase.charAt(i);
            if (Character.isUpperCase(currentChar)) {
                snakeCase.append('_');
                snakeCase.append(Character.toLowerCase(currentChar));
            } else {
                snakeCase.append(currentChar);
            }
        }

        return snakeCase.toString();
    }
}
