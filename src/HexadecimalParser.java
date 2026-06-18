import java.util.ArrayList;
import java.util.List;

/**
 * Validates and splits colon-separated hexadecimal values read from a QR code.
 */
public class HexadecimalParser {

    private static final int MAXIMUM_VALUES = 5;

    public QrParseResult parse(String qrData) {
        List<String> validValues = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        if (qrData == null || qrData.trim().isEmpty()) {
            warnings.add("No QR-code data was received.");
            return new QrParseResult(validValues, warnings);
        }

        String[] values = qrData.split(":", -1);

        for (String rawValue : values) {
            String hexadecimal = normalise(rawValue);

            if (validValues.size() >= MAXIMUM_VALUES) {
                warnings.add(
                    "Maximum of 5 values can be accepted. "
                        + "Remaining values were omitted."
                );
                break;
            }

            if (hexadecimal.isEmpty()) {
                warnings.add(
                    "Invalid input was omitted (" + rawValue + ")."
                );
                continue;
            }

            if (hexadecimal.length() > 2) {
                warnings.add(
                    "Values must be one or two hexadecimal digits. "
                        + "Omitted (" + rawValue + ")."
                );
                continue;
            }

            if (!isValidHexadecimal(hexadecimal)) {
                warnings.add(
                    "Invalid input was omitted (" + rawValue + ")."
                );
                continue;
            }

            validValues.add(hexadecimal.toUpperCase());
        }

        return new QrParseResult(validValues, warnings);
    }

    private String normalise(String value) {
        return value.replaceAll("\\s+", "").trim();
    }

    private boolean isValidHexadecimal(String value) {
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);

            boolean isDigit = Character.isDigit(character);
            boolean isLowerCaseHex =
                character >= 'a' && character <= 'f';
            boolean isUpperCaseHex =
                character >= 'A' && character <= 'F';

            if (!isDigit && !isLowerCaseHex && !isUpperCaseHex) {
                return false;
            }
        }

        return true;
    }
}
