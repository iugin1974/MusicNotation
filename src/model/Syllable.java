package model;

public class Syllable {
	 private String text;

	    public Syllable(String text) {
	        this.text = text;
	    }

	    public String getText() { return text; }
	    public boolean isExtender() { return "__".equals(text); }
	    public boolean isHyphen() { return "--".equals(text); }
	    public boolean isIgnored() { return "_".equals(text); }
}
