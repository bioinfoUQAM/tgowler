package utility;

import cleanrepresentation.Mapping;
import ontologyrep20.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class HTMLViz {

    public final static java.nio.charset.Charset ENCODAGE = Charset.forName("UTF-8");

    public static void dumpOriginalGraph(final String _filename, final OntoRepresentation _onto){
        final String begin = "<!DOCTYPE html>\n" +
                "<html lang=\"en-us\">\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\" />\n" +
                "    <meta name=\"author\" content=\"Vincent Link, Steffen Lohmann, Eduard Marbach, Stefan Negru, Vitalis Wiens\" />\n" +
                "    <meta name=\"keywords\" content=\"webvowl, vowl, visual notation, web ontology language, owl, rdf, ontology visualization, ontologies, semantic web\" />\n" +
                "    <meta name=\"description\" content=\"WebVOWL - Web-based Visualization of Ontologies\" />\n" +
                "    <meta name=\"robots\" content=\"noindex,nofollow\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no, \">\n" +
                "    <meta name=\"apple-mobile-web-app-capable\" content=\"yes\">\n" +
                "    <link rel=\"icon\" href=\"favicon.ico\" type=\"image/x-icon\" />\n" +
                "    <title>linearisation</title>\n" +
                "	<style>\n" +
                "	#cy {\n" +
                "	  width: 1800px;\n" +
                "	  height: 1000px;\n" +
                "	  display: block;\n" +
                "         background-color: #ecf0f1;\n" +
                "	}\n" +
                "	</style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "	<div id=\"cy\"></div>\n" +
                "	<script src=\"webowl/js/cytoscape.js\"></script>\n" +
                "       <script src=\"webowl/js/cytoscape-cose-bilkent.js\"></script>\n" +
                "       <script src=\"webowl/js/cola.min.js\"></script>\n"+
                "       <script src=\"webowl/js/cytoscape-cola.js\"></script>\n" +
                "       <script src=\"webowl/js/klay.js\"></script>\n" +
                "       <script src=\"webowl/js/cytoscape-klay.js\"></script>\n"+
                "	<script type=\"text/javascript\">\n" +
                "	\n";

        final String end = "	\n" +
                "	/*var structure = [ // list of graph elements to start with\n" +
                "		{ // node a\n" +
                "		  data: { id: 'a' }\n" +
                "		},\n" +
                "		{ // node b\n" +
                "		  data: { id: 'b' }\n" +
                "		},\n" +
                "		{ // edge ab\n" +
                "		  data: { id: 'ab', source: 'a', target: 'b' }\n" +
                "		}\n" +
                "	  ];*/\n" +
                "	var cy = cytoscape({\n" +
                "	  container: document.getElementById('cy'), // container to render in\n" +
                "	  boxSelectionEnabled: false,\n" +
                "      autounselectify: true,\n" +
                "	  elements: structure,\n" +
                "\n" +

                "	  style: [\n" +
                "		{\n" +
                "		  selector: 'node',\n" +
                "		  style: {\n" +
                "			'label': 'data(label)',\n" +
                "                       'background-color': '#fff',\n" +
                "			'shape': 'ellipsis',\n" +
                "			'padding-top': '5px',\n" +
                "			'padding-left': '15px',\n" +
                "			'padding-bottom': '5px',\n" +
                "			'padding-right': '15px',\n" +
                "			'width': 'label',\n" +
                "			'border-width': 1.8,\n" +
                "			'text-valign': 'center',\n" +
                "			'ghost': 'yes',\n" +
                //"			'shadow-color': 'black',\n" +
                "			'ghost-offset-x': 1,\n" +
                "			'ghost-offset-y': 2.5,\n" +
                "			'ghost-opacity': 0.2,\n" +
                "			'text-halign': 'center'\n" +
                "		  }\n" +
                "		},\n" +


//                "		{\n" +
//                "		  selector: '.is_class',\n" +
//                "		  css: {\n" +
//                "                   shape: 'ellipsis',\n" +
//                "		    'background-color': '#acf',\n" +
//                "		    'border-width': 2,\n" +
//                "		    'text-valign': 'center',\n" +
//                "		    'text-halign': 'center',\n" +
//                "		    'border-color': 'black', \n" +
//                "                   'padding-top': '25px',\n" +
//                "                   'padding-left': '35px',\n" +
//                "                   'padding-bottom': '25px',\n" +
//                "                   'padding-right': '35px'\n" +
//                "		  }\n" +
//                "		},\n" +



                //is_prop_node


                "		{\n" +
                "		  selector: 'edge',\n" +
                "		  css: {\n" +
                "			'target-arrow-shape': 'triangle'\n" +
                "		  },\n" +
                "		  style: {\n" +
                "			'target-arrow-shape': 'triangle', 'width':1.0,'opacity':0.8,'line-color':'#000', 'label': 'data(label)' // maps to data.label\n" +
                "                       ,'curve-style':'unbundled-bezier','control-point-distances':120,'control-point-weights':0.25     \n" +
                "		  }\n" +
                "		},\n" +


                "		{\n" +
                "		  selector: '.is_subclass',\n" +
                "		  css: {\n" +
                "                   'target-arrow-shape': 'triangle'\n" +
                "		  },\n" +
                "		  style: {\n" +
                "			'target-arrow-shape': 'triangle', 'width':0.8,'opacity':0.8,'line-color':'#888', 'label': 'data(label)' // maps to data.label\n" +
                "                       ,'curve-style':'bezier','line-style':'dashed', 'control-point-distances':120,'control-point-weights':0.25     \n" +
                "		  }\n" +
                "		},\n" +

                "		{\n" +
                "		  selector: '.is_canon_subclass',\n" +
                "		  css: {\n" +
                "                   'target-arrow-shape': 'triangle'\n" +
                "		  },\n" +
                "		  style: {\n" +
                "			'target-arrow-shape': 'triangle', 'width':2.0,'opacity':1.0,'line-color':'red', 'label': 'data(label)' // maps to data.label\n" +
                "                       ,'curve-style':'bezier','line-style':'dashed', 'control-point-distances':120,'control-point-weights':0.25     \n" +
                "		  }\n" +
                "		},\n" +


                "		{\n" +
                "		  selector: ':selected',\n" +
                "		  css: {\n" +
                "			'background-color': 'black',\n" +
                "			'line-color': 'black',\n" +
                "			'target-arrow-color': 'black',\n" +
                "			'source-arrow-color': 'black'\n" +
                "		  }\n" +
                "		}\n" +
                "	  ],\n" +


                "	  \n" +
                "	  \n" +
                "	  /*style: [ // the stylesheet for the graph\n" +
                "		{\n" +
                "		  selector: 'node',\n" +
                "		  style: {\n" +
                "			'background-color': '#666',\n" +
                "			'label': 'data(id)'\n" +
                "		  }\n" +
                "		},\n" +
                "\n" +
                "		{\n" +
                "		  selector: 'edge',\n" +
                "		  style: {\n" +
                "			'width': 3,\n" +
                "			'line-color': '#ccc',\n" +
                "			'target-arrow-color': '#ccc',\n" +
                "			'target-arrow-shape': 'triangle'\n" +
                "		  }\n" +
                "		}\n" +
                "	  ],*/\n" +
                "\n" +

                "	 layout: {\n" +
                "        name: 'klay', nodeDimensionsIncludeLabels: true, animate: true, klay: { spacing: 25,  thoroughness: 10, direction: 'UP' }\n" +
                //"	 name: 'cose-bilkent', animate: false, fit: false, nodeDimensionsIncludeLabels: true, numIter: 10000\n" +
                //"	 name: 'cose-bilkent', animate: false, fit: false, nodeDimensionsIncludeLabels: true\n" +
                //"		name: 'cola', edgeLength: 0.01, nodeSpacing: function( node ){ return 35; }, maxSimulationTime: 25000, nodeDimensionsIncludeLabels: true, fit: false, randomize: true\n" +
                //"		name: 'cola', ungrabifyWhileSimulating: false, avoidOverlap: true, handleDisconnected:false, edgeLength: 200, nodeSpacing: function( node ){ return 100; }, maxSimulationTime: 30000, fit: false, nodeDimensionsIncludeLabels: true, refresh: 1, randomize: true\n" +
                "		}\n" +


                "	});\n" +
                "	\n" +
                "	</script>\n" +
                "</body>\n" +
                "</html>";

        boolean use_classes = true;
        boolean use_properties = false;
        boolean use_instances = true;
        boolean show_is_a = true && use_instances && use_classes;
        boolean show_subclass = use_classes && true;
        boolean show_subproperty = use_properties && true;
        boolean show_triples = true && use_instances;
        boolean show_subclass_label = false && show_subclass;
        boolean show_subproperty_label = false && show_subproperty;
        boolean show_triplets_label = true && use_instances;
        boolean show_is_a_label = false && show_is_a;
        final int nbrConcepts = use_classes ? _onto.concepts.size : 0;
        final int nbrProperties = use_properties ? _onto.relations.size : 0;

        final ArrayList<String> nodes = new ArrayList<>( nbrConcepts + nbrProperties);//String[/*_onto.concepts.size*/];
        int count = 0;




        final int SHOW_PROPERTIES = 0x01;
        final int SHOW_CLASSES = 0x02;

        final int capabilities = SHOW_CLASSES | SHOW_PROPERTIES;

        if(0 != (capabilities & SHOW_CLASSES)) {
            nodes.add("{ data: { id: '"+100000+"', label: \"{}\", weight: 1500 }, classes: 'is_class' }");
            final LinkedList<Concept> concepts = _onto.getAllConcepts();
            if (use_classes) {
                concepts.reset();
                do {
                    Concept c = concepts.curr().value;
                    String label = c.getName();
                    nodes.add(/*[count++] =*/ "{ data: { id: '" + (c.index) + "', label: \"" + label.substring(label.lastIndexOf("#")) + "(" + c.index + ")" + "\", weight: 1500 }, classes: 'is_class' }");
                    count++;
                    //if(count > 5) break;
                }
                while (concepts.hasNext());
            }

            final ArrayList<String> edges = new ArrayList<>(_onto.instances.size());//new String[_onto.instances.size()+_onto.instance_triples.size()];
            final int lastNumber = count;//maxIdInstance+nbrConcepts ? + nbrProps ?
            count = 1;
//        for(final Triplet triplet : _onto.triplets){
//            String label = triplet.relation.getName();
//            edges.add(/*[count++] =*/ "{ data: { id:'"+(lastNumber+(count++))+"', source: '"+(maxIdInstance+triplet.domaine.index)+"', target: '"+(maxIdInstance+triplet.codomaine.index)+"'"+(true ? ", label: '"+label.substring(label.lastIndexOf("#"))+"'": "")+"}}");
//            if(edges.size() > 250) break;
//        }

            if (show_subclass) {
                concepts.reset();
                do {
                    Concept c = concepts.curr().value;
                    final ArrayList<Concept> parents = new ArrayList<>();//_onto.getParents(c);
                    if (c.parent != null) parents.add(c.parent);

                    System.out.println(c.toString() + " " + parents.toString());
                    if (!parents.isEmpty()) {
                        for (final Concept cc : parents) {
                            //if(c.parent != null && c.parent.index == cc.index){
                            //    edges.add(/*edges[count++] =*/ "{ data: { id:'"+(lastNumber+(count++))+"', source: '"+(c.index)+"', target: '"+(cc.index)+"'"+(show_subclass_label ? ", label: 'subclass_of'": "")+"}, classes: 'is_canon_subclass'}");
                            //}
                            //else{
                            edges.add(/*edges[count++] =*/ "{ data: { id:'" + (lastNumber + (count++)) + "', source: '" + (c.index) + "', target: '" + (cc.index) + "'" + (show_subclass_label ? ", label: 'subclass_of'" : "") + "}, classes: 'is_subclass'}");
                            //}

                        }
                    } else {
                        edges.add(/*edges[count++] =*/ "{ data: { id:'" + (lastNumber + (count++)) + "', source: '" + (c.index) + "', target: '" + (100000) + "'" + (show_subclass_label ? ", label: 'subclass_of'" : "") + "}, classes: 'is_subclass'}");
                    }


                }
                while (concepts.hasNext());
                final String structure = "var structure = { nodes: [\n" + String.join(",", nodes.toArray(new String[nodes.size()])) + "],\n edges: [\n" + String.join(",", edges.toArray(new String[edges.size()])) + " ] };\n";
                final Path p = Paths.get(_filename+".classes.html");
                try(final OutputStream out = new BufferedOutputStream(Files.newOutputStream(p, CREATE, TRUNCATE_EXISTING))){
                    out.write(begin.getBytes(ENCODAGE));
                    out.write(structure.getBytes(ENCODAGE));
                    out.write(end.getBytes(ENCODAGE));
                    out.flush();
                }
                catch(final IOException e){
                    System.out.println("could not read queries file... " + e);
                }
            }
        }

        nodes.clear();
        count = 0;

        if(0 != (capabilities & SHOW_PROPERTIES)) {
            nodes.add("{ data: { id: '"+100000+"', label: \"{}\", weight: 1500 }, classes: 'is_class' }");
            final LinkedList<Relation> relations = _onto.getAllProperties();
            relations.reset();
            do {
                Relation c = relations.curr().value;
                String label = c.getName();
                nodes.add(/*[count++] =*/ "{ data: { id: '" + (c.index) + "', label: \"" +
                        label.substring(label.lastIndexOf("#")) + "(" + c.index + ")" + "\", weight: 1500 }, classes: 'is_class' }");
                count++;
                //if(count > 5) break;
            }
            while (relations.hasNext());

            final ArrayList<String> edges = new ArrayList<>(_onto.instances.size());//new String[_onto.instances.size()+_onto.instance_triples.size()];
            final int lastNumber = count;//maxIdInstance+nbrConcepts ? + nbrProps ?
            count = 1;

            relations.reset();
            do {
                Relation c = relations.curr().value;
                final ArrayList<Relation> parents = new ArrayList<>();//_onto.getParents(c);
                if (c.parent != null) parents.add(c.parent);

                System.out.println(c.toString() + " " + parents.toString());
                if (!parents.isEmpty()) {
                    for (final Relation cc : parents) {
                        edges.add(/*edges[count++] =*/ "{ data: { id:'" + (lastNumber + (count++)) + "', source: '" +
                                (c.index) + "', target: '" + (cc.index) + "'" + (show_subclass_label ? ", label: 'subclass_of'" : "") + "}, classes: 'is_subclass'}");
                    }
                } else {
                    edges.add(/*edges[count++] =*/ "{ data: { id:'" + (lastNumber + (count++)) + "', source: '" +
                            (c.index) + "', target: '" + (100000) + "'" + (show_subclass_label ? ", label: 'subclass_of'" : "") + "}, classes: 'is_subclass'}");
                }
            }
            while (relations.hasNext());
            final String structure = "var structure = { nodes: [\n" + String.join(",", nodes.toArray(new String[nodes.size()])) + "],\n edges: [\n" + String.join(",", edges.toArray(new String[edges.size()])) + " ] };\n";
            final Path p = Paths.get(_filename+".properties.html");
            try(final OutputStream out = new BufferedOutputStream(Files.newOutputStream(p, CREATE, TRUNCATE_EXISTING))){
                out.write(begin.getBytes(ENCODAGE));
                out.write(structure.getBytes(ENCODAGE));
                out.write(end.getBytes(ENCODAGE));
                out.flush();
            }
            catch(final IOException e){
                System.out.println("could not read queries file... " + e);
            }
        }
    }

    public static void dumpCleanedHierarchy(final String _filename, final OntoRepresentation _originalOnto, final OntoRepresentation _cleanHierarchies){
        final String begin = "<!DOCTYPE html>\n" +
                "<html lang=\"en-us\">\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\" />\n" +
                "    <meta name=\"author\" content=\"Vincent Link, Steffen Lohmann, Eduard Marbach, Stefan Negru, Vitalis Wiens\" />\n" +
                "    <meta name=\"keywords\" content=\"webvowl, vowl, visual notation, web ontology language, owl, rdf, ontology visualization, ontologies, semantic web\" />\n" +
                "    <meta name=\"description\" content=\"WebVOWL - Web-based Visualization of Ontologies\" />\n" +
                "    <meta name=\"robots\" content=\"noindex,nofollow\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no, \">\n" +
                "    <meta name=\"apple-mobile-web-app-capable\" content=\"yes\">\n" +
                "    <link rel=\"icon\" href=\"favicon.ico\" type=\"image/x-icon\" />\n" +
                "    <title>linearisation</title>\n" +
                "	<style>\n" +
                "	#cy {\n" +
                "	  width: 1800px;\n" +
                "	  height: 1000px;\n" +
                "	  display: block;\n" +
                "         background-color: #ecf0f1;\n" +
                "	}\n" +
                "	</style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "	<div id=\"cy\"></div>\n" +
                "	<script src=\"webowl/js/cytoscape.js\"></script>\n" +
                "       <script src=\"webowl/js/cytoscape-cose-bilkent.js\"></script>\n" +
                "       <script src=\"webowl/js/cola.min.js\"></script>\n"+
                "       <script src=\"webowl/js/cytoscape-cola.js\"></script>\n" +
                "       <script src=\"webowl/js/klay.js\"></script>\n" +
                "       <script src=\"webowl/js/cytoscape-klay.js\"></script>\n"+
                "	<script type=\"text/javascript\">\n" +
                "	\n";

        final String end = "	\n" +
                "	/*var structure = [ // list of graph elements to start with\n" +
                "		{ // node a\n" +
                "		  data: { id: 'a' }\n" +
                "		},\n" +
                "		{ // node b\n" +
                "		  data: { id: 'b' }\n" +
                "		},\n" +
                "		{ // edge ab\n" +
                "		  data: { id: 'ab', source: 'a', target: 'b' }\n" +
                "		}\n" +
                "	  ];*/\n" +
                "	var cy = cytoscape({\n" +
                "	  container: document.getElementById('cy'), // container to render in\n" +
                "	  boxSelectionEnabled: false,\n" +
                "      autounselectify: true,\n" +
                "	  elements: structure,\n" +
                "\n" +

                "	  style: [\n" +
                "		{\n" +
                "		  selector: 'node',\n" +
                "		  style: {\n" +
                "			'label': 'data(label)',\n" +
                "                       'background-color': '#fff',\n" +
                "			'shape': 'ellipsis',\n" +
                "			'padding-top': '5px',\n" +
                "			'padding-left': '15px',\n" +
                "			'padding-bottom': '5px',\n" +
                "			'padding-right': '15px',\n" +
                "			'width': 'label',\n" +
                "			'border-width': 1.8,\n" +
                "			'text-valign': 'center',\n" +
                "			'ghost': 'yes',\n" +
                //"			'shadow-color': 'black',\n" +
                "			'ghost-offset-x': 1,\n" +
                "			'ghost-offset-y': 2.5,\n" +
                "			'ghost-opacity': 0.2,\n" +
                "			'text-halign': 'center'\n" +
                "		  }\n" +
                "		},\n" +


//                "		{\n" +
//                "		  selector: '.is_class',\n" +
//                "		  css: {\n" +
//                "                   shape: 'ellipsis',\n" +
//                "		    'background-color': '#acf',\n" +
//                "		    'border-width': 2,\n" +
//                "		    'text-valign': 'center',\n" +
//                "		    'text-halign': 'center',\n" +
//                "		    'border-color': 'black', \n" +
//                "                   'padding-top': '25px',\n" +
//                "                   'padding-left': '35px',\n" +
//                "                   'padding-bottom': '25px',\n" +
//                "                   'padding-right': '35px'\n" +
//                "		  }\n" +
//                "		},\n" +



                //is_prop_node


                "		{\n" +
                "		  selector: 'edge',\n" +
                "		  css: {\n" +
                "			'target-arrow-shape': 'triangle'\n" +
                "		  },\n" +
                "		  style: {\n" +
                "			'target-arrow-shape': 'triangle', 'width':1.0,'opacity':0.8,'line-color':'#000', 'label': 'data(label)' // maps to data.label\n" +
                "                       ,'curve-style':'unbundled-bezier','control-point-distances':120,'control-point-weights':0.25     \n" +
                "		  }\n" +
                "		},\n" +


                "		{\n" +
                "		  selector: '.is_subclass',\n" +
                "		  css: {\n" +
                "                   'target-arrow-shape': 'triangle'\n" +
                "		  },\n" +
                "		  style: {\n" +
                "			'target-arrow-shape': 'triangle', 'width':0.8,'opacity':0.8,'line-color':'#888', 'label': 'data(label)' // maps to data.label\n" +
                "                       ,'curve-style':'bezier','line-style':'dashed', 'control-point-distances':120,'control-point-weights':0.25     \n" +
                "		  }\n" +
                "		},\n" +

                "		{\n" +
                "		  selector: '.is_canon_subclass',\n" +
                "		  css: {\n" +
                "                   'target-arrow-shape': 'triangle'\n" +
                "		  },\n" +
                "		  style: {\n" +
                "			'target-arrow-shape': 'triangle', 'width':2.0,'opacity':1.0,'line-color':'red', 'label': 'data(label)' // maps to data.label\n" +
                "                       ,'curve-style':'bezier','line-style':'dashed', 'control-point-distances':120,'control-point-weights':0.25     \n" +
                "		  }\n" +
                "		},\n" +


                "		{\n" +
                "		  selector: ':selected',\n" +
                "		  css: {\n" +
                "			'background-color': 'black',\n" +
                "			'line-color': 'black',\n" +
                "			'target-arrow-color': 'black',\n" +
                "			'source-arrow-color': 'black'\n" +
                "		  }\n" +
                "		}\n" +
                "	  ],\n" +


                "	  \n" +
                "	  \n" +
                "	  /*style: [ // the stylesheet for the graph\n" +
                "		{\n" +
                "		  selector: 'node',\n" +
                "		  style: {\n" +
                "			'background-color': '#666',\n" +
                "			'label': 'data(id)'\n" +
                "		  }\n" +
                "		},\n" +
                "\n" +
                "		{\n" +
                "		  selector: 'edge',\n" +
                "		  style: {\n" +
                "			'width': 3,\n" +
                "			'line-color': '#ccc',\n" +
                "			'target-arrow-color': '#ccc',\n" +
                "			'target-arrow-shape': 'triangle'\n" +
                "		  }\n" +
                "		}\n" +
                "	  ],*/\n" +
                "\n" +

                "	 layout: {\n" +
                "        name: 'klay', nodeDimensionsIncludeLabels: true, animate: true, klay: { spacing: 25,  thoroughness: 10, direction: 'UP' }\n" +
                //"	 name: 'cose-bilkent', animate: false, fit: false, nodeDimensionsIncludeLabels: true, numIter: 10000\n" +
                //"	 name: 'cose-bilkent', animate: false, fit: false, nodeDimensionsIncludeLabels: true\n" +
                //"		name: 'cola', edgeLength: 0.01, nodeSpacing: function( node ){ return 35; }, maxSimulationTime: 25000, nodeDimensionsIncludeLabels: true, fit: false, randomize: true\n" +
                //"		name: 'cola', ungrabifyWhileSimulating: false, avoidOverlap: true, handleDisconnected:false, edgeLength: 200, nodeSpacing: function( node ){ return 100; }, maxSimulationTime: 30000, fit: false, nodeDimensionsIncludeLabels: true, refresh: 1, randomize: true\n" +
                "		}\n" +


                "	});\n" +
                "	\n" +
                "	</script>\n" +
                "</body>\n" +
                "</html>";

        boolean use_classes = true;
        boolean use_properties = false;
        boolean use_instances = true;
        boolean show_is_a = true && use_instances && use_classes;
        boolean show_subclass = use_classes && true;
        boolean show_subproperty = use_properties && true;
        boolean show_triples = true && use_instances;
        boolean show_subclass_label = false && show_subclass;
        boolean show_subproperty_label = false && show_subproperty;
        boolean show_triplets_label = true && use_instances;
        boolean show_is_a_label = false && show_is_a;
        final int nbrConcepts = use_classes ? _originalOnto.concepts.size : 0;
        final int nbrProperties = use_properties ? _originalOnto.relations.size : 0;

        final ArrayList<String> nodes = new ArrayList<>( nbrConcepts + nbrProperties);//String[/*_onto.concepts.size*/];
        int count = 0;


        nodes.add("{ data: { id: '"+100000+"', label: \"{}\", weight: 1500 }, classes: 'is_class' }");

        final LinkedList<Concept> concepts = _originalOnto.getAllConcepts();
        if(use_classes){
            concepts.reset();
            do{
                Concept c = concepts.curr().value;
                String label = c.getName();
                nodes.add(/*[count++] =*/ "{ data: { id: '"+(c.index)+"', label: \""+label.substring(label.lastIndexOf("#")) + "("+c.index+")"+"\", weight: 1500 }, classes: 'is_class' }");
                count++;
                //if(count > 5) break;
            }
            while(concepts.hasNext());
        }

        final ArrayList<String> edges = new ArrayList<>(_originalOnto.instances.size());//new String[_onto.instances.size()+_onto.instance_triples.size()];
        final int lastNumber = count;//maxIdInstance+nbrConcepts ? + nbrProps ?
        count =1;

        if(show_subclass){
            concepts.reset();
            do{
                Concept c = concepts.curr().value;
                final ArrayList<Concept> parents = _originalOnto.getParents(c);
                System.out.println(c.toString()+" "+parents.toString());
                if(!parents.isEmpty()){
                    for(final Concept cc : parents){
                        if(c.parent != null && ((Concept)_cleanHierarchies.index_concepts_by_name.get(c.getName())).parent.index == cc.index){
                            edges.add(/*edges[count++] =*/ "{ data: { id:'"+(lastNumber+(count++))+"', source: '"+(c.index)+"', target: '"+(cc.index)+"'"+(show_subclass_label ? ", label: 'subclass_of'": "")+"}, classes: 'is_canon_subclass'}");
                        }
                        else{
                            edges.add(/*edges[count++] =*/ "{ data: { id:'"+(lastNumber+(count++))+"', source: '"+(c.index)+"', target: '"+(cc.index)+"'"+(show_subclass_label ? ", label: 'subclass_of'": "")+"}, classes: 'is_subclass'}");
                        }

                    }
                }
                else{
                    edges.add(/*edges[count++] =*/ "{ data: { id:'"+(lastNumber+(count++))+"', source: '"+(c.index)+"', target: '"+(100000)+"'"+(show_subclass_label ? ", label: 'subclass_of'": "")+"}, classes: 'is_canon_subclass'}");
                }


            }
            while(concepts.hasNext());
        }


        final String structure = "var structure = { nodes: [\n" + String.join(",", nodes.toArray(new String[nodes.size()])) + "],\n edges: [\n" + String.join(",", edges.toArray(new String[edges.size()])) + " ] };\n";

        final Path p = Paths.get(_filename);
        try(final OutputStream out = new BufferedOutputStream(Files.newOutputStream(p, CREATE, TRUNCATE_EXISTING))){
            out.write(begin.getBytes(ENCODAGE));
            out.write(structure.getBytes(ENCODAGE));
            out.write(end.getBytes(ENCODAGE));
            out.flush();
        }
        catch(final IOException e){
            System.out.println("could not read queries file... " + e);
        }
    }

    public static void dumpCleanedMappedHierarchy(final String _filename, final OntoRepresentation _originalOnto,
                                                  final OntoRepresentation _cleanHierarchies, final Mapping _mapping){
        final String begin = "<!DOCTYPE html>\n" +
                "<html lang=\"en-us\">\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\" />\n" +
                "    <meta name=\"author\" content=\"Vincent Link, Steffen Lohmann, Eduard Marbach, Stefan Negru, Vitalis Wiens\" />\n" +
                "    <meta name=\"keywords\" content=\"webvowl, vowl, visual notation, web ontology language, owl, rdf, ontology visualization, ontologies, semantic web\" />\n" +
                "    <meta name=\"description\" content=\"WebVOWL - Web-based Visualization of Ontologies\" />\n" +
                "    <meta name=\"robots\" content=\"noindex,nofollow\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no, \">\n" +
                "    <meta name=\"apple-mobile-web-app-capable\" content=\"yes\">\n" +
                "    <link rel=\"icon\" href=\"favicon.ico\" type=\"image/x-icon\" />\n" +
                "    <title>linearisation</title>\n" +
                "	<style>\n" +
                "	#cy {\n" +
                "	  width: 1800px;\n" +
                "	  height: 1000px;\n" +
                "	  display: block;\n" +
                "         background-color: #ecf0f1;\n" +
                "	}\n" +
                "	</style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "	<div id=\"cy\"></div>\n" +
                "	<script src=\"webowl/js/cytoscape.js\"></script>\n" +
                "       <script src=\"webowl/js/cytoscape-cose-bilkent.js\"></script>\n" +
                "       <script src=\"webowl/js/cola.min.js\"></script>\n"+
                "       <script src=\"webowl/js/cytoscape-cola.js\"></script>\n" +
                "       <script src=\"webowl/js/klay.js\"></script>\n" +
                "       <script src=\"webowl/js/cytoscape-klay.js\"></script>\n"+
                "	<script type=\"text/javascript\">\n" +
                "	\n";

        final String end = "	\n" +
                "	/*var structure = [ // list of graph elements to start with\n" +
                "		{ // node a\n" +
                "		  data: { id: 'a' }\n" +
                "		},\n" +
                "		{ // node b\n" +
                "		  data: { id: 'b' }\n" +
                "		},\n" +
                "		{ // edge ab\n" +
                "		  data: { id: 'ab', source: 'a', target: 'b' }\n" +
                "		}\n" +
                "	  ];*/\n" +
                "	var cy = cytoscape({\n" +
                "	  container: document.getElementById('cy'), // container to render in\n" +
                "	  boxSelectionEnabled: false,\n" +
                "      autounselectify: true,\n" +
                "	  elements: structure,\n" +
                "\n" +

                "	  style: [\n" +
                "		{\n" +
                "		  selector: 'node',\n" +
                "		  style: {\n" +
                "			'label': 'data(label)',\n" +
                "                       'background-color': '#fff',\n" +
                "			'shape': 'ellipsis',\n" +
                "			'padding-top': '5px',\n" +
                "			'padding-left': '15px',\n" +
                "			'padding-bottom': '5px',\n" +
                "			'padding-right': '15px',\n" +
                "			'width': 'label',\n" +
                "			'border-width': 1.8,\n" +
                "			'text-valign': 'center',\n" +
                "			'ghost': 'yes',\n" +
                //"			'shadow-color': 'black',\n" +
                "			'ghost-offset-x': 1,\n" +
                "			'ghost-offset-y': 2.5,\n" +
                "			'ghost-opacity': 0.2,\n" +
                "			'text-halign': 'center'\n" +
                "		  }\n" +
                "		},\n" +


//                "		{\n" +
//                "		  selector: '.is_class',\n" +
//                "		  css: {\n" +
//                "                   shape: 'ellipsis',\n" +
//                "		    'background-color': '#acf',\n" +
//                "		    'border-width': 2,\n" +
//                "		    'text-valign': 'center',\n" +
//                "		    'text-halign': 'center',\n" +
//                "		    'border-color': 'black', \n" +
//                "                   'padding-top': '25px',\n" +
//                "                   'padding-left': '35px',\n" +
//                "                   'padding-bottom': '25px',\n" +
//                "                   'padding-right': '35px'\n" +
//                "		  }\n" +
//                "		},\n" +



                //is_prop_node


                "		{\n" +
                "		  selector: 'edge',\n" +
                "		  css: {\n" +
                "			'target-arrow-shape': 'triangle'\n" +
                "		  },\n" +
                "		  style: {\n" +
                "			'target-arrow-shape': 'triangle', 'width':1.0,'opacity':0.8,'line-color':'#000', 'label': 'data(label)' // maps to data.label\n" +
                "                       ,'curve-style':'unbundled-bezier','control-point-distances':120,'control-point-weights':0.25     \n" +
                "		  }\n" +
                "		},\n" +


                "		{\n" +
                "		  selector: '.is_subclass',\n" +
                "		  css: {\n" +
                "                   'target-arrow-shape': 'triangle'\n" +
                "		  },\n" +
                "		  style: {\n" +
                "			'target-arrow-shape': 'triangle', 'width':0.8,'opacity':0.8,'line-color':'#888', 'label': 'data(label)' // maps to data.label\n" +
                "                       ,'curve-style':'bezier','line-style':'dashed', 'control-point-distances':120,'control-point-weights':0.25     \n" +
                "		  }\n" +
                "		},\n" +

                "		{\n" +
                "		  selector: '.is_canon_subclass',\n" +
                "		  css: {\n" +
                "                   'target-arrow-shape': 'triangle'\n" +
                "		  },\n" +
                "		  style: {\n" +
                "			'target-arrow-shape': 'triangle', 'width':2.0,'opacity':1.0,'line-color':'red', 'label': 'data(label)' // maps to data.label\n" +
                "                       ,'curve-style':'bezier','line-style':'dashed', 'control-point-distances':120,'control-point-weights':0.25     \n" +
                "		  }\n" +
                "		},\n" +


                "		{\n" +
                "		  selector: ':selected',\n" +
                "		  css: {\n" +
                "			'background-color': 'black',\n" +
                "			'line-color': 'black',\n" +
                "			'target-arrow-color': 'black',\n" +
                "			'source-arrow-color': 'black'\n" +
                "		  }\n" +
                "		}\n" +
                "	  ],\n" +


                "	  \n" +
                "	  \n" +
                "	  /*style: [ // the stylesheet for the graph\n" +
                "		{\n" +
                "		  selector: 'node',\n" +
                "		  style: {\n" +
                "			'background-color': '#666',\n" +
                "			'label': 'data(id)'\n" +
                "		  }\n" +
                "		},\n" +
                "\n" +
                "		{\n" +
                "		  selector: 'edge',\n" +
                "		  style: {\n" +
                "			'width': 3,\n" +
                "			'line-color': '#ccc',\n" +
                "			'target-arrow-color': '#ccc',\n" +
                "			'target-arrow-shape': 'triangle'\n" +
                "		  }\n" +
                "		}\n" +
                "	  ],*/\n" +
                "\n" +

                "	 layout: {\n" +
                "        name: 'klay', nodeDimensionsIncludeLabels: true, animate: true, klay: { spacing: 25,  thoroughness: 10, direction: 'UP' }\n" +
                //"	 name: 'cose-bilkent', animate: false, fit: false, nodeDimensionsIncludeLabels: true, numIter: 10000\n" +
                //"	 name: 'cose-bilkent', animate: false, fit: false, nodeDimensionsIncludeLabels: true\n" +
                //"		name: 'cola', edgeLength: 0.01, nodeSpacing: function( node ){ return 35; }, maxSimulationTime: 25000, nodeDimensionsIncludeLabels: true, fit: false, randomize: true\n" +
                //"		name: 'cola', ungrabifyWhileSimulating: false, avoidOverlap: true, handleDisconnected:false, edgeLength: 200, nodeSpacing: function( node ){ return 100; }, maxSimulationTime: 30000, fit: false, nodeDimensionsIncludeLabels: true, refresh: 1, randomize: true\n" +
                "		}\n" +


                "	});\n" +
                "	\n" +
                "	</script>\n" +
                "</body>\n" +
                "</html>";

        boolean use_classes = true;
        boolean use_properties = false;
        boolean use_instances = true;
        boolean show_is_a = true && use_instances && use_classes;
        boolean show_subclass = use_classes && true;
        boolean show_subproperty = use_properties && true;
        boolean show_triples = true && use_instances;
        boolean show_subclass_label = false && show_subclass;
        boolean show_subproperty_label = false && show_subproperty;
        boolean show_triplets_label = true && use_instances;
        boolean show_is_a_label = false && show_is_a;
        final int nbrConcepts = use_classes ? _originalOnto.concepts.size : 0;
        final int nbrProperties = use_properties ? _originalOnto.relations.size : 0;

        final ArrayList<String> nodes = new ArrayList<>( nbrConcepts + nbrProperties);//String[/*_onto.concepts.size*/];
        int count = 0;


        nodes.add("{ data: { id: '"+100000+"', label: \"{}\", weight: 1500 }, classes: 'is_class' }");

        final LinkedList<Concept> concepts = _originalOnto.getAllConcepts();
        if(use_classes){
            concepts.reset();
            do{
                Concept c = concepts.curr().value;
                String label = c.getName();
                nodes.add(/*[count++] =*/ "{ data: { id: '"+(c.index)+"', label: \""+
                        label.substring(label.lastIndexOf("#")) + "[" + (_mapping.idsByClasses.get(c)) +"]\", weight: 1500 }, classes: 'is_class' }");
                count++;
                //if(count > 5) break;
            }
            while(concepts.hasNext());
        }

        final ArrayList<String> edges = new ArrayList<>(_originalOnto.instances.size());//new String[_onto.instances.size()+_onto.instance_triples.size()];
        final int lastNumber = count;//maxIdInstance+nbrConcepts ? + nbrProps ?
        count =1;


        if(show_subclass){
            concepts.reset();
            do{
                Concept c = concepts.curr().value;
                final ArrayList<Concept> parents = _originalOnto.getParents(c);
                System.out.println(c.toString()+" "+parents.toString());
                if(!parents.isEmpty()){
                    for(final Concept cc : parents){
                        if(c.parent != null && ((Concept)_cleanHierarchies.index_concepts_by_name.get(c.getName())).parent.index == cc.index){
                            edges.add(/*edges[count++] =*/ "{ data: { id:'"+(lastNumber+(count++))+"', source: '"+(c.index)+"', target: '"+(cc.index)+"'"+(show_subclass_label ? ", label: 'subclass_of'": "")+"}, classes: 'is_canon_subclass'}");
                        }
                        else{
                            edges.add(/*edges[count++] =*/ "{ data: { id:'"+(lastNumber+(count++))+"', source: '"+(c.index)+"', target: '"+(cc.index)+"'"+(show_subclass_label ? ", label: 'subclass_of'": "")+"}, classes: 'is_subclass'}");
                        }
                    }
                }
                else{
                    edges.add(/*edges[count++] =*/ "{ data: { id:'"+(lastNumber+(count++))+"', source: '"+(c.index)+"', target: '"+(100000)+"'"+(show_subclass_label ? ", label: 'subclass_of'": "")+"}, classes: 'is_canon_subclass'}");
                }


            }
            while(concepts.hasNext());
        }


        final String structure = "var structure = { nodes: [\n" + String.join(",", nodes.toArray(new String[nodes.size()])) + "],\n edges: [\n" + String.join(",", edges.toArray(new String[edges.size()])) + " ] };\n";

        final Path p = Paths.get(_filename);
        try(final OutputStream out = new BufferedOutputStream(Files.newOutputStream(p, CREATE, TRUNCATE_EXISTING))){
            out.write(begin.getBytes(ENCODAGE));
            out.write(structure.getBytes(ENCODAGE));
            out.write(end.getBytes(ENCODAGE));
            out.flush();
        }
        catch(final IOException e){
            System.out.println("could not read queries file... " + e);
        }
    }

}
