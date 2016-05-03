package poem_generator;

public class Verse {
	private int song;
	private String verse;
	
	public Verse(int song, String verse){
		this.song = song;
		this.verse = verse;
	}
	
	public int getSong(){
		return song;
	}
	
	public String getVerse(){
		return verse;
	}
	
	public String toString(){
		return song + " " + verse + "\n";
	}

}
