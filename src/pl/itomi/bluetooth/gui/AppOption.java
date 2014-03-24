package pl.itomi.bluetooth.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

public enum AppOption {
	
	LookAndFeel("landf", OptionType.VARIABLE_OPT, Arrays.asList("systemlf", "javalf") );
	
	
	
	private static final String UNKNOWN_OPTION_VARIABLE = "Unknown option variable.";

	private static final String OPTION_NUMBER_OF_ARGUMENTS = "Inapprpriate number of arguments.";

	private static final String OPTION_NOT_SUPPORTED = "Used option is not accepted by this application.";
	
	private static final String OPTION_IS_PRESENT = "present";

	private static final Map<String, AppOption> optionsMap = new HashMap<String, AppOption>();
	
	public static enum OptionType {
		SINGLE_OPT,
		VARIABLE_OPT;		
	}
	
	private String optionNameString;
	
	private OptionType optionType;
	
	private List<String> acceptedVariables;
	
	AppOption(final String optionString, final OptionType type, final List<String> acceptedVariables) {
		this.optionNameString = optionString;
		this.optionType = type;
		this.setAcceptedVariables(acceptedVariables);
	}
	
	AppOption(final String optionString, final OptionType type) {
		this(optionString, type, (List<String>)null);
	}
	
	public static Map<AppOption, String> process(String[] args) throws UnsupportedOptionException {
		final Map<AppOption, String> mapWithValuesForEachOption = new HashMap<AppOption, String>();
		
		for( int i = 0 ; i < args.length ; i++ ) {
			AppOption option = optionsMap.get(args[i]);
			if(option != null) {
				switch(option.getOptionType()) {
				case SINGLE_OPT:
					mapWithValuesForEachOption.put(option, OPTION_IS_PRESENT);
					break;
				case VARIABLE_OPT:
					if(args.length - 1 == i) {
						throw new UnsupportedOptionException(OPTION_NUMBER_OF_ARGUMENTS);
					} else {
						 mapWithValuesForEachOption.put(option, validateOption(option, args[i+1]));
					}
					break;
				default:
					throw new IllegalStateException();
				}
				i++;
				continue;
			}
			throw new UnsupportedOptionException(OPTION_NOT_SUPPORTED);
		}
		
		return mapWithValuesForEachOption;
	}	
	
	private static String validateOption(AppOption option, String string) throws UnsupportedOptionException {
		if( option.getAcceptedVariables().contains(string)) {
			return string;
		} else {
			throw new UnsupportedOptionException(UNKNOWN_OPTION_VARIABLE);
		}
	}

	public OptionType getOptionType() {
		return optionType;
	}

	public List<String> getAcceptedVariables() {
		return acceptedVariables;
	}

	public void setAcceptedVariables(List<String> acceptedVariables) {
		this.acceptedVariables = acceptedVariables;
	}

	static {
		for( final AppOption option : AppOption.values() ) {
			optionsMap.put(option.optionNameString, option);
		}
	}
}
