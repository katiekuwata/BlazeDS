////////////////////////////////////////////////////////////////////////////////
//
//  Copyright (C) 2003-2005 Macromedia, Inc. All Rights Reserved.
//  The following is Sample Code and is subject to all restrictions
//  on such code as contained in the End User License Agreement
//  accompanying this product.
//
////////////////////////////////////////////////////////////////////////////////

package console {
    
import mx.collections.ArrayCollection;
import mx.collections.ICollectionView;
import mx.controls.treeClasses.ITreeDataDescriptor;
    
public class ConsoleTreeDataDescriptor implements ITreeDataDescriptor {

        public function getChildren(node:Object, model:Object=null):ICollectionView
	{
	    if (node == null)
	        return null;
		else if (node.hasOwnProperty("children") && (node.children.length > 0))
		    return new ArrayCollection(node.children);
		else
    		return null;
	}
	
	public function hasChildren(node:Object, model:Object=null):Boolean
        {
            return true;
        }


	public function isBranch(node:Object, model:Object=null):Boolean
	{
	    if (node == null)
	        return false;
	    else
    		return (node.hasOwnProperty("children") && (node.children.length > 0)) ? true : false;
	}
	
	public function getData(node:Object, model:Object=null):Object 
	{
		return node;
	}

          
        public function addChildAt(node:Object, child:Object, index:int, model:Object=null):Boolean
	{
           return true;
	} 

       public function removeChildAt(node:Object, child:Object, index:int, model:Object=null):Boolean
       {
            return true;
       }
    
}

}