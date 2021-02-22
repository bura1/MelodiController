/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MelodiController;
import javax.sound.midi.*;

/**
 *
 * @author bura
 */
public class MidiPorts {

    MidiDevice device = null;
    MidiDevice.Info[] midiPorts = MidiSystem.getMidiDeviceInfo();
    int length = midiPorts.length;
    
    public MidiDevice.Info[] getPorts() {
        return midiPorts;
    }
    
    public int getLength() {
        return length;
    }
    
}
