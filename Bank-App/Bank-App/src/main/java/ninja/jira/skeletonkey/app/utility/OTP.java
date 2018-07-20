package ninja.jira.skeletonkey.app.utility;

/**
 * Represents the OTP function. This class authenticates all users by the same non-random passcode. It is reset upon starting the application.
 */
public class OTP {
    //Default passcode for all users.
    public static Integer passCode = 123456;

    /**
     * This method increments the passcode by 1. For example, 123456 will increment to 123457.
     */
    public static void increment() {
        passCode++;
    }

    /**
     * This method authenticates the user. Upon authenticating, passcode will be incremented.
     *
     * @param otp entered by the user to authenticate themself
     * @return true if user entered the correct passcode, false otherwise
     */
    public static boolean authenticate (Integer otp) {
        if (otp.intValue() == passCode.intValue()) {
            increment();
            return true;
        }
        return false;
    }
}
