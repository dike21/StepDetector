package com.example.stepdetector;

public class StepList {
	private Step[] step;
	private int nb,nbStep;
	private boolean lookingForMax;
	private float min, max, lastA;
	private long minTime, maxTime, lastTime;
	private static final int MAX_STEP_DURATION = 1000;
	private static final float MIN_DELTA_A = 0.5f;
	private static final int MAX_DELTA_A = 20;
	private static final int MIN_STEP_DURATION = 400;

	String pts;

	public StepList(){
		nbStep=0;
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
		if(lookingForMax){
			if(point<lastA&&lastA-min>MIN_DELTA_A&&lastA-min<MAX_DELTA_A){
				if(nbStep!=0){
					if(lastTime-getLastStep().getPeakTime()>MIN_STEP_DURATION){
						max=lastA;
						maxTime=lastTime;
					}
				}
				else{
					max=lastA;
					maxTime=lastTime;
				}
				lookingForMax=!lookingForMax;
				MainActivity.peakTimeString((float)(lastTime-MainActivity.getBeginning())/1000f,lastA);
			}
		}
		else{//looking for min
			if(point>lastA&&minTime<maxTime&&maxTime<lastTime&&lastTime-minTime<MAX_STEP_DURATION&&max-lastA>MIN_DELTA_A&&max-lastA<MAX_DELTA_A){//calibration here! (maybe add a minimum threshold for time-mintime
				addStep(lastTime-minTime, maxTime, min, max);
				MainActivity.peakTimeString((float)(lastTime-MainActivity.getBeginning())/1000f,lastA);
				min=lastA;
				minTime=lastTime;
				lookingForMax=!lookingForMax;
			}
			else if(point>lastA){
				min=lastA;
				minTime=lastTime;
				lookingForMax=!lookingForMax;
			}
		}
		lastTime=time;
		lastA=point;
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
	public void peakTimeString(float a){
		pts += String.valueOf(a)+"\n";
	}
}