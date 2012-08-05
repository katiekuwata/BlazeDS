package dev.remoting;

import flex.messaging.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Calendar;
import javax.servlet.http.Cookie;

/**
 * Created by IntelliJ IDEA.
 * User: wchan
 * Date: Jul 11, 2005
 * Time: 2:23:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestServices implements FlexSessionAttributeListener, FlexSessionListener{
    private String _keepit;
    public int testArgsOrder(int arg1,int arg2,int arg3,int arg4,int arg5,int arg6,int whichone) {

        switch (whichone) {
            case 1:
                return arg1;
            case 2:
                return arg2;
            case 3:
                return arg3;
            case 4:
                return arg4;
            case 5:
                return arg5;
            case 6:
                return arg6;
            default:
                return Integer.MIN_VALUE;

        }
    }

    public void testMixNumbers(int arg1,short arg2,float arg3,double arg4,Integer arg5)  {

    }

    public void testMixTypes(int arg1, boolean arg3, Boolean arg4, String arg5) {

    }

    public void keepIt(String it) {
        _keepit = it;
    }

    public String keepWhat() {
        return _keepit;
    }

    public String getUserSessionID()  {
       return FlexContext.getHttpRequest().getSession(true).getId();
    }
    public String getGatewayConfigFileName() {
        return FlexContext.getServletConfig().getInitParameter("gateway.configuration.file");
    }
    public void setCookie(String name, String value) {
        FlexContext.getHttpResponse().addCookie(new Cookie(name,value));
    }
    public void setCookies(HashMap map) {
        Iterator allKeys = map.keySet().iterator();
        String key=null;
        while (allKeys.hasNext()) {
            key = (String) allKeys.next();
            System.out.println(key + "," +map.get(key) );
            setCookie(key,(String) map.get(key));
        }
    }
    public HashMap getCookiesAsMap() {
        Cookie[] cookies = FlexContext.getHttpRequest().getCookies();
        HashMap map = new HashMap(10,0.7F);
        for (int i=0; i < cookies.length ;i++) {
            map.put(cookies[i].getName(),cookies[i].getValue());
        }
        return map;
    }
    public Cookie[] getCookies() {
        return FlexContext.getHttpRequest().getCookies();
    }
    public String getUserPrincipal() {
        return FlexContext.getHttpRequest().getUserPrincipal().toString();
    }
    public boolean isUserInRole(String role) {
        return FlexContext.getHttpRequest().isUserInRole(role);
    }

    public String slowEchoString(String s, long ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        return s;
    }

    public void throwException(String value) throws Exception {
        throw new Exception(value);
    }

    public void setAttribute(String key,Object value) {
        FlexSession fsession = FlexContext.getFlexSession();
        fsession.setAttribute(key,value);
    }

    public void setAttributeBind(String key) {
        FlexSession fsession = FlexContext.getFlexSession();
        fsession.setAttribute(key, new BindObject());
    }

    public void removeAttribute(String key) {
        FlexContext.getFlexSession().removeAttribute(key);
    }

    public Object getAttribute(String key) {
        return FlexContext.getFlexSession().getAttribute(key);
    }

    public void monitorSessionChange() {
        FlexContext.getFlexSession().addSessionAttributeListener(this);
        FlexContext.getFlexSession().addSessionDestroyedListener(this);
    }

    public void stopMonitorSessionChange() {
        FlexContext.getFlexSession().removeSessionAttributeListener(this);
        FlexContext.getFlexSession().removeSessionDestroyedListener(this);
    }

    public void attributeAdded(FlexSessionBindingEvent event) {
        System.out.println("<attributeAdded>" + event.getName() + ":" + event.getValue());
    }

    public void attributeRemoved(FlexSessionBindingEvent event) {
        System.out.println("<attributeRemoved>" + event.getName() + ":" + event.getValue());
    }

    public void attributeReplaced(FlexSessionBindingEvent event) {
        System.out.println("<attributeReplaced>" + event.getName() + ":" + event.getValue());
    }

    public void sessionDestroyed(FlexSession session) {
        System.out.println(Calendar.getInstance().toString() + session.toString()+" : Session Destroyed");
    }

    public void sessionCreated(FlexSession session) {
        System.out.println(Calendar.getInstance().toString() + session.toString()+" : Session Created");
    }

    public Object getUserPrincipalFromFlexSession() {
        return FlexContext.getFlexSession().getUserPrincipal();
    }

    public void invalidateSession() {
        FlexContext.getFlexSession().invalidate();
    }
}
