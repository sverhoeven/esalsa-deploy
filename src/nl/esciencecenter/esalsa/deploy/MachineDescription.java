package nl.esciencecenter.esalsa.deploy;

import java.io.File;
import java.util.HashMap;

import nl.esciencecenter.esalsa.util.FileDescription;
import nl.esciencecenter.esalsa.util.Utils;

public class MachineDescription {

	public final String name;
	
	public final ResourceDescription host;
	//public final ResourceDescription scheduler;
	public final ResourceDescription files;	
	public final ResourceDescription gateway;
	
	//public final int slots;
	
	public final String inputDir;
	public final String templateDir;
	
	public String outputDir;
	public String experimentDir;
	
	public final FileDescription startScript;
	public final FileDescription stopScript;
	public final FileDescription monitorScript;
	//public final FileDescription configTemplate;
	
	private ConfigurationTemplate template;
	
	public MachineDescription(String name, 
			ResourceDescription host, 
			//ResourceDescription scheduler, 
			ResourceDescription gateway, 
			ResourceDescription files, 			
			//int slots, 
			String inputDir, String outputDir, 
			String experimentDir, String templateDir) {
		
		super();
		
		this.name = name;
		this.host = host;
		//this.scheduler = scheduler;
		this.gateway = gateway;
		this.files = files;
		//this.slots = slots;
		
		this.inputDir = inputDir; // createFileDescription(inputDir, files, gateway);
		this.outputDir = outputDir; // createFileDescription(outputDir, files, gateway);
		this.experimentDir = experimentDir; // createFileDescription(experimentDir, files, gateway);
		this.templateDir = templateDir; // createFileDescription(templateDir, files, gateway);
		
		this.startScript = templateDir + File.separator + "start"; //   Utils.getSubFile(this.templateDir, "start");
		this.stopScript = templateDir + File.separator + "stop"; // Utils.getSubFile(this.templateDir, "stop");
		this.monitorScript = templateDir + File.separator + "monitor"; //Utils.getSubFile(this.templateDir, "monitor");
		//this.configTemplate = Utils.getSubFile(this.templateDir, "pop_in_template");
	}
	
	private static FileDescription createFileDescription(String path, ResourceDescription files, ResourceDescription gateway) { 
		String completeURI = files.URI + File.separator + path;
		ResourceDescription tmp = new ResourceDescription(completeURI, files.username, files.userkey, files.adaptors);
		return new FileDescription(tmp, gateway);
	}
	
	public String toString() { 
		return "Machine(" + name + ", base=" + host + /*" scheduler=" + scheduler + */", files=" + files + ", gateway=" + gateway 
				/*", slots=" + slots*/ + ", inputDir=" + inputDir + ", outputDir=" + outputDir + ", experimentDir=" + experimentDir + ", templateDir=" + templateDir;  
	}

	public void addTemplate(ConfigurationTemplate t) {
		template = t;
	}
	
	public String generateConfiguration(HashMap<String, String> variables) throws Exception {
		return template.generate(variables);
	}

	public void setExperimentDir(FileDescription experimentDir) {
		this.experimentDir = experimentDir; 
	}

	public void setOutputDir(FileDescription outputDir) {
		this.outputDir = outputDir;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MachineDescription other = (MachineDescription) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}

