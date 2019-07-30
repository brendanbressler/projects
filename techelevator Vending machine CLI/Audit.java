package com.techelevator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Audit {

	File fileOfAuditing = new File("audit.txt");
	PrintWriter auditWriter;
	
	VendingMachine machineOfVending = new VendingMachine();
	Scanner scannerOfAudit;
	public Audit(File audit ) {
		
	}
	public void printAudit() throws FileNotFoundException {
		
		try {
			auditWriter = new PrintWriter(fileOfAuditing);
			scannerOfAudit = new Scanner("audit.txt");
				
				
			while (scannerOfAudit.hasNextLine()) {
				String line = scannerOfAudit.nextLine();
				auditWriter.println(line);
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found dumbo!");
		}
	
	}
	
}

