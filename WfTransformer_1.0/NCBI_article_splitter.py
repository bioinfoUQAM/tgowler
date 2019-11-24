#!/usr/bin/env python
"""This script parses the extracted texts (from NCBI - after applying the XSLT transformation) and splits the content into sperated article files."""

__author__ = 'ahmedhalioui'
__email__ = "ahlioui@gmail.com"
__date__ = "2019-10-26"

import re
import argparse


splittingtxt = '</article>'
filenameformat = 'seg-#.xml'

parser = argparse.ArgumentParser(description='Welcome to Workflow Extractor from GATE XML files.')
parser.add_argument('--inputFile', dest='input', help='The path to the input file (PMC texts/subtexts), e.g. pmc_result-extracted.xml')
parser.add_argument('--outputDir', dest='output', help='The path to the output directory where the files are saved (make sure that the directory exists!)')

args = parser.parse_args()


def newfout(filenum):
    filename = filenameformat.replace('#', str(filenum))
    fout = open(args.output + filename, "w+")
    return fout


if __name__ == "__main__":
    file = open(args.input)
    lines = file.readlines()

    filenum = 1
    fout = newfout(filenum)

    for line in lines:
        # Stop loop: create a new file
        if splittingtxt in line:
            fout.write(splittingtxt)
            fout.close()
            filenum += 1
            fout = newfout(filenum)
        # Write the line
        else:
            match1 = re.search(r'.*xml version=.*', line)
            match2 = re.search(r'<.articles>', line)
            match3 = re.search(r'<articles>', line)
            match4 = re.search(r'^$', line)
            if not match1 and not match2 and not match3 and not match4:
                fout.write(line)
    fout.close()
