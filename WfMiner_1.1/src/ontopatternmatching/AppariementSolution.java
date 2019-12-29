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


public class AppariementSolution {
    public AppariementStructure parcours;
    public int[] appariement;
    public Workflow sequenceUtilisateur;
    public Motif motif;
    
    public String getSequenceUtilisateur(){
        return this.sequenceUtilisateur.toString();
    }
}
