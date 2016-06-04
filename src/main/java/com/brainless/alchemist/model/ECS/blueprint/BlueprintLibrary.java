package com.brainless.alchemist.model.ECS.blueprint;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import util.exception.TechnicalException;

/**
 * Singleton class that provide access to blueprints on the file system.
 * It deserializes all blueprints file found in "assets/data/blueprints/" at application start,
 * and can save a blue print at the same place.
 *
 * @author benoit
 *
 */
public class BlueprintLibrary {
	private static URI uri;
	private static final String EXTENSION = ".blueprint";
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final Map<String, Blueprint> blueprintMap;

	/**
	 * Eager deserialization of the blueprints found in PATH see <a href="https://github.com/FasterXML/jackson-databind/wiki/JacksonFeatures">here </a>for
	 * documentation
	 */
	static {
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		blueprintMap = new HashMap<>();
		try {
			uri = BlueprintLibrary.class.getResource("/data/blueprints/").toURI();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Private constructor for singleton pattern
	 */
	private BlueprintLibrary(){

	}

	/**
	 * Save the given blueprint in a file stored in the default folder.
	 * @param bp
	 */
	public static void saveBlueprint(Blueprint bp){
		try {
			mapper.writeValue(new File(uri.getPath(), bp.getName() + EXTENSION), bp);
			blueprintMap.put(bp.getName(), bp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void loadBlueprints(){
		for (File f : getFilesDeeply(uri.getPath())) {
			try {
				Blueprint bp = mapper.readValue(f, Blueprint.class);
				blueprintMap.put(bp.getName(), bp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static ArrayList<File> getFilesDeeply(String folderPath) {
		ArrayList<File> res = new ArrayList<>();
		File folder = new File(folderPath);
		if (!folder.exists()) {
			throw new RuntimeException("the folder " + folder.getAbsolutePath() + " was not found.");
		}
		for (File f : folder.listFiles()) {
			if(f.isDirectory()) {
				res.addAll(getFilesDeeply(f.getPath()));
			} else {
				res.add(f);
			}
		}
		return res;
	}

	/**
	 * Returns a list containing all blueprints
	 * @param name
	 * @return
	 */
	public static List<Blueprint> getAllBlueprints(){
		if (blueprintMap.isEmpty()) {
			loadBlueprints();
		}
		return new ArrayList<>(blueprintMap.values());
	}

	/**
	 * Returns a specific blueprint
	 * @param name
	 * @return the blueprint, or null if no such blueprint exists in the library
	 */
	public static Blueprint getBlueprint(String name){
		if (blueprintMap.isEmpty()) {
			loadBlueprints();
		}
		return blueprintMap.get(name);
	}

	public static URI getUri() {
		return uri;
	}

	public static void setUri(URI url) {
		BlueprintLibrary.uri = url;
		if (url.getPath() == null) {
			throw new TechnicalException("the path must be exist" + url);
		}

	}

}
