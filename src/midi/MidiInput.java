package midi;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

public class MidiInput {
	private MidiListener listener;
	private MidiDevice device;
	private Transmitter transmitter;

	public MidiInput(MidiListener l) {
		listener = l;
	}

	public MidiDevice openDevice(MidiDevice.Info info) {
		try {
			device = MidiSystem.getMidiDevice(info);
			device.open();
			return device;
		} catch (MidiUnavailableException e) {
			System.out.println("Midi unavailable");
		}
		return null;
	}

	public void setTransmitter(MidiDevice device) {
		if (listener == null) {
			return;
		}
		try {
			transmitter = device.getTransmitter();
			Receiver receiver = new Receiver() {

				@Override
				public void send(MidiMessage message, long timeStamp) {
					if (message instanceof ShortMessage) {
						ShortMessage sm = (ShortMessage) message;
						if (sm.getCommand() == ShortMessage.NOTE_ON && sm.getData2() > 0) {
							listener.noteOn(sm.getData1());
						}
					}

				}

				@Override
				public void close() {
				}
			};
			transmitter.setReceiver(receiver);
		} catch (MidiUnavailableException e) {
			throw new RuntimeException("Impossibile ottenere il transmitter", e);
		}
	}

	public void closeDevice() {
		if (transmitter != null) {
			transmitter.close();
		}
		if (device != null && device.isOpen()) {
			device.close();
		}
	}

	public List<MidiDevice.Info> findInputDevices() {
		List<MidiDevice.Info> listDevices = new ArrayList<>();

		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (MidiDevice.Info info : infos) {
			try {
				MidiDevice device = MidiSystem.getMidiDevice(info);
				if (device.getMaxTransmitters() != 0) {
					listDevices.add(info);
				}
			} catch (MidiUnavailableException e) {
				System.out.println("Midi unavailable");
			}
		}
		return listDevices;
	}
}
