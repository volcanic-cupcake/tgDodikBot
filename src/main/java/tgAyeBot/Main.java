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
		bot.chats = BotChat.readChats();
		
		MessageHandler handler = new MessageHandler(bot);
		
		// Listening for updates
		bot.setUpdatesListener(updates -> {
			
		    for (Update update : updates) {
		    	Message message = update.message();
		    	if (message != null) {
		    		Type type = message.chat().type();
		    		if (message.text() != null) {
			    		switch (type) {
			    		case Private:
			    			handler.Private(message);
			    			break;
			    		case group:
			    		case supergroup:
			    			handler.group(message);
			    			break;
			    		default:
			    			break;
			    		}
		    		}
		    		
		    		if (type != Type.Private) {
		    			try { handler.updateChatData(bot.chats, message); }
			    		catch (IOException e) {}
		    		}
		    	}
		    }
		    return UpdatesListener.CONFIRMED_UPDATES_ALL;
		});
	}

}