package flex.samples.marketdata;

import java.io.*;
import java.net.URLDecoder;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.util.List;
import java.util.ArrayList;

public class Portfolio {
	
	public static void main(String[] args) {
		Portfolio stockFeed = new Portfolio(); 
		stockFeed.getStocks();
	}
	
	public List getStocks() {

		List list = new ArrayList();
		
        try {
        	String filePath = URLDecoder.decode(getClass().getClassLoader().getResource("flex/samples/marketdata/portfolio.xml").getFile(), "UTF-8");;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            Document doc = factory.newDocumentBuilder().parse(new File(filePath));
            NodeList stockNodes = doc.getElementsByTagName("stock");
            int length = stockNodes.getLength();
            Stock stock;
            Node stockNode;
            for (int i=0; i<length; i++) {
            	stockNode = stockNodes.item(i);
            	stock = new Stock();
            	stock.setSymbol( getStringValue(stockNode, "symbol") );
            	stock.setName( getStringValue(stockNode, "company") );
            	stock.setLast( getDoubleValue(stockNode, "last") );
            	stock.setHigh( stock.getLast() );
            	stock.setLow( stock.getLast() );
            	stock.setOpen( stock.getLast() );
            	stock.setChange( 0 );
            	list.add(stock);
            	System.out.println(stock.getSymbol());
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }

        return list;
	}
	
	private String getStringValue(Node node, String name) {
		return ((Element) node).getElementsByTagName(name).item(0).getFirstChild().getNodeValue();		
	}

	private double getDoubleValue(Node node, String name) {
		return Double.parseDouble( getStringValue(node, name) );		
	}

}