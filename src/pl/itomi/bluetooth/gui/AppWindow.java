package pl.itomi.bluetooth.gui;

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
		JLabel services = new JLabel("Not empty");
		JTextArea serviceLog = new JTextArea(30, 50);
		
		JScrollPane scrollPane = createDeviceListScrollPanel();
		JScrollPane servicePane = createServiceScrollPanel();
		
		bar.setIndeterminate(true);
		bar.setVisible(false);
		
		mainPanel.add(b);
		mainPanel.add(scrollPane);
		mainPanel.add(services);
		mainPanel.add(bar);
		mainPanel.add(servicePane);
		
		
		servicePane.setPreferredSize(new Dimension(250, 250));
		scrollPane.setPreferredSize(new Dimension(250, 250));
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
				label.setText(value.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false));
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
				// TODO: connection here somhow
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
					name = device.getFriendlyName(false);
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
		JProgressBar bar = (JProgressBar) this.mainPanel.getComponent(3);
		
		bar.setVisible(true);
		
	}
	
	public void completed() {
		JProgressBar bar = (JProgressBar) this.mainPanel.getComponent(3);
		
		bar.setVisible(false);
		
	}

	public void addService(ServiceRecord service) {
		serviceRecordListModel.addElement(service);
	}
	
	public void setStatusLabel(String string) {
		JLabel label = (JLabel) this.mainPanel.getComponent(2);
		label.setText(string);
	}

}
