package tgAyeBot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class SessionStore {
	
	private static List<SetBirthdaySession> setBirthday = new ArrayList<SetBirthdaySession>();
	private static List<DelBirthdaySession> delBirthday = new ArrayList<DelBirthdaySession>();
	// also add new stuff to clear() method!

	public static void clear(long userId) {
		//example begins
		for (SetBirthdaySession session : setBirthday) {
			if (session.authorId() == userId) setBirthday.remove(session);
			break;
		}
		//example ends
		
		for (DelBirthdaySession session : delBirthday) {
			if (session.authorId() == userId) delBirthday.remove(session);
			break;
		}
		
		//add new stuff here as in the example
		
	}
	
	public static List<SetBirthdaySession> setBirthday() {
		return setBirthday;
	}
	public static List<DelBirthdaySession> delBirthday() {
		return delBirthday;
	}
}