package EmpireCoreLib.Permissions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import EmpireCoreLib.EmpireCoreLib;
import EmpireCoreLib.Commands.Engine.Command;
import EmpireCoreLib.Commands.Engine.CommandCompletion;
import EmpireCoreLib.Commands.Engine.CommandManager;
import EmpireCoreLib.Commands.Engine.CommandResponse;
import EmpireCoreLib.Commands.Engine.CommandTree;
import EmpireCoreLib.Exceptions.CommandException;
import EmpireCoreLib.Localization.Localization;
import EmpireCoreLib.Utilities.StringUtilities;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class Node {
	
	Node 							parent				= null;
	String 							NodeName		    = "";
	String							Description			= "";
	String 							localizationKey;
	Command 						commandAnnot;
	Method 							method;
	DefaultPermissionLevel 			Permissionlevel		= DefaultPermissionLevel.NONE;
	protected List<Node>			children 			= new ArrayList<Node>();
	
	private Supplier<String> Name = Suppliers.memoizeWithExpiration(new Supplier<String>() {
		@Override
		public String get() {
			String key = getLocalizationKey() + ".name";
			return getLocal().hasLocalization(key) ? getLocal().getLocalization(key).getUnformattedText()
					: getAnnotation().name();
		}
	}, 5, TimeUnit.MINUTES);
	
	private Supplier<String> syntax = Suppliers.memoizeWithExpiration(new Supplier<String>() {
		@Override
		public String get() {
			String key = getLocalizationKey() + ".syntax";
			return getLocal().hasLocalization(key) ? getLocal().getLocalization(key).getUnformattedText()
					: getAnnotation().syntax();
		}
	}, 5, TimeUnit.MINUTES);
	
	public Node(Command commandAnnot, Method method) {
		this(null, commandAnnot, method);
	}
	
	public Node(Node parent, Command commandAnnot, Method method) {
		this.parent = parent;
		this.commandAnnot = commandAnnot;
		this.method = method;
		String name = getAnnotation().name();
		Node parentNode = this;
		while ((parentNode = new Node(parentNode.getParent(), commandAnnot, method)) != null) {
			name = parentNode.getAnnotation().name() + "." + name;
		}
		localizationKey = "command." + name;
	}
	
	public Node(Node parent, String nodename, String description, DefaultPermissionLevel level) {
		
		setParent(parent);
		setNodeName(nodename);
		setDescription(description);
		setPermissionLevel(level);	
	}
	
	public Node(Node node) {
		this.NodeName = node.getName();
		this.Description = node.getDescription();
		this.parent = node.getParent();
		this.localizationKey = node.getLocalizationKey();
		this.commandAnnot = node.getAnnotation();
		this.Permissionlevel = node.getPermissionLevel();
		this.children.addAll(node.getChildren());
		this.method = node.getMethod();
		this.RootNode = node.getRootNode();
	}
	
	public void commandCall(ICommandSender sender, List<String> args) {

		/*
		 * // Check if the player has access to the command using the
		 * firstpermissionbreach method first Method permMethod =
		 * firstPermissionBreaches.get(permission); if(permMethod != null) {
		 * Boolean result = true; try { result =
		 * (Boolean)permMethod.invoke(null, permission, sender); } catch
		 * (Exception e) {
		 * Empires.instance.LOG.error(ExceptionUtils.getStackTrace(e)); }
		 * if(!result) { // If the first permission breach did not allow the
		 * method to be called then call is aborted throw new
		 * CommandException("commands.generic.permission"); } }
		 */

		try {
			CommandResponse response = (CommandResponse) method.invoke(null, sender, args);
			if (response == CommandResponse.SEND_HELP_MESSAGE) {
				int page = 1;
				if (!args.isEmpty() && StringUtilities.tryParseInt(args.get(0)))
					page = Integer.parseInt(args.get(0));
				sendHelpMessage(sender, page);
				
			} else if (response == CommandResponse.SEND_SYNTAX) {
				sendSyntax(sender);
			}
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof CommandException) {
				//ChatManager.send(sender, ((CommandException) e.getCause()).message);
			} else if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			} else {
				EmpireCoreLib.logger.info(ExceptionUtils.getStackTrace(e));
			}
		} catch (IllegalAccessException e) {
			EmpireCoreLib.logger.info(ExceptionUtils.getStackTrace(e));
		}
	}

	//Set:
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public void setNodeName(String nodeName) {
		this.NodeName = nodeName;
	}
	public void setDescription(String description) {
		this.Description = description;
	}
	
	public void setPermissionLevel(DefaultPermissionLevel level) {
		this.Permissionlevel = level;
	}
	public void addChild(Node child) {
		children.add(child);
		child.parent = this;
	}
	//Get:
	public Node getParent() {
		return this.parent;
	}
	
	public List<Node> getChildren() {
		return children;
	}
	
	public String getChildFromName(String name) {
		for (int i=0; i<children.size(); i++) {
			Node child = children.get(i);
			
			if (child.getName() == name) {
				return child.getName();
			}
		}
		return null;
	}
	
	public List<String> getTabCompletionList(int argumentNumber, String argumentStart) {
		List<String> completion = new ArrayList<String>();
		if (commandAnnot.completionKeys().length == 0) {
			for (Node child : getChildren()) {
				String localizedCommand = child.getLocalizedName();
				if (localizedCommand.startsWith(argumentStart)) {
					completion.add(localizedCommand);
				}
			}
		} else {
			if (argumentNumber < commandAnnot.completionKeys().length) {
				for (String s : CommandCompletion.getCompletionList(commandAnnot.completionKeys()[argumentNumber])) {
					if (s.startsWith(argumentStart)) {
						completion.add(s);
					}
				}
			}
		}
		return completion;
	}
	
	public String getLocalizedName() {
		return Name.get();
	}
	
	public String getLocalizationKey() {
		return localizationKey;
	}
	public Node getChild(String name) {
		for (Node Child : getChildren()) {

			if (Child.getLocalizedName().equals(Child.getName())) {
				return Child;
			} else {
				for (String alias : Child.getAnnotation().alias()) {
					if (alias.equals(name)) {
						return Child;
					}
				}
			}
		}
		return null;
	}

	public String getCommandLine() {
		if (getParent() == null)
			return "/" + getLocalizedName();
		else
			return new Node(this.parent, commandAnnot, method).getCommandLine() + " " + getLocalizedName();
	}

	public Localization getLocal() {
		return getCommandTree().getLocal();
	}

	public Node getCommandTree() {
		Node node = this;

		while (node.getParent() != null) {
			
			node = node.getParent();
		}
		
		return CommandManager.getTree(node.getAnnotation().permission());
	}
	
	public Command getAnnotation() {
		return commandAnnot;
	}

	public Method getMethod() {
		return method;
	}
	
	public String getName() {
		return this.NodeName;
	}

	public String getDescription() {
		return this.Description;
	}
	
	public DefaultPermissionLevel getPermissionLevel() {
		return this.Permissionlevel;
	}
	
	public String fullNodeName() {
		String name = this.parent.toString() + "." + this.NodeName.toString();
		return name;
	}

	public String getLocalizedSyntax() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
