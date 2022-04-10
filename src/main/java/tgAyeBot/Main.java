package tgAyeBot;


import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

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
		bot.confirmAllUpdates(handler);
		try {	bot.congratulateToday();	}
    	catch (IOException e) {}
		
		Timer timer = new Timer();
		long delay = Bot.congratulateDelay(10);
		long period = 24 * 60 * 60 * 1000;
		
		timer.schedule(new TimerTask() {
		    @Override
		    public void run() {
		    	try {	bot.congratulateToday();	}
		    	catch (IOException e) {}
		    }
		}, delay, period);
		// Listening for updates
		bot.setUpdatesListener(updates -> {
			if (updates.size() > 5) return UpdatesListener.CONFIRMED_UPDATES_ALL;
			
		    for (Update update : updates) {
		    	
		    	Message message = update.message();
		    	
		    	if (message != null) {			    		
		    		Type type = message.chat().type();
		    		String text = message.text();
		    		long fromId = message.from().id();
		    		boolean isBanned = bot.banned.contains(fromId);
		    		
		    		switch(type) {
		    		case Private:
		    			if (!isBanned) {
			    			handler.setBirthdaySession(message);
			    			handler.setJokeSession(message);
			    			if (text != null) handler.Private(message);
		    			}
		    			break;
		    		case group:
		    		case supergroup:		    			
		    			try { handler.updateChatData(bot.chats, message); }
			    		catch (IOException e) {}
		    			
		    			if (text != null && !isBanned) handler.group(message);
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