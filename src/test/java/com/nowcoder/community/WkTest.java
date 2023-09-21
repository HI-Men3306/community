package com.nowcoder.community;

import java.io.IOException;

public class WkTest {
    public static void main(String[] args) throws IOException {
        String cmd = "d:/work/wkhtmltox/bin/wkhtmltoimage --quality 75 https://www.nowcoder.com d:/work/data/wk-images/1.png";
        Process exec = Runtime.getRuntime().exec(cmd);
        System.out.println(exec);
        System.out.println("ok");
    }
}
