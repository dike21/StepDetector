package com.example.stepdetector;

public class StepList {
	private Step[] step;
	private int nb,nbStep;
	private boolean start;
	private float min, max;
	private long minTime, maxTime;

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
	void addPoint(float point, long time, boolean maxBoolean) {
		// TODO Auto-generated method stub
		if(start){//it's a minimum
			min=point;
			minTime=time;
			start=!start;
		}
		else{
			if(maxBoolean){
				if(point-min>0.2&&point-min<20){
					max=point;
					maxTime=time;
				}
				else{
					System.out.println("point:"+String.valueOf(point)+"\nmin:"+String.valueOf(min)+"\n");
				}
			}
			else{
				System.out.println("min time:"+String.valueOf(minTime)+"\n" +
						"max time:"+String.valueOf(maxTime)+"\n" +
						"time:"+String.valueOf(time)+"\n" +
						"min:"+String.valueOf(min)+"\n" +
						"max:"+String.valueOf(max)+"\n" +
						"point:"+String.valueOf(point)+"\n\n");
				if(minTime<maxTime&&maxTime<time&&time-minTime<1200&&max-point>2&&max-point<20){//calibration here! (maybe add a minimum threshold for time-mintime
					addStep(time-minTime, maxTime, min, max);
				}
				min=point;
				minTime=time;
			}
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
