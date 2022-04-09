package tgAyeBot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pengrad.telegrambot.model.ChatMember;

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

		if ( lines.isEmpty() ) return chats;
		
		long chatId = 0;
		List<Long> chatMembers = new ArrayList<Long>();
		boolean nextChatId = true;
		for (String line : lines) {
			if (nextChatId) {
				chatId = Long.parseLong(line);
				nextChatId = false;
			}
			else if ( line.contentEquals("__________") ) {
				List<Long> members = new ArrayList<Long>();
				for (long id : chatMembers) {
					members.add(id);
				}
				
				BotChat newChat = new BotChat(chatId, members);
				chats.add(newChat);
				
				chatMembers.clear();
				nextChatId = true;
			}
			else {
				chatMembers.add( Long.parseLong(line) );
			}
		}

		return chats;
	}
	
	public static void writeChats(List<BotChat> chats) throws IOException {
		List<String> lines = new ArrayList<String>();
		for (BotChat chat : chats) {
			lines.add( Long.toString(chat.id) );
			for (long member : chat.members) {
				lines.add( Long.toString(member) );
			}
			lines.add("__________");
		}
		
		TextFile.writeLines(Resource.chats.path, lines, false);
	}
	
	public boolean contains(long userId) {
		if (members().contains(userId)) return true;
		else return false;
	}
}