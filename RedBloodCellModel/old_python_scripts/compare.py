import sys
if __name__ == '__main__':
	f1 = sys.argv[1]
	f2 = sys.argv[2]
	line_no = int(sys.argv[3])
	dict1 = {}
	dict2 = {}
	line_pos = 0
	with open(f1,'r') as f:
		heads1 = f.readline().split()
		for line in f:
			if line_pos == line_no:
				sline = line.split()
				for i,h in enumerate(heads1):
					dict1[h] = float(sline[i])
			line_pos += 1


	line_pos = 0
	with open(f2,'r') as f:
		heads2 = f.readline().split()
		for line in f:
			if line_pos == line_no:
				sline = line.split()
				for i,h in enumerate(heads2):
					dict2[h] = float(sline[i])
			line_pos += 1
	
	for key in dict1:
		err = abs(dict1[key] - dict2[key])
		if err > 1e-6:
			print key,dict1[key],dict2[key],abs(dict1[key] - dict2[key])