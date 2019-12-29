/**
 *
 * @author Enridestroy
 */

package ontopatternmatching;

import ca.uqam.gdac.framework.matcher.ConceptMatcher;
import ca.uqam.gdac.framework.matcher.Matcher;
import static java.awt.PageAttributes.MediaType.A;
import legacy.Operation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import legacy.Pair;
import ontologyrep2.Concept;
import ontologyrep2.OntoRepresentation;

/**
 * 
 * @author Enridestroy
 */
//public class Motif  extends IntermediateWorkflow{
public class Motif {
    public ArrayList<Integer> concepts = new ArrayList<>();
    public ArrayList<Integer[]> relations = new ArrayList<>();
    public Integer sourcePositionInf = -1;
    public Integer propertySup = -1;
    public Integer lastAppliedProperty = -1;
    public Operation lastOperation = null;
    public Integer[] lastAddedLink = null;
    public int support = 0;
    public float generality = 0;
    
    
    /*public ArrayList<JobBlock> pile_de_taches = new ArrayList<>();
    public JobBlock firstConceptBlock = null;//represente un pointeur vers le premier bloc ajoute
    public ArrayList<JobBlock> concepts_to_blocks = new ArrayList<>();//permet de requeter les concepts vers le bloc qui les represente
    public JobBlock lastAddedConcept = null;//pointeur vers le dernier concept ajoute
    //public int deplacements = 0;//ne sert a rien (pour l'instant^^)
    public RelationJobBlock lastAddedRelation = null;//pointeur vers la derniere relation ajoutee
    public ArrayList<RelationJobBlock> relations_to_blocks = new ArrayList<>();//permet de requeter les relations vers le bloc qui les represente
    public JobBlock lastTouchedBlock = null;*/
    
    public AppariementStructure empty_structure = new AppariementStructure();
    public ArrayList<AppariementExtensionTask> appariement_extensions = new ArrayList<>();

    public ArrayList<Integer> getConcepts() {
        return concepts;
    }

    public HashMap<Pair<Integer,Integer>, ArrayList<Integer>> getProperties() {
//        return relations;
        HashMap<Pair<Integer,Integer>, ArrayList<Integer>> setR = new HashMap();
        for (Integer[] r : relations){
            Pair<Integer,Integer> keyR =new Pair(r[1], r[2]);
             ArrayList<Integer> valueR = new ArrayList(r[0]);
            setR.put(keyR, valueR);
        }
        return setR;
    }
    
    public Integer nbConcepts() {
        return concepts.size();
    }
    
    public Integer nbProperties( Integer subjectPosition,  Integer objectPosition) {
        Integer nbLinks=0;
        for (Integer[] r : relations){
            if (r[1] == subjectPosition && r[2] == objectPosition)
                nbLinks++;
        }
        return nbLinks;
    }
    
    public Integer nbProperties() {
        return relations.size();
    }
    
    
    //ca peut etre une liste de methodes => addConcept et addRelations, splConcept ... => bien
    
    //empty structure est la representation du motif du niveau n-1 vide.
    //chaque fois que l'on modifie la represnetation, il s'agit d'une copie contenant l'appariement du niveau n-1
    
    
    //public WorkflowMatcher sm = null;
    
    /**
	 * Given a concept, returns the longest path through all it´s sub concepts.
	 * The length of a path is defined to be the amount of edges between super
	 * and sub concept until a leave sub concept, a sub concept without
	 * children, is reached <br/>
	 * <br/>
	 * NOTE: Assumes that the ontology is structure like a tree
	 */
    public ConceptMatcher conceptMatcher( final Workflow input, final OntoRepresentation hierarchyRepresentation )
    {
	return ( new ConceptMatcher( this, input, hierarchyRepresentation ) );
    }
    
    public Matcher matcher( Workflow input, OntoRepresentation hierarchyRepresentation )
    {
        return ( new Matcher( this, input, hierarchyRepresentation ) );
    }
    
    public int getSuppData(final OntoRepresentation hierarchyRepresentation ){

        int suppData=0;
        
        for(Integer c : concepts){
//            System.out.println(c);
            // Data
            if (hierarchyRepresentation.isConceptEqualOrDescendant(64, c))
                suppData++;
            
        }
        
        return suppData;
    }
    
    public int getSuppProgram(final OntoRepresentation hierarchyRepresentation ){

        int suppProg=0;
        
        for(Integer c : concepts){
//            System.out.println(c);
            // DataCollectionProgram
            if (hierarchyRepresentation.isConceptEqualOrDescendant(103, c))
                suppProg++;
            
            // WorkflowAlignmentProgram
            if (hierarchyRepresentation.isConceptEqualOrDescendant(110, c))
                suppProg++;
            
            // ModelSelectionProgram
            
            // GeneralPurposePackagesProgram
            if (hierarchyRepresentation.isConceptEqualOrDescendant(44, c))
                suppProg++;
            
            // HypothesisValidationProgram
            if (hierarchyRepresentation.isConceptEqualOrDescendant(52, c))
                suppProg++;
            
            // TreeAnalysisProgram
            
            // TreeVisualizationProgram
            if (hierarchyRepresentation.isConceptEqualOrDescendant(42, c))
                suppProg++;
            
        }
        
        return suppProg;
    }
    
    public int getSuppMetadata(final OntoRepresentation hierarchyRepresentation ){

        int suppMeta=0;
        
        for(Integer c : concepts){
//            System.out.println(c);
            // Source
            if (hierarchyRepresentation.isConceptEqualOrDescendant(75, c))
                suppMeta++;
            
            // Models
            if (hierarchyRepresentation.isConceptEqualOrDescendant(22, c))
                suppMeta++;
            
            // ParametersBootstrappingProgram
            if (hierarchyRepresentation.isConceptEqualOrDescendant(55, c))
                suppMeta++;
            
            // Method
            if (hierarchyRepresentation.isConceptEqualOrDescendant(15, c))
                suppMeta++;
            
        }
        
        return suppMeta;
    }
    
    public int getSuppConcepts(final OntoRepresentation hierarchyRepresentation ){

        return concepts.size();
    }
    
    public int getSuppRelations(final OntoRepresentation hierarchyRepresentation ){
    
        return relations.size();
    }
    
    public int getConceptSolutions(){
    
        return relations.size();
    }
    
    
    public float getGenerality(final OntoRepresentation hierarchyRepresentation ){
    
        int genC=0;
        int genR=0;
//        Concept max= new 
//        int maxhc=hierarchyRepresentation.findDepth(hierarchyRepresentation.getConcept(c));
        
        for(Integer c : concepts){
            int hc=hierarchyRepresentation.findDepth(hierarchyRepresentation.getConcept(c));
//            System.out.println();
            genC=genC+hc;
        }
        
        for(Integer[] r : relations){
            int hr=hierarchyRepresentation.findDepthRel(hierarchyRepresentation.getRelation(r[0]));
//            System.out.println();
            genC=genC+hr;
        }
//        System.out.println("genC: "+ genC);
//        System.out.println("genR: "+ genR);
        
        float meanC=(float)(genC/concepts.size())/5;
        float meanR=0;
        if (relations.size()>0)
            meanR=(float)(genR/relations.size())/1;
        
        this.generality=(meanC+meanR)/2;
        
//        System.out.println("meanC: "+ meanC);
//        System.out.println("meanR: "+ meanR);
//        System.out.println("generality: "+(meanC+meanR)/2);
        return ((meanC+meanR)/2);
    }
    
    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
//        s.append("m_items:").append(this.concepts.size()).append(",").append(this.relations.size());
        s.append("Concepts_ID : ");
        for(Integer c : concepts){
            s.append("").append(c).append(", ");
        }
        if (relations.size() > 0) {
            s.append("Relations_ID : ");
        for(Integer[] r : relations){
            s.append("{").append(r[0]).append(" => ").append(r[1]).append(",").append(r[2]).append("}, ");
        }
        }
        
        return s.toString().substring(0, s.length() - 2);
    }
    
    /**
     * On va remplir une nouvelle structure d'appariement avec les resultats d'un ancien matching (niveau n-1)
     * @param previousMatch
     * @param m
     * @return 
     */
    public AppariementStructure fillStructureWithPreviousMatching(){
        /*if(previousMatch.concepts_to_blocks.size()<1){
            System.out.println("Building new structure");
            previousMatch = m.createAppariementStructure(previousMatch);
            //System.out.println("mm:"+m.empty_structure.concepts_to_blocks.size());
            return previousMatch;
        }*/       
        //System.out.println("updating structure");
        return this.updateAppariementStructureWithCurrentPattern();
    }
    
    /**
     * On met a jour la structure
     * @param target
     * @param m
     * @return 
     */
    public AppariementStructure updateAppariementStructureWithCurrentPattern(){
        //System.out.println("00000"+target.concepts_to_blocks.size());
        
        //on doit copier les nouveaux concepts dans la structure
        //AppariementStructure toto = new AppariementStructure(this.empty_structure);
        
        //this empty structure puis ajouter les nouveaux blocs
        
        //this.empty_structure = new AppariementStructure(this.empty_structure);
        
        /*
        System.out.println("---------------------------\nc:"+this.empty_structure.concepts_to_blocks.size() + "vs" + this.concepts.size());
        System.out.println("p:"+ this.empty_structure.relations_to_blocks.size() + "vs" + this.relations.size());
        
        
        for(JobBlock j : this.empty_structure.concepts_to_blocks){
            System.out.println("bl:"+j);
        }
        
        System.out.println("op:"+this.lastOperation.getSymbol());
        for(Integer j : this.concepts){
            System.out.println("m:"+j);
        }
        for(Integer[] j : this.relations){
            System.out.println("mm:"+j[0]+","+j[1]+","+j[2]);
        }*/
        
        /*for(int i=m.appariement_extensions.size();i>0;i--){
            target = target.extend(m.appariement_extensions.get(i-1));
        }*/
        AppariementStructure s = null;
        AppariementExtensionTask extension = null;
        //this.appariement_extensions.get(;
        int pos = this.appariement_extensions.size();
        if(pos>0){
            extension = this.appariement_extensions.get(pos-1);
            s = this.empty_structure.extend(extension);
        }
        
        /*for(AppariementExtensionTask extension : this.appariement_extensions){
            //do task
            //first = extension.extend(target);
            s = this.empty_structure.extend(extension);
            //System.out.println("extending!"+extension.toString());
        }*/
        
        
        /*
        if(this.empty_structure.concepts_to_blocks.size() != this.concepts.size() || this.empty_structure.relations_to_blocks.size() != this.relations.size()){
            System.out.println("pb:c:"+this.empty_structure.concepts_to_blocks.size() + "vs" + this.concepts.size());
            System.out.println("pb:p:"+ this.empty_structure.relations_to_blocks.size() + "vs" + this.relations.size());
            System.exit(1);
        }*/
        
        /*for(JobBlock b : toto.pile_de_taches){
            b.solution = 0;
        }*/
        if(s!=null) this.empty_structure = s;
        /*
        System.out.println(toto+" && "+target);
        if(target.firstConceptBlock!=null) {
            System.out.println(toto.firstConceptBlock.hashCode()+" && "+target.firstConceptBlock.hashCode());
        }*/
        //m.appariement_extensions.clear();//on vide la liste des extensions
        //System.out.println("qsdqsd"+target.concepts_to_blocks.size());
        return s;
    }
    
    /**
     * 
     * @param a
     * @return 
     */
//    
//    public AppariementStructure createAppariementStructure(AppariementStructure a){
//        /*if(this.empty_structure!=null){
//            System.out.println("Using already defined sequence matcher.");
//            this.clearWorkflowMatcher();
//            return this.empty_structure;
//        }
//        else{*/
//            //System.out.println("Building new sequence matcher.");
//            //WorkflowMatcher sm = new WorkflowMatcher();
//            /**
//             * Permet de creer une nouvelle structure d'appariement vide a partir du motif courrant
//             */
//        
//        /*for(JobBlock j : a.pile_de_taches){
//            if(j.solution<1){
//                System.out.println("un bloc n'a pas de solution!!!!!:"+j);
//                System.exit(1);
//            }else{
//                System.out.println("check:"+j);
//            }
//        }*/
//        AppariementStructure prec = new AppariementStructure(a);        
//        
//        /*{
//            System.out.println("original:");
//            JobBlock j = prec.firstConceptBlock;
//            while(j!=null){
//                System.out.println(j);
//                if(j.firstChild!=null){
//                    j=j.firstChild;
//                    continue;
//                }
//                j=j.next;
//            }
//        }*/
//        
//        Integer[] c_solutions = new Integer[prec.concepts_to_blocks.size()];
//        {
//            int i=0;
//            for(JobBlock j : prec.concepts_to_blocks){
//                c_solutions[i] = j.solution;
//                if(c_solutions[i]<1){
//                    System.out.println("One block has not found a solution!!!:"+i);
//                    System.exit(1);
//                }else{
//                    //System.out.println("c:"+i+":"+c_solutions[i]);
//                }
//                i++;
//            }
//        }
//        Integer[] p_solutions = new Integer[prec.relations_to_blocks.size()];
//        {
//            int i=0;
//            for(RelationJobBlock j : prec.relations_to_blocks){
//                p_solutions[i] = j.solution;
//                if(p_solutions[i]<1){
//                    System.out.println("One block has not found a solution!!!:"+i);
//                    System.exit(1);
//                }else{
//                    //System.out.println("p:"+i+":"+p_solutions[i]);
//                    if(j.domaine.solution<1){
//                        System.out.println("(D)p:"+i+":"+j.domaine.solution);
//                        System.exit(1);
//                    }
//                }
//                i++;
//            }
//        }
//        
//        //ici, toutes les relations et tous les concepts ont une solution
//        
//        
//        //System.out.println("tes:"+c_solutions[0]);
//        
//        
//        
//        //AppariementStructure a = new AppariementStructure();
//        a = new AppariementStructure();
//
//        //{
//        ArrayList<Integer[]> _relations = new ArrayList(this.relations);
//        Collections.sort(_relations, new RelationCastComparator());
//
//        int s_c = this.concepts.size();
//        int min_size_concepts = 5; int min_size_relations = 5;
//        if(s_c<min_size_concepts) s_c = min_size_concepts;//si le motif est petit alors on lui donne de la marge
//        int s_r = this.relations.size();
//        if(s_r<min_size_relations) s_r=min_size_relations;
//
//        a.pile_de_taches = new ArrayList<>(s_c+(s_r*3));
//        a.concepts_to_blocks = new ArrayList<>(s_c);
//        a.relations_to_blocks = new ArrayList<>(s_r);
//
//        //construction de la structure
//        for(Integer o : this.concepts){
//            this.addConcept(o, a);
//            //System.out.println("adding concept");
//        }        
//        a.firstConceptBlock = a.pile_de_taches.get(0);//on initialise le premier block
//        //plutot concepts to blocks 0 ?
//
//        for(Integer[] r : _relations){
//            this.addRelation(r, a);
//            //System.out.println("adding relation");
//        }
//        //}
//        //WorkflowMatcher.debug = true;
//                
//        //System.out.println("Cap:"+a.concepts_to_blocks.size()+"vs"+"s:"+c_solutions.length);        
//        //mise a jour des solutions de concepts
//        
//        //System.out.println("precc:"+prec.concepts_to_blocks.size() +" vs ac:" +a.concepts_to_blocks.size());
//        //System.out.println("precp:"+prec.relations_to_blocks.size() +" vs ap:" +a.relations_to_blocks.size());
//        
//        /*if(prec.concepts_to_blocks.size() - a.concepts_to_blocks.size() < -1){
//            System.out.println("ajout de plus d'un concept");
//            System.exit(1);
//        }
//        if(prec.relations_to_blocks.size() - a.relations_to_blocks.size() < -1){
//            System.out.println("ajout de plus d'un relation");
//            System.exit(1);
//        }*/
//        
//        /*
//        //COPIE
//        int max = prec.concepts_to_blocks.size();
//        if(max == a.concepts_to_blocks.size())max-=1; //&& this.lastOperation == Operation.SC) max-=1;
//        for(int i=0; i<max;i++){
//            a.concepts_to_blocks.get(i).solution = prec.concepts_to_blocks.get(i).solution;
//        }
//        max = prec.relations_to_blocks.size();
//        if(max == a.relations_to_blocks.size()) max-=1;// && this.lastOperation == Operation.SP) max-=1;
//        for(int i=0; i<max;i++){
//            //a.relations_to_blocks.get(i).solution = prec.relations_to_blocks.get(i).solution;
//            a.relations_to_blocks.get(i).domaine.solution = prec.relations_to_blocks.get(i).domaine.solution;
//            //a.relations_to_blocks.get(i).curr_rel = prec.relations_to_blocks.get(i).curr_rel;
//            //((RelationJobBlock)a.relations_to_blocks.get(i).domaine).curr_rel = ((RelationJobBlock)prec.relations_to_blocks.get(i).domaine).curr_rel;
//        }*/
//        
//        
//        /*
//        {
//            System.out.println("new(+"+this.lastOperation.getSymbol()+"):");
//            JobBlock j = a.firstConceptBlock;
//            while(j!=null){
//                System.out.println(j);
//                if(j.firstChild!=null){
//                    j=j.firstChild;
//                    continue;
//                }
//                j=j.next;
//            }
//        }*/
//        
//        
//        /*if(prec.concepts_to_blocks.size() == a.concepts_to_blocks.size()){
//            if(prec.lastAddedConcept.item == a.lastAddedConcept.item){
//                
//                //on a ajoute ou spe une relation, on peut copier les concepts sans probleme
//                {
//                    int i=0;
//                    for(JobBlock j : a.concepts_to_blocks){
//                        if(i<c_solutions.length && c_solutions[i]!=null){
//                            j.solution = c_solutions[i];
//                        }i++;
//                    }
//                }
//                               
//                if(prec.relations_to_blocks.size() == a.relations_to_blocks.size()){
//                    if(prec.lastAddedRelation.item == a.lastAddedRelation.item){
//                        //on vient de specialiser un concept
//                        System.out.println("Tout correspond, ce n'est pas possible...");
//                    }
//                    else{
//                        //on a specialise une relation
//                        //specialisation de relation
//                        {
//                            //System.out.println("Speciliastion de rel");
//                            int i=0;
//                            for(RelationJobBlock j : a.relations_to_blocks){
//                                if(i<p_solutions.length-1 && p_solutions[i]!=null){
//                                    j.solution = p_solutions[i];
//                                    j.domaine.solution = p_solutions[i];
//                                }i++;
//                            }
//                        }                        
//                    }
//                }
//                else{
//                    //on vient d'ajouter une relation
//                    //ajout de relation
//                    {
//                        //System.out.println("ajout de rel");
//                        int i=0;
//                        for(RelationJobBlock j : a.relations_to_blocks){
//                            if(i<p_solutions.length && p_solutions[i]!=null){
//                                j.solution = p_solutions[i];
//                                j.domaine.solution = p_solutions[i];
//                            }i++;
//                        }
//                    }
//                }
//            }
//            else{
//                //on a specialise un concept
//                //specialisation de concept
//                {
//                    //System.out.println("spec de concept");
//                    int i=0;
//                    for(JobBlock j : a.concepts_to_blocks){
//                        if(i<c_solutions.length-1 && c_solutions[i]!=null){
//                            j.solution = c_solutions[i];
//                        }i++;
//                    }
//                }
//                //donc on peut copier les relations sans probleme
//                {
//                    int i=0;
//                    //System.out.println("r:"+a.relations_to_blocks.size());
//                    for(RelationJobBlock j : a.relations_to_blocks){
//                        if(i<p_solutions.length && p_solutions[i]!=null){
//                            j.solution = p_solutions[i];
//                            j.domaine.solution = p_solutions[i];
//                        }i++;
//                    }
//                }
//                
//            }
//        }
//        else{
//            //on a ajoute un concept
//            //ajout de concept
//            {
//                //System.out.println("ajout de concept");
//                int i=0;
//                for(JobBlock j : a.concepts_to_blocks){
//                    if(i<c_solutions.length && c_solutions[i]!=null){
//                        j.solution = c_solutions[i];
//                    }i++;
//                }
//            }
//            
//            //donc on peut aussi copier les relations sans probleme
//            {
//                //System.out.println("r:"+a.relations_to_blocks.size());
//                int i=0;
//                for(RelationJobBlock j : a.relations_to_blocks){
//                    if(i<p_solutions.length && p_solutions[i]!=null){
//                        j.solution = p_solutions[i];
//                        j.domaine.solution = p_solutions[i];
//                    }i++;
//                }
//            }
//            
//        }*/
//        //System.out.println("Pap:"+a.relations_to_blocks.size()+"vs"+"s:"+p_solutions.length); 
//        
//        /**
//         * mise a jour des concepts
//         */
//        /*
//        //si on n'a pas ajoute d'elements 
//        if(prec.concepts_to_blocks.size() == a.concepts_to_blocks.size()){
//            //alors je verifie si le dernier est le meme
//            if(prec.lastAddedConcept.item == a.lastAddedConcept.item){
//                //si c'est le meme on copie toutes les solutions
//                int i=0;
//                for(JobBlock j : a.concepts_to_blocks){
//                    if(i<c_solutions.length && c_solutions[i]!=null){
//                        j.solution = c_solutions[i];
//                    }i++;
//                }
//            }
//            else{
//                //si c'est pas le meme on copie tout sauf le dernier
//                int i=0;
//                for(JobBlock j : a.concepts_to_blocks){
//                    if(i<c_solutions.length-1 && c_solutions[i]!=null){
//                        j.solution = c_solutions[i];
//                    }i++;
//                }
//            }
//            
//        }
//        else{
//            //si pas autant d'elements qu'avant (plus),
//            int i=0;
//            for(JobBlock j : a.concepts_to_blocks){
//                if(i<c_solutions.length && c_solutions[i]!=null){
//                    j.solution = c_solutions[i];
//                }i++;
//            }
//            //alors on copie tout
//        }
//        */
//        /**
//         * mise a jour des relations
//         */    
//        /*if(prec.relations_to_blocks.size() == a.relations_to_blocks.size()){
//            //alors je verifie si le dernier est le meme
//            if(prec.lastAddedRelation!=null && prec.lastAddedRelation.item == a.lastAddedRelation.item){
//                //si c'est le meme on copie toutes les solutions
//                int i=0;
//                for(RelationJobBlock j : a.relations_to_blocks){
//                    if(i<p_solutions.length && p_solutions[i]!=null){
//                        j.solution = p_solutions[i];
//                        j.domaine.solution = p_solutions[i];
//                    }i++;
//                }
//            }
//            else{
//                //si c'est pas le meme on copie tout sauf le dernier
//                int i=0;
//                for(RelationJobBlock j : a.relations_to_blocks){
//                    if(i<p_solutions.length-1 && p_solutions[i]!=null){
//                        j.solution = p_solutions[i];
//                        j.domaine.solution = p_solutions[i];
//                    }i++;
//                }
//            }
//
//        }
//        else{
//            //si pas autant d'elements qu'avant (plus),
//            int i=0;
//            for(RelationJobBlock j : a.relations_to_blocks){
//                if(i<p_solutions.length && p_solutions[i]!=null){
//                    j.solution = p_solutions[i];
//                    j.domaine.solution = p_solutions[i];
//                }i++;
//            }
//            //alors on copie tout
//        }*/
//                
//        //JobBlock j = prec.firstConceptBlock;
//        //parcourir tous les blocs ?
//        //comment detecter les nouveaux blocs de relations?
//        //le codomaine est a la fin ou en tout cas dans
//        
//        //get last relation bloc
//            //si c'est le meme alors on vient d'ajouter ou spe un concept -1
//        //get last concept
//            //si c'est le meme alors on vient d'ajouter ou spe une relation -2
//        
//        //1 - pour tous les blocs tant que != de lastconcept, alors copier la solution suivante        
//        //remet on lastconcept a 0 pour une specialisation ?
//        
//        //pour les relations, copier tout
//        
//        //2- les deux derniers blocs de la liste sont les elements ajoutes ou modifies on remonte la liste ?
//        //on copie tout a l'envers ?
//        
//        //}
//        //System.out.println("m:"+this.empty_structure.concepts_to_blocks.size());        
//        //this.empty_structure.lastTouchedBlock = this.empty_structure.pile_de_taches.get(this.empty_structure.pile_de_taches.size()-1);
//        //return sm;
//        return a;
//    }
    
    public void clearWorkflowMatcher(AppariementStructure a){
        for(JobBlock b : a.pile_de_taches){
            b.solution = 0;
        }
        //this.empty_structure.userWorkflow = s;
        //this.firstConceptBlock.solution = 0;
        
        
        /*JobBlock one = this.firstConceptBlock;
        while(one!=null){
            one.solution = 0;
            if(one.firstChild!=null){
                one = one.firstChild;
                one.solution = 0;
            }
            one = one.next;
        }*/
        //this.pile_de_taches2.clear();//
    }
        
    /**
     * Construit un motif a partir d'un seul concept
     * @param first_concept 
     */
    public Motif(Integer first_concept){
        this.concepts.add(first_concept);
    }
    
    public Motif(){
        //on fait rien...
    }
    
    /**
     * Construit un motif a partir d'une liste de concepts
     * @param concepts 
     */
    public Motif(ArrayList<Integer> concepts){
        this.concepts.addAll(concepts);
    }
    
    public Motif(final Motif m){
        this.concepts = new ArrayList<>(m.concepts.size());
        for(Integer i : m.concepts){
            this.concepts.add(i);
        }
        //System.out.println("on a "+this.concepts.size()+" concepts.");
        this.relations = new ArrayList<>(m.relations.size());
        for(Integer[] i : m.relations){
            this.relations.add(i);
        }
        
        //System.out.println("on a "+this.concepts.size()+" relations.");
        this.lastOperation = m.lastOperation;
        this.sourcePositionInf = m.sourcePositionInf;
        this.propertySup = m.propertySup;
        this.lastAppliedProperty = m.lastAppliedProperty;
        
        this.appariement_extensions = new ArrayList(m.appariement_extensions); 
        this.empty_structure = new AppariementStructure(m.empty_structure);
        
        //this.representation = new Representation( m.representation );
        
        /*this.concepts_to_blocks = new ArrayList<>(m.concepts_to_blocks);
        this.relations_to_blocks = new ArrayList<>(m.relations_to_blocks);
        
        
        if(m.firstConceptBlock!=null) this.firstConceptBlock = new JobBlock(m.firstConceptBlock.item, m.firstConceptBlock.jobs, m.firstConceptBlock.prev, 
                m.firstConceptBlock.next, m.firstConceptBlock.position);
        
        if(m.lastAddedConcept!=null) this.lastAddedConcept = new JobBlock(m.lastAddedConcept.item, m.lastAddedConcept.jobs, m.lastAddedConcept.prev, 
                m.lastAddedConcept.next, m.lastAddedConcept.position);
        
        if(m.lastTouchedBlock!=null) this.lastTouchedBlock = new JobBlock(m.lastTouchedBlock.item, m.lastTouchedBlock.jobs, m.lastTouchedBlock.prev, 
                m.lastTouchedBlock.next, m.lastTouchedBlock.position);
        
        //if(m.firstConceptBlock!=null) this.firstConceptBlock = new JobBlock(m.firstConceptBlock);
        //if(m.lastAddedConcept!=null) this.lastAddedConcept = new JobBlock(m.lastAddedConcept);
        //if(m.lastTouchedBlock!=null) this.lastTouchedBlock = new JobBlock(m.lastTouchedBlock);
        
        this.firstConceptBlock = m.firstConceptBlock;
        this.lastAddedConcept = m.lastAddedConcept;
        this.lastTouchedBlock = m.lastTouchedBlock;
        
        this.lastAddedLink = m.lastAddedLink;*/
        //this.pile_de_taches = new ArrayList<>(m.pile_de_taches);
       
    }
    
    public void addConceptC(final Integer concept){
        // Addition of the concept in the list of concepts 
        //super.appendConcept( concept );
        this.concepts.add(concept);
        // Update the status of the last operation
        this.lastOperation = Operation.AC;
        // Update of the representation
        //representation.addConceptC( concept );
        
        //ajoute un concept a la structure
        //this.lastTouchedBlock = this.addConcept(concept);
        
        //AppariementExtensionTask extension = this.empty_structure.
        
        
        AppariementExtensionTask task = new AddConceptTask();
        task.item = new Integer[]{concept};
        this.appariement_extensions.add(task);
    }
    
    public boolean isFirstAPC(){
        return ( this.lastOperation != Operation.AP && this.lastOperation != Operation.SP );
    }
    
    public boolean onlyOneAPC(){
        // Get the source position used by the last AP
        Integer sourcePosition = this.sourcePositionInf;
        // Get the target position
        Integer targetPosition = this.concepts.size();
        Integer[] curr_rel;int matches=0;
        for(int i=0;i<this.relations.size();i++){
            curr_rel = this.relations.get(i);
            //if(curr_rel[1] > sourcePosition+1) break;
            if(Objects.equals(curr_rel[1], sourcePosition) && Objects.equals(curr_rel[2], targetPosition)){
                matches++;
            }
        }
        //System.out.println("APC:"+matches);
        return (matches==1);
        //ArrayList<Integer> appliedProperties = getLinksByElementPositions( sourcePosition, targetPosition );
        //return( appliedProperties.size( ) == 1 );
    }
    
    /**
    * Add a property between the positions of the subject concept and the last concept of the sequence.
    * 
    * @param subjectPosition Position of the subject.
    * @param property Property to add.
    */
    public void addPropertyC(final Integer subjectPosition, final Integer property){
        // Addition of the property in the hash map
        Integer targetPosition = this.concepts.size();
        Integer[] rel = new Integer[]{property, subjectPosition+1, targetPosition};
        this.relations.add(rel);
        //ordonner ?

        // Update the property sup :
        // If this is the first AP operation applied on the last concept of the pattern 
        // - OR - 
        // If this a new AP operation which is applied between the last concept
        // and a source position different from the last source position

        // Update the property sup
        //updatePropertySupByAP( property, subjectPosition );
        if(isFirstAPC() || !Objects.equals(subjectPosition+1, this.sourcePositionInf)){
            this.propertySup = property;
        }

        // Update the source position used by the AP operation
        //setLastPropertySourcePosition( subjectPosition );
        this.sourcePositionInf = subjectPosition+1;

        // Update the last added property
        //setLastAppliedProperty( property );
        this.lastAppliedProperty = property;

        // Update the status of the last operation
        //setLastOperation( Operation.AP );
        this.lastOperation = Operation.AP;
        // Update of the representation
        //representation.addPropertyC(subjectPosition, property );
        this.lastAddedLink = rel;
        
        //ajoute une relation a la structure
        //this.lastTouchedBlock = this.addRelation(rel);
        
        
        AppariementExtensionTask task = new AddRelationTask();
        task.item = rel;
        this.appariement_extensions.add(task);
    }
    
    /**
    * Specializes the last concept of the pattern.
    * 
    * @param splConcept Concept used to specialize the last concept of the pattern.
    */
    public void splConceptC(final Integer splConcept){
        // Specialization of the concept in the list of concepts
        //super.replaceLastConcept(splConcept);
        this.concepts.set(this.concepts.size()-1, splConcept);
        // Update the status of the last operation
        //setLastOperation( Operation.SC );
        this.lastOperation = Operation.SC;
        // Update of the representation
        //representation.splConceptC( splConcept );
        
        //specialise un bloc de concept
        //this.lastTouchedBlock = this.speConcept(splConcept);
        
        //this.appariement_extensions.add(this.empty_structure.createSpeConceptExtension());
        
        
        AppariementExtensionTask task = new SpeConceptTask();
        task.item = new Integer[]{splConcept};
        this.appariement_extensions.add(task);
    }
    
    public void splPropertyC(final Integer subjectPosition, final Integer splProperty) {
        // Specialization of the property in the hash map
        Integer targetPosition = this.concepts.size();
        {
            int pos=-1;boolean found_first=false;
            //System.out.println(""+(subjectPosition+1)+" && "+targetPosition+"");
            for(Integer[] rel : this.relations){
                if(found_first){
                    if(!Objects.equals(rel[2], targetPosition)) break;//on sort
                }
                else{
                    if(rel[1] > (subjectPosition+1)){
                        break;
                    }
                    else if(rel[1] == subjectPosition+1){
                        if(Objects.equals(rel[2], targetPosition)){
                            found_first=true;
                        }
                    }
                }
                pos++;
            }
            //System.out.println("lastAddedLink:"+pos);
            if(pos>-1) {
                Integer[] lres = this.relations.get(pos);
                /*if(!Objects.equals(targetPosition, lres[2])){
                    for(Integer[] rel : this.relations){
                        System.out.println(""+rel[0]+","+rel[1]+","+rel[2]);
                    }
                    System.out.println("erreur d'inconsistance dans la specialisation d'une propriete:"+(targetPosition)+"vs"+lres[2]);
                    System.exit(1);
                }*/
                this.relations.set(pos, new Integer[]{splProperty, lres[1], lres[2]});
            }
        }
        // Update the last applied property
        //setLastAppliedProperty( splProperty );
        this.lastAppliedProperty = splProperty;
        // Update the status of the last operation
        //setLastOperation( Operation.SP );
        this.lastOperation = Operation.SP;
        // Update the status of the property sup
        //setPropertySup( splProperty );
        this.propertySup = splProperty;
        // Update of the representation
        //representation.splPropertyC( splProperty );
        
        //on specialise une relation
        //JobBlock block = this.speRelation(subjectPosition, lastAddedLink);
        
        
        //this.appariement_extensions.add(this.empty_structure.createSpeRelationExtension());
        AppariementExtensionTask task = new SpeRelationTask();
        task.item = new Integer[]{splProperty, subjectPosition+1, targetPosition};
        this.appariement_extensions.add(task);
        
    }
    
    /*
    //ajoute une nouvelle relation au motif
    public final JobBlock addRelation(Integer[] relation, AppariementStructure a){
        Job[] jobs;
        JobBlock[] blocks = new JobBlock[2];
        int pos = 0;      
        //////////////////////////////////////
        // BLOC 1 - DOMAIN VALIDATION
        //////////////////////////////////////
        //JobBlock domaine_block = this.concepts_to_blocks.get(relation[1]-1);
        JobBlock domaine_block = a.concepts_to_blocks.get(relation[1]-1);//les relations commencent a zero mtn
        //Ajout du premier bloc a la pile
        jobs = new Job[1];
        //on ajoute un par un les jobs
        jobs[0] = new ValideDomaineRelation();
        
        pos = domaine_block.position;
        if(domaine_block.lastChild!=null) pos = domaine_block.lastChild.position;
        //on cree notre nouveau bloc avec l'item, les jobs et le pointeur optionel
        blocks[0] = new RelationJobBlock(relation[0], jobs, domaine_block, null, domaine_block.last_domain_added, null, pos, (short)1);
        
        //////////////////////////////////////
        // BLOC 2 - RANGE VALIDATION
        //////////////////////////////////////
        //on recupere le bloc du concept codomaine
        //JobBlock codomaine_block = this.concepts_to_blocks.get(relation[2]-1);
        JobBlock codomaine_block = a.concepts_to_blocks.get(relation[2]-1);//les relations commencent a zero mtn
        //Ajout du deuxieme bloc a la pile
        jobs = new Job[1];
        //on ajoute un par un les jobs
        jobs[0] = new ValideCodomaineRelation();
        //jobs[1] = new VerifieSiRelationExiste();
        pos = codomaine_block.position;
        if(codomaine_block.lastChild!=null) pos = codomaine_block.lastChild.position;
        //on cree notre nouveau bloc avec l'item, les jobs et le pointeur optionel
        blocks[1] = new RelationJobBlock(relation[0], jobs, codomaine_block, codomaine_block.next, blocks[0], codomaine_block.last_range_added, pos, (short)2);
        //////////////////////////////////////
        // MISE A JOUR DES BLOCS ET CREATION DES LIENS
        //////////////////////////////////////
        
        //indique que le prochain traitement a partir de la verification de la relation potentielle
        //du domaine est le matching du concept qui va jouer le role de codomaine
        blocks[0].next = a.concepts_to_blocks.get(relation[1]-1).next;       
        //indique que le prochain traitement a partir de la verification de la relation potentielle
        //du codomaine est le matching des deux appariements de relation trouves
        //blocks[1].next = blocks[2];
        blocks[1].next = a.concepts_to_blocks.get(relation[2]-1).next;
        
        //ajoute le fils au block du domaine (ex: A => R1(1))
        //domaine_block.addChild(blocks[0]);
        a.concepts_to_blocks.get(relation[1]-1).addChild(blocks[0]);
        //ajoute le fils au block du codomaine (ex:B => R1(2))
        //codomaine_block.addChild(blocks[1]);
        a.concepts_to_blocks.get(relation[2]-1).addChild(blocks[1]);
        //ajoute le deuxieme fils au block du codomaine (ex: R1(2) => Rel1)
        //codomaine_block.addChild(blocks[2]);
        //construit le pointeur vers le derniere relation ajoutee
        a.lastAddedRelation = (RelationJobBlock)blocks[1];
        //ajoute cette relation a la liste des relations
        a.relations_to_blocks.add(a.lastAddedRelation);
        //ajoute tous les blocks a la pile des taches mais...
        a.pile_de_taches.addAll(Arrays.asList(blocks));//...la pile ne sert plus a grand chose
        
        return blocks[0];//on renvoie le domaine
    }
    
    //ajoute une nouveau concept au motif
    public final JobBlock addConcept(int concept, AppariementStructure a){
        Job[] jobs = new Job[1];
        //on ajoute un par un les jobs
        jobs[0] = new TrouveNextConcept();
       
        //utiliser concept_to_blocks plutot ?
        JobBlock last = a.lastAddedConcept;
        //on cree notre nouveau bloc avec l'item, les jobs et le pointeur optionel
        JobBlock b = new JobBlock(concept, jobs, last, null, a.concepts_to_blocks.size());
        a.lastAddedConcept = b;
        //le concept au dessus correspond au domaine du premier bloc
        if(last!=null) last.next = a.lastAddedConcept;
        //on ajoute notre block a la fin de la pile
        a.pile_de_taches.add(a.lastAddedConcept);
        //ajoute le nouveau concept a la liste des concepts disponibles 
        a.concepts_to_blocks.add(b);//ajoute le block a la suite
        return b;
    }*/
    
    /**
    * Comment va t'on proceder ?
    * -par JobBLock
    * -par position dans le motif ?
    * -ou alor
     * @param a
     * @return 
    */
    //specialise une relation du motif
    public final JobBlock speRelation(AppariementStructure a){
        return this.speRelation(a.lastAddedRelation, new Integer[]{1, 2, 3});
        //OU
        //this.speRelation(this.relations_to_blocks.get(this.relations_to_blocks.size()-1));
    }
    
    //specialise une relation du motif
    public final JobBlock speRelation(int position, Integer[] nouvelle_relation, AppariementStructure a){
        return this.speRelation(a.relations_to_blocks.get(position), nouvelle_relation);
        //peut etre ajouter un -1 ? si on travaille aussi a partir de 1
    }
    
    //specialise une relation du motif
    public final JobBlock speRelation(RelationJobBlock block, Integer[] nouvelle_relation){       
        JobBlock[] blocks = new JobBlock[2];
        //on recupere les blocks de domaine et codomaine
        blocks[0] = block.domaine;blocks[1]=block;
        //pour chaque block...
        for(JobBlock b : blocks){
            b.item = nouvelle_relation[0];//...on change le label de la nouvelle relation...
            b.solution = 0;//...et on detruit la solution trouvee
        }
        return blocks[0];//on renvoie le block qui valide la relation a partir du domaine comme ca
        //on peut commencer directement par lui
    }
    
    //specialise un concept du motif
    public final JobBlock speConcept(int nouveau_concept, AppariementStructure a){
        return this.speConcept(a.lastAddedConcept, nouveau_concept);
    }
    
    //specialise un concept du motif
    public final JobBlock speConcept(int position, int nouveau_concept, AppariementStructure a){
        return this.speConcept(a.concepts_to_blocks.get(position), nouveau_concept);
        //peut etre ajouter un -1 a position ? si on travaille aussi a partir de 1
    }
    
    //specialise un concept du motif
    public final JobBlock speConcept(JobBlock block, int nouveau_concept){
       block.item = nouveau_concept;
       block.solution = 0;
       return block;
    }    
    
    
    /**
     * 
     * @param p 
     */
    /*public Motif(final Pattern p){       
        //concepts
        ArrayList<Integer> _concepts = p.getConcepts();
        for(Integer c : _concepts){
            this.concepts.add(c);
            //System.out.println("A new concept in the motif is:"+c);
        }
        
        //relations
        HashMap<Pair<Integer, Integer>, ArrayList<Integer>> properties = p.getProperties();
        for(Pair<Integer, Integer> key : properties.keySet()){
            ArrayList<Integer> relations_entre_deux_positions = properties.get(key);
            Integer[] nouvelle_rel = new Integer[3];
            nouvelle_rel[1] = key.getFirst()+1;//on recupere la position du domaine
            nouvelle_rel[2] = key.getSecond()+1;//on recupere la position du codomaine
            for(Integer rel : relations_entre_deux_positions){
                nouvelle_rel[0] = rel;//on recupere le nom de la relation
                this.relations.add(nouvelle_rel);//on ajoute
                //System.out.println("A relation in the motif is : ("+nouvelle_rel[0]+","+nouvelle_rel[1]+","+nouvelle_rel[2]+")");
            }
        }
        //ensuite on va trier tout ca...    
        Collections.sort(this.relations, new RelationCastComparator());
    }*/
}
