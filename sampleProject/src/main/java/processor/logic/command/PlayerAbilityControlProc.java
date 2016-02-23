package processor.logic.command;


import com.jme3.app.state.AppStateManager;
import com.simsilica.es.Entity;

import commonLogic.CommandState;
import component.ability.AbilityTrigger;
import component.ability.PlayerControl;
import model.Command;
import model.ECS.pipeline.Processor;
import model.state.DataState;

public class PlayerAbilityControlProc extends Processor {

	private Command command; 
	
	@Override
	protected void onInitialized(AppStateManager stateManager) {
		command = stateManager.getState(CommandState.class).getCommand();
	}
	
	@Override
	protected void registerSets() {
		registerDefault(AbilityTrigger.class, PlayerControl.class);
	}
	
	@Override
	protected void onEntityEachTick(Entity e) {
		AbilityTrigger triggers = e.get(AbilityTrigger.class);
		triggers.triggers.clear();
		for(String abilityName : command.abilities){
			triggers.triggers.put(abilityName, true);
		}
	}
}