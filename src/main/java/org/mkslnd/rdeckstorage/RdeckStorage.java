package org.mkslnd.rdeckstorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.cli.*;
/**
 *
 * @author Guybrush Threepwood
 */
public class RdeckStorage {
       /**
     * @param args the command line arguments
     */
    // configuracion global para procesar filchero config.
    private static final Properties properties = new Properties();
    
    public static void main(String[] args) {
        //Testing arguments.
        Options options = new Options();

        Option configFile = new Option("c", "config", true, "Configuration file");
        configFile.setRequired(true);
        options.addOption(configFile);

        Option listAll = new Option("l", "list", false, "List all availble credentials");
        listAll.setRequired(false);
        options.addOption(listAll);
        
        Option dlist = new Option("d", "dump", true, "Coma Separated list of Credentials id to be dumped");
        dlist.setRequired(false);
        options.addOption(dlist);

        Option da = new Option("a", "dumpall", false, "dump all Credentials");
        da.setRequired(false);
        options.addOption(da);
        

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;//not a good practice, it serves it purpose         
        
        // checking options.
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("rdeckStorage", options);
            System.exit(1);
        }
        String configFilePath = cmd.getOptionValue("config");
//        System.out.println(configFilePath);
        boolean listAllStatus = cmd.hasOption("list");
//        System.out.println(Boolean.toString(listAllStatus));
        String dumpList = cmd.getOptionValue("dump");
//        System.out.println(dumpList);

//Loading config file.
        try {
                properties.load(new FileInputStream(new File(configFilePath)));
        } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
        
// Managing database.

        // TODO code application logic here
        RdKeyStorage keystorage = new RdKeyStorage(
                properties.getProperty("dataSource.driverClassName")
                ,properties.getProperty("dataSource.username")
                , properties.getProperty("dataSource.password")
                ,properties.getProperty("dataSource.url")
            );
        
        if (listAllStatus) keystorage.listAllKeys();
        
        if (cmd.hasOption("dumpall")) {
            keystorage.dumpAllKeys(
                    properties.getProperty("rundeck.storage.converter.1.config.provider")
                    ,properties.getProperty("rundeck.storage.converter.1.config.algorithm")
                    , properties.getProperty("rundeck.storage.converter.1.config.password")
            );
        }

        if (cmd.hasOption("dump") ) 
            {
                keystorage.dumpKeys( dumpList
                    ,properties.getProperty("rundeck.storage.converter.1.config.provider")
                    ,properties.getProperty("rundeck.storage.converter.1.config.algorithm")
                    , properties.getProperty("rundeck.storage.converter.1.config.password")
                );
            }

    }// main
}//RdeckStorage
