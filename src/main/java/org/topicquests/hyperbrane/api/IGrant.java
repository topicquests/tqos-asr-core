/**
 * 
 */
package org.topicquests.hyperbrane.api;

/**
 * @author jackpark
 * Interface for grants typically in PubMed docs
 * <Grant>
 * 	<GrantID>MC_U117533887</GrantID>
 * 	<Agency>Medical Research Council</Agency>
 * 	<Country>United Kingdom</Country>
 * </Grant>
 */
public interface IGrant {
	public static String
		GRANT_ID	= "gId",
		AGENCY		= "agncy",
		COUNTRY		= "cntry";
	
	void setGrantId(String id);
	String getGrantId();
	
	void setAgency(String agency);
	String getAgency();
	
	void setCountry(String country);
	String getCountry();
	
}