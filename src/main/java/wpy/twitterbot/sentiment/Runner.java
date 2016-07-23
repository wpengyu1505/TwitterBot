package wpy.twitterbot.sentiment;

import java.io.IOException;

import twitter4j.TwitterException;

public class Runner {
    public static void main(String[] args) throws TwitterException, IOException {
        if (args.length < 4) {
            System.err.println("Must provide arguments: <query text> <date yyyy-MM-dd> <size limit> <output path>");
            System.exit(-1);
        }
        String company = args[0];
        String date = args[1];
        int limit = Integer.parseInt(args[2]);
        String outputPath = args[3];
        SearchEngine engine = new SearchEngine(company, date);
        engine.execute(limit, outputPath + "/" + company + "_" + date + ".txt");
    }
}
