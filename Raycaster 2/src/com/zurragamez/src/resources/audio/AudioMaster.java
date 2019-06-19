package com.zurragamez.src.resources.audio;

import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.ALC10.ALC_DEFAULT_DEVICE_SPECIFIER;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcGetString;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.stackMallocInt;
import static org.lwjgl.system.MemoryStack.stackPop;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.libc.LibCStdlib.free;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;

import com.zurragamez.src.Main;
import com.zurragamez.src.Player;

public class AudioMaster {
	private static final String TAG = "AudioMaster";
	
	public static int sus;
	
	public static int walk_01, walk_02;
	
	public static int zombie_01, zombie_02, zombie_03, zombie_04, zombie_05;
	public static int zombie_hurt_01, zombie_hurt_02, zombie_hurt_03, zombie_hurt_04;
	public static int zombie_death_01;
	public static int flame_01, flame_02;
	
	public static int player_shoot;
	
	private static long device;
	private static long context;
	private static ALCCapabilities alcCapabilities;
//	private static ALCapabilities alCapabilities;
	
	public static List<Integer> buffers = new ArrayList<Integer>();
	
	public static void init() {
		String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		device = alcOpenDevice(defaultDeviceName);
		int[] attributes = {0};
		context = alcCreateContext(device, attributes);
		alcMakeContextCurrent(context);
		
		alcCapabilities = ALC.createCapabilities(device);
		AL.createCapabilities(alcCapabilities);
	}
	
	public static void destroy() {
		for(int buffer : buffers) {
			AL10.alDeleteBuffers(buffer);
		}

		alcDestroyContext(context);
		alcCloseDevice(device); 
	}
	
	public static void setListenerData(Player player) {
		ByteBuffer bb = ByteBuffer.allocateDirect(6 * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer orientation = bb.asFloatBuffer();
		orientation.put(0, (float)Math.cos(player.dir));
		orientation.put(1, (float)Math.sin(player.dir));
		orientation.put(2, 0);
		orientation.put(3, 0);
		orientation.put(4, 0);
		orientation.put(5, 1);
		AL10.alListenerfv(AL10.AL_ORIENTATION, orientation);
		AL10.alListener3f(AL10.AL_POSITION, player.x, player.y, 0.5f);
	}
	
	public static void loadSounds() {
		sus = AudioMaster.loadSound("self", "res/sounds/other/flame_01.ogg");
		
		walk_01 = AudioMaster.loadSound("self", "res/sounds/player/walk.ogg");
		walk_02 = AudioMaster.loadSound("self", "res/sounds/player/walk2.ogg");
		
		zombie_01 = AudioMaster.loadSound("self", "res/sounds/zombie_01.ogg");
		zombie_02 = AudioMaster.loadSound("self", "res/sounds/zombie_02.ogg");
		zombie_03 = AudioMaster.loadSound("self", "res/sounds/zombie_03.ogg");
		zombie_04 = AudioMaster.loadSound("self", "res/sounds/zombie_04.ogg");
		zombie_05 = AudioMaster.loadSound("self", "res/sounds/zombie_05.ogg");
		zombie_hurt_01 = AudioMaster.loadSound("self", "res/sounds/zombie_hurt_01.ogg");
		zombie_hurt_02 = AudioMaster.loadSound("self", "res/sounds/zombie_hurt_02.ogg");
		zombie_hurt_03 = AudioMaster.loadSound("self", "res/sounds/zombie_hurt_03.ogg");
		zombie_hurt_04 = AudioMaster.loadSound("self", "res/sounds/zombie_hurt_04.ogg");
		zombie_death_01 = AudioMaster.loadSound("self", "res/sounds/gore.ogg");
		flame_01 = AudioMaster.loadSound("self", "res/sounds/other/flame_01.ogg");
		flame_02 = AudioMaster.loadSound("self", "res/sounds/other/flame_02.ogg");
		
		player_shoot = AudioMaster.loadSound("self", "res/sounds/shoot.ogg");
	}
	
	public static int loadSound(String tag, String path) {
		stackPush();
		IntBuffer channelsBuffer = stackMallocInt(1); 
		stackPush();
		IntBuffer sampleRateBuffer = stackMallocInt(1);
		
		ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(path, channelsBuffer, sampleRateBuffer);
		
		int channels = channelsBuffer.get();
		int sampleRate = sampleRateBuffer.get();
		stackPop();
		stackPop();
		
		int format = -1;
		if(channels == 1) {
			format = AL_FORMAT_MONO16;
		} else if(channels == 2) {
			format = AL_FORMAT_STEREO16;
		}
		
		int bufferPointer = alGenBuffers();
		
		alBufferData(bufferPointer, format, rawAudioBuffer, sampleRate);
		
		free(rawAudioBuffer);
		
		Main.printDebug(tag + "->" + TAG, "Loaded sound: " + path + " successfully");
		return bufferPointer;
	}

}
