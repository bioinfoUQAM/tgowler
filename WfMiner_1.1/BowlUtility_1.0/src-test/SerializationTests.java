import bowl.BowlLoader;
import bowl.BowlUtility;
import bowl.Converter2Bowl;
import cleanrepresentation.Mapping;
import cleanrepresentation.OntologyMiningPreprocessor;
import ontologyrep20.Concept;
import ontologyrep20.Instance;
import ontologyrep20.LinkedList;
import ontologyrep20.OntoRepresentation;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.Test;
import utility.SesameHelper;

import java.util.ArrayList;

public class SerializationTests {

    @Test
    public void A_Test(){
        Model model = SesameHelper.readRDF("./resources/ontology-travel.owl", RDFFormat.RDFXML, "http://www.owl-ontologies.com/");
        //probablement lancer une step d'inference ?
        final OntoRepresentation representation = new OntoRepresentation();
        BowlUtility.loadOntologyFromModel(model, representation);

        System.out.println(" ============================ ");
        //HTMLViz.dumpOriginalGraph("../GraphLinearisation/test-viz.html", representation);
        System.out.println(" ============================ ");

        final OntoRepresentation cleanRepresentation = representation.clone();//shallow copy, hierarchy links will be removed
        final Mapping mapping = OntologyMiningPreprocessor.performAll(Mapping.POSTFIX_ORDER, representation, cleanRepresentation); // POSTFIX_ORDER
        //System.out.println(mapping.toString());
        System.out.println(mapping.classesByIDs.toString());
        System.out.println(mapping.propertiesByIDs.toString());

        System.out.println(" ============================ ");

        //HTMLViz.dumpCleanedHierarchy("../GraphLinearisation/test-viz2.html", representation, cleanRepresentation);
        System.out.println(" ============================ ");
        //HTMLViz.dumpCleanedMappedHierarchy("../GraphLinearisation/test-viz3.html", representation, cleanRepresentation, mapping);
        final OntoRepresentation serializedRepresentation = new OntoRepresentation();// cleanRepresentation.clone();//shallow copy, hierarchy links will be removed
        OntologyMiningPreprocessor.updateOntology(mapping, cleanRepresentation, serializedRepresentation);

        //HTMLViz.dumpOriginalGraph("../GraphLinearisation/test-viz5", cleanRepresentation);
        //HTMLViz.dumpOriginalGraph("../GraphLinearisation/test-viz6", serializedRepresentation);

        Converter2Bowl.serialize("./someontology.bowl", serializedRepresentation, model, null, false);
        OntoRepresentation onto = BowlLoader.deserialize("./someontology.bowl", false, false);
        //HTMLViz.dumpOriginalGraph("../GraphLinearisation/test-viz7", onto);
        //HTMLViz.dumpCleanedHierarchy("../GraphLinearisation/test-viz4.html", onto, onto);
    }

//    @Test
//    public void B_Test(){
//        OntoRepresentation onto = BowlLoader.deserialize("../WfMiner_1.1/phylOntology_v51_small.bowl", true, true);
//        {
//            final LinkedList<Concept> concepts = onto.getAllConcepts();
//            concepts.reset();
//            do {
//                final Concept oldConcept = concepts.curr().value;
//                //final String label = oldConcept.getName();
//                System.out.println("concept: "+oldConcept.toString());
//            }
//            while (concepts.hasNext());
//        }
//
//        ArrayList<Instance> allInstances = onto.getAllInstances();
//        for(final Instance inst : allInstances){
//            System.out.println("inst: "+inst.getName()+" "+inst.concept.toString());
//        }
//
//    }
}
