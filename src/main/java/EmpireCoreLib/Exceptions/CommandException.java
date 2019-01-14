package EmpireCoreLib.Exceptions;


public class CommandException extends FormattedException {

	public CommandException(String localizationKey, Object... args) {
		super(localizationKey, args);
	}
}
