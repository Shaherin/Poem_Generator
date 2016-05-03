package poem_generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;



import poem_generator.Poem_Generator;

/* -Class for generating poems
 * -Interacts with Poem_Generator, which has dependencies on Corpus and WordNet_Wrapper
 * -Poem_Generator, WordNet_Wrapper, and Stanford_Wrapper are implemented as non-static, 
 *  thread safe singletons with lazy initialization, which is convenient for a small project 
 *  like this, where a singleton would have few negative implications. 
 */
public class Poem_Generator_Demo
{
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException{	    
		
		/** Time */
		long begin = System.nanoTime(); //= System.nanoTime(); 
		
		/** Test Poem_Generator */
		Poem_Generator generator = Poem_Generator.getInstance();
		
		System.out.println("Generating...");
		ArrayList<String> poem = generator.generateFreeVerse(1, 8);
		//ArrayList<String> poem = generator.generateSonnet();
            
		    //print poem
		    for(int i = 0 ; i<poem.size(); i++)
		    {
			    System.out.println(poem.get(i));
		    }//*/
		
		//executor.Shutdown_Executor();
		
		/** Testing wordnet & stanford*/
		//WordNet_Wrapper wordnet = WordNet_Wrapper.getInstance();
		//ArrayList<String> hypernyms = wordnet.getHypernyms( wordnet.getWordStem("dog") );
		//ArrayList<String> holonyms = wordnet.getHolonyms( "canine" );
		
		//WordNet_Wrapper.printArrayList(holonyms);	
		
		//Stanford_Wrapper stanford = Stanford_Wrapper.getInstance();
		//String str = stanford.POS_Tagger("Hi I'm Steve");
		//System.out.println(str);//*/
		//stanford.tokenize("Hi I'm Steve");
		
		//Corpus corpus = Corpus.getInstance();
		//System.out.println(corpus.getSentence(Corpus.SentenceType.RANDOM));
		
		long end = System.nanoTime();
		long elapsed_time = (end - begin) / 1000000; //in ms
		
		//System.out.println("\nSingle Thread Time: "+elapsed_time_single);
		System.out.println("Elapsed Time: " +elapsed_time +" ms");
	    
	}
}

