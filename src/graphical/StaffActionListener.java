package graphical;

import notation.Staff;

public interface StaffActionListener {

    void requestKeySignature(Staff staff);
    void requestTimeSignature(Staff staff);
}
