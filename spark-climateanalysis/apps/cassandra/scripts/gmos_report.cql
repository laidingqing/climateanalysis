DROP KEYSPACE IF EXISTS sparkgmos;
CREATE KEYSPACE sparkgmos WITH REPLICATION = {'class': 'SimpleStrategy',  'replication_factor':1};
USE sparkgmos; DROP TABLE IF EXISTS gmos_report;
USE sparkgmos; CREATE TABLE gmos_report(year text, latitude_left bigint, latitude_right bigint, longitude_left bigint, longitude_right bigint, season text, average_tempeture double, available_tavg bigint, PRIMARY KEY(year, latitude_left, latitude_right, longitude_left, longitude_right, season));