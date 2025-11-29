package scoreWriter;

import java.util.ArrayList;
import java.util.List;

import scoreWriter.VoiceLayer.VoiceType;

public class Staff {

	private List<VoiceLayer> voices;
	
	private static final VoiceLayer EMPTY_LAYER = new VoiceLayer(VoiceType.ONE_VOICE) {
	    @Override
	    public List<GraphicalObject> getObjects() {
	        return List.of(); // lista vuota immutabile
	    }
	};
	
	public Staff() {
		voices = new ArrayList<>();
		voices.add(new VoiceLayer(VoiceType.STAFF_WIDE));
		voices.add(new VoiceLayer(VoiceType.VOICE_ONE));
		voices.add(new VoiceLayer(VoiceType.VOICE_TWO));
	}
	
	public void addVoice(int type) {
		
	}
	
	public VoiceLayer getVoice(VoiceType voiceType) {
		for (VoiceLayer v : voices) {
			if (v.getVoiceType() == voiceType) return v;
		}
		return EMPTY_LAYER;
	}
	
	public VoiceLayer getVoice(int n) {
		return voices.get(n);
	}
	
	public List<GraphicalObject> getObjects(VoiceType voiceType) {
		return getVoice(voiceType).getObjects();
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

	public void clearVoice(VoiceType voiceType) {
		getVoice(voiceType).clear();
		
	}
	
}
