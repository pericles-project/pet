# PERICLES Extraction Tool - PET 

The PERICLES Extraction Tool (PET) is an open source (Apache 2 licensed) Java software for the extraction of significant information from the environment where digital objects are created and modified. This information supports object use and reuse, e.g. for a better long-term preservation of data. The Tool was developed entirely for the PERICLES EU project [http://www.pericles-project.eu/](http://www.pericles-project.eu/) by Fabio Corubolo, University of Liverpool, and Anna Eggers, Göttingen State and University Library.

*PEASE NOTICE:* PET is prototype research software, and as such it has limitations in its usability and it has not undergone extensive testing; don't expect production quality release software. It will require some careful configuration, but still it is working software and can produce useful and novel results.

In a nutshell, PET works by analysing the use of the data from within the creator or consumer environment, extracting information useful for the later reuse of the data that is not possible to derive in later phases of the data lifecycle, as for example at ingest time. It works based on sheer curation principles, but has no remote functionality, so the environments user has full control of which information to extract and to keep. The tool works by analysing both files, and their changes, and the system environment. 

The tool is generic. It can be adapted for various scenarios, as it provides a plug-in structure for the integration of use specific extraction algorithms.

Various information extraction techniques are implemented as plug-in extraction modules, as complete implementations or where possible by re-using already existing external tools and libraries. Environment monitoring is supported by specialized monitoring daemons and continuous extraction of relevant information triggered by environment events related to the creation and alteration of digital objects, like, e.g., the alteration of an observed file or directory, opening or closing a specific file, and other system calls. A snapshot extraction mode exists for capturing the current state of the environment, which is mainly designed to extract information that doesn't change frequently, as e.g. system resource specifications.     

An advantage of the use of PET is that established information extraction tools can be integrated as modules. An user who had to use many different tools to extract the information and metadata for a scenario, could use our tool as framework instead, and will get all required information in one standard format (JSON, or XML) saved in a selectable storage interface. Furthermore this approach enables the possibility to enrich the established set of information with additional information extracted by other PET modules.

## [Download PET] (https://github.com/pericles-project/pet/releases/latest)

# User documentation 

### [Quick start guide] (https://github.com/pericles-project/pet/wiki/Quick-start-guide)
*  [Introduction video](https://www.youtube.com/watch?v=wonEEil1_c0)
*  [Pericles BLOG presenting PET] (http://www.pericles-project.eu/blog/post/metadata%20extraction,%20environment%20information)
*  [Description and features of the tool] (https://github.com/pericles-project/pet/wiki)
*  [Examples for tool usages] (https://github.com/pericles-project/pet/wiki/Examples)
*  [Exemplary scenarios for the tool] (https://github.com/pericles-project/pet/wiki/Scenarios)

*  [Slides for IDCC2015] (http://www.slideshare.net/FabioCorubolo/slides-for-idcc-pet-presentation)
*  [Tutorial 1 for IDCC2015] (http://www.slideshare.net/FabioCorubolo/pet-tutorial-script-1-system-info)
*  [Tutorial 2 for IDCC2015] (http://www.slideshare.net/FabioCorubolo/pet-tutorial-script-2-file-information)
*  [Tutorial 3 for IDCC2015] (http://www.slideshare.net/FabioCorubolo/pet-demo-script-3-monitoring-document-access)

# Developer documentation 

* [How to build from source] (https://github.com/pericles-project/pet/wiki/Building-from-source)
* [Development of Extraction Modules and Monitoring Daemons for the tool] (https://github.com/pericles-project/pet/wiki/Development-of-Extraction-Modules-and-Monitoring-Daemons)
* [Javadocs] (http://pericles-project.github.io/pet/gh-pages/javadoc/)
* [Eclipse installation for developers] (https://github.com/pericles-project/pet/wiki/Eclipse-installation)
* [Development information and software architecture description] (https://github.com/pericles-project/pet/wiki/PET-Development-and-Software-Architecture)
* [Ideas for future developments] (https://github.com/pericles-project/pet/wiki/TODO-list,-further-developments)

# Research
* [iPRES 2014 paper: ‘A pragmatic approach to signifcant environment information collection to support object reuse’] (http://www.pericles-project.eu/uploads/files/ipres2014_PET.pdf)
* [Slides used for paper presentation] (http://www.slideshare.net/FabioCorubolo/ipres-2014-paper-presentation)
* [Full project deliverable on Environment Information Extraction] (http://www.pericles-project.eu/uploads/files/PERICLES_WP4_D4_1-Initial_version_environment_information_extraction_tools-V1_0.pdf)
 
We investigated how to map the PET extracted information into high level ontologies, e.g. the Digital Ecosystem Model. The result is a workflow which was shown on Dec. 2016 at the PERICLES final event:
* [Digital Ecosystem Model] (https://github.com/pericles-project/DEM) An ontology to model digital ecosystems
* [Workflow] (https://github.com/annaeg/PET2EcoBuilder4DEMO)

# Credits

 _This project has received funding from the European Union’s Seventh Framework Programme for research, technological development and demonstration under grant agreement no FP7- 601138 PERICLES._   
 
 <a href="http://ec.europa.eu/research/fp7"><img src="https://github.com/pericles-project/pet/blob/master/wiki-images/LogoEU.png" width="110"/></a>
 <a href="http://www.pericles-project.eu/"> <img src="https://github.com/pericles-project/pet/blob/master/wiki-images/PERICLES%20logo_black.jpg" width="200" align="right"/> </a>

<a href="http://www.liv.ac.uk/"> <img src="https://github.com/pericles-project/pet/blob/master/wiki-images/liverpool_logo.png" width="300"/></a>

<a href="http://www.sub.uni-goettingen.de/"><img src="https://github.com/pericles-project/pet/blob/master/wiki-images/sub-logo.jpg" width="300"/></a>


