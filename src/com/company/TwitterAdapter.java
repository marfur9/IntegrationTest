package com.company;

import org.joda.time.DateTime;
import twitter4j.*;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.ConfigurationContext;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;

class TwitterAdapter {
    private Twitter twitter;

    TwitterAdapter(){
        OAuthAuthorization authorization = new OAuthAuthorization(ConfigurationContext.getInstance());
        twitter = new TwitterFactory().getInstance(authorization);
    }

    List<FacebookPage> recentTweetsToFacebookPages(int minutes) { //converts links from tweets from the last xx minutes to FacebookPage objects
        List<FacebookPage> facebookPages = new ArrayList<>();
        List<Status> statuses = getRecentTimeline(minutes);

        for (Status current : statuses) {
            URLEntity[] urls = current.getURLEntities();
            if (urls.length != 0) { //does nothing if there is no links in current status
                for(URLEntity currentURL : urls) {
                    String fullURL = currentURL.getExpandedURL();
                    System.out.println("Full URL found in tweet: " + fullURL);
                    if (isFacebookLink(fullURL)) {
                        String pageName = getPageNameFromLink(fullURL);
                        User requester = current.getUser();
                        FacebookPage currentPage = new FacebookPage(pageName, requester.getScreenName());
                        facebookPages.add(currentPage);
                        System.out.println("Only the page name/ID from the tweet: " + pageName);
                        System.out.println("Requested by name: " + requester.getName() + " Screen name: " + requester.getScreenName());
                    } else { //if there is a link but it isn't a facebook link
                        System.out.println(fullURL + " is not a valid facebook link.");
                    }
                }
            }
        }

        if (facebookPages.isEmpty()) {
            return null;
        } else {
            return facebookPages;
        }
    }

    void postRepliesToTwitter(List<FacebookPage> facebookPages) throws TwitterException{
        for (FacebookPage current : facebookPages){
            String newTweet = composeTweet(current);
            if(newTweet != null) {
                    twitter.updateStatus(newTweet);
            } else{
                System.out.println("Unexpected error while composing tweet");
            }
        }
    }

    private String composeTweet(FacebookPage facebookPage) throws TwitterException{
        String newTweet;
        String replyMention = setReplyMention(facebookPage.getRequester());

            if (facebookPage.getLikeCount() == null && facebookPage.getName() != null) { //valid facebook page, but couldn't get like count
                newTweet = replyMention + " Could not find " + facebookPage.getName() + "'s like count";
            } else if (facebookPage.getLikeCount() == null) { //not valid page (maybe a link to a facebook person/app/game)
                newTweet = replyMention + " The requested url " + facebookPage.getURL() + " is not a valid/public facebook page or something went wrong.";
            } else {
                newTweet = replyMention + " Facebook page " + facebookPage.getName() + " has " + NumberFormat.getNumberInstance(Locale.US).format(facebookPage.getLikeCount()) + " likes.";
            }
        return newTweet;
    }

    private List<Status> getRecentTimeline(int minutes) { //returns a list of tweets from the last xx minutes
        List<Status> statuses = new ArrayList<>();
        try {
            //Get last 30 tweets from timeline and mentions
            statuses = twitter.getUserTimeline();
            statuses.addAll(twitter.getMentionsTimeline());
            //Find the time/date from xx minutes ago
            DateTime dateTime = new DateTime().minusMinutes(minutes); //finds the time xx minutes ago
            Date lastHour = dateTime.toDate();
            //remove tweets older than xx minutes from list
            statuses.removeIf(current -> current.getCreatedAt().before(lastHour));

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

    private String setReplyMention(String requester) throws  TwitterException{
        String replyMention;
        if(requester.equals(twitter.getScreenName())){
            replyMention = "";
        } else{
            replyMention = "@" + requester;
        }
        return replyMention;
    }
}
