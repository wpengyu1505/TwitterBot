package wpy.twitterbot.sentiment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Properties;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Twitter sentiment analysis
 * 
 * @author wpengyu
 */
public class SearchEngine {

    private static final int SEARCH_LIMIT = 100;
    private static final String OAUTH_PROPERTIES_FILE = "access.properties";
    private Twitter twitter;
    private Query query;
    private ConfigurationBuilder conf;
    private SentimentAnalyzer analyzer;

    public SearchEngine(String text, String date) throws IOException {

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
        query = new Query(text + " -rt -http -https -@" + text);
        query.setLang("en");
        query.setUntil(date);
    }

    public void execute(int limit, String outputFile) throws TwitterException, IOException {
        query.setCount(limit);
        if (limit > SEARCH_LIMIT) {
            executeByBatch(limit, outputFile);
        } else {
            OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(outputFile));
            QueryResult result = twitter.search(query);
            for (Status status : result.getTweets()) {
                processTweetOutput(status, fw);
            }
            fw.close();
        }
    }

    private void executeByBatch(int limit, String outputFile) throws TwitterException, IOException {
        query.setCount(limit);
        long tweetId = Long.MAX_VALUE;
        long count = 0;
        OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(outputFile));
        while (count < limit) {
            QueryResult result = twitter.search(query);
            for (Status status : result.getTweets()) {
                tweetId = tweetId > status.getId() ? status.getId() : tweetId;
                count++;
                processTweetOutput(status, fw);
            }

            // System.out.println(tweetId);
            // Set the minimum tweet ID as the max for the next query
            query.setMaxId(tweetId - 1);
        }
        fw.close();
    }

    private void processTweetOutput(Status status, OutputStreamWriter writer) throws IOException {
        String text = status.getText();
        Sentiment sm = analyzer.getSentimentScore(text);
        if (sm != null) {
            String out = status.getCreatedAt().toString() + "|" + "@" + status.getUser().getScreenName() + "|"
                    + status.getText().replaceAll("\n", ". ").replaceAll("\\|", " ") + "|" + sm.toString();
            System.out.println(out);
            writer.write(out + "\n");
        }
    }

}
