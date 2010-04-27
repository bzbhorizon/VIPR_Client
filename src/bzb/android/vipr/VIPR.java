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
    
    private ZoomControls zoom;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
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
		
		turnLeft = (Button) findViewById(R.id.r0);
		turnLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendMessage("r0");
			}
		});
		turnRight = (Button) findViewById(R.id.r1);
		turnRight.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendMessage("r1");
			}
		});
		select = (Button) findViewById(R.id.c);
		select.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendMessage("c");
			}
		});
		
		zoom = (ZoomControls) findViewById(R.id.zoom);
		zoom.setOnZoomInClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendMessage("z0");
			}
		});
		zoom.setOnZoomOutClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMessage("z1");
			}
		});
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
		sendMessage(se.values[2] + "," + se.values[1] + "/");
		tvx.setText("x:" + se.values[0]);
		tvy.setText("y:" + se.values[1]);
		tvz.setText("z:" + se.values[2]);
	}
	
	private void sendMessage (String message) {
		if (out != null) {
			try {
				out.write(message.getBytes());
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}