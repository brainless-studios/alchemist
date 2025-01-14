package logic.processor;


import java.util.HashMap;
import java.util.Map;

import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.light.Light;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityId;

import logic.processor.rendering.ui.VelocityView;
import plugin.infiniteWorld.world.terrain.heightmap.Parcel;

public class SpatialPool {

	
	public static final Map<EntityId, Spatial> models = new HashMap<>();
	public static final Map<Parcel, Spatial> terrainParcels = new HashMap<>();
	public static final Map<Parcel, Spatial> coverParcels = new HashMap<>();
	public static final Map<EntityId, Spatial> inertias = new HashMap<>();
	public static final Map<EntityId, Spatial> appliedVelocities = new HashMap<>();
	public static final Map<EntityId, Spatial> resultingVelocity = new HashMap<>();
	public static final Map<EntityId, ParticleEmitter> emitters = new HashMap<>();
	public static final Map<EntityId, Light> lights = new HashMap<>();
	public static final Map<VelocityView, Spatial> velocities = new HashMap<>();
	public static final Map<EntityId, AudioNode> playingSounds = new HashMap<>();
	
	private SpatialPool(){
	}
}
