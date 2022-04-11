/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.mkslnd.rdeckstorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;
import java.util.Properties;
import org.apache.commons.cli.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 *
 * @author campom10
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

/*
   System.out.println(" Data Retrieved Successfully ..");
   // Testing encryption.
   try {
       System.out.println("Probando encriptacion");
        Security.addProvider(new BouncyCastleProvider());
        StandardPBEStringEncryptor mySecondEncryptor = new StandardPBEStringEncryptor();
        mySecondEncryptor.setProviderName("BC");
        mySecondEncryptor.setAlgorithm(properties.getProperty("rundeck.storage.converter.1.config.algorithm"));
        mySecondEncryptor.setPassword(properties.getProperty("rundeck.storage.converter.1.config.password"));
//        byte[] myEncryptedBytes = new byte[] { (byte)0xe0, 0x4f, (byte)0xd0, 0x20, (byte)0xea, 0x3a, 0x69, 0x10, (byte)0xa2, (byte)0xd8, 0x08, 0x00, 0x2b, 0x30, 0x30, (byte)0x9d };

        String  plainString =  mySecondEncryptor.decrypt(MyEncodedString);
        System.out.println("Clear:"+plainString);

//        String mySecondEncryptedText = mySecondEncryptor.encrypt(myText);

   }
   catch( Exception e) {
        System.err.println( e.getClass().getName()+":"+e.getMessage());
        System.exit(0);
    }
*/

    }// main
}//RdeckStorage
