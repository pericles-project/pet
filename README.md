# PERICLES Extraction Tool - PET 

The PERICLES Extraction Tool (PET) is an open source and platform independent tool under Apache license version 2, written in Java 7, for the extraction of significant information from the environment where digital objects are created and modified. This information supports object use and reuse, e.g. for a better long-term preservation of data. The Tool was developed entirely in scope of the PERICLES EU project [http://www.pericles-project.eu/](http://www.pericles-project.eu/) by Fabio Corubolo, University of Liverpool, and Anna Eggers, Göttingen State and University Library.

In a nutshell, PET works by analysing the use of the data from within the creator or consumer environment, extracting information useful for the later reuse of the data that is not possible to derive in later phases of the data lifecycle, as for example at ingest time. It works based on sheer curation principles, but has no remote functionality, so the environments user has full control of which information to extract and to keep.

The tool is generic. It can be adapted for various scenarios, as it provides a plug-in structure for the integration of use specific extraction algorithms.

Various information extraction techniques are implemented as plug-in extraction modules, as complete implementations or where possible by re-using already existing external tools and libraries. Environment monitoring is supported by specialized monitoring daemons and continuous extraction of relevant information triggered by environment events related to the creation and alteration of digital objects, like, e.g., the alteration of an observed file or directory, opening or closing a specific file, and other system calls. A snapshot extraction mode exists for capturing the current state of the environment, which is mainly designed to extract information that doesn't change frequently, as e.g. system resource specifications.     

An advantage of the use of PET is that established information extraction tools can be integrated as modules. An user who had to use many different tools to extract the information and metadata for a scenario, could use our tool as framework instead, and will get all required information in one standard format (JSON, or XML) saved in a selectable storage interface. Furthermore this approach enables the possibility to enrich the established set of information with additional information extracted by other PET modules.


## Documentation can be found on the GitHub wiki: https://github.com/pericles-project/pet/wiki
* Description of the tool
* Quick start guide
* Examples for tool usages
* Exemplary scenarios for the tool
* How to build the tool from sources
* Development information and software architecture description
* Eclipse installation for developers
* A todo list for further developments
* Development of Extraction Modules and Monitoring Daemons for the tool


[University of Liverpool] (http://www.liv.ac.uk/)
[Göttingen State and University Library] (http://www.sub.uni-goettingen.de/)



