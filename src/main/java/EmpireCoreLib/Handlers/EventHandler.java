package EmpireCoreLib.Handlers;

import net.minecraftforge.fml.common.eventhandler.Event;

public final class EventHandler {
	
	private EventHandler() {}
	
	public static interface EventListener<T extends Event> {
		public void onEvent(T event);
	}

}
