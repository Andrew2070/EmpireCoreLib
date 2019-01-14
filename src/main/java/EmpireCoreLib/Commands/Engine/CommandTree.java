package EmpireCoreLib.Commands.Engine;


import java.util.List;
import java.util.UUID;

import EmpireCoreLib.Localization.Localization;
import EmpireCoreLib.Permissions.IPermissionBridge;
import EmpireCoreLib.Permissions.Node;
import EmpireCoreLib.Proxies.PermissionProxy;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockBaseLogic;

public class CommandTree extends Node {

	private Localization local;
	private IPermissionBridge customManager;

	public CommandTree(Node root, Localization local) {
		super(root);
		this.local = local;
	}

	public CommandTree(Node root, Localization local, IPermissionBridge customManager) {
		this(root, local);
		this.customManager = customManager;
	}

	public void commandCall(ICommandSender sender, List<String> args) {
		Node node = super.getParent();
		while (!args.isEmpty() && node.getChild(args.get(0)) != null) {
			node = node.getChild(args.get(0));
			args = args.subList(1, args.size());
		}

		try {
			if (hasPermission(sender, node)) {
				node.commandCall(sender, args);
			}
		} catch (CommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Node getNodeFromArgs(List<String> args) {
		Node child = super.getParent();
		while (!args.isEmpty() && child.getChild(args.get(0)) != null) {
			child = child.getChild(args.get(0));
			args = args.subList(1, args.size());
		}
		return child;
	}

	public int getArgumentNumber(List<String> args) {
		Node current = super.getParent();
		while (!args.isEmpty() && current.getChild(args.get(0)) != null) {
			current = current.getChild(args.get(0));
			args = args.subList(1, args.size());
		}

		return args.size() - 1;
	}

	public boolean hasCommandNode(String perm) {
		return hasCommandNode(super.getParent(), perm);
	}

	public boolean hasCommandNode(Node current, String perm) {
		if (perm.equals(current.getAnnotation().permission()))
			return true;

		boolean exists = false;
		for (Node child : current.getChildren()) {
			if (hasCommandNode(child, perm))
				return true;
		}
		return false;
	}

	public boolean hasPermission(ICommandSender sender, Node node) throws CommandException {
		if (!node.getAnnotation().console() && (sender instanceof MinecraftServer || sender instanceof RConConsoleSource
				|| sender instanceof CommandBlockBaseLogic)) {
			throw new CommandException("commands.generic.permission");
		}

		if (sender instanceof EntityPlayer) {
			UUID uuid = ((EntityPlayer) sender).getUniqueID();
			String permission = node.getAnnotation().permission();

			if (PermissionProxy.getPermissionManager().hasPermission(uuid, permission)
					|| (customManager != null && customManager.hasPermission(uuid, permission))) {
				return true;
			}
			throw new CommandException("commands.generic.permission");
		}
		return true;
	}

	public Localization getLocal() {
		return local;
	}
}