/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CustomEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import monitor.InterfaceData;

/**
 *
 * @author hmusallam
 */
    public class Interface_event {
  private final List _listeners = new ArrayList();
  public synchronized void addEventListener(InterfaceListener listener)  {
    _listeners.add(listener);
  }
  public synchronized void removeEventListener(InterfaceListener listener)   {
    _listeners.remove(listener);
  }
  // call this method whenever you want to notify
  //the event listeners of the particular event
  public synchronized void fireEvent(InterfaceData data) {
    InterfaceEventObject event = new InterfaceEventObject(this);
    Iterator i = _listeners.iterator();
    while(i.hasNext())  {
      ((InterfaceListener) i.next()).eventHandler(event,data);
    }
  }

}
