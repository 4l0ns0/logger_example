package com.example.app.logger;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Settings implements Serializable {

	private static final long serialVersionUID = 2423395342872760704L;

	private String user;
	private String password;
	private String dbms;
	private String host;
	private String port;
	private String scheme;
	
}
