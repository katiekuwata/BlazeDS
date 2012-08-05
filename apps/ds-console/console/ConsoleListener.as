package console
{
    import console.containers.UpdateListener;

    public class ConsoleListener extends UpdateListener
    {
        private var _console:console;
        
        public function ConsoleListener(c:console)
        {
            _console = c;
            ConsoleManager.getInstance().registerListener(this, []);
        }
        
        public override function mbeanModelUpdate(mbeanModel:Object):void
        {
                 for each (var appObj:Object in mbeanModel)
                 {
                     var names:Array = (appObj.label as String).split(".");
                     _console.appsList.addItem({label: names[2], data: appObj});
                 }                 
                 _console.appSelect.selectedIndex = 0;
        }
    }
}