/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MelodiController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

/**
 *
 * @author bura
 */
public class MidiSender extends Thread {
    
    private volatile boolean running = true;
    
    static Socket socket;
    static ServerSocket serverSocket;
    static InputStreamReader inputStreamReader;
    static BufferedReader bufferedReader;
    static String message;
    
    MidiDevice device = null;
    MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
    
    volatile int deviceId = 0;
    volatile int portNum = 1314;

    @Override
    public void start() {
        
        DatagramPacket dpac;
        DatagramSocket dsoc = null;
        
        try {
            dsoc = new DatagramSocket(portNum);
        } catch (SocketException ex) {
            Logger.getLogger(MidiSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte [] b = new byte[1000];
        
        while (running) {
            
            try {                
                dpac = new DatagramPacket(b, b.length);
                dsoc.receive(dpac);
                message = new String(dpac.getData());
            } catch(IOException e) {
                e.printStackTrace();
            }

            ShortMessage midiMsg = new ShortMessage();
            
            try {
                System.out.println(message);
                int noteNum = Integer.parseInt(message.substring(1,4));
                int note;
                if (noteNum == 100 || noteNum == 101 || noteNum == 102 || 
                        noteNum == 103 || noteNum == 104 || noteNum == 105 || 
                        noteNum == 106 || noteNum == 107 || noteNum == 108 || 
                        noteNum == 109 || noteNum == 110 || noteNum == 111 || 
                        noteNum == 112 || noteNum == 113 || noteNum == 114 || 
                        noteNum == 115 || noteNum == 116 || noteNum == 117 || 
                        noteNum == 118 || noteNum == 119 || noteNum == 120 ||
                        noteNum == 121 || noteNum == 122 || noteNum == 123 ||
                        noteNum == 124 || noteNum == 125 || noteNum == 126 ||
                        noteNum == 127 || noteNum == 128 || noteNum == 129) {
                    
                    note = Integer.parseInt(message.substring(1,4));
                } else {
                    note = Integer.parseInt(message.substring(1,3));
                }
                
                int velocity = Integer.parseInt(message.substring(4,7));
                
                if (message.substring(0,1).equals("0")) {
                    midiMsg.setMessage(ShortMessage.NOTE_ON, 0, note, velocity);
                }
                if (message.substring(0,1).equals("1")) {
                    midiMsg.setMessage(ShortMessage.NOTE_OFF, 0, note, velocity);
                }
            } catch (InvalidMidiDataException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            long timeStamp = -1;
            Receiver rcvr = null;
            try {
                rcvr = device.getReceiver();
            } catch (MidiUnavailableException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
                    
            rcvr.send(midiMsg, timeStamp);            
        }
        
        try {
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(MidiSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        device.close();
        
    }
    
    public void socketStuff() {
        try {
            device = MidiSystem.getMidiDevice(infos[deviceId]);
        } catch (MidiUnavailableException e) {}

        try {
            device.open();
        } catch (MidiUnavailableException e) {}
    }
    
}
