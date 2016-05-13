package com.brainless.alchemist.presentation.base;

import java.util.ArrayList;
import java.util.List;

import com.brainless.alchemist.model.ECS.blueprint.Blueprint;
import com.brainless.alchemist.model.ECS.builtInComponent.Parenting;
import com.brainless.alchemist.presentation.common.EntityNode;
import com.simsilica.es.EntityComponent;

public class AbstractPresenter<T extends Viewer> {
	protected final T viewer;

	public AbstractPresenter(T viewer) {
		this.viewer = viewer;
	}

	public Blueprint createBlueprint(EntityNode ep) {
		// we have to ignore the parenting component, for it is created from the blueprint tree
		List<EntityComponent> comps = new ArrayList<>();
		for (EntityComponent comp : ep.componentListProperty()) {
			if (!(comp instanceof Parenting)) {
				comps.add(comp);
			}
		}

		List<Blueprint> children = new ArrayList<>();
		for (EntityNode child : ep.childrenListProperty()) {
			children.add(createBlueprint(child));
		}

		return new Blueprint(ep.nameProperty().getValue(), comps, children);
	}
}
