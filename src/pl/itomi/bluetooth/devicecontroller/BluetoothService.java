package pl.itomi.bluetooth.devicecontroller;

import java.util.ArrayList;
import java.util.List;

import javax.bluetooth.UUID;

public enum BluetoothService {
/*	SDP	0x0001	Bluetooth Core Specification
	UDP	0x0002	[NO USE BY PROFILES]
	RFCOMM	0x0003	RFCOMM with TS 07.10
	TCP	0x0004	[NO USE BY PROFILES]
	TCS-BIN	0x0005	Telephony Control Specification / TCS Binary [DEPRECATED]
	TCS-AT	0x0006	[NO USE BY PROFILES]
	ATT	0x0007	Attribute Protocol
	OBEX	0x0008	IrDA Interoperability
	IP	0x0009	[NO USE BY PROFILES]
	FTP	0x000A	[NO USE BY PROFILES]
	HTTP	0x000C	[NO USE BY PROFILES]
	WSP	0x000E	[NO USE BY PROFILES]
	BNEP	0x000F	Bluetooth Network Encapsulation Protocol (BNEP)
	UPNP	0x0010	Extended Service Discovery Profile (ESDP) [DEPRECATED]
	HIDP	0x0011	Human Interface Device Profile (HID)
	HardcopyControlChannel	0x0012	Hardcopy Cable Replacement Profile (HCRP)
	HardcopyDataChannel	0x0014	See Hardcopy Cable Replacement Profile (HCRP)
	HardcopyNotification	0x0016	Hardcopy Cable Replacement Profile (HCRP)
	AVCTP	0x0017	Audio/Video Control Transport Protocol (AVCTP)
	AVDTP	0x0019	Audio/Video Distribution Transport Protocol (AVDTP)
	CMTP	0x001B	Common ISDN Access Profile (CIP) [DEPRECATED]
	MCAPControlChannel	0x001E	Multi-Channel Adaptation Protocol (MCAP)
	MCAPDataChannel	0x001F	Multi-Channel Adaptation Protocol (MCAP)
	L2CAP	0x0100	Bluetooth Core Specification 
	SDP	0x0001	16-bit
RFCOMM	0x0003	16-bit
OBEX	0x0008	16-bit
HTTP	0x000C	16-bit
L2CAP	0x0100	16-bit
BNEP	0x000F	16-bit
Serial Port	0x1101	16-bit
ServiceDiscoveryServerServiceClassID	0x1000	16-bit
BrowseGroupDescriptorServiceClassID	0x1001	16-bit
PublicBrowseGroup	0x1002	16-bit
OBEX Object Push Profile	0x1105	16-bit
OBEX File Transfer Profile	0x1106	16-bit
Personal Area Networking User	0x1115	16-bit
Network Access Point	0x1116	16-bit
Group Network	0x1117	16-bit
	*/
	
	SDP(new UUID(0x0001), "Service Discovery Protocol"),
	RFCOMM(new UUID(0x0003), "RFCOMM"),
	TSCBIN(new UUID(0x0005), "Telephony Control Specification / TSC Binary [ deprecated ]"),
	ATT(new UUID(0x0007),"Attribute Protocol"),
	OBEX(new UUID(0x0008), "OBEX IrDA Interoperability"),
	BNEP(new UUID(0x000F), "Bluetooth Network Encapsulation Protocol"),
	UPNP(new UUID(0x0010), "Extended Service Discovery Profile (ESDP) [ deprecated ] "),
	HIDP(new UUID(0x0011), "Human Interface Device Profile (HID)"),
//	SP(new UUID(0x1101), "Serial Port"),
//	OBEXOPP(new UUID(0x1105), "OBEX Object Push Profile"),
//	OBEXFTP(new UUID(0x1106), "OBEX File Transfer Protocol"),
//	AVRCP_TARGET(new UUID(0x110C), "Audio/Video Remote Control Protocol"),
//	AVRCP(new UUID(0x110E), "Audio/Video Remote Control Protocol"),
//	AVRCP_CONTROL(new UUID(0x110F), "Audio/Video Remote Control Protocol"),
//	PANU(new UUID(0x1115), "Personal Area Networking User"),
//	PANP(new UUID(0x1117), "Personal Area Networking Profile"),
	HSP(new UUID(0x1131), "HeadSet Profile");


	
	
	private static final UUID[] allServices;
	
	private final UUID serviceUUID;
	
	private final String description;
	
	BluetoothService(UUID serviceUUID, final String description) {
		this.serviceUUID = serviceUUID;
		this.description = description;
	}
	
	
	public static UUID[] getAll() {
		return allServices;
	}

	public String getDescription() {
		return this.description;
	}
	
	public UUID UUID() {
		return this.serviceUUID;
	}
	
	static {
		List<UUID> listOfUUIDs = new ArrayList<>();
		for( final BluetoothService service : BluetoothService.values()) {
			listOfUUIDs.add(service.serviceUUID);
		}
		
		allServices = new UUID[listOfUUIDs.size()];
		listOfUUIDs.toArray(allServices);
	}
	
}
