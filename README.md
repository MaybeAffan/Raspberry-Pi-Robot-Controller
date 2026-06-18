# QR-Driven SwiftBot Dance Controller

This project was built for Brunel University's SwiftBot platform. The SwiftBot is a Raspberry Pi-based robot with a camera, motors, underlights, physical buttons and an ultrasonic sensor.

The program uses QR codes as the robot's input. The user presents a QR code containing hexadecimal values, such as `3F` or `1F:2D:C8`, and the SwiftBot performs a separate dance routine for every valid value it reads.

## From QR Code to Dance Routine

When a code is scanned, the program first checks the input rather than immediately moving the robot. It accepts one or two digit hexadecimal values and supports up to five values in one QR code.

Invalid values are skipped without ending the session. This means a code containing a mixture of valid and invalid values can still be used, with the program showing the user which entries were ignored.

Each accepted value is manually converted into decimal, octal and binary. Those conversions are then used to determine the speed, underlight colour and movement sequence for that routine.

Before movement starts, the robot checks the area in front of it using its ultrasonic sensor. After completing the routines from a QR code, the user can either scan another one or exit using the SwiftBot's physical X and Y buttons. Completed values are saved to `Done.txt` when the session ends.

## Refactoring the Original Submission

The original version of this project was submitted as one Java file as part of a first-year software implementation assignment.

For this repository, I refactored the code so that the QR validation, number conversion, dance data, SwiftBot interaction and file handling are separated from the main application flow. The behaviour stays the same, but the code is easier to follow and maintain.

## Working with the Project

The project uses Brunel's SwiftBot API, which is not included in this repository. To work with the code, place a compatible API JAR inside a local `lib` folder:

```text
lib/
└── SwiftBot-API-5.1.3.jar
```

The source can then be compiled with:

```cmd
javac -cp "lib\SwiftBot-API-5.1.3.jar" -d out src\*.java
```

A JAR can be created with:

```cmd
jar cfe target\qr-driven-swiftbot-dance-controller.jar SwiftbotDance -C out .
```

## Licence

MIT License
