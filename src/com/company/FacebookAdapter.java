package com.company;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.Page;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

class FacebookAdapter {


    private final FacebookClient facebookClient;
    private String accessToken;
    private Properties prop = new Properties();
    private InputStream input = null;


    FacebookAdapter() {
        accessToken = setAccessToken();
        facebookClient = new DefaultFacebookClient(accessToken, Version.VERSION_2_12);
    }

    List<FacebookPage> updatePages (List<FacebookPage> facebookPages){ //finds fan count and name for all Facebook pages in a list and returns it
        for(FacebookPage current : facebookPages) {
            Long likeCount;
            String name;

            try {
                Page page = facebookClient.fetchObject(current.getURL(), Page.class,
                        Parameter.with("fields", "fan_count,name"));

                System.out.println("Page likes: " + page.getFanCount());
                likeCount = page.getFanCount();
                name = page.getName();
                current.setLikeCount(likeCount);
                current.setName(name);
            } catch (FacebookOAuthException e) { //not a valid facebook page
                e.printStackTrace();
            }
        }
        return facebookPages;
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
