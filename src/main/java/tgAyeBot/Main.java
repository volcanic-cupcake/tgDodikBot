package tgAyeBot;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import com.pengrad.telegrambot.*;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Chat.Type;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.ChatMemberUpdated;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Poll;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.request.GetChatMemberCount;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetChatMemberResponse;
import com.pengrad.telegrambot.response.GetMeResponse;
import com.pengrad.telegrambot.response.SendResponse;

enum Resource {
	TOKEN("src/main/resources/TOKEN.txt");
	
	String path;
	Resource(String path) {
		this.path = path;
	}
}
public class Main {

	public static void main(String[] args) throws IOException {

		String token = "";
		Scanner scan = new Scanner(new File(Resource.TOKEN.path));
		while(scan.hasNextLine()) {
			token += scan.nextLine();
		}
		scan.close();
		
		Bot bot = new Bot(token);
		
		System.out.println();
		
		// Register for updates
		bot.removeGetUpdatesListener();
		bot.setUpdatesListener(updates -> {
		    for (Update update : updates) {
		    	ChatMemberUpdated member = update.myChatMember();
		    	if (member != null) {
		    		System.out.println("not null");
		    		System.out.println(member.newChatMember().user().id());
		    	}
		    	// wanted to create another bot, add it to that chat and see the difference between myChatMember() and stuff
		    }
		    return UpdatesListener.CONFIRMED_UPDATES_ALL;
		});
	}

}
