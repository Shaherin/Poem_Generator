package poem_generator;

import java.util.*;
import java.util.regex.Pattern;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
public class EndWords {
	
	public static ArrayList<String> getBagOfWords(String word, ArrayList<String> synonyms) throws IOException{
		String allText = new String(Files.readAllBytes(Paths.get("C:\\Users\\Jenade Moodley\\Documents\\BagOfWords\\Poem Corpus v1.txt")));

		ArrayList<String> bag = new ArrayList<>();
		ArrayList<String> temp = new ArrayList<>();
		
		String[] words = allText.split("[ \n\t\r.,;:!?(){}]");
		
		for(int i=0; i< words.length; i++){
			if(!words[i].equals("")){
				temp.add(words[i]);
			}
		}
		
		bag.add(word);
		for(int i=0; i<synonyms.size();i++)
			bag.add(synonyms.get(i));
	
		
		for(int i=0; i<temp.size(); i++){
			if(word.equalsIgnoreCase(temp.get(i)))
			{
				if(i > 1  && !bag.contains(temp.get(i-2)))
					bag.add(temp.get(i-2));
				if(i > 0  && !bag.contains(temp.get(i-1)))
					bag.add(temp.get(i-1));
				if(i < temp.size()-1  && !bag.contains(temp.get(i+1)))
					bag.add(temp.get(i+1));
				if(i < temp.size()-2  && !bag.contains(temp.get(i+2)))
					bag.add(temp.get(i+2));
			}
		}
		
		for(int j=0; j< synonyms.size(); j++){
			
		for(int i=0; i<temp.size(); i++){
			if(synonyms.get(j).equalsIgnoreCase(temp.get(i)))
			{
				if(i > 1  && !bag.contains(temp.get(i-2)))
					bag.add(temp.get(i-2));
				if(i > 0  && !bag.contains(temp.get(i-1)))
					bag.add(temp.get(i-1));
				if(i < temp.size()-1  && !bag.contains(temp.get(i+1)))
					bag.add(temp.get(i+1));
				if(i < temp.size()-2  && !bag.contains(temp.get(i+2)))
					bag.add(temp.get(i+2));
			}
		}
		}
		bag.remove("#");
		
		//***Add stanford code to determine determinants here
		
		/* String someString; //string to have determiners removed
		   String taggedString = stanfordTools.POS_Tagger(someString); //return a tagged string
		   
		   Pattern pattern = pattern.compile("\\b\\w+_DET\\b");
		   Matcher matcher = pattern.matcher(taggedString);	   
		   
		   ArrayList<String> determiners = new ArrayList<String>(); //list of determiners obtained from someString
		   
		   //identify determiners
		   while(matcher.find())
		   {
		       determiners.add(matcher.group().replace("_DET", ""););
		   }
		   
		   //for each entry in determiners remove from someString
		   for(String s : determiners)
		   {
		       someString.replace(s, "");
		   }
		   
		*/
		
		
				 
		return bag;
	}
	
	public static ArrayList<Verse> getVersesWithBagOfWords(ArrayList <String> t, ArrayList<Verse> end) throws IOException{
		ArrayList<Verse> set = new ArrayList<>();
		 for(int k=0; k<end.size(); k++){
				String temp = end.get(k).getVerse();
				int song = end.get(k).getSong();
				for(int j=0; j<t.size(); j++){
					String c1 = t.get(j).toUpperCase();
					String c2 = t.get(j).toLowerCase();
					String c3 = "["+c1.charAt(0)+"/"+c2.charAt(0)+"]";
					String sub = t.get(j).substring(1, t.get(j).length());
					String p = "\\W"+c3+sub+"\\W";

					Pattern pattern2 = Pattern.compile(p);
					Matcher matcher2 = pattern2.matcher(temp);
					boolean found = matcher2.find();
				if(!temp.equals("*") && found){
				//	System.out.println(found + " "+ temp + " " +p);
					Verse a = new Verse(song, temp);
				set.add(a);
				break;
					}
				}
				}
	
		return set;
	}
	
	public static ArrayList<Verse> getEndWords(ArrayList<String> end) throws FileNotFoundException{
		ArrayList<Verse> set = new ArrayList<>();
		int song = 0;
		Pattern ex = Pattern.compile("^!");
		Scanner sc = new Scanner(new File("C:\\Users\\Jenade Moodley\\Documents\\BagOfWords\\Poem Corpus v1.txt"));
		
			while(sc.hasNextLine()){
				String temp = sc.nextLine();
				if(temp.equals("#"))
					 song++;
			else{
				for(int i=0; i<end.size(); i++){
					String c1 = end.get(i).toUpperCase();
					String c2 = end.get(i).toLowerCase();
					String t = "["+c1.charAt(0)+"/"+c2.charAt(0)+"]";
					String sub = end.get(i).substring(1, end.get(i).length());
					String p = t+sub+"\\S$|"+t+sub+"$";
					//System.out.println(p);
				
				Pattern pattern = Pattern.compile(p);
				Matcher match = pattern.matcher(temp);
				Matcher exMatcher= ex.matcher(temp);
				boolean found = match.find();
				if(found && !exMatcher.find()){
					//System.out.println(found);
					set.add(new Verse(song,temp));
					break;
				}
				}
			}
			}
			
			sc.close();
		return set;
	}
public static void main(String [] args) throws IOException{
	ArrayList<String> end = new ArrayList<>();
	end.add("Love");
	end.add("Above");
	end.add("Grove");
	end.add("Shove");
	end.add("Dove");
	ArrayList<String> synonyms = new ArrayList<>();
	synonyms.add("Like");
	synonyms.add("Affection");
	
	ArrayList <String> bag = getBagOfWords("Love", synonyms);
	ArrayList<Verse> set = getEndWords(end);
	ArrayList<Verse> set2 = getVersesWithBagOfWords(bag,set);
	System.out.println("Bag of Words");
	System.out.println(bag);
	System.out.println("End Words");
	System.out.println(set);

	System.out.println("Bag of Words added to End Words");
	System.out.println(set2);
	
	
}
}
