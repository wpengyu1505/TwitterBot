package wpy.twitterbot.sentiment;

import java.util.ArrayList;
import java.util.HashSet;

public class Sentiment {

    private double valence;
    private double arousal;
    private double dominance;
    private int hits;
    private ArrayList<String> keywords;

    public Sentiment(double valence, double arousal, double dominance, int hits, ArrayList<String> keywords) {
        this.valence = valence;
        this.arousal = arousal;
        this.dominance = dominance;
        this.hits = hits;
        this.keywords = keywords;
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

	public String toString() {
		String keys = "";
		for (String key : keywords) {
			keys = keys + key + "-";
		}
		keys = keys.substring(0, keys.length() - 1);
        return "v=" + valence + ",a=" + arousal + ",d=" + dominance + ",hit=" + hits + ",key=" + keys;
    }

}
