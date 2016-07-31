package wpy.twitterbot.sentiment;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Sentiment {

    private String tweetId;
    private String dateTime;
    private String tweet;
    private double valence;
    private double arousal;
    private double dominance;
    private int hits;
    private ArrayList<String> keywords;
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private static final String delimiter = "\\|";

    public Sentiment(String tweetId, String tweet, String dateTime, double valence, double arousal, double dominance,
            int hits, ArrayList<String> keywords) {

        this.tweet = tweet;
        this.dateTime = dateTime;
        this.valence = valence;
        this.arousal = arousal;
        this.dominance = dominance;
        this.hits = hits;
        this.keywords = keywords;
    }

    public Sentiment(String line) {
        String[] tokens = line.split(delimiter);
        this.tweetId = tokens[0];
        this.dateTime = tokens[1];
        this.tweet = tokens[3];

        String[] sentiments = tokens[4].split(",");
        this.valence = Double.parseDouble(sentiments[0].split("=")[1]);
        this.arousal = Double.parseDouble(sentiments[1].split("=")[1]);
        this.dominance = Double.parseDouble(sentiments[2].split("=")[1]);
        this.hits = Integer.parseInt(sentiments[3].split("=")[1]);

        this.keywords = new ArrayList<String>();
        for (String key : sentiments[4].split("=")[1].split("-")) {
            this.keywords.add(key);
        }
    }

    public String getTweetId() {
        return tweetId;
    }

    public void setTweetId(String tweetId) {
        this.tweetId = tweetId;
    }

    public double getValence() {
        return valence;
    }

    public void setValence(double valence) {
        this.valence = valence;
    }

    public double getArousal() {
        return arousal;
    }

    public void setArousal(double arousal) {
        this.arousal = arousal;
    }

    public double getDominance() {
        return dominance;
    }

    public void setDominance(double dominance) {
        this.dominance = dominance;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public String toString() {
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        String keys = "";
        for (String key : keywords) {
            keys = keys + key + "-";
        }
        keys = keys.substring(0, keys.length() - 1);
        return "v=" + decimalFormat.format(valence) + ",a=" + decimalFormat.format(arousal) + ",d="
                + decimalFormat.format(dominance) + ",hit=" + hits + ",key=" + keys;
    }

}
