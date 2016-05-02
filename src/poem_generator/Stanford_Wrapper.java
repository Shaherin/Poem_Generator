package poem_generator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/* -Provides relevant functions for poem generation related to the 
 *  Stanford CoreNLP library
 */
public class Stanford_Wrapper {
  /** Singleton Functions */    
	private Stanford_Wrapper()
	{
		// Create the Stanford CoreNLP pipeline
		Properties props = new Properties();
	    props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie");
	    pipeline = new StanfordCoreNLP(props);
	}
	
	//private instance
	private static final Stanford_Wrapper stanfordNLP = new Stanford_Wrapper();
	
	//return instance
	public static Stanford_Wrapper getInstance()
	{
		return stanfordNLP;
	}
	
  /** Data Fields */
	private StanfordCoreNLP pipeline;
	
  /** Stanford Functions */
	/* returns an array size 3 consisting of
	 * [0] = subject
	 * [1] = relation
	 * [2] = object
	 */
	public String[] getRelationTriple(String line)
	{
	    // Annotate an example document.
	    //Annotation doc = new Annotation("Obama was born in Hawaii. He is our president.");
	    Annotation doc = new Annotation(line);
	    pipeline.annotate(doc);
        
	    String[] relation_triple = null; 
	    
	    // Loop over sentences in the document
	    for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
	    	//Get the OpenIE triples for the sentence
	    	Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
	    	
	    	for (RelationTriple triple : triples)
            {
	    		/*System.out.println(triple.confidence + "\t" +
	    					       triple.subjectLemmaGloss() + "\t" +
	    					       triple.relationLemmaGloss() + "\t" +
	    					       triple.objectLemmaGloss());
	    					      
	    		System.out.println(triple.object);
	    		System.out.println(triple.objectGloss());*/
	    		
	    		/*System.out.println(triple.confidence + "\t" +
	    	            triple.subjectGloss() + "\t" +
	    	            triple.relationGloss() + "\t" +
	    	            triple.objectGloss());*/
	    		
	    		relation_triple = new String[3];
	    		
	    	    relation_triple[0] = triple.subjectGloss(); 
	    	    relation_triple[1] = triple.relationGloss();
	    	    relation_triple[2] = triple.objectGloss();
	    	}
        }
	   
	    return relation_triple;
	}
	
	//returns arraylist of tokens for a given string using PTB tokenization
	public ArrayList<String> tokenize(String line)
	{    
		  // By token
	      PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new StringReader(line),
	              new CoreLabelTokenFactory(), "");
	      while (ptbt.hasNext()) {
	        CoreLabel label = ptbt.next();
	        System.out.println(label);
	      }
		
	      return null;
	}
	
	public String POS_Tagger(String line)
	{   
		MaxentTagger tagger = new MaxentTagger("stanford-postagger-full-2015-12-09//models//english-caseless-left3words-distsim.tagger");
		
		String taggedString = tagger.tagString(line);
		//String [] words = taggedString.split(" ");
			
		return taggedString;
	}
	
  /** Multithreading functions */
	public Callable<String[]> getRelationTriple_Callable(String sentence)
	{
		return () -> { return getRelationTriple(sentence); };
	}
	
  /** Print Functions */
	//prints a single RelationTriple
		public void printRelationTriple(RelationTriple triple)
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
