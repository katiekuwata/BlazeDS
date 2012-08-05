
public class Simple implements SimpleMBean
{
    private String simpleString;
    public String getSimpleString()
    {
        return simpleString;
    }
    public void setSimpleString(String val)
    {
        simpleString = val;
    }
}