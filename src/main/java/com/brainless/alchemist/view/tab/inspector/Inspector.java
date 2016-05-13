package com.brainless.alchemist.view.tab.inspector;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.brainless.alchemist.presentation.common.EntityNode;
import com.brainless.alchemist.presentation.inspector.InspectorPresenter;
import com.brainless.alchemist.presentation.inspector.InspectorViewer;
import com.brainless.alchemist.view.tab.inspector.customControl.ComponentEditor;
import com.brainless.alchemist.view.util.ViewLoader;
import com.simsilica.es.EntityComponent;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class Inspector extends BorderPane implements InspectorViewer {

	protected final InspectorPresenter presenter;

	protected final Map<Class<? extends EntityComponent>, ComponentEditor> editors = new HashMap<>();

	@FXML
	protected VBox componentBox;

	@FXML
	protected Label infoLabel;

	@FXML
	protected Button addButton;

	public Inspector() {
		presenter = new InspectorPresenter(this);
		ViewLoader.loadFXMLForControl(this);
	}

	@FXML
	protected void initialize() {
		addButton.setOnAction(e -> showComponentChooser());
		inspectNewEntity(null);
	}

	@Override
	public void inspectNewEntity(EntityNode ep){
		componentBox.getChildren().clear();
		editors.clear();
		infoLabel.textProperty().unbind();

		addButton.visibleProperty().setValue(ep != null);

		if(ep == null){
			infoLabel.setText("No entity selected");
		} else {
			for(EntityComponent comp : ep.componentListProperty()) {
				addComponentEditor(comp);
			}
			infoLabel.textProperty().bind(ep.nameProperty());
		}
	}

	@Override
	public void updateComponentEditor(EntityComponent comp){
		ComponentEditor editor = editors.get(comp.getClass());
		if(editor.isExpanded()) {
			editor.updateComponent(comp);
		}
	}

	@Override
	public void addComponentEditor(EntityComponent comp){
		ComponentEditor editor = new ComponentEditor(comp,
				compClass -> presenter.removeComponent(compClass),
				(c, propertyName, value) -> presenter.updateComponent(c, propertyName, value));
		editors.put(comp.getClass(), editor);
		componentBox.getChildren().add(editor);
	}

	@Override
	public void removeComponentEditor(EntityComponent comp){
		ComponentEditor editor = editors.get(comp.getClass());
		editors.remove(comp.getClass());
		componentBox.getChildren().remove(editor);
	}

	protected void showComponentChooser() {
		ChoiceDialog<String> dialog = new ChoiceDialog<>(presenter.getComponentNames().get(0), presenter.getComponentNames());
		dialog.setTitle("Component choice".toUpperCase());
		dialog.setHeaderText(null);
		dialog.setContentText("Choose the component in the list :");
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(compName -> presenter.addComponent(compName));
	}
}
