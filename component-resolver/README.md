Delivery Cube Common: Component Resolver
===================

The Delivery Cube Common: Component Resolver contains utility classes and components to help accessing ATG Nucleus components from outside ATG

- [The Component Resolver Project](#the-component-resolver-project )
    - [What Are Component Resolvers?](#about)
    - [Get the Code](#get-the-code)
    - [Build the Project](#build-the-project)
        - [Add ATG Modules to Maven](#add-atg-modules-to-maven)
    - [Using Component Resolvers](#using-component-resolvers)


# The Component Resolver Project

The Component Resolver project contains utility classes and components to help accessing ATG Nucleus components from outside ATG


## What Are Component Resolvers?

The ATG Nucleus is a component container - somewhat akin to a Spring Bean Factory. 
It hosts executable Java components, and manages their lifecycle - instantiating, starting up, shutting down, etc.
It also manages inter-component dependencies.

Components in the Nucleus are scoped - they can be request-scoped, session-scoped or global (application-scoped).

Components in the Nucleus are uniquely named. In effect, the Nucleus provides a namespace.

Within the Nucleus, components can be bound to, and accessed, by name.

Component Resolvers are classes that help access Nucleus components, by name, from outside a Nucleus context.
They can be used, for example, from within a Spring MVC web application to get access to a component running within the Nucleus.

## Get the Code

Clone the repository with

    git clone https://github.com/deliverycube/deliverycube-common.git
    
or click on the `Clone in Desktop` button or `Download ZIP` button to get a local copy.


## Build the Project
Go to the Component Resolvers project and build with Maven

    cd .../path/to/deliverycube-common/component-resolvers
    mvn clean install

The resultant JAR will contain compiled classes. Include this in your web application, or from wherever you expect to resolve a Nucleus component.


### Add ATG Modules to Maven

Note that you will need to have the ATG DAS module added to your Maven repository. 
Please read the article at https://vihung.wordpress.com/2012/05/11/putting-atg-dependencies-in-maven/ to find out more.

## Using Component Resolvers

This project provides a Component Resolver interface, and two implementations of that Component Resolver interface.

The DynamoComponentResolver implementation uses the Dynamo Request and ATG's internal mechanism to resolve components. This brings with it a compile-time dependency on DAS classes. 

The JndiComponentResolver implementation uses JNDI. This does not have any compile-time dependency on ATG classes.

For example, from a Spring project, you can create a Profile Resolver component that gives your Spring objects a handle to the ATG user profile, like so

    <bean id="profileResolver"
      class="deliverycube.common.atg.componentresolver.jndi.JndiComponentResolver">
        <property name="componentName" value="/atg/userprofiling/Profile"/>
    </bean>  

and then, in your non-ATG class

    Profile userProfile = (Profile) profileResolver.resolveComponent();
    
will give you a the customer Profile object.