package logic.processor.rendering;

import com.brainless.alchemist.model.ECS.pipeline.BaseProcessor;
import com.brainless.alchemist.model.tempImport.RendererPlatform;
import com.brainless.alchemist.model.tempImport.TranslateUtil;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.post.Filter;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.simsilica.es.Entity;

import component.assets.Lighting;
import component.motion.PlanarStance;
import component.motion.SpaceStance;
import logic.processor.SpatialPool;
import util.geometry.geom3d.Point3D;
import util.math.AngleUtil;

public class LightProc extends BaseProcessor {

	@Override
	protected void registerSets() {
		register("space", SpaceStance.class, Lighting.class);
		register("planar", PlanarStance.class, Lighting.class);
	}
	
	@Override
	protected void onEntityEachTick(Entity e) {
		Lighting l = e.get(Lighting.class);
		SpaceStance spaceStance = e.get(SpaceStance.class);
		PlanarStance planarStance = e.get(PlanarStance.class);
		Point3D position, direction;
		if(spaceStance != null){
			position = spaceStance.position;
			direction = spaceStance.direction;
		} else {
			position = planarStance.coord.get3D(planarStance.elevation);
			direction = Point3D.UNIT_X.getRotationAroundZ(planarStance.orientation.getValue());
		}
		
		if(l.distance == Double.POSITIVE_INFINITY){
			manageDirectional(e, l, position, direction);
		} else if(l.innerAngle >= AngleUtil.FULL || l.outerAngle >= AngleUtil.FULL){
			managePoint(e, l, position, direction);
		} else {
			manageSpot(e, l, position, direction);
		}
	}

	private void manageDirectional(Entity e, Lighting l, Point3D position, Point3D direction){
		if(!SpatialPool.lights.containsKey(e.getId()) || !(SpatialPool.lights.get(e.getId()) instanceof DirectionalLight)){
			DirectionalLight light = new DirectionalLight();
			SpatialPool.lights.put(e.getId(), light);
			RendererPlatform.getMainSceneNode().addLight(light);

			if(l.shadowIntensity.getValue() > 0){
//				DirectionalLightShadowRenderer sr = new DirectionalLightShadowRenderer(AppFacade.getAssetManager(), 4096, 1);
//				//sr.setEnabled(false);
//				//sr.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
//				sr.setShadowZExtend(4096);
//				sr.setLight(light);
//				AppFacade.getViewPort().addProcessor(sr);
				DirectionalLightShadowFilter sf = getShadowFilter();
				if(sf != null){
					sf.setEnabled(true);
					sf.setLight(light);
				}
			}
		}
		DirectionalLight light = (DirectionalLight)SpatialPool.lights.get(e.getId());
		
		light.setColor(TranslateUtil.toColorRGBA(l.color).mult((float)(l.intensity*l.activation.getValue())));
		light.setDirection(TranslateUtil.toVector3f(direction));

		if(l.shadowIntensity.getValue() > 0){
			DirectionalLightShadowFilter sf = getShadowFilter();
			if(sf != null && sf.getLight() == light)
				sf.setShadowIntensity((float)l.getShadowIntensity().getValue());
		}
	}
	
	private DirectionalLightShadowFilter getShadowFilter(){
		for(Filter f : RendererPlatform.getFilterPostProcessor().getFilterList())
			if(f instanceof DirectionalLightShadowFilter)
				return (DirectionalLightShadowFilter)f;
		return null;
	}
	
	private void managePoint(Entity e, Lighting l, Point3D position, Point3D direction){
		if(!SpatialPool.lights.containsKey(e.getId()) || !(SpatialPool.lights.get(e.getId()) instanceof PointLight)){
			PointLight light = new PointLight();
			SpatialPool.lights.put(e.getId(), light);
			RendererPlatform.getMainSceneNode().addLight(light);
		}
		PointLight light = (PointLight)SpatialPool.lights.get(e.getId());

		light.setColor(TranslateUtil.toColorRGBA(l.color).mult((float)(l.intensity*l.activation.getValue())));
		light.setPosition(TranslateUtil.toVector3f(position));
		light.setRadius((float)l.distance);
	}
	
	private void manageSpot(Entity e, Lighting l, Point3D position, Point3D direction){
		if(!SpatialPool.lights.containsKey(e.getId()) || !(SpatialPool.lights.get(e.getId()) instanceof SpotLight)){
			SpotLight light = new SpotLight();
			SpatialPool.lights.put(e.getId(), light);
			RendererPlatform.getMainSceneNode().addLight(light);
			
//			if(l.shadowCaster){
//				SpotLightShadowFilter sf = new SpotLightShadowFilter(AppFacade.getAssetManager(), 1024);
//				sf.setEnabled(true);
////				sf.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
////				sf.setShadowZExtend(SHADOWMAP_SIZE);
//				sf.setLight(light);
//				AppFacade.getFilterPostProcessor().addFilter(sf);
//			}
		}
		SpotLight light = (SpotLight)SpatialPool.lights.get(e.getId());
		
		light.setColor(TranslateUtil.toColorRGBA(l.color).mult((float)(l.intensity*l.activation.getValue())));
		light.setSpotRange((float)l.distance);
		light.setPosition(TranslateUtil.toVector3f(position));
		light.setDirection(TranslateUtil.toVector3f(direction));
		light.setSpotInnerAngle((float)l.innerAngle);
		light.setSpotOuterAngle((float)l.outerAngle);
	}
	
	@Override
	protected void onEntityRemoved(Entity e) {
		RendererPlatform.getMainSceneNode().removeLight(SpatialPool.lights.remove(e.getId()));
	}
}
