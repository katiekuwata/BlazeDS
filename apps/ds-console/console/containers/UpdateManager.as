package console.containers
{
    public interface UpdateManager
    {
        function registerListener(listner:UpdateListener, types:Array):void;
        function unregisterListener(listner:UpdateListener):void;
        function activateListener(listener:UpdateListener):void;
        function deactivateListener(listener:UpdateListener):void;
    }
}