package tgAyeBot;




import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
	
	public List<Command> commands() {
		Bot bot = this;
		List<Command> commands = new ArrayList<Command>();
		Command help = new Command(Type.PRIVATE_AND_GROUP, "/help") {
			
			@Override
			public void execute(Message message) {
				long chatId = message.chat().id();
				String text =
						"[ УСІ ЧАТИ ]\n"
						+ "/help - ти це щойно прописав блядь\n"
						+ "/youtube - плейлист з поясненням на Ютубі\n"
						+ "/russian_warship :D\n\n"
						
						+ "[ ЛС ЗІ МНОЮ ]\n"
						+ "/setbirthday - зберегти привітання на День Народження\n"
						+ "/delbirthday - видалити привітання на ДН\n"
						+ "/mybirthdays - список привітань на ДН\n\n"
						
						+ "[ ГРУПОВІ ЧАТИ ]\n"
						+ "/pidoras - pidoras";
				SendMessage send = new SendMessage(chatId, text);
				bot.execute(send);
			}
			
		};
		commands.add(help);
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