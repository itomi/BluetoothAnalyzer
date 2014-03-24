package pl.itomi.bluetooth;

import pl.itomi.bluetooth.gui.AppWindow;

public class EntryPoint {

	public static void main(String[] args) {
		(new AppWindow()).processArguments(args).initGui().start();
	}

}
