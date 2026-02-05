   package guiTools;

public class EmailAddressRecognizer {
   public static String emailAddressErrorMessage = "";
   public static String emailAddressInput = "";
   public static int emailAddressIndexofError = -1;
   private static int state = 0;
   private static int nextState = 0;
   private static boolean finalState = false;
   private static String inputLine = "";
   private static char currentChar;
   private static int currentCharNdx;
   private static boolean running;
   private static int domainPartCounter = 0;

   private static String displayInput(String input, int currentCharNdx) {
      String var10000 = input.substring(0, currentCharNdx);
      String result = var10000 + "?\n";
      return result;
   }

   private static void displayDebuggingInfo() {
      String var10001;
      if (currentCharNdx >= inputLine.length()) {
         var10001 = state > 99 ? " " : (state > 9 ? "  " : "   ");
         System.out.println(var10001 + state + (finalState ? "       F   " : "           ") + "None");
      } else {
         var10001 = state > 99 ? " " : (state > 9 ? "  " : "   ");
         System.out.println(var10001 + state + (finalState ? "       F   " : "           ") + "  " + currentChar + " " + (nextState > 99 ? "" : (nextState <= 9 && nextState != -1 ? "    " : "   ")) + nextState + "     " + domainPartCounter);
      }

   }

   private static void moveToNextCharacter() {
      ++currentCharNdx;
      if (currentCharNdx < inputLine.length()) {
         currentChar = inputLine.charAt(currentCharNdx);
      } else {
         System.out.println("End of input was found!");
         currentChar = ' ';
         running = false;
      }

   }

   public static String checkEmailAddress(String input) {
      state = 0;
      inputLine = input;
      currentCharNdx = 0;
      emailAddressInput = input;
      String var10000;
      if (input.length() <= 0) {
         emailAddressErrorMessage = "There was no email address found.\n";
         var10000 = emailAddressErrorMessage;
         return var10000 + displayInput(input, 0);
      } else {
         currentChar = input.charAt(0);
         if (input.length() > 255) {
            emailAddressErrorMessage = "A valid email address must be no more than 255 characters.\n";
            var10000 = emailAddressErrorMessage;
            return var10000 + displayInput(input, 255);
         } else {
            running = true;
            System.out.println("\nCurrent Final Input  Next  DomainName\nState   State Char  State  Size");

            while(running) {
               nextState = -1;
               switch(state) {
               case 0:
                  if ((currentChar < 'A' || currentChar > 'Z') && (currentChar < 'a' || currentChar > 'z') && (currentChar < '0' || currentChar > '9')) {
                     running = false;
                  } else {
                     nextState = 1;
                  }
                  break;
               case 1:
                  if ((currentChar < 'A' || currentChar > 'Z') && (currentChar < 'a' || currentChar > 'z') && (currentChar < '0' || currentChar > '9')) {
                     if (currentChar == '.') {
                        nextState = 0;
                     } else if (currentChar == '@') {
                        nextState = 2;
                     } else {
                        running = false;
                     }
                     break;
                  }

                  nextState = 1;
                  break;
               case 2:
                  if ((currentChar < 'A' || currentChar > 'Z') && (currentChar < 'a' || currentChar > 'z') && (currentChar < '0' || currentChar > '9')) {
                     running = false;
                     break;
                  }

                  nextState = 3;
                  domainPartCounter = 1;
                  break;
               case 3:
                  if (currentChar >= 'A' && currentChar <= 'Z' || currentChar >= 'a' && currentChar <= 'z' || currentChar >= '0' && currentChar <= '9') {
                     ++domainPartCounter;
                     if (domainPartCounter <= 63) {
                        nextState = 3;
                     } else {
                        running = false;
                     }
                  } else if (currentChar == '.') {
                     nextState = 2;
                     domainPartCounter = 0;
                  } else {
                     running = false;
                  }
                  break;
               case 4:
                  if ((currentChar < 'A' || currentChar > 'Z') && (currentChar < 'a' || currentChar > 'z') && (currentChar < '0' || currentChar > '9')) {
                     running = false;
                  } else {
                     nextState = 3;
                     domainPartCounter = 1;
                  }
               }

               if (running) {
                  displayDebuggingInfo();
                  moveToNextCharacter();
                  state = nextState;
                  nextState = -1;
               }
            }

            displayDebuggingInfo();
            System.out.println("The loop has ended.");
            emailAddressIndexofError = currentCharNdx;
            switch(state) {
            case 0:
               emailAddressIndexofError = currentCharNdx;
               emailAddressErrorMessage = "May only be alphanumberic.\n";
               return emailAddressErrorMessage;
            case 1:
               emailAddressIndexofError = currentCharNdx;
               emailAddressErrorMessage = "Missing '@' symbol in email address.\n";
               var10000 = emailAddressErrorMessage;
               return var10000 + displayInput(input, currentCharNdx);
            case 2:
               emailAddressIndexofError = currentCharNdx;
               emailAddressErrorMessage = "Domain part must start with alphanumeric character.\n";
               var10000 = emailAddressErrorMessage;
               return var10000 + displayInput(input, currentCharNdx);
            case 3:
               if (currentCharNdx < input.length()) {
                  emailAddressIndexofError = currentCharNdx;
                  emailAddressErrorMessage = "This must be the end of the input.\n";
                  var10000 = emailAddressErrorMessage;
                  return var10000 + displayInput(input, currentCharNdx);
               }

               emailAddressIndexofError = -1;
               emailAddressErrorMessage = "";
               return emailAddressErrorMessage;
            case 4:
               emailAddressIndexofError = currentCharNdx;
               emailAddressErrorMessage = "Domain part after '.' must start with alphanumeric character.\n";
               var10000 = emailAddressErrorMessage;
               return var10000 + displayInput(input, currentCharNdx);
            default:
               return "";
            }
         }
      }
   }
}