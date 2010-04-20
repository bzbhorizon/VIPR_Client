package bzb.android.vipr;

import java.io.DataOutputStream;
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
import android.widget.TextView;

public class VIPR extends Activity implements SensorEventListener {
    private SensorManager sensorManager;

    private TextView tvx;
    private TextView tvy;
    private TextView tvz;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        try {
			Socket connfd = new Socket (Config.ip, Config.port);
			DataOutputStream out = new DataOutputStream(connfd.getOutputStream()); 
        
	        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
			List<Sensor> sensors = sensorManager.getSensorList( Sensor.TYPE_ORIENTATION );
			Sensor sensor = sensors.get(0);
			sensorManager.registerListener(
			            this,
			            sensor,
			            SensorManager.SENSOR_DELAY_UI );
			Log.i(getClass().getName(),"Registered listener for sensor " + sensor.getName());
        } catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		tvx = (TextView) findViewById(R.id.x);
		tvy = (TextView) findViewById(R.id.y);
		tvz = (TextView) findViewById(R.id.z);
    }
    
	@Override
	protected void onStop() {
		sensorManager.unregisterListener(this);
		Log.i(getClass().getName(), "Stopped");
		super.onStop();
	}

	@Override
	public void onAccuracyChanged(Sensor s, int arg1) {}

	@Override
	public void onSensorChanged(SensorEvent se) {
		tvx.setText("x:" + se.values[0]);
		tvy.setText("y:" + se.values[1]);
		tvz.setText("z:" + se.values[2]);
	}
	
}