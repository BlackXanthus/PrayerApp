
#!/usr/bin/env python

import json


def cleanString(line):

	myString =""
	

	for word in line:
		if(word != ''):
			myString=myString+" "+word;


	myString = myString.replace("\t"," ")
	myString = myString.replace('MP',"")
	myString = myString.replace('EP',"")
	myString = myString.replace("NT","")
	myString = myString.replace("OT","")
	myString = myString.replace("\"","")
	myString = myString.replace("\u2013","-")
	myString = myString.replace("\u00e2\u20ac\u201c","")
	myString = myString.replace("\n","")
	myString = myString.strip(" ")
	
	return myString


filename = "CiW-YearOne.csv"
lectionaryArray = []

with open(filename) as f:
	content = f.readlines()


for line in content:
	line.strip()
	myLine = line.split(",")
	if(myLine[0]!=''):
		lectionaryArray.append(myLine)


season = ""
week = ""
day = ""
ep = False
mp = False
ignore = False
lectionary = {}

for line in lectionaryArray:

	if(line[0].startswith('DAILY') and line[1].startswith('EUCHARIST')):
			ignore = True

	if(line[0] == "ADVENT" or line[0] == "Epiphany" or line[0] == "LENT" or line[0]=="Easter" or line[0]=="EASTER" or line[0]=="PENTECOST" or line[0]=="TRINITY" or line[0]=="KINGDOM"):
		ignore = False
		season = line[0]
		if(season not in lectionary):
			print ("Make New Season:"+season)
			lectionary[season] = {}
			lectionary[season]['Name'] = line[0]

		week = line[1]
		if(week not in lectionary[season]):
			print("Make New Wweek:"+week)
			lectionary[season][week]= {}
			lectionary[season][week]['Name'] = line[1]
			mp=False
			ep=False


	if(line[1].startswith('Before') or line[1].startswith('BEFORE')):
		ignore = False

		season = line[1]+line[2]
		if(season not in lectionary):
			print ("Make New Season:"+season)
			lectionary[season] = {}
			lectionary[season]['Name'] = line[1]+" "+line[2]
		week = line[0]
		if(week not in lectionary[season]):
				print("Make New Week:"+week)
				lectionary[season][week]= {}
				lectionary[season][week]['Name'] = line[0]
				mp=False
				ep=False


	if((line[0] == 'Monday') and  not ignore):
		day = "Monday";
		if(day not in lectionary[season][week]):
			print("Make New Day:"+day)
			lectionary[season][week][day] = {}
			lectionary[season][week][day]['Name'] = day
	if((line[0] == 'Tuesday') and  not ignore):
		day = "Tuesday";
		if(day not in lectionary[season][week]):
			print("Make New Day:"+day)
			lectionary[season][week][day] = {}
			lectionary[season][week][day]['Name'] = day
	if((line[0] == 'Wednesday') and  not ignore):
		day = "Wednesday";
		if(day not in lectionary[season][week]):
			print("Make New Day:"+day)
			lectionary[season][week][day] = {}
			lectionary[season][week][day]['Name'] = day
	if((line[0] == 'Thursday') and  not ignore):
		day = "Thursday";
		if(day not in lectionary[season][week]):
			print("Make New Day:"+day)
			lectionary[season][week][day] = {}
			lectionary[season][week][day]['Name'] = day
	if((line[0] == 'Friday') and  not ignore):
		day = "Friday";
		if(day not in lectionary[season][week]):
			print("Make New Day:"+day)
			lectionary[season][week][day] = {}
			lectionary[season][week][day]['Name'] = day
	if((line[0] == 'Saturday') and  not ignore):
		day = "Saturday";
		if(day not in lectionary[season][week]):
			print("Make New Day:"+day)
			lectionary[season][week][day] = {}
			lectionary[season][week][day]['Name'] = day

	if(line[0].startswith('MP') or line[0].startswith('\"MP') and not ignore):
		lectionary[season][week][day]["MorningPrayer"] = {}
		lectionary[season][week][day]["MorningPrayer"]["Psalm"] = cleanString(line)
		mp = True;	
		ep = False;

	if(line[0].startswith('EP') or line[0].startswith('\"EP') and not ignore):
		lectionary[season][week][day]["EveningPrayer"] = {}
		lectionary[season][week][day]["EveningPrayer"]["Psalm"] = cleanString(line)
		ep = True;	
		mp = False;


	if(mp and not ignore) :
		if(line[0].startswith('\tOT')):
			lectionary[season][week][day]["MorningPrayer"]["OT"] = cleanString(line)
		if(line[0].startswith('\tNT')):
			lectionary[season][week][day]["MorningPrayer"]["NT"] = cleanString(line)
			mp=False;

	if(ep and not ignore) :
		if(line[0].startswith('\tOT')):
			lectionary[season][week][day]["EveningPrayer"]["OT"] = cleanString(line)
		if(line[0].startswith('\tNT')):
			lectionary[season][week][day]["EveningPrayer"]["NT"] = cleanString(line)
			ep=False;

	if(not ignore):
		print(line)

with open('lectionary-YearOne.json', 'w') as json_file:
	json.dump(lectionary,json_file,indent=4)

#print(lectionary)
	
