package tgAyeBot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BotChat {
	
	private long id;
	private List<Long> members;
	
	public BotChat(long id, List<Long> members) {
		this.id = id;
		this.members = members;
	}
	
	public long id() {
		return this.id;
	}
	public List<Long> members() {
		return this.members;
	}
	
	public static List<BotChat> readChats() throws FileNotFoundException {
		List<BotChat> chats = new ArrayList<BotChat>();
		List<String> lines = TextFile.readLines(Resource.chats.path);
		
		List<Long> chatMembers = new ArrayList<Long>();
		long chatId = Long.parseLong( lines.get(0) );
		lines.remove(0);
		
		for (String line : lines) {
			boolean isChatId = ! line.startsWith("\t");
			
			if (isChatId) {
				BotChat chat = new BotChat(chatId, chatMembers);
				chats.add(chat);
				
				chatId = Long.parseLong(line);
				chatMembers.clear();
			}
			else {
				String memberLine = line;
				memberLine = memberLine.substring(1);
				chatMembers.add( Long.parseLong(memberLine) );
			}
		}
		
		return chats;
	}
	
	public static void writeChats(List<BotChat> chats) throws IOException {
		List<String> lines = new ArrayList<String>();
		for (BotChat chat : chats) {
			lines.add( Long.toString(chat.id) );
			for (long member : chat.members) {
				lines.add("\t" + Long.toString(member) );
			}
		}
		
		TextFile.writeLines(Resource.chats.path, lines, false);
	}
}