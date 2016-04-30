package poem_generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import poem_generator.Corpus;

public class Poem_Generator {
  /** Singleton Functions */
	private Poem_Generator()
	{
		corpus = Corpus.getInstance();
		word_tools = WordNet_Wrapper.getInstance();
		//stanford_tools = Stanford_Wrapper.getInstance();
		
		executor = new Executor_Wrapper();
	}
    
	//private instance
	private static Poem_Generator generator;
	
	//return instance
	public static synchronized Poem_Generator getInstance()
	{   
		if(generator == null)
		{
			generator = new Poem_Generator();
		}
			
	    return generator;
	}
	
  /** Data Fields */
	private static Corpus corpus;
    private static WordNet_Wrapper word_tools;
    private static Stanford_Wrapper stanford_tools;
  
  /** Multithreading */
    private static Executor_Wrapper executor; //cached threadPool creates threads as needed
    
  /** Generation Functions */
    private static Random random = new Random(); 
    public ArrayList<String> generateFreeVerse(int numStanzas, int linesPerStanza, float rhymeBias)
	{   
		ArrayList<String> Poem = new ArrayList<String>(); //easiest to represent as an arraylist so individual lines can be accessed
		
		Pattern pattern = Pattern.compile("(\\b\\w+\\b)(\\p{Punct})?$");//get word at end of line
		//Pattern pattern = Pattern.compile("(\\b\\w*\\b).*?(\n)");//get word at end of line
		Matcher matcher; 	
		String end_word = null;
		
	    for(int stanza = 0 ; stanza < numStanzas ; stanza++)
	    {   
	    	/**not implemented at the moment*/
	        //boolean lastRhymingCouplet = false; //true if last 2 lines rhymed
	        //float rhymeBias; //probability of a next sentence rhyming
	        
	        //generate first line
	        Poem.add(corpus.getSentence(Corpus.SentenceType.RANDOM)); //append random sentence
	        for(int line = 1 ; line<linesPerStanza ; line++)
	        {   
	        	//list containing callables which result in the return of a list of sentences containing rhyming words
	    	   	List<Callable<String>> callables = new ArrayList<Callable<String>>();
	       	    	
	       	   	//get word at end of last line
	       	   	matcher = pattern.matcher(Poem.get( line - 1 ));	
	       	    	
	       	   	if(matcher.find())
	       	   	{
	       	   	    end_word = matcher.group(0);    
	       	    	    
	       	   	    ArrayList<String> rhymes = word_tools.getRhymingWords(end_word); //list of rhyming words
                       
	      	        /** -There is a bug where InputStream throws an exception in the
	       	   	     *   getRhymingWords() function in the WordNet_Wrapper class.
	       	   	     *  -Below is a crude and unsafe brute force solution that continues pressing
	       	   	     *   the function for a return indefinitely
	       	   	     *  -The bug is possibly related to the fact that it relies on the URL to rhymebrain.com,
	           	     *   where rhymebrain.com itself may be the cause of the issues
    	    	     *  -Also note that rhymebrain.com enforces a hard limit on the number of requests
	   	    	     *   submitted by a single IP per hour
	       	         */
	       	   	    while(rhymes == null)
                    {
                        rhymes = word_tools.getRhymingWords(end_word); //list of rhyming words
                    }
	       	    	    
	             	//search corpus for sentences containing the first number_of_rhymes rhymes at most
	       	    	int number_of_rhymes = 15;
	       	        for(int i = 0; i<number_of_rhymes; i++)
	       	   	    {   
	       	   	     	if(i >= rhymes.size())//if number_of_rhymes rhymes were not found, break at size
	       	         	{	
	       	   	    		break;
	       	   	    	}	
	       	   	        callables.add(corpus.getSentence_Callable(Corpus.SentenceType.CONTAINS_RHYME, rhymes.get(i)));
	       	   	    }
	       	    	    
	           	    //retrieve a random sentence in case no rhyming sentences are found
	   	    	    callables.add(corpus.getSentence_Callable(Corpus.SentenceType.RANDOM));
	       	    }
	       	    	
	       	   	//search corpus for sentences containing rhyming words
	       	   	List<String> rhyming_sentences = executor.invokeAll_String(callables);
	       	   	
	       	   	//choose a sentence
	           	int index = 0;
	      	   	if(rhyming_sentences.size() == 1)
	           	{
	        		Poem.add(rhyming_sentences.get(0).replace("\n",""));
	   	    	}
	       	   	else
	           	{	
	   	    		index  = random.nextInt(rhyming_sentences.size()-1);
	      	    		
	       	        Poem.add(rhyming_sentences.get(index).replace("\n",""));
	       	   	}
	       	 	
	        }		        
	        //skip a line for next stanza
	        Poem.add("");      
        }
	    
	    return Poem;
    }
		
	public void generateSonnet()
	{
		//TODO
	}
		
	public void generateHaiku()
	{
		//relies on syllables
		//TODO
	}
}