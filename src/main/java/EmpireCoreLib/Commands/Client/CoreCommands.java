package EmpireCoreLib.Commands.Client;

import java.util.List;

import EmpireCoreLib.Commands.Engine.Command;
import EmpireCoreLib.Commands.Engine.CommandResponse;
import EmpireCoreLib.Commands.Engine.CommandSender;
import net.minecraft.command.ICommandSender;

public class CoreCommands {

	@Command(name = "EmpireCore",
			 alias = {"emp", "Emp", "Empire", "EmpireCore", "empirecore", "empcore", "EMPCORE", "empCore"},
			 syntax = "/empire",
			 console = true,
			 parentName = "",
			 permission = "EmpireCore",
			 description = "EmpireCoreLib Command For All Mods/SubCommands",
			 completionKeys = {"empireCompletionAndAll"})
	public static CommandResponse baseCommand(ICommandSender sender, List<String> args) {
		
		
		CommandSender.sendMessage(sender, "EmpireCoreLib Command!");
		System.out.println("Command Empire Executed Succesfully");	
		
		return CommandResponse.SEND_HELP_MESSAGE;

	}
	
	
	
}
