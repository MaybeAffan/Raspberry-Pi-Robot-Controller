/**
 * Represents one validated hexadecimal value and the values derived from it
 * for a SwiftBot dance sequence.
 */
public class DanceCommand {

    private final String hexadecimal;
    private final int decimal;
    private final int octal;
    private final String binary;
    private final int speed;
    private final int red;
    private final int green;
    private final int blue;
    private final int movementDurationMillis;

    private DanceCommand(
        String hexadecimal,
        int decimal,
        int octal,
        String binary,
        int speed,
        int red,
        int green,
        int blue,
        int movementDurationMillis
    ) {
        this.hexadecimal = hexadecimal;
        this.decimal = decimal;
        this.octal = octal;
        this.binary = binary;
        this.speed = speed;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.movementDurationMillis = movementDurationMillis;
    }

    public static DanceCommand fromHexadecimal(
        String hexadecimal,
        NumberConverter converter
    ) {
        int decimal = converter.hexadecimalToDecimal(hexadecimal);
        int octal = converter.decimalToOctal(decimal);
        String binary = converter.decimalToBinary(decimal);

        int speed = octal < 50 ? octal + 50 : Math.min(octal, 100);

        int red = decimal;
        int green = 3 * (decimal % 80);
        int blue = Math.max(red, green);

        int movementDurationMillis =
            hexadecimal.length() == 1 ? 1000 : 500;

        return new DanceCommand(
            hexadecimal,
            decimal,
            octal,
            binary,
            speed,
            red,
            green,
            blue,
            movementDurationMillis
        );
    }

    public String getHexadecimal() {
        return hexadecimal;
    }

    public int getDecimal() {
        return decimal;
    }

    public int getOctal() {
        return octal;
    }

    public String getBinary() {
        return binary;
    }

    public int getSpeed() {
        return speed;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public int getMovementDurationMillis() {
        return movementDurationMillis;
    }

    public int[] getUnderlightColours() {
        return new int[] {red, green, blue};
    }
}
