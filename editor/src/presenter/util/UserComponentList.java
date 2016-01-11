package presenter.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.simsilica.es.EntityComponent;

import model.ES.component.Naming;

public class UserComponentList extends HashMap<String, Class<? extends EntityComponent>>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserComponentList() {
		add(Naming.class);
	}
	
	@SafeVarargs
	protected final void add(Class<? extends EntityComponent> ... compClasses){
		for(Class<? extends EntityComponent> compClass : compClasses)
			put(compClass.getSimpleName(), compClass);
	}

	public List<String> getSortedNames(){
		List<String> res = new ArrayList<String>(keySet());
		Collections.sort(res);
		return res;
	}
}
