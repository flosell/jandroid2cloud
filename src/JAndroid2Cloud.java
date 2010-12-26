import java.io.File;
import java.util.Date;


public class JAndroid2Cloud {
    private static final File CONFIG_FILE = new File(System.getProperty("user.home")+"/.jandroid2cloud.properties");
    public static void main(String[] args) {
	final Configuration configuration = Configuration.getConfiguration(CONFIG_FILE);
	Runtime.getRuntime().addShutdownHook(new Thread() {
	    @Override
	    public void run() {
		configuration.saveConfiguration(CONFIG_FILE);
		System.out.println("saved configuration");
	    }
	});
	
	Android2CloudServerConnection connection = new Android2CloudServerConnection(configuration);
	while (true) {
	    String link = connection.getLink();
	    if (!link.equals(configuration.getOldlink())) {
		configuration.openURLinBrowser(link);
		System.out.println((new Date()).toString()+" found new link:"+link);
		configuration.setOldlink(link);
		configuration.saveConfiguration(CONFIG_FILE); // TODO: only for development
	    } else {
		System.out.println((new Date()).toString()+" nothing new...");
	    }
	    try {
		Thread.sleep(15000);
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }
}
