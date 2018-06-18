package com.company;

import twitter4j.TwitterException;

import java.util.List;


public class Main {

    public static void main(String[] args) throws TwitterException{
        TwitterAdapter twitterAdapter = new TwitterAdapter();
        FacebookAdapter facebookAdapter = new FacebookAdapter();
        int minutes = 10; //minute interval to check twitter

        //while (true) {
            List<FacebookPage> facebookPages = twitterAdapter.recentTweetsToFacebookPages(minutes);

            if (facebookPages != null) {
                    facebookPages = facebookAdapter.updatePages(facebookPages);
                    twitterAdapter.postRepliesToTwitter(facebookPages);
            } else {
                System.out.println("no facebook links the last " + minutes + " minutes.");
            }

            /*try {
                Thread.sleep(1000 * 60 * minutes);
            } catch (InterruptedException e) {
                System.out.println("got interrupted!");
            }*/
        //}
    }
}
