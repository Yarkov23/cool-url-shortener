package com.task.shortener.util;

import java.util.regex.Pattern;

public final class UrlValidator {

    private static final Pattern URL_PATTERN = Pattern.compile(
            "^https?://(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)$"
    );

    public static boolean isValid(String url) {
        if (url == null || url.isEmpty())
            return false;

        return URL_PATTERN.matcher(url).matches();
    }
}


