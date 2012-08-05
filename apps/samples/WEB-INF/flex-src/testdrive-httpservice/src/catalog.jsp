<%@page import="flex.samples.product.ProductService, 
				flex.samples.product.Product, 
				java.util.List"%>
<?xml version="1.0" encoding="utf-8"?>
<catalog>
<%
	ProductService srv = new ProductService();
	List list = null;
	list = srv.getProducts();
	Product product;
	for (int i=0; i<list.size(); i++)
	{
		product = (Product) list.get(i);
%>	
    <product productId="<%= product.getProductId()%>">
        <name><%= product.getName() %></name>
        <description><%= product.getDescription() %></description>
        <price><%= product.getPrice() %></price>
        <image><%= product.getImage() %></image>
        <category><%= product.getCategory() %></category>
        <qtyInStock><%= product.getQtyInStock() %></qtyInStock>
    </product>
<%
	}
%>
</catalog>