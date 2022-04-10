package tgAyeBot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.pengrad.telegrambot.model.Chat.Type;
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
		String text = message.text();
		if (text.startsWith("/birthdayremove_")) {
			SessionStore.clear(message.from().id());
			birthdayRemove(message);
		}
		else if (text.startsWith("/birthdaytext_")) {
			SessionStore.clear(message.from().id());
			birthdayText(message);
		}
		else privateCommands(message);
	}
	
	public void group(Message message) {
		String text = message.text();
		if (text.startsWith("/birthdaytext_")) {
			SessionStore.clear(message.from().id());
			birthdayText(message);
		}
		else groupCommands(message);
	}
	
	public void setBirthdaySession(Message message) {
		
		//array of text messages this session ignores
		List<String> ignore = new ArrayList<String>();
		ignore.add("/setbirthday");
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
					boolean contactReceived = contact != null && contact.userId() != null;
					boolean contactChecked = false;
					if (contactReceived) { //checks if received contact is not sent by the same person
						contactChecked = !contact.userId().equals(from.id());
					}
					
					boolean forwardReceived = forwardFrom != null && forwardFrom.id() != null;
					boolean forwardChecked = false;
					if (forwardReceived) { //checks if forward message is not from the same person
						forwardChecked = !forwardFrom.id().equals(from.id());
					}
					
					boolean contactApproved = contactReceived && contactChecked;
					boolean forwardApproved = forwardReceived && forwardChecked;
					
					boolean birthdayExists = false;
					if (contactApproved) birthdayExists = birthdayExists(from.id(), contact.userId());
					else if (forwardApproved) birthdayExists = birthdayExists(from.id(), forwardFrom.id());
					
					boolean successful = (contactApproved || forwardApproved) && !birthdayExists;
					
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
						else if (birthdayExists) {
							respond = "Ви вже створили привітання для цієї людини!";
						}
						else {
							respond = "Виникла помилка\n"
									+ "Можливі причини:\n"
									+ "\n"
									+ "🔻я не можу отримати данні про цю людину через її налаштування конфіденційності\n"
									+ "\n"
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
									+ "Максимальна кількість символів: 3000";
						}
						else {
							respond = "Виникла помилка\n"
									+ "Можливі причини:\n"
									+ "\n"
									+ "🔻ви вказали дату у неправильному форматі\n"
									+ "\n"
									+ "🔻ви вказали ім'я своєї бабусі замість дати\n";
						}
					}
					else {
						respond = "Друже, краще надійшли мені текстове повідомлення "
								+ "з датою Дня Народження твоєго друга";
					}
				}
				else if (textExpected) {
					boolean textReceived = text != null;
					
					if (textReceived) {
						final int LENGTH_LIMIT = 3000;
						boolean successful = text.length() <= LENGTH_LIMIT;
						if (successful) {
							try {
								setBirthday.setText(text);
								boolean isAnonymous = bot.userIsAnonymous(fromId);
								Birthday birthday = setBirthday.toBirthday(isAnonymous);
								Birthday.addBirthday(birthday);
								
								SessionStore.clear(fromId);
							} catch (IOException e) {}
							
							respond = "Готово, я привітаю твого друга коли настане час :)";
						}
						else {
							respond = "Вибач, твоє повідомлення має більше 3000 символів :(";
						}
					}
					else {
						respond = "так неможна, можна тільки текст з привітанням :(";
					}
				}
				
				SendMessage sendMessage = new SendMessage(chatId, respond);
				bot.execute(sendMessage);
				break;
			}
			
		}
	}
	public void setJokeSession(Message message) {
		//array of text messages this session ignores
		List<String> ignore = new ArrayList<String>();
		ignore.add("/setjoke");
		ignore.add("/anonymous");
		ignore.add("/cancel");
		
		long fromId = message.from().id();
		List<SetJokeSession> list = SessionStore.setJoke();
		
		for (SetJokeSession setJoke : list) {
			
			//in case there is an existing SetJokeSession of that user
			if (setJoke.authorId() == fromId) {
				
				boolean jokeExpected = setJoke.text() == null;
				boolean confirmExpected = !jokeExpected && setJoke.confirmed() == false;
				
				String text = message.text();
				long chatId = message.chat().id();
				
				//break, if it contains something from ignore list
				boolean fromIgnoreList = ignore.contains(text);
				if (fromIgnoreList) break;
				
				String response = "";
				if (jokeExpected) {
					if (text == null) response = "Вибачте, я приймаю лише текстові анекдоти";
					else if (text.length() > 3700) response = "Вибачте, ваш анекдот занадто довгий";
					else {
						boolean anonymous = false;
						try {	anonymous = bot.userIsAnonymous(fromId);	}
						catch (FileNotFoundException e) {}
						
						String name;
						if (anonymous) name = "АНОНІМУС";
						else name = setJoke.authorName();
						
						response =
							  "🔸" + name + "🔸\n"
							+ "\n"
							+ text + "\n"
							+ "_ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n\n"
							+ "Підтвердити: /confirm\n"
							+ "\n"
							+ "Відмінити: /cancel";
						
						setJoke.setText(text);
					}
				}
				else if (confirmExpected) {
					
					if (text == null) response = "Будь ласка, відправте /confirm або /cancel";
					else if ( !text.contentEquals("/confirm") ) {
						response = "Будь ласка, відправте /confirm або /cancel";
					}
					else {
						setJoke.setConfirmed(true);
						
						boolean anonymous = false;
						try {	anonymous = bot.userIsAnonymous(fromId);	}
						catch (FileNotFoundException e) {}
						
						Joke joke = setJoke.toJoke(anonymous);
						try {	Joke.addJoke(joke);	}
						catch (IOException e) {}
						
						SessionStore.clear(fromId);
						response = "Анекдот збережено! Дякую за ваш внесок!";
					}
				}
				SendMessage sendMessage = new SendMessage(chatId, response);
				bot.execute(sendMessage);
				break;
			}
		}
	}
	private boolean birthdayExists(long fromId, long contactId) {
		List<Birthday> birthdays = null;
		try {	birthdays = Birthday.readBirthdays();	}
		catch (FileNotFoundException e) {}
		
		boolean exists = false;
		for (Birthday birthday : birthdays) {
			boolean isDisplayed = birthday.isDisplayed();
			boolean fromIdMatch = birthday.authorId() == fromId;
			boolean contactIdMatch = birthday.contactId() == contactId;
			
			exists = (fromIdMatch && contactIdMatch) && !isDisplayed;
			if (exists) break;
		}
		
		return exists;
	}
	
	
	private void birthdayRemove(Message message) {
		List<Birthday> birthdays = null;
		try {	birthdays = Birthday.readBirthdays();	}
		catch (FileNotFoundException e) {}
		
		String text = message.text();
		long fromId = message.from().id();
		long chatId = message.chat().id();
		
		String code = text.replace("/birthdayremove_", "");
		
		for (Birthday birthday : birthdays) {
			boolean matches = birthday.code().contentEquals(code);
			boolean isAuthor = birthday.authorId() == fromId;
			boolean approved = matches && isAuthor;
			if (approved) {
				birthdays.remove(birthday);
				try {	Birthday.writeBirthdays(birthdays);   }
				catch (IOException e) {}
				
				String response = "Ваше привітання було видалено!";
				SendMessage send = new SendMessage (chatId, response);
				bot.execute(send);
				break;
			}
		}
	}
	private void birthdayText(Message message) {
		List<Birthday> birthdays = null;
		try {	birthdays = Birthday.readBirthdays();	}
		catch (FileNotFoundException e) {}
		
		String text = message.text();
		long chatId = message.chat().id();
		
		String code = "";
		switch (message.chat().type()) {
		case Private:
			code = text.replace("/birthdaytext_", "");
			break;
		case group:
		case supergroup:
			code = text.replace("/birthdaytext_", "").replace(bot.username(), "");
			break;
		default:
			break;
		}
		
		String response = "";
		boolean matches = false;
		for (Birthday birthday : birthdays) {
			matches = birthday.code().contentEquals(code);
			if (matches) {
				response += "🔸" + birthday.authorName() + "🔸" + "\n\n";
				response += birthday.text();
				break;
			}
		}
		
		if (!matches) response = "Такого тексту не існує!";
		SendMessage send = new SendMessage(chatId, response);
		bot.execute(send);
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
				if (command.terminatesSessions()) SessionStore.clear(message.from().id());
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
				if (command.terminatesSessions()) SessionStore.clear(message.from().id());
				command.execute(message);
				break;
			}
		}
	}
}
