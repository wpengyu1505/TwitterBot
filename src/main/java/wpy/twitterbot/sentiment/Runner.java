package wpy.twitterbot.sentiment;

import java.io.IOException;
import java.text.ParseException;

import twitter4j.TwitterException;

public class Runner {
    public static void main(String[] args) throws TwitterException, IOException, ParseException, InterruptedException {
        if (args.length < 5) {
            System.err.println(
                    "Must provide arguments: <text|username> <content> <date yyyy-MM-dd> <size limit> <output path>");
            System.exit(-1);
        }

        String mode = args[0];
        String content = args[1];
        String date = args[2];
        int limit = Integer.parseInt(args[3]);
        String outputPath = args[4];
        SearchEngine engine = new SearchEngine(content, date);
        if (mode.equals("tweet")) {
            engine.searchTweets(content, date, limit, outputPath + "/" + content + "_" + date + ".txt");
        } else if (mode.equals("user")) {
            engine.getUserTimeline(content, outputPath + "/" + content + ".txt");
        } else {
            System.err.println("Not supported action");
            System.exit(-1);
        }
    }
}
