Delivery Cube Common: Feature Toggles Module 
===================

The Delivery Cube Common: Feature Toggles Module contains utility classes and components for providing Feature Toggles to your ATG application

- [The Feature Toggles Project](#the-feature-toggles-project )
    - [What Are Feature Toggles?](#about)
    - [Get the Code](#get-the-code)
    - [Build the Project](#build-the-project)
        - [Add ATG Modules to Maven](#add-atg-modules-to-maven)
    - [Using Feature Toggles](#using-feature-toggles)
    - [Extending Feature Toggles](#extending-feature-toggles)


# The Feature Toggles Project

The Feature Toggles module contains code and config for a Feature Toggle Service in ATG


## What Are Feature Toggles?

For a background on Feature Toggles, please read the article by Martin Fowler at 
http://martinfowler.com/bliki/FeatureToggle.html


## Get the Code

Clone the repository with

    git clone https://github.com/deliverycube/deliverycube-common.git
    
or click on the `Clone in Desktop` button or `Download ZIP` button to get a local copy.


## Build the Project
Go to the Feature Toggles project and build with Maven

    cd .../path/to/deliverycube-common/feature-toggles
    mvn clean install

The resultant JAR contains both compiled classes and the appropriate config files, and so can be used as both the 
classes JAR and your config JAR in your ATG module.


### Add ATG Modules to Maven

Note that you will need to have the ATG DAS module added to your Maven repository. 
Please read the article at https://vihung.wordpress.com/2012/05/11/putting-atg-dependencies-in-maven/ to find out more.

## Using Feature Toggles

This project provides a Feature Toggles service that can be used to define which features are toggled and which are not.

In your own module, override the configuration for the `FeatureToggles` service component, and override the `features` 
property along the lines below

    # /deliverycube/common/featuretoggles/FeatureToggles
    features=\
        myFeature1=true,\
        myFeature2=false,\
        myFeature3=true

This sample above declares that `myFeature1` and `myFeature3` are enabled, whilst `myFeature2` is  explicitly disabled.

For any feature that is not explicitly defined in the configuration, the service treats it as disabled. 

This service is a global component, and can be queried directly from code, along the lines of

    if(getFeatureToggles().isFeatureToggled("myFeature1")) {
        // code to execute if feature toggle is on
    } else {
        // code to execute if feature toggle is off
    }
      

This project also provides a Feature Toggles Droplet that can be used within JSP pages to conditionally render HTML/JSP 
code based on whether a feature is toggled.

    <dsp:droplet name="/deliverycube/common/featuretoggles/FeatureToggleDroplet">
      <dsp:param name="feature" value="myFeature1"/>
      <dsp:oparam name="on">
        <!-- Content to render if the feature toggle is on -->
      </dsp:oparam>
      <dsp:oparam name="off">
        <!-- Content to render if the feature toggle is off -->
      </dsp:oparam>
    </dsp:droplet>


## Extending Feature Toggles
The Feature Toggles service implements a very simple interface - 
`deliverycube.common.atg.featuretoggles.FeatureToggleService`

This requires a single method to be implemented

    /**
     * Determine the value of the feature toggle for the given feature.
     * 
     * @param pFeature
     *            the feature
     * @return the feature toggle - <code>true</code> if on, <code>false</code>
     *         if off or unspecified.
     */
    public abstract boolean isFeatureToggled(final String pFeature);

You can create your own implementation of the Feature Toggles service by implementing this interface.

For example, you may want to create an implementation that uses a versioned repository to hold feature toggles. This would allow you to enable or disable features at run-time by deploying new data from the BCC.    
