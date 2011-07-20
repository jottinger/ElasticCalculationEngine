package org.openspaces.calcengine.common;

import java.io.Serializable;

import com.gigaspaces.annotation.pojo.*;

@SpaceClass (persist=true)
public class Trade implements Serializable{

	public Trade (){}
	private Integer id;
	private Double NPV;
	private CacheFlowData cacheFlowData;
	
	@SpaceRouting
	@SpaceId (autoGenerate = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Double getNPV(){
		return this.NPV;
	}
	
	public String getBook(){
		return "Book" + this.getId() % 4;
	}

	public void setNPV(Double nPV) {
		NPV = nPV;
	}

	public CacheFlowData getCacheFlowData() {
		if (cacheFlowData == null )
			return new CacheFlowData ();
		
		return cacheFlowData;
	}

	public void setCacheFlowData(CacheFlowData cacheFlowData) {
		this.cacheFlowData = cacheFlowData;
	}


}