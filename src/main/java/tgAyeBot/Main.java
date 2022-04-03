package tgAyeBot;


import java.io.IOException;

import com.pengrad.telegrambot.*;
import com.pengrad.telegrambot.model.Chat.Type;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;

public class Main {

	public static void main(String[] args) throws IOException {
		String token = TextFile.read(Resource.TOKEN.path);
		Bot bot = new Bot(token);
		MessageHandler.setBot(bot);
		MessageHandler.setCommands( bot.commands() );
		
		// Listening for updates
		bot.setUpdatesListener(updates -> {
		    for (Update update : updates) {
		    	Message message = update.message();
		    	if (message != null) {
		    		MessageHandler handler = new MessageHandler(message);
		    		Type type = message.chat().type();
		    		switch (type) {
		    		case Private:
		    			handler.Private();
		    			break;
		    		case group:
		    			handler.group();
		    			break;
		    		default:
		    			break;
		    		}
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