package poem_generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

/* -Handles loading of corpus, and retrieving sentences from the corpus
 * -Corpus is stored as a single string, with delimiters for ease of parsing
 * -Poems are separated by *
 * -Could store poems separately in a map rather than a String
 */
public class Corpus {
  /** Singleton Functions*/	
	//private constructor
	private Corpus(){};
	
	//private unique instance
	private static final Corpus corpus = new Corpus();
	
	//retrieve instance
	public static Corpus getInstance()
	{
		return corpus;
	}
	
  /** Data Fields */	
	//use a scanner containing corpus for easy parsing
	private Scanner corpusScanner = null; 
	private String corpusString = new String();

  /** Functions */
	public void loadCorpus(String path)
	{
	    File file = null;
		Scanner scan = null;
			
		try
		{
			file = new File(path);
			scan = new Scanner(file);
				
			while(scan.hasNext())
			{
				this.corpusString = this.corpusString +  scan.nextLine() + "\n";
			}
				
			this.corpusScanner = new Scanner(this.corpusString);
				
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("Unable to open file " + path + "\n");		
		}
			
	}
	
	public void printCorpus()
	{
		System.out.println(corpusString);
	}
	
  /** Sentence Fetching */
	public static enum SentenceType
	{
	    CONTAINS_SUBJECT, CONTAINS_WORD, CONTAINS_RHYME, RANDOM	
	}
	
	/* -Randomises the Sentence Type generated
	 * -randomSentenceType used so as not to create a new random each time this function 
	 *  is called
	 */
	private static Random randomSentenceType = new Random(); 
	public static SentenceType getRandomSentenceType()
	{
		int type = randomSentenceType.nextInt(SentenceType.values().length);
		return SentenceType.values()[type];
	}
		
	public static String getSentence(SentenceType type, String word)
	{   
		String sentence = null;
		switch(type)
		{
			case CONTAINS_SUBJECT:
			    //TODO
			    break;
			case CONTAINS_WORD:
			    //TODO
			    break;
			case CONTAINS_RHYME:
			    //TODO
			    break;
			case RANDOM:
			    //TODO
			    //get a random sentence from the corpus
			    break;
		}
	    return sentence;
	}
		
	/* -To emulate optional args. If no word is passed, return random sentence, or 
	 *  use this to specify RANDOM for readability.
	 * -getSentence is exception safe  
	 */
	public static String getSentence(SentenceType type)
	{
		return getSentence(SentenceType.RANDOM, null);
	}
}
