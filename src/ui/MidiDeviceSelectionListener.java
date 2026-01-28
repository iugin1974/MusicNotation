package ui;

import javax.sound.midi.MidiDevice;

public interface MidiDeviceSelectionListener {
    void deviceSelected(MidiDevice.Info info);
}
