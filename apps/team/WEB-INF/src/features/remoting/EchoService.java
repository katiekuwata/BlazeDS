/**
 * A simple class that simply echoes back the provided text. Used by remoting
 * samples.
 */
package features.remoting;

import features.remoting.externalizable.ExternalizableClass;
import flex.messaging.io.BeanProxy;
import flex.messaging.io.PropertyProxyRegistry;

public class EchoService
{
    // Making sure the read-only properties of ReadOnly class are serialized 
    // back to the client.
    static 
    {
        PropertyProxyRegistry registry = PropertyProxyRegistry.getRegistry();
        BeanProxy beanProxy = new BeanProxy();
        beanProxy.setIncludeReadOnly(true);
        registry.register(ReadOnly.class, beanProxy);
    } 

    public String echo(String text)
    {
        return "I received '" + text + "' from you";
    }

    public int echoInt(int value)
    {
        return value;
    }

    public boolean echoBoolean(boolean value)
    {
        return value;
    }

    public ExternalizableClass echoExternalizableClass(ExternalizableClass value)
    {
        return value;
    }

    public ReadOnly echoReadOnly()
    {
        ReadOnly ro = new ReadOnly("property1");
        return ro;
    }

    // A Class that only has a read-only property.
    public class ReadOnly
    {
        private String property;

        public ReadOnly(String property)
        {
            this.property = property;
        }

        public String getProperty()
        {
            return property;
        }
    }
}

