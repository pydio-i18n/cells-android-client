package com.pydio.android.cells.utils;

import android.os.Build;
import android.util.Base64;

import com.pydio.cells.api.CustomEncoder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AndroidCustomEncoder implements CustomEncoder {

    @Override
    public byte[] base64Decode(byte[] data) {
        return Base64.decode(data, Base64.DEFAULT | Base64.NO_WRAP | Base64.NO_PADDING | Base64.NO_CLOSE);
    }

    @Override
    public String base64Decode(String s) {
        byte[] data = s.getBytes(StandardCharsets.UTF_8);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return new String(base64Decode(data), StandardCharsets.UTF_8);
        } else {
            return new String(base64Decode(data));
        }
    }

    @Override
    public String utf8Encode(String value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return URLEncoder.encode(value, StandardCharsets.UTF_8);
        } else {
            try {
                return URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return value;
            }

        }
    }

    public String utf8Decode(String value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } else {
            try {
                return URLDecoder.decode(value, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return value;
            }
        }
    }

    @Override
    public byte[] getUTF8Bytes(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }
}
