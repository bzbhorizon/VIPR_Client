package bzb.android.vipr;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ZoomControls;

public class VIPR extends Activity implements SensorEventListener {
    private SensorManager sensorManager;

	private Socket sock;
	private BufferedOutputStream out;
    
    private TextView tvx;
    private TextView tvy;
    private TextView tvz;
    
    private Button turnLeft;
    private Button turnRight;
    private Button select;
    private Button clutch;
    
    private ZoomControls zoom;
    
    private boolean holding = true;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        holding = true;
        
        new Thread(new Runnable(){
			public void run () {
				try {
					sock = new Socket (Config.ip, Config.port);
					out = new BufferedOutputStream(sock.getOutputStream());
					
					sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
					List<Sensor> sensors = sensorManager.getSensorList( Sensor.TYPE_ORIENTATION );
					Sensor sensor = sensors.get(0);
					sensorManager.registerListener(
					            VIPR.this,
					            sensor,
					            SensorManager.SENSOR_DELAY_UI );
					Log.i(getClass().getName(),"Registered listener for sensor " + sensor.getName());
					
					sendMessage("e");
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
        
		tvx = (TextView) findViewById(R.id.x);
		tvy = (TextView) findViewById(R.id.y);
		tvz = (TextView) findViewById(R.id.z);
		
		clutch = (Button) findViewById(R.id.clutch);
		clutch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switchClutch();
			}
		});
		turnLeft = (Button) findViewById(R.id.r0);
		turnLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				clutchOn();
				sendMessage("r1");
			}
		});
		turnRight = (Button) findViewById(R.id.r1);
		turnRight.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				clutchOn();
				sendMessage("r0");
			}
		});
		select = (Button) findViewById(R.id.c);
		select.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				clutchOn();
				sendMessage("c");
			}
		});
		
		zoom = (ZoomControls) findViewById(R.id.zoom);
		zoom.setOnZoomInClickListener(new OnClickListener() {
			public void onClick(View v) {
				clutchOn();
				sendMessage("z0");
			}
		});
		zoom.setOnZoomOutClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clutchOn();
				sendMessage("z1");
			}
		});
    }
    
    private void switchClutch () {
    	if (holding) {
			clutchOff();
		} else {
			clutchOn();
		}
    }
    
    private void clutchOn () {
    	if (!holding) {
	    	holding = true;
			sendMessage("h1");
			clutch.setText("Start moving");
    	}
    }
    
    private void clutchOff () {
    	if (holding) {
	    	holding = false;
			sendMessage("h0");
			clutch.setText("Stop moving");
    	}
    }
    
	@Override
	protected void onStop() {
		sendMessage("e");
		
		if (sensorManager != null) {
			sensorManager.unregisterListener(this);
		}
		
		try {
			if (out != null) {
				out.close();
			}
			if (sock != null) {
				sock.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Log.i(getClass().getName(), "Stopped");
		super.onStop();
	}

	@Override
	public void onAccuracyChanged(Sensor s, int arg1) {}

	@Override
	public void onSensorChanged(SensorEvent se) {
		if (!holding) {
			sendMessage(se.values[2] + "," + se.values[1]);
			tvx.setText("x:" + se.values[0]);
			tvy.setText("y:" + se.values[1]);
			tvz.setText("z:" + se.values[2]);
		} else {
			tvx.setText("Holding position");
			tvy.setText("Holding position");
			tvz.setText("Holding position");
		}
	}
	
	private void sendMessage (String message) {
		if (out != null) {
			try {
				out.write((message + "/").getBytes());
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}