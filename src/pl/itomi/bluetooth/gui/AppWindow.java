package pl.itomi.bluetooth.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.itomi.bluetooth.devicecontroller.BluetoothController;

public class AppWindow extends JFrame implements ActionListener {

	private static final String EMPTY = "";

	private static final String BUTTON_DISCOVER = "Discover";

	private static final long serialVersionUID = 1L;

	private static final String BLUETOOTH_ANALYZER = "Bluetooth analyzer";
	
	private static final Map<AppOption, String> appOptions = new HashMap<AppOption, String>();
	
	private final JPanel mainPanel = new JPanel();
	
	private final DefaultListModel<RemoteDevice> deviceListModel = new DefaultListModel<RemoteDevice>();
	
	private final DefaultListModel<ServiceRecord> serviceRecordListModel = new DefaultListModel<>();
	
	private final ConcurrentHashMap<Long, RemoteDevice> discoveredDevices = new ConcurrentHashMap<>();
	
	private final BluetoothController btController = new BluetoothController(this, discoveredDevices);

	
	public AppWindow() {
		super(BLUETOOTH_ANALYZER);
	}
	
	public AppWindow processArguments(String[] args) {
		try {
			appOptions.putAll(AppOption.process(args));
		} catch (UnsupportedOptionException e) {
			System.out.println(e.getMessage());
		}
		
		AppOptionsInterperter optionsInterpreter = new AppOptionsInterperter(appOptions);
		
		optionsInterpreter.setOptionsForWindow(this);
		
		return this;
	}

	public void start() {
		this.setContentPane(mainPanel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(640, 640);
		this.setVisible(true);
	}

	public AppWindow initGui() {		
		JButton b = new JButton(BUTTON_DISCOVER);
		JProgressBar bar = new JProgressBar();
		JLabel statusLabel = new JLabel("Not empty");
		JScrollPane devicePane = createDeviceListScrollPanel();
		JScrollPane servicePane = createServiceScrollPanel();
		
		JPanel statusPanel = new JPanel(new BorderLayout());
		
		statusPanel.add(BorderLayout.EAST, bar);
		statusPanel.add(BorderLayout.WEST, statusLabel);
		
		bar.setVisible(true);
		bar.setValue(0);
		
		mainPanel.setLayout(new BorderLayout());
		
		mainPanel.add(BorderLayout.NORTH, b);
		mainPanel.add(BorderLayout.WEST, devicePane);
		mainPanel.add(BorderLayout.EAST, servicePane);
		mainPanel.add(BorderLayout.SOUTH, statusPanel);
		
		//servicePane.setPreferredSize(new Dimension(400, 250));
		//devicePane.setPreferredSize(new Dimension(250, 250));
		b.addActionListener(this);
		
		return this;
	}

	private JScrollPane createServiceScrollPanel() {
		final JList<ServiceRecord> serviceList = new JList<>(serviceRecordListModel);
		JScrollPane servicePane = new JScrollPane(serviceList);
		ListCellRenderer<ServiceRecord> serviceRenderer = createServiceCellRenderer();
		
		serviceList.setCellRenderer(serviceRenderer);
		
		ListSelectionListener serviceListListener = createServiceSelectionListener(serviceList);
		serviceList.addListSelectionListener(serviceListListener );
		return servicePane;
	}

	private ListCellRenderer<ServiceRecord> createServiceCellRenderer() {
		ListCellRenderer<ServiceRecord> serviceRenderer = new ListCellRenderer<ServiceRecord>() {
			
			@Override
			public Component getListCellRendererComponent(
					JList<? extends ServiceRecord> list, ServiceRecord value,
					int index, boolean isSelected, boolean cellHasFocus) {
				JLabel label = new JLabel();
				String str = list.getModel().getElementAt(index).getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
				if( str == null ) {
					str = "error";
				}
				label.setText(str);
				return label;
			}
		};
		return serviceRenderer;
	}

	private ListSelectionListener createServiceSelectionListener(
			final JList<ServiceRecord> serviceList) {
		ListSelectionListener serviceListListener = new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				ServiceRecord selected = serviceList.getSelectedValue();
				boolean mustBeMaster = JOptionPane.showConfirmDialog(null, "Request master connection?", "Connection setting", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
				String url = selected.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, mustBeMaster);
				btController.connectToDevice(url);
			}
		};
		return serviceListListener;
	}

	private JScrollPane createDeviceListScrollPanel() {
		final JList<RemoteDevice> list = new JList<RemoteDevice>(deviceListModel);
		ListSelectionListener listListener = craeteDeviceListSelectionListener(list);
		ListCellRenderer<RemoteDevice> renderer = createListCellRenderer();		
		
		list.setCellRenderer(renderer);
		list.addListSelectionListener(listListener);
		
		JScrollPane scrollPane = new JScrollPane(list);
		return scrollPane;
	}

	private ListSelectionListener craeteDeviceListSelectionListener(
			final JList<RemoteDevice> list) {
		ListSelectionListener listListener = new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				RemoteDevice device = (RemoteDevice) list.getSelectedValue();
				serviceRecordListModel.clear();
				btController.provideServices(device);
			}
		};
		return listListener;
	}

	private ListCellRenderer<RemoteDevice> createListCellRenderer() {
		ListCellRenderer<RemoteDevice> renderer = new ListCellRenderer<RemoteDevice>() {
			@Override
			public Component getListCellRendererComponent(
					JList<? extends RemoteDevice> list, RemoteDevice value,
					int index, boolean isSelected, boolean cellHasFocus) {
				RemoteDevice device = (RemoteDevice) list.getModel().getElementAt(index);
				String name = "error";
				try {
					name = device.getFriendlyName(false) + " - " + device.getBluetoothAddress();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return new JLabel(name);
			}

		};
		return renderer;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == mainPanel.getComponent(0)) {
			System.out.println("Starting discovery");
			btController.provideDevices();
		}
	}
	
	public void updateView() {
		deviceListModel.clear(); 
		
		//TODO : lets say we have 12391238 devices, this impl is ineficient
		//		 there should be cached version and comparsion of list and removing and adding bt devices
		for( final Long cardinalNumber : discoveredDevices.keySet() ) {
			deviceListModel.addElement(discoveredDevices.get(cardinalNumber));
		}
		
	}

	public void begin() {
		JPanel panel = (JPanel) this.mainPanel.getComponent(3);
		JProgressBar bar = (JProgressBar) panel.getComponent(0);
		
		bar.setIndeterminate(true);
		
		
	}
	
	public void completed() {
		JPanel panel = (JPanel) this.mainPanel.getComponent(3);
		JProgressBar bar = (JProgressBar) panel.getComponent(0);
	
		bar.setIndeterminate(false);
		bar.setValue(0);
		
	}

	public void addService(ServiceRecord service) {
		serviceRecordListModel.addElement(service);
	}
	
	public void setStatusLabel(String string) {
		JPanel panel = (JPanel) this.mainPanel.getComponent(3);
		JLabel label = (JLabel) panel.getComponent(1);
	
		label.setText(string);
	}

}

