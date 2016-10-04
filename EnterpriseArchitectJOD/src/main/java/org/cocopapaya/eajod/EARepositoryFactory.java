package org.cocopapaya.eajod;

import java.util.HashMap;
import java.util.Map;

import org.sparx.Repository;

public class EARepositoryFactory {

	
	public static EARepositoryFactory INSTANCE;
	
	private Repository repo; 	
	private String eapFileLocation;
	private static Map<String,EARepositoryFactory> INSTANCES = new HashMap<>();
	
	public static void registerEapFile(String eapFileLocation){
		INSTANCES.put( "DEFAULT", new EARepositoryFactory (eapFileLocation));
	}
	
	public static EARepositoryFactory getInstance(){
			
		return INSTANCES.get("DEFAULT");
	}
	
	private EARepositoryFactory(String eapFileLocation) {	
		this.eapFileLocation = eapFileLocation;
		

	}
	
	private void openRepository(){
		
		System.out.println("Opening eap file: "+this.eapFileLocation);
		
		repo = new Repository();	
		repo.OpenFile(this.eapFileLocation);
		
		Runtime.getRuntime().addShutdownHook(new Thread( new Runnable() {
			
			@Override
			public void run() {
				System.out.println("Requesting repository to close file.");
				repo.CloseFile();
				repo.Exit();
			}
		}));
	}
	
	public Repository getRepository(){
		
		if( repo == null){
			openRepository();
		}
		
		return repo;
	}

}
