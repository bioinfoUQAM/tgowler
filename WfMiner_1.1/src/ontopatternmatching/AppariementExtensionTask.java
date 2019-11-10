/**
 *
 * @author Enridestroy
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ontopatternmatching;

public abstract class AppariementExtensionTask{        
    public Integer[] item = null;

    public abstract AppariementStructure extend(AppariementStructure structure);   
    
    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("extension:");
        for(Integer i : this.item){
            s.append(",");
            s.append(i);
        }
        return super.toString() + ">" + s.toString();
    }
}
