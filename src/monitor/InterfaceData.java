/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitor;

/**
 *
 * @author hmusallam
 */
public class InterfaceData {
    
    private final  String user_name;
    private final  String interface_physical_address;
    private final  String interface_name;
    private final  String windows;
    private final  String windows_version;

    public InterfaceData( String user_name, String interface_physical_address, String interface_name, String windows, String windows_version) {
        this.user_name = user_name;
        this.interface_physical_address = interface_physical_address;
        this.interface_name = interface_name;
        this.windows = windows;
        this.windows_version = windows_version;
    }

   

    public String getUser_name() {
        return user_name;
    }

    public String getInterface_physical_address() {
        return interface_physical_address;
    }

    public String getInterface_name() {
        return interface_name;
    }

    public String getWindows() {
        return windows;
    }

    public String getWindows_version() {
        return windows_version;
    }
    
  
}
