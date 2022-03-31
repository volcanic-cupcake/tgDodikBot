package tgAyeBot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class SessionStore {
	private static List<BirthdaySession> birthday = new ArrayList<BirthdaySession>();
	// also add new stuff to clear() method!

	public static void clear(long userId) {
		//example
		Iterator<BirthdaySession> birthdayIterator = birthday.iterator();
		while (birthdayIterator.hasNext()) {
			BirthdaySession session = birthdayIterator.next();
			if (session.authorId() == userId) birthday.remove(session);
		}
		
		//add new stuff here as in the example
	}
	
	public static List<BirthdaySession> birthday() {
		return birthday;
	}
}