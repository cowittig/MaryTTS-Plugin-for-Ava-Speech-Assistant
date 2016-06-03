package org.ava.marytts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.AudioInputStream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.util.data.audio.AudioPlayer;

/**
 * This class controls any activity of the MaryTTS plugin.
 * <p>
 * This includes:
 * 		-- synthesizing a given message and generating the audio output.
 * 		-- continuing and interrupting the audio output (not yet supported)
 *
 * @author conwitti
 * @since 2016-05-25
 * @version 1
 *
 */
public class MaryTTSController {

	private final static Logger log = LogManager.getLogger();

	/** Instance of the MaryTTS engine. */
	private MaryInterface marytts;

	/** Lists containing the current audio player and the thread executing
	 *  that audio player. These lists will contain only one audio player or thread
	 *  and are ugly workarounds for accessing the audio player from both the application
	 *  thread and the thread executing the audio player.*/
	private List<AudioPlayer> apl;
	private List<Thread> threadList;

	/** When user requests audio ouput, but an existing audio player is active,
	 *  existing player will be stopped and after given time (in ms) the new
	 *  audio player will start playing.
	 */
	private final int PAUSE_WHEN_ABORTING_CURRENT_OUTPUT = 100;

	/**
	 * Initializes the MaryTTS engine.
	 */
	public MaryTTSController() {
		apl = Collections.synchronizedList(new ArrayList<AudioPlayer>());
		threadList = Collections.synchronizedList(new ArrayList<Thread>());
		init();
	}

	/**
	 * Initialize a local version of the MaryTTS engine without externally starting a server.
	 */
	private void init() {
		try {
			marytts = new LocalMaryInterface();
	        marytts.setVoice("cmu-slt-hsmm");
	        log.debug("MaryTTS instance triggered [voice = " + marytts.getVoice().toString() + "].");
		} catch(MaryConfigurationException ex) {
			log.error("An error occured while configurating MaryTTS: " + ex.getMessage());
			log.catching(Level.DEBUG, ex);
		}
	}

	/**
	 * Synthesize the given text and play the audio.
	 * <p>
	 * Audio playback will happen in a separate thread, to avoid blocking Ava
	 * when playing long texts.
	 *
	 * @param msg The text to synthesize and play.
	 */
	public void speak(final String msg) {
		// we use the audio player list and thread list to enable
		// communication with the audio thread (i.e. stopping the audio player)
		// for some reason it did not work otherwise, so we will stick to this ugly
		// workaround for now

		Runnable speaker = new Runnable() {
            public void run() {
            	if(!apl.isEmpty()) {
            		apl.get(0).cancel();
            		log.debug("Aborted audio output [audio player = " + apl.get(0) + "]");
            		try {
						Thread.sleep(PAUSE_WHEN_ABORTING_CURRENT_OUTPUT);
					} catch (InterruptedException e) {
						// ignore; InterruptedException will always be thrown due to the thread
						// actively playing audio
					}
            		apl.remove(0);
            		threadList.remove(0);
            	}

        		try {
        			AudioInputStream audio;
                	AudioPlayer ap = new AudioPlayer();
        			audio = marytts.generateAudio(msg);
        			ap.setAudio(audio);
        	        apl.add(ap);
        	        ap.start();
        	        log.debug("Started audio output [audio player = " + ap + "]");

        		} catch (SynthesisException e) {
        			log.error("An error occured while synthesizing speech: " + e.getMessage());
        			log.catching(Level.DEBUG, e);
        		}
            }
        };

        Thread t = new Thread(speaker);
        threadList.add(t);
        t.start();
	}

	/**
	 * Stop the plugin and take care of any open resources.
	 * Stop any playing audio as well as any running threads.
	 */
	public void destruct() {
		log.debug("MaryTTS destruct triggered.");

		for(AudioPlayer ap : apl) {
			log.debug("Stop audio player .");
			ap.cancel();
		}
		apl.clear();

		for(Thread t : threadList) {
			log.debug("Remove thread " + t.getName() + " [" + t + "] from thread list.");
			try {
				t.interrupt();
			} catch (Exception ex) {
				if( ex instanceof InterruptedException ) {
					// ignore; InterruptedException will always be thrown when
					// interrupting this thread (due to it actively playing audio)
				}
			}
		}
		threadList.clear();
	}

	/**
	 * Continue paused audio output.
	 * <p>
	 * This is not yet supported, due to pausing audio not being supported.
	 */
	public void continueSpeaking() {
		//ap.getLine().start();
		log.info("Continuation of MaryTTS aduio output is not supported.");
	}

	/**
	 * Interrupt current audio output.
	 * <p>
	 * This is not yet supported. Instead audio output will be completely halted.
	 * Continuation is not possible.
	 */
	public void interruptSpeaking() {
		apl.get(0).cancel();
		log.info("Canceled MaryTTS audio output. Pausing is not supported.");
	}

}
