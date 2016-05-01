package poem_generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.morph.WordnetStemmer;

/* -Uses the JWI library and WordNet to provide functions relevant to 
 *  poem generation
 * -Uses rhymebrain.com to determine set of rhyming words
 */
public class WordNet_Wrapper{
  /** Singleton Functions */
	private WordNet_Wrapper() 
	{
		try
		{
			file = new File(path);
			
			dict = new RAMDictionary(file, ILoadPolicy.NO_LOAD);
			dict.open();
		}
		catch(IOException ex)
		{
            //exception safe as long as wordnet database is inside project folder
		}
	};
	
	//private instance
	private static WordNet_Wrapper wordNet;
	
	//get instance
	public static synchronized WordNet_Wrapper getInstance()
	{   
		//synchronized method ensures that no 2 threads enter this if block concurrently
		if(wordNet == null)
		{
		    wordNet = new WordNet_Wrapper();
		} 
		return wordNet;
	}
    
  /** Data Fields */	
    private static final String path = "WordNet3.1-Dict";
    
    //JWI dictionary object
    private IRAMDictionary dict;
    private File file;
 
  /** WordNet JWI Functions */
    /* -Loading into memory decreases execution time, however
     *  time taken to load into memory is not insignificant
     */
    public void loadIntoMemory()
    {
    	try 
    	{
			dict.load(true);
		}
    	catch (InterruptedException ex) 
    	{
			System.out.println("Dictionarty loading failed");
			ex.printStackTrace();
		}
    }
    
    //returns an array containing word ID, lemma, and gloss(definition)
	public String[] getDefinition(String word) throws IOException
	{	
		 // look up first sense of the word
		 IIndexWord idxWord = dict.getIndexWord (word, POS. NOUN );
		 IWordID wordID = idxWord.getWordIDs().get(0) ;
		 String s_wordID = "" + wordID;
		 IWord iword = dict.getWord ( wordID );
		 
		 String[] definition = { s_wordID, iword.getLemma(), iword.getSynset().getGloss()};
		 
		 return definition;
	}
    
	public String getWordStem(String word)
	{
		return getWordStem(word, false);
	}
	
	//get word stem
	public String getWordStem(String word, boolean printStem)
	{
		 WordnetStemmer stemmer = new WordnetStemmer(dict);
		 List<String> stems = stemmer.findStems(word, POS.NOUN);
			 
		 if(printStem)
		 {
			 System.out.println("Stems: ");
			 for(int i = 0; i<stems.size(); i++)
			 {
			 	System.out.println(stems.get(i));
			 }
		 }
			 
		 return stems.get(0);
	}
			
	//get synonyms
	public ArrayList<String> getSynonyms(String word)
	{
		ArrayList<String> synonyms = new ArrayList<String>();
		
		ISynset synset = getSynonyms_ISynset(word);
		for( IWord w : synset.getWords() )
	        synonyms.add( w.getLemma() );
		
		return synonyms;
	}
	
	public ISynset getSynonyms_ISynset(String word)
	{
		// look up first sense of the word 
		IIndexWord idxWord = dict.getIndexWord(word, POS.NOUN );
		IWordID wordID = idxWord.getWordIDs().get(0) ; // 1st meaning
		IWord iword = dict.getWord( wordID );
		ISynset synset = iword.getSynset();
			
		return synset;
	}
	
	//get hypernyms (broad category eg. hypernym of dog is canine)
	public ArrayList<String> getHypernyms(String word)
	{
		 List <ISynsetID> hypernyms = getHypernyms_ISynsetID(word);
		
		 List <IWord > words;
		 ArrayList<String> s_hypernyms = new ArrayList<String>();
			for( ISynsetID sid : hypernyms )
			{
			    words = dict.getSynset(sid).getWords();
						     
				for( Iterator <IWord > i = words.iterator(); i.hasNext() ; )
				{
				    s_hypernyms.add( i.next().getLemma() );
				}
		    }
		 
		 return s_hypernyms;
		 
	}
	
	public List<ISynsetID> getHypernyms_ISynsetID(String word)
	{
		// get the synset
		IIndexWord idxWord = dict.getIndexWord(word, POS.NOUN );
		IWordID wordID = idxWord.getWordIDs().get(0) ; // 1st meaning
		IWord iword = dict.getWord( wordID );
		ISynset synset = iword.getSynset();
		
	    // get the hypernyms
		List <ISynsetID> hypernyms = synset.getRelatedSynsets(Pointer.HYPERNYM);
				 
		return hypernyms;
	}
	
	//get hyponyms - opposite of hypernym (specific category eg. hyponym of canine is dog)
	public ArrayList<String> getHyponyms(String word)
	{
		 List <ISynsetID> hyponyms = getHypernyms_ISynsetID(word);
		
		 List <IWord > words;
		 ArrayList<String> s_hyponyms = new ArrayList<String>();
			for( ISynsetID sid : hyponyms )
			{
			    words = dict.getSynset(sid).getWords();
						     
				for( Iterator <IWord > i = words.iterator(); i.hasNext() ; )
				{
					s_hyponyms.add( i.next().getLemma() );
				}
		    }
		 
		 return s_hyponyms;
		 
	}
	
	public List<ISynsetID> getHyponyms_ISynsetID(String word)
	{
		// get the synset
		IIndexWord idxWord = dict.getIndexWord(word, POS.NOUN );
		IWordID wordID = idxWord.getWordIDs().get(0) ; // 1st meaning
		IWord iword = dict.getWord( wordID );
		ISynset synset = iword.getSynset();
		
	    // get the hyponyms
		List <ISynsetID> hyponyms = synset.getRelatedSynsets(Pointer.HYPONYM);
				 
		return hyponyms;
	}	
	
	//get holonyms - 'X' is a holonym of 'Y' if Y's are parts/members of X's
	//returns X ie. the broader context
	public ArrayList<String> getHolonyms(String word)
	{
		 List <ISynsetID> holonyms = getHypernyms_ISynsetID(word);
		
		 List <IWord > words;
		 ArrayList<String> s_holonyms = new ArrayList<String>();
			for( ISynsetID sid : holonyms )
			{
			    words = dict.getSynset(sid).getWords();
						     
				for( Iterator <IWord > i = words.iterator(); i.hasNext() ; )
				{
					s_holonyms.add( i.next().getLemma() );
				}
		    }
		 
		 return s_holonyms;
		 
	}
	
	public List<ISynsetID> getHolonyms_ISynsetID(String word)
	{
		// get the synset
		IIndexWord idxWord = dict.getIndexWord(word, POS.NOUN );
		IWordID wordID = idxWord.getWordIDs().get(0) ; // 1st meaning
		IWord iword = dict.getWord( wordID );
		ISynset synset = iword.getSynset();
		
	    // get the holonyms
		List <ISynsetID> holonyms = synset.getRelatedSynsets(Pointer.HOLONYM_PART);
				 
		return holonyms;
	}
	
	//get meronyms - 'X' is a meronym of 'Y' if X's are parts/members of Y's
	//returns X ie. the smaller context
	public ArrayList<String> getMeronyms(String word)
	{
		 List <ISynsetID> meronyms = getHypernyms_ISynsetID(word);
			
		 List <IWord > words;
		 ArrayList<String> s_meronyms = new ArrayList<String>();
			for( ISynsetID sid : meronyms )
			{
			    words = dict.getSynset(sid).getWords();
							     
				for( Iterator <IWord > i = words.iterator(); i.hasNext() ; )
				{
					s_meronyms.add( i.next().getLemma() );
				}
		    }
			 
		 return s_meronyms;
			 
	}
		
	public List<ISynsetID> getMeronyms_ISynsetID(String word)
	{
		// get the synset
		IIndexWord idxWord = dict.getIndexWord(word, POS.NOUN );
		IWordID wordID = idxWord.getWordIDs().get(0) ; // 1st meaning
		IWord iword = dict.getWord( wordID );
		ISynset synset = iword.getSynset();
			
	    // get the meronyms
		List <ISynsetID> meronyms = synset.getRelatedSynsets(Pointer.MERONYM_PART);
					 
		return meronyms;
	}

	//returns arraylist of rhyming words using rhymebrain.com
	public ArrayList<String> getRhymingWords(String word) {	
	    // Make a URL to the web page
	    URL url;
		try {
			url = new URL("http://rhymebrain.com/talk?function=getRhymes&word=" + word);
		} catch (MalformedURLException e) {
			return null;
		}

	    // Get the input stream through URL Connection
	    URLConnection con;
		try 
		{   
			con = url.openConnection();
		} 
		catch (IOException e) 
		{   
			System.out.println("URL exception connecting to rhymebrain.com. Retrying...");
			return null;
		}
	    
		InputStream is;
		try 
		{
			is = con.getInputStream();
		} 
		catch (IOException e) 
		{   
			System.out.println("Input stream exception connecting to rhymebrain.com. Retrying...");
			return null;
		}
		
	    BufferedReader br = new BufferedReader(new InputStreamReader(is));

	    //arraylist to store words
	    ArrayList<String> rhyming_words = new ArrayList<>();
	        
	    //pattern + matcher
	    Pattern pattern = Pattern.compile( "(\"word\":\")(\\b\\w+\\b)(\")" );
	    Matcher matcher;
	        
	    /* -Each line is read in format: {"word":"flow","freq":24,"score":300,"flags":"bc","syllables":"1"}
         * -Maybe select words based on freqency/score
	     * 
	     * */  
	     String line = new String();
	     // read each line
	     try 
	     {
			while ((line = br.readLine()) != null) {
			     matcher = pattern.matcher(line);
			        if(matcher.find())
			            rhyming_words.add(matcher.group(2)); //add second group to the arraylist
			 }
		} 
	     catch (IOException e) 
	     {
	    	//return result as far as exception
			return rhyming_words;
		}      
	     return rhyming_words;
    }
    
  /** Concurrency functions */
	public Callable<String[]> getDefinition_Callable(String word)
	{	
		return () -> { return getDefinition(word); };
	};
	
	public Callable<String> getWordStem_Callable(String word)
	{
		return () -> { return getWordStem(word); };
	}
	
	public Callable<ArrayList<String>> getSynonyms_Callable(String word)
	{
		return () -> { return getSynonyms(word); };
	}
	
	public Callable<ArrayList<String>> getHypernyms_Callable(String word)
	{
		return () -> { return getHypernyms(word); };
	}
	
	public Callable<ArrayList<String>> getHyponyms_Callable(String word)
	{
		return () -> { return getHyponyms(word); };
	}
	
	public Callable<ArrayList<String>> getHolonyms_Callable(String word)
	{
		return () -> { return getHolonyms(word); };
	}
	
	public Callable<ArrayList<String>> getMeronyms_Callable(String word)
	{
		return () -> { return getMeronyms(word); };
	}
	
	public Callable<ArrayList<String>> getRhymingWords_Callable(String word)
	{
		return () -> { return getRhymingWords(word); };
	}
	
  /** Print Functions */
	public static void printDefinition(String[] definition)
	{
		 System.out.println("Definition:");
		 System.out.println( "Id = " + definition[0] );
		 System.out.println( " Lemma = " + definition[1] );
		 System.out.println( " Gloss = " + definition[2] );
		 
		 System.out.println("");
	}
	
	public static void printArrayList(ArrayList<String> list)
	{
		for(int i = 0 ; i<list.size() ; i++)
		{
			System.out.println( list.get(i) );
		}
	}
	
	public static void printSynonyms(ISynset synset)
	{
		System.out.println("Synonyms:");
			
		// iterate over words associated with the synset
		for( IWord w : synset.getWords() )
	        System.out.println( w.getLemma() );
					
		System.out.println("");
	}
	
	public static void printSynonyms(ArrayList<String> synonyms)
	{   
		System.out.println("Synonyms:");
		
		for(int i = 0 ; i<synonyms.size() ; i++)
		{
			System.out.println( synonyms.get(i) );
		}
		
		System.out.println("");
	}
		
	public static void printRhymingWords(ArrayList<String> rhyming_words)
	{
		System.out.println("Rhyming words:");
			
		for(int i = 0 ; i < rhyming_words.size() ; i++)
		{
		    System.out.format("%1$-15s", rhyming_words.get(i));
		    //System.out.format("%15s", rhyming_words.get(i));
		    	
		    if((i+1)%8 == 0)
		    	System.out.println("");
	    }
		
		System.out.println("");
		System.out.println("");
    }
	
	public static void printHypernyms(ArrayList<String> hypernyms)
	{
		System.out.println("Hypernyms:");
		
		for(int i = 0 ; i<hypernyms.size(); i++)
		{
			System.out.println(hypernyms.get(i));
		}
		
		System.out.println("");
	}
	
	public void printHypernyms(List<ISynsetID> hypernyms)
	{   
		System.out.println("Hypernyms:");
		
		//print out each hypernym's id and synonyms
		List <IWord > words;
		for( ISynsetID sid : hypernyms )
		{
			words = dict.getSynset(sid).getWords();
			System.out.print(sid + " {");
							     
		    for( Iterator <IWord > i = words.iterator(); i.hasNext() ; )
			{
				System.out.print( i.next().getLemma() );
					    
				if( i.hasNext () )
					System.out.print(", ");
			}
							     
		    System.out.println("}");
	    }
	}

}
