import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HTMLGenerator {

    private final String root;

    public HTMLGenerator(String root) {
        this.root = new File(root).getAbsolutePath();
    }
 
    public void walk(String path) {

        File dir = new File(path);
        File[] list = dir.listFiles();

        List<String> directories = new ArrayList<>();
        List<String> images = new ArrayList<>();
        // If the folder is not empty, then depending on whether the file in the folder is a library or an image, add it to the appropriate list
        if (list != null) {
            for (File f : list) {
                if (f.isDirectory()) {
                    directories.add(f.getName());
                    System.out.println("\"" + f + "\"" + " bejárva");
                    walk(f.getAbsolutePath());
                } else if (isImage(f.getName())) {
                    images.add(f.getName());
                }
            }
        }

        directories.sort(null);
        images.sort(null);

        generateHTML(path, directories, images);
    }

    private void generateHTML(String path, List<String> directories, List<String> images) {
        generateIndex(path, directories, images);
        //relative path to the root folder
        String toRoot = relativeToRoot(path) + "index.html";
        generatePicturePages(path, images, toRoot);
    }

    //generating index page
    private void generateIndex(String path, List<String> directories, List<String> images) {

        String indexPath = path + File.separator + "index.html";
        boolean isRoot = new File(path).getAbsolutePath().equals(root);
        //relative path to root folder and parent folder
        String toRoot = relativeToRoot(path) + "index.html";
        String toParent = "../index.html";

        StringBuilder html = new StringBuilder();
        //html header
        html.append("""
                <html>
                <head>
                    <title>My images</title>
                </head>
                <body>
                """);
        //link to the root
        html.append("<a href=\"").append(toRoot).append("\"><h1>My images</h1></a>\n");
        //if we are not in the root folder then + link to the parent folder
        if (!isRoot) {
            html.append("<a href=\"").append(toParent).append("\">^^</a><br>\n");
        }

        html.append("<hr>\n<h2>Directories</h2><ul>\n");
        // + headline + ul list containing subdirectories as li/list elements, referring to themselves
        for (String d : directories) {
            html.append("<li><a href=\"")
                    .append(d)
                    .append("/index.html\">")
                    .append(d)
                    .append("</a></li>\n");
        }

        html.append("</ul>\n");
        //if there are image files, a new list of them is created
        if (!images.isEmpty()) {
            html.append("<hr><h2>Pictures</h2><ul>\n");

            for (String p : images) {
                html.append("<li><a href=\"")
                        .append(imageToHtml(p))
                        .append("\">")
                        .append(p)
                        .append("</a></li>\n");
            }

            html.append("</ul>\n");
        }
        //html closing
        html.append("""
                </body>
                </html>
                """);

        try (FileWriter writer = new FileWriter(indexPath)) {
            writer.write(html.toString());
        } catch (IOException e) {
            System.out.println("Hiba index.html generálásakor!");
            e.printStackTrace();
            System.exit(1);
        }
    }

    //generate image page
    private void generatePicturePages(String currentDir, List<String> images, String toRoot) {

        for (int i = 0; i < images.size(); i++) {
            String image = images.get(i);

            String prev = (i > 0) ? imageToHtml(images.get(i - 1)) : null;
            String next = (i < images.size() - 1) ? imageToHtml(images.get(i + 1)) : null;

            //The file is created in the given folder with the given name
            File out = new File(currentDir, imageToHtml(image));

            try (FileWriter fw = new FileWriter(out)) {

                StringBuilder html = new StringBuilder();

                // title tag -> filename h1 first heading and center alignment of navigation links (div)
                // the image is a block element and is centered
                //maximum width and height are 95% of the window
                html.append("""
                            <html>
                            <head>
                                <title>""").append(image).append("""
                                </title>
                                <style>
                                    h1 { text-align: center; }
                                    div { text-align: center; }
                                    img { display: block; margin-left: auto; margin-right: auto; max-width: 95%; max-height: 95%; }
                                </style>
                            </head>
                            <body>
                            """);

                // link to the main index/root folder
                html.append("<a href=\"").append(toRoot).append("\"><h1>Index</h1></a>\n");
                html.append("<hr>\n");

                // Navigate to the previous index page with the ^^ arrows
                html.append("<div>\n");
                html.append("    <a href=\"index.html\">^^</a>\n");
                html.append("    <p>\n");  
                //Go back to previous, if there is a previous one
                if (prev != null)
                    html.append("        <a href=\"").append(prev).append("\">Back</a>\n");
                else
                     html.append("<span style=\"color:purple; text-decoration:underline; cursor:pointer;\">Back</span>\n");
                //display current image name in navigations
                html.append("        ").append(image).append("\n");
                //Go to next if there is a next or it will be a fake button
                if (next != null)
                    html.append("        <a href=\"").append(next).append("\">Next</a>\n");
                else
                    html.append("<span style=\"color:purple; text-decoration:underline; cursor:pointer;\">Next</span>\n");

                html.append("""
                                </p>
                            </div>
                            <hr>
                            """);

                // Clickable image
                if (next != null) {
                    // there is next -> the image is clickable
                    html.append("<a href=\"")
                        .append(next)
                        .append("\"><img src=\"")
                        .append(image)
                        .append("\"></a>\n");
                } else {
                    // there isn't next → NOT clickable
                    html.append("<img src=\"")
                        .append(image)
                        .append("\">\n");
                }

                html.append("""
                            </body>
                            </html>
                            """);

                fw.write(html.toString());

            } catch (IOException e) {
                System.out.println("Hiba a kép oldalának írásakor: " + e.getMessage());
                System.exit(2);
            }
        }
    }

    // HELPER METHODS
    private boolean isImage(String name) {
        String n = name.toLowerCase();
        return n.endsWith(".png") || n.endsWith(".jpg") || n.endsWith(".jpeg");
    }

    private String imageToHtml(String s) {
        return s.substring(0, s.lastIndexOf('.')) + ".html";
    }

    private String relativeToRoot(String currentPath) {
        File rootFile = new File(root);
        File curFile = new File(currentPath);
        //converting file paths to URIs and calculating the relative path, rel = how many levels deeper the current folder is
        String rel = rootFile.toURI().relativize(curFile.toURI()).getPath(); 
        String[] parts = rel.split("/");

        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) sb.append("../");
        }
        return sb.toString();
    }
}
