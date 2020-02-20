package ca.uqam.gdac.framework.miner;

import ca.uqam.gdac.framework.library.Library;
import ca.uqam.gdac.framework.matcher.Matcher;
import static ca.uqam.gdac.framework.matcher.Matcher.lastMatch;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import legacy.Operation;
import legacy.Pair;
import ontologyrep2.Concept;
import ontologyrep2.OntoRepresentation;
import ontologyrep2.Relation;
import ontologyrep2.Triplet;
import ontopatternmatching.AppariementSolution;
import ontopatternmatching.JobBlock;
import ontopatternmatching.Motif;
import ontopatternmatching.Sequence;
import ontopatternmatching.SequenceMatcher;
import legacy.PatternGenerator;
import legacy.PropertiesAndSubject;
import org.apache.commons.math3.analysis.function.Log;

/**
 *Miner realizes the mining of user sequences regarding to a minimum support.<br/>
 * Extract the frequent patterns from the users sequences.
 * 
 * @author Frapin Kevin
 *
 */
public class Miner 
{
	//-------------------------------------------------------------- Attributes
	//private static HierarchyRepresentation<Integer> hierarchyRepresentation;
        public static OntoRepresentation hierarchyRepresentation;
	
	// Minimum support (relative)
	private static double minSup;
        private static int minSup2;
        private static ArrayList<String[]> index_items = new ArrayList<>();
        public static int nRules=0;
        public static Map<Integer, IfThenRule> rules = new HashMap();
        public static Map<Integer, IfThenRule> allRules = new HashMap();
        public static Map<Integer, IfThenRule> bannedRules = new HashMap();
        public static ArrayList<Integer> solutionsToBeApplied = new ArrayList();
	
	
	//------------------------------------------------------- Setters / Getters
	/*private static void setHierarchyRepresentation( final HierarchyRepresentation<Integer> hierarchyRepresentation )
	{
		Miner.hierarchyRepresentation = hierarchyRepresentation;
	}*/
        
        /**
         * 
         * @param hierarchyRepresentation 
         */
        private static void setHierarchyRepresentation( final OntoRepresentation hierarchyRepresentation )
	{
		Miner.hierarchyRepresentation = hierarchyRepresentation;
	}

	private static void setMinSup( final double minSup )
	{
		Miner.minSup  = (double) minSup;
                Miner.minSup2 = (int)(((double)minSup)*10d);
	}
	
	//--------------------------------------------------- Public static methods
        public static ArrayList<Motif> addConceptC(final Motif pattern, final OntoRepresentation hierarchyRepresentation, int minLevel, int level){
            setHierarchyRepresentation( hierarchyRepresentation );
            return addConceptC(pattern, minLevel, level);
	}
        
        public static ArrayList<Motif> addPropertyC(final Motif pattern, final OntoRepresentation hierarchyRepresentation){
            //setHierarchyRepresentation( hierarchyRepresentation );
            return addPropertyC( pattern );
	}
        
        public static ArrayList<Motif> findFrequentPatternsDF( final ArrayList<Sequence> userSequences, final double minSup, final OntoRepresentation hierarchyRepresentation, int minLevel ){
            System.out.println("Level\tTime\t\tSize");
            
            // Initialize the miner
            Miner.initialize(userSequences, minSup, hierarchyRepresentation);      
            ArrayList<Sequence> sequences = Library.getUserSequences2();
            //pour chaque sequence on va lui creer une structure d'appriement
            AppariementSolution[] appariements = new AppariementSolution[sequences.size()];
            for(int i=0;i<sequences.size();i++){
                appariements[i] = new AppariementSolution();
                appariements[i].sequenceUtilisateur = sequences.get(i);
                appariements[i].appariement = new int[0];
            }
                        
            // Update the frequent patterns
            Miner.generateAndTestDF(new Motif(), appariements, minLevel, 0);
//            System.out.println("On a "+Library.frequentPatterns2.size()+" patterns.");
            
            /**
             * Si on a des elements dans l'index, alors on generer un fichier d'index
             */
            if(!Miner.index_items.isEmpty()){
                
                StringBuilder ss = new StringBuilder();
                ss.append("{ \"files\" : [");int i=0;
                for(String[] s : Miner.index_items){
                    if(i!=0) ss.append(",");
                    ss.append(" { \"file\":\"").append(s[0]).append(".txt\", \"concepts\":\"").append(s[1]).append("\", \"relations\":\"").append(s[2]).append("\", \"etapes\":\"").append(s[3]).append("\", \"match\":\"").append(s[4]).append( "\" } ");i++;
                }
                ss.append("] }");
                
                Writer writer = null;
                try {
                    writer = new BufferedWriter(new OutputStreamWriter(
                          new FileOutputStream("visualisations/index.txt"), "utf-8"));
                    writer.write(ss.toString());
                } 
                catch (IOException ex) {
                  // report
                } 
                finally {
                   try { if(writer!=null) writer.close();} catch (IOException ex) {}
                }   
            }
            
            return Library.getFrequentPatterns2();
        }
        
        public static ArrayList<Motif> generateCandidates2(final ArrayList<Motif> patterns, final OntoRepresentation hierarchyRepresentation, int minLevel, int level){
            setHierarchyRepresentation( hierarchyRepresentation );//a quoi sert cette ligne ?
            ArrayList<Motif> candidates = new ArrayList<>();
            for (Motif sequence : patterns ) {
                ArrayList<Motif> generateCandidates2 = generateCandidates2(sequence, minLevel, level);
                for(Motif m : generateCandidates2){
                    if(!candidates.contains(m)) candidates.add(m);
                }
                
            }
            return candidates;
	}
                
        public static ArrayList<Motif> splConceptC(final Motif pattern, final OntoRepresentation hierarchyRepresentation){
		setHierarchyRepresentation( hierarchyRepresentation );
		return splConceptC(pattern);
	}
        
        public static ArrayList<Motif> splPropertyC(final Motif pattern, final OntoRepresentation hierarchyRepresentation){
            setHierarchyRepresentation( hierarchyRepresentation );
            return splPropertyC(pattern);	
	}

	//-------------------------------------------------- Private static methods
        
        private static ArrayList<Motif> addConceptC(final Motif motif, int minLevel, int level){
            return PatternGenerator.generateSequencesByACC(motif, hierarchyRepresentation, minLevel, level);
        }
        
        private static ArrayList<Motif> appendConceptC(final Motif motif, int minLevel, int level){
            return PatternGenerator.generateSequencesByDCC(motif, hierarchyRepresentation, minLevel, level);
        }
        
        private static ArrayList<Motif> addPropertyC( final Motif motif ){
            ArrayList<Motif> candidates = new ArrayList<>();
//            System.out.println("Adding properties to the pattern.");
            if(addPropertyCAllowed(motif)){
                // Get the properties which can be applied
                ArrayList<PropertiesAndSubject> possibleProperties = findPossiblePropertiesToAdd( motif );
                if(!motif.isFirstAPC()){
                    PropertiesRuler.apcFilterPropertiesBySrcPosition(motif, possibleProperties);
                    PropertiesRuler.apcFilterPropertiesById(motif, possibleProperties);
                }
                ArrayList<Motif> generateSequencesByAPC = PatternGenerator.generateSequencesByAPC(motif, possibleProperties);
                for(Motif m : generateSequencesByAPC){
                    if(!candidates.contains(m)) candidates.add(m);
                }
            }
            return candidates;
	}
        
		
	//-------------------------- GROUP : Checking of allowed operations
	// Check if a property addition is allowed on the sequence
	/*private static boolean addPropertyCAllowed( final Pattern sequence )
	{
		return ( sequence.getTransactions( ).size( ) >= 2 );
	}*/
        
        private static boolean addPropertyCAllowed(final Motif sequence){
            return ( sequence.concepts.size( ) > 1 );
        }
        
    
    
    
    private static ArrayList<Motif> generateCandidates2(final Motif motif, int minLevel, int level){
            
//            System.out.println("le motif est "+motif.toString());
            //System.exit(1);
            
            ArrayList<Motif> motifs_candidats = new ArrayList<>();
            
            // 1. addConceptC
//            System.out.println("From: " + motif.toString());
            ArrayList<Motif> addConceptC = addConceptC(motif, minLevel, level);
//            System.out.println(""+addConceptC.size()+" ajouts de concepts...");
//            System.out.println("addConceptC: " + addConceptC);
            motifs_candidats.addAll(addConceptC);
            
            // 2. appendConceptC
//            System.out.println("From: " + motif.toString());
            ArrayList<Motif> appendConceptC = appendConceptC(motif, minLevel, level);
//            System.out.println(""+appendConceptC.size()+" append de concepts...");
//            System.out.println("appendConceptC: " + appendConceptC);
//            motifs_candidats.addAll(appendConceptC);
            for(Motif m : appendConceptC){
                if(!motifs_candidats.contains(m)) motifs_candidats.add(m);
            }
            
            // 3. splConceptC
            ArrayList<Motif> splConceptC = splConceptC(motif);   
            for(Motif m : splConceptC){
                if(!motifs_candidats.contains(m)) motifs_candidats.add(m);
            }
//            
//            // 4. addPropertyC
//            ArrayList<Motif> addPropertyC = addPropertyC(motif);     
//            for(Motif m : addPropertyC){
//                if(!motifs_candidats.contains(m)) motifs_candidats.add(m);
//            }
//            // 5. splPropertyC
//            ArrayList<Motif> splPropertyC = splPropertyC(motif);
//            //System.out.println(""+splPropertyC.size()+" spe de props...");
//            for(Motif m : splPropertyC){
//                if(!motifs_candidats.contains(m)) motifs_candidats.add(m);
//            }
            
            //if(motif.relations!=null && motif.relations.size() > 1 ) System.exit(1);
            return motifs_candidats;
        }
    
        public static ArrayList<String> conceptsToString (Motif p, OntoRepresentation ontology) {
            ArrayList<String> cs = new ArrayList<String> ();
            for(ArrayList<Integer> t : p.transactions)
                for(Integer c : t)
                    cs.add(ontology.getConcept(c).getName().split("#")[1]);
             return cs;
        }

        public static ArrayList<Integer> conceptsToSteps (Motif p, OntoRepresentation ontology) {
            ArrayList<Integer> cs = new ArrayList ();
            for(ArrayList<Integer> t : p.transactions)
                for (Integer c : t){
                    String uriStep = ontology.getConceptByLevel(ontology.getConcept(c), 0).getName().split("#")[1];
                    Integer step=0;
                    switch (uriStep) {
                        case "DataCollectionStep":  cs.add(1); break;
                        case "SequenceAlignmentStep":  cs.add(2); break;
                        case "ModelSelectionStep":  cs.add(3); break;
                        case "PhylogeneticInferenceStep": cs.add(4); break;
                        case "HypothesisValidationStep":  cs.add(5); break;
                        case "TreeAnalysisStep":  cs.add(6); break;
                        case "TreeVisualizationStep":  cs.add(7); break;
                    }

            }
            return cs;
        }

        public static ArrayList<String> relationsToString (Motif p, OntoRepresentation ontology) {
            ArrayList<String> cs = new ArrayList<String> ();
            Integer d,r;
            String rl;
            for (Integer[] t : p.relations){
                rl=ontology.getRelation(t[0]).getName().split("#")[1];
                d=t[1];
                r=t[2];
                String triplet=d+";"+r+";"+rl;
                cs.add(triplet);
            }
            return cs;
        }
        
        /**
         * 
         * @param userSequences
         * @param minSup
         * @param hierarchyRepresentation 
         */
        private static void initialize( final ArrayList<Sequence> userSequences, final double minSup, final OntoRepresentation hierarchyRepresentation ) 
	{
            Library.initialize( );
            Library.setUserWorkflows(userSequences);
            Miner.setMinSup(  minSup );
            Miner.setHierarchyRepresentation(hierarchyRepresentation );
	}
        
        private static ArrayList<Motif> splConceptC(final Motif sequence){
            // New candidates
            ArrayList<Motif> candidates = new ArrayList<>();
            if(splConceptCAllowed(sequence)){
                ArrayList<Motif> generateSequencesBySCC = PatternGenerator.generateSequencesBySCC(sequence, hierarchyRepresentation );
                if (! generateSequencesBySCC.isEmpty())
//                    System.out.println("Add to candidates: " + generateSequencesBySCC);
                    for(Motif m : generateSequencesBySCC){
                        if(!candidates.contains(m)) candidates.add(m);
                    }
            }
            return candidates;
	}
                
        private static boolean splConceptCAllowed( final Motif sequence ){
            Operation lastOperation = sequence.lastOperation;
            return ( lastOperation == Operation.AC || lastOperation == Operation.SC );
	}
	        
        private static ArrayList<Motif> splPropertyC(final Motif pattern){
            ArrayList<Motif> candidates = new ArrayList<>( );
            if(splPropertyCAllowed(pattern)){
                //System.out.println("Allowed to specialize property");
                // Get the properties that could be used to specialize the last property
                ArrayList<Relation> possibleProperties = findPossiblePropertiesToSpl( pattern );
                //System.out.println("possible properties:"+possibleProperties.size());
                // Keep the properties that can really be applied between the two concepts
                possibleProperties = PropertiesRuler.spcFilterPropertiesByConcepts( pattern, possibleProperties,  hierarchyRepresentation );
                //System.out.println("possible properties2:"+possibleProperties.size());
                if(!pattern.onlyOneAPC()){
                    // Keep the properties that have an ID < property sup
                    possibleProperties = PropertiesRuler.filterPropertiesByPropertySup(possibleProperties, pattern.propertySup);
                }
                ArrayList<Motif> generateSequencesBySPC = PatternGenerator.generateSequencesBySPC( pattern, possibleProperties);
                for(Motif m : generateSequencesBySPC){
                    if(!candidates.contains(m)) candidates.add(m);
                }
                //System.out.println("sequencesSPC:"+possibleProperties.size());
            }
            return candidates;
	}
        
        private static boolean splPropertyCAllowed(final Motif sequence){
            Operation lastOperation = sequence.lastOperation;
            return ( lastOperation == Operation.AP || lastOperation == Operation.SP );
	}

	// Find the properties that could be added to the sequence
	// (Properties between the last concept and the others concepts)
	//
	// Return: 
	//  A list composed of pairs containing <possible properties, position of source concept>
	private static ArrayList<PropertiesAndSubject> findPossiblePropertiesToAdd(final Motif workflow){
//            System.out.println("workflow.concepts " + workflow.concepts);
//            ArrayList<Integer> lastTransaction = workflow.concepts.get(workflow.concepts.size( ) - 1);
            Integer lastConcept = workflow.concepts.get(workflow.concepts.size()-1);
            Concept objet = hierarchyRepresentation.getConcept(lastConcept);
            // Concepts of the sequence MINUS the last concept
            ArrayList<Integer> sourceConcepts = new ArrayList<>(workflow.concepts);
            sourceConcepts.remove(sourceConcepts.size()-1);
            // Recovery of the existing properties
            ArrayList<PropertiesAndSubject> possibleProperties = new ArrayList<>();
            
            for ( int i = 0; i < sourceConcepts.size(); i++){
                // Find the existing properties between other concept and last concept
                Integer sourceConcept = sourceConcepts.get(i);
                Concept sujet = hierarchyRepresentation.getConcept(sourceConcept);
                ArrayList<Triplet> properties = hierarchyRepresentation.getRootProperties(sujet, objet);
                if(properties!=null){
                    ArrayList<Relation> rels = new ArrayList<>();
                    for(Triplet t : properties){
                        rels.add(t.relation);
                    }
                    PropertiesAndSubject propertiesAndSubject = new PropertiesAndSubject(new ArrayList<>(rels), i);
                    possibleProperties.add(propertiesAndSubject);
                }
            }
            //System.out.println("There are "+possibleProperties.size()+" possible properties.");
            return possibleProperties;
	}
	        
        private static ArrayList<Relation> findPossiblePropertiesToSpl(final Motif sequence)	{
            // Get the property to specialize
            Integer propertyToSpecialize = sequence.lastAppliedProperty;
            // Get all the children of this property (they represent the possible specializations)
            ArrayList<Relation> propertyChildren = hierarchyRepresentation.getPropertyChildren(propertyToSpecialize);
            return propertyChildren;
        }
        
        
        /**
         * Modification de la methode pour qu'elle 
         * @param pattern
         * @param userSequences
         * @return 
         */
        public static AppariementSolution[] findMatchingSequences(Motif motif, final AppariementSolution[] previous_solutions, int[] n){
//                System.out.println("motif: " + motif.concepts);
                //mise a jour de la stucture
                motif.updateAppariementStructureWithCurrentPattern();
//                System.out.println("motif: " + motif.concepts);

                SequenceMatcher m = new SequenceMatcher();

                AppariementSolution[] solutions = new AppariementSolution[previous_solutions.length];
                int i=0;
                
//                System.out.println("motif: "+motif.toString());
//                System.out.println("previous_solutions: "+previous_solutions.length);
//                for (AppariementSolution pre:previous_solutions )
//                    System.out.println(pre.sequenceUtilisateur.objects.toString());
//                System.out.println("n: ");
//                
                for (AppariementSolution previous_matching : previous_solutions){
//                    System.out.println("\tprevious_matching - appariement: " + previous_matching.appariement);
//                    System.out.println("\tprevious_matching - motif: " + previous_matching.motif);
//                    System.out.println("\tprevious_matching - sequenceUtilisateur: " + previous_matching.sequenceUtilisateur);
//                    System.out.println("\tprevious_matching - parcours: " + previous_matching.parcours);
//                    
                    //va nous permettre de savoir si des solutions de n-1 on ete modifiees.
                    //necessaire dans le cas d'ajout de relations changeant la position du domaine et 
                    //impliquant un chnagement de position de codomaine sinon echec du matching
                    boolean[] alterations_de_solutions = new boolean[motif.empty_structure.pile_de_taches.size()];
                    //on construit un nouveau tableau de solutions

                    AppariementSolution solution = new AppariementSolution();
                    
                    solution.appariement = new int[motif.empty_structure.pile_de_taches.size()];
                    
                    
//                    System.out.println("previous_matching.appariement: "+previous_matching.appariement.length);
                    
                    System.arraycopy(previous_matching.appariement, 0, solution.appariement, 0, previous_matching.appariement.length);

                    //_matcher.reset(previous_matching.sequenceUtilisateur, solution.appariement);
                    boolean matched=false;

                    JobBlock point_de_depart = motif.empty_structure.pile_de_taches.get(motif.empty_structure.last_touched_position);

                    //ici on peut ajouter des contraintes ?
                    Random randomGenerator = new Random();
                    int randomInt = randomGenerator.nextInt(100);
                    int min = 101;//ou 96 ou 101
                    ArrayList<StringBuilder> bbb = new ArrayList<>();

                    if(randomInt>min){
                        
                    }
                    else{
                        SequenceMatcher.html_output = false;
//                        
//                        System.out.println("motif: " + motif.concepts);
//                        System.out.println("\tpoint_de_depart: " + point_de_depart);
//                        System.out.println("\tsolution.appariement: " + solution.appariement);
//                        System.out.println("\talterations_de_solutions: " + alterations_de_solutions);
//                        System.out.println("\tprevious_matching.sequenceUtilisateur: " + previous_matching.sequenceUtilisateur);
                        matched = m.tryToMatch(point_de_depart, solution.appariement, alterations_de_solutions, previous_matching.sequenceUtilisateur, hierarchyRepresentation, motif); 
                    }
                                       
                    if(matched){
                        solution.sequenceUtilisateur = previous_matching.sequenceUtilisateur;
                        solution.motif = motif;
                        solutions[i] = solution;
                        n[0]=n[0]+10;
                        i++;
                    }
		}
                //System.out.println(solutions.length+"to"+i);
                AppariementSolution[] sol = new AppariementSolution[i];
                System.arraycopy(solutions, 0, sol, 0, i);
                solutions = null;
                //System.out.println(found_A+" contre "+found_B+" elements trouves");
		return sol;
        }
 	
    
        // Return the user sequences which match the pattern
	public static Pair<ArrayList<Integer>, Set<Sequence>> findMatchingGold( Motif pattern, Set<Sequence> userSequences )
	{
		Set<Sequence> matchingSequences = new HashSet( );
//                Pair<Integer, Set<Sequence>> resultM =new Pair<Integer, Set<Sequence>>();
                
		Matcher matcher = pattern.matcher( new Sequence(), hierarchyRepresentation );
                
                ArrayList<Integer> solutions = new ArrayList();
                                
		// Find the sequences that match the pattern
		for ( Sequence userSequence : userSequences )
		{
//                    System.out.println("Useq:"+userSequence.getTransactions().size());
                    //Matcher matcher = pattern.matcher( userSequence, hierarchyRepresentation );
                    //matcher = pattern.matcher( userSequence, hierarchyRepresentation );
                    matcher.reset(userSequence);//remet a zero le matcher plutot que de faire des "new"
                    if( matcher.find( ) )
                    {
                        //System.out.println("found");    
                        // NOTE : Improve the processing by saving the match ?
                        solutions.add(matcher.lastMatch);
                        matchingSequences.add( userSequence );
                    }
		}
		return new Pair(solutions, matchingSequences);
	}
        
        //!!!!!!!!!!!
	// Recursive method used to generate and test candidates in a depth first way
	// Return the set of frequent patterns
        // faire une verification du support avant de lancer le matching
	private static void generateAndTestDF(Motif pattern, final AppariementSolution[] solutions, int minLevel, int level){
            //System.out.println("******************NEXT LEVEL***************************");
            long start = System.currentTimeMillis();
            
            // for rules !
            double minConf=0.8;
            
            // Candidates generated from the current pattern
            System.out.println("pattern: " + pattern.toString());
            ArrayList<Motif> candidates = generateCandidates2(pattern, minLevel, level);//n => n+1
//            System.out.println("To: " + candidates);
            ArrayList<Motif> Freqcandidates = new ArrayList();//n => n+1
            //System.out.println("on a "+candidates.size()+" candidats...");
            Motif[] motifs_qui_matchent = new Motif[candidates.size()];
            AppariementSolution[][] sequences_qui_matchent = new AppariementSolution[motifs_qui_matchent.length][0];
            
            int i=0;
//            System.out.println("========================== generating and testing =============================");
            
            for (Motif candidate : candidates){
                
                if (!candidate.concepts.isEmpty()) {            
                    int[] nseq = new int[1];
//                    System.out.println("Candidate: " + candidate.toString());
    //                System.out.println("Candidate Concepts: " + candidate.concepts);

                    AppariementSolution[] seq = findMatchingSequences(candidate, solutions, nseq);//on apparie des motifs n+1 avec des appariement n

                    // GENERATE RULE HERE FROM PARENT as SUBLIST and CHILD as LIST
                    ArrayList<Integer> stepsInteger = conceptsToSteps(candidate,hierarchyRepresentation);
                    ArrayList<String> candString= conceptsToString(candidate,hierarchyRepresentation);
                    ArrayList<String> patternString= conceptsToString(pattern,hierarchyRepresentation);
                    String conclusion=candString.toString().split(", ")[candString.toString().split(", ").length-1].replace("[", "").replace("]", "");

                    candidate.support = nseq[0] / 10;
                    float premissSupp = pattern.support/(float)Library.getNbUserSequences()*100;
                    float ruleSupp = candidate.support/(float)Library.getNbUserSequences()*100;
                    float conf = (float)ruleSupp/(float)premissSupp;

    //                System.out.println(stepsInteger.toString());
    //                System.out.println("LIST: ");                
    //                System.out.println(candString.toString()+": "+ ruleSupp);
    //                System.out.println(stepsInteger.toString());
    //                System.out.println("SUBLIST: ");
    //                System.out.println(patternString.toString()+": "+ premissSupp);
    //                System.out.println("CONF: ");
    //                System.out.println(conf);
    //                System.out.println(minSup2);

                    if(minSup2 == 0){
                        int minsupexact = (int)(((double)minSup*100));
    //                    minsupexact *= 10;
    //                    System.out.println("nseq[0]: "+nseq[0]);
    //                    System.out.println("minsupexact: "+minsupexact);
                        if(nseq[0] >= minsupexact){
                        // If the candidate is frequent
                            //System.out.println(""+candidate.toString()+" is frequent "+nseq[0]);
                            candidate.support = nseq[0] / 10;
                            Library.addFrequentPattern(candidate);                   
                            motifs_qui_matchent[i] = candidate;
                            sequences_qui_matchent[i] = seq;
    //                        sequences_qui_matchentApply[i] = seqApply;
                            i++;

    //                        System.out.println(stepsInteger.toString());
    //                        System.out.println("LIST: ");                
    //                        System.out.println(candString.toString()+": "+ ruleSupp);
    //                        System.out.println(stepsInteger.toString());
    //                        System.out.println("SUBLIST: ");
    //                        System.out.println(patternString.toString()+": "+ premissSupp);

                            if (conf>minConf && premissSupp>minSup && ruleSupp>minSup){
                                System.out.println(candString.toString()+": "+ ruleSupp);
                                IfThenRule rule = new IfThenRule();
                                rule.premise=pattern.transactions;
                                rule.properties=candidate.relations;
                                rule.steps=(ArrayList<Integer>)stepsInteger.clone();
                                rule.conclusion=candidate.transactions.get(candidate.concepts.size()-1);
                                rule.confidence=conf*100;
                                rule.support=seq.length/(float)Library.getNbUserSequences()*100;
                                rule.Msequences=seq;
                                rule.pattern=candidate;
                                rule.prefix=pattern;
                                allRules.putIfAbsent(allRules.hashCode(), rule);
                                nRules++;

                                if (pattern.support==candidate.support || rule.steps.size()<1){
                                    bannedRules.putIfAbsent(bannedRules.hashCode(), rule);
    //                                candidates.remove(candidate);
                                }
                                else if ( rule.steps.size()>2 && (rule.steps.get(rule.steps.size()-1)==1 || rule.steps.get(rule.steps.size()-1)==rule.steps.get(rule.steps.size()-2)))
                                        bannedRules.putIfAbsent(bannedRules.hashCode(), rule);
                                else {
    //                                System.out.println(rule.toString());
                                    rules.putIfAbsent(rule.hashCode(), rule);
                                }
                            }
    //                        else
    //                            continue;
                        }
                    }
                    else
                    {
                        if(getRelativeSupport2(nseq[0] , Library.getNbUserSequences()) >= minSup2){
                        // If the candidate is frequent
                            //System.out.println(""+candidate.toString()+" is frequent "+nseq[0]);
                            candidate.support = nseq[0] / 10;
                            Library.addFrequentPattern(candidate);                   
                            motifs_qui_matchent[i] = candidate;
                            sequences_qui_matchent[i] = seq;
    //                        sequences_qui_matchentApply[i] = seqApply;
                            i++;

    //                        System.out.println(stepsInteger.toString());
    //                        System.out.println("LIST: ");                
    //                        System.out.println(candString.toString()+": "+ ruleSupp);
    //                        System.out.println(stepsInteger.toString());
    //                        System.out.println("SUBLIST: ");
    //                        System.out.println(patternString.toString()+": "+ premissSupp);

                            if (conf>minConf && premissSupp>minSup && ruleSupp>minSup){
                                IfThenRule rule = new IfThenRule();
                                rule.premise=pattern.transactions;
                                rule.properties=candidate.relations;
                                rule.steps=(ArrayList<Integer>)stepsInteger.clone();
                                rule.conclusion=candidate.transactions.get(candidate.transactions.size()-1);
                                rule.confidence=conf*100;
                                rule.support=seq.length/(float)Library.getNbUserSequences()*100;
                                rule.Msequences=seq;
                                rule.pattern=candidate;
                                rule.prefix=pattern;
                                allRules.putIfAbsent(allRules.hashCode(), rule);
                                nRules++;

    //                            System.out.println(rule.steps.toString());
                                if (pattern.support==candidate.support || rule.steps.size()<1){
                                    bannedRules.putIfAbsent(bannedRules.hashCode(), rule);
    //                                candidates.remove(candidate);
                                }
                                else if ( rule.steps.size()>2 && (rule.steps.get(rule.steps.size()-1)==1 || rule.steps.get(rule.steps.size()-1)==rule.steps.get(rule.steps.size()-2)))
                                        bannedRules.putIfAbsent(bannedRules.hashCode(), rule);
                                else {
                                    rules.putIfAbsent(rule.hashCode(), rule);
    //                                System.out.println(rule.toString());
                                }

                            }
    //                        

                        }
                    }
                } // canadidate not empty
                
            }
            
//            System.out.println("==============================================================================");
            
            i=0;
            System.out.println("\tCandidate: " + motifs_qui_matchent[i]);
            if (sequences_qui_matchent[i].length > 0){
                for (int si=0; si<sequences_qui_matchent[i].length; si++){
                    System.out.println("\t\tMatching: " + sequences_qui_matchent[i][si].sequenceUtilisateur);
                }
            }
            //Miner.generateAndTestDF(motifs_qui_matchent[i], sequences_qui_matchent[i]);
            
            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            Date date = new Date(timeElapsed);
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dateFormatted = formatter.format(date);
            
            System.out.println(level + "\t" + dateFormatted + "\t" + candidates.size());
            
            while(i < motifs_qui_matchent.length && motifs_qui_matchent[i]!=null){
                
                Miner.generateAndTestDF(motifs_qui_matchent[i], sequences_qui_matchent[i], minLevel, level + 1);
                sequences_qui_matchent[i] = null;motifs_qui_matchent[i]=null;
                i++;
            }
	}
	
	// Return the relative support of the set of sequences regarding to the total of user sequences
        //q;skdqlksdjlqsjdkq
	private static double getRelativeSupport( final Set<Sequence> sequences )
	{
		double nbSequences =  ( double ) sequences.size( );
		double totalSequences = ( double ) Library.getNbUserSequences( );
                //System.out.println(nbSequences+"/"+totalSequences);
		double relativeSupport = nbSequences / totalSequences;
		return relativeSupport;
	}
        
        /**
         * Methode revue pour renvoyer le support relatif d'un ensemble de sequences
         * On envoie des parametres plutot que de calculer les elements a chaque fois
         * essayer de voir la precision requise (cast int * 10) ?
         * @param nseq
         * @param nbuserseq
         * @return 
         */
        private static int getRelativeSupport2(final int nseq, final int nbuserseq){
            int relativeSupport = nseq / nbuserseq;
            return relativeSupport;
        }

	
}
