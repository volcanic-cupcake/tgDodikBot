package tgAyeBot;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.pengrad.telegrambot.model.Contact;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;



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
	
	
	
	public void setBirthdaySession(Message message) {
		
		//array of text messages this session ignores
		List<String> ignore = new ArrayList<String>();
		ignore.add("/anonymous");
		
		long fromId = message.from().id();
		List<SetBirthdaySession> list = SessionStore.setBirthday();
		
		for (SetBirthdaySession setBirthday : list) {
			
			//in case there is an existing SetBirthdaySession of that user
			if (setBirthday.authorId() == fromId) {
				
				boolean contactExpected = setBirthday.contactId() == 0;
				boolean dateExpected = !contactExpected && setBirthday.birthdayDate() == null;
				boolean textExpected = !dateExpected && setBirthday.text() == null;
				
				String text = message.text();
				Contact contact = message.contact();
				User from = message.from();
				User forwardFrom = message.forwardFrom();
				long chatId = message.chat().id();
				
				//break, if it contains something from ignore list
				boolean fromIgnoreList = ignore.contains(text);
				if (fromIgnoreList) break;
				
				String respond = "";
				if (contactExpected) {
					boolean contactReceived = contact != null;
					boolean contactChecked = false;
					if (contactReceived) { //checks if received contact is not sent by the same person
						contactChecked = contact.userId() != from.id();
					}
					
					boolean forwardReceived = forwardFrom != null;
					boolean forwardChecked = false;
					if (forwardReceived) { //checks if forward message is not from the same person
						forwardChecked = forwardFrom.id() != from.id();
					}
					
					boolean contactApproved = contactReceived && contactChecked;
					boolean forwardApproved = forwardReceived && forwardChecked;
					boolean successful = contactApproved || forwardApproved;
					
					//changing session parameters
					if (successful) {
						if (contactApproved) {
							long contactId = contact.userId();
							
							String fullName = "";
							String firstName = contact.firstName();
							String lastName = contact.lastName();
							if (firstName != null) fullName += firstName;
							if (lastName != null) fullName += " " + lastName;
							
							setBirthday.setContactId(contactId);
							setBirthday.setContactName(fullName);
						}
						else if (forwardApproved) {
							long forwardFromId = forwardFrom.id();
							
							String fullName = "";
							String firstName = forwardFrom.firstName();
							String lastName = forwardFrom.lastName();
							if (firstName != null) fullName += firstName;
							if (lastName != null) fullName += " " + lastName;
							
							setBirthday.setContactId(forwardFromId);
							setBirthday.setContactName(fullName);
						}
						
					}
					
					//preparing a message respond
					if (successful) {
						respond = "Чудово! Тепер надійшли мені дату народження у форматі dd.MM\n"
								+ "\n"
								+ "Наприклад 05.12 або 22.04 або 03.06";
					}
					else {
						
						boolean selfCongratulate =
								(contactReceived && !contactChecked) ||
								(forwardReceived && !forwardChecked);
						if (selfCongratulate) {
							respond = "Самого себе вітати неможна, мене не обдуриш :D";
						}
						else {
							respond = "Виникла помилка :(\n"
									+ "\n"
									+ "Можливі причини:\n"
									+ "🔻я не можу отримати данні про цю людину через її налаштування конфіденційності\n"
									+ "🔻те, що ви прислали, не є контактом або пересланим від когось повідомленням\n";
						}
						
					}
				}
				else if (dateExpected) {
					boolean textReceived = text != null;
					if (textReceived) {
						ZonedDateTime date = Bot.uaDateTime(text);
						boolean successful = date != null;
						
						if (successful) {
							setBirthday.setBirthdayDate(date);
							
							respond = "Прекрасно! А тепер, час написати текст привітання :D\n"
									+ "\n"
									+ "Максимальна кількість символів: 1000";
						}
						else {
							respond = "Виникла помилка :(\n"
									+ "\n"
									+ "Можливі причини:"
									+ "🔻ви вказали дату у неправильному форматі"
									+ "🔻ви вказали ім'я своєї бабусі замість дати";
						}
					}
					else {
						respond = "Друже, краще надійшли мені текстове повідомлення "
								+ "з датою Дня Народження твоєго друга";
					}
				}
				else if (textExpected) {
					
				}
				
				SendMessage sendMessage = new SendMessage(chatId, respond);
				bot.execute(sendMessage);
				break;
			}
			
		}
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
