package wpy.twitterbot.sentiment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import twitter4j.HashtagEntity;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UserMentionEntity;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Twitter sentiment analysis
 * 
 * @author wpengyu
 */
public class SearchEngine {

    private static final int SEARCH_LIMIT = 100;
    private static final String OAUTH_PROPERTIES_FILE = "access.properties";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sdtf = new SimpleDateFormat("yyyyMMddHHmmss");
    private Twitter twitter;
    private Query query;
    private ConfigurationBuilder conf;
    private SentimentAnalyzer analyzer;

    public SearchEngine(String text, String date) throws IOException, ParseException {

        Properties props = new Properties();
        props.load(new FileInputStream(OAUTH_PROPERTIES_FILE));
        conf = new ConfigurationBuilder();
        conf.setDebugEnabled(true);
        conf.setOAuthConsumerKey(props.getProperty("oauth.consumerKey"));
        conf.setOAuthConsumerSecret(props.getProperty("oauth.consumerSecrete"));
        conf.setOAuthAccessToken(props.getProperty("oauth.accessToken"));
        conf.setOAuthAccessTokenSecret(props.getProperty("oauth.accessTokenSecrete"));

        analyzer = new SentimentAnalyzer();

        twitter = new TwitterFactory(conf.build()).getInstance();
    }

    public void searchTweets(String text, String date, int limit, String outputFile)
            throws TwitterException, IOException, InterruptedException, ParseException {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sdf.parse(date));
        calendar.add(Calendar.DATE, 1);
        String nextDate = sdf.format(calendar.getTime());

        // query = new Query(text + " -rt -http -https -@" + text);

        query = new Query(text + " -rt -http -https");
        query.setLang("en");
        query.setUntil(nextDate);
        query.setSince(date);
        query.setCount(limit);

        if (limit > SEARCH_LIMIT) {
            searchTweetsByBatch(limit, outputFile);
        } else {
            OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(outputFile));
            handleRateStatus();
            QueryResult result = twitter.search(query);
            System.out.println("Returned tweets: " + result.getTweets().size());
            for (Status status : result.getTweets()) {
                processTweetOutput(status, fw);
            }
            fw.close();
        }
    }

    private void searchTweetsByBatch(int limit, String outputFile)
            throws TwitterException, IOException, InterruptedException {
        query.setCount(limit);
        long tweetId = Long.MAX_VALUE;
        long count = 0;
        OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(outputFile));
        while (count < limit) {

            handleRateStatus();
            QueryResult result = twitter.search(query);
            twitter.getUserTimeline("");

            // If no more tweets returned just quit
            System.out.println("Returned tweets: " + result.getTweets().size());
            if (result.getTweets().isEmpty()) {
                System.out.println("There are no more tweets for the current search criteria");
                break;
            }
            for (Status status : result.getTweets()) {
                tweetId = tweetId > status.getId() ? status.getId() : tweetId;
                count++;
                processTweetOutput(status, fw);
            }

            // Set the minimum tweet ID as the max for the next query
            query.setMaxId(tweetId - 1);
        }
        fw.close();
    }

    public void getUserTimeline(String username, String outputFile) throws TwitterException, IOException {

        OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(outputFile));

        for (int i = 1; i < 10; i++) {
            Paging paging = new Paging(i, 20);
            ResponseList<Status> statuses = twitter.getUserTimeline(username, paging);
            for (Status status : statuses) {
                processTweetOutput(status, fw);
            }
        }
        fw.close();
    }

    private void processTweetOutput(Status status, OutputStreamWriter writer) throws IOException {
        String text = status.getText();
        String dateTime = sdtf.format(status.getCreatedAt());
        String tweetId = Long.toString(status.getId());
        String screenName = status.getUser().getScreenName();
        HashtagEntity[] hashtags = status.getHashtagEntities();
        UserMentionEntity[] mentions = status.getUserMentionEntities();

        Sentiment sm = analyzer.getSentimentScore(text, dateTime, tweetId);
        if (sm != null) {
            String out = tweetId + "|" + dateTime + "|" + "@" + screenName + "|"
                    + text.replaceAll("\n", ". ").replaceAll("\\|", " ") + "|" + formatHashTags(hashtags) + "|"
                    + formatMentions(mentions) + "|" + sm.toString();
            writer.write(out + "\n");
        } else {
            String out = tweetId + "|" + dateTime + "|" + "@" + screenName + "|"
                    + text.replaceAll("\n", ". ").replaceAll("\\|", " ") + "|" + formatHashTags(hashtags) + "|"
                    + formatMentions(mentions) + "|" + "neutral";
            writer.write(out + "\n");
        }
    }

    private void handleRateStatus() throws InterruptedException, TwitterException {
        System.out.println("Rate status");
        RateLimitStatus rateLimitStatus = twitter.getRateLimitStatus().get("/search/tweets");
        System.out.println("Search remaining: " + rateLimitStatus.getRemaining());
        if (rateLimitStatus.getRemaining() < 10) {
            System.out.println(
                    "Search has reached limit, wait " + rateLimitStatus.getSecondsUntilReset() + " seconds to reset");
            Thread.sleep(rateLimitStatus.getSecondsUntilReset() * 1000);
        }
    }

    private String formatHashTags(HashtagEntity[] hashtags) {

        String output = "hashtag(";
        for (HashtagEntity hashtag : hashtags) {
            output += hashtag.getText() + ",";
        }
        output += ")";
        return output;
    }

    private String formatMentions(UserMentionEntity[] mentions) {

        String output = "mention(";
        for (UserMentionEntity mention : mentions) {
            output += mention.getScreenName() + ",";
        }
        output += ")";
        return output;
    }
}
