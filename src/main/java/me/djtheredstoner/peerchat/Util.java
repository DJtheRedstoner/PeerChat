package me.djtheredstoner.peerchat;

import java.util.concurrent.ThreadLocalRandom;

public class Util {

    private static final String ID_ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int ID_LENGTH = 16;

    public static String generateId() {
        var random = ThreadLocalRandom.current();

        var builder = new StringBuilder();
        for (int i = 0; i < ID_LENGTH; i++) {
            builder.append(ID_ALPHABET.charAt(random.nextInt(ID_ALPHABET.length())));
        }

        return builder.toString();
    }

}
