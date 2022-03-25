package tgAyeBot;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.response.GetChatMemberResponse;
import com.pengrad.telegrambot.response.GetMeResponse;


public class Bot extends TelegramBot {
	
	public Bot(String token) {
		super(token);
	}
	
	public static ZonedDateTime uaDateTimeNow() {
		Instant instant = Instant.now();
		ZoneId zoneId = ZoneId.of("Europe/Kiev");
		ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, zoneId);
		return zdt;
	}
	public static ZonedDateTime uaDateTime(String dateString) {
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
		ZonedDateTime now = uaDateTimeNow();
		ZoneId zoneId = ZoneId.of("Europe/Kiev");
		
		int monthNow = now.getMonthValue();
		int dayNow = now.getDayOfMonth();
		boolean isThisYear = monthInput > monthNow || (monthInput == monthNow && dayInput > dayNow);
		System.out.println(ZonedDateTime.of(2015, 2, 30, 4, 23, 3, 34, zoneId).toString());
		SimpleDateFormat dayFormat = new SimpleDateFormat("dd.MM");
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
}