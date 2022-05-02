package tgAyeBot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Insult implements InsultInterface {
	private static String newLineChar = "FIfWdtkdtU";
	
	private long authorId;
	private String authorName;
	private String text;
	
	Insult(long authorId, String authorName, String text) {
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
	
	public static List<Insult> readInsults() throws FileNotFoundException {
		List<Insult> insults = new ArrayList<Insult>();
		List<String> lines = TextFile.readLines(Resource.insults.path);
		
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
				
				Insult insult = new Insult(authorId, authorName, text);
				insults.add(insult);
				
				counter = 0;
				break;
			}
		}
		return insults;
	}
	
	public static void writeInsults(List<Insult> insults) throws IOException {
		List<String> lines = new ArrayList<String>();
		String authorIdLine;
		String authorNameLine;
		String textLine;
		
		for (Insult insult : insults) {
			authorIdLine = Long.toString( insult.authorId() );
			authorNameLine = insult.authorName();
			textLine = insult.text().replace("\n", newLineChar);
			
			lines.add(authorIdLine);
			lines.add(authorNameLine);
			lines.add(textLine);
		}
		
		String filePath = Resource.insults.path;
		TextFile.writeLines(filePath, lines, false);
	}
	
	public static void addInsult(Insult insult) throws IOException {
		List<Insult> insults = readInsults();
		insults.add(insult);
		writeInsults(insults);
	}
}
