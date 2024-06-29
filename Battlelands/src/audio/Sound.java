package audio;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {
	public String name;
	private FloatControl volume;
	private Clip sound;
	private float defaultVolume;
	private float delta;
	
	public Sound(String name, String filePath, float defaultVolume, int loopState, float delta) {
		this.name = name;
		this.defaultVolume = defaultVolume;
		this.delta = delta;
		File soundFile = new File(filePath);
		
		try {
			if(soundFile.exists()) {
				AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundFile);
				sound = AudioSystem.getClip();
				sound.open(audioInput);
				volume = (FloatControl)sound.getControl(FloatControl.Type.MASTER_GAIN);
				sound.loop(loopState);
				sound.start();
				setVolume(0);
			}else {
				System.out.println("Can't find file");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void setVolume(float value) {
		try {
			volume.setValue(delta * (float) Math.log10(value) +defaultVolume);	
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	public float getVolume() {
		return (float) Math.pow(10f, (volume.getValue()-defaultVolume) / delta);
		
	}
	
	public void setVolumeWithSoftening(float value, float softeningCoef, float softeningTriger) {
		if(Math.abs(value - getVolume()) >softeningTriger) {
			if(value>getVolume()){
				setVolume(getVolume()*(1 + softeningCoef));
			}else {
				setVolume(getVolume()*(1 - softeningCoef));
			}
		}else {
			setVolume(value);
		}
	}
}
