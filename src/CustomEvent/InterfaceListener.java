/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CustomEvent;

import java.util.EventListener;
import monitor.InterfaceData;

/**
 *
 * @author hmusallam
 */
public  interface InterfaceListener extends EventListener{
 
       public void eventHandler(InterfaceEventObject event,InterfaceData data);
 }
 