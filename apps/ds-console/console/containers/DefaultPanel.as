package console.containers
{
    import console.ConsoleManager;
    import mx.containers.Panel;
    import flash.events.Event;
    
    public class DefaultPanel extends UpdateListener
    {
        private var display:DefaultPanelDisplay;
        
        public function DefaultPanel():void
        {
            super();
            display = new DefaultPanelDisplay;
            this.addChild(display);
            label = "Console";
            
            // No data is ever used, but to ensure compatibility with the rest of
            // the app this panel still registers with the ConsoleManager
            ConsoleManager.getInstance().registerListener(this, []);
        }                
    }
}