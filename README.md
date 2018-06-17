# Project Title

Integration Test

## Getting Started

A project made to test integrating different systems (facebook and twitter using restfb and twitter4j in java)

### Prerequisites

The project will need 4 different keys/tokens in a root file called "twitter4j.properties" to work.

[How to make key/tokens](https://developer.twitter.com/en/docs/basics/authentication/guides/access-tokens.html)

twitter4j.properties file (in project root)
```
debug=true
oauth.consumerKey=xxxxxxxxxxxxxxxxxxxx
oauth.consumerSecret=xxxxxxxxxxxxxxxxx
oauth.accessToken=xxxxxxxxxxxxxxxxx
oauth.accessTokenSecret=xxxxxxxxxxxxxxxxx
```
You also need to get a access token from https://developers.facebook.com/tools/explorer/ and put it in config.properties

## Built With

* [Twitter4j](http://twitter4j.org/en/) - Unofficial Java library for the Twitter API.
* [restfb](https://restfb.com/) - A simple and flexible Facebook Graph API client written in Java. Open source software.
* [Joda-Time](http://www.joda.org/joda-time/) - Joda-Time provides a quality replacement for the Java date and time classes.

