package poem_generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* -Handles loading of corpus, and retrieving sentences from the corpus
 * -Corpus is stored as a single string, with delimiters for ease of parsing
 * -Poems are separated by #
 * 
 */
public class Corpus {
  /** Singleton Functions */
	private Corpus()
	{
		//corpus_scanner = null;
		corpus_string_full = new String();
		corpus_string_1 = new String();
		corpus_string_2 = new String();
		corpus_string_3 = new String();
		corpus_string_4 = new String();
			
		word_tools = WordNet_Wrapper.getInstance();
		//stanford_tools = Stanford_Wrapper.getInstance();
		
		loadCorpus(path);
	};
    
	//private instance
	private static Corpus corpus;
	
	//get instance
	public static synchronized Corpus getInstance()
	{   
		//synchronized method ensures that no 2 threads enter this if block concurrently
		if(corpus == null)
		{
		    corpus = new Corpus();
		} 
		return corpus;
	}
	
  /** Data Fields */	
	private static final String path = "corpusV2.txt";
	private static final int poem_count = 113;
	
	/* -Corpus is loaded and stored in 4 strings.
	 * -Each string is searched on a seperate thread
	 * -The full corpus is also stored - this does not necessarily take
	 *  up processing time, mainly storage space
	 */
	private String corpus_string_full;
	private String corpus_string_1;
	private String corpus_string_2;
	private String corpus_string_3;
	private String corpus_string_4;
	
	//break points to split corpus into 4 parts
	static int break_points[] = {29, 58, 87, 113};
	
	private static WordNet_Wrapper word_tools;
	private static Stanford_Wrapper stanford_tools;
    
  /** Functions */
	public void loadCorpus(String path)
	{
	    File file = null;
		Scanner scan = null;
		
		try
		{
			file = new File(path);
			scan = new Scanner(file);
				
			/*while(scan.hasNext())
			{
				this.corpus_string = this.corpus_string +  scan.nextLine() + "\n";
			}*/
			
			//this.corpus_scanner = new Scanner(this.corpus_string);
			int current_poem = 1;
			while(scan.hasNextLine())
			{   
				String line = scan.nextLine();
				if( current_poem <= break_points[0] )
				{   
					corpus_string_1 = corpus_string_1 + line +"\n";
				}	
				
				if( current_poem > break_points[0] && current_poem <= break_points[1] )
				{   
					corpus_string_2 = corpus_string_2 + line +"\n";
				}
				
				if( current_poem > break_points[1] && current_poem <= break_points[2] )
				{   
					corpus_string_3 = corpus_string_3 + line +"\n";
				}
				
				if( current_poem > break_points[2] && current_poem <= break_points[3] )
				{   
					corpus_string_4 = corpus_string_4 + line +"\n";		
			    }	
				
				if( line.equals("#") )
				{
					current_poem++;
				}
				
				corpus_string_full += line +"\n";
		    }
		}
		catch(FileNotFoundException ex)
		{   
			//exception safe if corpus is stored in project folder
			System.out.println("Unable to open file " + path + "\n");		
		}
	}
	
	public void printCorpus()
	{
		System.out.println(corpus_string_full);
	}
	
  /** Sentence Fetching */
	public static enum SentenceType
	{
	    CONTAINS_SUBJECT, CONTAINS_WORD, CONTAINS_RHYME, RANDOM	
	}
	
	/* -Randomizes the Sentence Type generated
	 * -Random randomSentenceType used so as not to create a new random object 
	 *  each time this function is called
	 */
	private static Random random_SentenceType = new Random(); 
	public static SentenceType getRandomSentenceType()
	{
		int type = random_SentenceType.nextInt(SentenceType.values().length);
		return SentenceType.values()[type];
	}
		
	private static Random random = new Random();
	public String getSentence(SentenceType type, String word)
	{   
		ArrayList<String> sentences = new ArrayList<String>(); //list of sentences retrieved from the corpus
		String final_sentence = null; //sentence to be returned from the list
		
		switch(type)
		{
			case CONTAINS_SUBJECT:
				//get subject with stanford_tools
				
				//find sentence
				//searchCorpus(word);
			    //TODO
			    break;
			case CONTAINS_WORD:
			    //TODO
			    break;
			case CONTAINS_RHYME:				
				String rhyming_word = null; //chosen word 	
				
				//get list of rhyming words
				ArrayList<String> rhymes = word_tools.getRhymingWords(word);
				if(rhymes != null)
				{   
					//TODO -this case is not finished
					
					//choose random rhyming word
					int random_rhyme = random.nextInt(rhymes.size()-1);
				    rhyming_word = rhymes.get(random_rhyme); 
				    
					//find a word at the end of a sentence - look for \n character
				    Pattern rhyming_pattern = Pattern.compile("(\\b"+ rhyming_word +"\\b)(\\p{Punct})?$"); 
				    
				    //search corpus for sentences that match rhyming_pattern
				    sentences = searchCorpus(rhyming_pattern, corpus_string_full);	
				    //System.out.println(sentences.get(0));
				}
		
				if(!sentences.isEmpty())
				{   
					//choose a random sentence //((max - min) + 1) + min
					int index = random.nextInt(sentences.size());
					final_sentence = sentences.get(index);

					return final_sentence;
				}
				
			    break;
			case RANDOM:
			{
				/* - Chooses a random poem to select a line from
				 * - reads poem into a string and counts lines
				 * - chooses a random line from poem
				 */
                
				//choose a random poem
				int poem_choice = random.nextInt(poem_count) + 1; //((max - min) + 1) + min
				int poem_length = 0;//length of chosen poem initialized to 0
				String poem = new String(); //chosen poem will be written here
				
				int current_poem = 1;
				Scanner local_scanner = null;
				
				//determine which of the 4 sections of the corpus current poem is in
				if(poem_choice>=0 && poem_choice <= break_points[0])
				{
					local_scanner = new Scanner(corpus_string_1);
					current_poem = 1;
				}
				else if( poem_choice > break_points[0] && poem_choice <= break_points[1] )
				{
					local_scanner = new Scanner(corpus_string_2);
					current_poem = break_points[0] + 1;
				}
				else if( poem_choice > break_points[1] && poem_choice <= break_points[2] )
				{   
					local_scanner = new Scanner(corpus_string_3);
					current_poem = break_points[1] + 1;
				}
				else if( poem_choice > break_points[3] && poem_choice <= break_points[4] )
				{
					local_scanner = new Scanner(corpus_string_4);
					current_poem = break_points[2] + 1;
				}
				else
				{
					local_scanner = new Scanner(corpus_string_full);
					//current_poem = 1;
				}
				
				//find beginning of chosen poem in corpus section
				while(local_scanner.hasNextLine())
				{   
					//find poem
					String current_line = new String();
					current_line = local_scanner.nextLine();
					
					if( current_line.equals("#") )
					{	
						current_poem++;		
					}
					
					//once poem is found - break
					if( current_poem == poem_choice)
					{   
						
						//System.out.println("Poem found!");
						break;
					}	
				}
				
				//starting from selected poem - read poem into string and count length
				while(local_scanner.hasNextLine())
			    {  
			        String next_line = local_scanner.nextLine();

					if(!next_line.equals("#"))
					{
					    poem += next_line + "\n";
			    		poem_length++;
					}
			    	else
			    	{
			    	    break; //exit while loop at next poem
			    	}    
			    }	    
				
				//get random line from chosen poem
				int line_number = 0;
				if(poem_length > 1)
				    line_number  = random.nextInt(poem_length) + 1;
                
			    Scanner poem_scanner = new Scanner(poem);
				
			    //get the chosen line from the poem
			    int current_line = 1;
			    while(poem_scanner.hasNextLine())
			    {   
			    	if(current_line != line_number)
			    	{
			    		poem_scanner.nextLine();
			    	    current_line++;	    
			    	}
			    	else
			    	{   
			    	    final_sentence = poem_scanner.nextLine();
			    	    poem_scanner.close();
			    	    local_scanner.close();
			    	    
			    	    return final_sentence;
			    	}
			    }			    	  
			    
			    local_scanner.close();
			    
			    break;
		    }	
		}//end switch
		
		
		
        return final_sentence; //null is returned if no sentence is found or some error occurs
	    
	}
		
	/* -To emulate optional args. If no word is passed, return random sentence 
	 * -Use this to specify RANDOM for readability.
	 * -getSentence is exception safe  
	 */
	public String getSentence(SentenceType type)
	{
		return getSentence(SentenceType.RANDOM, null);
	}
	
	/* -Searches for lines containing word and returns the line
	 * -Searches portion of the corpus passed as an argument
	 */
	private ArrayList<String> searchCorpus(Pattern pattern, String corpus_string)
	{   
		//local scanner is required since searchCorpus will operate on multiple threads
		Scanner local_scanner = new Scanner(corpus_string);

		ArrayList<String> sentences = new ArrayList<String>();
		String current_line = new String();
		
		//from start_poem, search sentences for word until end_poem
		while(local_scanner.hasNextLine())
		{   
			//read one line at a time
			current_line = local_scanner.nextLine();
			
		    //match pattern in current line
			Matcher matcher = pattern.matcher(current_line.toLowerCase());
			
			if(matcher.find())
			{
				//matcher.matches();
				sentences.add(current_line + "\n");
			}//*/
					
		}
		
		local_scanner.close();
		
		return sentences;
	}
	
  /** Multithreading functions */
	public Callable<String> getSentence_Callable(SentenceType type, String word)
	{
		return () -> {
			return getSentence(type, word);
		};
	}
	
	public Callable<String> getSentence_Callable(SentenceType type)
	{
		return () -> {
			return getSentence(type);
		};
	}
	
	/* -Corpus is searched using 4 threads each searching a quarter of the corpus
	 * -returns list of 4 callables
	 */
	 public List<Callable<ArrayList<String>>> searchCorpus(Pattern pattern)
	 {   
		 List<Callable<ArrayList<String>>> callables = new ArrayList<Callable<ArrayList<String>>>();
		 
		 Callable<ArrayList<String>> search1 = () -> {
			 return searchCorpus(pattern, corpus_string_full);	
		 };
		 
		 Callable<ArrayList<String>> search2 = () -> {
			 return searchCorpus(pattern, corpus_string_2);	
		 };
		 
		 Callable<ArrayList<String>> search3 = () -> {
			 return searchCorpus(pattern, corpus_string_3);	
		 };
		 
		 Callable<ArrayList<String>> search4 = () -> {
			 return searchCorpus(pattern, corpus_string_4);	
		 };//*/
		 
		 callables = Arrays.asList(search1, search2, search3, search4);
		 //callables = Arrays.asList(search1);
		 
		 return callables;
	 }
}
