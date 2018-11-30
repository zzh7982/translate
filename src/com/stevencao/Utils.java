package com.stevencao;

import com.alibaba.fastjson.JSONArray;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * description: Utils
 *
 * @author ralf
 * @version [1.0, 2018/11/28]
 */

class Utils {
    private static String token(String a) {
        Integer b = 406644;
        Long b1 = 3293161072L;
        char jd = '.';
        String sb = "+-a^+6";
        String zb = "+-3^+b+-f";
        Integer[] e = new Integer[a.length()];
        for (int f = 0, g = 0; g < a.length(); g++) {
            int m = a.charAt(g);
            if (128 > m) {
                e[f++] = m;
            } else {
                if (2048 > m) {
                    e[f++] = m >> 6 | 192;
                } else {
                    if (55296 == (m & 64512) && g + 1 < a.length() && 56320 == (a.charAt(g + 1) & 64512)) {
                        m = 65536 + ((m & 1023) << 10) + (a.charAt(++g) & 1023);
                        e[f++] = m >> 18 | 240;
                        e[f++] = m >> 12 & 63 | 128;
                    } else {
                        e[f++] = m >> 12 | 224;
                        e[f++] = m >> 6 & 63 | 128;
                    }
                    e[f++] = m & 63 | 128;
                }
            }
        }
        long al = b;
        for (Integer anE : e) {
            al += anE;
            al = getRl(al, sb);
        }
        al = getRl(al, zb);
        al ^= b1;
        if (0 > al) {
            al = (al & 2147483647) + 2147483648L;
        }
        al %= 1E6;
        return al + "" + jd + (al ^ b);
    }

    static String translateGoogleString(String text, String fromLanguage, String toLanguage) {
        StringBuilder url = new StringBuilder();
        try {
            url.append("https://translate.google.cn/translate_a/single?").append("client=t&sl=").append(fromLanguage)
                    .append("&tl=").append(toLanguage).append("&hl=zh-CN").append("&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw")
                    .append("&dt=rm&dt=ss&dt=t&ie=UTF-8&oe=UTF-8&otf=2&ssel=0&tsel=0&kc=1&tk=").append(token(text)).append("&q=")
                    .append(URLEncoder.encode(text, "utf-8"));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Connection con = Jsoup.connect(url.toString());
        //请求头设置，特别是cookie设置（这些参数在f12都可以kanda）
        con.header("Accept", "*/*");
        con.header("Content-Type", "application/json; charset=UTF-8");
        con.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36");
        con.header("Cookie", "");
        //解析请求结果
        Document doc;
        try {
            StringBuilder result = new StringBuilder();
            doc = con.ignoreContentType(true).get();
            String rsp = doc.body().text();

            JSONArray jsonArray = JSONArray.parseArray(rsp);
            if (jsonArray.size() == 14) {

                result.append(((JSONArray) ((JSONArray) jsonArray.get(0)).get(0)).get(1)).append(":");
                result.append(((JSONArray) ((JSONArray) jsonArray.get(0)).get(0)).get(0)).append("\n");
                result.append(((JSONArray) ((JSONArray) jsonArray.get(0)).get(1)).get(3)).append("\n");

            } else if (jsonArray.size() == 9) {
                JSONArray sentence = (JSONArray) jsonArray.get(0);
                for (int i = 0; i < sentence.size() - 1; i++) {
                    result.append(((JSONArray) sentence.get(i)).get(0)).append("\n");
                }
            }
            //返回内容
            return result.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static long getRl(long a, String b) {

        char t = 'a';
        char yb = '+';
        long s = 4294967295L;
        for (Integer c = 0; c < b.length() - 2; c += 3) {
            char d = b.charAt(c + 2);
            int f = d >= t ? d - 87 : Integer.valueOf(d + "");
            long g = b.charAt(c + 1) == yb ? a >>> f : a << f;
            a = b.charAt(c) == yb ? a + g & s : a ^ g;
        }
        return a;

    }

}
