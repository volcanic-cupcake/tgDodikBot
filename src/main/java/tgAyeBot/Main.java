package tgAyeBot;


import java.io.IOException;
import java.util.List;

import com.pengrad.telegrambot.*;
import com.pengrad.telegrambot.model.Chat.Type;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

public class Main {

	public static void main(String[] args) throws IOException {
		String token = TextFile.read(Resource.TOKEN.path);
		Bot bot = new Bot(token);
		bot.chats = BotChat.readChats();
		MessageHandler handler = new MessageHandler(bot);
		
		bot.confirmAllUpdates(handler);
		// Listening for updates
		bot.setUpdatesListener(updates -> {
			if (updates.size() > 10) return UpdatesListener.CONFIRMED_UPDATES_ALL;
			
		    for (Update update : updates) {
		    	
		    	Message message = update.message();
		    	
		    	if (message != null) {			    		
		    		Type type = message.chat().type();
		    		String text = message.text();
		    		
		    		switch(type) {
		    		case Private:
		    			handler.setBirthdaySession(message);
		    			if (text != null) handler.Private(message);
		    			break;
		    		case group:
		    		case supergroup:		    			
		    			try { handler.updateChatData(bot.chats, message); }
			    		catch (IOException e) {}
		    			
		    			if (text != null) handler.group(message);
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