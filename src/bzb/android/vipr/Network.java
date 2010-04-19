package bzb.android.vipr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Network extends Activity {

	private Button button;
	private EditText field;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.ip);
	    
	    field = (EditText) findViewById(R.id.field);
	    
	    button = (Button) findViewById(R.id.button);
	    button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Config.ip = field.getText().toString();
            	startActivity(new Intent(Network.this, VIPR.class));
            }
        });
	}
	
	@Override
	protected void onStop() {
		Log.i(getClass().getName(), "Stopped");
		super.onStop();
	}

}