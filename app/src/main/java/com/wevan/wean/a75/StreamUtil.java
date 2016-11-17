package com.wevan.wean.a75;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * Created by Wean on 2016/11/16.
 */

public class StreamUtil {
    //将流信息转化成字符串
    public static String parserStreamUtil(InputStream in)throws IOException{
        //字符流，读取流,将字节流转化成字符流
        BufferedReader br=new BufferedReader(new InputStreamReader(in));
        //写入流
        StringWriter sw=new StringWriter();
        //读写操作,数据缓冲区
        String str=null;
        while ((str=br.readLine())!=null){
            sw.write(str);
        }
        sw.close();
        br.close();
        return sw.toString();
    }
}
