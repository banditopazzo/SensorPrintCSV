package banditopazzo.sensorprintcsv;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private String TAG = "SensorEvent";

    private SensorManager SM;
    private Sensor accelerometer;
    private TextView status;
    private boolean running = false;
    private CSVWriter writer;
    private String movement = "Not moving";
    private TextView movementDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get status view
        status = (TextView) findViewById(R.id.status);

        //Get movement view
        movementDisplay = (TextView) findViewById(R.id.movementDisplay);



        //Create sensor manager
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Accelerometer
        accelerometer = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    public void startStopRecording(View v){
        if (!running) {
            createWriter();
            SM.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            running = true;
            status.setText("Running");
        } else {
            SM.unregisterListener(this);
            running = false;
            status.setText("Stopped");
            try {
                writer.close();
            } catch (java.io.IOException e) {
                Log.d(TAG,"Errore apertura file");
            }

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String[] values = {
                Float.toString(event.values[0]),
                Float.toString(event.values[1]),
                Float.toString(event.values[2]),
                movement
        };
        writer.writeNext(values);
        Log.d(
                TAG,
                Float.toString(event.values[0]) + " " +
                Float.toString(event.values[1]) + " " +
                Float.toString(event.values[2]) + " " +
                movement
        );
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Not used
    }

    private void createWriter(){
        Date date=new Date();
        String time = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss", Locale.ITALY).format(date);

        //Setup File
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "AnalysisData-" + time + ".csv";
        String filePath = baseDir + File.separator + fileName;
        //Log.d(TAG, filePath);
        File f = new File(filePath );
        // File exist
        if(f.exists() && !f.isDirectory()){
            try {
                writer = new CSVWriter(new FileWriter(filePath , true));
            } catch (java.io.IOException e){
                Log.d(TAG, "Errore apertura file");
            }
        }
        else {
            try {
                writer = new CSVWriter(new FileWriter(filePath));
            } catch (java.io.IOException e){
                e.printStackTrace();
                Log.d(TAG, "Errore apertura file");
            }
        }
    }

    public void goFoward(View v){
        movement = "Foward";
        movementDisplay.setText("Foward");
    }

    public void goBackwards(View v){
        movement = "Backwards";
        movementDisplay.setText("Backwards");
    }

    public void goLeft(View v){
        movement = "Left";
        movementDisplay.setText("Left");
    }

    public void goRight(View v){
        movement = "Right";
        movementDisplay.setText("Right");
    }

    public void stopMoving(View v){
        movement = "Not moving";
        movementDisplay.setText("Not moving");
    }


}

