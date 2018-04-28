#!/bin/bash
# Add Citus repository for package manager
curl https://install.citusdata.com/community/deb.sh | sudo bash
# install the server and initialize db
sudo apt-get -y install postgresql-10-citus-7.3
# preload citus extension
sudo pg_conftool 10 main set shared_preload_libraries citu
# Allow access from outside the server
sudo pg_conftool 10 main set listen_addresses '*'
###############################
# EDIT THIS
sudo vi /etc/postgresql/10/main/pg_hba.conf
###############################
# start the db server
sudo service postgresql restart
# and make it start automatically when computer does
sudo update-rc.d postgresql enable
# add the citus extension
sudo -i -u postgres psql -c "CREATE EXTENSION citus;"
# May need to change ports
sudo -i -u postgres psql -c "SELECT * from master_add_node('worker-101', 5432);"
sudo -i -u postgres psql -c "SELECT * from master_add_node('worker-102', 5432);"
# verify
sudo -i -u postgres psql -c "SELECT * FROM master_get_active_worker_nodes();"
# Start Citus
sudo -i -u postgres psql
