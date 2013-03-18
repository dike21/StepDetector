package com.example.stepdetector;

public class StepList {
	private Step[] step;
	private int nb,nbStep;
	private boolean start,lookingForMax;
	private float min, max, lastA;
	private long minTime, maxTime, lastTime;

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
	private Step getLastStep(){
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
				if(point<lastA&&lastA-min>1.5&&lastA-min<20){
					max=lastA;
					maxTime=lastTime;
				}
			}
			else{//looking for min
				if(point>lastA&&minTime<maxTime&&maxTime<lastTime&&lastTime-minTime<1000&&max-lastA>1.5&&max-lastA<20){//calibration here! (maybe add a minimum threshold for time-mintime
					addStep(lastTime-minTime, maxTime, min, max);
					min=lastA;
					minTime=lastTime;
				}
			}
		}
		lookingForMax=!lookingForMax;
		lastTime=time;
		lastA=point;
		if(lastTime-minTime>1000){
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