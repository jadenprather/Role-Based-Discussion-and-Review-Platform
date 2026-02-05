   package guiTools;

public final class EmailValidator {
   private EmailValidator() {
   }

   public static boolean isValid(String email) {
      if (email == null) {
         return false;
      } else {
         String err = EmailAddressRecognizer.checkEmailAddress(email);
         return err == null || err.isEmpty();
      }
   }

   public static String error(String email) {
      String err = EmailAddressRecognizer.checkEmailAddress(email);
      return err == null ? "" : err;
   }
}