package poem_generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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
	        boolean lastRhymingCouplet = false; //true if last 2 lines rhymed
	        //float rhymeBias; //probability of a next sentence rhyming
	        
	        //generate first line
	        Poem.add(corpus.getSentence(Corpus.SentenceType.RANDOM)); //append random sentence
	        for(int line = 1 ; line<linesPerStanza ; line++)
	        {   
	        	//rhyming is done mostly in couplets
	        		/* •Begin a new rhyming couplet
	       			 *   -select a new word, subject, or random sentence, and guarantee the next sentence rhymes
	       			 *   
	       			 * •Continue rhyme to triplet etc... - random with increasing negative bias
	       			 * 
	       			 * •Interlude ie. no rhyme - random sentence  
	       			 */
	        		
	        		//list containing callables which result in the return of a list of sentences containing rhyming words
	       	    	List<Callable<String>> callables = new ArrayList<Callable<String>>();
	       	    	
	       	    	//get word at end of last line
	       	    	matcher = pattern.matcher(Poem.get( line - 1 ));	
	       	    	if(matcher.find())
	       	    	{
	       	    	    end_word = matcher.group(0);    
	       	    	    ArrayList<String> rhymes = word_tools.getRhymingWords(end_word); //list of rhyming words
	       	    	    System.out.println(rhymes.size() -1);
	       	    	    //search corpus for sentences containing the first 10 rhymes at most
	       	    	    for(int i = 0; i<5; i++)
	       	    	    {   
	       	    	    	if(i >= rhymes.size())//if 10 rhymes were not found, break at size
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
	       	    		Poem.add(corpus.getSentence(Corpus.SentenceType.RANDOM));
	       	    		//Poem.add(rhyming_sentences.get(0));
	       	    	}
	       	    	else
	       	    	{	
	       	    		index  = random.nextInt(rhyming_sentences.size()-1);	    	
	       	    	    Poem.add(rhyming_sentences.get(index));
	       	    		//Poem.add(corpus.getSentence(Corpus.SentenceType.RANDOM));	
	       	    	}
	       	    	
	        }	
	        
	        //skip a line for next stanza
	        Poem.add("\n");      
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
	
	
	//finds a word in the corpus
		public List<Callable<ArrayList<String>>> testCorpus(String word) throws InterruptedException, ExecutionException
		{   
			Pattern word_pattern = Pattern.compile("\\b"+word +"\\b"); 
			
			//list of all callables returning result of type ArrayList<String>
			List<Callable<ArrayList<String>>> callables = corpus.searchCorpus(word_pattern);
			//list of Future objects corresponding to callables
			//List<Future<ArrayList<String>>> results = new ArrayList<Future<ArrayList<String>>>();
			
			return callables;
			
			//submit callables to the executor
			/*try 
			{
				results = executor.invokeAll(callables);
			} 
			catch (InterruptedException e) 
			{
				System.out.println("Thread interrupted");
				Thread.currentThread().interrupt(); //if a thread is interrupted, we must interrupt it to continue execution
			}
			
			//final list of String poem lines to be returned once all threads are complete 
			ArrayList<String> final_result = new ArrayList<String>();
			
			final_result.addAll(results.get(0).get());
			final_result.addAll(results.get(1).get());
			final_result.addAll(results.get(2).get());
			final_result.addAll(results.get(3).get());
			
			return final_result;*/
			
		}
}
