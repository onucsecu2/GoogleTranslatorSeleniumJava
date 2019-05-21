import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class updateJar {
	public void updateJarFile(File srcJarFile,List <String> filesList) throws IOException {
	    File tmpJarFile = File.createTempFile("tempJar", ".tmp");
	    JarFile jarFile = new JarFile(srcJarFile);
	    boolean jarUpdated = false;
	    try {
	    JarOutputStream tempJarOutputStream = new JarOutputStream(new FileOutputStream(tmpJarFile));
	    
        try {
            //Added the new files to the jar.
            for(int i=0; i < filesList.size(); i++) {
            	
            	
                File file = getFileOnline(filesList.get(i));
                FileInputStream fis = new FileInputStream(file);
                try {
                    byte[] buffer = new byte[1024];
                    int bytesRead = 0;
                    JarEntry entry = new JarEntry(file.getName());
                    tempJarOutputStream.putNextEntry(entry);
                    while((bytesRead = fis.read(buffer)) != -1) {
                        tempJarOutputStream.write(buffer, 0, bytesRead);
                    }
 
                    //System.out.println(entry.getName() + " added.");
                }
                finally {
                    fis.close();
                }
                file.delete();
            }
 
            //Copy original jar file to the temporary one.
            Enumeration jarEntries = jarFile.entries();
            while(jarEntries.hasMoreElements()) {
                JarEntry entry = (JarEntry) jarEntries.nextElement();
                if(filesList.contains(entry.getName())) {
                	continue;
                }
                InputStream entryInputStream = jarFile.getInputStream(entry);
                tempJarOutputStream.putNextEntry(entry);
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                while ((bytesRead = entryInputStream.read(buffer)) != -1) {
                    tempJarOutputStream.write(buffer, 0, bytesRead);
                }
            }
 
            jarUpdated = true;
        }
        catch(Exception ex) {
            ex.printStackTrace();
            tempJarOutputStream.putNextEntry(new JarEntry("stub"));
        }
        finally {
            tempJarOutputStream.close();
        }
	    }finally {
	        jarFile.close();
	        //System.out.println(srcJarFile.getAbsolutePath() + " closed.");
	 
	        if (!jarUpdated) {
	            tmpJarFile.delete();
	        }
	    }
	    if (jarUpdated) {
	        srcJarFile.delete();
	        tmpJarFile.renameTo(srcJarFile);
	        //System.out.println(srcJarFile.getAbsolutePath() + " updated.");
	    }
	}

	private File getFileOnline(String string) throws IOException {
		// TODO Auto-generated method stub
		String fromFile = "https://trackingdaily.000webhostapp.com/"+string;
	    String toFile = System.getProperty("user.home")+"/"+string ;
		URL website = new URL(fromFile);
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		FileOutputStream fos = new FileOutputStream(toFile);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		rbc.close();
		return new File(toFile);
	}

}