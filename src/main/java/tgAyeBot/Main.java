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
		    		Type type = message.chat().type();
		    		switch (type) {
		    		case Private:
		    			MessageHandler.Private(message);
		    			break;
		    		case group:
		    		case supergroup:
		    			MessageHandler.group(message);
		    			break;
		    		default:
		    			break;
		    		}
		    	}
		    }
		    return UpdatesListener.CONFIRMED_UPDATES_ALL;
		});
	}

}