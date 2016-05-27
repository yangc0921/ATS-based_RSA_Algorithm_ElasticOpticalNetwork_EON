package eon.general;

import eon.network.Route;

/**
 * @restructured by vxFury
 *
 */
public class Time {
	String name;
	double time;
	int keytime;
	Route linearroute;
	int route_index;
	int occpyspectrum = Constant.MAXIUM;

	public Time(String name, double time, int keytime, Route linearroute) {
		this.name = name;
		this.time = time;
		this.keytime = keytime;
		this.linearroute = linearroute;
	}

	public int getOccpySpectrum() {
		return occpyspectrum;
	}

	public void setOccpySpectrum(int occpyspectrum) {
		this.occpyspectrum = occpyspectrum;
	}

	public int getRouteIndex() {
		return route_index;
	}

	public void setRouteIndex(int route_index) {
		this.route_index = route_index;
	}

	public Route getRoute() {
		return linearroute;
	}

	public void setRoute(Route linearroute) {
		this.linearroute = linearroute;
	}

	public int getKeyTime() {
		return keytime;
	}

	public void setKeyTime(int keytime) {
		this.keytime = keytime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}
}
