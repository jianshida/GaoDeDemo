package cn.cloudtop.gaodedemo.model;

import com.amap.api.services.core.LatLonPoint;

import java.io.Serializable;

public class PositionModel implements Serializable  {
	private String Address;
	private LatLonPoint latLonPoint ;
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public LatLonPoint getLatLonPoint() {
		return latLonPoint;
	}
	public void setLatLonPoint(LatLonPoint latLonPoint) {
		this.latLonPoint = latLonPoint;
	}

}
