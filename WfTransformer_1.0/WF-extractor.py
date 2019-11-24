#!/usr/bin/env python
"""This script generates WFMiner workflows from the annotated articles (GATE display format)."""

__author__ = 'ahmedhalioui'
__email__ = "ahlioui@gmail.com"
__date__ = "2019-10-14"

import copy
import xmltodict
import argparse
import ast
import xml.etree.cElementTree as ET
from xml.dom import minidom

parser = argparse.ArgumentParser(description='Welcome to Workflow Extractor from GATE XML files.')
parser.add_argument('--input', dest='input', help='The path to the input file, e.g., GATE_output.xml')
parser.add_argument('--output', dest='output', help='path to the output file, e.g., WF_output.xml')

args = parser.parse_args()
# print(args.input)
# print(args.output)


def chunks(l, n):
    """Yield successive n-sized chunks from l."""
    for i in range(0, len(l), n):
        yield l[i:i + n]


if __name__ == "__main__":
    WFs = []

    with open(args.input) as fd:
        doc = xmltodict.parse(fd.read())

    for articlesKey, articles in doc['articles'].items():
        for article in articles:
            # GATE DISPLAY VERSION
            # Transactions
            i = 0
            mapping_items = {}
            # WF_transactions = []
            transactions = article['#text'].split(' >,')[0][3:].split('}, {')
            WF_transactions = []
            for transaction in transactions:
                items = transaction.strip(' }{ ').split(', ')
                item_ids = []
                for item in items:
                    mapping_items[item.split("|")[1]] = i
                    item_id = item.split("|")[0]
                    item_ids.append(item_id)
                    i += 1
                WF_transactions.append(item_ids)


            # GATE INLINE VERSION
            # links_string = article['Workflow']['@list-workflow'].split(', {')[1].replace(' }]', '')

            # GATE DISPLAY VERSION
            links_string = article['#text'].split(' >,')[1][3:].replace(" )] }", "")
            links = links_string.strip('][ ').split(', ')
            tuples = []
            for tuple_string in links:
                tuple = tuple_string.strip(' )( ')
                tuples.append(tuple)
            tuples_copy = copy.deepcopy(tuples)
            triplets = list(chunks(tuples_copy, 3))

            #
            WF_links = []
            for triplet in triplets:
                if triplet[0] != '}':
                     #print(triplet)
                    domain = str(mapping_items[triplet[0].split("|")[1]])
                    type = triplet[1]
                    my_range = str(mapping_items[triplet[2].split("|")[1]])
                    WF_links.append([domain, type, my_range])
                    # print(domain, type, Myrange)

            # WF_links.append(triplets)
            dicWF = {"file": article["@file"], "pmcid": article["@pmcid"],
                     "transactions": WF_transactions, "triplets": WF_links}
            # print(dicWF)
            WFs.append(dicWF)

    # Write the XML tree
    wfs = ET.Element("Workflows")

    for workflow in WFs:
        # print(workflow)
        wf = ET.SubElement(wfs, "Workflow")
        wf.set("file", workflow["file"])
        wf.set("pmcid", workflow["pmcid"])
        for transaction in workflow["transactions"]:
            if transaction[0] != '':
                tr = ET.SubElement(wf, "Transaction")
                for item in transaction:
                    it = ET.SubElement(tr, "Item")
                    it.text = item

        for triplet in workflow["triplets"]:
            if triplet[0] != '' and triplet[0] != '}':
                li = ET.SubElement(wf, "Triplet")
                subject = ET.SubElement(li, "Subject").text = triplet[0]
                link = ET.SubElement(li, "Link").text = triplet[1]
                object = ET.SubElement(li, "Object").text = triplet[2]

    # Pretty Writer
    xmlstr = minidom.parseString(ET.tostring(wfs)).toprettyxml(indent="   ")
    with open(args.output, "w", encoding="UTF-8") as f:
        f.write(xmlstr)
