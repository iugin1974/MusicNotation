package scoreWriter;

import java.util.ArrayList;
import java.util.List;

public class Staff {

	private List<VoiceLayer> voices;

	public Staff() {
		voices = new ArrayList<>();
		voices.add(new VoiceLayer(0));
		voices.add(new VoiceLayer(1));
		voices.add(new VoiceLayer(2));
	}
	
	public void addVoice(int type) {
		
	}

	public VoiceLayer getVoice(int n) {
		return voices.get(n);
	}
	
	public List<GraphicalObject> getObjects(int voiceNumber) {
		return getVoice(voiceNumber).getObjects();
	}
	
	public List<VoiceLayer> getVoices() {
		return voices;
	}
	
	public boolean removeObject(GraphicalObject obj) {
		 if (obj == null) 
		        return false;
		 
		for (VoiceLayer v : voices) {
			if (v.removeObject(obj))
				return true;
		}
		return false;
	}

	public void clearVoice(int voiceNumber) {
		getVoice(voiceNumber).clear();
		
	}
	
}
