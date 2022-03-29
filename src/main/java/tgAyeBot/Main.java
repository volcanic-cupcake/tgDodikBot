package tgAyeBot;


import java.io.IOException;

import com.pengrad.telegrambot.*;
import com.pengrad.telegrambot.model.ChatMemberUpdated;
import com.pengrad.telegrambot.model.Contact;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

public class Main {

	public static void main(String[] args) throws IOException {
		String token = TextFile.read(Resource.TOKEN.path);
		Bot bot = new Bot(token);
		
		// Listening for updates
		bot.setUpdatesListener(updates -> {
		    for (Update update : updates) {
		    	Message message = update.message();
		    	if (message != null) {
		    		System.out.println(message.text());
			    	SendMessage send = new SendMessage(message.chat().id(), "/test");
			    	bot.execute(send);
		    	}
		    	/*ChatMemberUpdated member = update.myChatMember();
		    	if (member != null) {
		    		System.out.println("not null");
		    		System.out.println(member.newChatMember().user().id());
		    	}
		    	
		    	Contact contact = update.message().contact();
		    	if (contact != null) System.out.println(contact.userId());*/
		    }
		    return UpdatesListener.CONFIRMED_UPDATES_ALL;
		});
	}

}