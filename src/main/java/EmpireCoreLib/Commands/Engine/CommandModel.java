package EmpireCoreLib.Commands.Engine;


import java.util.Arrays;
import java.util.List;

import EmpireCoreLib.Permissions.Node;
import EmpireCoreLib.Permissions.PermissionLevel;
import EmpireCoreLib.Permissions.PermissionObject;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

/**
 * Command model which instantiates all base commands that need to be registered
 * to Minecraft
 */

public class CommandModel implements ICommand {

	private CommandTree commandTree;

	public CommandModel(CommandTree commandTree) {
		this.commandTree = commandTree;
	}

	@Override
	public List getAliases() {
		return Arrays.asList(commandTree.getParent().getAnnotation().alias());
	}

	@Override
	public String getName() {
		return commandTree.getParent().getLocalizedName();
	}
	
	@Override
	public String getUsage(ICommandSender sender) {
		return commandTree.getParent().getLocalizedSyntax();
	}
	
	public String getPermissionNode() {
		return commandTree.getParent().getAnnotation().permission();
	}

	/**
	 * Processes the command by calling the method that was linked to it.
	 */

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
			Node node = commandTree.getNodeFromArgs(Arrays.asList(args));

		int argumentNumber = commandTree.getArgumentNumber(Arrays.asList(args));
		if (argumentNumber < 0)
			return null;

		return node.getTabCompletionList(argumentNumber, args[args.length - 1]);
	}
	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		commandTree.commandCall(sender, Arrays.asList(args));
	}
	/**
	 * This method does not have enough arguments to check for subcommands down
	 * the command trees therefore it always returns true. The check is moved
	 * directly to the processCommand method.
	 */

	@Override
	public int compareTo(ICommand o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		// TODO Auto-generated method stub
		return false;
	}

}
