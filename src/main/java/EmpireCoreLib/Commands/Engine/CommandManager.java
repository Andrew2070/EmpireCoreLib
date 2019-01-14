package EmpireCoreLib.Commands.Engine;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import EmpireCoreLib.EmpireCoreLib;
import EmpireCoreLib.Exceptions.CommandException;
import EmpireCoreLib.Localization.Localization;
import EmpireCoreLib.Permissions.IPermissionBridge;
import EmpireCoreLib.Permissions.Node;
import net.minecraft.command.ICommandSender;

public class CommandManager {

	/**
	 * Registrar used to register any commands. Offers compatibility for Bukkit
	 * and ForgeEssentials
	 */
	private static final ICommandRegistrar registrar = makeRegistrar();

	private static final List<CommandTree> commandTrees = new ArrayList<CommandTree>();

	public static final String ROOT_PERM_NODE = "ROOT";

	private CommandManager() {
	}

	/**
	 * It is enforced that the class has to contain ONE root command .
	 */
	public static void registerCommands(Class clazz, String rootPerm, Localization local) {
		
		
		Node root = null;
		CommandTree commandTree = rootPerm == null ? null : getTree(rootPerm);

		Map<Command, Method> nodes = new HashMap<Command, Method>();

		for (final Method method : clazz.getDeclaredMethods()) {
			if (method.isAnnotationPresent(Command.class)) {
				if (isMethodValid(method)) {
					Command command = method.getAnnotation(Command.class);
					if (command.parentName().equals(ROOT_PERM_NODE)) {
						if (commandTree == null) {
							root = new Node(command, method);
						} else {
							throw new CommandException("Class " + clazz.getName() + " has more than one root command.");
						}
					} else {
						nodes.put(command, method);
					}
				} else {
					EmpireCoreLib.logger.info(("Method " + method.getName() + " from class " + clazz.getName()
							+ " is not valid for command usage"));
				}
			}
		}

		if (commandTree == null) {
			if (root == null) {
				throw new CommandException("Class " + clazz.getName() + " has no root command.");
			} else {
				commandTree = new CommandTree(root, local);
				commandTrees.add(commandTree);
			}
		}

		registrar.registerCommand(new CommandModel(commandTree), commandTree.getParent().getAnnotation().permission(),
				false);

		constructTree(commandTree.getParent(), nodes);

		for (Map.Entry<Command, Method> entry : nodes.entrySet()) {
			EmpireCoreLib.logger.info("Missing parent: " + entry.getKey().permission() + " |<-| " + entry.getKey().parentName());
		}
	}

	public static CommandTree getTree(String basePerm) {
		for (CommandTree tree : commandTrees) {
			if (tree.getParent().getAnnotation().permission().equals(basePerm))
				return tree;
		}
		return null;
	}

	public static CommandTree getTreeFromPermission(String perm) {
		for (CommandTree tree : commandTrees) {
			if (tree.hasCommandNode(perm)) {
				return tree;
			}
		}
		return null;
	}

	public static String getPermForCommand(String commandName) {
		for (CommandTree tree : commandTrees) {
			if (tree.getParent().getLocalizedName().equals(commandName)) {
				return tree.getParent().getAnnotation().permission();
			}
		}
		return null;
	}

	private static Node findNode(Node root, String perm) {
		if (root.getAnnotation().permission().equals(perm))
			return root;

		for (Node child : root.getChildren()) {
			Node foundNode = findNode(child, perm);
			if (foundNode != null)
				return foundNode;
		}
		return null;
	}

	private static void constructTree(Node root, Map<Command, Method> nodes) {
		int currentNodeNumber;
		do {
			currentNodeNumber = nodes.size();
			for (Iterator<Map.Entry<Command, Method>> it = nodes.entrySet().iterator(); it.hasNext();) {
				Map.Entry<Command, Method> entry = it.next();

				Node parent = findNode(root, entry.getKey().parentName());

				if (parent != null) {
					parent.addChild(new Node(parent, entry.getKey(), entry.getValue()));
					if (!root.getLocal().hasLocalization(entry.getKey().permission() + ".help")) {
						EmpireCoreLib.logger.info("Missing help: " + entry.getKey().permission() + ".help");
					}
					it.remove();
				}
			}
		} while (currentNodeNumber != nodes.size());
	}

	private static boolean isMethodValid(Method method) {
		if (!method.getReturnType().equals(CommandResponse.class))
			return false;

		if (method.getParameterTypes().length != 2)
			return false;

		if (!(method.getParameterTypes()[0].equals(ICommandSender.class)
				&& method.getParameterTypes()[1].equals(List.class)))
			return false;

		return true;
	}

	private static ICommandRegistrar makeRegistrar() {
			return new VanillaCommandRegistrar();
		}
	}
