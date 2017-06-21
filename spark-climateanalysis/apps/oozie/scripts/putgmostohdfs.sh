if $(hadoop fs -test -f /root/gsom-latest.tar.gz); then hadoop fs -rm -skipTrash /root/gsom-latest.tar.gz; fi;
hadoop fs -put /tmp/gsom-latest.tar.gz /root