package scoreWriter;

import java.util.ArrayList;
import java.util.List;

import musicInterface.MusicObject;
import notation.Score;
import notation.Staff;
import notation.Voice;

public class LayerMixer {

    private final Score score;

    public LayerMixer(Score score) {
        this.score = score;
    }

    public List<List<MusicObject>> mixStaff(int StaffNumber) {
    	Staff staff = score.getStaff(StaffNumber);
    	return mixStaff(staff);
    }
    
    public List<List<MusicObject>> mixStaff(Staff staff) {

        // Lista risultato: una lista per ogni voce (tranne STAFF_WIDE)
        List<List<MusicObject>> result = new ArrayList<>();

        // Oggetti staff-wide (comuni a ogni lista)
        List<MusicObject> wide = score.getStaffWideObjects(staff);

        // Per ogni voice dello staff
        for (Voice vl : staff.getVoices()) {

            // Skippa la voce STAFF_WIDE
            if (vl.getVoiceType() == 0)
                continue;

            // Crea lista combinata: staff-wide + questa voice
            List<MusicObject> combined = new ArrayList<>();
            combined.addAll(wide);
            combined.addAll(score.getObjects(staff, vl.getVoiceType()));

            // Ordina per X
            combined.sort(new CompareXPos());

            // Aggiunge alla struttura
            result.add(combined);
        }

        return result;
    }
}
