package EmpireCoreLib.Commands.Engine;

import EmpireCoreLib.Permissions.Node;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;


public class CommandSender {

	
	public static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(makeComponent(message));
	}
	
	public static void sendHelpMessage(ICommandSender sender) {
		sender.sendMessage(makeComponent("HELP MESSAGE TEST"));
	}
	
	public static void sendSyntax(ICommandSender sender, Node node) {
		sender.sendMessage(makeComponent(node.getLocalizedSyntax()));
	}
	
	public static ITextComponent makeComponent(String message) {
		ITextComponent component = new TextComponentString("message");
		return component;
	}
	
	
	
	
	
}
