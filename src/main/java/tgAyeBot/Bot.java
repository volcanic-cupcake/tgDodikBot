package tgAyeBot;




import java.io.FileNotFoundException;
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

import tgAyeBot.Command.Type;


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
				+ "/setbirthday - зберегти привітання на ДН\n"
				+ "/delbirthday - видалити привітання на ДН\n"
				+ "/mybirthdays - список привітань на ДН\n\n"
				
				+ "[ ГРУПОВІ ЧАТИ ]\n"
				+ "/pidoras - pidoras";
		SendMessage send = new SendMessage(chatId, text);
		return send;
	}
	public Command[] commands() {
		Bot bot = this;
		//List<Command> commands = new ArrayList<Command>();
		
		Command help = new Command(Type.PRIVATE_AND_GROUP, "/help") {
			@Override
			public void execute(Message message) {
				long chatId = message.chat().id();
				SendMessage send = helpMessage(chatId);
				bot.execute(send);
			}
		};
		
		Command youtube = new Command(Type.PRIVATE_AND_GROUP, "/youtube") {
			@Override
			public void execute(Message message) {
				long chatId = message.chat().id();
				String link = "its not a link but it will be";
				SendMessage send = new SendMessage(chatId, link);
				bot.execute(send);
			}
		};
		
		Command russian_warship = new Command(Type.PRIVATE_AND_GROUP, "/russian_warship") {
			@Override
			public void execute(Message message) {
				long chatId = message.chat().id();
				int messageId = message.messageId();
				String text = "иди нахуй!";
				SendMessage send = new SendMessage(chatId, text)
						.replyToMessageId(messageId);
				bot.execute(send);
			}
		};
		
		Command cancel = new Command(Type.PRIVATE, "/cancel") {
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
		
		Command my_birthdays = new Command(Type.PRIVATE, "/mybirthdays") {
			@Override
			public void execute(Message message) throws FileNotFoundException {
				long fromId = message.from().id();
				List<Birthday> allBirthdays = Birthday.readBirthdays();
				List<Birthday> myBirthdays = new ArrayList<Birthday>();
				for (Birthday birthday : allBirthdays) {
					//finished here
				}
			}
		};
		
		Command set_birthday = new Command(Type.PRIVATE, "/setbirthday") {
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
				List<BirthdaySession> bdaySessions = SessionStore.birthday();
				
				BirthdaySession newSession = new BirthdaySession(now, fromId, fullName);
				bdaySessions.add(newSession);
			}
		};
		
		Command del_birthday = new Command(Type.PRIVATE, "/delbirthday") {
			@Override
			public void execute(Message message) {
				
			}
		};
		
		Command[] commands = {
				help, youtube, russian_warship, cancel,
				set_birthday, del_birthday, my_birthdays
		};
		return commands;
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
}