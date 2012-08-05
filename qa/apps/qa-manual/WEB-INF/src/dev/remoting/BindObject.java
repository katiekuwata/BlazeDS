package dev.remoting;

import flex.messaging.FlexSessionBindingListener;
import flex.messaging.FlexSessionBindingEvent;

/**
 * Created by IntelliJ IDEA.
 * User: wichan
 * Date: Nov 9, 2006
 * Time: 4:43:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class BindObject implements FlexSessionBindingListener{
    public void valueBound(FlexSessionBindingEvent event) {
        System.out.println("BindObject.valueBound" + event.getName());
    }

    public void valueUnbound(FlexSessionBindingEvent event) {
        System.out.println("BindObject.valueUnbound" + event.getName());
    }
}
