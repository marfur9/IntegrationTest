package com.company;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.Page;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FacebookAdapter {


    private final FacebookClient facebookClient;
    String accessToken;
    Properties prop = new Properties();
    InputStream input = null;


    FacebookAdapter() {
        accessToken = setAccessToken();
        facebookClient = new DefaultFacebookClient(accessToken, Version.VERSION_2_12);
    }

    public FacebookPage updatePage (FacebookPage FBPage){
        Long likeCount;
        String name;
        Page page = facebookClient.fetchObject(FBPage.getURL(), Page.class,
                Parameter.with("fields", "fan_count,name"));

        System.out.println("Page likes: " + page.getFanCount());

        likeCount = page.getFanCount();
        name = page.getName();
        FBPage.setLikeCount(likeCount);
        FBPage.setName(name);
        return FBPage;
    }

    private String setAccessToken(){
        try {
            input = new FileInputStream("config.properties");
            prop.load(input);
            accessToken = prop.getProperty("accessToken");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return accessToken;
    }
}
