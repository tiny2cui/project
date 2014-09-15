package com.simit.net.domain;


public class Location {
	private int degree;
	private int minute;
	private float second;

	public Location(int  degree, int minute ,float second ) {
		this.degree=degree;
		this.minute=minute;
		this.second=second;
		
}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public float getSecond() {
		return second;
	}

	public void setSecond(float second) {
		this.second = second;
	}
}
