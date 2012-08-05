package security; 

import java.security.Principal; 

public class PerClientPrincipal implements Principal { 

    private String name; 
     
    public PerClientPrincipal(){ 
         
    } 
    public PerClientPrincipal(String inName){ 
        name = inName; 
    } 

    public String getName() { 
        return name; 
    } 
     
    public void setName(String inName) { 
        name = inName; 
    } 

}