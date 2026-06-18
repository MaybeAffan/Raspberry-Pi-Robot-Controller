import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Entry point for the QR-Driven SwiftBot Dance Controller.
 *
 * This class only coordinates the application flow. QR parsing, number
 * conversion, robot control and file writing are kept in separate classes.
 */
public class SwiftbotDance {

    private final SwiftbotController swiftbotController;
    private final HexadecimalParser hexadecimalParser;
    private final NumberConverter numberConverter;
    private final SessionHistoryWriter historyWriter;

    public SwiftbotDance() {
        swiftbotController = new SwiftbotController();
        hexadecimalParser = new HexadecimalParser();
        numberConverter = new NumberConverter();
        historyWriter = new SessionHistoryWriter();
    }

    public static void main(String[] args) {
        SwiftbotDance application = new SwiftbotDance();

        try {
            application.run();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            System.out.println("The program was interrupted.");
        } catch (IOException exception) {
            System.out.println("Could not save the completed dance history.");
            System.out.println(exception.getMessage());
        } finally {
            application.swiftbotController.shutdown();
        }
    }

    private void run() throws InterruptedException, IOException {
        List<String> completedDances = new ArrayList<>();
        boolean keepRunning = true;

        while (keepRunning) {
            System.out.println();
            System.out.println("Please scan a QR code now.");

            String qrData = swiftbotController.scanQrCode(10);

            if (qrData.isEmpty()) {
                System.out.println("No QR code could be detected.");

                if (swiftbotController.askToContinue()) {
                    continue;
                }

                break;
            }

            System.out.println("Decoded QR code: " + qrData);

            QrParseResult parseResult = hexadecimalParser.parse(qrData);

            for (String warning : parseResult.getWarnings()) {
                System.out.println(warning);
            }

            if (!parseResult.hasValidValues()) {
                System.out.println("No valid hexadecimal values were detected.");

                if (swiftbotController.askToContinue()) {
                    continue;
                }

                break;
            }

            System.out.println(
                "Valid hexadecimal values: " + parseResult.getValidValues()
            );

            for (String hexadecimal : parseResult.getValidValues()) {
                DanceCommand command = DanceCommand.fromHexadecimal(
                    hexadecimal,
                    numberConverter
                );

                swiftbotController.displayCommandDetails(command);

                System.out.println(
                    "The next dance sequence will start shortly."
                );
                Thread.sleep(5000);

                swiftbotController.performDance(command);

                System.out.println();
                System.out.println(
                    "Dance for " + command.getHexadecimal()
                        + " was successfully completed."
                );

                swiftbotController.flashUnderlights();
                completedDances.add(command.getHexadecimal());
            }

            System.out.println();
            System.out.println("All dance sequences were completed.");
            System.out.println("Do you want to scan another QR code?");

            keepRunning = swiftbotController.askToContinue();
        }

        if (completedDances.isEmpty()) {
            System.out.println("No completed dance sequences were saved.");
        } else {
            System.out.println(historyWriter.appendCompleted(completedDances));
        }

        System.out.println("Exiting program...");
    }
}
