/*
 * Copyright 2012 Niklas Rehfeld .
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ddt;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ddt.listener.dsi.DocumentSummaryInfoListener;
import org.ddt.listener.ole.OleStreamListener;
import org.ddt.output.BasicTextOutputModule;
import org.ddt.output.BasicXMLOutputModule;
import org.ddt.output.CSVOutputModule;
import org.ddt.output.OutputModule;
import org.ddt.processor.FileProcessor;
//import org.ddt.processor.HSLFFileProcessor;
import org.ddt.processor.POIFSEventProcessor;
import org.ddt.processor.RecordEventProcessor;
import org.ddt.listener.records.DConRefListener;
import org.ddt.listener.records.DConRefRecord;
import org.ddt.listener.records.SupBookListener;
import org.apache.poi.hssf.record.SupBookRecord;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * This is the main class, which handles the command-line options and runs the
 * program.
 * <p/>
 * @author Niklas Rehfeld
 */
public class OfficeDDT
{

    /** Do we recurse into subdirectories? */
    private boolean recursive;
    /** File ore Directory to check for links */
    private File inputFile;
    /** where to write the output */
    private OutputStream output;
    /** Output module to use for formatting the output */
    private OutputModule outputModule;
    /** FileProcessors that will do the actual work */
    private List<FileProcessor> processors;
    /** how many files have been processed */
    private int count;
    /** how many links have been found */
    private int found;
    /** whether to use file extensions to ignore some files */
    private boolean ignoreNonOffice;
    /** the logger to log to. */
    private static final Logger logger = Logger.getLogger("org.ddt");

    /**
     * Main method.
     * <p/>
     * <b>Usage: </b>
     * <code> OfficeDDT [-o outputfile] [-r] [-f outputFormat] input</code>
     * <p/>
     * Prints any links found in
     * <code>input</code> to
     * <code>outputfile</code>
     * or to the console if no
     * <code>-o</code> option is given, in the format
     * specified by
     * <code> outputFormat</code>.
     * <p/>
     * If
     * <code>input</code> is a directory and the
     * <code>-r</code> flag is used,
     * then the directory is scanned recursively.
     * <p/>
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        OfficeDDT ol = new OfficeDDT();
        ol.parseArgs(args);

        ol.findLinks();
    }

    /**
     * Constructor.
     */
    public OfficeDDT()
    {
        processors = new ArrayList<FileProcessor>();
        count = 0;
        found = 0;
    }

    /**
     * starts running the program on the input file.
     */
    private void findLinks()
    {
        try
        {
            processFile(inputFile);
            outputModule.write();
        } catch (IOException ex)
        {
            logger.log(Level.WARNING, "Something went wrong.", ex);
        }


        System.err.printf("\nProcessed %d files\n", count);
        System.err.printf("Found %d Links", outputModule.getNumberLinksFound());
    }

    /**
     * Processes the given file/directory.
     * <p/>
     * If the file is a directory, the files in it are processed and if the
     * recursive flag is true subdirectories are processed too.
     *
     * @todo some sort of file detection beforehand, don't think catching
     * exceptions is the ideal way of doing this...
     *
     * @todo tidy up exception handling, seems to throw exceptions that are 
     * handled inside the method.
     *
     * @param f
     */
    private void processFile(File f) throws IOException
    {
        if (f.isDirectory())
        {
            for (File subFile : f.listFiles())
            {
                if (subFile.isFile() || recursive)
                {
                    processFile(subFile);
                }
            }
        } else
        {
	    //check file types. If the -i option has been selected, just check 
	    //the file extension, otherwise attempt to read it as a POI doc.
	    int filetype = this.getFileType(f);
	    if (filetype == Constants.FILETYPE_ALL && ignoreNonOffice)
		{
		    logger.log(Level.WARNING, "Unknown file type {0}, ignoring it.", f.getName());
		    return;
		}
	    else
		{
		    // this next bit is simply to check if we have a vaguely usable file
		    // (i.e. some sort of office file)
		    FileInputStream is = new FileInputStream(f);
		    try
			{
			    POIFSFileSystem pfs = new POIFSFileSystem(is);
			}
		    catch(IOException e)
			{
			    logger.log(Level.WARNING, "Failed to read {0}, skipping it", f.getName());
			    is.close();
			    return;
			}
		    catch(OfficeXmlFileException e)
			{
			    logger.log(Level.WARNING, "XML files not supported: skipping file {0}", f.getName());
			    is.close();
			    return;
			}
		    is.close();
		}

	    count++;
	    //process the actual file.
	    //There's a problem here, if the last processor doesn't accept the
	    //file type then the file in the output module can be null.
	    //seems to be fixed, but need to look at it a bit closer.
	    try
            {
                FileProcessor p = null;
                for (int i = 0; i < processors.size(); i++)
                {
                    //IOExceptions need to be caught inside the loop, as they
                    //can happen to one file processor, but not others, so it
                    //still makes sense to keep processing with the rest of them.
                    //they are not fatal for the file, just the file/processor
                    //combination.
                    try
                    {
                        p = processors.get(i);
			

			boolean accepted = p.acceptsFileType(filetype);
                        if (accepted)
                        {
                            logger.log(Level.FINE,
                                    "processing file {0} with {1}", new Object[]
                                    {
                                        f.getName(),
                                        p.getClass().getCanonicalName()
                                    });
                            outputModule.addLinks(f, p.process(f));
                        }
                    } catch (IOException e)
                    {
                        logger.log(Level.INFO,
                                "Couldn''t process file {0} with processor {1}\n Error: {2}",
                                new Object[]
                                {
                                    f.getName(),
                                    p.toString(), e.getLocalizedMessage()
                                });
                        continue;
                    }
                }
            } catch (OfficeXmlFileException ex) //zip files/ooxml files
            {
                logger.log(Level.WARNING, "zip files and ooxml files not supported: {0}",
                        f.getName());
                return;
            }
        }

    }

    /**
     * Parses the argument list.
     * <p/>
     * @param args command line arguments.
     */
    private void parseArgs(String[] args)
    {
        if (args.length < 1)
        {
            usage();
        }

        List<String> argList = new ArrayList<String>();
        argList.addAll(Arrays.asList(args));

        int pos;

        //debug messages
        Level l = Level.WARNING;
        if (argList.contains("-v"))
        {
            pos = argList.indexOf("-v");
            if (argList.size() > ++pos)
            {
                try
                {
                    l = Level.parse(argList.get(pos).toUpperCase());

                } catch (IllegalArgumentException e)
                {
                    System.err.println(
                            "No such verbosity level: " + argList.get(pos));
                    System.exit(-2);
                }
                argList.remove(pos);//must be first as we don't know it's name.
                argList.remove("-v");
            }
        }

        //quiet! (only fatal messages i.e. runtime errors)
        if (argList.contains("-q"))
        {
            l = Level.OFF;
            argList.remove("-q");
        }

        //actually set the verbosity levels.
        //need to set the root logger and all of its handlers.
        for (Handler h : Logger.getLogger("").getHandlers())
        {
            h.setLevel(l);
        }
        logger.setLevel(l);


        //recursive switch
        recursive = false;
        if (argList.contains("-r"))
        {
            recursive = true;
            argList.remove("-r");
        }

	//ignore unknown file extensions
	ignoreNonOffice = false;
	if (argList.contains("-i"))
	    {
		ignoreNonOffice = true;
		argList.remove("-i");
	    }

        //output file
        if (argList.contains("-o"))
        {
            pos = argList.indexOf("-o");
            String outArg;
            if (argList.size() > ++pos)
            {
                outArg = argList.remove(pos);
                try
                {
                    output = new FileOutputStream(outArg);
                    argList.remove("-o");
                } catch (FileNotFoundException ex)
                {
                    logger.log(Level.SEVERE, "Can't open output file", ex);
                    usage();
                }
            } else
            {
                usage(); //exits program.
            }
        } else
        {
            output = System.out;
        }

        //format
        if (argList.contains("-f"))
        {
            pos = argList.indexOf("-f");
            if (++pos < argList.size())
            {
                outputModule = selectOutputFormat(argList.remove(pos));
                argList.remove("-f");
            }
        } else
        {
            outputModule = selectOutputFormat("default");
        }

        registerDefaultProcessors();
        //should now be empty except for the input argument.
        if (argList.size() != 1)
        {
            usage();
        }

        inputFile = new File(argList.get(0));

        //if no input has been set, die.
        if (inputFile == null || !inputFile.canRead())
        {
            logger.log(Level.SEVERE, "Can't open input file {0}", inputFile.getPath());
            usage();
        }

        logger.log(Level.FINEST, "Opened input file: {0} ",
                inputFile.getPath());


    }

    /**
     * Adds the default FileProcessor implementations that get used.
     */
    private void registerDefaultProcessors()
    {
//        processors.add(new TestProcessor(inputFile, outputModule));

        POIFSEventProcessor pep = new POIFSEventProcessor();
	pep.registerListener(new DocumentSummaryInfoListener());//, "\005DocumentSummaryInformation");
	POIFSEventProcessor pep2 = new POIFSEventProcessor();
	pep2.registerListener(new OleStreamListener());
        processors.add(pep);
	processors.add(pep2);

//        processors.add(new HSLFFileProcessor());

        RecordEventProcessor rep = new RecordEventProcessor();
        rep.registerListener(new SupBookListener(),
                SupBookRecord.sid);
        rep.registerListener(new DConRefListener(),
                DConRefRecord.sid);

        processors.add(rep);
    }

    /**
     * Prints program usage information. Quits the program immediately.
     */
    private void usage()
    {
        System.err.println(
                "Usage: OfficeLink [-o output] [-f format] [-d level | -q] [-r] [-i] input");
        System.err.println("Options:");
        System.err.println(
                "\t input \t\tthe file or directory to run on (compulsory) ");
        System.err.println(
                "\t -o output \tthe file to output results to. (optional: defaults to the console)");
        System.err.println(
                "\t -f format \tthe format to write the output as (optional: defaults to plain text)");
        System.err.println(
                "\t\t\t (the currently available formats are text, CSV and XML)");
        System.err.println(
                "\t -v level\tthe verbosity level. (optional: defaults to 'WARNING')");
        System.err.println("\t\t\tlevel must be one of:");
        System.err.println(
                "\t\t\t ALL, FINEST, FINER, FINE, INFO, WARNING, SEVERE, OFF");
        System.err.println("Flags:");
        System.err.println(
                "\t -r \t\tif input is a directory, whether to recurse into subdirectories (optional)");
        System.err.println(
                "\t -q \t\tsupress all error output, equivalent to '-v OFF' (optional)");
	System.err.println("\t -i \t\tuse file extensions to ignore non-office files. (optional)" +
			   "\n\t\t\tNOTE: This means that any file not ending in .ppt, .xls or .doc is ignored." + 
			   "\n\t\t\tThis will speed up processing for directories with lots of non-office files,"+
			   "\n\t\t\tbut file extensions are not always a reliable way of identifying file types.");
        System.exit(0);
    }

    /**
     * Selects an output module from a map.
     * <p/>
     * @return
     */
    private OutputModule selectOutputFormat(String format)
    {

        if (format.equalsIgnoreCase("csv"))
        {
            return new CSVOutputModule(output);
        }
        if (format.equalsIgnoreCase("xml"))
        {
            return new BasicXMLOutputModule(output);
        }
        //default
        return new BasicTextOutputModule(output);
    }

    /**
     * gets the file type from office files by their extensions.
     *
     * @param f the file to check.
     * @return 1 (001b) for .doc, 2 (010b) for .xls, 4 (100b) for .ppt
     * @see Constants
     */
    private int getFileType(File f)
    {
        if (f.getName().endsWith("xls"))
        {
            return Constants.FILETYPE_EXCEL;
        }
        if (f.getName().endsWith("doc"))
        {
            return Constants.FILETYPE_WORD;
        }
        if (f.getName().endsWith("ppt"))
        {
            return Constants.FILETYPE_POWERPOINT;
        }
        return Constants.FILETYPE_ALL;
    }
}
