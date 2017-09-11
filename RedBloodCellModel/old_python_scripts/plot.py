import sys
import numpy
import pylab as plt

if __name__ == '__main__':
	file1 = sys.argv[1]
	if len(sys.argv) > 3:
		file2 = sys.argv[2]
		species = sys.argv[3]
	else:
		species = sys.argv[2]
		file2 = None

	print species

	t1 = []
	s1 = []

	t2 = []
	s2 = []

	with open(file1,'r') as f:
		heads = f.readline()
		hs = heads.split(',')
		spos = hs.index(species)
		for line in f:
			sline = line.split(',')
			t1.append(float(sline[0]))
			s1.append(float(sline[spos]))
	if file2:
		with open(file2,'r') as f:
			heads = f.readline()
			hs = heads.split(',')
			spos = hs.index(species)
			for line in f:
				sline = line.split(',')
				t2.append(float(sline[0]))
				s2.append(float(sline[spos]))

	plt.figure()
	plt.plot(t1,s1,'.-')
	if file2:
		plt.plot(t2,s2)
	plt.show()