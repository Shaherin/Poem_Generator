package poem_generator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;

import poem_generator.Poem_Generator;
import poem_generator.Corpus;
import poem_generator.WordNet_Wrapper;

/* -Class for generating poems
 * -Interacts with Poem_Generator, which has dependencies on Corpus and WordNet_Wrapper
 * -Poem_Generator, WordNet_Wrapper, and Stanford_Wrapper are implemented as non-static, 
 *  thread safe singletons with lazy initialization, which is convenient for a small project 
 *  like this, where a singleton would have few negative implications. 
 */
public class Poem_Generator_Demo
{
	/* -Currently just a test program for the wrapper class
	 */
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException{
		/** Test Poem_Generator*/
		
		/** Test corpus */
		//Corpus corpus = Corpus.getInstance();
		
		//String sentence = corpus.getSentence(Corpus.SentenceType.RANDOM);
	    //System.out.println("Sentence: " +sentence);
		
		//Initialize word tools
	    //WordNet_Wrapper dict = WordNet_Wrapper.getInstance();
	    //dict.loadIntoMemory();
	    
		//String word = "dog";
		//String wordStem = dict.getWordStem(word, false);
	    
	    //long begin_single = System.nanoTime(); 
		
	    //for(int i = 0; i<3 ;i++)
	    //{
	    	//WordNet_Wrapper.printRhymingWords( dict.getRhymingWords( dict.getWordStem("dog") ) );
	    //}
	    //WordNet_Wrapper.printSynonyms( dict.getSynonyms( dict.getWordStem("dog") ) );
	    //WordNet_Wrapper.printSynonyms( dict.getSynonyms("human") );
	    //WordNet_Wrapper.printSynonyms( dict.getSynonyms("fish") );
	    
	    //long end_single = System.nanoTime();
		//long elapsed_time_single = (end_single - begin_single) / 1000000; //in ms
		
		//System.out.println("Single Thread Time: "+elapsed_time_single);
	    
	    //WordNet_Wrapper.printHypernyms( dict.getHypernyms(dict.getWordStem("dog")));
	    
	    //dict.printRhymingWords( dict.getRhymingWords(wordStem));    
	    
	    //dict.printHypernyms( dict.getHypernyms_ISynsetID(wordStem) );
	   
	    //dict.getRelationTriples("Obama was born in Hawaii. He is our president.");
	    
		
		/** Test Stanford Wrapper */
		
		/** Time */
		long begin = System.nanoTime(); //= System.nanoTime(); 
		
		/** Testing Executors */		
		//list of callables
		//List<WordNet_Thread> callables = Arrays.asList(thread1, thread2, thread3);
		
		//ExecutorService executor = Executors.newCachedThreadPool();
		//System.out.println("Poem generator result: " +generator.testCorpus("away"));

		//generator.Shutdown_Executor();
		
		//Poem_Generator generator = Poem_Generator.getInstance();
		//Executor_Wrapper executor = new Executor_Wrapper();
		//List<String> results = executor.invokeAll(generator.testCorpus("away"));
		
		//executor.Shutdown_Executor();
		
		//System.out.println(results);
		Poem_Generator generator = Poem_Generator.getInstance();
		ArrayList<String> poem = generator.generateFreeVerse(1, 4, 0);

		for(int i = 0 ; i<poem.size(); i++)
		{
			System.out.println(poem.get(i));
		}//*/
		
		//Corpus corpus = Corpus.getInstance();
		//WordNet_Wrapper wordnet = WordNet_Wrapper.getInstance();
		//System.out.println(corpus.getSentence(Corpus.SentenceType.RANDOM));
		//wordnet.printRhymingWords( wordnet.getRhymingWords("dove"));
	    //System.out.println(corpus.getSentence(Corpus.SentenceType.CONTAINS_RHYME, "dove"));
		
		Executor_Wrapper.Shutdown_Executor();
		
		long end = System.nanoTime();
		long elapsed_time = (end - begin) / 1000000; //in ms
		
		//System.out.println("\nSingle Thread Time: "+elapsed_time_single);
		System.out.println("Elapsed Time: " +elapsed_time);
	}
}

