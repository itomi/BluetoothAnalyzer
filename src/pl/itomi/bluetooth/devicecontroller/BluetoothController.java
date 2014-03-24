package pl.itomi.bluetooth.devicecontroller;
import java.util.concurrent.ConcurrentHashMap;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

import pl.itomi.bluetooth.gui.AppWindow;

public class BluetoothController {

	private LocalDevice localDevice;
	
	private DiscoveryAgent discoveryAgent;
	
	private DiscoveryListener listener;
	
	private AppWindow appWindow;
	
	private ConcurrentHashMap<Long, RemoteDevice> discoveredDevices;

	private long updateRate = 1000;

	protected Long deviceNum = 0L;
	
	public BluetoothController(final AppWindow appWindow, final ConcurrentHashMap<Long, RemoteDevice> discoveredDevices) {
		
		this.appWindow = appWindow;
		this.discoveredDevices = discoveredDevices;
		
		 listener = new DiscoveryListener() {
			
			@Override
			public void servicesDiscovered(int arg0, ServiceRecord[] servicesArray) {
				for( final ServiceRecord record : servicesArray ) {
					appWindow.addService(record);
				}
			}
			
			@Override
			public void serviceSearchCompleted(int arg0, int responseCode) {
				switch(responseCode) {
					case SERVICE_SEARCH_COMPLETED:
						appWindow.setStatusLabel("Completed");
						break;
					case SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
						appWindow.setStatusLabel("Device not reachable");
						break;
					case SERVICE_SEARCH_ERROR:
						appWindow.setStatusLabel("Error");
						break;
					case SERVICE_SEARCH_NO_RECORDS:
						appWindow.setStatusLabel("No records found");
						break;
					case SERVICE_SEARCH_TERMINATED:
						appWindow.setStatusLabel("Terminated");
						break;
					default:
						break;
				}
				appWindow.completed();	
			}
			
			@Override
			public void inquiryCompleted(int arg0) {
				appWindow.completed();
			}
			
			@Override
			public void deviceDiscovered(RemoteDevice device, DeviceClass deviceClass) {
				discoveredDevices.put((deviceNum), device);
				
				deviceNum+=1;
				appWindow.updateView();
			}
		};
		
		try {
			this.localDevice = LocalDevice.getLocalDevice();
			this.discoveryAgent = localDevice.getDiscoveryAgent();
		} catch (BluetoothStateException e) {
			e.printStackTrace();
		}
		
		
				
	}
	
	public void provideDevices() {
		appWindow.begin();
		discoveredDevices.clear();
		
		try {
			this.discoveryAgent.startInquiry(DiscoveryAgent.GIAC , this.listener);
		} catch (BluetoothStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
	
	public void provideServices(RemoteDevice remoteDevice) {
		final int[] attrSet = { 0x0000, 0x0001, 0x0002};
		final UUID[] services = {BluetoothService.RFCOMM.UUID()};//BluetoothService.getAll() ;
		appWindow.begin();
		try {
			this.discoveryAgent.searchServices(attrSet, services, remoteDevice , this.listener);
		} catch (BluetoothStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
