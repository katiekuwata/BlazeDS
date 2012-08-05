package features.remoting.externalizable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * A simple class that uses Externalizable interface to read and write its properties.
 */
public class ExternalizableClass implements Externalizable
{
    private String property1;
    private String property2;

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        property1 = (String)in.readObject();
        property2 = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(property1);
        out.writeObject(property2);
    }
}
