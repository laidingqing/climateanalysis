if $(hadoop fs -test -d /root1/gmos/income); then hadoop fs -rm -R -skipTrash /root1/gmos/income; fi;
if $(hadoop fs -test -d /root1/gmos/etl); then hadoop fs -rm -R -skipTrash /root1/gmos/etl; fi