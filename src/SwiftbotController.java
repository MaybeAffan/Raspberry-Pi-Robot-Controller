import swiftbot.Button;
import swiftbot.SwiftBotAPI;

import java.awt.image.BufferedImage;

/**
 * Contains all direct interaction with the SwiftBot API.
 */
public class SwiftbotController {

    private final SwiftBotAPI swiftBot;
    private volatile Button selectedButton;

    public SwiftbotController() {
        swiftBot = new SwiftBotAPI();
    }

    /**
     * Attempts to scan a QR code for the requested number of seconds.
     */
    public String scanQrCode(int timeoutSeconds) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (timeoutSeconds * 1000L);

        System.out.println(
            "Starting a " + timeoutSeconds + "-second QR-code scan."
        );

        while (System.currentTimeMillis() < endTime) {
            try {
                BufferedImage image = swiftBot.getQRImage();
                String decodedMessage = swiftBot.decodeQRImage(image);

                if (decodedMessage != null && !decodedMessage.trim().isEmpty()) {
                    System.out.println("QR code found.");
                    return decodedMessage;
                }

                long elapsedMillis =
                    System.currentTimeMillis() - startTime;

                System.out.println(
                    "No QR code found yet. Time elapsed: "
                        + (elapsedMillis / 1000.0) + " seconds"
                );
            } catch (Exception exception) {
                System.out.println(
                    "The QR code could not be read. "
                        + "Try adjusting the camera position."
                );
            }

            Thread.sleep(1000);
        }

        return "";
    }

    /**
     * Shows the values that will be used for the current dance sequence.
     */
    public void displayCommandDetails(DanceCommand command) {
        System.out.println();
        System.out.println("Hexadecimal = " + command.getHexadecimal());
        System.out.println("Decimal = " + command.getDecimal());
        System.out.println("Octal = " + command.getOctal());
        System.out.println("Binary = " + command.getBinary());
        System.out.println("Speed = " + command.getSpeed());
        System.out.println(
            "LED Colour (Red " + command.getRed()
                + ", Green " + command.getGreen()
                + ", Blue " + command.getBlue() + ")"
        );
        System.out.println();
    }

    /**
     * Runs the movement sequence for a single DanceCommand.
     */
    public void performDance(DanceCommand command) {
        avoidObstacleIfNecessary();

        swiftBot.fillUnderlights(command.getUnderlightColours());

        String reversedBinary =
            new StringBuilder(command.getBinary()).reverse().toString();

        boolean antiClockwise = true;

        for (char digit : reversedBinary.toCharArray()) {
            if (digit == '1') {
                swiftBot.move(
                    command.getSpeed(),
                    command.getSpeed(),
                    command.getMovementDurationMillis()
                );
            } else {
                if (antiClockwise) {
                    swiftBot.move(
                        0,
                        command.getSpeed(),
                        command.getMovementDurationMillis()
                    );
                } else {
                    swiftBot.move(
                        command.getSpeed(),
                        0,
                        command.getMovementDurationMillis()
                    );
                }

                antiClockwise = !antiClockwise;
            }
        }
    }

    /**
     * Checks the closest ultrasonic reading and reverses if an object is
     * detected within 30 cm.
     */
    public void avoidObstacleIfNecessary() {
        double closestDistance = swiftBot.useUltrasound();

        for (int reading = 0; reading < 3; reading++) {
            double currentDistance = swiftBot.useUltrasound();

            if (currentDistance < closestDistance) {
                closestDistance = currentDistance;
            }
        }

        if (closestDistance < 30) {
            int reverseDurationSeconds =
                (int) (1 + (30 - closestDistance) / 10);

            System.out.println("Obstacle detected. Moving back first.");

            swiftBot.move(
                -100,
                -100,
                reverseDurationSeconds * 1000
            );
        }
    }

    /**
     * Flashes the underlights white three times after a routine.
     */
    public void flashUnderlights() throws InterruptedException {
        int[] white = {255, 255, 255};

        swiftBot.disableUnderlights();

        for (int flash = 0; flash < 3; flash++) {
            swiftBot.fillUnderlights(white);
            Thread.sleep(500);

            swiftBot.disableUnderlights();
            Thread.sleep(500);
        }
    }

    /**
     * Uses the X and Y buttons as a repeat / exit choice.
     *
     * Y returns true to continue. X returns false to exit. The user is asked
     * again if no button is pressed within five seconds.
     */
    public boolean askToContinue() throws InterruptedException {
        while (true) {
            selectedButton = null;

            swiftBot.disableUnderlights();
            swiftBot.disableButtonLights();
            swiftBot.disableAllButtons();

            System.out.println("Press Y to continue or X to exit.");

            swiftBot.setButtonLight(Button.X, true);
            swiftBot.setButtonLight(Button.Y, true);

            swiftBot.enableButton(Button.X, () -> registerButton(Button.X));
            swiftBot.enableButton(Button.Y, () -> registerButton(Button.Y));

            long timeout = System.currentTimeMillis() + 5000;

            while (
                selectedButton == null
                    && System.currentTimeMillis() < timeout
            ) {
                Thread.sleep(100);
            }

            swiftBot.disableAllButtons();
            swiftBot.disableButtonLights();

            if (selectedButton == Button.Y) {
                return true;
            }

            if (selectedButton == Button.X) {
                return false;
            }

            System.out.println("No button was pressed. Please try again.");
        }
    }

    private void registerButton(Button button) {
        selectedButton = button;
        swiftBot.toggleButtonLight(button);
        swiftBot.disableAllButtons();
    }

    /**
     * Safely turns off active SwiftBot controls when the application ends.
     */
    public void shutdown() {
        try {
            swiftBot.disableAllButtons();
            swiftBot.disableButtonLights();
            swiftBot.disableUnderlights();
        } catch (Exception ignored) {
            // The robot may already have been disconnected.
        }
    }
}
