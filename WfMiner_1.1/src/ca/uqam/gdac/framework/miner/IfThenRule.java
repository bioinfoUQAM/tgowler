/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uqam.gdac.framework.miner;

import java.util.ArrayList;
import ontologyrep2.OntoRepresentation;
import ontopatternmatching.AppariementSolution;
import ontopatternmatching.Motif;

/**
 *
 * @author ahmedhalioui
 */
public class IfThenRule {
    
    public ArrayList<ArrayList<Integer>> premise = new ArrayList<>();
    public ArrayList<Integer> conclusion;
    public ArrayList<Integer> steps = new ArrayList<>();
    public Motif pattern = new Motif();
    public Motif prefix = new Motif();
    public ArrayList<Integer[]> properties = new ArrayList<>();
    public float confidence ;
    public float support ;
    public int size ;
    public int generality ;
    public float diversity ;
    public float rank =0;
    public int utility =0;
    public float precision =0;
    
    
    public AppariementSolution[] Msequences = new AppariementSolution[1]; 
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public float getFreqData(){
        OntoRepresentation ontology = Miner.hierarchyRepresentation;
        return((float)pattern.getSuppData(ontology)/11);
    }
    
    public float getFreqMetaData(){
        OntoRepresentation ontology = Miner.hierarchyRepresentation;
        return((float)pattern.getSuppMetadata(ontology)/7);
    }
    
    public float getFreqProgs(){
        OntoRepresentation ontology = Miner.hierarchyRepresentation;
        return((float)pattern.getSuppProgram(ontology)/7);
    }
    
    public float getFreqRelations(){
//        OntoRepresentation ontology = Miner.hierarchyRepresentation;
        return((float)pattern.nbProperties()/7);
    }

    public float getPrecision() {
        return precision;
    }
    
    public int getUtility() {
        return utility;
    }
    
    public float getRank() {
        return rank;
    }
    
    public float getDiversity() {
        return diversity;
    }

    public int getSize() {
        return pattern.nbConcepts()+pattern.nbProperties();
    }

    public float getGenerality() {
        OntoRepresentation ontology = Miner.hierarchyRepresentation;
        return pattern.getGenerality(ontology);
    }
    
    public Motif getPrefix() {
        return prefix;
    }
    
    public Motif getPattern() {
        return pattern;
    }

    
    public ArrayList<Integer> getSteps() {
        return steps;
    }

    public IfThenRule() {
    }

    public float getSupport() {
        return support;
    }

    public AppariementSolution[] getMsequences() {
        return Msequences;
    }

    public ArrayList<ArrayList<Integer>> getPremise() {
        return premise;
    }

    public ArrayList<Integer> getConclusion() {
        return conclusion;
    }

    public ArrayList<Integer[]> getProperties() {
        return properties;
    }

    public float getConfidence() {
        return confidence;
    }
    
    public void setSteps(ArrayList<Integer> steps) {
        this.steps = steps;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public void setPremise(ArrayList<ArrayList<Integer>> premise) {
        this.premise = premise;
    }

    public void setConclusion(ArrayList<Integer> conclusion) {
        this.conclusion = conclusion;
    }

    public void setProperties(ArrayList<Integer[]> properties) {
        this.properties = properties;
    }

    public void setSupport(float support) {
        this.support = support;
    }

    public void setMsequences(AppariementSolution[] Msequences) {
        this.Msequences = Msequences;
    }

    public void setPattern(Motif pattern) {
        this.pattern = pattern;
    }

    public void setPrefix(Motif prefix) {
        this.prefix = prefix;
    }
    
    public void setSize(int size) {
        this.size = size;
    }

    public void setGenerality(int generality) {
        this.generality = generality;
    }

    public void setDiversity(float diversity) {
        this.diversity = diversity;
    }
    
    public void setRank(float rank) {
        this.rank = rank;
    }

    public void setUtility(int utility) {
        this.utility = utility;
    }

    public void setPrecision(float precision) {
        this.precision = precision;
    }
    
    
    public String prefixToString(){
        StringBuilder s = new StringBuilder();
        OntoRepresentation ontology = Miner.hierarchyRepresentation;
        ArrayList<String> cs = new ArrayList<String> ();
        
        for(ArrayList<Integer> t : prefix.transactions)
            for(Integer c : t)
                cs.add(ontology.getConcept(c).getName().split("#")[1]);
        s.append(cs.toString());
        
        s.append(", ");
        for(Integer[] r : prefix.relations){
           s.append("{(");
           s.append(r[1]+","+r[2]);
           s.append(")=");
           String uri = ontology.getRelation(r[0]).getName();
           s.append(uri.substring(uri.indexOf("#")+1));
           s.append("}");
        }
        return s.toString();
    }
    
    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        OntoRepresentation ontology = Miner.hierarchyRepresentation;
        
        ArrayList<String> cs = new ArrayList<String> ();
        
        for (ArrayList<Integer> t : premise)
            for(Integer c : t)
                cs.add(ontology.getConcept(c).getName().split("#")[1]);
        s.append(cs.toString());
        s.append(" => ");
        
        for (Integer c : conclusion)
            s.append(ontology.getConcept(c).getName().split("#")[1]);
        
        s.append(", ");
        s.append("{");
        for(Integer[] r : properties){
           s.append("(");
           s.append(r[1]+","+r[2]);
           s.append(")=");
           String uri = ontology.getRelation(r[0]).getName();
           s.append(uri.substring(uri.indexOf("#")+1));
        }
        s.append("}");
        s.append(" (confidence: "+confidence+"%)");
        s.append(" (support: "+support+"%)");
        return s.toString();
    }
    
    
}
