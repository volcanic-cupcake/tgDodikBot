package tgAyeBot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;



public class MessageHandler {
	private Bot bot;
	private List<Command> Private;
	private List<Command> group;
	
	public MessageHandler(Bot bot) {
		setBot(bot);
		setCommands( bot.commands() );
	}
	
	private void setBot(Bot newBot) {
		this.bot = newBot;
	}
	private void setCommands(Command[] commands) {
			
		List<Command> privateList = new ArrayList<Command>();
		List<Command> groupList = new ArrayList<Command>();

		for (Command command : commands) {
			switch (command.type()) {
			case PRIVATE:
				privateList.add(command);
				break;
			case GROUP:
				groupList.add(command);
				break;
			case PRIVATE_AND_GROUP:
				privateList.add(command);
				groupList.add(command);
				break;
			}
		}
		
		this.Private = privateList;
		this.group = groupList;
	}
	
	public void Private(Message message) {
		privateCommands(message);
	}
	
	public void group(Message message) {		
		groupCommands(message);
	}
	
	public void updateChatData(List<BotChat> chats, Message message) throws IOException {
		
		User[] joinedUsers = message.newChatMembers();
		User leftUser = message.leftChatMember();
		
		boolean isJoinedUsers = joinedUsers != null;
		boolean isLeftUser = leftUser != null;
		boolean isNormal = !isJoinedUsers && !isLeftUser;
		
		long chatId = message.chat().id();
		long fromId = message.from().id();
		
		if (isNormal) normalMessage(chats, chatId, fromId);
		else if (isJoinedUsers) joinedUsersMessage(chats, chatId, fromId, joinedUsers);
		else if (isLeftUser) leftUserMessage( chats, chatId, fromId, leftUser.id() );
	}
	
	
	private void normalMessage(List<BotChat> chats, long chatId, long fromId) throws IOException {
		//checks if a chat already exists
		boolean chatExists = false;
		for (BotChat chat : chats) {
			chatExists = chat.id() == chatId;
			if (chatExists) {
				boolean userExists = chat.members().contains(fromId);
				if ( !userExists ) {
					chat.members().add(fromId);
					BotChat.writeChats(chats);
				}
				break;
			}
		}
		
		//adds the chat to the chats list and the text database,
		//if it doesn't exist
		if ( !chatExists ) {
			List<Long> members = new ArrayList<Long>();
			members.add(fromId);
			
			BotChat newChat = new BotChat(chatId, members);
			chats.add(newChat);
			BotChat.writeChats(chats);
		}
	}
	
	private void joinedUsersMessage(List<BotChat> chats, long chatId, long fromId, User[] joinedUsers) throws IOException {
	
		//gathers all userIds in one list
		List<Long> userIds = new ArrayList<Long>();
		for (User joinedUser : joinedUsers) {
			userIds.add( joinedUser.id() );
		}
		if ( !userIds.contains(fromId) ) userIds.add(fromId);
		
		//checks if a chat already exists
		boolean chatExists = false;
		for (BotChat chat : chats) {
			chatExists = chat.id() == chatId;
			if (chatExists) {
				//adds new userIds to the chats list and the text database
				boolean containsNewUser = false;
				boolean changes = false; //whether users have been removed/added
				for (long userId : userIds) {
					containsNewUser = !chat.members().contains(userId);
					if (containsNewUser) {
						chat.members().add(userId);
						changes = true;
					}
				}
				if (changes) BotChat.writeChats(chats);
				break;
			}
		}
		
		//adds the chat to the chats list and the text database,
		//if it doesn't exist
		if ( !chatExists ) {
			BotChat newChat = new BotChat(chatId, userIds);
			chats.add(newChat);
			BotChat.writeChats(chats);
		}
	}
	
	private void leftUserMessage(List<BotChat> chats, long chatId, long fromId, long leftId) throws IOException {
		
		boolean kickerExists = kickerExists(fromId, leftId);
		
		//checks if a chat already exists
		boolean chatExists = false;
		for (BotChat chat : chats) {
			chatExists = chat.id() == chatId;
			if (chatExists) {
				
				boolean removeLeft = chat.members().contains(leftId);
				boolean addKicker =
						kickerExists &&
						!chat.members().contains(fromId);
				boolean changes = removeLeft || addKicker; //whether users have been removed/added
				if (removeLeft) chat.members().remove(leftId);
				if (addKicker) chat.members().add(fromId);
				
				if (changes) BotChat.writeChats(chats);
				break;
			}
		}
		
		//adds the chat to the chats list and the text database,
		//if it doesn't exist
		if ( !chatExists ) {
			List<Long> members = new ArrayList<Long>();
			if (kickerExists) members.add(fromId);
			
			BotChat newChat = new BotChat(chatId, members);
			chats.add(newChat);
			BotChat.writeChats(chats);
		}
	}
	
	//checks if there's someone who kicked the user or they left on their own
	private boolean kickerExists(long fromId, long leftId) {
		boolean exists;
		if (fromId != leftId) exists = true;
		else exists = false;
		
		return exists;
	}
	
	private void privateCommands(Message message) {
		String inputText = message.text();
		String text = inputText.strip();
		
		for (Command command : this.Private) {
			String commandText = command.command();
			if ( commandText.contentEquals(text) ) {
				command.execute(message);
				break;
			}
		}
	}
	private void groupCommands(Message message) {
		String inputText = message.text();
		String text = inputText.strip();
		
		for (Command command : this.group) {
			String commandText = command.command() + this.bot.username();
			if ( commandText.contentEquals(text) ) {
				command.execute(message);
				break;
			}
		}
	}
}
