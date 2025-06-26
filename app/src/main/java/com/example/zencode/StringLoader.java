package com.example.zencode;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import java.io.InputStreamReader;

public class StringLoader {

    public static String readRawTextFile(Context context, int resId) {
        InputStream inputStream = context.getResources().openRawResource(resId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
