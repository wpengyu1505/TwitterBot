package wpy.twitterbot.sentiment;

public class Sentiment {

    private double valence;
    private double arousal;
    private double dominance;
    private int hits;

    public Sentiment(double valence, double arousal, double dominance, int hits) {
        this.valence = valence;
        this.arousal = arousal;
        this.dominance = dominance;
        this.hits = hits;
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
	
    public String toString() {
        return "v=" + valence + ",a=" + arousal + ",d=" + dominance + ",hit=" + hits;
    }

}
