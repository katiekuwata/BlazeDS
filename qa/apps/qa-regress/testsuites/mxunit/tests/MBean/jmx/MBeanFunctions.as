
   public function getMBeanAttribute(ObjectName:String = null, AttributeName:String = null):void
        {
        // example MBean Object name and AttributeName
        // ObjectName = "flex.runtime.Admin Tester:id=MessageBroker1,type=MessageBroker";
        //  AttributeName = "EnterpriseThroughput";
        	
           handler = manager.currentTest.chain(runtimeManagement,[ResultEvent.RESULT,FaultEvent.FAULT],getAttributeHandler,{"Attribute":AttributeName});    
           var token:AsyncToken = runtimeManagement.getAttribute(ObjectName,AttributeName);                           
        } 
            
         
   public function getAttributeHandler(event:Object = null, params:Object = null):void   
   {
         if (event is ResultEvent)
         {		 
         	out.text += params.Attribute + " result: " + ObjectUtil.toString(event.result) + "\n";
            Assert.assertTrue(event.result > 0);
         }
         else
         {
         	out.text += ObjectUtil.toString(event.fault);
            Assert.fail("Should not have FaultEvent: " + event.fault.faultString + ",Code: "
            		+ event.fault.faultCode);
         }
     }

     
   public function getMBeanAttributes(ObjectName:String = null, AttributeNames:Array = null):void
        {        	
           handler = manager.currentTest.chain(runtimeManagement,[ResultEvent.RESULT,FaultEvent.FAULT],getAttributesHandler,{"Attribute":AttributeNames});      
           var token:AsyncToken = runtimeManagement.getAttributes(ObjectName,AttributeNames);                           
        } 
            
         
   public function getAttributesHandler(event:Object = null, params:Object = null):void   
   {
         if (event is ResultEvent)
         {		
             out.text += params.Attribute + " result: " + ObjectUtil.toString(event.result) + "\n"; 
             var re:ResultEvent = event as ResultEvent;                   
             var result:Array = re.result as Array; 
             for (var i:int = 0; i < result.length; i++) {
                 Assert.assertTrue(result[i].value > 0);
             }
         }
         else
         {
            out.text += ObjectUtil.toString(event.fault);
            Assert.fail("Should not have FaultEvent: " + event.fault.faultString + ",Code: " + event.fault.faultCode);
         }
     }
     
   public function invokeOperation(objectName:String = null, operation:String = null, params:Array=null, signature:Array=null ):void
      {
      	  handler = manager.currentTest.chain(runtimeManagement,[ResultEvent.RESULT,FaultEvent.FAULT],invokeOperationHandler,{"Operation":operation});      
          var token:AsyncToken = runtimeManagement.invoke(objectName, operation, params, signature); 
      }
      
   public function invokeOperationHandler(event:Object = null, params:Object = null):void   
      {
         if (event is ResultEvent)
         {	
            if (params.Operation == "getTargetLevel"){
                var TargetLevel:int = event.result;
                out.text += "\nTargetLevel = " + TargetLevel;
            }
            out.text += params.Operation + " result (might be null): " + ObjectUtil.toString(event.result) + "\n";
         }
         else
         {
         	out.text += ObjectUtil.toString(event.fault);
            Assert.fail("Should not have FaultEvent: " + event.fault.faultString + ",Code: "
            		+ event.fault.faultCode);
         }
       }
     
  public function getFlexMBeanObjectNames(domainName:String):void
     {
      	handler = manager.currentTest.chain(runtimeManagement,[ResultEvent.RESULT,FaultEvent.FAULT],getFlexMBeanObjectNamesHandler,{"domainName":domainName});      
        var token:AsyncToken = runtimeManagement.getFlexMBeanObjectNames(); 
     }
     
     public function getFlexMBeanObjectNamesHandler(event:Object = null, params:Object = null):void
     {
     	if (event is ResultEvent)
     	{
     	    out.text += "getFlexMBeanObjectNames.length = " + event.result.length + "\n";
           /* out.text +=  "getFlexMBeanObjectNames result: \n"
            for (var i:int = 0; i < event.result.length; i++) {
            	 if (event.result[i].domain == params.domainName) {
                 out.text +=  "\n" + ObjectUtil.toString(event.result[i].canonicalName) + "\n";
                 }
            }
            */

          // the order and specific MBeans returned varies - so just asserting it is an array of objects here
          Assert.assertTrue(event.result.length > 0);
         // uncomment this if you want to see everything    
     	//	out.text +=  "\ngetFlexMBeanObjectNamesHandler result: \n" + ObjectUtil.toString(event.result) + "\n";
         }
         else
         {
            out.text += ObjectUtil.toString(event.fault);
            Assert.fail("Should not have FaultEvent: " + event.fault.faultString + ",Code: " + event.fault.faultCode);
         }	
     }
     
     public function getMBeanInfo(objectName:String):void
     {
      	handler = manager.currentTest.chain(runtimeManagement,[ResultEvent.RESULT,FaultEvent.FAULT],getMBeanInfoHandler,{"MBeanName":objectName});      
        var token:AsyncToken = runtimeManagement.getMBeanInfo(objectName);
     }
     
     public function getMBeanInfoHandler(event:Object = null, params:Object = null):void   
   {
         if (event is ResultEvent)
         {		 
            out.text += "\ngetMBeanInfo for " + params.MBeanName + ": \n" + ObjectUtil.toString(event.result) + "\n";
            Assert.assertTrue(event.result.attributes.length > 0);
         }
         else
         {
         	out.text += ObjectUtil.toString(event.fault);
            Assert.fail("Should not have FaultEvent: " + event.fault.faultString + ",Code: "
            		+ event.fault.faultCode);
         }
     }
     
     
     
      
    
     
