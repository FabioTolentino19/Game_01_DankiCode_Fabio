package com.tolentsgames.main;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
* Handles playing, stoping, and looping of sounds for the game.
* @author Tyler Thomas
*
*/
public class Sound {
	
	private Clip clip;
	
	private FloatControl volume;
	public static final int sliderVolume[] = new int[] {-50, -40, -20, -15, -10, -5, -2, 0, 2, 4, 6};
	
	public static final Sound musicBackground = new Sound("res/music_cursogame.wav");
	public static final Sound hurtEffect = new Sound("res/50924__rutgermuller__vocal-man-hit-ohhh.wav");
	public static final Sound hurtEffectEnemy = new Sound("res/547192__mrfossy__voice-adultmale-paingrunts-13.wav");
	public static final Sound bipMenu = new Sound("res/Blip_Select3_bip_menu.wav");
	public static final Sound bipLife = new Sound("res/Powerup2_life.wav");
	public static final Sound bipWeapon = new Sound("res/Powerup6_weapon.wav");
	public static final Sound bipAmmo = new Sound("res/Powerup14_Ammo.wav");
	public static final Sound bipShoot = new Sound("res/574818__wesleyextreme-gamer__gunshot.wav");
	public static final Sound explosionWall = new Sound("res/Explosionwall.wav");
	
	public static int musicVolume = 3;
	public static int effectsVolume = 3;
	public static final int maxVolume = 9;
	
	public Sound(String fileName) {
   // specify the sound to play
   // (assuming the sound can be played by the audio system)
   // from a wave File
   try {
     File file = new File(fileName);
     if (file.exists()) {
       AudioInputStream sound = AudioSystem.getAudioInputStream(file);
      // load the sound into memory (a Clip)
       clip = AudioSystem.getClip();
       clip.open(sound);
       volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
     }
     else {
       throw new RuntimeException("Sound: file not found: " + fileName);
     }
   }
   catch (MalformedURLException e) {
     e.printStackTrace();
     throw new RuntimeException("Sound: Malformed URL: " + e);
   }
   catch (UnsupportedAudioFileException e) {
     e.printStackTrace();
     throw new RuntimeException("Sound: Unsupported Audio File: " + e);
   }
   catch (IOException e) {
     e.printStackTrace();
     throw new RuntimeException("Sound: Input/Output Error: " + e);
   }
   catch (LineUnavailableException e) {
     e.printStackTrace();
     throw new RuntimeException("Sound: Line Unavailable Exception Error: " + e);
   }

 // play, stop, loop the sound clip
 }
 public void play(int setVolume){
	
	 volume.setValue(sliderVolume[setVolume]);
	 clip.setFramePosition(0);  // Must always rewind!
     clip.start();
 }
 public void loop(int setVolume){
	 
	 volume.setValue(sliderVolume[setVolume]);
	 clip.loop(Clip.LOOP_CONTINUOUSLY);
/*     for(int i = 0; i < 16; i ++) {
    	 System.out.println("Volume: " + sliderVolume[i]);
    	 volume.setValue(sliderVolume[i]);
    	 try {
    		 Thread.sleep(1000);
    		 } catch (InterruptedException e){System.out.println(e);}
     } */
 }
 
 public void setVolume(int setVolume ) {
	 volume.setValue(sliderVolume[setVolume]);
 }
 public void stop(){
     clip.stop();
   }
 }

