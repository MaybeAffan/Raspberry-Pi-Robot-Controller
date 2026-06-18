import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores the accepted hexadecimal values and any validation messages.
 */
public class QrParseResult {

    private final List<String> validValues;
    private final List<String> warnings;

    public QrParseResult(List<String> validValues, List<String> warnings) {
        this.validValues = new ArrayList<>(validValues);
        this.warnings = new ArrayList<>(warnings);
    }

    public List<String> getValidValues() {
        return Collections.unmodifiableList(validValues);
    }

    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

    public boolean hasValidValues() {
        return !validValues.isEmpty();
    }
}
