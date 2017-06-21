if [ -f "/tmp/gsom-latest.tar.gz" ]; then rm -f "/tmp/gsom-latest.tar.gz"; fi;
wget https://www.ncei.noaa.gov/data/gsom/archive/gsom-latest.tar.gz  -O /tmp/gsom-latest.tar.gz