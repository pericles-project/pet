# PERICLES Extraction Tool - PET 

The PERICLES Extraction Tool (PET) is an open source (Apache 2 licensed) Java software for the extraction of significant information from the environment where digital objects are created and modified. This information supports object use and reuse, e.g. for a better long-term preservation of data. The Tool was developed entirely for the PERICLES EU project [http://www.pericles-project.eu/](http://www.pericles-project.eu/) by Fabio Corubolo, University of Liverpool, and Anna Eggers, Göttingen State and University Library.

In a nutshell, PET works by analysing the use of the data from within the creator or consumer environment, extracting information useful for the later reuse of the data that is not possible to derive in later phases of the data lifecycle, as for example at ingest time. It works based on sheer curation principles, but has no remote functionality, so the environments user has full control of which information to extract and to keep. The tool works by analysing both files, and their changes, and the system environment. 

The tool is generic. It can be adapted for various scenarios, as it provides a plug-in structure for the integration of use specific extraction algorithms.

Various information extraction techniques are implemented as plug-in extraction modules, as complete implementations or where possible by re-using already existing external tools and libraries. Environment monitoring is supported by specialized monitoring daemons and continuous extraction of relevant information triggered by environment events related to the creation and alteration of digital objects, like, e.g., the alteration of an observed file or directory, opening or closing a specific file, and other system calls. A snapshot extraction mode exists for capturing the current state of the environment, which is mainly designed to extract information that doesn't change frequently, as e.g. system resource specifications.     

An advantage of the use of PET is that established information extraction tools can be integrated as modules. An user who had to use many different tools to extract the information and metadata for a scenario, could use our tool as framework instead, and will get all required information in one standard format (JSON, or XML) saved in a selectable storage interface. Furthermore this approach enables the possibility to enrich the established set of information with additional information extracted by other PET modules.

## [Dowload PET] (https://github.com/pericles-project/pet/releases/latest)

# User documentation 

## [Quick start guide] (https://github.com/pericles-project/pet/wiki/Quick-start-guide)

### [Description and features of the tool] (https://github.com/pericles-project/pet/wiki)

### [Examples for tool usages] (https://github.com/pericles-project/pet/wiki/Examples)

### [Exemplary scenarios for the tool] (https://github.com/pericles-project/pet/wiki/Scenarios)

# Developer documentation 

* [How to build from source] (https://github.com/pericles-project/pet/wiki/Building-from-source)
* [Development of Extraction Modules and Monitoring Daemons for the tool] (https://github.com/pericles-project/pet/wiki/Development-of-Extraction-Modules-and-Monitoring-Daemons)
* [Javadocs] (http://pericles-project.github.io/pet/gh-pages/javadoc/)
* [Eclipse installation for developers] (https://github.com/pericles-project/pet/wiki/Eclipse-installation)
* [Development information and software architecture description] (https://github.com/pericles-project/pet/wiki/PET-Development-and-Software-Architecture)
* [Ideas for future developments] (https://github.com/pericles-project/pet/wiki/TODO-list,-further-developments)

## Other documentation can be found on the GitHub wiki: https://github.com/pericles-project/pet/wiki

<img src="https://github.com/pericles-project/pet/blob/master/wiki-images/LogoEU.png" width="150"/>

<a href="http://www.pericles-project.eu/"><img src="https://github.com/pericles-project/pet/blob/master/wiki-images/PERICLES%20logo_black.jpg" width="350"/> </a>

<a href="http://www.liv.ac.uk/"> <img src="https://github.com/pericles-project/pet/blob/master/wiki-images/liverpool_logo.png" width="350"/></a>

<a href="http://www.sub.uni-goettingen.de/"><img src="https://github.com/pericles-project/pet/blob/master/wiki-images/sub-logo.jpg" width="350"/></a>


