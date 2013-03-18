package com.example.stepdetector;

public class StepList {
	private Step[] step;
	private int nb,nbStep;
	private boolean start,lookingForMax;
	private float min, max, lastA;
	private long minTime, maxTime, lastTime;
	private static final int MAX_STEP_DURATION = 1000;
	private static final int MIN_DELTA_A = 1;
	private static final int MAX_DELTA_A = 20;
	
	public StepList(){
		nbStep=0;
		start=true;
		step= new Step[3];
		for(int i=0;i<3;i++){
			step[i]=new Step();
		}
	}
	private void addStep(long d, long pt, float v, float p) {
		if(nb<2){
			step[nb].setStepParams(d, pt, v, p);
			nb++;
		}
		else{
			step[0]=step[1];
			step[1]=step[2];
			step[2].setStepParams(d, pt, v, p);
		}
		nbStep++;
	}
	Step getLastStep(){
		if(nb!=0)
			return step[nb];
		else {
			return null;
		}
	}
	void addPoint(float point, long time) {
		if(start){//it's a minimum
			min=point;
			minTime=time;
			lastA=point;
			start=!start;
			lookingForMax=false;
		}
		else{
			if(lookingForMax){
				if(point<lastA&&lastA-min>MIN_DELTA_A&&lastA-min<MAX_DELTA_A){
					max=lastA;
					maxTime=lastTime;
				}
			}
			else{//looking for min
				if(point>lastA&&minTime<maxTime&&maxTime<lastTime&&lastTime-minTime<MAX_STEP_DURATION&&max-lastA>MIN_DELTA_A&&max-lastA<MAX_DELTA_A){//calibration here! (maybe add a minimum threshold for time-mintime
					addStep(lastTime-minTime, maxTime, min, max);
					min=lastA;
					minTime=lastTime;
				}
			}
		}
		lookingForMax=!lookingForMax;
		lastTime=time;
		lastA=point;
		if(lastTime-minTime>MAX_STEP_DURATION){
			start=true;
		}
	}
	public String getString() {
		return "\nmin time:"+String.valueOf(minTime)+"\n" +
				"max time:"+String.valueOf(maxTime)+"\n" +
				"min:"+String.valueOf(min)+"\n" +
				"max:"+String.valueOf(max)+"\n" ;

	}
	public int getNbStep() {
		return nbStep;
	}
}