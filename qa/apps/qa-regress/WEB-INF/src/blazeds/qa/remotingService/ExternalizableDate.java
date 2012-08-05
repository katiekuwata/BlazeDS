package blazeds.qa.remotingService;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Date;
import java.util.Map;
import java.util.Calendar;

public class ExternalizableDate extends Date implements Externalizable
{
    private static final long serialVersionUID = 8037877279661457358L;
    

    public void readExternal(ObjectInput input) throws IOException,
            ClassNotFoundException
    {
        Object source = input.readObject();
        if (source instanceof Map)
        {
            Map map = (Map) source;
            int year = Integer.valueOf((String) map.get("year"));
            int month = Integer.valueOf((String) map.get("month"));
            int date = Integer.valueOf((String) map.get("date"));
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set( year, month, date );            
            setTime( calendar.getTimeInMillis() );
        }
        else
        {
            throw new IOException("Cannot find expected value type");
        }
    }

    public void writeExternal(ObjectOutput output) throws IOException
    {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(this);
        Map map = new HashMap();
        map.put("year", new Integer(calendar.get(Calendar.YEAR)));
        map.put("month", new Integer(calendar.get(Calendar.MONTH)));
        map.put("date", new Integer(calendar.get(Calendar.DATE)));
        output.writeObject(map);
    }

}
