/**
 * Provides manual hexadecimal, binary and octal conversion methods.
 *
 * The assignment required conversion logic rather than Java's built-in
 * base-conversion methods, so the methods below perform the calculations.
 */
public class NumberConverter {

    public int hexadecimalToDecimal(String hexadecimal) {
        int decimal = 0;
        int power = hexadecimal.length() - 1;

        for (int index = 0; index < hexadecimal.length(); index++) {
            int digitValue = hexadecimalDigitValue(hexadecimal.charAt(index));
            decimal += digitValue * powerOfSixteen(power);
            power--;
        }

        return decimal;
    }

    public String decimalToBinary(int decimal) {
        if (decimal == 0) {
            return "0000";
        }

        String binary = "";

        while (decimal > 0) {
            int remainder = decimal % 2;
            binary = remainder + binary;
            decimal = decimal / 2;
        }

        return binary;
    }

    public int decimalToOctal(int decimal) {
        if (decimal == 0) {
            return 0;
        }

        String octal = "";

        while (decimal > 0) {
            int remainder = decimal % 8;
            octal = remainder + octal;
            decimal = decimal / 8;
        }

        return Integer.parseInt(octal);
    }

    private int hexadecimalDigitValue(char character) {
        if (Character.isDigit(character)) {
            return Character.getNumericValue(character);
        }

        switch (Character.toUpperCase(character)) {
            case 'A':
                return 10;
            case 'B':
                return 11;
            case 'C':
                return 12;
            case 'D':
                return 13;
            case 'E':
                return 14;
            case 'F':
                return 15;
            default:
                throw new IllegalArgumentException(
                    "Invalid hexadecimal digit: " + character
                );
        }
    }

    private int powerOfSixteen(int power) {
        int result = 1;

        for (int index = 0; index < power; index++) {
            result *= 16;
        }

        return result;
    }
}
