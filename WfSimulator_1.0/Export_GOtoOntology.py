__author__ = 'ahmedhalioui'

import rdflib
from rdflib import Literal, RDF, RDFS, Namespace, URIRef
import random
import os
import collections


indDir="/Users/ahmedhalioui/PycharmProjects/randomMonteCarlo/Phylogenetic Gazetteer/"

def queryOntology():

    # Define the graph g
    g = rdflib.Graph()
    print "load ontology ..."
    # load the ontology on the graph g
    g.load("/Users/ahmedhalioui/PycharmProjects/randomMonteCarlo/ontology/phylOntology_v51_schema.rdf")

    # process begins here !
    print "process individuals ..."
    unique = {}

    # for each file in lists
    for fname in os.listdir(indDir):
        if not fname == ".DS_Store":  # make sure it's a file, not a directory entry
            with open(indDir + fname) as f:
                name=fname.split("_")
                definitionProp=URIRef(u'http://purl.obolibrary.org/obo/IAO_0000115')
                gOp=name[0]
                go = URIRef(u'http://purl.obolibrary.org/obo/'+'GO_'+name[2][:-4])
                inds=open(indDir + fname,'r').readlines()

                for ind in inds:
                    numbers = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9']

                    # generate a unique id
                    id = ''.join(random.choice(numbers) for _ in range(15))
                    while (id in unique.keys()):
                        id = ''.join(random.choice(numbers) for _ in range(15))
                    id.replace('_', '')

                    indiv = ind.strip('\n')

                    # indiv already exists in unique dictionary
                    if (indiv.lower() in unique.values()):
                        # get unique id and value and use these values !
                        for k, v in unique.iteritems():
                            if str(v).lower() == str(indiv).lower():
                                indiv=v.lower()
                                id=k
                                break
                                # print indiv

                    # id is not in unique dictionary
                    else :
                        # print indiv
                        unique[id]=indiv.lower()
                        unique = collections.OrderedDict(sorted(unique.items()))
                        uid = URIRef(u'http://purl.obolibrary.org/obo/' + gOp + '_' + id)

                        # for k,v in unique.iteritems():
                        #     print k+"\t"+v

                        g.add([uid, RDF.type, go])
                        g.add([uid, RDFS.label, Literal(indiv)])
                        g.add([uid, definitionProp, Literal(gOp)])

                        print "%s, %s, %s" % (uid, RDF.type, go)
                        print "%s, %s, %s" % (uid, RDFS.label, Literal(indiv))
                        print "%s, %s, %s" % (uid, definitionProp, Literal(gOp))




    output_address = "/Users/ahmedhalioui/PycharmProjects/randomMonteCarlo/ontology/result.rdf"
    g.serialize(destination=output_address)

queryOntology()

