package org.mkslnd.rdeckstorage;

import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 *
 * @author Guybrush Threepwood.
 */
public class RdKeyStorage {
    private String driverClassName= null;
    private String username= null;
    private String password= null;
    private String url= null;
    private Connection keysdb=null;
    private String baseQuery="select id, version,json_data, date_created, dir, last_updated, name, namespace, data, path_sha  from public.storage where namespace is null and dir like 'keys/%'  "; //Do not forget the ";"

    public RdKeyStorage(String driverClassName, String username, String password, String url) {
        this.driverClassName=driverClassName;
        this.username=username;
        this.password=password;
        this.url=url;
    }

public void openConnection(){
    try {
      Class.forName(driverClassName);
      keysdb = DriverManager.getConnection(url,username, password);
      keysdb.setAutoCommit(false);
//      System.out.println("Successfully Connected.");
    } catch ( Exception e ) {
      System.err.println("Error opening database Connection");
      System.err.println( e.getClass().getName()+": "+ e.getMessage() );
      System.exit(1);
   }
}  //OpenConnection  

public void closeConnection(){
    try {
        keysdb.close();
//        System.out.println("Successfully DisConnected.");
    } catch ( Exception e ) {
        System.err.println("Error closing database Connection");
        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
        System.exit(1);
   }
}

public void listAllKeys(){
    openConnection();
        try {
            Statement stmt = keysdb.createStatement();
            ResultSet rs = stmt.executeQuery( baseQuery+";" );
            while ( rs.next() ) {
               int id = rs.getInt("id");
               String  name = rs.getString("name");
               String  dir = rs.getString("dir");
               System.out.println(id+" "+name+" "+dir);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(RdKeyStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    closeConnection();
}

public void dumpKeys(String keylist, String eprovider, String ealgorithm, String epassword){
    openConnection();
        try {
            
            Statement stmt = keysdb.createStatement();
            String query = baseQuery +" and id in ("+klist4query(keylist)+");";
//            System.out.println(query);
            ResultSet rs = stmt.executeQuery( query );
            System.out.println();
            System.out.println("******************************************");
            System.out.println("*           DUMPING KEYS                 *");
            System.out.println("******************************************");

            while ( rs.next() ) {
                int id = rs.getInt("id");
                String  name = rs.getString("name");
                String  dir = rs.getString("dir");
                System.out.println("==>" +id+" "+name+" "+dir);
                System.out.println("================VALUE=====================");
//Here goes decrypt of the key.               
                byte[] data = rs.getBytes("data");
                String b64data = new String( Base64.getEncoder().encode(data),StandardCharsets.UTF_8);
                Security.addProvider(new BouncyCastleProvider());
                StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
                encryptor.setProviderName(eprovider);
                encryptor.setAlgorithm(ealgorithm);
                encryptor.setPassword(epassword);
                String  plainData =  encryptor.decrypt(b64data);
                System.out.println(plainData);
               System.out.println("=============END VALUE=====================");
               
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(RdKeyStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    closeConnection();
}//dumpKeys

private String klist4query(String keylist){
    boolean first=true;
    StringBuilder enclosed= new StringBuilder();
    for (String key : keylist.split(",") ) {
//        System.out.println("Processing:"+ key);
        if (first) {
//            System.out.println("fKey:"+key.trim() );
            first=false;
            enclosed.append("\'"+key.trim()+"\'");
        }
        else { // Not First
//            System.out.println("nfKey:"+key.trim() );
            enclosed.append(",\'"+key.trim()+"\'");
        }
    }// for
//    System.out.println("Retorno:"+enclosed.toString());
    return enclosed.toString();
} // klist4query

public  List<Integer> getAllKeysId(){
    openConnection();
    List keyids = new ArrayList<String>();
        try {
            Statement stmt = keysdb.createStatement();
            ResultSet rs = stmt.executeQuery( baseQuery+";" );
            while ( rs.next() ) {
               int id = rs.getInt("id");
               keyids.add(Integer.valueOf(id));
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(RdKeyStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    closeConnection();
    return keyids;
}

public void dumpAllKeys(String eprovider, String ealgorithm, String epassword){
                    dumpKeys( keyid2String(getAllKeysId())
                    ,eprovider
                    ,ealgorithm
                    , epassword
                );
}//dumpAllKeys
 

private String keyid2String(List<Integer> keyids){
    boolean first=true;
    StringBuilder skeylist= new StringBuilder();
    for (Integer key : keyids) {
        if (first) {
//            System.out.println("fKey:"+key.trim() );
            first=false;
            skeylist.append(key.toString());
        }
        else { // Not First
//            System.out.println("nfKey:"+key.trim() );
            skeylist.append(","+key.toString());
        }
    }// for
//    System.out.println(skeylist.toString());
    return skeylist.toString();
} // keyid2String

    
}
