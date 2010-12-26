import java.io.File;


public class JAndroid2Cloud {
    public static void main(String[] args) {
	final File configFile = new File("config.properties");
	final Configuration configuration = Configuration.getConfiguration(configFile);
	Runtime.getRuntime().addShutdownHook(new Thread() {
	    @Override
	    public void run() {
		configuration.saveConfiguration(configFile);
	    }
	});
	Android2CloudServerConnection connection = new Android2CloudServerConnection(configuration);
	System.out.println(connection.getLink());
    }
}
