#!/usr/bin/env python
"""This script generates WFMiner workflows from the annotated articles (GATE display format)."""

__author__ = 'ahmedhalioui'
__email__ = "ahlioui@gmail.com"
__date__ = "2019-11-03"

import os
import re
import argparse
import xml.etree.ElementTree as ET

parser = argparse.ArgumentParser(description='Welcome to Workflow Extractor from GATE XML files.')
parser.add_argument('--mappingFile', dest='mappingFile', help='The path file where the rename information is, e.g., WF_2018_2019.xml')
parser.add_argument('--directory', dest='directory', help='path to the output directory, e.g., annotated_2018_2019')

args = parser.parse_args()
# print(args.mappingFile)
# print(args.directory)


def getNewNames(file):
    dicNames = {}
    tree = ET.parse(file)
    root = tree.getroot()

    for article in root:
        pmcid = article.attrib['pmcid'].split(', ')[0]
        dicNames[pmcid] = article.attrib['file']

    return dicNames


def getPreviousNames(inputDir):
    previousNames = {}
    for filename in os.listdir(inputDir):
        if filename != '.DS_Store':
            filepath = os.path.join(inputDir, filename)
            print(filename)
            try:
                tree = ET.parse(filepath)
                article = tree.getroot()
                previousNames[filename] = article.attrib['pmc']
            except ET.ParseError as e:
                # print(str(e)+", in the {} file".format(filename))
                fp = open(filepath)
                for i, line in enumerate(fp):
                    if i == 1:
                        pmcid = re.search(r'.*pmc=&quot;(\d+)&quot;.*', line).group(1)
                        previousNames[filename] = pmcid
                        break
                fp.close()
                continue

    return previousNames


def rename(directory, previousNames, newNames):
    for filename in os.listdir(directory):
        if filename != '.DS_Store':
            if filename in previousNames.keys():
                pmcid = previousNames[filename]
                src = os.path.join(directory, filename)
                print(filename, pmcid)
                if pmcid in newNames.keys():
                    print("{} -> {}".format(filename, newNames[pmcid]))
                    dst = os.path.join(directory, newNames[pmcid])
                    os.rename(src, dst)
                else:  # there is no workflow in the file
                    print("{} is removed".format(src))
                    os.remove(src)
            else:
                print('Error in {}'.format(filename))


if __name__ == "__main__":
    newNames = getNewNames(args.mappingFile)
    # print(newNames)
    previousNames = getPreviousNames(args.directory)
    # print(previousNames)
    rename(args.directory, previousNames, newNames)
