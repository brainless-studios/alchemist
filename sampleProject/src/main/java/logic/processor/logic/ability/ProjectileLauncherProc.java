package logic.processor.logic.ability;

import java.util.ArrayList;
import java.util.List;

import com.brainless.alchemist.model.ECS.blueprint.Blueprint;
import com.brainless.alchemist.model.ECS.blueprint.BlueprintLibrary;
import com.brainless.alchemist.model.ECS.builtInComponent.Parenting;
import com.brainless.alchemist.model.ECS.pipeline.BaseProcessor;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;

import component.ability.Ability;
import component.ability.Cooldown;
import component.combat.damage.DamageCapacity;
import component.combat.damage.Projectile;
import component.combat.damage.ProjectileLauncher;
import component.lifeCycle.SpawnOnTouch;
import component.motion.PlanarNeededThrust;
import component.motion.PlanarStance;
import component.motion.PlanarVelocityToApply;
import component.motion.physic.Physic;
import util.LogUtil;
import util.geometry.geom3d.Point3D;
import util.math.Angle;
import util.math.AngleUtil;
import util.math.RandomUtil;

public class ProjectileLauncherProc extends BaseProcessor {

	@Override
	protected void registerSets() {
		registerDefault(PlanarStance.class, ProjectileLauncher.class, Ability.class, Cooldown.class);
	}
	
	@Override
	protected void onEntityEachTick(Entity e) {
		Ability trigger = e.get(Ability.class);
		if(trigger.isTriggered()){
			ProjectileLauncher launcher = e.get(ProjectileLauncher.class);
			PlanarStance stance = e.get(PlanarStance.class);
			
			// TODO locate aggressor better in the hierarchy
			EntityId p = entityData.getComponent(e.getId(), Parenting.class).getParent();
			double orientation = stance.orientation.getValue() + ((RandomUtil.next()-0.5)*(1-launcher.getPrecision().getValue()))*AngleUtil.FULL;
			
			Blueprint bp = BlueprintLibrary.getBlueprint(launcher.getProjectileBluePrint());
			if(bp == null){
				LogUtil.warning("Can't locate projectile's blueprint : "+launcher.getProjectileBluePrint()+".");
				return;
			}
			EntityId eid = bp.createEntity(entityData, null);;
			
			// adding the spawner for collision exception
			Physic ph =entityData.getComponent(eid, Physic.class); 
			if(ph != null)
				entityData.setComponent(eid, new Physic(ph.getVelocity(), ph.getType(), ph.getExceptions(), ph.getMass(), ph.getRestitution(), p));
			
			// correcting the orientation of the velocity to apply
			PlanarVelocityToApply vel = entityData.getComponent(eid, PlanarVelocityToApply.class); 
			if(vel != null)
				entityData.setComponent(eid, new PlanarVelocityToApply(vel.vector.getRotation(orientation)));

			// correcting the orientation of the needed thrust
			PlanarNeededThrust thrust = entityData.getComponent(eid, PlanarNeededThrust.class);
			if(thrust != null)
				entityData.setComponent(eid, new PlanarNeededThrust(thrust.getDirection().getRotation(orientation)));
				
			entityData.setComponent(eid, new PlanarStance(stance.coord.getTranslation(stance.orientation.getValue(), 0.2), new Angle(orientation), stance.elevation, Point3D.UNIT_Z));
			entityData.setComponent(eid, new Projectile(p, stance.coord));
			
			DamageCapacity damageCapacity = entityData.getComponent(e.getId(), DamageCapacity.class);
			if(damageCapacity != null) {
				entityData.setComponent(eid, damageCapacity);
				if(!damageCapacity.getBlueprintOnImpact().isEmpty()){
					SpawnOnTouch spawn = entityData.getComponent(eid, SpawnOnTouch.class);
					List<String> blueprintNames = new ArrayList<>();
					if(spawn != null)
						blueprintNames.addAll(spawn.getBlueprintNames());
					blueprintNames.add(damageCapacity.getBlueprintOnImpact());
					entityData.setComponent(eid, new SpawnOnTouch(blueprintNames));
				}
			}
			
			Cooldown cd = e.get(Cooldown.class);
			setComp(e, new Cooldown(cd.getDuration(), cd.getDuration()));
		}
	}
}
