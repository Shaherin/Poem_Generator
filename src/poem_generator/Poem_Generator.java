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
		stanford_tools = Stanford_Wrapper.getInstance();
		
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
    public ArrayList<String> generateFreeVerse(int numStanzas, int linesPerStanza)
	{   
		ArrayList<String> Poem = new ArrayList<String>(); //easiest to represent as an arraylist so individual lines can be accessed
		
		//patterns
		Pattern pattern = Pattern.compile("(\\b\\w+\\b)(\\p{Punct})?$");//get word at end of line
		Pattern ends_with_punctuation = Pattern.compile("(\\b\\w+\\b)([,;:-])$");//ends with commas/semicolon/colon
		Pattern conj_pattern = Pattern.compile("^\\b\\w+_CC\\b"); //conjunction at beginning of sentence
		Pattern determiner_pattern = Pattern.compile("\\b\\w+_DT\\b$"); //sentence endind with det
		
		Matcher matcher; //matcher will be reused	
		String end_word = null;
		
	    for(int stanza = 0 ; stanza < numStanzas ; stanza++)
	    {   
	    	/**not implemented at the moment*/
	        //boolean lastRhymingCouplet = false; //true if last 2 lines rhymed
	        //float rhymeBias; //probability of a next sentence rhyming
	    	
	        //generate first line of the stanza (ensuring no conjunctions at start)
	    	boolean first_line_found = false;
	    	
	    	String first_line = null;
	    	String tagged_line = null;
	    	while(!first_line_found)
	    	{
	    		first_line = corpus.getSentence(Corpus.SentenceType.RANDOM); //string to have determiners removed
				
		    	tagged_line = stanford_tools.POS_Tagger(first_line); //return a tagged string	  
				Matcher conj_matcher = conj_pattern.matcher(tagged_line); //look for conj at beginning of sentence   
				   
				   
				//identify determiners
			    if(!conj_matcher.find())
				{
				    first_line_found = true;
				}	
	    	} 			
	    	
			//add first line of stanza
	    	Poem.add(first_line); 
	        
	        //generate the rest of the poem
	        for(int line = 1 ; line<linesPerStanza ; line++)
	        {   
	        	//list containing callables which result in the return of a list of sentences containing rhyming words
	    	   	List<Callable<String>> callables = new ArrayList<Callable<String>>();
	       	    
	    	   	//odd lines must attempt to rhyme since rhyming is done in couplets
	    	   	if(line %2 == 0)
	    	   	{
	    	   		Poem.add(corpus.getSentence(Corpus.SentenceType.RANDOM));
	    	   		continue;
	    	   	}//*/
	    	   	
	       	   	//get word at end of last line of the poem
	       	   	matcher = pattern.matcher(Poem.get( line - 1 ));	
	       	   	
	       	   	if(matcher.find())
	       	   	{
	       	   	    end_word = matcher.group(0);    
	       	    	
	       	   	    //get list of words that rhyme with end_word
	       	   	    ArrayList<String> rhymes = word_tools.getRhymingWords(end_word);
                       
	      	        /** ��The issue described below was solved(redundant calls were being made to rhymebrain in Corpus class), 
	      	         *    however the fix does no harm so it shall remain
	      	         *    
	      	         *  -There is a bug where InputStream throws an exception in the
	       	   	     *   getRhymingWords() function in the WordNet_Wrapper class.
	       	   	     *  -Below is a crude and unsafe brute force solution that continues pressing
	       	   	     *   the function for a return indefinitely
	       	   	     *  -The bug is possibly related to the fact that it relies on the URL to rhymebrain.com,
	           	     *   where rhymebrain.com itself may be the cause of the issues
    	    	     *  -Also note that rhymebrain.com enforces a hard limit(350) on the number of requests
	   	    	     *   submitted by a single IP per hour (each individual request counts towards limit)
	       	         */
	       	   	    while(rhymes == null)
                    {
                        rhymes = word_tools.getRhymingWords(end_word); //list of rhyming words
                    }
	       	    	    
	             	//search corpus for sentences containing the first number_of_rhymes rhymes at most
	       	    	int number_of_rhymes = 20;
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
	      	   		//if no rhyming sentences were found, add index 0 which is the random sentence
	        		Poem.add(rhyming_sentences.get(0).replace("\n","")); 
	   	    	}
	       	   	else
	           	{       	        
	       	   		//random int excluding the random sentence
	   	    		index  = random.nextInt(rhyming_sentences.size()-1);    	    	   	   
	       	         	    		
	   	    		Poem.add(rhyming_sentences.get(index).replace("\n",""));
	       	   	}
	    	   	
	      	   	/** Final checks before line is finalized*/	   	
	      	   	//the last line of a stanza should not end in punctuation or a determiner
	      	   	if(line == linesPerStanza-1)
	      	   	{   
	      	   		String current_line = Poem.get(line);
	      	   		matcher = ends_with_punctuation.matcher(current_line);
	      	   		
	      	   		if(matcher.find())
	      	   		{
	      	   			Poem.set(line, current_line.replace(matcher.group(2), ""));
	      	   		}
	      	   	    
	      	   		current_line = Poem.get(line);
	      	   		
	      	   		matcher = determiner_pattern.matcher(current_line);
	      	   		if(matcher.find())
	      	   		{
	      	   		Poem.set(line, current_line.replace(matcher.group(1), ""));
	      	   		}
	      	   		
	      	   	}
	       	 	
	        }	   	   	
	        //skip a line for next stanza
	        Poem.add("");      
        }	    
	    System.out.println("Complete");
	    return Poem;
    }
		
    public ArrayList<String> generateSonnet()
	{
		ArrayList<String> Poem = new ArrayList<String>();
		Pattern pattern = Pattern.compile("(\\b\\w+\\b)(\\p{Punct})?$");//get word at end of line
		Matcher matcher; 	
		String end_word = null;
		for(int stanza = 0 ; stanza < 3 ; stanza++)
	    { 
		String sentence = corpus.getSentence(Corpus.SentenceType.RANDOM);
		while(sentence == null || countSyllables(sentence) != 10){
			sentence = corpus.getSentence(Corpus.SentenceType.RANDOM);
		}
		System.out.println(sentence);
		Poem.add(sentence);
		
		sentence = corpus.getSentence(Corpus.SentenceType.RANDOM);
		while(sentence == null || countSyllables(sentence) != 10){
			sentence = corpus.getSentence(Corpus.SentenceType.RANDOM);
		}
		System.out.println(sentence);
		Poem.add(sentence);

		for(int line = 2 ; line < 4 ; line++)
		{   
		  List<Callable<String>> callables = new ArrayList<Callable<String>>();
   	    	
       	   //get word at end of last line
		   matcher = pattern.matcher(Poem.get(Poem.size()-2));
		   String sen = "";
		   if(matcher.find())
       	   	{
       	   	    end_word = matcher.group(0);    
       
       	    	/*ArrayList<String> rhymes = new ArrayList<String>();
	       	   	String[] a = {"see","be","me","maybe","sea","enemy","dignity","happy", "completely","melody","beauty",
	       	   			"proudly","loudly","free","reality","freely", "you","do"};
	       	   	for(int i = 0; i < a.length; i++){
	       	   		rhymes.add(a[i]);
	       	   	}//*/
       	   	    
       	   	    ArrayList<String> rhymes = word_tools.getRhymingWords(end_word); //list of rhyming words
       	   	    while(rhymes == null)
                {
                  rhymes = word_tools.getRhymingWords(end_word); //list of rhyming words              
                }//*/
       	    	    
             	//search corpus for sentences containing the first number_of_rhymes rhymes at most
       	    	 int number_of_rhymes = 10;
       	  
       	    	 for(int i = 0; i<number_of_rhymes; i++)
       	   	    {   
       	   	     	if(i >= rhymes.size())//if number_of_rhymes rhymes were not found, break at size
       	         	{	
       	   	    		break;
       	   	    	}	
       	   	        callables.add(corpus.getSentence_Callable(Corpus.SentenceType.CONTAINS_RHYME, rhymes.get(i)));
       	   	  sen = corpus.getSentence(Corpus.SentenceType.CONTAINS_RHYME, rhymes.get(i));
       	   	  
       	   	      
       	   	  //System.out.println(sen);
       	   	       // while(sen == null ||sen.equals(Poem.get(Poem.size() - 2)) ){ //||  
   	   	             //sen = corpus.getSentence(Corpus.SentenceType.CONTAINS_RHYME, rhymes.get(i));
   	   	           // System.out.println(sen);
       	   	        //}
       	   	    System.out.println(sen);  
       	   	    }
       	   	   
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
   	    		index  = random.nextInt(rhyming_sentences.size() - 1);
   	    		System.out.println(index);
   	    		Poem.add(rhyming_sentences.get(index).replace("\n",""));
       	   	}
         }
	
		if(stanza == 1){
			Poem.add("");
		}
		if(stanza == 2){
			String rhymingCouplet = corpus.getSentence(Corpus.SentenceType.RANDOM);
			while(rhymingCouplet == null || countSyllables(rhymingCouplet) != 10){
				rhymingCouplet = corpus.getSentence(Corpus.SentenceType.RANDOM);
			}
			Poem.add(rhymingCouplet);
		    matcher = pattern.matcher(Poem.get(Poem.size()-1));
			List<Callable<String>> callables = new ArrayList<Callable<String>>();
			String sen = "";
			if(matcher.find())
       	   	{
       	   	    end_word = matcher.group(0);    
       	
       	    	/*ArrayList<String> rhymes = new ArrayList<String>();
	       	   	String[] a = {"see","be","me","maybe","sea","enemy","dignity","happy", "completely","melody","beauty",
	       	   			"proudly","loudly"};
	       	   	for(int i = 0; i < a.length; i++){
	       	   		rhymes.add(a[i]);
	       	   	}//*/
       	   	    
       	   	    ArrayList<String> rhymes = word_tools.getRhymingWords(end_word); //list of rhyming words
       	    	while(rhymes == null)
                {
                  rhymes = word_tools.getRhymingWords(end_word); //list of rhyming words              
                }//*/
       	    	    
             	//search corpus for sentences containing the first number_of_rhymes rhymes at most
       	    	 int number_of_rhymes = 10;
       	    	 for(int i = 0; i<number_of_rhymes; i++)
       	   	    {   
       	   	     	if(i >= rhymes.size())//if number_of_rhymes rhymes were not found, break at size
       	         	{	
       	   	    		break;
       	   	    	}	
       	   	        callables.add(corpus.getSentence_Callable(Corpus.SentenceType.CONTAINS_RHYME, rhymes.get(i)));
       	   	        sen = corpus.getSentence(Corpus.SentenceType.CONTAINS_RHYME, rhymes.get(i));
       	   	
       	   	          // while(sen == null || sen.equals(Poem.get(Poem.size() - 1))|| countSyllables(sen) != 10){   
       	   	            //  sen = corpus.getSentence(Corpus.SentenceType.CONTAINS_RHYME, rhymes.get(i));
       	   	            //}	
       	    
       	   	    }   
       	    }
 	   	List<String> rhyming_sentences = executor.invokeAll_String(callables);
       	   	
       	   	//choose a sentence
           	int index = 0;
      	   	if(rhyming_sentences.size() == 1)
           	{
        		Poem.add(rhyming_sentences.get(0).replace("\n",""));
   	    	}
       	   	else
           	{	
   	    		index  = random.nextInt(rhyming_sentences.size() - 1);
   	    		System.out.println(index);
   	    		Poem.add(rhyming_sentences.get(index).replace("\n",""));
       	   	}
	
		}
	   }
		
		return Poem;
	}

	public int countSyllables(String word) {
	    int numSyllables = 0;
	    word = word.toLowerCase();
	    for (int i = 0; i < word.length(); i++) {
	        if (word.charAt(i) == '\"' || word.charAt(i) == '\'' || word.charAt(i) == '-' || word.charAt(i) == ',' || word.charAt(i) == ')' || word.charAt(i) == '(') {
	            word = word.substring(0,i)+word.substring(i+1, word.length());
	        }
	    }
	    boolean isEndVowel = false;
	    for (int j = 0; j < word.length(); j++) {
	        if (word.contains("a") || word.contains("e") || word.contains("i") || word.contains("o") || word.contains("u")) {
	            if (isVowel(word.charAt(j)) && !((word.charAt(j) == 'e') && (j == word.length()-1))) {
	                if (isEndVowel == false) {
	                	numSyllables++;
	                    isEndVowel = true;
	                }
	            } else {
	                isEndVowel = false;
	            }
	        } else {
	        	numSyllables++;
	            break;
	        }
	    }
	    
	    return numSyllables;
	}
	
	public boolean isVowel(char c) {
        if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
            return true;
        } else {
            return false;
        }
    }
	
	public void generateHaiku()
	{
		//relies on syllables
		//TODO
	}
}