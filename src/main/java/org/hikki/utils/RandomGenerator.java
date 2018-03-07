package org.hikki.utils;

import java.util.Random;

/**
 * Created by HIKKIさまon 2017/11/25 19:35
 * Description:.
 */
public class RandomGenerator {
    public static int randInt(int min, int max) {
        Random random = new Random();
        int randomNum = random.nextInt((max - min) + 1) + min;
        return randomNum;
    }
}
