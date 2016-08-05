package wpy.twitterbot.sentiment;

import java.io.IOException;
import java.text.ParseException;

import twitter4j.TwitterException;

public class Runner {
    public static void main(String[] args) throws TwitterException, IOException, ParseException, InterruptedException {
        if (args.length < 4) {
            System.err.println(
                    "Must provide arguments: <query text> <date yyyy-MM-dd> <size limit> <output path> [username]");
            System.exit(-1);
        }
        String company = args[0];
        String date = args[1];
        int limit = Integer.parseInt(args[2]);
        String outputPath = args[3];
        String username = null;
        if (args.length > 4) {
            username = args[4];
        }
        SearchEngine engine = new SearchEngine(company, date);
        System.out.println("username: " + username);
        engine.execute(limit, outputPath + "/" + company + "_" + date + ".txt", username);
    }
}
