package tgAyeBot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Joke implements JokeInterface {
	private static String newLineChar = "FIfWdtkdtU";
	
	private long authorId;
	private String authorName;
	private String text;
	
	Joke(long authorId, String authorName, String text) {
		setAuthorId(authorId);
		setAuthorName(authorName);
		setText(text);
	}
	
	@Override
	public long authorId() {
		return this.authorId;
	}
	@Override
	public String authorName() {
		return this.authorName;
	}
	@Override
	public String text() {
		return this.text;
	}
	
	@Override
	public void setAuthorId(long authorId) {
		this.authorId = authorId;
	}
	@Override
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	@Override
	public void setText(String text) {
		this.text = text;
	}
	
	public static List<Joke> readJokes() throws FileNotFoundException {
		List<Joke> jokes = new ArrayList<Joke>();
		List<String> lines = TextFile.readLines(Resource.jokes.path);
		
		String authorIdLine;
		String authorNameLine;
		String textLine;
		
		long authorId = 0;
		String authorName = null;
		String text = null;
		
		int counter = 0;
		for (String line : lines) {
			switch (counter) {
			case 0:
				authorIdLine = line;
				authorId = Long.parseLong(authorIdLine);
				counter++;
				break;
			case 1:
				authorNameLine = line;
				authorName = authorNameLine;
				counter++;
				break;
			case 2:
				textLine = line;
				text = textLine.replace(newLineChar, "\n");
				
				Joke joke = new Joke(authorId, authorName, text);
				jokes.add(joke);
				
				counter = 0;
				break;
			}
		}
		return jokes;
	}
	
	public static void writeJokes(List<Joke> jokes) throws IOException {
		List<String> lines = new ArrayList<String>();
		String authorIdLine;
		String authorNameLine;
		String textLine;
		
		for (Joke joke : jokes) {
			authorIdLine = Long.toString( joke.authorId() );
			authorNameLine = joke.authorName();
			textLine = joke.text().replace("\n", newLineChar);
			
			lines.add(authorIdLine);
			lines.add(authorNameLine);
			lines.add(textLine);
		}
		
		String filePath = Resource.jokes.path;
		TextFile.writeLines(filePath, lines, false);
	}
	
	public static void addJoke(Joke joke) throws IOException {
		List<Joke> jokes = readJokes();
		jokes.add(joke);
		writeJokes(jokes);
	}
}
