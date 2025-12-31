/**
 * Class untuk mengelola session user yang sedang login
 * @author Lenovo
 */
public class UserSession {
    
    private static int currentUserId;
    private static String currentUserName;
    private static String currentUsername;
    private static boolean isLoggedIn = false;
    
    /**
     * Set data user yang sedang login
     * @param userId
     * @param userName
     * @param username
     */
    public static void setCurrentUser(int userId, String userName, String username) {
        currentUserId = userId;
        currentUserName = userName;
        currentUsername = username;
        isLoggedIn = true;
    }
    
    /**
     * Clear session data (logout)
     */
    public static void clearSession() {
        currentUserId = 0;
        currentUserName = null;
        currentUsername = null;
        isLoggedIn = false;
    }
    
    /**
     * Get current user ID
     * @return user ID
     */
    public static int getCurrentUserId() {
        return currentUserId;
    }
    
    /**
     * Get current user name
     * @return user name
     */
    public static String getCurrentUserName() {
        return currentUserName;
    }
    
    /**
     * Get current username
     * @return username
     */
    public static String getCurrentUsername() {
        return currentUsername;
    }
    
    /**
     * Check if user is logged in
     * @return true if logged in, false otherwise
     */
    public static boolean isLoggedIn() {
        return isLoggedIn;
    }
}