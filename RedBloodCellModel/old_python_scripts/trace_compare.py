import sys
if __name__ == '__main__':
	file1 = sys.argv[1]
	file2 = sys.argv[2]
	problems = []
	found = False
	tol = 1e-9
	with open(file1,'r') as f1:
		with open(file2,'r') as f2:
			heads1 = f1.readline().split()
			heads2 = f2.readline().split()
			for line1 in f1:
				line2 = f2.readline()
				tokens1 = line1.split()
				tokens2 = line2.split()
				for i,h in enumerate(heads1):
					pos2 = heads2.index(h)
					val1 = float(tokens1[i])
					val2 = float(tokens2[pos2])
					err = abs(val1-val2)
					if err > tol:
						problems.append(h)
						found = True
				if found:
					print tokens1[heads1.index("Time")],problems
					break

