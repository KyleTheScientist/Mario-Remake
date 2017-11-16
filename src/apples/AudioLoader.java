package apples;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AudioLoader {

	public static void play(String path){
		
		AudioInputStream stream = null;
		
		if(path.charAt(0) != '/'){
			path = '/' + path;
		}
		try {
			Clip clip = AudioSystem.getClip();
			stream = AudioSystem.getAudioInputStream(AudioLoader.class.getResource(path));
			clip.open(stream);
			clip.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
}
