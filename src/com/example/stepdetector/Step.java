package com.example.stepdetector;

public class Step {
	private long duration,peakTime;
	private float valley,peak;
	private boolean step;
	
	
	public Step() {
		// TODO Auto-generated constructor stub
	}
	public void setStepParams(long d, long pt, float v, float p) {
		this.peakTime=pt;
		this.duration=d;
		this.valley=v;
		this.peak=p;
	}
	public void setStep(boolean step) {
		this.step = step;
	}
	public boolean getStep() {
		return step;
	}
	public float getPeak() {
		return peak;
	}
}
