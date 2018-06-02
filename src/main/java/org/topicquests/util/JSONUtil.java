/*
 * Copyright 2014, 2018 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
/**
 * @author jackpark
 *
 */
public class JSONUtil {

	/**
	 * 
	 */
	public JSONUtil() {
	}

	/**
	 * Does not return <code>null</code>
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public JSONObject load(String filePath) throws Exception {
		JSONObject result = null;
		File myFile = new File(filePath);
		if (myFile.exists()) {
			InputStream is = new GZIPInputStream(new FileInputStream(myFile));
			BufferedReader rdr = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			String json = rdr.readLine();
			rdr.close();
			JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
			try {
				result = (JSONObject)parser.parse(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			result = new JSONObject();
		return result;
	}
	
	/**
	 * Save <code>jo</code> to a file
	 * @param filePath
	 * @param jo
	 * @throws Exception
	 */
	public void save(String filePath, JSONObject jo) throws Exception {
		File myFile = new File(filePath);
		FileOutputStream fos = new FileOutputStream(myFile);
		PrintWriter out = new PrintWriter(fos);
		out.println(jo.toJSONString());
		out.flush();
		out.close();
	}
}
