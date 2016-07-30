package wpy.twitterbot.sentiment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class SentimentAnalyzer {

    private HashMap<String, Keyword> keywords;
    public static final String METAFILE = "ANEW.txt";

    public SentimentAnalyzer() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(METAFILE)));
        keywords = new HashMap<String, Keyword>();
        String line = null;
        while ((line = br.readLine()) != null) {
            Keyword keyword = new Keyword(line);
            keywords.put(keyword.getWord(), keyword);
        }
        br.close();
    }

    public Sentiment getSentimentScore(String line) {

        String[] tokens = line.split(" ");
        ArrayList<String> keys = new ArrayList<String>();
        ArrayList<Keyword> hitKeywords = new ArrayList<Keyword>();
        
        double valence = 0;
        double arousal = 0;
        double dominance = 0;
        int hitCount = 0;

        for (String token : tokens) {
            if (keywords.containsKey(token.toLowerCase())) {
                Keyword kw = keywords.get(token.toLowerCase());
                valence += kw.getValence();
                arousal += kw.getArousal();
                dominance += kw.getDominance();
                
                hitKeywords.add(kw);
                hitCount++;
                keys.add(kw.getWord());
            }
        }
        
        

        return hitCount < 1 ? null : new Sentiment(valence / hitCount, arousal / hitCount, dominance / hitCount, hitCount, keys);
    }
    
    public double getWeightedMeanValence(ArrayList<Keyword> keywords) {
        double totalStd = 0;
        for (Keyword kw : keywords) {
            totalStd += kw.getValenceStd();
        }
        
        double weightSum = 0;
        for (Keyword kw : keywords) {
            weightSum += 1 - (kw.getValenceStd() / totalStd);
        }
        
        double meanValence = 0;
        for (Keyword kw : keywords) {
            meanValence += (1 - (kw.getValenceStd() / totalStd)) / weightSum;
        }
        
        return meanValence;
    }
    
    public double getWeightedMeanArousal(ArrayList<Keyword> keywords) {
        double totalStd = 0;
        for (Keyword kw : keywords) {
            totalStd += kw.getArousalStd();
        }
        
        double weightSum = 0;
        for (Keyword kw : keywords) {
            weightSum += 1 - (kw.getArousalStd() / totalStd);
        }
        
        double meanArousal = 0;
        for (Keyword kw : keywords) {
            meanArousal += (1 - (kw.getArousalStd() / totalStd)) / weightSum;
        }
        
        return meanArousal;
    }

    private class Keyword {

        private String word;
        private double valence;
        private double valenceStd;
        private double arousal;
        private double arousalStd;
        private double dominance;
        private double dominanceStd;
        private int frequency;

        public Keyword(String line) {
            String[] fields = line.split(" ");
            //System.out.println(line);
            this.word = fields[0];
            this.valence = Double.parseDouble(fields[2]);
            this.valenceStd = Double.parseDouble(fields[3].replaceAll("\\(", "").replaceAll("\\)", ""));
            this.arousal = Double.parseDouble(fields[4]);
            this.arousalStd = Double.parseDouble(fields[5].replaceAll("\\(", "").replaceAll("\\)", ""));
            this.dominance = Double.parseDouble(fields[6]);
            this.dominanceStd = Double.parseDouble(fields[7].replaceAll("\\(", "").replaceAll("\\)", ""));
            this.frequency = fields[8].equals(".") ? 0 : Integer.parseInt(fields[8]);
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public double getValence() {
            return valence;
        }

        public void setValence(double valence) {
            this.valence = valence;
        }

        public double getValenceStd() {
            return valenceStd;
        }

        public void setValenceStd(double valenceStd) {
            this.valenceStd = valenceStd;
        }

        public double getArousal() {
            return arousal;
        }

        public void setArousal(double arousal) {
            this.arousal = arousal;
        }

        public double getArousalStd() {
            return arousalStd;
        }

        public void setArousalStd(double arousalStd) {
            this.arousalStd = arousalStd;
        }

        public double getDominance() {
            return dominance;
        }

        public void setDominance(double dominance) {
            this.dominance = dominance;
        }

        public double getDominanceStd() {
            return dominanceStd;
        }

        public void setDominanceStd(double dominanceStd) {
            this.dominanceStd = dominanceStd;
        }

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }
    }

}
