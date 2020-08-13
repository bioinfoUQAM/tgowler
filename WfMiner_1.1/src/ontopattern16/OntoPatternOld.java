/**
 *
 * @author Enridestroy
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ontopattern16;

import ca.uqam.gdac.framework.library.Library;
import ca.uqam.gdac.framework.miner.IfThenRule;
import ca.uqam.gdac.framework.miner.Miner;
import static ca.uqam.gdac.framework.miner.Miner.allRules;
import static ca.uqam.gdac.framework.miner.Miner.conceptsToString;
import static ca.uqam.gdac.framework.miner.Miner.hierarchyRepresentation;
import static preprocessing.bowlLoader.loadRawSequences;

import ca.uqam.gdac.framework.xml.WorkflowFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import legacy.Pair;
import legacy.RawUserWorkflow;
import ontologyrep20.OntoRepresentation;
import ontologyrep20.RadixTree;
import ontopatternmatching.AppariementSolution;
import ontopatternmatching.Motif;
import ontopatternmatching.Sequence;
import org.xml.sax.SAXException;
import utility.HTMLViz;

import javax.rmi.CORBA.Util;


public class OntoPatternOld {
    public static void main(final String[] args) throws FileNotFoundException, IOException{
        final int topKitems=Integer.parseInt(args[5]);
        final int topNrules=Integer.parseInt(args[6]);
        double thresold_supp=Double.parseDouble(args[0]);
        
        // Load Parameters "[minSupp]" "[PATH_TO]/[bowl_file]" "[PATH_TO]/[train_set]" "[namespace]" "[PATH_TO]/[test_set]" "[topkItems]" "[topnRules]" "[min)ontology_level]"
        // E.g., "0.1" "./phylOntology_v51_small_final.bowl" "./WD-Phy-extracted-1/WD-Phy-extracted-1_2783_0.xml" "http://www.co-ode.org/ontologies/ont.owl#" "./WD-Phy-gold-1.xml" "10" "50" "2"
        HashMap<String, String[]> parametres = new HashMap<String, String[]>(){{
            //ontologie workflows phylogenetique
            put("phylo", new String[]{
                args[1],
                args[2],
                args[3],
                args[4]
//                "http://www.co-ode.org/ontologies/ont.owl#"
                
            });
            //ontologie tourisme
            put("tour", new String[]{
                "testformining.bowl", "resources/file-tests/sequencesTestPerformance.xml", "http://www.info.uqam.ca/Members/valtchev_p/mbox/ETP-tourism.owl#"
            });
        }}; 
        
        // Set the mode to the phylogenetics WFs not to tourism sequences
        String mode = "phylo";
        // Load the fowl file (ontology )
        final String bowlFile = parametres.get(mode)[0];
        System.out.println("Loading bowl ontology and workflow sequences ...");
        // TODO: to implement a new version of bowl serializer and deserialize

        ontologyrep20.OntoRepresentation ontology = bowl.BowlLoader.deserialize(bowlFile, true, true);
        //HTMLViz.dumpOriginalGraph("./example", ontology);

        // Get WF Training File
        final String rawSequences = parametres.get(mode)[1];
        System.out.println(rawSequences);
        // Load WF Test File
        final String rawGoldSequences = parametres.get(mode)[3];
        // Get the ontology namespace
        final String namespace = parametres.get(mode)[2];
        
        // Init WF TRAINING set
        final ArrayList<Sequence> userSequences = new ArrayList<>();
        ArrayList<RawUserWorkflow> rawUserSequences = null;
        try{
            rawUserSequences = WorkflowFactory.createRawUserSequences(rawSequences);
//            System.out.println(rawUserSequences);
            if(rawSequences==null){
                return;
            }
        }
        catch (SAXException | IOException e ){
            System.out.println(e);
            System.out.println("We couldn't load training workflows.");
            return;
        }
        
        // Init WF TEST set
        final ArrayList<Sequence> userGoldSequences = new ArrayList<>();
        ArrayList<RawUserWorkflow> rawUserGoldSequences = null;
        try{
            rawUserGoldSequences = WorkflowFactory.createRawUserSequences(rawGoldSequences);
            if(rawGoldSequences==null){
                return;
            }
        }
        catch (SAXException | IOException e ){
            System.out.println("We couldn't load test (gold standard) workflows.");
            return;
        }
        
        // ***************************************************
        // REPLACE WORKFLOW INSTANCES WITH ONTOLOGICAL CLASSES
        // ***************************************************
        RadixTree.RadixNode localNameNode = ontology.index_instances_by_name.getNode(namespace.toCharArray(), ontology.index_instances_by_name.root);
        if(localNameNode == null) {
            System.out.println("no valid namespace");
            return;
        }
        else {  
            // Training set

//            System.out.println("Reading the Training set " + parametres.get(mode)[1] + " ...");
            loadRawSequences(rawUserSequences, userSequences, ontology, rawSequences, localNameNode, args[3]);
            System.out.println("rawUserWorkflows: " + rawUserSequences.toString());
            // test set
//            System.out.println("Reading the Test set " + parametres.get(mode)[3] + " ...");
            loadRawSequences(rawUserGoldSequences, userGoldSequences, ontology, rawGoldSequences, localNameNode, args[3]);
//            System.out.println("/" + rawUserGoldSequences.size() + " workflows (Train/Test)");
        }
        
        long startTime = System.nanoTime();
        
        // ****************************
        // MINING GENERALIZED WORKFLOWS
        // ****************************
        System.out.println("Mining Generalized Workflows ...");
        ArrayList<Motif> patterns = Miner.findFrequentPatternsDF(userSequences, thresold_supp, ontology, Integer.parseInt(args[7]) );
        System.out.println("On a "+patterns.size()+" patterns");
        for (Motif pattern : patterns)
            System.out.println(pattern.toString());
        
        // **********************
        // PRUNING WORKFLOW RULES
        // **********************
        ArrayList<String> exportP = new ArrayList<String>();
        ArrayList<String> exportR = new ArrayList<String>();
        System.out.println("============== First elimination ==============");
        for (IfThenRule ruleb : Miner.bannedRules.values()){
            System.out.println(ruleb.toString());
        }
        System.out.println(Miner.bannedRules.size()+" first banned rules");
        
        System.out.println();
        System.out.println("============== final elimination ==============");
        Miner.bannedRules.putAll(secondElimination(Miner.rules)) ;
//        for (IfThenRule ruleb : Miner.bannedRules.values()){
//            System.out.println(ruleb.toString());
//        }
        System.out.println(Miner.bannedRules.size()+" banned rules");
        
        System.out.println();
//        System.out.println("============== Rules from the closed set ==============");
//        for (IfThenRule rule : Miner.rules.values()){
//            System.out.println(rule.toString());
//        }
        System.out.println(Miner.rules.size()+ " remaining rules.");
       
        // ****************
        // PRINT OUT RULES
        // ****************
        System.out.println();
        System.out.println("============== Statistics about the average of Top"+topNrules+" rules ==============");
        
        System.out.println("Confidence \t Support \t Depth \t FreqSteps \t FreqData \t FreqMetaData \t FreqProgs \t freqRel");
//      confidence, support, generality, steps, freqData, freqMetaData, freqProgs, freqRel
//        applyRules1(Miner.rules, userGoldSequences, ontology);
        String statistics = applyRules2(Miner.rules, userGoldSequences, ontology, topNrules, topKitems);
        
        File fRules = new File("./RecommendationRules.txt");
        File fPatterns = new File("./patterns.txt");
        
       // tests if file exists
        boolean boolR = fRules.exists();
        boolean boolP = fPatterns.exists();
        
        if(boolR == true)
         {
            // delete() invoked
            fRules.delete();
            fRules.createNewFile();
//            System.out.println("delete() invoked");
         }
        else
            fRules.createNewFile();
        
        if(boolP == true)
         {
            // delete() invoked
            fPatterns.delete();
            fPatterns.createNewFile();
//            System.out.println("delete() invoked");
         }
        else
            fPatterns.createNewFile();
        
        String headLines="Ontology: "+args[1]+"\n";
        headLines=headLines+"Train set: "+args[2]+"\n";
        headLines=headLines+"Test set: "+args[4]+"\n";
        headLines=headLines+"Top items: "+args[5]+"\n";
        headLines=headLines+"Top rules: "+args[6]+"\n";
        headLines=headLines+"min_supp: "+args[0]+"\n";
        headLines=headLines+"min_ontology_level: "+args[7]+"\n";
        headLines=headLines+"\n";
        Files.write(Paths.get(fRules.getPath()), headLines.getBytes(), StandardOpenOption.APPEND);
        
        Files.write(Paths.get(fRules.getPath()), "Rules: \n".getBytes(), StandardOpenOption.APPEND);
        
        // *************
        // RANKING RULES
        // *************
        HashMap<IfThenRule, Float> rulesToSort = new HashMap();
        ArrayList<IfThenRule> rulesBySequence=new ArrayList();
        
        for(IfThenRule r : Miner.rules.values()){
            rulesToSort.put(r, (float)r.getRank()/(float)r.getUtility());
            rulesBySequence.add(r);
        }
        
        // Rank final rules !
        Map<IfThenRule, Float> map = sortByValues(rulesToSort);
        Iterator iterator2 = map.entrySet().iterator();
//            System.out.println("###### SORTED RULES:");
        System.out.println();
        int cout=0;
        while(iterator2.hasNext() && cout<topNrules) {
            Map.Entry me2 = (Map.Entry)iterator2.next();
            StringBuilder b = new StringBuilder();
            for (IfThenRule rs:rulesBySequence){
                if (me2.getKey().toString().equals(rs.toString())){
                    b.append(rs.toString())
                    .append(" (MPR: ").append((float)rs.getRank()/(float)rs.getUtility()).append(")");
                    exportR.add(b.toString()) ;
                    break;
                }
            }
            cout++;
        }
        
        for(Motif m : patterns){
            StringBuilder b = new StringBuilder();
            // motif support
            b.append("[BaseSequence( support: ").append((m.support/(float)Library.getNbUserSequences())*100).append(", ")
            .append("steps: ").append(m.getSuppConcepts(ontology)).append(", ")
            .append("relations: ").append(m.getSuppRelations(ontology)).append(", ")
            .append("specilization: ").append(m.getGenerality(ontology)).append(", ")
            .append("data: ").append(m.getSuppData(ontology)).append(", ")
            .append("metadata: ").append(m.getSuppMetadata(ontology)).append(", ")
            .append("program: ").append(m.getSuppProgram(ontology)).append(",")
            
            .append(" [concepts=[");
//                b.append("[BaseSequence").append(" [concepts=[");
            for(ArrayList<Integer> t : m.transactions){
                b.append("[");
                for(Integer c : t){
                        String uri = ontology.getConcept(c).getName();
    //                    String uriStep = ontology.getConceptByLevel(ontology.getConcept(c), 0).getName().split("#")[1];
                        b.append(uri.substring(uri.indexOf("#")+1));
    //                    b.append(":");
    //                    b.append(uriStep);
                        b.append(", ");
                }
                b.append("]");
            }
            b.append("], properties=");
            for(Integer[] r : m.relations){
                b.append("{(");
                b.append(r[1]).append(",").append(r[2]);
                b.append(")=");
                String uri = ontology.getRelation(r[0]).getName();
                b.append(uri.substring(uri.indexOf("#")+1));
                b.append("}");
                }
            b.append("], ");
//            System.out.println(b.toString());
            exportP.add(b.toString()) ;
        }
        
        
        for (String p : exportR) {
            String line = "\n";
            line=p+line;
        
            try {
            
            Files.write(Paths.get(fRules.toPath().toString()), line.getBytes(), StandardOpenOption.APPEND);
            }catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
        }
        Files.write(Paths.get(fRules.toPath().toString()), statistics.getBytes(), StandardOpenOption.APPEND);
        
        
        String headLinesP="Ontology: "+args[1]+"\n";
        headLinesP=headLinesP+"Train set: "+args[2]+"\n";
        headLinesP=headLinesP+"min_supp: "+args[0]+"\n";
        headLinesP=headLinesP+"min_ontology_level: "+args[7]+"\n";
        headLinesP=headLinesP+"\n";
        Files.write(Paths.get(fPatterns.getPath()), headLinesP.getBytes(), StandardOpenOption.APPEND);
        
        
        Files.write(Paths.get(fPatterns.toPath().toString()), "Patterns: \n".getBytes(), StandardOpenOption.APPEND);
        for (String p : exportP) {
            String line = "\n";
            line=p+line;
        
            try {
            
            Files.write(Paths.get(fPatterns.toPath().toString()), line.getBytes(), StandardOpenOption.APPEND);
            }catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
        }
        String statisticsP =patterns.size()+"\n patterns";
        Files.write(Paths.get(fPatterns.toPath().toString()), statisticsP.getBytes(), StandardOpenOption.APPEND);
        
        
//        ArrayList<Rule> rules;
//        generateRules (patterns, ontology);
        
        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000;
        System.out.println(Miner.allRules.size()+"\t generated rules");
        System.out.println(Miner.allRules.size()+"\t generated patterns");
        System.out.println(Miner.rules.size()+"\t closure rules");
//        System.out.println(exportP.size()+"\t patterns");
        System.out.println(duration+"\t ms");

    }
    
    public static Map<Integer, IfThenRule> secondElimination (Map<Integer, IfThenRule> r){
        Map<Integer, IfThenRule> eliminated = new HashMap();

        for (IfThenRule x : r.values()){
            ArrayList<Sequence> ax = new ArrayList();
            for (int i=0;i<x.Msequences.length;i++)
                ax.add(x.Msequences[i].sequenceUtilisateur);
            
            for (IfThenRule y : r.values()){
                if (compareRule(x,y)==false){
                    ArrayList<Sequence> ay = new ArrayList();
                    for (int j=0;j<y.Msequences.length;j++)
                    ay.add(y.Msequences[j].sequenceUtilisateur);

                    if (compareRuleSequences(ax,ay)==true && ! ( eliminated.containsValue(x) ||  eliminated.containsValue(y) ) ){
//                        System.out.print("in i");
                        if ( x.premise.size() <= y.premise.size())
                            eliminated.putIfAbsent(x.hashCode(), x);
                        else eliminated.putIfAbsent(y.hashCode(), y);
                    }
                }
            }
//            System.out.println();
        }
        
        for (IfThenRule re : eliminated.values()){
            Miner.rules.remove(re.hashCode());
        }
        
        return eliminated;
    }
    
    public static boolean compareRuleSequences(ArrayList<Sequence> ax, ArrayList<Sequence> ay){
        if (ax.containsAll(ay))
            return true;
        else return false;
    }
    
    public static boolean compareRule(IfThenRule x, IfThenRule y){
        if(x.toString().equals(y.toString()) )
            return true;
        else return false;
    }
    
    public static void applyRules1(Map<Integer, IfThenRule> rules, ArrayList<Sequence> userGoldSequences, OntoRepresentation ontology){
        for (IfThenRule rule : rules.values()){
            System.out.println("========== Rule ==========");
            System.out.println(rule.toString());
            Set<Integer> nextObjects = new HashSet();
            System.out.println("---- learned sequences");
            int count=0;
            for (AppariementSolution lseq : rule.Msequences){
                    int lastMatchPos = lseq.appariement[lseq.appariement.length-1];
                    // list of predicted objects
                    nextObjects.add(lastMatchPos);
                    count++;
            }
            
            System.out.println(count+" sequences are already matched ..."); 
           
            System.out.println("---- gold sequences");
//            System.out.println("prefix: "+rule.prefixToString());
            
            // find last matched sequences (resultMatching.getSecond()) and last matched position in each sequence (esultMatching.getFirst())
            Set userGoldSequencesSet = new HashSet(userGoldSequences);
            Pair<ArrayList<Integer>, Set<Sequence>> resultMatching = Miner.findMatchingGold(rule.prefix , userGoldSequencesSet );
            
            System.out.println(resultMatching.getSecond().size()+" sequences are fired ...");            
            
            // NOW APPLY RULE CONCLUSION ON LAST POSITION OF EACH SEQUENCE AND CALCULATE MEASURES
            int i=0;
            int perfect=0;
            int incorrect=0;
            for (Sequence seq:resultMatching.getSecond()){
//                System.out.println(rule);
//                System.out.println(seq.toString());
                int realBeforeObjectPos = resultMatching.getFirst().get(i);
                int realNextObject = nexStepObject(realBeforeObjectPos, seq, rule.conclusion.get(0));
//                System.out.println("realBeforeObject: "+realBeforeObject);
//                System.out.println("realNextObject: "+realNextObject);
                
                if (realNextObject!=0 && nextObjects.contains(realNextObject) ){
                    perfect++;
                }
                else
                    incorrect++;
                
                i++;
            }

            System.out.println(perfect+" correctly found recommendation and "+incorrect+" not found.");
            System.out.println("==========================");
        }
    
    }
    
    public static String applyRules2(Map<Integer, IfThenRule> rules, ArrayList<Sequence> userGoldSequences, OntoRepresentation ontology, int topNRules, int topKItems){
        // NOW APPLY RULE CONCLUSION ON LAST POSITION OF EACH SEQUENCE AND CALCULATE MEASURES
        
//        System.out.println("---- recommendation simulation over the Gold standard ...");
        // find last matched sequences (resultMatching.getSecond()) and last matched position in each sequence (esultMatching.getFirst())
        
        // model metrics
        float MAP=0;
        float Recall1=0;
        float Recall2=0;
        float Recall=0;
        float Precision=0;
        float FMeasure=0;
        int hit1=0;
        int hit2=0;

        for (Sequence seq:userGoldSequences){
            // Sequence to predict
//            System.out.println("Sequence to predict:");
//            System.out.println(seq.toString());
            
            // Metrics intit
            
            int perfect=0;
            int incorrect=0;
            // a fired perfect rule with their full recommendations for each sequence
            // (rule, recommendations)
            ArrayList<Pair<IfThenRule,ArrayList<Integer>>> setOfRules = new ArrayList();
            HashMap<IfThenRule,ArrayList<Integer>> recommendedObjectsRules = new HashMap();
        
            // convert sequence in a set for the old matching
            Set user1Set = new HashSet();
            user1Set.add(seq);
            
            int nFiredRules=0;
            
            HashMap<IfThenRule, Float> rulesToSort = new HashMap();
            ArrayList<IfThenRule> rulesBySequence=new ArrayList();
            
            Set<Integer> realNextObjects = new HashSet();
            
//            System.out.println();
//            System.out.println("Fired rules:");
//            int countI = 0;
            IfThenRule newRule = new IfThenRule();
            for (IfThenRule rule : rules.values()){                
                // map the sequence with the rule
                Pair<ArrayList<Integer>, Set<Sequence>> resultMatchingPrefix = Miner.findMatchingGold(rule.prefix , user1Set );
                Pair<ArrayList<Integer>, Set<Sequence>> resultMatchingTotal = Miner.findMatchingGold(rule.pattern , user1Set );
                Set<Integer> nextObjects = new HashSet();                    
                
                // if the sequence match the rule, fire the rule
                if (resultMatchingPrefix.getSecond().size()>0){
                    nFiredRules++;
                    int sumUtility = rule.getUtility();
                    sumUtility=sumUtility+1;
                    rule.setUtility(sumUtility);
//                        System.out.println(rule.getUtility());
//                        break;
                    // get the last matched position
                    int realBeforeObjectPos = resultMatchingPrefix.getFirst().get(0);
                    // object to recommend
                    
                    
                    // get already matched solutions from rule.Msequences
                    for (AppariementSolution lseq : rule.Msequences){
//                        int lastMatchPos = lseq.appariement[lseq.appariement.length-1];
                        int lastMatchPos = lseq.appariement[lseq.appariement.length-1]-1;
//                        System.out.println("latest matched pos: "+lastMatchPos);
//                        System.out.println("latest pos in sequence:"+(lseq.sequenceUtilisateur.objects.size()-1));
                        int lastmatchedObject=0;
                        if (! (lastMatchPos>=lseq.sequenceUtilisateur.objects.size())){
                            lastmatchedObject=lseq.sequenceUtilisateur.objects.get(lastMatchPos);
                            nextObjects.add(lastmatchedObject);
                        }
//                        System.out.print(lastmatchedObject+", ");
                        // list of the proposed objects
                        
                    }
                    // get the next object that matches with the rule's conclusion
                    int realNextObject = nexStepObject(realBeforeObjectPos, seq, rule.conclusion.get(0));
                    if (realNextObject != 99999999)
                        realNextObjects.add(realNextObject);
                    
//                    System.out.println("now: "+realNextObject);
//                    System.out.println("next: "+nextObjects);
                    
                    // **** IF PERFECT MATCH ! ****
                    if (realNextObject!=99999999 && nextObjects.contains(realNextObject) && resultMatchingTotal.getFirst().size()>0){
                        perfect++;
                        ArrayList<Integer> nobj = new ArrayList(nextObjects);
//                        for (int countj=0;countj<nextObjects.size();countj++)
//                            nobj.add(nextObjects.);
                        
                        Pair<IfThenRule,ArrayList<Integer>> pair = new Pair (rule, nobj);
                        setOfRules.add(pair);
                        recommendedObjectsRules.put(rule, nobj);
                    }
                    else
                        incorrect++;
                    
                    // Diversity for each sequence
                    for (Pair<IfThenRule,ArrayList<Integer>> L2 : setOfRules){
                        float Diversity=0;
                        int count=1;
                        for (Pair<IfThenRule,ArrayList<Integer>> L1 : setOfRules){
                            if (!L2.equals(L1)){
                                count++;
                                ArrayList<Integer> recomL2 = L2.getSecond();
                                ArrayList<Integer> recomL1 = L1.getSecond();
                                recomL2.removeAll(recomL1);
                                if (L2.getSecond().size()==0){
                                    Diversity=0;
                                }
                                else
                                    Diversity = Diversity+(recomL2.size()/L2.getSecond().size());
                                // The ratio L2/L1 corresponds to the fraction of elements in the list L2 that are not in the list L1. 
                                // Diversity = 0 means that the lists of rule valid recommendations are perfectly equals and no diversity
                                // Diversity = 1 means that the lists of rule valid recommendations are not equals and there is valid diversity
                            }
                        }
                        // Diversity is a mean diversiry with all other rule
//                        System.out.println("Diversity: "+Diversity+" Count: "+count);
                        Diversity = (float)Diversity/(float)count ;
//                        System.out.println("Average Diversity: "+Diversity);
                        L2.getFirst().setDiversity(Diversity);
        //                System.out.println(Diversity);
                    }
                    
                    
                    
//                    System.out.println(rule.toString());
                    float confidence = rule.getConfidence();
                    float support=rule.getSupport();
                    float size=rule.getSize();
                    float generality=rule.getGenerality();
                    float diversity=rule.getDiversity();
//                    System.out.println("(C:"+confidence+") (S:"+support+") (L:"+size+") (G:"+generality+") (D:"+diversity+")");
                    //
                    
                    float[] metrics={confidence, support, size, generality, diversity};
                    float sumMetric = confidence*1000000+ support*10000+ size*100+ generality*10+ diversity*1;
                            
//                    rulesToSort.put(rule, metrics[0]);
                    rulesToSort.put(rule, sumMetric);
                    rulesBySequence.add(rule);
                    
                    
//                    rankRules(ruleToSort);
                
                }// end if where there is a match on prefix
//                countI ++;
            } //end rule
//            System.out.println();
            
            
            
            // RANK RULES HERE
            Map<IfThenRule, Float> map = sortByValues(rulesToSort);
            
            // SAVE RANKING IN ranks ArrayList<Pair<IfThenRule, Integer>>
            ArrayList<Pair<IfThenRule, Integer>> ranks = new ArrayList();
            Iterator iterator2 = map.entrySet().iterator();
            float rank=100;
            int i=0;
            
//            System.out.println("###### SORTED RULES:");
            while(iterator2.hasNext()) {
                Map.Entry me2 = (Map.Entry)iterator2.next();
                for (IfThenRule rs:rulesBySequence){
                    if (me2.getKey().toString().equals(rs.toString())){
                        Pair<IfThenRule, Integer> r = new Pair(rs, rank);
                        float sumRank = rs.getRank();
                        sumRank=sumRank+rank;
                        rs.setRank(sumRank);
                        ranks.add(r);
                        break;
//                        System.out.println(rs+": "+rank);
                    }
                }
//                System.out.println((float)rank - (float)100/ (float)(map.size()-1));
                float step = (float)rank - (float)100/ (float)(rulesToSort.size()-1);
//                rank=Math.round(step*100)/100;
                rank=step;
                if (rank<1 || rulesToSort.size()-1==0)
                    rank=0;
//                System.out.println(rank);
            }
            
            
            
            // CALCULATE TOP N ITEMS
//            System.out.println();
//            System.out.println("==============================================");
//            System.out.println("Rules recommendations: ");
//            System.out.println("Workflow Sequence: ");
//            System.out.println(seq.toString());
//            System.out.println("...");

            Set<Integer> recommends = new HashSet();
            HashMap<IfThenRule, Float> ruleRankI = new HashMap();
            HashMap<Integer, Float> itemRankI = new HashMap();
            
            Iterator it = recommendedObjectsRules.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                for (IfThenRule rs:rulesBySequence){
                    if (pair.getKey().toString().equals(rs.toString())){
//                        System.out.println(rs.toString());
                        
                        int nRecom = 1;
                        if (pair.getValue().toString().split(",").length>1)
                            nRecom = pair.getValue().toString().split(", ").length;
//                        System.out.println(nRecom);
//                        System.out.println(rs.getConfidence());
                        float rankI = (float)rs.getConfidence()/(nRecom*(float)recommendedObjectsRules.size());
//                        System.out.println(rankI);
                        
                        ruleRankI.put(rs, rankI);
                        break;
//                        System.out.println(rs+": "+rank);
                    }
                }
//                System.out.println(pair.getValue());
            }
            
            
            Iterator iterator3 = recommendedObjectsRules.entrySet().iterator();
//            System.out.println("items ranking ...");
            
//            ArrayList<Pair<Integer, Float>> itemsToSort = new ArrayList();
            HashMap<Integer, Float> itemsToSort = new HashMap();
            
            while (iterator3.hasNext()) {
                
                Map.Entry me3 = (Map.Entry)iterator3.next();
                Iterator iterator4 = ruleRankI.entrySet().iterator();
                while (iterator4.hasNext()) {
                    Map.Entry me4 = (Map.Entry)iterator4.next();
                    if (me3.getKey().equals(me4.getKey())){
                        int item=0;
                        float rankItem=Float.parseFloat(me4.getValue().toString());
                        
                        if (me3.getValue().toString().replace("[", "").replace("]", "").split(", ").length>0){
                            String[] values = me3.getValue().toString().replace("[", "").replace("]", "").split(", ");
                            for (String itemList : values){
//                                System.out.println("itemList: "+itemList);
                                if (!itemList.equals("")){
                                    if (itemsToSort.keySet().contains(Integer.parseInt(itemList))){
                                        if ( itemsToSort.get(Integer.parseInt(itemList)) < rankItem)
                                            itemsToSort.put(Integer.parseInt(itemList), rankItem);
                                        else continue;
                                    }
                                    else itemsToSort.put(Integer.parseInt(itemList), rankItem);
                                }

                            }
                        }
                            
                        else{
                            if (itemsToSort.keySet().contains(Integer.parseInt(me3.getValue().toString().replace("[", "").replace("]", "")))){
                                if ( itemsToSort.get(Integer.parseInt(me3.getValue().toString().replace("[", "").replace("]", ""))) < rankItem)
                                    itemsToSort.put(Integer.parseInt(me3.getValue().toString().replace("[", "").replace("]", "")), rankItem);
                                else continue;
                            }
                            else itemsToSort.put(Integer.parseInt(me3.getValue().toString().replace("[", "").replace("]", "")), rankItem);
                        }

                    }
                }
            }
                    
            
            // SORTED ITEMS !!!
//            System.out.println();
            Map<Integer, Float> sortedItems = sortByValues(itemsToSort);
            
//            int[] topitems = {5, 10, 20, 30, 40, 50};
//            int N=5;
//            int N=10;
//            int N=20;
//            int N=40;
//            int N=40;
//            int N=50;
//            System.out.println("TOP "+N+":");
            ArrayList<Integer> keys = new ArrayList<Integer>(sortedItems.keySet());
            Collections.reverse(keys);
            
            ArrayList<Integer> topK = new ArrayList();
            
            // K is the top K items to recommend
            
            if (keys.size()<topKItems)
                topK=keys;
            else
                topK = new ArrayList<Integer>(keys.subList(0, topKItems));
//            System.out.println(topN);
//            
//            System.out.println("Recommended objects: ");
//            System.out.println(keys);
//            
//            System.out.println("Real objects: ");
//            System.out.println(realNextObjects);
//            
           
//            System.out.println("==============================================");
            
            
            // Precision
            if (nFiredRules!=0)
                Precision=(float)perfect/(float)(perfect+incorrect);
            else Precision=0;
            
            ArrayList<Integer> truths = new ArrayList(realNextObjects);
            
            // Recall 1
            if (topK.containsAll(truths)){
                hit1++;  
            }
            
            // Recall 2
            for (int t:truths){
                if (topK.contains(t)){
                    hit2++;
                    break;
                }
            }
                
            
            // Add precisions to rule
            for (IfThenRule rs:rulesToSort.keySet()){
                float p = rs.getPrecision()+Precision;
                rs.setPrecision(p);
            }

            // DISPLAY TOP K% rule of metrics
            ArrayList<ArrayList> oneRecomm = new ArrayList();
            ArrayList SR = new ArrayList();
            ArrayList topSR = new ArrayList();
            ArrayList SC= new ArrayList();
            ArrayList topSC= new ArrayList();
            ArrayList SS= new ArrayList();
            ArrayList topSS= new ArrayList();
            ArrayList SL= new ArrayList();
            ArrayList topSL= new ArrayList();
            ArrayList SG= new ArrayList();
            ArrayList topSG= new ArrayList();
            ArrayList SD= new ArrayList();
            ArrayList topSD= new ArrayList();
            ArrayList SP= new ArrayList();
            ArrayList topSP = new ArrayList();
            
            ArrayList Ssteps= new ArrayList();
            ArrayList topsteps = new ArrayList();
            ArrayList SFreqData= new ArrayList();
            ArrayList topFreqData = new ArrayList();
            ArrayList SFreqMetaData= new ArrayList();
            ArrayList topFreqMetaData = new ArrayList();
            ArrayList SFreqProgs= new ArrayList();
            ArrayList topFreqProgs = new ArrayList();
            ArrayList SFreqRelations= new ArrayList();
            ArrayList topFreqRelations = new ArrayList();
            
            for (Pair<IfThenRule, Integer> ruleRanked:ranks){
                ArrayList resultsRecomDetai = new ArrayList();
                float confidence = ruleRanked.getFirst().getConfidence();
                float support=ruleRanked.getFirst().getSupport();
                float size=ruleRanked.getFirst().getSize();
                float diversity=ruleRanked.getFirst().getDiversity();
                
                float generality=ruleRanked.getFirst().getGenerality();
               
                ArrayList<Integer> stepsL = Miner.conceptsToSteps(ruleRanked.getFirst().pattern, hierarchyRepresentation);
                Set<Integer> stepsS = new HashSet<Integer>(stepsL);
                float steps = (float)stepsS.size()/7;
                
                float FreqData=ruleRanked.getFirst().getFreqData();
                float FreqMetaData=ruleRanked.getFirst().getFreqMetaData();
                float FreqProgs=ruleRanked.getFirst().getFreqProgs();
                float FreqRelations=ruleRanked.getFirst().getFreqRelations();
                
//                System.out.println("steps: "+stepsS);
//                float depth=ruleRanked.getFirst().getGenerality();
//                System.out.println(ruleRanked.getFirst().toString());
//                System.out.println("(Rank: "+ruleRanked.getSecond()+") (Confidence:"+confidence+") (Support:"+support+") (Size:"+size+") (Generality:"+generality+") (Diversity:"+diversity+") (Precision:"+Precision+") ");
//                System.out.println(ruleRanked.getSecond()+"\t"+confidence*100+"\t"+support+"\t"+size+"\t"+generality+"\t"+diversity+"\t"+Precision*100);
                // SR
                resultsRecomDetai.add(ruleRanked.getSecond());
                SR.add(ruleRanked.getSecond());
                // SC
                resultsRecomDetai.add(confidence*100);
                SC.add(confidence*100);
                // SS
                resultsRecomDetai.add(support);
                SS.add(support);
                // SL
                resultsRecomDetai.add(size);
                SL.add(size);
                // SG
                resultsRecomDetai.add(generality);
                SG.add(generality);
                // SD
                resultsRecomDetai.add(diversity);
                SD.add(diversity);
                // SP
                resultsRecomDetai.add(Precision*100);
                SP.add(Precision*100);
                
                resultsRecomDetai.add(steps);
                Ssteps.add(steps);
                resultsRecomDetai.add(FreqData);
                SFreqData.add(FreqData);
                resultsRecomDetai.add(FreqMetaData);
                SFreqMetaData.add(FreqMetaData);
                resultsRecomDetai.add(FreqProgs);
                SFreqProgs.add(FreqProgs);
                resultsRecomDetai.add(FreqRelations);
                SFreqRelations.add(FreqRelations);
                
                
                oneRecomm.add(resultsRecomDetai);
            }
//            System.out.println(oneRecomm);
//            final float top=topNRules*oneRecomm.size() /100;
//            System.out.println("topk: "+top);
//            DISPLAY TOP K %
            for (int d=0; d<topNRules;d++)
                if (d<oneRecomm.size()){
                    topSR.add(oneRecomm.get(d).get(0));
                    topSC.add(oneRecomm.get(d).get(1));
                    topSS.add(oneRecomm.get(d).get(2));
                    topSL.add(oneRecomm.get(d).get(3));
                    topSG.add(oneRecomm.get(d).get(4));
                    topSD.add(oneRecomm.get(d).get(5));
                    topSP.add(oneRecomm.get(d).get(6));
                    topsteps.add(oneRecomm.get(d).get(7));
                    topFreqData.add(oneRecomm.get(d).get(8));
                    topFreqMetaData.add(oneRecomm.get(d).get(9));
                    topFreqProgs.add(oneRecomm.get(d).get(10));
                    topFreqRelations.add(oneRecomm.get(d).get(11));
//                    System.out.println((d+1)+": "+ oneRecomm.get(d));
//                    for (int s=0; s<oneRecomm.get(d).size();s++){
//                        System.out.print(oneRecomm.get(d).get(s)+"\t");
//                    }
                        
                }
//            System.out.println();  
            
            // DISPLAY AVERAGE OF TOP N rules %           
            // confidence, support, generality, steps, freqData, freqMetaData, freqProgs, freqRel
            System.out.println((float)average(topSC)/100+"\t"+average(topSS)+"\t"+average(topSG)*100+"\t"
                +average(topsteps)*100+"\t"+average(topFreqData)*100+"\t"+average(topFreqMetaData)*100+"\t"+average(topFreqProgs)*100+"\t"+average(topFreqRelations)*100);
            
//            System.out.println(topSP);
            MAP=MAP+Precision;
            
        }// end Sequence 
        
        
        
        MAP=(float)MAP/(float)userGoldSequences.size()*100;
        Recall1=(float)hit1/(float)userGoldSequences.size();
        Recall2=(float)hit2/(float)userGoldSequences.size();
        Recall=(float)(Recall1+Recall2)/2;
        FMeasure=(float)( (float)(2*MAP*Recall)/(float)(MAP+Recall) )/2;
        
        System.out.println();
        System.out.println("######### Statistics about simulated recommendation:");
        System.out.println("MPR\tSutility\tPrecision\tNumber of recommendations");
        
                
        // DISPLAY TOP K %
        ArrayList<ArrayList> AllRecomm = new ArrayList();
        ArrayList RC = new ArrayList();
        ArrayList RS1 = new ArrayList();
        ArrayList RS2 = new ArrayList();
        ArrayList RM = new ArrayList();
        ArrayList RP = new ArrayList();
        ArrayList RU = new ArrayList();
        for (IfThenRule rule : rules.values()){
            ArrayList ruleRecomm = new ArrayList();
            float mpr = (float)rule.getRank()/(float)rule.getUtility();
//            System.out.println(rule.confidence*100+"\t"+rule.support+"\t"+(((float)rule.utility/(float)userGoldSequences.size())*100)+"\t"+mpr);
            // RC
            ruleRecomm.add(rule.getConfidence()*100);
            RC.add(rule.getConfidence()*100);
            // RS1
            ruleRecomm.add(rule.getSupport());
            RS1.add(rule.getSupport());
            // RS2
            ruleRecomm.add((((float)rule.getUtility()/(float)userGoldSequences.size())*100));
            RS2.add((((float)rule.getUtility()/(float)userGoldSequences.size())*100));
            // RM
            ruleRecomm.add(mpr);
            RM.add(mpr);
            
            ruleRecomm.add(((float)rule.getPrecision()/(float)rule.getUtility())*100);
            RP.add(((float)rule.getPrecision()/(float)rule.getUtility())*100);
            
            ruleRecomm.add(rule.getUtility());
            RU.add(rule.getUtility());
            
            AllRecomm.add(ruleRecomm);
        }
       
        // MPR, CONFIDENCE, COVERAGE, SUPPORT
        // MPR
//        Collections.sort(RM);
//        for (int r=0;r<topk;r++){
        for (int r=0;r<AllRecomm.size();r++){
          // MPR(3), Coverage(2), Precision(4), Utility(5)
          System.out.println(AllRecomm.get(r).get(3)+"\t"+AllRecomm.get(r).get(2)+"\t"+AllRecomm.get(r).get(4)+"\t"+AllRecomm.get(r).get(5));
//            if (r<RM.size())
//                System.out.println(RM.get(r));
        }
        
        
        // WE HAVE TO RE SORT TO GET THE BEST TOP RULES ! ... stand by for the moments
//        for (int r=0; r<topk;r++)
//            if (r<AllRecomm.size()){
////                System.out.println((r+1)+": "+ AllRecomm.get(r));
//                for (int m=0;m<AllRecomm.get(r).size();m++)
//                    System.out.print(AllRecomm.get(r).get(m)+"\t");
//                System.out.println();
//            }
        
//        System.out.println();
//        System.out.println(average(RC)+"\t"+average(RS1)+"\t"+average(RS2)+"\t"+average(RM));
        
        System.out.println();
        
        String stats = "\nMAP: "+average(RP)+"%\nRecall1: "+Recall1*100+"%\nRecall2: "+Recall2*100+"%\nRecall: "+Recall*100+"%\nFMeasure: "+FMeasure*100+"%";
        
        System.out.println(stats);
//        System.out.println("MAP-all: "+average(RP));
//        System.out.println("Recall1: "+Recall1);
//        System.out.println("Recall2: "+Recall2);
//        System.out.println("Recall: "+Recall);
////        System.out.println("Hits: "+hit);
//        System.out.println("FMeasure: "+FMeasure);
//        System.out.println();
//        
        System.out.println("###############################################################");
        return stats;
    }
    
    public static float average(ArrayList<Float> list){
        float avg = 0;
        for (int i = 0; i < list.size(); i++)  {
            avg += list.get(i) ; 
        }
        return avg/list.size();
    }
    
    /**
     *
     * @param list
     * @return
     */
    public static float averageInt(ArrayList<Integer> list){
        float avg = 0;
        for (int i = 0; i < list.size(); i++)  {
            avg += list.get(i) ; 
        }
        return avg/list.size();
    }
    
    private static HashMap sortByValues(HashMap map) { 
       List list = new LinkedList(map.entrySet());
       // Defined Custom Comparator here
       Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
               return ((Comparable) ((Map.Entry) (o1)).getValue())
                  .compareTo(((Map.Entry) (o2)).getValue());
            }
       });

       // Here I am copying the sorted list in HashMap
       // using LinkedHashMap to preserve the insertion order
       HashMap sortedHashMap = new LinkedHashMap();
       for (Iterator it = list.iterator(); it.hasNext();) {
              Map.Entry entry = (Map.Entry) it.next();
              sortedHashMap.put(entry.getKey(), entry.getValue());
       } 
       return sortedHashMap;
  }
    
    
    
    
//    public static ArrayList<IfThenRule> rankRules (List<Pair<Integer, Float>> rules){
//        ArrayList<IfThenRule> sorted = new ArrayList();
//        
//        Collections.sort(rules, new Comparator<Pair<IfThenRule, Float>>() {
//            @Override
//            public int compare(final Pair<String, Integer> o1, final Pair<String, Integer> o2) {
//                // TODO: implement your logic here
//            }
//        });
//        
////        Set<Float> sortC = new HashSet();
//        
//        
//        
//        return sorted;
//    }
    
    public static int nexStepObject (int realBeforeObjectPos, Sequence seq, int predictedNextConcept) {
        int i=realBeforeObjectPos;
        while (i<seq.objects.size()){
            if( hierarchyRepresentation.isConceptEqualOrDescendant( predictedNextConcept, seq.objects.get(i) ) ){
                return seq.objects.get(i);
            }
            else
                i++;
        }
        return 99999999;
    }
    
    
    
}
