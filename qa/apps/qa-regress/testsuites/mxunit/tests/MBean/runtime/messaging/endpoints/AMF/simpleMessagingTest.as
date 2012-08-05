// This is a hacked up version of a more complete test in \qa-regress\testsuites\mxunit\tests\messagingService
        import mx.messaging.Channel;
        import mx.messaging.ChannelSet;
        import mx.messaging.Consumer;
        import mx.messaging.Producer;
        import mx.messaging.config.*;
        import mx.messaging.events.*;
        import mx.messaging.messages.*;
                           
        //  Name of the destination to be used by the Producer and the Consumer. 
        private var destination:String = "MyTopic_MBean";          
      //   private var destination:String = "topic_authentication"; 
       private var channel:Channel;
       private var pro:Producer;
       private var con:Consumer;            
      
       public function simpleMessageTest():void {  
            
            //setup the producer
            pro = new Producer();
            pro.destination = destination;           
            
            //setup the consumer
             con = new Consumer(); 
            con.destination = destination;
             
             pro.addEventListener(ChannelFaultEvent.FAULT, producerEventHandler);
             pro.addEventListener(MessageFaultEvent.FAULT, producerEventHandler);
             pro.addEventListener(MessageAckEvent.ACKNOWLEDGE, producerEventHandler);  
                                                    
             con.addEventListener(MessageFaultEvent.FAULT, consumerEventHandler);
             con.addEventListener(ChannelFaultEvent.FAULT, consumerEventHandler);
             con.addEventListener(MessageEvent.MESSAGE, consumerEventHandler);           

            // Setup an event handler for the consumer ack event. 
          
            con.addEventListener(MessageAckEvent.ACKNOWLEDGE, consumerAck);       		
            var cs:ChannelSet = new ChannelSet(); 
 		//	channel = ServerConfig.getChannel("qa-streaming-amf",false);  	
 			channel = ServerConfig.getChannel("qa-polling-constraint-amf");  			
        	cs.addChannel(channel);
			
			//set the consumer to use the new channel set then subscribe the consumer
			con.channelSet = cs; 
		  //  con.setCredentials("sampleuser","samplepassword"); 
		    con.setCredentials("manager","manager1"); 
			//set the producer to use the new channel set
            pro.channelSet = cs; 
          //  pro.setCredentials("sampleuser","samplepassword");  
            pro.setCredentials("manager","manager1");       
            con.subscribe();                                     
        }
 
        //  Listen for consumer ack events so we can tell when the consumer has subscribed. 

        private function consumerAck(event:MessageAckEvent):void {
            //make sure the ack is for a subscribe operation
           // out.text += "\n begin consumerAck \n";
            if((event.correlation is CommandMessage) && (CommandMessage(event.correlation).operation == CommandMessage.SUBSCRIBE_OPERATION))
            {
                /*
                 * Since we are subscribed remove the event listener for consumer ack events. Then create a new event
                 * listener for message events as we did previously. 
                 */        
                con.removeEventListener(MessageAckEvent.ACKNOWLEDGE, consumerAck);                                
                //send the message
                var msg:IMessage = new AsyncMessage();    
         //   out.text += "\n about to send message \n";
                msg.body = "hello" ;
                pro.send(msg);              
            } 
        }
        
        /**
        *  Listen for disconnect events. 
        */
        public function channelDisconnect(event:ChannelEvent):void {     
            con.removeEventListener(ChannelEvent.DISCONNECT, channelDisconnect);   
            
            Assert.hasPendingTest = false; 
        }
        /**
        *  Listen for events from the producer.    
        */
        public function producerEventHandler(event:Event):void 
        {  
            if (event is MessageAckEvent) {
                trace("Producer received ack for message"); 
             //   out.text += "\nin producerEventHandler MessageAckEvent";   
            } else if (event is ChannelFaultEvent) {
                con.unsubscribe();
                con.disconnect();
                var cfe:ChannelFaultEvent = event as ChannelFaultEvent;                        
                Assert.fail("Channel faulted with following error while sending message: " + cfe.faultDetail); 
                Assert.hasPendingTest = false;        
            } else if (event is MessageFaultEvent) {
                con.unsubscribe();
                con.disconnect();
                var mfe:MessageFaultEvent = event as MessageFaultEvent;
                Assert.fail("Message fault while sending message: " + mfe.faultDetail);    
                Assert.hasPendingTest = false;             
            }
        }
        /**
        *  Listen for events from the consumer. 
        */
        public function consumerEventHandler(event:Event):void
        {
            if (event is ChannelFaultEvent) {
                var cfe:ChannelFaultEvent = event as ChannelFaultEvent;                        
               Assert.fail("Consumer channel faulted with the following error: " + cfe.faultDetail);     
            } else if (event is MessageFaultEvent) {            
                var mfe:MessageFaultEvent = event as MessageFaultEvent;
                Assert.fail("Consumer got the following message fault: " + mfe.faultDetail);                
            } else if (event is MessageEvent) {                     
                var me:MessageEvent = event as MessageEvent;
                //get the message body from the message and store it.
                var result:String = me.message.body.toString();
                //assert we got the correct message for the channel being tested
             Assert.assertEquals(result, "hello" );
             out.text += "message result: " + result + "\n";
                /*
                 * Since we got the message remove the event listener for message events. Then create a new event
                 * listener for disconnect events using addAsync as we did previously. 
                 */ 
                con.removeEventListener(MessageEvent.MESSAGE, consumerEventHandler);   
                con.addEventListener(ChannelEvent.DISCONNECT, channelDisconnect);    
            }
            //disconnect the consumer 
        //    con.logout();
            con.unsubscribe();
            con.disconnect();
        }

        public function disconnectProducer():void {
        //    pro.logout();
            pro.disconnect();
        }
