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
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
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
		
		/** Time */
		long begin = System.nanoTime(); //= System.nanoTime(); 
		
		/** Test Poem_Generator */
		Poem_Generator generator = Poem_Generator.getInstance();
		ArrayList<String> poem = generator.generateFreeVerse(2, 4, 0);

		    for(int i = 0 ; i<poem.size(); i++)
		    {
			    System.out.println(poem.get(i));
		    }//*/
		
		//executor.Shutdown_Executor();
		
		long end = System.nanoTime();
		long elapsed_time = (end - begin) / 1000000; //in ms
		
		//System.out.println("\nSingle Thread Time: "+elapsed_time_single);
		System.out.println("Elapsed Time: " +elapsed_time);
	    
	}
}

