package poem_generator;

import java.util.Collection;
import java.util.Properties;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

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
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	}
	
	//private instance
	private static final Stanford_Wrapper stanfordNLP = new Stanford_Wrapper();
	
	//return instance
	public static Stanford_Wrapper getInstance()
	{
		return stanfordNLP;
	}
	
  /** Data Fields */
	private static StanfordCoreNLP pipeline;
	
  /** Stanford Functions */
	public static void getRelationTriples(String line){
		
	    // Annotate an example document.
	    //Annotation doc = new Annotation("Obama was born in Hawaii. He is our president.");
	    Annotation doc = new Annotation(line);
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
	
  /** Print Functions */
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
