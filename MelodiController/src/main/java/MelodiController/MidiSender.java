/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MelodiController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
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
public class MidiSender implements Runnable {
    
    private volatile boolean running = true;
    
    static Socket socket;
    static ServerSocket serverSocket;
    static InputStreamReader inputStreamReader;
    static BufferedReader bufferedReader;
    static String message;
    
    MidiDevice device = null;
    MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
    
    public void zaustavi() {
        running = false;
    }

    @Override
    public void run() {
        
        try {
            //if (serverSocket.getLocalPort() != 6000) {
                serverSocket = new ServerSocket(6000);
            //}
        } catch (IOException ex) {
            Logger.getLogger(MidiSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            device = MidiSystem.getMidiDevice(infos[5]);
        } catch (MidiUnavailableException e) {}

        try {
            device.open();
        } catch (MidiUnavailableException e) {}
            
        if (device.isOpen()) {
            System.out.println("open");
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                socket = serverSocket.accept();
                inputStreamReader = new InputStreamReader(socket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                message = bufferedReader.readLine();
            } catch(IOException e) {
                e.printStackTrace();
            }

            if (!Thread.currentThread().isInterrupted()) {
                ShortMessage midiMsg = new ShortMessage();
                try {
                    int note = Integer.parseInt(message.substring(1,3));
                    if (message.substring(0,1).equals("1")) {
                        midiMsg.setMessage(ShortMessage.NOTE_ON, 0, note, 93);
                    }
                    if (message.substring(0,1).equals("0")) {
                        midiMsg.setMessage(ShortMessage.NOTE_OFF, 1, note, 93);
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
        }
        
        // Close
        device.close();
        if (!(device.isOpen())) {
            System.out.println("Closed");
        }
    }
    
}