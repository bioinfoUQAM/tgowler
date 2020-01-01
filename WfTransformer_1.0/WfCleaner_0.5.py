__author__ = 'ahmedhalioui'

import os
import re

rootdir ='/Users/ahmedhalioui/Documents/Oxygen Projects/results_Gold-like'
newf='/Users/ahmedhalioui/Documents/Oxygen Projects/extracted_gold-like_wf.xml'

# 1. CLEAN WHITE INDIVIDUALS

fw=open(newf,'a')
fw.write('<?xml version="1.0" encoding="utf-8"?>\n')
fw.write('<Sequences>\n')
for root, dirs, files in os.walk(rootdir):
    print root
    for file in files:

        if not '_seq_seq.xml' in str(file) and '.xml' in str(file):
            f=open(os.path.join(root, file),'r')
            lines=f.readlines()
            print (lines)
            if (len(lines)>1):
                # fw.write('<Sequence>\n')
                for line in lines:
                    fw.write('\n')
                    if not '<?xml version="1.0" encoding="utf-8"?>' in line and not 'MathML"/>' in line:
                        print line
                        fw.write(line)
fw.write('</Sequences>\n')

# 2. CLEAN WHITE SPACES EVERY WHERE

newff=open('/Users/ahmedhalioui/Documents/Oxygen Projects/extracted_gold-like_wf_clean.xml','a')
regex = re.compile(r"\n+", re.IGNORECASE)
regex1 = re.compile(r".*MathML\"..\n", re.IGNORECASE)
regex2 = re.compile(r"<Sequence>\n</Sequence>", re.IGNORECASE)
regex3 = re.compile(r" xmlns:xlink=.+MathML\"", re.IGNORECASE)

with open(newf, 'r') as myfile:
    data=myfile.read()
    lines1 = re.sub(regex,"\n", data)
    lines2 = re.sub(regex1,"", lines1)
    lines3 = re.sub(regex2,"", lines2)
    lines4 = re.sub(regex3,"", lines3)
    lines = re.sub(regex,"\n", lines4)
newff.write(lines)