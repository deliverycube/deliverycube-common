Delivery Cube Common Module
===================

The Delivery Cube Common Module contains utility classes and components useful for all for ATG projects.


# The Feature Toggles Project

The Feature Toggles module contains code and config for a Feature Toggle Service in ATG


## What Are Feature Toggles?

For a background on Feature Toggles, please read the article by Martin Fowler at http://martinfowler.com/bliki/FeatureToggle.html


## Get the Code

Clone the repository with

    git clone https://github.com/vihung/deliverycube-common.git
    
or click on the `Clone in Desktop` button or `Download ZIP` button to get a local copy.


## Build the Project
Go to the Feature Toggles project and build with Maven

    cd .../path/to/deliverycube-common/feature-toggles
    mvn clean install

The resultant JAR contains both compiled classes and the appropriate config files, and so can be used as both the classes JAR and your config JAR in your ATG module.


### Add ATG Modules to Maven

Note that you will need to have the ATG DAS module added to your Maven repository. 
Please read the article at https://vihung.wordpress.com/2012/05/11/putting-atg-dependencies-in-maven/ to find out more.