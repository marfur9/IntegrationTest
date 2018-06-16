package com.company;

import org.joda.time.DateTime;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.ConfigurationContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;

public class TwitterAdapter {
    Twitter unauthenticatedTwitter = new TwitterFactory().getInstance();
    long userIDTwitter = 1006463801045200896L; //twitter profile i want to read from
    Properties prop = new Properties();
    InputStream input = null;
    AccessToken accessToken;
    String[] consumerKeys;
    Twitter twitter;

    TwitterAdapter(){
        accessToken = setAccessToken();
        consumerKeys = setConsumerKeys();
        OAuthAuthorization authorization = new OAuthAuthorization(ConfigurationContext.getInstance());
        twitter = new TwitterFactory().getInstance(authorization);
    }

    public List<FacebookPage> recentTweetsToFacebookPages(int minutes) { //converts links from tweets from the last xx minutes to FacebookPage objects
        List<FacebookPage> FBPages = new ArrayList();
        List<Status> statuses = getRecentTimeline(userIDTwitter, minutes);
        Iterator<Status> statusIterator = statuses.iterator();

        while (statusIterator.hasNext()) {
            Status current = statusIterator.next();
            URLEntity[] urls = current.getURLEntities();
            if (urls.length != 0) { //does nothing if there is no links
                for(URLEntity currentURL : urls) {
                    String fullURL = currentURL.getExpandedURL();
                    System.out.println("Full URL found in tweet: " + fullURL);
                    if (isFacebookLink(fullURL)) {
                        String pageName = getPageNameFromLink(fullURL);
                        User requester = current.getUser();
                        FacebookPage currentPage = new FacebookPage(pageName, requester.getScreenName());
                        FBPages.add(currentPage);
                        System.out.println("Only the page name/ID from the tweet: " + pageName);
                        System.out.println("Requested by name: " + requester.getName() + " Screen name: " + requester.getScreenName());
                    } else { //if there is a link but it isn't a facebook link
                        System.out.println(fullURL + " is not a valid facebook link.");
                    }
                }
            }
        }

        if (FBPages.isEmpty()) {
            return null;
        } else {
            return FBPages;
        }
    }

    public void postRepliesToTwitter(List<FacebookPage> FBPages){
        for (FacebookPage current : FBPages){
            String newTweet = composeTweet(current);
            try{
                twitter.updateStatus(newTweet);
            }catch (TwitterException e){
                System.err.println("Error occurred while posting tweet!");
            }
        }
    }

    public String composeTweet(FacebookPage fbPage){
        String newTweet;
            if (fbPage.getLikeCount() == null) {
                    newTweet = "Could not find " + fbPage.getName() + "s like count";
            } else {
                    newTweet = "@" + fbPage.getRequester() + " Facebook page " + fbPage.getName() + " has " + NumberFormat.getNumberInstance(Locale.US).format(fbPage.getLikeCount()) + " likes.";
            }

        return newTweet;
    }

    private List<Status> getRecentTimeline(Long userID, int minutes) { //returns a list of tweets from the last xx minutes
        List<Status> statuses = new ArrayList();
        try {
            //Get last 30 tweets from timeline and mentions
            statuses = unauthenticatedTwitter.getUserTimeline(userID);
            statuses.addAll(twitter.getMentionsTimeline());
            //Find the time/date from xx minutes ago
            DateTime dateTime = new DateTime().minusMinutes(minutes); //finds the time xx minutes ago
            Date lastHour = dateTime.toDate();
            //remove tweets older than xx minutes from list
            Iterator<Status> statusIterator = statuses.iterator();
            while(statusIterator.hasNext()){
                Status current = statusIterator.next();
                if(current.getCreatedAt().before(lastHour)){
                    statusIterator.remove();
                }
            }

        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            System.exit(-1);
        }
        return statuses;
    }

    private boolean isFacebookLink (String url){
        try {
            URL urlObject = new URL(url);
            String[] splitHost = urlObject.getHost().split("\\.");
            for(String hostPart : splitHost){
                if(hostPart.equals("facebook")) {
                    return true;
                }
            }
                return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getPageNameFromLink (String url) {
        try {
            URL urlObject = new URL(url);
            System.out.println(urlObject.getPath());
            String[] path = urlObject.getPath().split("/");
            System.out.println(path[1]);
            return path[1];
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private AccessToken setAccessToken(){
        try {
            input = new FileInputStream("twitter4j.properties");
            prop.load(input);
            accessToken = new AccessToken(prop.getProperty("oauth.accessToken"), prop.getProperty("oauth.accessTokenSecret"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return accessToken;
    }

    private String[] setConsumerKeys(){
        String[] consumerKeys = new String[2];
        try {
            input = new FileInputStream("twitter4j.properties");
            prop.load(input);
            consumerKeys[0] = prop.getProperty("oauth.consumerKey");
            consumerKeys[1] = prop.getProperty("oauth.consumerSecret");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return consumerKeys;
    }


}
