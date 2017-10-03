package org.rda.QueryStore.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Helper {
	private static final String CHARACTER_SEPARATOR=" ; ";
	
	private static final Helper instance = new Helper();

	public static Helper getInstance() {
		return instance;
	}

	private Helper() {
	}

	public String encodeParametersMap(Map<String, String> parameters) {
		String toReturn = "";

		List<String> parameterNames = new ArrayList<String>(parameters.keySet());
		Collections.sort(parameterNames);
		String parameterName;
		for (int i = 0; i < parameterNames.size(); i++) {
			parameterName = parameterNames.get(i);
			toReturn = toReturn + parameterName + "="
					+ parameters.get(parameterName);
			if(i<parameterNames.size()-1){
				toReturn = toReturn + CHARACTER_SEPARATOR;
			}
		}

		return toReturn;
	}
	
	public List<String> decodeParam(String toDecode){
		List<String> toReturn = new ArrayList<String>();
		
		String [] content  = toDecode.split(CHARACTER_SEPARATOR);
		for(int i=0; i< content.length; i++){
			toReturn.add(content[i].trim());
		}
		return toReturn;
	}
	
	
	
}
