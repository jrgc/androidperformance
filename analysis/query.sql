select connectiontype='Wifi',date_part('hour', devicetime),cast(floor(date_part('minute', devicetime)/10) as int),count(*) from measurement,network where measurement.measurementid = network.measurementid  group by connectiontype='Wifi',date_part('hour', devicetime),cast(floor(date_part('minute', devicetime)/10) as int);