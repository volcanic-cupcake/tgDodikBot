package tgAyeBot;




import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetChatMemberResponse;
import com.pengrad.telegrambot.response.GetMeResponse;

import tgAyeBot.Command.CommandType;


public class Bot extends TelegramBot {
	
	private final String USERNAME = "@" + botGetMe().username();
	
	public Bot(String token) {
		super(token);
	}
	
	public static ZonedDateTime uaDateTimeNow() {
		Instant instant = Instant.now();
		ZoneId zoneId = ZoneId.of("Europe/Kiev");
		ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, zoneId);
		return zdt;
	}
	public static ZonedDateTime uaDateTime(String dateString) { //accepts dd:MM format
		dateString = dateString.strip();
		int dayInput = 0;
		int monthInput = 0;
		try {
			dayInput = Integer.parseInt(dateString.substring(0, 2));
			monthInput = Integer.parseInt(dateString.substring(3));
		}
		catch (NumberFormatException | StringIndexOutOfBoundsException e) {
			return null;
		}
		
		ZoneId zoneId = ZoneId.of("Europe/Kiev");
		ZonedDateTime now = uaDateTimeNow();
		
		int monthNow = now.getMonthValue();
		int dayNow = now.getDayOfMonth();
		boolean isThisYear = monthInput > monthNow || (monthInput == monthNow && dayInput > dayNow);
		
		int year;
		if (isThisYear) year = now.getYear();
		else year = now.getYear() + 1;
		
		ZonedDateTime zdt = ZonedDateTime.of(year, monthInput, dayInput, 0, 0, 0, 0, zoneId);
		return zdt;
	}
	
	public SendMessage helpMessage(long chatId) {
		String text =
				"[ УСІ ЧАТИ ]\n"
				+ "/help - ти це щойно прописав блядь\n"
				+ "/youtube - плейлист з поясненням на Ютубі\n"
				+ "/russian_warship :D\n\n"
				
				+ "[ ЛС ЗІ МНОЮ ]\n"
				+ "/cancel - відмінити лайно\n"
				+ "/anonymous - анонімний режим\n"
				+ "/setbirthday - зберегти привітання на ДН\n"
				+ "/delbirthday - видалити привітання на ДН\n"
				+ "/mybirthdays - список привітань на ДН\n\n"
				
				+ "[ ГРУПОВІ ЧАТИ ]\n"
				+ "/pidoras - pidoras";
		SendMessage send = new SendMessage(chatId, text);
		return send;
	}
	public boolean sendMyBirthdays(long fromId, long chatId) throws FileNotFoundException {
		List<Birthday> allBirthdays = Birthday.readBirthdays();
		List<Birthday> myBirthdays = new ArrayList<Birthday>();
		for (Birthday birthday : allBirthdays) {
			if (birthday.authorId() == fromId) myBirthdays.add(birthday);
		}
		
		boolean isEmpty;
		String output;
		if (myBirthdays.isEmpty()) {
			isEmpty = true;
			output = "Пусто, прямо як у москаляки в голові!";
		}
		else {
			isEmpty = false;
			output = birthdaysToText(myBirthdays);
		}
		secureTextSend(chatId, output);
		return isEmpty;
	}
	
	public void delBirthdays(long fromId, long chatId) {
		ZonedDateTime now = uaDateTimeNow();
		SessionStore.clear(fromId);
		List<DelBirthdaySession> sessions = SessionStore.delBirthday();
		DelBirthdaySession newSession = new DelBirthdaySession(fromId, now);
		sessions.add(newSession);
		
		boolean birthdaysEmpty = true;
		try {
			birthdaysEmpty = sendMyBirthdays(fromId, chatId);
		} catch (FileNotFoundException e) {}
		
		if (!birthdaysEmpty) {
			String text =
					  "Відправ мені номер привітання, яке ти хочеш видалити.\n"
					+ "Наприклад: 1";
			SendMessage send = new SendMessage(chatId, text);
			this.execute(send);
		}
	}
	public Command[] commands() {
		Bot bot = this;
		//List<Command> commands = new ArrayList<Command>();
		
		Command help = new Command(CommandType.PRIVATE_AND_GROUP, "/help") {
			@Override
			public void execute(Message message) {
				long chatId = message.chat().id();
				SendMessage send = helpMessage(chatId);
				bot.execute(send);
			}
		};
		
		Command youtube = new Command(CommandType.PRIVATE_AND_GROUP, "/youtube") {
			@Override
			public void execute(Message message) {
				long chatId = message.chat().id();
				String link = "its not a link but it will be";
				SendMessage send = new SendMessage(chatId, link);
				bot.execute(send);
			}
		};
		
		Command russian_warship = new Command(CommandType.PRIVATE_AND_GROUP, "/russian_warship") {
			@Override
			public void execute(Message message) {
				long chatId = message.chat().id();
				int messageId = message.messageId();
				String text = "...иди нахуй!";
				SendMessage send = new SendMessage(chatId, text)
						.replyToMessageId(messageId);
				bot.execute(send);
			}
		};
		
		Command cancel = new Command(CommandType.PRIVATE, "/cancel") {
			@Override
			public void execute(Message message) {
				int messageId = message.messageId();
				long chatId = message.chat().id();
				long fromId = message.from().id();
				
				SessionStore.clear(fromId);
				SendMessage send = new SendMessage(chatId, "гаразд")
						.replyToMessageId(messageId);
				bot.execute(send);
			}
		};
		
		Command anonymous = new Command(CommandType.PRIVATE, "/anonymous") {
			@Override
			public void execute(Message message) {
				long fromId = message.from().id();
				long chatId = message.chat().id();
				List<Long> anonymous = null;
				try {
					anonymous = readAnonymous();
				} catch (FileNotFoundException e) {}
				
				boolean isEnabled = anonymous.contains(fromId);
				String output;
				if (isEnabled) {
					anonymous.remove(fromId);
					try {
						writeAnonymous(anonymous);
					} catch (IOException e) {}
					
					output = "Ви більше не анонімус :o"; 
				}
				else {
					anonymous.add(fromId);
					try {
						writeAnonymous(anonymous);
					} catch (IOException e) {}
					
					output = "Ви тепер анонімус :D";
				}
				
				SendMessage send = new SendMessage(chatId, output);
				bot.execute(send);
			}
		};
		
		Command my_birthdays = new Command(CommandType.PRIVATE, "/mybirthdays") {
			@Override
			public void execute(Message message) {
				long fromId = message.from().id();
				long chatId = message.chat().id();
				
				try {
					sendMyBirthdays(fromId, chatId);
				} catch (FileNotFoundException e) {}
				
			}
		};
		
		Command set_birthday = new Command(CommandType.PRIVATE, "/setbirthday") {
			@Override
			public void execute(Message message) {
				User from = message.from();
				long fromId = from.id();
				String fullName = "";
				String firstName = from.firstName();
				String lastName = from.lastName();
				if (firstName != null) fullName += firstName;
				if (lastName != null) fullName += " " + lastName;
				ZonedDateTime now = uaDateTimeNow();
				
				SessionStore.clear(fromId);
				List<SetBirthdaySession> bdaySessions = SessionStore.setBirthday();
				
				SetBirthdaySession newSession = new SetBirthdaySession(now, fromId, fullName);
				bdaySessions.add(newSession);
			}
		};
		
		Command del_birthday = new Command(CommandType.PRIVATE, "/delbirthday") {
			@Override
			public void execute(Message message) {
				long fromId = message.from().id();
				long chatId = message.chat().id();
				
				delBirthdays(fromId, chatId);
			}
		};
		
		Command[] commands = {
				help, youtube, russian_warship, cancel, anonymous,
				set_birthday, del_birthday, my_birthdays
		};
		return commands;
	}
	
	public static List<Long> readAnonymous() throws FileNotFoundException {
		List<String> lines = TextFile.readLines(Resource.anonymousMode.path);
		List<Long> ids = new ArrayList<Long>();
		for (String line : lines) {
			ids.add( Long.parseLong(line) );
		}
		return ids;
	}
	public static void writeAnonymous(List<Long> ids) throws IOException {
		List<String> lines = new ArrayList<String>();
		String line;
		for (long id : ids) {
			line = Long.toString(id);
			lines.add(line);
		}
		TextFile.writeLines(Resource.anonymousMode.path, lines, false);
	}

	public void secureTextSend(long chatId, String text) {
		final int MAX_LENGTH = 4096;
		
		if (text.length() <= MAX_LENGTH) {
			SendMessage send = new SendMessage(chatId, text);
			this.execute(send);
		}
		else { //sending multiple messages
			int num = text.length() / MAX_LENGTH;
			int leftNum = text.length() % MAX_LENGTH;
			
			String temp;
			for (int i = 0; i < num; i++) {
				temp = text.substring(0, MAX_LENGTH);
				text = text.substring(MAX_LENGTH, text.length());
				
				SendMessage send = new SendMessage(chatId, temp);
				this.execute(send);
			}
			
			if (leftNum != 0) {
				SendMessage send = new SendMessage(chatId, text);
				this.execute(send);
			}
		}
	}
	
	public boolean userIsAnonymous(long userId) throws FileNotFoundException {
		List<Long> anonymousList = Bot.readAnonymous();
		boolean anonymous = anonymousList.contains(userId);
		return anonymous;
	}
	
	public ChatMember botGetChatMember(long chatId, long userId) {
		GetChatMember request = new GetChatMember(chatId, userId);
    	GetChatMemberResponse getChatMemberResponse = this.execute(request);
    	ChatMember chatMember = getChatMemberResponse.chatMember();
		return chatMember;
	}
	
	public User botGetMe() {
		GetMe request = new GetMe();
		GetMeResponse getMeResponse = this.execute(request);
		User me = getMeResponse.user();
		return me;
	}
	public String username() {
		return this.USERNAME;
	}
	
	private String zdtToString(ZonedDateTime zdt) {
		
		String day = Integer.toString( zdt.getDayOfMonth() );
		String month = Integer.toString( zdt.getMonthValue() );
		String year = Integer.toString( zdt.getYear() );
		
		if (day.length() != 2) day = "0" + day;
		if (month.length() != 2) month = "0" + month;
		
		String date = day + "." + month + "." + year;
		return date;
	}
	private String birthdaysToText (List<Birthday> birthdays) {
		String separator = "_ _ _ _ _ _ _ _ _ _ _ _ _ _ _";
		int number = 1;
		String output = "";
		String date;
		for (Birthday birthday : birthdays) {
			date = zdtToString( birthday.birthdayDate() );
			
			output	+= separator + "\n\n"
					
					+ "#" + number + " [ " + birthday.contactName() + " ]\n\n"
					+ "Ваше ім'я:\n"
					+ birthday.authorName() + "\n\n"
					+ "Дата:\n"
					+ date + "\n\n"
					+ "Текст:\n"
					+ birthday.text() + "\n";
			
			number++;
		}
		output += separator;
		
		if (output.contentEquals("")) return null;
		else return output;
	}
}