package com.company;

import java.util.List;


public class Main {

    public static void main(String[] args) {
        TwitterAdapter twitterAdapter = new TwitterAdapter();
        FacebookAdapter FBAdapter = new FacebookAdapter();
        int minutes = 10; //minute interval to check twitter
        boolean running = true;

        //while (running) {
            List<FacebookPage> FBPages = twitterAdapter.recentTweetsToFacebookPages(minutes);

            if (FBPages != null) {
                for (FacebookPage currentPage : FBPages) {
                    currentPage = FBAdapter.updatePage(currentPage);
                }

                //twitterAdapter.postRepliesToTwitter(FBPages);
            } else {
                System.out.println("no facebook links the last " + minutes + " minutes.");
            }

            try {
                Thread.sleep(1000 * 60 * minutes);
            } catch (InterruptedException e) {
                System.out.println("got interrupted!");
            }
        //}
    }
}
