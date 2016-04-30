package poem_generator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Executor_Wrapper {
	/** Constructor */
	public Executor_Wrapper()
	{
		executor = Executors.newCachedThreadPool();
	}
	
	/** Data fields*/
	private static ExecutorService executor;
	
	/** Functions */
	public List<String> invokeAll(List<Callable<ArrayList<String>>> callables)
	{    
		List<Future<ArrayList<String>>> futures = null;
		List<String> results = new ArrayList<String>();
				
		try {
			futures = executor.invokeAll(callables);
		} 
		catch (InterruptedException ex) 
		{   
			System.out.println("Thread interrupted");
			Thread.currentThread().interrupt();
			ex.printStackTrace();
		}
		
		for( Future<ArrayList<String>> future : futures)
		{
			try {
				results.addAll(future.get());
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		//returns single arraylist which is a concatenation of all the results
	    return results;	
	}
	
	public List<String> invokeAll_String(List<Callable<String>> callables)
	{    
		List<Future<String>> futures = null;
		List<String> results = new ArrayList<String>();
				
		try {
			futures = executor.invokeAll(callables);
		} 
		catch (InterruptedException ex) 
		{   
			System.out.println("Thread interrupted");
			Thread.currentThread().interrupt();
			ex.printStackTrace();
		}
		
		for( Future<String> future : futures)
		{
			try {
				if(future.get() != null)
				    results.add(future.get());
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	    return results;	
	}
	
	public <T> T submit(Callable<T> callable)
	{
		Future<T> future = executor.submit(callable);
		T result = null;
		
		try {
	        result = future.get();
		} 
		catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
		
	}
	
	//safe shutdown
	public static void Shutdown_Executor()
    {
    	/** Shutdown executor */
		System.out.println("\nShutting down executor");
		executor.shutdown(); //waits for running tasks to finish - does not accept new tasks
		try
		{	
			/* -Waits for tasks to complete, current thread to be interrupted, or specified time to
			 *  complete. Whichever comes first.
			 * -Then allows executor to shut down 
			 * -Long.MAX_VALUE allows threads to take as long as they need, since Long.MAX_VALUE
			 *  nanoseconds is roughly 292 years of wait time. 
			 */		
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); 
		}
		catch(InterruptedException ex) 
		{   
			//allows shutdown in event of task interruption - exception is caught and executor continues executing threads
			System.err.println("task interrupted"); 
		}
		//this block is unnecessary in our case
		finally
		{
		    if(!executor.isTerminated()) //isTerminated is true if all tasks have completed during shut down
		    {
		    	System.err.println("Cancel unfinished tasks"); //this should never happen in our case - even interrupted tasks count as terminated
		    }
		    executor.shutdownNow(); //more of a brute force shutdown
		    
		    System.out.println( executor.isShutdown() ? "Executor shutdown successful" 
                                                      : "Executor shutdown failed" );	    
		}
    }
}
