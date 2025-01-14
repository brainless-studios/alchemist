package logic.processor.rendering.model;

import com.brainless.alchemist.model.ECS.pipeline.BaseProcessor;
import com.brainless.alchemist.model.tempImport.TranslateUtil;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;

import component.assets.Model;
import component.motion.PlanarStance;
import logic.processor.SpatialPool;
import util.geometry.geom3d.Point3D;
import util.math.AngleUtil;

public class ModelPlacingProc extends BaseProcessor {

	@Override
	protected void registerSets() {
		registerDefault(Model.class, PlanarStance.class);
	}
	
	@Override
	protected void onEntityAdded(Entity e) {
		onEntityUpdated(e);
	}
	@Override
	protected void onEntityUpdated(Entity e) {
		Model model = e.get(Model.class);
		Spatial s = SpatialPool.models.get(e.getId());
		
		if(s == null)
			return;
		
		PlanarStance stance = e.get(PlanarStance.class);

		// translation
		s.setLocalTranslation(TranslateUtil.toVector3f(stance.coord.get3D(stance.elevation)));

		// rotation
		Quaternion r = new Quaternion();
		Point3D pu = stance.upVector;
		Point3D pv = Point3D.UNIT_X.getRotationAroundZ(stance.orientation.getValue());
		Vector3f u = TranslateUtil.toVector3f(pu).normalize();
		Vector3f v = TranslateUtil.toVector3f(pv).normalize();
		r.lookAt(v, u);

		// we correct the pitch of the unit because the direction is always flatten
		// this is only to follow the terrain relief
		double angle = Math.acos(pu.getDotProduct(pv) / (pu.getNorm() * pv.getNorm()));
		r = r.mult(new Quaternion().fromAngles((float) (-angle+AngleUtil.RIGHT+model.pitchFix.getValue()), (float) (model.rollFix.getValue()), (float) (model.yawFix.getValue())));

		s.setLocalRotation(r);
	}
}
