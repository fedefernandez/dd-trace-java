package com.datadoghq.profiling.controller;

import com.datadoghq.profiling.controller.openjdk.OpenJdkController;

/**
 * This is the factory used to get a controller.
 * 
 * @author Marcus Hirt
 */
public final class ControllerFactory {
		
	public Controller createController() throws UnsupportedEnvironmentException {
		try {
			Class.forName("com.oracle.jrockit.jfr.Producer");
			throw new UnsupportedEnvironmentException("The JFR controller is currently not supported on the Oracle JDK <= JDK 11!");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			Class.forName("jdk.jfr.Event");
		} catch (ClassNotFoundException e) {
			throw new UnsupportedEnvironmentException("The JFR controller could not find a supported JFR API");
		}
		return new OpenJdkController();
	}
}
