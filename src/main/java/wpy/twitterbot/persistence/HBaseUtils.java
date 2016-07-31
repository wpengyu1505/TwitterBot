package wpy.twitterbot.persistence;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import wpy.twitterbot.sentiment.Sentiment;

public class HBaseUtils {

    private static final String TBL_SENTIMENT = "SENTIMENT";
    private static final byte[] CONTENT_FAMILY = Bytes.toBytes("C");
    private static final byte[] SENTIMETN_FAMILY = Bytes.toBytes("S");
    private static final byte[] VALENCE_QUALIFIER = Bytes.toBytes("VALENCE");
    private static final byte[] AROUSAL_QUALIFIER = Bytes.toBytes("AROUSAL");
    private static final byte[] DOMINANCE_QUALIFIER = Bytes.toBytes("DOMINANCE");
    private static final byte[] KEYWORDS_QUALIFIER = Bytes.toBytes("KEYWORD");

    private Configuration conf = null;

    public HBaseUtils() {
        conf = HBaseConfiguration.create();
    }

    @SuppressWarnings("deprecation")
    public Put putTwitterSentiments(String record) {

        Sentiment sentiment = new Sentiment(record);
        Put put = new Put(Bytes.toBytes(sentiment.getDateTime()));
        put.add(CONTENT_FAMILY, VALENCE_QUALIFIER, Bytes.toBytes(sentiment.getValence()));
        put.add(SENTIMETN_FAMILY, VALENCE_QUALIFIER, Bytes.toBytes(sentiment.getValence()));
        put.add(SENTIMETN_FAMILY, AROUSAL_QUALIFIER, Bytes.toBytes(sentiment.getValence()));
        put.add(SENTIMETN_FAMILY, DOMINANCE_QUALIFIER, Bytes.toBytes(sentiment.getValence()));
        put.add(SENTIMETN_FAMILY, KEYWORDS_QUALIFIER, Bytes.toBytes(sentiment.getValence()));

        return put;
    }

    public void loadSentimentFile(String filename) throws IOException {

        HTable sentimentTable = new HTable(conf, TBL_SENTIMENT);
        sentimentTable.setAutoFlush(false);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        String line = null;
        ArrayList<Put> puts = new ArrayList<Put>();
        int counter = 0;
        while ((line = br.readLine()) != null) {
            puts.add(putTwitterSentiments(line));
            counter++;
            if (counter > 100) {
                sentimentTable.put(puts);
                counter = 0;
                puts.clear();
            }
        }

        // Put the final batch and flush
        sentimentTable.put(puts);
        sentimentTable.flushCommits();
        br.close();
    }
}
