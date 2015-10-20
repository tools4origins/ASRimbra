package edu.tsp.asr;

import de.rtner.security.auth.spi.SimplePBKDF2;

/*
 *
 * This class is used to generate and check token used for authentication
 * Token structure is username/expirationTimeInMilliseconds/signature
 * The signature is generated from username, expirationTimeInMilliseconds and a private element : key
 *
 * This means that no matters which server create the token, any server having the key can check
 * that the user is logged in.
 *
 * This also means that your key SHOULD NOT be shared to anyone.
 * If anyone has this key, he can generate any signature he wants and appears to your system as a logged user.
 *
 * As it is a scholar project, we use a simple key and do not wonders if it will be used by someone else.
 *
 */
public final class TokenManager {
    public static String key = "";

    public static String get(String username) {
        long expirationTime = System.currentTimeMillis() + 1000*60*30; // token valid for 30 minutes
        return formatToken(username, expirationTime);
    }

    public static String extractUserName(String token) {
        String[] parts = token.split("/");
        return parts[0];
    }

    public static String formatToken(String username, long expirationTime) {
        return username + "/" + expirationTime + "/" + sign(username, expirationTime);
    }

    public static String sign(String username, long expirationTime) {
//        return formatSign(username, expirationTime);
        return new SimplePBKDF2().deriveKeyFormatted(formatSign(username, expirationTime));
    }

    public static String formatSign(String username, long expirationTime) {
        return key + username + key + expirationTime + key;
    }

    public static boolean checkToken(String token) {
        if (token==null) {
            return false;
        }
        String[] parts = token.split("/");
        String username = parts[0];
        long expirationTime = Long.parseLong(parts[1]);
        String sign = parts[2];
        String signContent = formatSign(username, expirationTime);

//        return sign.equals(signContent);
        return new SimplePBKDF2().verifyKeyFormatted(sign, signContent);
    }
}
