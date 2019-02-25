import csv

with open('long_piezo.csv','r') as f:
    with open('long_piezo_filtered.csv','w') as g:
        reader = csv.reader(f)
        writer = csv.writer(g)
        heads = next(reader)
        writer.writerow(heads)
        count = 0
        for line in reader:
            time = float(line[0])
            time_hours = time / 60.0
            time_days = time_hours / 24.0
            if time_days.is_integer():
                writer.writerow(line)
        
        