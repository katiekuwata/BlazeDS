////////////////////////////////////////////////////////////////////////////////
//
//  Copyright (C) 2003-2005 Macromedia, Inc. All Rights Reserved.
//  The following is Sample Code and is subject to all restrictions
//  on such code as contained in the End User License Agreement
//  accompanying this product.
//
////////////////////////////////////////////////////////////////////////////////

package console.containers
{

import flash.events.MouseEvent;
import mx.containers.HBox;
import mx.controls.Button;
import mx.controls.Label;
import mx.controls.TextInput;
import mx.messaging.management.MBeanOperationInfo;
import mx.messaging.management.MBeanParameterInfo;

/**
 * The Operation container is an HBox that renders UI to invoke an operation on
 * an MBean. When the operationInfo property of an Operation is set, the current
 * UI for the Operation is torn down, and new UI is rendered based on the new 
 * MBeanOperationInfo metadata provided.
 * <p>
 * This container/control should not be instantiated directly. A parent OperationSet
 * container will create nested Operations based on its MBean metadata.
 */
public class Operation extends HBox
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 * @private
	 * Constructor.
	 */
	public function Operation(wrapper:OperationSet)
	{
		super();
		
		_wrapper = wrapper;
		_values = [];
		_signature = [];
	}
	
	/**
	 * @private
	 * The parent container.
	 */
	private var _wrapper:OperationSet;
	
	/**
	 * @private
	 * Array to store references to controls for each argument.
	 */
	private var _values:Array; 
	
	/**
	 * @private
	 * Array to store the type signature for this operation.
	 */
	private var _signature:Array;
	
	//----------------------------------
	//  MBeanOperationInfo
	//----------------------------------

	/**
	 * @private
	 * Storage for the operation info.
	 */	
	private var _operationInfo:MBeanOperationInfo;

	/**
	 * MBean operation metadata that drives the UI for this component.
	 */
	public function get operationInfo():MBeanOperationInfo
	{
		return _operationInfo;
	}

	public function set operationInfo(value:MBeanOperationInfo):void
	{
                var i:int;

		_operationInfo = value;
		// Remove any existing children and refs to argument values.
		_values.splice(0);
		_signature.splice(0);
		for (i = numChildren - 1; i >= 0; --i)
		{
			removeChildAt(i);	
		}		
		
		// Build UI for this operation.
		var opName:Button = new Button();
		opName.label = value.name;
		opName.addEventListener("click", invokeOperation);		
		addChild(opName);	
		
		var openParen:Label = new Label();
		openParen.text = " (";
		addChild(openParen);
		
		var comma:Label = new Label();
		comma.text = ", ";
		var paramName:String;
		var n:int = value.signature.length;
		for (i = 0; i < n; ++i)
		{
			var pName:Label = new Label();
			paramName = value.signature[i].name;
			if (paramName.length > 0)
			{
				pName.text = paramName;	
			}
			else
			{
				pName.text = "p" + (i + 1);				
			}
			addChild(pName);
			var pValue:TextInput = new TextInput();
			addChild(pValue);
			_values[i] = pValue;
			_signature[i] = value.signature[i].type;
			if (i != (n - 1))
			{
				addChild(comma);
			}
		}
		
		var closeParen:Label = new Label();
		closeParen.text = " ) ";
		addChild(closeParen);
	}
	
	/**
	 * @private
	 * Calls back into the parent OperationSet to dispatch an
	 * event for this operation invocation request.
	 */
	private function invokeOperation(e:MouseEvent):void
	{
		var argsToPass:Array = [];
		var n:int = _values.length;	
		for (var i:int = 0; i < n; ++i)
		{
			argsToPass.push(_values[i].text);	
		}	
		_wrapper.dispatchInvokeEvent(_operationInfo.name, argsToPass, _signature);
	}
}	
	
}