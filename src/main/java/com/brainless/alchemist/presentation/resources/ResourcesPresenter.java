package com.brainless.alchemist.presentation.resources;

import com.brainless.alchemist.model.ECS.blueprint.Blueprint;
import com.brainless.alchemist.model.ECS.blueprint.BlueprintLibrary;
import com.brainless.alchemist.presentation.base.AbstractPresenter;
import com.brainless.alchemist.presentation.base.BlueprintComparator;
import com.brainless.alchemist.presentation.common.EntityNode;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class ResourcesPresenter extends AbstractPresenter<ResourcesViewer> {

	private final ListProperty<Blueprint> blueprintList;

	public ResourcesPresenter(ResourcesViewer viewer) {
		super(viewer);
		blueprintList = new SimpleListProperty<Blueprint>(FXCollections.observableArrayList());
		for(Blueprint bp : BlueprintLibrary.getAllBlueprints()) {
			blueprintList.add(bp);
		}
		blueprintList.sort(new BlueprintComparator());
	}
	
	public ListProperty<Blueprint> getBlueprintList() {
		return blueprintList;
	}
	
	public void saveEntityAsBlueprint(EntityNode ep){
		Blueprint saved = createBlueprint(ep);
		BlueprintLibrary.saveBlueprint(saved);
		
		for(Blueprint bp : blueprintList) {
			if(bp.getName().equalsIgnoreCase(saved.getName())){
				blueprintList.set(blueprintList.indexOf(bp), saved);
				return;
			}
		}
		// at this point, the saved blueprint doesn't replace any existing blueprint
		blueprintList.add(saved);
		blueprintList.sort(new BlueprintComparator());
	}
	


}
