import sys
sys.path.append('../')
import database
import pprint
from queryconstructor import QueryConstructor
from plotconstructor import *

filename_prefix = "ping_over_country"
 
filename=filename_prefix

qc = QueryConstructor()
qc.setGroupOrderSelectBy("device","networkcountry")
qc.addSelectMedian("ping","avg")
qc.addSelectPercentile("ping","avg",25)
qc.addSelectPercentile("ping","avg",75)
qc.addSelectField("count(*)")
qc.addWhereNotEqualsString("network","networktype","")
qc.addWhereNotEqualsString("device","networkcountry","sa")
qc.addWhereEqualsString("network","networktype","UMTS")

qc.applyMobileClauses()
qc.applyLatencyClauses("www.google.com")
#qc.addWhereEqualsString("device","networkname",'AT&T')

result = database.query(qc.toString())

pprint.pprint(result)

plot = SimpleBarPlot()
plot.addMapping(MappingWithBounds(result,0,1,2,3,"Type"))
plot.setYLabel("ms")
plot.setXLabel("Device Design")
plot.setTitle("3G RTT latency for different countries with UMTS") 
#plot.setLegend("ms")

plot.construct()

plot.save(filename)
plot.show()
