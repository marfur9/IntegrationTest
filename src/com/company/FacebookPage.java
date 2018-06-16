package com.company;

public class FacebookPage {
    private Long likeCount;
    private String URL, name, requester;

    public FacebookPage(String URL, Long likeCount, String name, String requester){
        this.URL = URL;
        this.likeCount = likeCount;
        this.name = name;
        this.requester = requester;
    }
    public FacebookPage(String URL, String requester){
        this.URL = URL;
        this.likeCount = null;
        this.name = null;
        this.requester = requester;
    }



    // getters and setters
    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setLikeCount(Long likeCount) {

        this.likeCount = likeCount;
    }

    public Long getLikeCount() {

        return likeCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }
}
