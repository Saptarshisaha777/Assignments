package com.assignment.DocIngest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class DocIngestApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocIngestApplication.class, args);
	}

}

//echo "# Assignments" >> README.md
//git init
//git add README.md
//gicommit"
//git branch -M main
//git remote add origin https://github.com/Saptarshisaha777/Assignments.git
//git push -u origin main
