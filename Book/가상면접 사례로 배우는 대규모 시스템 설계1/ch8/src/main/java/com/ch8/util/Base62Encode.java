package com.ch8.util;

import org.springframework.stereotype.Component;

@Component
public class Base62Encode {
    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = BASE62.length();

    /**
     * ID를 Base62 String으로 인코딩
     *
     * @param id
     * @return
     */
    public String encode(long id) {
        if (id == 0) {
            return String.valueOf(BASE62.charAt(0));
        }

        StringBuilder encoded = new StringBuilder();
        while (id > 0) {
            encoded.append(BASE62.charAt((int) (id % BASE)));
            id /= BASE;
        }
        return encoded.reverse().toString();
    }

    /**
     * Base62 문자열 -> ID로 디코딩
     *
     * @param shortUrl
     * @return
     */
    public long decode(String shortUrl) {
        long id = 0;
        for (char c : shortUrl.toCharArray()) {
            id = id * BASE + BASE62.indexOf(c);
        }
        return id;
    }
}
