package ui;

import javax.sound.midi.MidiDevice;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MidiDeviceChooser extends JDialog {

    private JList<MidiDevice.Info> deviceList;

    public MidiDeviceChooser(
            List<MidiDevice.Info> devices,
            MidiDeviceSelectionListener listener
    ) {
        deviceList = new JList<>(devices.toArray(new MidiDevice.Info[0]));
        deviceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        deviceList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus
            ) {
                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );

                MidiDevice.Info info = (MidiDevice.Info) value;
                setText(info.getName() + " — " + info.getVendor());
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(deviceList);

        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Annulla");

        ok.addActionListener(e -> {
            MidiDevice.Info selected = deviceList.getSelectedValue();
            if (selected != null) {
                listener.deviceSelected(selected);
                dispose();
            }
        });

        cancel.addActionListener(e -> dispose());

        JPanel buttons = new JPanel();
        buttons.add(ok);
        buttons.add(cancel);

        setLayout(new BorderLayout(5, 5));
        add(scrollPane, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        setSize(400, 300);
        setLocationRelativeTo(null);
    }
}
