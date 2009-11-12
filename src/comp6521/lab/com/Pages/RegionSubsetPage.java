/**
 * November 08, 2009
 * RegionSubsetPage.java: page to hold intermediate region tuples in query execution.
 */
package comp6521.lab.com.Pages;

import comp6521.lab.com.Records.RegionSubsetRecord;

/**
 * @author Dimitri Tiago
 */
public class RegionSubsetPage extends Page<RegionSubsetRecord>
{
	// no-argument constructor
	public RegionSubsetPage()
	{
		super();
		super.m_records = super.CreateArray(100);
	}
	
	public RegionSubsetRecord[] CreateArray(int n){ return new RegionSubsetRecord[n]; }
	public RegionSubsetRecord   CreateElement(){ return new RegionSubsetRecord(); }
}
