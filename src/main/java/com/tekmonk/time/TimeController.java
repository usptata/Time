package com.tekmonk.time;

import com.tekmonk.time.url.URLReader;
import com.tekmonk.time.parser.*;
import java.net.*;

import org.springframework.web.bind.annotation.RestController;
import org.json.simple.JSONArray;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class TimeController {
    @GetMapping(value = "/getlateststories")
    @ResponseBody
    public String sayHello() {
        String strurl = "https://time.com";
        JSONArray json = null;
        try {
            URL url = new URL(strurl);
            URLReader urlreader = new URLReader(url);
            urlreader.read();
            LatestNewsParser sourcedata = new LatestNewsParser(urlreader);
            HTMLTag tag = sourcedata.parser("partial latest-stories");
            json = sourcedata.getLatestStories(tag, "latest-stories__item");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json == null ? "Not able to process." : json.toJSONString();
    }
}
