package validation;
import java.util.ArrayList;
import java.util.List;

public class PasswordValidator {
	
	public static int passwordIndexofError = -1;	// The index where the error was located
	private static int Min_length = 8;				// Minimum Length of password
	private static int Max_length = 32;				// Maximum Length of Password
	
	public static String evaluate(String input)
	{
		if (input == null) 					// User gives no input
		{
			input = "";						// Set user input to empty string
		}
		
		passwordIndexofError = 0;
		
		if (input.isEmpty())				// if empty string give error message
		{
			return "*** Error *** The password is empty!";
		}
		
		// All the requirements in password
		boolean foundUpperCase = false;
		boolean foundLowerCase = false;
		boolean foundNumericDigit = false;
		boolean foundSpecialChar = false;
		boolean foundLongEnough = false;
		boolean foundTooLong =  false;	
		
		int charCounter = 0; 			// count current amount of characters
		
		for (int i = 0; i < input.length(); i++)	//loop through the input string one by one
		{
			char currentChar = input.charAt(i);				//currentChar
			
			if (currentChar >= 'A' && currentChar <= 'Z') {
				foundUpperCase = true;
				charCounter++;
				
			} else if (currentChar >= 'a' && currentChar <= 'z') {
				foundLowerCase = true;
				charCounter++;
				
			} else if (currentChar >= '0' && currentChar <= '9') {
				foundNumericDigit = true;
				charCounter++;
				
			} else if ("~`!@#$%^&*()_-+={}[]|\\:<>.,?/".indexOf(currentChar) >= 0) {
				foundSpecialChar = true;
				charCounter++;
				
			} else {
				passwordIndexofError = i;
				return "*** Error *** An invalid character has been found!";
			}
			
			if (charCounter >= Min_length) {
				foundLongEnough = true;
			}
			if (charCounter > Max_length) {
				passwordIndexofError = i;
				return "Password too long: maximum is " + Max_length + " characters.";
			}
			
		}
		
		// Construct a String with a list of the requirement elements that were found.
		List<String> missing = new ArrayList<>();
		
		if(!foundUpperCase)
		{
			missing.add("Upper case");
		}
		if(!foundLowerCase)
		{
			missing.add("Lower case");
		}
		
		if(!foundNumericDigit)
		{
			missing.add("Numeric digit");
		}
		
		if(!foundSpecialChar)
		{
			missing.add("Special Char");
		}
		
		if(!foundLongEnough)
		{
			missing.add("Length is Okay");
		}
		if(foundTooLong)
		{
			missing.add("32 Character Limit reached");
		}
		
		if (missing.isEmpty())
		{
			return "";
		}
		
		//putting together all the error strings
		passwordIndexofError = input.length();
		return String.join("; ", missing) + "; Conditions were not completed";
		
				
		
	}
}