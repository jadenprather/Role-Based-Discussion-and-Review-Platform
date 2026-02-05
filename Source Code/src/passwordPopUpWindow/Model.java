package passwordPopUpWindow;

import javafx.scene.paint.Color;

/*******
 * Title: Model Class - establishes the required GUI data and the computations.
 *
 * Description: This Model class is a major component of a Model View Controller (MVC)
 * application design that provides the user with a Graphical User Interface using JavaFX
 * widgets as opposed to a command line interface.
 *
 * In this case the Model deals with an input from the user and checks to see if it conforms to
 * the requirements specified by a graphical representation of a finite state machine.
 *
 * This is a purely static component of the MVC implementation.  There is no need to instantiate
 * the class.
 *
 * Copyright: Lynn Robert Carter ©️ 2025
 *
 * @author Lynn Robert Carter
 *
 * @version 2.00  2025-07-30 Rewrite of this application for the Fall 2025 offering of CSE 360
 * and other ASU courses.
 */
public class Model {

	/*-----------------------------------------------------------------------------*
	 * Attributes used by the FSM to inform the GUI/user what passed/failed.
	 *-----------------------------------------------------------------------------*/
	public static String passwordErrorMessage = "";  // The error message text
	public static String passwordInput = "";         // The input being processed
	public static int passwordIndexofError = -1;     // The index where the error was located
	public static boolean foundUpperCase = false;
	public static boolean foundLowerCase = false;
	public static boolean foundNumericDigit = false;
	public static boolean foundSpecialChar = false;
	public static boolean foundLongEnough = false;
	public static boolean foundShortEnough = true;

	private static String inputLine = "";            // The input line
	private static char currentChar;                 // The current character in the line
	private static int currentCharNdx;               // The index of the current character
	private static boolean running;                  // The FSM running flag

	private static void displayInputState() {
		// Console diagnostics (optional)
		System.out.println(inputLine);
		System.out.println(inputLine.substring(0,currentCharNdx) + "?");
		System.out.println("The password size: " + inputLine.length() + "  |  The currentCharNdx: " +
				currentCharNdx + "  |  The currentChar: \"" + currentChar + "\"");
	}

	/**
	 * Mechanical transformation of a directed graph into code.
	 * @param input password candidate
	 * @return empty string if valid; otherwise aggregated error message
	 */
	public static String evaluatePassword(String input) {
		passwordErrorMessage = "";
		passwordIndexofError = 0;
		inputLine = input;
		currentCharNdx = 0;

		if (input.length() <= 0) {
			return "*** Error *** The password is empty!";
		}

		if (input.length() > 32) {
			foundShortEnough = false;
			return "*** Error *** Password is too long!";
		} else {
			foundShortEnough = true;
		}

		currentChar = input.charAt(0);
		passwordInput = input;

		foundUpperCase = false;
		foundLowerCase = false;
		foundNumericDigit = false;
		foundSpecialChar = false;
		foundNumericDigit = false;
		foundLongEnough = false;
		foundShortEnough = true;

		running = true;

		while (running) {
			displayInputState();

			if (currentChar >= 'A' && currentChar <= 'Z') {
				System.out.println("Upper case letter found");
				foundUpperCase = true;
			} else if (currentChar >= 'a' && currentChar <= 'z') {
				System.out.println("Lower case letter found");
				foundLowerCase = true;
			} else if (currentChar >= '0' && currentChar <= '9') {
				System.out.println("Digit found");
				foundNumericDigit = true;
			} else if ("~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/".indexOf(currentChar) >= 0) {
				System.out.println("Special character found");
				foundSpecialChar = true;
			} else {
				passwordIndexofError = currentCharNdx;
				return "*** Error *** An invalid character has been found!";
			}

			if (currentCharNdx >= 7) {
				System.out.println("At least 8 characters found");
				foundLongEnough = true;
			}

			if (inputLine.length() <= 32) {
				foundShortEnough = true;
			} else {
				foundShortEnough = false;
			}

			// Advance
			currentCharNdx++;
			if (currentCharNdx >= inputLine.length())
				running = false;
			else
				currentChar = input.charAt(currentCharNdx);

			System.out.println();
		}

		// Build an error string of missing conditions
		String errMessage = "";
		if (!foundUpperCase)    errMessage += "Upper case; ";
		if (!foundLowerCase)    errMessage += "Lower case; ";
		if (!foundNumericDigit) errMessage += "Numeric digits; ";
		if (!foundSpecialChar)  errMessage += "Special character; ";
		if (!foundLongEnough)   errMessage += "Long Enough; ";

		if (errMessage.equals("")) return "";

		passwordIndexofError = currentCharNdx;
		return errMessage + "conditions were not satisfied";
	}
}
