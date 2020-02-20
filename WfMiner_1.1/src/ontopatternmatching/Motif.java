/**
 *
 * @author Enridestroy
 */

package ontopatternmatching;

import ca.uqam.gdac.framework.matcher.ConceptMatcher;
import ca.uqam.gdac.framework.matcher.Matcher;
import ca.uqam.gdac.framework.miner.Miner;
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
//public class Motif  extends IntermediateSequence{
public class Motif {
    public ArrayList<ArrayList<Integer>> transactions = new ArrayList<>();
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
    
    public OntoRepresentation ontology = Miner.hierarchyRepresentation;

    public ArrayList<ArrayList<Integer>> getTransactions() {
        return transactions;
    }
    
    public ArrayList<Integer> getAllConcepts() {
        ArrayList<Integer> flat_concepts = new ArrayList<Integer>();
        for (ArrayList<Integer> transaction : transactions)
            for (Integer concept : transaction)
                flat_concepts.add(concept);
        return flat_concepts;
    }
    
    public ArrayList<Integer> getConcepts(int transaction) {
        return transactions.get(transaction);
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
    
    public Integer nbConcepts(int transaction) {
        return transactions.get(transaction).size();
    }
    
    public Integer nbTransactions() {
        return transactions.size();
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
    
    
    /**
	 * Given a concept, returns the longest path through all it´s sub concepts.
	 * The length of a path is defined to be the amount of edges between super
	 * and sub concept until a leave sub concept, a sub concept without
	 * children, is reached <br/>
	 * <br/>
	 * NOTE: Assumes that the ontology is structure like a tree
	 */
    public ConceptMatcher conceptMatcher( final Sequence input, final OntoRepresentation hierarchyRepresentation )
    {
	return ( new ConceptMatcher( this, input, hierarchyRepresentation ) );
    }
    
    public Matcher matcher( Sequence input, OntoRepresentation hierarchyRepresentation )
    {
        return ( new Matcher( this, input, hierarchyRepresentation ) );
    }
    
    public int getSuppData(final OntoRepresentation hierarchyRepresentation ){

        int suppData=0;
        
        for (ArrayList<Integer> transaction : transactions ){
            for(Integer c : transaction){
//            System.out.println(c);
            // Data
            if (hierarchyRepresentation.isConceptEqualOrDescendant(64, c))
                suppData++;
            
        }
        }
        
        
        return suppData;
    }
    
    public int getSuppProgram(final OntoRepresentation hierarchyRepresentation ){

        int suppProg=0;
        
        for (ArrayList<Integer> transaction : transactions ){
            for(Integer c : transaction){
                //  System.out.println(c);
                // DataCollectionProgram
                if (hierarchyRepresentation.isConceptEqualOrDescendant(103, c))
                    suppProg++;

                // SequenceAlignmentProgram
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
        }
        
        return suppProg;
    }
    
    public int getSuppMetadata(final OntoRepresentation hierarchyRepresentation ){

        int suppMeta=0;
        
        for (ArrayList<Integer> transaction : transactions ){
            for(Integer c : transaction){
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
        
        for (ArrayList<Integer> transaction : transactions ){
            for(Integer c : transaction){
                int hc=hierarchyRepresentation.findDepth(hierarchyRepresentation.getConcept(c));
    //            System.out.println();
                genC=genC+hc;
            }
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
        s.append("[");
        for (ArrayList<Integer> transaction : transactions ){
            s.append("[");
            for(Integer c : transaction){
                OntoRepresentation ontology = Miner.hierarchyRepresentation;
                String c_label = ontology.getConcept(c).getName().split("#")[1];
                s.append("").append(c).append(" (").append(c_label).append("), ");
            }
            s.deleteCharAt(s.length() - 2);
            s.append("]");
            s.append(", ");
        }
        if (s.length() >= 2)
            s.deleteCharAt(s.length() - 2);
        s.append("]");
        for(Integer[] r : relations){
            s.append("{").append(r[0]).append(" => ").append(r[1]).append(",").append(r[2]).append("}, ");
        }
        return s.toString().replace(" ", "");
    }
    
    /**
     * On va remplir une nouvelle structure d'appariement avec les resultats d'un ancien matching (niveau n-1)
     * @param previousMatch
     * @param m
     * @return 
     */
    public AppariementStructure fillStructureWithPreviousMatching(){
        return this.updateAppariementStructureWithCurrentPattern();
    }
    
    /**
     * On met a jour la structure
     * @param target
     * @param m
     * @return 
     */
    public AppariementStructure updateAppariementStructureWithCurrentPattern(){
        AppariementStructure s = null;
        AppariementExtensionTask extension = null;
        //this.appariement_extensions.get(;
        int pos = this.appariement_extensions.size();
        if(pos>0){
            extension = this.appariement_extensions.get(pos-1);
            s = this.empty_structure.extend(extension);
        }
        
        if(s!=null) this.empty_structure = s;
        return s;
    }
    
    public void clearSequenceMatcher(AppariementStructure a){
        for(JobBlock b : a.pile_de_taches){
            b.solution = 0;
        }
    }
        
    /**
     * Construit un motif a partir d'un seul concept
     * @param first_concept 
     */
    public Motif(Integer first_concept){
        ArrayList<Integer> transaction = new ArrayList<Integer>();
        transaction.add(first_concept);
        this.transactions.add(transaction);
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
        ArrayList<Integer> transaction = new ArrayList<Integer>();
        for (int concept : concepts){
            transaction.add(concept);
        }
        this.transactions.add(transaction);
        this.concepts.addAll(concepts);
//        System.out.println("Motif from concepts: " + this.concepts);
    }
    
    public Motif(final Motif m){
        this.transactions = new ArrayList<ArrayList<Integer>>(m.transactions.size());
        for (ArrayList<Integer> transaction : m.transactions){
            this.transactions.add(transaction);
            for (Integer concept : transaction)
                this.concepts.add(concept);
//            System.out.println("Motif from Motif: " + this.concepts);
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
       
    }
    
    // The problem is here
    // append to last
    public void appendConceptC(final Integer concept, int level){
        
        if (this.nbTransactions() != 0){
            
            ArrayList<Integer> lastTransaction = this.getConcepts(this.nbTransactions() - 1);
            Integer previous_size = lastTransaction.size();
            
//            System.out.println("lastTransaction: " + lastTransaction);
//            System.out.println("previous_size: " + previous_size);
            
            if (!lastTransaction.isEmpty())
                if (concept >= lastTransaction.get(previous_size - 1) ){
                    String s_appendConceptC = ontology.getConcept(concept).getName().split("#")[1];
                    System.out.println("appendConceptC: " + concept + " (" + s_appendConceptC + ")");
                    
                    ArrayList<Integer> lastNewTransaction = new ArrayList<Integer>(lastTransaction.subList(0, previous_size));
                    lastNewTransaction.add(concept);
//                    System.out.println("lastNewTransaction: " + lastNewTransaction);
                    this.transactions.remove(this.nbTransactions() - 1);
                    this.transactions.add(lastNewTransaction);
//                    System.out.println("transactions: " + transactions);

                    // add the concept to the list of all concepts
                    this.concepts = getAllConcepts();

                    // Update the status of the last operation
                    this.lastOperation = Operation.DC;

                    AppariementExtensionTask task = new AddConceptTask();
                    task.item = new Integer[]{concept};
                    this.appariement_extensions.add(task);
                }
        }
    }
    
    // add to new
    public void addConceptC(final Integer concept, int level){
        String s_addConceptC = ontology.getConcept(concept).getName().split("#")[1];
        System.out.println("addConceptC: " + concept + " (" + s_addConceptC + ")");
        
        // Addition of the concept in the list of concepts 
        ArrayList<Integer> newTransaction = new ArrayList<Integer>();
        newTransaction.add(concept);
        this.transactions.add(newTransaction);
        this.concepts.add(concept);
        // Update the status of the last operation
        this.lastOperation = Operation.AC;
        
        AppariementExtensionTask task = new AddConceptTask();
        task.item = new Integer[]{concept};
        this.appariement_extensions.add(task);
            
    }
    
    /**
    * Specializes the last concept of the pattern.
    * 
    * @param splConcept Concept used to specialize the last concept of the pattern.
    */
    public void splConceptC(final Integer splConcept){
        String s_splConcept = ontology.getConcept(splConcept).getName().split("#")[1];
        // Specialization of the concept in the last tranaction
        ArrayList<Integer> lastTransaction = this.transactions.get(this.transactions.size( ) - 1);
        lastTransaction.set(lastTransaction.size()-1, splConcept);
        Integer previous_previous_size = lastTransaction.size() - 1;

//            System.out.println("lastTransaction: " + lastTransaction);
//            System.out.println("previous_size: " + previous_size);
            
            if (!lastTransaction.isEmpty())
                System.out.println("splConceptC: " + splConcept + " (" + s_splConcept + ")");
                // if there is a second last
                if (previous_previous_size - 1 > 0){
                    if (lastTransaction.get(previous_previous_size - 1) <= splConcept){
                        this.concepts.set(this.concepts.size()-1, splConcept);
                        // Update the status of the last operation
                        this.lastOperation = Operation.SC;


                        AppariementExtensionTask task = new SpeConceptTask();
                        task.item = new Integer[]{splConcept};
                        this.appariement_extensions.add(task);
                    }
                }
                else{
                    this.concepts.set(this.concepts.size()-1, splConcept);
                    // Update the status of the last operation
                    this.lastOperation = Operation.SC;


                    AppariementExtensionTask task = new SpeConceptTask();
                    task.item = new Integer[]{splConcept};
                    this.appariement_extensions.add(task);
                }
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
    
    //specialise un concept du motif dans une position
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
}
