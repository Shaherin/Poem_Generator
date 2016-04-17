package poem_generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.morph.WordnetStemmer;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/* -Uses the JWI library and WordNet to provide functions relevant to 
 *  poem generation
 * -Uses rhymebrain.com to determine set of rhyming words 
 */
public class WordNet_Wrapper {
  /** Singleton Functions */
	private WordNet_Wrapper() 
	{
		try
		{
			File file = new File(path);
			
			dict = new Dictionary (file);
			dict.open();
		}
		catch(IOException ex)
		{
            //exception safe as long as wordnet database is inside project folder
		}
	};
	
	//private instance
	private static final WordNet_Wrapper wordNet = new WordNet_Wrapper();
	
	//get instance
	public static WordNet_Wrapper getInstance()
	{
		return wordNet;
	}
  
  /** Data Fields */	
    private static final String path = "WordNet3.1-Dict";
    
    //JWI dictionary object
    private static IDictionary dict; 
 
  /** Wordnet JWI Functions */	
    //get the definition of a word
	public static void getDefinition(String word) throws IOException{	
		 // look up first sense of the word
		 IIndexWord idxWord = dict.getIndexWord (word, POS. NOUN );
		 IWordID wordID = idxWord.getWordIDs().get(0) ;
		 IWord iword = dict.getWord ( wordID );
			 
		 System.out.println("Definition:");
		 System.out.println( "Id = " + wordID );
		 System.out.println( " Lemma = " + iword.getLemma() );
		 System.out.println( " Gloss = " + iword.getSynset().getGloss() );
		 System.out.println("");		 
	}

	//get word stem
	public static String getWordStem(String word, boolean printStems)
	{
		 WordnetStemmer stemmer = new WordnetStemmer(dict);
		 List<String> stems = stemmer.findStems(word, POS.NOUN);
			 
		 if(printStems)
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
	public static ISynset getSynonyms(String word){
		// look up first sense of the word 
		IIndexWord idxWord = dict.getIndexWord(word, POS. NOUN );
		IWordID wordID = idxWord.getWordIDs().get(0) ; // 1st meaning
		IWord iword = dict.getWord( wordID );
		ISynset synset = iword.getSynset();
			
		return synset;
	}
		
	public void getHypernyms (String word){
		 // get the synset
		 IIndexWord idxWord = dict.getIndexWord("dog", POS. NOUN );
		 IWordID wordID = idxWord.getWordIDs().get(0) ; // 1st meaning
		 IWord iword = dict.getWord( wordID );
		 ISynset synset = iword.getSynset();
			
		 // get the hypernyms
		 List < ISynsetID > hypernyms = synset.getRelatedSynsets(Pointer.HYPERNYM);
			
		 // print out each hypernyms id and synonyms
		 List <IWord > words ;
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
	}//*/
		
	public static void getRelationTriples(String line){
		// Create the Stanford CoreNLP pipeline
		Properties props = new Properties();
	    props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie");
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

	    // Annotate an example document.
	    Annotation doc = new Annotation("Obama was born in Hawaii. He is our president.");
	    //Annotation doc = new Annotation(line);
	    pipeline.annotate(doc);

	    // Loop over sentences in the document
	    for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
	    	//Get the OpenIE triples for the sentence
	    	Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
	    	
	    	for (RelationTriple triple : triples)
            {
	    		System.out.println(triple.confidence + "\t" +
	    					       triple.subjectLemmaGloss() + "\t" +
	    					       triple.relationLemmaGloss() + "\t" +
	    					       triple.objectLemmaGloss());
	    	}
        }
	    
	    //TODO
	    
	    //for a single sentence
	    //CoreMap sentence = doc.get(CoreAnnotations.SentencesAnnotation.class).get;
	    	
	    //RelationTriple triple = 
	}
		
	//returns arraylist of rhyming words using rhymebrain.com
	public static ArrayList<String> getRhymingWords(String word) throws IOException {	
	    // Make a URL to the web page
	    URL url = new URL("http://rhymebrain.com/talk?function=getRhymes&word=" + word);

	    // Get the input stream through URL Connection
	    URLConnection con = url.openConnection();
	    InputStream is = con.getInputStream();

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
	     String line = null;
	     // read each line
	     while ((line = br.readLine()) != null) {
	         matcher = pattern.matcher(line);
	            if(matcher.find())
	                rhyming_words.add(matcher.group(2)); //add second group to the arraylist
	     }      
	     return rhyming_words;
    }
	
  /** Print Functions */
	public static void printSynonyms(ISynset synset)
	{
		System.out.println("Synonyms:");
			
		// iterate over words associated with the synset
		for( IWord w : synset.getWords() )
	        System.out.println( w.getLemma() );
					
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
    }
		
	//prints a single RelationTriple
	public static void printRelationTriple(RelationTriple triple)
	{   
		//TODO
		
		// Print the triples
      	//for (RelationTriple triple : triples) {
    	  System.out.println(triple.confidence + "\t" +
        		triple.subjectLemmaGloss() + "\t" +
            	triple.relationLemmaGloss() + "\t" +
            	triple.objectLemmaGloss());
      	//}
	}
}
