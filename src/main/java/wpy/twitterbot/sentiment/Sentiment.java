package wpy.twitterbot.sentiment;

public class Sentiment {

    private double valence;
    private double arousal;
    private double dominance;

    public Sentiment(double valence, double arousal, double dominance) {
        this.valence = valence;
        this.arousal = arousal;
        this.dominance = dominance;
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

    public String toString() {
        return "v=" + valence + ",a=" + arousal + ",d=" + dominance;
    }
}
