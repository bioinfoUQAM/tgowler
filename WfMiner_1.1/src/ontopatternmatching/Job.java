/**
 *
 * @author Enridestroy
 */
package ontopatternmatching;

import ontologyrep20.OntoRepresentation;

public interface Job {
    public int doJob(JobBlock j, Sequence sequence, int[] appariement, boolean[] modifications, OntoRepresentation ontology, Motif m);
    
    public int doJob_with_output(JobBlock j, Sequence sequence, int[] appariement, boolean[] modifications, OntoRepresentation ontology, Motif m, String[] info);
}
