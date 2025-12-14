package scoreWriter;

import java.util.ArrayList;
import java.util.List;

import graphical.GraphicalObject;
import model.Voice;

public class Staff {

	private List<Voice> voices;

	public Staff() {
		voices = new ArrayList<>();
		voices.add(new Voice(0));
		voices.add(new Voice(1));
		voices.add(new Voice(2));
	}
	
	public void addVoice(int type) {
		
	}

	public Voice getVoice(int n) {
		return voices.get(n);
	}
	
	public List<GraphicalObject> getObjects(int voiceNumber) {
		return getVoice(voiceNumber).getObjects();
	}
	
	public List<Voice> getVoices() {
		return voices;
	}
	
	public List<Voice> getVoicesWithMusic() {
		List<Voice> voiceWithMusic = new ArrayList<>();
		for (int i = 1; i < voices.size(); i++) {
			if (!voices.get(i).isEmpty()) voiceWithMusic.add(voices.get(i));
		}
		return voiceWithMusic;
	}
	
	public boolean removeObject(GraphicalObject obj) {
		 if (obj == null) 
		        return false;
		 
		for (Voice v : voices) {
			if (v.removeObject(obj))
				return true;
		}
		return false;
	}

	public void clearVoice(int voiceNumber) {
		getVoice(voiceNumber).clear();
	}
	
	public void sort() {
	    for (Voice voice : getVoices()) {
	        voice.sort();
	    }
	}
}
