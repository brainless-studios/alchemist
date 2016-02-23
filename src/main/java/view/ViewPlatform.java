package view;

import java.util.ArrayList;
import java.util.List;

import com.simsilica.es.EntityComponent;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import presentation.common.EntityNode;
import view.common.SceneInputListener;
import view.common.SceneInputManager;

public class ViewPlatform {
	public static final ListProperty<Class<? extends EntityComponent>> expandedComponents = new SimpleListProperty<>(FXCollections.observableArrayList());
	public static final List<EntityNode> expandedEntityNodes = new ArrayList<>();
	public static final ObjectProperty<Scene> JavaFXScene = new SimpleObjectProperty<>();
	private static final SceneInputManager sceneInputManager = new SceneInputManager();

	public static TabPane hierarchyTabPane, inspectorTabPane, resourcesTabPane, sceneViewTabPane;
	public static SceneInputListener camera;
	public static SceneInputListener game = null;

	public static SceneInputManager getSceneInputManager() {
		return sceneInputManager;
	}
}