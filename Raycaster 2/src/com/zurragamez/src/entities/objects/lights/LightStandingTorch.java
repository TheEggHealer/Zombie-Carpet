package com.zurragamez.src.entities.objects.lights;

import com.zurragamez.src.entities.particles.ParticleSmoke;
import com.zurragamez.src.resources.Sprite;
import com.zurragamez.src.resources.audio.AudioMaster;
import com.zurragamez.src.resources.audio.Source;

public class LightStandingTorch extends Light {

	private int flickerCooldown = 4;
	private int smokeCooldown = 4;
	private int soundCooldown = random.nextInt(400) + 40;
	
	private Source source;
	private int sound_flame1, sound_flame2;
	
	public LightStandingTorch(float x, float y) {
		super(x, y, 0.6f, true, 6, Sprite.object_light);
		nearRadius = 7;
		
		source = new Source();
		source.setLocation(x, y);
		source.setGain(0.3f);
		sound_flame1 = AudioMaster.loadSound("res/sounds/other/flame_01.ogg");
		sound_flame2 = AudioMaster.loadSound("res/sounds/other/flame_02.ogg");
	}
	
	public void update() {
		super.update();
//		Player p = main.getPlayer();
//		x = p.x;
//		y = p.y;
		setupLight();
		if(--flickerCooldown <= 0) {
			flickerCooldown = random.nextInt(20);
			brightness = 1f + random.nextFloat() * 0.6f;
		}
	}
	
	@Override
	public void updateNear() {
		super.updateNear();
		if(--smokeCooldown <= 0) {
			smokeCooldown = random.nextInt(20);
			main.addEntity(new ParticleSmoke(x, y));
		}
		if(!source.isPlaying() && --soundCooldown <= 0) {
			soundCooldown = random.nextInt(400) + 40;
			source.setPitch(0.7f - random.nextFloat() * 0.2f);
			source.play(random.nextInt(2) == 0 ? sound_flame1 : sound_flame2);
		}
	}
	
}
