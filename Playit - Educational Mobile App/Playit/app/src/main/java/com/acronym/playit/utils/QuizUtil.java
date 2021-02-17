package com.acronym.playit.utils;

import com.acronym.playit.helpers.QuizOpenHelper;

import java.util.HashMap;
import java.util.Map;

public class QuizUtil {

    private static Map<String, String> math = new HashMap<>();

    private static void addMathQuizzes() {
        math.put(Constants.MATH_0, "40");
        math.put(Constants.MATH_1, "*/+");
        math.put(Constants.MATH_2, "123");
        math.put(Constants.MATH_3, "7.03");
        math.put(Constants.MATH_4, "180");
        math.put(Constants.MATH_5, "9");
        math.put(Constants.MATH_6, "88");
        math.put(Constants.MATH_7, "30");
        math.put(Constants.MATH_8, "3.14");
        math.put(Constants.MATH_9, "3/4");
    }

    public static String mathMap(String key) {
        addMathQuizzes();
        return math.get(key);
    }


}
