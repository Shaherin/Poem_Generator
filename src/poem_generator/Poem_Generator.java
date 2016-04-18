package poem_generator;

import poem_generator.Corpus;

public class Poem_Generator {
  /** Singleton Functions */
	private Poem_Generator()
	{
		corpus = Corpus.getInstance();
		wordTools = WordNet_Wrapper.getInstance();
	}
    
	//private instance
	private static final Poem_Generator generator = new Poem_Generator();
	
	//return instance
	public static Poem_Generator getInstance()
	{   
		return generator;
	}
	
  /** Data Fields */
	private static Corpus corpus;
    private static WordNet_Wrapper wordTools;
  
	
  /** Generation Functions */
    public static void generateFreeVerse(int numStanzas, int linesPerStanza, float rhymeBias)
	{   
		String Poem = null;
			
	    for(int stanza = 0 ; stanza<numStanzas-1 ; stanza++)
	    {   
	        boolean lastRhymingCouplet = false; //true if last 2 lines rhymed
	        //float rhymeBias; //probability of a next sentence rhyming
	        for(int line = 0 ; line<linesPerStanza-1 ; line++)
	        {   
	        	//generate first line
	            if(line == 0)
	            {   
	        	    Poem += corpus.getSentence(Corpus.SentenceType.RANDOM); //append random sentence
	        	}
	        	//rhyming is done mostly in couplets
	        	else if(lastRhymingCouplet)
	        	{
	        		/* •Begin a new rhyming couplet
	       			 *   -select a new word, subject, or random sentence, and guarantee the next sentence rhymes
	       			 *   
	       			 * •Continue rhyme to triplet etc... - random with increasing negative bias
	       			 * 
	       			 * •Interlude ie. no rhyme - random sentence  
	       			 */
	       	    	
	       	    	//lastRhymingCouplet = false;
        		}
	        }	
	        //skip a line for next stanza
	    }
    }
		
	public static void generateSonnet()
	{
		//TODO
	}
		
	public static void generateHaiku()
	{
		//relies on syllables
		//TODO
	}
}
