__author__ = 'ahmedhalioui'

import random
import rdflib
import os
import re

############ DECLARE GLOBAL VARIABLES
with open('/Users/ahmedhalioui/Documents/MonteCarloSimulation_Phyloflows/Triplets/Triplets.txt', 'rt') as f:
    wordsFromTripletstxt = f.readlines()
f.close()
#wordsFromTripletstxt is a list of triplets - From the file: ./Triplets/Triplets.txt
wordsFromTripletstxt = [ w.rstrip() for w in wordsFromTripletstxt ]

with open('/Users/ahmedhalioui/Documents/MonteCarloSimulation_Phyloflows/IndividualsAll/Individuals.txt', 'rt') as f:
    wordsFromIndividualstxt = f.readlines()
f.close()
#wordsFromIndividualstxt is a list of all unique individuals - From the file: ./IndividualsAll/Individuals.txt
wordsFromIndividualstxt = [ w.rstrip() for w in wordsFromIndividualstxt ]

# with open('/Users/ahmedhalioui/Documents/MonteCarloSimulation_Phyloflows/AllGenSequences.xml', 'rt') as f:
#     wordsFromAllGenSequencesxml = f.readlines()
# f.close()
#wordsFromAllGenSequencesxml is a list of sequences generated after the simulation - From the file: ./AllGenSequences.xml
# wordsFromAllGenSequencesxml= [ w.rstrip() for w in wordsFromAllGenSequencesxml ]
############ DECLARE GLOBAL VARIABLES


###______
### Get wordsFromTripletstxt randomly with replacement - in order to create duplicants
###______
def new_deck_choice():

    for p in range(100000):
        wordsFromTripletstxt.append(random.choice(wordsFromTripletstxt))
        #print random.choice(wordsFromTripletstxt)

###______
### Get wordsFromTripletstxt randomly without replacement - a sampling
###______
def new_deck(indiv):
    IndividualsPerConceptDir="/Users/ahmedhalioui/Documents/MonteCarloSimulation_Phyloflows/IndividualsPerConcept/"
    Pattern = ['DataType_Individuals.txt','Source_Individuals.txt','Method_Individuals.txt','Models_Individuals.txt','DataCollectionProgram_Individuals.txt','SequenceAlignmentProgram_Individuals.txt','ModelProgram_Individuals.txt','PhylogeneticInferenceProgram_Individuals.txt','HypothesisValidationProgram_Individuals.txt','TreeVisualizationProgram_Individuals.txt']
    ConceptMandatory1File=IndividualsPerConceptDir+Pattern[0]
    ConceptMandatory2File=IndividualsPerConceptDir+Pattern[7]

    MList = []
    listL = []
    testList =[]
    c1=0
    c2=0
    # print "========================= new sampling ========================="
    for w in random.sample(wordsFromTripletstxt, indiv):
        # print w
        subject = w.split('\t')[0]
        object = w.split('\t')[2]
        property = w.split('\t')[1]
        listL.append(w)
        testList.append(subject)
        testList.append(object)

    #print "tuples are ok to place. Minimum Patten is here :)"
    conceptObjectList=[]
    # look for the other concepts
    for elemen in testList:
        concept=[]
        for fname in os.listdir(IndividualsPerConceptDir):
            if not fname==".DS_Store":    # make sure it's a file, not a directory entry
                with open(IndividualsPerConceptDir+fname) as f:
                    for line in f:       # process line by line
                        if elemen == line.rstrip('\n'):
                            concept.append(fname)
                            break
        conceptObjectList.append((elemen,concept))

    # print conceptObjectList

    DataType_Individuals = []
    Source_Individuals = []
    Method_Individuals = []
    Models_Individuals = []
    DataCollectionProgram_Individuals = []
    SequenceAlignmentProgram_Individuals = []
    ModelProgram_Individuals = []
    PhylogeneticInferenceProgram_Individuals = []
    HypothesisValidationProgram_Individuals = []
    TreeVisualizationProgram_Individuals = []

    for coupleInd in conceptObjectList:
        individual=coupleInd[0]

        for mathcedC in coupleInd[1]:

            if mathcedC == "DataType_Individuals.txt":
                DataType_Individuals.append(individual)
            if mathcedC == "Source_Individuals.txt":
                Source_Individuals.append(individual)
            if mathcedC == "Method_Individuals.txt":
                Method_Individuals.append(individual)
            if mathcedC == "Models_Individuals.txt":
                Models_Individuals.append(individual)
            if mathcedC == "DataCollectionProgram_Individuals.txt":
                DataCollectionProgram_Individuals.append(individual)
            if mathcedC == "SequenceAlignmentProgram_Individuals.txt":
                SequenceAlignmentProgram_Individuals.append(individual)
            if mathcedC == "ModelProgram_Individuals.txt":
                ModelProgram_Individuals.append(individual)
            if mathcedC == "PhylogeneticInferenceProgram_Individuals.txt":
                PhylogeneticInferenceProgram_Individuals.append(individual)
            if mathcedC == "HypothesisValidationProgram_Individuals.txt":
                HypothesisValidationProgram_Individuals.append(individual)
            if mathcedC == "TreeVisualizationProgram_Individuals.txt":
                TreeVisualizationProgram_Individuals.append(individual)

    # print "DataType_Individuals= %s" %DataType_Individuals
    # print "Source_Individuals= %s" %Source_Individuals
    # print "Method_Individuals= %s" %Method_Individuals
    # print "Models_Individuals= %s" %Models_Individuals
    # print "DataCollectionProgram_Individuals= %s" %DataCollectionProgram_Individuals
    # print "SequenceAlignmentProgram_Individuals= %s" %SequenceAlignmentProgram_Individuals
    # print "ModelProgram_Individuals= %s" %ModelProgram_Individuals
    # print "PhylogeneticInferenceProgram_Individuals= %s" %PhylogeneticInferenceProgram_Individuals
    # print "HypothesisValidationProgram_Individuals= %s" %HypothesisValidationProgram_Individuals
    # print "TreeVisualizationProgram_Individuals= %s" %TreeVisualizationProgram_Individuals

    MList.append(DataType_Individuals)
    MList.append(Source_Individuals)
    MList.append(Method_Individuals)
    MList.append(Models_Individuals)
    MList.append(DataCollectionProgram_Individuals)
    MList.append(SequenceAlignmentProgram_Individuals)
    MList.append(ModelProgram_Individuals)
    MList.append(PhylogeneticInferenceProgram_Individuals)
    MList.append(HypothesisValidationProgram_Individuals)
    MList.append(TreeVisualizationProgram_Individuals)

    if not (MList[0] and MList[7]): return new_deck(indiv)
    else: return (MList, len(testList))

###______
### Get wordsFromAllGenSequencesxml (simulated sequences) randomly without replacement - a sampling
###______
# def new_sequences(pop):
#
#     list = []
#     meanLengthOfw = 0
#     numberOfw=0
#     for w in random.sample(wordsFromAllGenSequencesxml, pop):
#         numberOfw=numberOfw+1
#         list.append(w)
#         #print w
#         lenW=len(str(w).split("\t"))
#         #print lenW
#         meanLengthOfw=meanLengthOfw+lenW/2-1
#         #print lenW/2-1
#     #print "Mean length is %d" %(meanLengthOfw/numberOfw)
#     return ((meanLengthOfw/numberOfw), list)

###______
### Fibonacci function
###______
def fib(n):

    cur = 1
    old = 1
    i = 1
    while (i < n):
        cur, old, i = cur+old, cur, i+1
    return cur

###______
### Create a file containing Triplets from qTriplets query
###______
def write_qTriplets(qTriplets, file):

    for rowT in qTriplets:
        #print rowT
        SubjectName=str(rowT).split("#")[1]
        SubjectNameValue=SubjectName.split("'), rdflib.term")[0]

        PropertyName=str(rowT).split("#")[2]
        PropertyNameValue=PropertyName.split("'), rdflib.term")[0]

        ObjectName=str(rowT).split("#")[3]
        ObjectNameValue=ObjectName.split("'))")[0]

        with open(file,"a") as fT:
            fT.write(SubjectNameValue+"\t"+PropertyNameValue+"\t"+ObjectNameValue+"\n")
            #print SubjectNameValue+"\t"+PropertyNameValue+"\t"+ObjectNameValue
        fT.close()
        #print rowT
        #print ObjectNameValue

###______
### Create a lit of files containing Individuals grouped by their concepts/classes from qClasses query
###______
def write_qClasses(qClasses, qIndividualsPerConcept):

    for rowI in qIndividualsPerConcept:
        if rowI:
            # print row
            IndividualName = str(rowI).split("#")[1]
            IndividualNameValue = IndividualName.split("'), rdflib.term")[0]
            print IndividualNameValue
            IndividualClass = str(rowI).split("#")[2]
            IndividualClassValue = IndividualClass.split("'))")[0]
            print IndividualClassValue
            # print claxzssNameValue
            fileC = open("/Users/ahmedhalioui/Documents/MonteCarloSimulation_Phyloflows/IndividualsPerConcept/" + IndividualClassValue + "_Individuals.txt",'a')
            fileC.write(IndividualNameValue + "\n")


###______
### Create a file containing all Individuals from qIndividualsAll query
###______
def write_qIndividualsAll(qIndividualsAll,file):
    # sorted(qIndividualsAll)
    for rowIA in  qIndividualsAll:
        with open(file,"a") as fI:
            print rowIA
            #IndividualNameAllValue=IndividualNameAll.split("')")[0]
            IndividualNameAll=str(rowIA[0]).split("#")[1]
            fI.write(IndividualNameAll+"\n")
        fI.close()

###______
### Creates Triplets.txt, Individuals.txt and IndividualsPerConcept files
###______
def queryOntology():

    # Define the graph g
    g = rdflib.Graph()
    # load the ontology on the graph g
    g.load("/Users/ahmedhalioui/Documents/MonteCarloSimulation_Phyloflows/phylOntology_v51_small_final.rdf")

    #QUERY INDIVIDUALS WITH THEIR CONCEPTS (CLASSES)

    qIndividualsPerConcept = """SELECT distinct ?subject ?class
    WHERE { ?subject a ?object .
        filter ( contains(str(?object),"NamedIndividual" )) .
        ?subject rdf:type ?type.
  		?type rdfs:subClassOf* ?class.
        filter NOT EXISTS {
            filter ( contains(str(?class),"NamedIndividual")) .
        }.
        filter ( str(?class)="http://www.co-ode.org/ontologies/ont.owl#DataType" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#DataBase" ||
        str(?class)="http://www.co-ode.org/ontologies/ont.owl#DataCollectionProgram" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#SequenceAlignmentProgram" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#ModelSelectionProgram" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#Models" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#PhylogeneticInferenceProgram" ||
        str(?class)="http://www.co-ode.org/ontologies/ont.owl#Method" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#HypothesisValidationProgram" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#bootstraps" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#TreeAnalysisProgram" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#TreeVisualizationProgram").
     }"""
    #QUERY ALL DISTINCT INDIVIDUALS  in G
    qIndividualsAll = """SELECT distinct ?subject
    WHERE { ?subject a ?object .
        filter ( contains(str(?object),"NamedIndividual" )) .
        ?subject rdf:type ?type.
  		?type rdfs:subClassOf* ?class.
        filter NOT EXISTS {
            filter ( contains(str(?class),"NamedIndividual")) .
        }.
        filter ( str(?class)="http://www.co-ode.org/ontologies/ont.owl#DataType" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#DataBase" ||
        str(?class)="http://www.co-ode.org/ontologies/ont.owl#DataCollectionProgram" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#SequenceAlignmentProgram" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#ModelSelectionProgram" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#Models" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#PhylogeneticInferenceProgram" ||
        str(?class)="http://www.co-ode.org/ontologies/ont.owl#Method" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#HypothesisValidationProgram" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#bootstraps" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#TreeAnalysisProgram" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#TreeVisualizationProgram").
     }"""
    #QUERY ALL DISTINCT CLASSES in G
    qClasses = """SELECT distinct ?class
    WHERE { ?subject a ?object .
        filter ( contains(str(?object),"NamedIndividual" )) .
        ?subject rdf:type ?type.
  		?type rdfs:subClassOf* ?class.
        filter NOT EXISTS {
            filter ( contains(str(?class),"NamedIndividual")) .
        }.
        filter ( str(?class)="http://www.co-ode.org/ontologies/ont.owl#DataType" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#DataBase" ||
        str(?class)="http://www.co-ode.org/ontologies/ont.owl#DataCollectionProgram" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#SequenceAlignmentProgram" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#ModelSelectionProgram" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#Models" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#PhylogeneticInferenceProgram" ||
        str(?class)="http://www.co-ode.org/ontologies/ont.owl#Method" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#HypothesisValidationProgram" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#bootstraps" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#TreeAnalysisProgram" || str(?class)="http://www.co-ode.org/ontologies/ont.owl#TreeVisualizationProgram").
     }"""
    #QUERY ALL LINKS in G
    qTriplets = """SELECT distinct ?subject ?property ?object
	WHERE { ?subject ?property ?object .
		?subject a ?x .
		?object a ?y .
		?property a owl:ObjectProperty.
	}"""

    ### Create lists of Individuals grouped by their concepts/classes from qClasses query
    write_qClasses(g.query(qClasses), g.query(qIndividualsPerConcept))

    ### Create a list of all Individuals from qIndividualsAll query
    fileIndividuals = "/Users/ahmedhalioui/Documents/MonteCarloSimulation_Phyloflows/IndividualsAll/Individuals.txt"
    write_qIndividualsAll(g.query(qIndividualsAll), fileIndividuals)

    ### Create a list of Triplets from qTriplets query
    Triplets="/Users/ahmedhalioui/Documents/MonteCarloSimulation_Phyloflows/Triplets/Triplets.txt"
    write_qTriplets(g.query(qTriplets), Triplets)



def create_uniquelst(i, uniquelst, emptylst, sequenceMonteCarlo):
    for elemen in sequenceMonteCarlo:
        if sequenceMonteCarlo.count(elemen)>1:
            emptylst.append((i,elemen))
            uniquelst.append(elemen)
        i=i+1

###______
###
###______
def createModifySequnceMonteCarlo(uniquelist, sequenceMonteCarlo):
#for each element in the list uniquelist: the list of tuples (duplicantPosition, duplicantValue)
    PosProbaDelete = []
    randomPosToReplace= []
    Deletes=[]
    finalDeletes=[]
    i=0
    for ele in sequenceMonteCarlo:
        for toDelete in uniquelist:
            if ele == toDelete:
                PosProbaDelete.append((i,toDelete))
        i=i+1
    #print PosProbaDelete

    for eli in uniquelist:
        for iter in PosProbaDelete:
            if eli  == iter[1]:
                Deletes.append(iter[0])

    #print "what delete? : %s" %Deletes

    choosefinalDeletes = random.choice(xrange(len(Deletes)))
    #print "How many should we delete? : %s" %choosefinalDeletes

    for x in xrange(choosefinalDeletes):
        finalDeletes.append(random.choice(Deletes))
    #print "to delete: %s" %finalDeletes

    for toD in finalDeletes:
        value=replaceIndividual(sequenceMonteCarlo[toD],toD)
        sequenceMonteCarlo[value[0]]=value[1]

    #print "New sequenceMonteCarlo is: %s after deletion of random duplications: " %sequenceMonteCarlo
    return sequenceMonteCarlo


def replaceIndividual(ElemInd,toD):
    IndividualsPerConceptDir="/Users/ahmedhalioui/Documents/MonteCarloSimulation_Phyloflows/IndividualsPerConcept/"

    #concept=ElemInd[0]

    for fname in os.listdir(IndividualsPerConceptDir):
            if not fname==".DS_Store":    # make sure it's a file, not a directory entry

                with open(IndividualsPerConceptDir+fname) as f:
                    wft = f.readlines()
                f.close()
                wft = [ w.rstrip() for w in wft ]

                with open(IndividualsPerConceptDir+fname) as f:
                    for line in f:       # process line by line
                        if ElemInd[1] == line.rstrip('\n'):
                            newI=random.choice(wft)
                            break
    val=(toD,(ElemInd[0],newI))

    return val

###_____
### Creates AllGenSequences.xml : a simulation of sequences with assuraing duplicants and sequences of links in a sequences
###______
def simulateSequences():

    # ThresholdNIndividuals=10
    # ThresholdNIndividuals=20
    # ThresholdNIndividuals=30
    # ThresholdNIndividuals=50
    # ThresholdNIndividuals=50
    # ThresholdNProp=10
    # ThresholdNProp=20
    # ThresholdNProp=30
    # ThresholdNProp=50
    # ThresholdNProp=100

    #L contains Fibonacci numbers
    L=[]
    for k in range(3,10):
        L.append(fib(k))
    print L

    #for each sequence length of fibonacci number (4 is the number of length we want to generate from the fibonacci list 7-3 [3,5,8,13] ~ [6,10,16,26] )
    filenamea = "/Users/ahmedhalioui/Documents/MonteCarloSimulation_Phyloflows/Gensequences/Stratified_GenSequences.xml"

    # filenamea="/Users/ahmedhalioui/Documents/MonteCarloSimulation_Phyloflows/Gensequences/ThresholdNIndividuals/%s_ThresholdNIndividuals_GenSequences.xml" %(ThresholdNIndividuals)
    # filenamea="/Users/ahmedhalioui/Documents/MonteCarloSimulation_Phyloflows/Gensequences/ThresholdNClasses/%s_ThresholdNClasses_GenSequences.xml" %(ThresholdNClasses)
    # filenamea="/Users/ahmedhalioui/Documents/MonteCarloSimulation_Phyloflows/Gensequences/ThresholdNProp/%s_ThresholdNProp_GenSequences.xml" %(ThresholdNProp)
    # filenamea="/Users/ahmedhalioui/Documents/MonteCarloSimulation_Phyloflows/Gensequences/ThresholdNDuplicant/%s_ThresholdNDuplicant_GenSequences.xml" %(ThresholdNDuplicant)
    with open(filenamea, "wa") as fa:

        # <?xml version="1.0" encoding="utf-8"?><sequences>
        fa.write('<?xml version="1.0" encoding="utf-8"?>\n')
        fa.write('<sequences>\r\r')
        mean_prop = 0
        mean_ind = 0
        SumProp = 0
        SumInd = 0
        count = 0
        # create 100 sequences in AllGenSequences
        for hundredSeq in xrange(200):
            for items in xrange(5):

                # deck is a list of sampled wordsFromTripletstxt of a fibonacci number of triplets
                (deck, sequenceMonteCarloLength) = new_deck(L[items])
                # print deck

                # sequenceMonteCarlo is the sequence we want to generate which contains exactly len(deck)*2
                sequenceMonteCarlo = []
                # for i in xrange(sequenceMonteCarloLength):
                #     sequenceMonteCarlo.append(i)
                # print "sequenceMonteCarlo: %s" %sequenceMonteCarlo
                #
                # mylistPositions is list of positions to in sequenceMonteCarlo
                mylistPositions = list(xrange(sequenceMonteCarloLength))
                # print "mylistPositions: %s" %mylistPositions
                #
                # generate and polpulte the sequenceMonteCarlo
                # print "====================================================================="
                # print "here begins the reordering with the new object,concept structure: ..."
                # print "====================================================================="
                sequenceMonteCarlo = placeElementsInSequence(deck, sequenceMonteCarlo, mylistPositions)

                # sequenceMonteCarloNonDuplicatesList is a list of UNIQUE elements
                sequenceMonteCarloNonDuplicatesList = list(set(sequenceMonteCarlo))
                # print sequenceMonteCarloNonDuplicatesList
                # print sequenceMonteCarlo

                # check if there are duplicants
                if not (len(sequenceMonteCarloNonDuplicatesList) == len(sequenceMonteCarlo)):
                    i = 0
                    uniquelst = []
                    emptylst = []

                    # print sequenceMonteCarlo

                    # construct a list (uniquelst) of tuples (emptylst) of duplicants: a tuple contains the duplicant position and value
                    create_uniquelst(i, uniquelst, emptylst, sequenceMonteCarlo)

                    # Caculate the number of duplicants
                    numberDup = float(len(sequenceMonteCarlo) - len(sequenceMonteCarloNonDuplicatesList)) / len(
                        sequenceMonteCarlo) * 100
                    # print "Purcentage of duplicants: %.2f" % numberDup
                    # print "Number of duplicants: %d" % (len(sequenceMonteCarlo)-len(sequenceMonteCarloNonDuplicatesList))

                    # Replace duplicants
                    uniquelist = list(set(uniquelst))
                    # print uniquelist
                    # print emptylst

                    posToDel = []
                    # # create uniquelist the list of tuples (duplicantPosition, duplicantValue)
                    newSequenceMonteCarlo = createModifySequnceMonteCarlo(uniquelist, sequenceMonteCarlo)
                    # print newSequenceMonteCarlo

                    newSeqMonteCarlo = []
                    for eachItem in newSequenceMonteCarlo:
                        newSeqMonteCarlo.append(eachItem[1])

                    SeqMonteCarlo = []
                    for idx, val in enumerate(newSeqMonteCarlo):
                        curr = val
                        if idx + 1 != len(newSeqMonteCarlo):
                            next = newSeqMonteCarlo[idx + 1]
                            if curr != next:
                                SeqMonteCarlo.append(curr)
                        else:
                            SeqMonteCarlo.append(curr)
                    print SeqMonteCarlo

                    NProp = calculaNumbProperties(SeqMonteCarlo)
                    NClasses = calculaNumbsClasses(sequenceMonteCarlo)
                    NIndividuals = calculaNumbsIndividuals(SeqMonteCarlo)

                    # NDuplicants = calculaNumbsDuplicants(sequenceMonteCarlo)

                    print "******************************************** STATISTICS: ********************************************"
                    print "Number of items in sequence: %d" % NIndividuals
                    print "Number of classes in sequence: %d" % NClasses
                    print "Number of links in sequence: %d" % NProp
                    # print "Purcentage of duplicants in sequence: %.2f" %NDuplicants
                    print "*****************************************************************************************************"
                    generateNIndividuals(fa, 0, float(NIndividuals), SeqMonteCarlo)

                    count = count + 1
                    SumProp = SumProp + NProp
                    SumInd = SumInd + NIndividuals
                    mean_prop = SumProp / count
                    mean_ind = SumInd / count
                    print "# of workflows: %f" % count
                    print "AVG number of individuals: %f" % mean_ind
                    print "AVG Number of links: %f" % mean_prop

                    if count == 1500:
                        break

                        # IF there is a threshold !
                        # if res :
                        #     count=count+1
                        #     SumProp = SumProp + NProp
                        #     SumInd = SumInd + NIndividuals
                        # print "trouve %f" %(count)
                        # if count!=0 :
                        #     mean_prop = SumProp / count
                        #     mean_ind = SumInd / count
                        #     print "AVG number of individuals: %f" % mean_ind
                        #     print "AVG Number of links: %f" % mean_prop

        fa.write('</sequences>')

    fa.close()

def generateNIndividuals(fa,threshold,NIndividuals,newSeqsequenceMonteCarlo):

    # if NIndividuals >= threshold :
    # if NIndividuals >= 31 and NIndividuals <= threshold:
    print newSeqsequenceMonteCarlo
    #print "Threshold in sequence: %d" %NIndividuals
    # Write in AllGenSequences.xml the generated sequence -- SEQUENCE HEADER
    fa.write('\t<sequence>')

    # for each individual in the generated sequenceMonteCarlo write the element -- INDIVIDUAL HEADER & VALUE
    for ind in newSeqsequenceMonteCarlo:
        fa.write('\r\t\t<individual>')
        fa.write('%s' % ind)
        fa.write('</individual>\r')
    fa.write('\t</sequence>\r\r')
        # return True


def calculaNumbsDuplicants(seqMonteCarlo):
    seqMonteCarloNonDuplicatesList= list(set(seqMonteCarlo))
    return float(len(seqMonteCarlo)-len(seqMonteCarloNonDuplicatesList))/len(seqMonteCarlo)*100

def calculaNumbsIndividuals(seqMonteCarlo):
    return len(seqMonteCarlo)

def calculaNumbsClasses(seqMonteCarlo):
    classes = set()
    for eachElem in seqMonteCarlo :
        classes.add(eachElem[0])
    print len(classes)
    return len(classes)

def calculaNumbProperties(seqMonteCarlo):
    numProperties=0
    TuplesFile="/Users/ahmedhalioui/Documents/MonteCarloSimulation_Phyloflows/Triplets/Triplets.txt"
    for subjectElem in seqMonteCarlo:
        for objectElem in seqMonteCarlo:
            #tupleLine= re.escape(subjectElem) + r"\t.*\t"+ re.escape(objectElem)
            tupleLine = re.compile( "%s\t.*\t%s" %(subjectElem,objectElem) )

            with open(TuplesFile) as f:
                for line in f:       # process line by line
                    # print line.rstrip('\n')
                    # print str(tupleLine)
                    # print tupleLine.match(line.rstrip('\n'))
                    if tupleLine.match(line.rstrip('\n')):
                        #print line.rstrip('\n')
                        numProperties=numProperties+1
    return numProperties

def flatten(lst):
	return sum( ([x] if not isinstance(x, list) else flatten(x)
		     for x in lst), [] )

###______
###
###______
def placeElementsInSequence(deck, sequenceMonteCarlo, mylistPositions):

    #################################################### SOLUTION 1 ####################################################

    listToComplete = chooseRandomPositions(deck,mylistPositions)
    # print deck
    # print mylistPositions
    # print listToComplete

    for element in listToComplete:
        valueFromDeck = random.choice(deck[element])
        sequenceMonteCarlo.append((element,valueFromDeck))
    #print sequenceMonteCarlo
    return sequenceMonteCarlo

    ####################################################################################################################

    #################################################### SOLUTION 2 ####################################################
    # # choose a random value for the mandatory1 which is deck[0] (DataType_Individuals)
    # mandatory1=random.choice(deck[0])
    # # place it in the first positon
    # mylistPositions.remove(0)
    # sequenceMonteCarlo[0]=mandatory1
    #
    # # choose a random value for the mandatory2 which is deck[7] (PhylogeneticInferenceProgram_Individuals)
    # mandatory2=random.choice(deck[7])
    # # choose a random position for the mandatory2 which is deck[7] (PhylogeneticInferenceProgram_Individuals)
    # randomPosition2 = random.choice(mylistPositions)
    #
    # # place it in the sequenceMonteCarlo
    # sequenceMonteCarlo[randomPosition2]=mandatory2
    # mylistPositions.remove(randomPosition2)
    #
    # print sequenceMonteCarlo
    # print mylistPositions
    #
    # i=0
    # serializeDLeftDeck=[]
    # for serializeLeftDeck in deck:
    #     serializeDLeftDeck.append(serializeLeftDeck)
    #     if i>6:break
    #     i=i+1
    #
    # j=9
    # serializeDRightDeck=[]
    # for serializeRightDeck in reversed(deck):
    #     #tmp2=flatten(serializeRightDeck)
    #     serializeDRightDeck.append(serializeRightDeck)
    #     if j<8:break
    #     j=j-1
    #
    # print "serializeDLeftDeck: %s" %serializeDLeftDeck
    # print "serializeDRightDeck: %s" %serializeDRightDeck
    #
    # for restOf in mylistPositions:
    #     if restOf<randomPosition2:
    #         for ii in serializeDLeftDeck:
    #             if ii != []:
    #                 randomRestValueL=random.choice(ii)
    #                 serializeDLeftDeck.remove(ii)
    #                 break
    #
    #         print randomRestValueL
    #         sequenceMonteCarlo[restOf] = randomRestValueL
    #
    #     else:
    #         for jj in serializeDLeftDeck:
    #             if ii != []:
    #                 randomRestValueR=random.choice(jj)
    #                 serializeDLeftDeck.remove()
    #                 break
    #
    #         print randomRestValueR
    #         sequenceMonteCarlo[restOf] = randomRestValueR
    #
    # tmpsequenceMonteCarlo = flatten(sequenceMonteCarlo)
    # print "sequenceMonteCarlo :): %s" %(tmpsequenceMonteCarlo)

    ####################################################################################################################

def nonEmptyChoice(iList,iPos):
    chosenPos=random.choice(xrange(len(iList)))

    if iList[chosenPos] == []:
    #if iList[chosenPos] == [] or chosenPos in iPos:
        return nonEmptyChoice(iList,iPos)
    else: return chosenPos

def chooseRandomPositions(deck,mylistPositions):
    chosenPos=[]
    #print deck
    for i in xrange(len(mylistPositions)):
        chosenValue= nonEmptyChoice(deck,chosenPos)
        chosenPos.append(chosenValue)
    if not (0 in chosenPos and 7 in chosenPos):
        return chooseRandomPositions(deck,mylistPositions)
    else: return sorted(chosenPos)
###______
###
###______

###______
### create 10 files of 10*10 sequences each from wordsFromAllGenSequencesxml
###______
# def create10Files100Sequences():
#
#     for item in xrange(10):
#         filenamei="/Users/ahmedhalioui/Documents/MonteCarloSimulation_Phyloflows/"+`item+1`+"_test.xml"
#         with open(filenamei,"a") as fi:
#             fi.write('<?xml version="1.0" encoding="utf-8"?>\n<sequences>\n')
#
#             #generate 10*10 sequences from wordsFromAllGenSequencesxml
#             for i in xrange(10):
#                 meanLength, newSequences = new_sequences(10)
#                 sequencetoWrite = str(newSequences).replace('[\'','')
#                 sequencetoWrite = str(sequencetoWrite).replace('\']','')
#                 sequencetoWrite = str(sequencetoWrite).replace('\\t','\t')
#                 sequencetoWrite = str(sequencetoWrite).replace('\', \'','\n')
#                 #print "Mean length is %d" %(meanLength)
#                 #print sequencetoWrite
#                 fi.write(sequencetoWrite)
#                 fi.write('\n')
#             fi.write('</sequences>')
#             fi.close()


############ MAIN FUNCTION
# queryOntology()
simulateSequences()
# create10Files100Sequences()
############ MAIN FUNCTION