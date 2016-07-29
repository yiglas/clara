#  This script should do all the preparation for your project to run, such as downloading any dependencies and compiling if necessary

# update all packages.
sudo apt-get update

# install git : looking at the VM given to me git was already installed.
# sudo apt-get install git

# download the web service code from git : yet if you are running this script, you've already 
# - downloaded the code from git.
# git clone https://github.com/yiglas/clara-challenge-backend.git giphy-search

# install the latest version of java.
sudo apt-get install openjdk-8-jre-headless

# install leiningen.
sudo curl https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein -o /usr/local/bin/lein
# set the permissions for the lein script.
sudo chmod a+x /usr/local/bin/lein

# execute the self-install function of lein.
/usr/local/bin/lein self-install

# make sure to download all the dependencies for the app
lein deps

# compile the web services into a single file.
lein uberjar

# clojure needs two packages
sudo apt-get install nginx supervisor

# create a place to expose the clojure restful service
sudo mkdir -p /var/www/giphy-search/app /var/www/logs

# move the clojure application into the newly created folders
sudo cp ~/giphy-search/target/giphy-search-0.1.0-SNAPSHOT-standalone.jar /var/www/giphy-search/app/

# the application use www-data, make sure to give it permissions
sudo chown -R www-data /var/www/giphy-search/

cd /var/www/giphy-search

# create a symlink without the version
sudo ln -s giphy-search-0.1.0-SNAPSHOT-standalone.jar giphy-search.jar

# move the conf file into supervisor
sudo cp ~/giphy-search/giphy-serach.conf /etc/supervisor/conf.d/giphy-search.conf

