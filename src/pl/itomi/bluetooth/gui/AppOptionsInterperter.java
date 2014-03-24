package pl.itomi.bluetooth.gui;

import java.util.List;
import java.util.Map;

import javax.swing.UIManager;

public class AppOptionsInterperter {

	private final Map<AppOption, String> options;

	public AppOptionsInterperter(final Map<AppOption, String> options) {
		this.options = options;
	}

	public void setOptionsForWindow(final AppWindow appWindow) {
		for(final AppOption entry : options.keySet()) {
			dispatchOption(appWindow, entry);
		}
	}

	private void dispatchOption(final AppWindow appWindow, final AppOption entry) {
		switch (entry) {
		case LookAndFeel:
			setLookAndFeel(appWindow, options.get(entry));
			break;
		default:
			break;

		}
	}

	private void setLookAndFeel(AppWindow appWindow, String string) {
		List<String> accepted = AppOption.LookAndFeel.getAcceptedVariables();
		
		try {
			if( string.equals(accepted.get(0))) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} else if (string.equals(accepted.get(1))) {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
