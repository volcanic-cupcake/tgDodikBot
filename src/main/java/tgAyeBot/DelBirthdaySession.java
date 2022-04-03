package tgAyeBot;

import java.io.FileNotFoundException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class DelBirthdaySession extends Session{
	
	DelBirthdaySession(long authorId, ZonedDateTime created) {
		super(authorId, created);
	}
	
	public List<Birthday> myBirthdays() throws FileNotFoundException {
		List<Birthday> allBirthdays = Birthday.readBirthdays();
		List<Birthday> myBirthdays = new ArrayList<Birthday>();
		
		for (Birthday birthday : allBirthdays) {
			if ( birthday.authorId() == authorId() ) myBirthdays.add(birthday);
		}
		
		return myBirthdays;
	}
}
