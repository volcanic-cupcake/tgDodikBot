package tgAyeBot;


import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Scanner;

import com.pengrad.telegrambot.*;
import com.pengrad.telegrambot.model.ChatMemberUpdated;
import com.pengrad.telegrambot.model.Contact;
import com.pengrad.telegrambot.model.Update;

enum Resource {
	TOKEN("src/main/resources/TOKEN.txt");
	
	String path;
	Resource(String path) {
		this.path = path;
	}
}
public class Main {

	public static void main(String[] args) throws IOException {
		ZonedDateTime uaTime = Bot.uaDateTimeNow();
		
		String token = "";
		Scanner scan = new Scanner(new File(Resource.TOKEN.path));
		while(scan.hasNextLine()) {
			token += scan.nextLine();
		}
		scan.close();
		
		Bot bot = new Bot(token);
		
		// Listening for updates
		bot.setUpdatesListener(updates -> {
		    for (Update update : updates) {
		    	ChatMemberUpdated member = update.myChatMember();
		    	if (member != null) {
		    		System.out.println("not null");
		    		System.out.println(member.newChatMember().user().id());
		    	}
		    	
		    	Contact contact = update.message().contact();
		    	if (contact != null) System.out.println(contact.userId());
		    }
		    return UpdatesListener.CONFIRMED_UPDATES_ALL;
		});
	}

}
