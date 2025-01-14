package logic.processor.rendering.audio;

import java.util.HashMap;
import java.util.Map;

import com.brainless.alchemist.model.ECS.pipeline.BaseProcessor;
import com.brainless.alchemist.model.tempImport.RendererPlatform;
import com.jme3.audio.AudioNode;
import com.simsilica.es.Entity;

import component.ability.Ability;
import component.ability.AbilityControl;
import component.assets.AudioSource;
import logic.commonLogic.Controlling;
import logic.processor.SpatialPool;

public class AbilityAudioProc extends BaseProcessor {
	Map<String, AudioNode> sounds = new HashMap<>();

	@Override
	protected void registerSets() {
		registerDefault(AudioSource.class, AbilityControl.class);
	}
	
	@Override
	protected void onEntityEachTick(Entity e) {
		AudioSource source = e.get(AudioSource.class);
		
		Ability a = Controlling.getControl(Ability.class, e.getId(), entityData);
		
		if(a.isTriggered())
			start(e, source);
		else if(source.isLoop())
			stop(e, source);
	}
	
	private void start(Entity e, AudioSource source){
		AudioNode audio = getAudioNode(source.getPath());
		audio.setLooping(source.isLoop());
		audio.setVolume((float)source.getVolume().getValue());
		audio.setPositional(false);
		
		audio.setRefDistance(4);
		audio.setReverbEnabled(false);
		playSound(e, audio);
	}

	private void stop(Entity e, AudioSource source){
		AudioNode audio = getAudioNode(source.getPath());
		audio.stop();
	}
	
	protected AudioNode getAudioNode(String soundPath) {
		if (!sounds.containsKey(soundPath)) {
			sounds.put(soundPath, new AudioNode(RendererPlatform.getAssetManager(), "sounds/" + soundPath));
			RendererPlatform.getMainSceneNode().attachChild(sounds.get(soundPath));
		}
		return sounds.get(soundPath).clone();
	}
	
	private void playSound(Entity e, AudioNode a){
		AudioNode current = SpatialPool.playingSounds.get(e.getId());
		if(current != null)
			current.stop();
		a.play();
		SpatialPool.playingSounds.put(e.getId(), a);
	}

}
