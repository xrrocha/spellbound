import re
from NorvigSpellingCorrector import candidates

lines = [line.rstrip('\n') for line in open('spanish-name-typos.tsv')]
for line in lines:
    typo, word = re.split(r'\t+', line)
    for candidate in candidates(typo):
        print(typo + "\t" + candidate)
