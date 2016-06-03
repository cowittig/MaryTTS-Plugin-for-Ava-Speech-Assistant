package org.ava.marytts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ava.pluginengine.TTSPlugin;

/**
 * This class implements the base plugin class to be
 * compatible to Ava's plugin engine.
 * <p>
 * It maninly provides lifecycle functionality, like
 * starting and stopping the plugin.
 *
 * @author Constantin
 * @since 2016-05-25
 * @version 1
 */
public class MaryTTSPlugin implements TTSPlugin {

	private final static Logger log = LogManager.getLogger();

	/** The plugin controller manages the plugin activity. */
	private MaryTTSController pluginController;

	/**
	 * Default constructor.
	 */
	public MaryTTSPlugin() {

	}

	/**
	 * Start the plugin.
	 */
	@Override
	public void start() {
		this.pluginController = new MaryTTSController();
		log.info("MaryTTS plugin started.");
	}

	/**
	 * Stop the plugin.
	 */
	@Override
	public void stop() {
		if( !hasPluginBeenStarted() ) {
			return;
		}

		this.pluginController.destruct();
		log.info("MaryTTS plugin stopped.");
	}

	/**
	 * Continue the plugin.
	 * <p>
	 * This may continue paused speech output. However, pausing speech output
	 * is not yet supported.
	 */
	@Override
	public void continueExecution() {
		if( !hasPluginBeenStarted() ) {
			return;
		}

		log.info("MaryTTS plugin resume triggered.");
		this.pluginController.continueSpeaking();
	}

	/**
	 * Interrupt the plugin.
	 * <p>
	 * This may pause speech output. However, pausing speech output
	 * is not yet supported, instead any current output will just be
	 * stopped and cannot be restored.
	 */
	@Override
	public void interruptExecution() {
		if( !hasPluginBeenStarted() ) {
			return;
		}

		log.info("MaryTTS plugin interrupt triggered.");
		this.pluginController.interruptSpeaking();
	}

	/**
	 * Synthesize the given message and play the generated audio.
	 *
	 * @param msg The text to synthesize.
	 */
	@Override
	public void sayText(String msg) {
		if( !hasPluginBeenStarted() ) {
			return;
		}

		this.pluginController.speak(msg);
	}

	/**
	 * Checks if the plugin has been started yet.
	 *
	 * @return boolean True if the plugin has been started yet, false if not.
	 */
	private boolean hasPluginBeenStarted() {
		if( this.pluginController == null ) {
			log.error("MaryTTS plugin not initialized. Start plugin first.");
			return false;
		} else {
			return true;
		}
	}

}
