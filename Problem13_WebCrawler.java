import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

class Problem13_WebCrawler {
	public static int level;
	public static HashMap<String, Integer> allURLonPage = new HashMap<>();

	public static void writeFile(HashMap<String, Integer> linksToWrite) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(
					"./crawled-urls.txt"));

			for (String lineToWrite : linksToWrite.keySet()) {
				// if you want only URLs in the file change out (commented line)
				// with
				// another one.
				out.write(lineToWrite + "\t level->"
						+ linksToWrite.get(lineToWrite) + "\n");
				// out.write(lineToWrite+"\n");
			}
			out.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error");
		} catch (Exception e) {
			System.out.println("Error: " + e);
		} finally {
			System.out.println("Writting to file...done");
		}
		return;
	}

	public static boolean validURL(String checkURL) {

		Pattern p = Pattern
				.compile("(https:\\/\\/|http:\\/\\/|ftp:\\/\\/|www\\.)[\\w-]+[.][\\w-]+");
		Matcher m = p.matcher(checkURL);

		if (m.find()) {
			return true;
		} else {
			return false;
		}
	}

	public static ArrayList<String> extraxtURL(String startURL, Integer level) {
		ArrayList<String> newList = new ArrayList<String>();
		try {
			Parser htmlParser = new Parser(startURL);
			NodeList tagNodeList = htmlParser
					.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));
			for (int j = 0; j < tagNodeList.size(); j++) {
				Tag anchorTag = (Tag) tagNodeList.elementAt(j);
				if (anchorTag.getTagName().equals("A")
						|| anchorTag.getTagName().equals("a")) {
					LinkTag link = (LinkTag) anchorTag;
					String ahrefString = link.extractLink().toString();
					if (validURL(ahrefString)
							&& !allURLonPage.containsKey(ahrefString)) {
						newList.add(ahrefString);
						allURLonPage.put(ahrefString, level);
					}
				}
			}
		} catch (ParserException pe) {
			// pe.printStackTrace();;
		}

		return newList;
	}

	public static void search(int currentLevel, ArrayList<String> currnetURL) {
		if (currentLevel >= level || currnetURL.size() == 0) {// currnetURL.isEmpty()
			return;
		}
		for (String string : currnetURL) {
			ArrayList<String> levelInside = extraxtURL(string, currentLevel + 1);
			search(currentLevel + 1, levelInside);
		}
		return;
	}

	public static void main(String[] args) throws ParserException {
		Scanner input = new Scanner(System.in);
		System.out.print("Input start URL:");
		String startURL = input.nextLine();
		System.out
				.print("Input deep of search(current level:0 - level higher then 2 is very slow):");
		level = input.nextInt();
		ArrayList<String> startURLLevel0 = new ArrayList<>(
				Arrays.asList(startURL));
		search(0, startURLLevel0);
		writeFile(allURLonPage);
	}
}