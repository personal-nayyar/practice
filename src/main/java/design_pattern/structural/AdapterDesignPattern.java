package design_pattern.structural;

/**
 * Adapter Design Pattern: USB to Ethernet Adapter Example
 * 
 * This example demonstrates how the Adapter pattern can be used to make
 * incompatible interfaces work together, just like a USB-to-Ethernet adapter
 * lets you connect an Ethernet cable to a USB port.
 */
public class AdapterDesignPattern {
    public static void main(String[] args) {
        // We have an Ethernet cable
        EthernetCable ethernetCable = new EthernetCable();
        
        // But our laptop only has USB ports
        // So we use an adapter
        USBPort adapter = new USBToEthernetAdapter(ethernetCable);
        
        // Now we can connect to the internet!
        Laptop laptop = new Laptop();
        laptop.connectToInternet(adapter);
    }
}

// Target interface (what your laptop expects)
interface USBPort {
    void connectUSB();
}

// Adaptee (the Ethernet cable you have)
class EthernetCable {
    public void connectEthernet() {
        System.out.println("Connected via Ethernet");
    }
}

// Adapter (makes Ethernet cable work with USB port)
class USBToEthernetAdapter implements USBPort {
    private final EthernetCable ethernetCable;
    
    public USBToEthernetAdapter(EthernetCable cable) {
        this.ethernetCable = cable;
    }
    
    @Override
    public void connectUSB() {
        System.out.println("Converting USB signal to Ethernet...");
        ethernetCable.connectEthernet();
        System.out.println("Internet connection established!");
    }
}

// Client (your laptop)
class Laptop {
    public void connectToInternet(USBPort port) {
        System.out.println("Laptop: Attempting to connect to internet...");
        port.connectUSB();
    }
}
