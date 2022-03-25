package tgAyeBot;



import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
	
	public static ZonedDateTime uaDateTime() {
		Instant instant = Instant.now();
		ZoneId zoneId = ZoneId.of("Europe/Kiev");
		ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, zoneId);
		return zdt;
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