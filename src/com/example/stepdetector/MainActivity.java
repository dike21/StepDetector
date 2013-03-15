package com.example.stepdetector;

import android.app.Activity; 
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;
//import java.util.Timer;
import com.example.stepdetector.R;

public class MainActivity extends Activity implements SensorEventListener {
	private float A;
	private TextView textView;
	private Button button;
	private SensorManager mSensorManager = null;
	private StepList stepList;
	// accelerometer vector
	static final float ALPHA = 0.35f;
	//private Timer fuseTimer = new Timer();
	Sensor accelerometer;//, linear_accelerometer;
	float lastA,min,max;
	float accelFilter[] = new float[3];
	double temp;
	boolean lookingForMin,pause;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set full screen view
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_activity);

		lookingForMin=true;//we start trying to find a minimum
		stepList=new StepList();
		textView = (TextView)findViewById(R.id.textview);
		button = (Button)findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pause=!pause;
			}
		});

		// get sensorManager and sensors
		mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);

		List<Sensor> mySensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (mySensors.size() > 0)  accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

	}

	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			onAccelerometerChanged(event.values[0], event.values[1], event.values[2]);
			break;
		}
	}

	public void onAccelerometerChanged(float accelX, float accelY, float accelZ) {
		// low pass filter
		accelFilter[0] = accelFilter[0]+(ALPHA * (accelX - accelFilter[0]));
		accelFilter[1] = accelFilter[1]+(ALPHA * (accelY - accelFilter[1]));
		accelFilter[2] = accelFilter[2]+(ALPHA * (accelZ - accelFilter[2]));

		//without filter
		/*accelFilter[0] = accelX;
		accelFilter[1] = accelY;
		accelFilter[2] = accelZ;*/

		onFilteredAccelerometerChanged(accelFilter[0], accelFilter[1], accelFilter[2]);
		lastA=accelFilter[0]*accelFilter[0]+accelFilter[1]*accelFilter[1]+accelFilter[2]*accelFilter[2];
		temp =(double) lastA;
		lastA = (float)Math.sqrt(temp);
	}
	void onFilteredAccelerometerChanged(float x, float y, float z){
		A=(x * x + y * y + z * z);//acc 3D
		temp =(double) A;
		A = (float) Math.sqrt(temp);

		if(lookingForMin){
			if(A>lastA){
				min=lastA;
				stepList.addPoint(min, System.currentTimeMillis(), false);
			}
		}
		else{//looking for max
			if(A<lastA){
				max=lastA;
				stepList.addPoint(max, System.currentTimeMillis(), true);
			}
		}
		if(!pause)
			textView.setText(String.valueOf(stepList.getNbStep())+stepList.getString()+"\ncurrent time:"+String.valueOf(System.currentTimeMillis())+"\nLast A: "+String.valueOf(lastA));
		lookingForMin=!lookingForMin;
	}
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	protected void onResume() {
		super.onResume();
		// start the sensor listeners 
		if ( accelerometer != null) mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	protected void onPause() {
		// unregister listener
		super.onPause();
		// unregister sensor listeners to prevent the activity from draining the device's battery.
		mSensorManager.unregisterListener(this);		
	}

	@Override
	protected void onStop() {
		super.onStop();
		// unregister sensor listeners to prevent the activity from draining the device's battery.
		mSensorManager.unregisterListener(this);
		// The TimerTask is still running
	}

	@Override
	protected void onDestroy() { 
		super.onDestroy();
		// unregister sensor listeners to prevent the activity from draining the device's battery.
		mSensorManager.unregisterListener(this);
		// Remove the TimerTask from the list
		//fuseTimer.cancel();
		finish();
	}
} 