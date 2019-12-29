package ca.uqam.gdac.framework.matcher;

import java.util.ArrayList;
import ontologyrep2.OntoRepresentation;
import ontopatternmatching.Motif;
import ontopatternmatching.Workflow;


class BaseMatcher 
{
	//-------------------------------------------------------------- Attributes
	// Ontology agent used to explore the ontology
	//protected HierarchyRepresentation<Integer> hierarchyRepresentation;
        protected OntoRepresentation hierarchyRepresentation;
	
	// Pattern to match on the input sequence
	protected Motif pattern;
	
	// Workflow to be matched against the pattern
	protected Workflow input;
	
	//------------------------------------------------------------- Constructor
	/*public BaseMatcher( final Pattern pattern, final UserWorkflow input, final HierarchyRepresentation<Integer> hierarchyRepresentation )
	{
		this.pattern = pattern;
		this.input = input;
		this.hierarchyRepresentation = hierarchyRepresentation;
	}*/
        
        public BaseMatcher( final Motif pattern, final Workflow input, final OntoRepresentation hierarchyRepresentation )
	{
		this.pattern = pattern;
		this.input = input;
		this.hierarchyRepresentation = hierarchyRepresentation;
	}
	
	//------------------------------------------------ Protected static methods
	protected static ArrayList<Integer> subArrayList( final ArrayList<Integer> list, final int start, final int end ) 
	{
		return ( new ArrayList<Integer>( list.subList( start, end ) ) );
	}
}
