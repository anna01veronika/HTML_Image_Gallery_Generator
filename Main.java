import java.io.File;
import java.io.FilenameFilter;


public class Main {

    // Constant for cleaning operation
    private static final String CLEAN_OPTION = "-c";
    // Constant for displaying help
    private static final String HELP_OPTION_SHORT = "-h";
    private static final String HELP_OPTION_LONG = "-help";

    private static void printHelp() {
        System.out.println("HASZNALATI UTMUTATO:\n");
        System.out.println("A program a megadott mappában lévő képekhez (és alkönyvtárakhoz) generál HTML index fájlokat.\n");
        System.out.println("Generálás (HTML fájlok létrehozása):\n\tjava Main <mappa eleresi utvonala>");
        System.out.println("\nTisztítás (HTML fájlok törlése):\n\tjava Main <mappa eleresi utvonala> " + CLEAN_OPTION);
        System.out.println("\nSúgó:\n\tjava Main " + HELP_OPTION_SHORT + " vagy java Main " + HELP_OPTION_LONG);
    }

    public static void main(String[] args) {
        
        // Check arguments and show help
        if (args.length < 1 || args[0].equals(HELP_OPTION_SHORT) || args[0].equals(HELP_OPTION_LONG)) {
            printHelp();
            return;
        }

        // the folder path
        String rootPath = args[0];
        // Is a cleaning mode selected?
        boolean isCleanMode = args.length == 2 && args[1].equals(CLEAN_OPTION);

        if (args.length > 2) {
            System.err.println("HIBA: Túl sok argumentumot adtál meg.");
            printHelp();
            System.exit(1);
        }
        
        File rootDir = new File(rootPath);
        
        // Check whether the specified path exists and is a folder
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            System.err.println("HIBA! Adj meg egy létező, érvényes MAPPÁT az első argumentumban: " + rootPath);
            System.exit(1);
        }

        // then Generate or Clean
        
        if (isCleanMode) {
            // Clean up method: recursive HTML file deletion
            System.out.println("--- Tisztítás indítása: HTML fájlok törlése (" + rootPath + ") ---");
            cleaner(rootPath);
            System.out.println("HTML fájlok törlése befejeződött a megadott mappában és alkönyvtáraiban.");
            
        } 
        else if (args.length == 1) {
            // create HTML files
            System.out.println("--- Generálás indítása: HTML fájlok létrehozása (" + rootPath + ") ---");
            HTMLGenerator generator = new HTMLGenerator(rootPath);
            generator.walk(rootPath);
            System.out.println("HTML generálás befejeződött.");
            
        } 
        else {
            printHelp();
        }
    }

    public static void cleaner(String path) {
        File root = new File(path);
        
        // list the contents of the file
        File[] list = root.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                File file = new File(dir, name);
                return file.isDirectory() || name.toLowerCase().endsWith(".html");
            }
        });

        if (list == null) return;
        
        for (File f : list) {
            if (f.isDirectory()) {
                // Recursive call to subdirectory
                cleaner(f.getAbsolutePath());
            } else {
                // tries to delete the file, which must have a .html extension, true if the deletion was successful
                if (f.delete()) {
                    System.out.println("Törölve: " + f.getName());
                } else {
                    System.err.println("HIBA: Nem sikerült törölni a fájlt: " + f.getAbsolutePath());
                }   //if the deletion wasn't successful
            }
        }
    }
}
