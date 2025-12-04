package scoreWriter;

import java.util.ArrayList;
import java.util.List;

public class LayerMixer {

    private final Score score;

    public LayerMixer(Score score) {
        this.score = score;
    }

    public List<List<GraphicalObject>> mixStaff(int StaffNumber) {
    	Staff staff = score.getStaff(StaffNumber);
    	return mixStaff(staff);
    }
    public List<List<GraphicalObject>> mixStaff(Staff staff) {

        // Lista risultato: una lista per ogni voce (tranne STAFF_WIDE)
        List<List<GraphicalObject>> result = new ArrayList<>();

        // Oggetti staff-wide (comuni a ogni lista)
        List<GraphicalObject> wide = score.getStaffWideObjects(staff);

        // Per ogni voice dello staff
        for (VoiceLayer vl : staff.getVoices()) {

            // Skippa la voce STAFF_WIDE
            if (vl.getVoiceType() == VoiceLayer.VoiceType.STAFF_WIDE)
                continue;

            // Crea lista combinata: staff-wide + questa voice
            List<GraphicalObject> combined = new ArrayList<>();
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
