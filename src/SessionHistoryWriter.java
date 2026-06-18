import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Appends completed dance commands to a local text file.
 */
public class SessionHistoryWriter {

    private static final String HISTORY_FILE_NAME = "Done.txt";

    public String appendCompleted(List<String> completedDances)
        throws IOException {

        List<String> sortedDances = new ArrayList<>(completedDances);
        Collections.sort(sortedDances);

        Path historyFile = Paths.get(HISTORY_FILE_NAME).toAbsolutePath();

        Files.write(
            historyFile,
            sortedDances,
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
        );

        return "Completed dance sequences were saved to: "
            + historyFile;
    }
}
