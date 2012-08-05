package console.containers
{
    import mx.charts.LineChart;
    import mx.charts.series.LineSeries;

    public class PollableAttributeChart extends LineChart
    {
        function PollableAttributeChart(provider:Object):void
        {
            super();
            
            dataProvider = provider;
            this.percentHeight = 100;
            this.percentWidth = 100;
            
            var series:LineSeries = new LineSeries;
            series.dataProvider = provider;
            series.yField = "value";
            this.addChild(series);
            
            initialize();
        }
    }
}