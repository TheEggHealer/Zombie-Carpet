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

import com.zurragamez.src.Player;

public class AudioMaster {
	
	private static long device;
	private static long context;
	private static ALCCapabilities alcCapabilities;
	private static ALCapabilities alCapabilities;
	
	public static List<Integer> buffers = new ArrayList<Integer>();
	
	public static void init() {
		String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		device = alcOpenDevice(defaultDeviceName);
		int[] attributes = {0};
		context = alcCreateContext(device, attributes);
		alcMakeContextCurrent(context);
		
		alcCapabilities = ALC.createCapabilities(device);
		alCapabilities = AL.createCapabilities(alcCapabilities);
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
		AL10.alListener3f(AL10.AL_POSITION, player.x, player.y, 0);
	}
	
	public static int loadSound(String path) {
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
		
		return bufferPointer;
	}

}
