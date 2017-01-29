package tme.model;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

public final class DBConnector {
    private static String URI;
    private static Collection collection = null;
    private static Database database = null;
    
    private DBConnector() { }
    
    public static Database getInstance() {
        if (database == null) {
            try {
                Class cl = Class.forName("org.exist.xmldb.DatabaseImpl");
                database = (Database)cl.newInstance();
                database.setProperty("create-database", "true");
                DatabaseManager.registerDatabase(database);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | XMLDBException exc) {

            }
        }
        
        return database;
    }
    
    public static boolean setCollection(String URI, String collection) {
        try {
            DBConnector.URI = URI;
            DBConnector.collection = DatabaseManager.getCollection(URI + collection);
            return true;
        } catch (XMLDBException exc) {
            return false;
        }
    }
    
    public static void close() {
        try {
            if (collection != null)
                collection.close();
        } catch (XMLDBException exc) {
            
        }
    }
    
    public static Collection getCollection() {
        return collection;
    }
    
    public static String getURI() {
        return URI;
    }
}
