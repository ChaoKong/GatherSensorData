package com.example.gathersensordata;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Button;
import android.widget.TextView;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends Activity implements
ConnectionCallbacks, OnConnectionFailedListener,LocationListener{
	
	private static final String TAG = "IO";
	private static final String TAG2 = "IO_Output";
	
    private Thread recordingThread = null;
    private Thread emittingThread = null;
    private boolean isRecording = false;
    private boolean isEmitting = false;

    int sampleRate = 44100;
    
    int wifiEnableFlag = 0;

	Handler handler = new Handler();
	
	
	AudioManager	am	= null;
	AudioRecord record =null;
	AudioTrack track =null;
	
	WifiManager wifiManager;
//	WifiReceiver receiverWifi;
	List<ScanResult> wifiList;
	
	WifiInfo wifiInfo = null; 
	
	
	
	File SoundFile = null;
	FileOutputStream foutSound = null;
	OutputStreamWriter outwriterSound = null;
	
	File SoundDataFile = null;
	FileOutputStream foutSoundData = null;
	OutputStreamWriter outwriterSoundData = null;
	
	File EmitFile = null;
	FileOutputStream foutEmit = null;
	OutputStreamWriter outwriterEmit= null;
	
	EditText ChirpInput = null;
	MediaPlayer mediaPlayer = null;
	
	
	TextView textLIGHT_reading;
	TextView textMagne_reading;
	TextView textTemper_reading;
	TextView textPressure_reading;
	TextView textAccele_reading;
	TextView textProxi_reading;
	TextView textSignal_reading;
	TextView textLocation_reading;
	TextView textGyro_reading;
	TextView textWifi_reading;
	TextView textWifi_available;
	TextView textRGB_reading;
	
	
	SensorManager mySensorManager = null;
	Sensor LightSensor = null;
	Sensor MagneSensor = null;
	Sensor PressureSensor = null;
	Sensor TemperSensor = null;
	Sensor AcceleSensor = null;
	Sensor ProxiSensor = null;
	Sensor GyroSensor = null;
	
	File LightFile = null;
	FileOutputStream foutLight = null;
	OutputStreamWriter outwriterLight = null;
	
	File RGBFile = null;
	FileOutputStream foutRGB = null;
	OutputStreamWriter outwriterRGB = null;
	
	
	File MagneFile = null;
	FileOutputStream foutMagne = null;
	OutputStreamWriter outwriterMagne = null;
	
	File PressureFile = null;
	FileOutputStream foutPressure = null;
	OutputStreamWriter outwriterPressure = null;
	
	File TemperFile = null;
	FileOutputStream foutTemper = null;
	OutputStreamWriter outwriterTemper = null;
	
	File AcceleFile = null;
	FileOutputStream foutAccele = null;
	OutputStreamWriter outwriterAccele = null;
	
	File ProxiFile = null;
	FileOutputStream foutProxi = null;
	OutputStreamWriter outwriterProxi = null;
	
	File SignalFile = null;
	FileOutputStream foutSignal = null;
	OutputStreamWriter outwriterSignal = null;
	
	File SpeedFile = null;
	FileOutputStream foutSpeed = null;
	OutputStreamWriter outwriterSpeed = null;
	
	File GyroFile = null;
	FileOutputStream foutGyro= null;
	OutputStreamWriter outwriterGyro = null;
	
	File WifiFile = null;
	FileOutputStream foutWifi= null;
	OutputStreamWriter outwriterWifi = null;
	
	
	TelephonyManager Tel = null;
	MyPhoneStateListener    MyListener= null;
	
	GoogleApiClient mGoogleApiClient;
	LocationRequest mLocationRequest;
	Location mLocation;

	File root = null;
	File dir = null;
	Context context = null;
	
	

	BufferedReader bufferedReader = null;
	
	File InputRGBFile = null;
	
	int RGBAvailabe = 0;
	
			

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
		root = android.os.Environment.getExternalStorageDirectory();

		dir = new File (root.getAbsolutePath() + "/IOdetection");
		dir.mkdirs();
		context = this;
		
		InputRGBFile = new File("/sys/devices/virtual/sensors/light_sensor/raw_data");
		
		RGBAvailabe = 0;
		if (InputRGBFile.exists())
		{
			
			try {
				bufferedReader = new BufferedReader(new FileReader(InputRGBFile));
				RGBAvailabe = 1;
			} catch (FileNotFoundException e2) {
				// TODO Auto-generated catch block
				RGBAvailabe = 0;
				//Log.d("try to read RGB, unavailable", "try to read RGB, unavailable");
				e2.printStackTrace();
				
			}
						
			//Log.d("RGB available", String.valueOf(RGBAvailabe));
			
		}
		
		init();
		setButtonHandlers();
        enableButtons(false);
        enableButton(R.id.emitStop, false);
        
        //textLIGHT_available = (TextView)findViewById(R.id.LIGHT_available);
        textLIGHT_reading = (TextView)findViewById(R.id.LIGHT_reading);
        //textMagne_available = (TextView)findViewById(R.id.Magne_available);
        textMagne_reading = (TextView)findViewById(R.id.Magne_reading);
        //textTemper_available = (TextView)findViewById(R.id.Temper_available);
        textTemper_reading = (TextView)findViewById(R.id.Temper_reading);
        //textPressure_available = (TextView)findViewById(R.id.Pressure_available);
        textPressure_reading = (TextView)findViewById(R.id.Pressure_reading);
        //textAccele_available = (TextView)findViewById(R.id.Accele_available);
        textAccele_reading = (TextView)findViewById(R.id.Accele_reading);
        //textProxi_available = (TextView)findViewById(R.id.Proxi_available);
        textProxi_reading = (TextView)findViewById(R.id.Proxi_reading);
        
        textSignal_reading = (TextView)findViewById(R.id.Signal_reading);
        textLocation_reading = (TextView)findViewById(R.id.Location_reading);
        textGyro_reading = (TextView)findViewById(R.id.Gyro_reading);
        textWifi_reading  = (TextView)findViewById(R.id.Wifi_reading);
        textWifi_available = (TextView)findViewById(R.id.Wifi_avaiable);
        
        textRGB_reading = (TextView)findViewById(R.id.RGB_reading);
        
        
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        
        mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        
        LightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        
        if(LightSensor != null){
        	//textLIGHT_available.setText("Sensor LIGHT Available");
        	//mySensorManager.registerListener(LightSensorListener, LightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        	LightFile = new File(dir, "LightRecord.txt");
        	RGBFile = new File(dir,"RGB.txt");
        	if (!LightFile.exists()){
        		try {
					LightFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	if (!RGBFile.exists()){
        		try {
        			RGBFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }else{
       	}
        
        MagneSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        
        if(MagneSensor != null){
        	//textMagne_available.setText("Sensor MAGNETIC_FIELD Available");
        	//mySensorManager.registerListener(MagneSensorListener, MagneSensor, SensorManager.SENSOR_DELAY_NORMAL);
        	MagneFile = new File(dir, "MagneRecord.txt");
        	if (!MagneFile.exists()){
        		try {
        			MagneFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
      
        }else{
        	//textMagne_available.setText("Sensor MAGNETIC_FIELD NOT Available");
        	//textMagne_reading.setText("MAGNETIC(uT): ");
       	}
        
        PressureSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        
        if(PressureSensor != null){
        	//textPressure_available.setText("Sensor PRESSURE Available");
        	//mySensorManager.registerListener(PressureSensorListener, PressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        	PressureFile = new File(dir, "AltitudeRecord.txt");
        	if (!PressureFile.exists()){
        		try {
        			PressureFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
      
        }else{
        	//textPressure_available.setText("Sensor PRESSURE NOT Available");
        	//textPressure_reading.setText("Altitude(m): " );
       	}
        
        TemperSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        
        if(TemperSensor != null){
        	//textTemper_available.setText("Sensor AMBIENT_TEMPERATURE Available");
        	//mySensorManager.registerListener(TemperSensorListener, TemperSensor, SensorManager.SENSOR_DELAY_NORMAL);
        	TemperFile = new File(dir, "TemperatureRecord.txt");
        	if (!TemperFile.exists()){
        		try {
        			TemperFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
      
        }else{
        	//textTemper_available.setText("Sensor MBIENT_TEMPERATURE NOT Available");
        	//textTemper_reading.setText("Temperature(C): ");
       	}
        
        AcceleSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        if(AcceleSensor != null){
        	//textAccele_available.setText("Sensor ACCELEROMETER Available");
        	AcceleFile = new File(dir, "AcceleRecord.txt");
        	if (!AcceleFile.exists()){
        		try {
        			AcceleFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
      
        }else{
        	//textAccele_available.setText("Sensor ACCELEROMETER NOT Available");
        	//textTemper_reading.setText("Temperature(C): ");
       	}
        
        ProxiSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        
        if(ProxiSensor != null){
        	//textProxi_available.setText("Sensor PROXIMITY Available");
        	ProxiFile = new File(dir, "ProxiRecord.txt");
        	if (!ProxiFile.exists()){
        		try {
        			ProxiFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
      
        }else{
        	//textProxi_available.setText("Sensor PROXIMITY NOT Available");
        	//textTemper_reading.setText("Temperature(C): ");
       	}
        
        GyroSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        
        if(GyroSensor != null){
        	//textProxi_available.setText("Sensor PROXIMITY Available");
        	GyroFile = new File(dir, "GyroRecord.txt");
        	if (!GyroFile.exists()){
        		try {
        			GyroFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
      
        }else{
        	//textProxi_available.setText("Sensor PROXIMITY NOT Available");
        	//textTemper_reading.setText("Temperature(C): ");
       	}
        
        SignalFile = new File(dir,"SignalRecord.txt");
    	if (!SignalFile.exists()){
    		try {
    			SignalFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    	}
    	
        SpeedFile = new File(dir,"SpeedRecord.txt");
    	if (!SpeedFile.exists()){
    		try {
    			SpeedFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    	}
    	
    	
        WifiFile = new File(dir,"WifiRecord.txt");
    	if (!WifiFile.exists()){
    		try {
    			WifiFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    	}
    	
    
        
    	Tel = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);;
    	MyListener= new MyPhoneStateListener();
    	
    	// Create an instance of GoogleAPIClient.
    	if (mGoogleApiClient == null) {
    	    mGoogleApiClient = new GoogleApiClient.Builder(this)
    	        .addConnectionCallbacks((ConnectionCallbacks) this)
    	        .addOnConnectionFailedListener((OnConnectionFailedListener) this)
    	        .addApi(LocationServices.API)
    	        .build();
    	}
      
    }
    
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	protected void onPause() {
		super.onPause();
//		unregisterReceiver(receiverWifi);
	}

	protected void onResume() {
//		registerReceiver(receiverWifi, new IntentFilter(
//				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		super.onResume();
	}
	
	private void init() {
		
		ChirpInput = (EditText) findViewById(R.id.inputChirp);
    	
    	
		ChirpInput.setText(""+22);
		
		
		am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		//am.setMode(AudioManager.MODE_NORMAL);
		
		
		SoundFile = new File(dir, "SoundRecord.txt");
    	if (!SoundFile.exists()){
    		try {
    			SoundFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
		SoundDataFile = new File(dir, "SoundData.txt");
    	if (!SoundDataFile.exists()){
    		try {
    			SoundDataFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	
		EmitFile = new File(dir, "EmitRecord.txt");
    	if (!EmitFile.exists()){
    		try {
    			EmitFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}

		
	}

	private void StartRecord() {
		short[] lin = new short[1024];
		int num = 0;

		record.startRecording();
		//track.play();
		if(SoundFile.exists()){
			  
			  try{
	    		
				  foutSound = new FileOutputStream(SoundFile,true);
				  outwriterSound = new OutputStreamWriter(foutSound);
			
			  } catch(Exception e)
			  {

			  }
		}
		
		if(SoundDataFile.exists()){
			  
			  try{
	    		
				  foutSoundData = new FileOutputStream(SoundDataFile);
				  outwriterSoundData = new OutputStreamWriter(foutSoundData);
			
			  } catch(Exception e)
			  {

			  }
		}

		while (true) {
			num = record.read(lin, 0, 1024);
			//track.write(lin, 0, num);
	        if (num > 1000){
	            long curTime = System.currentTimeMillis();
	            String curTimeStr = ""+curTime+";   ";
	            Log.i(TAG,curTimeStr);
	            try {
	            	outwriterSound.append(curTimeStr);
	    		} catch (IOException e1) {
	    			// TODO Auto-generated catch block
	    			e1.printStackTrace();
	    		}
	        	String strI = ""+(lin.length);
	        	Log.i(TAG, strI);
        	
	        	for ( int i=0; i<lin.length;i++){
	        		try {
	        			String tempS = Short.toString(lin[i])+"    ";
	        			outwriterSound.append(tempS);
	        			outwriterSoundData.append(tempS);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
        		try {
        			String tempS = "\n";
        			outwriterSound.append(tempS);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        	
	        }
		}
		
	}
	
	
    private void startEmitting() {
    	
    	int numChirp =22;
    	String strChirpSelect = ChirpInput.getText().toString();
    	numChirp = Integer.parseInt(strChirpSelect);
    	switch (numChirp){
    		case 1: mediaPlayer = MediaPlayer.create(this, R.raw.chirp1);
    				break;
    		case 2: mediaPlayer = MediaPlayer.create(this, R.raw.chirp2);
    				break;
    		case 3: mediaPlayer = MediaPlayer.create(this, R.raw.chirp3);
					break;
    		case 4: mediaPlayer = MediaPlayer.create(this, R.raw.chirp4);
					break;
    		case 5: mediaPlayer = MediaPlayer.create(this, R.raw.chirp5);
    				break;
    		case 6: mediaPlayer = MediaPlayer.create(this, R.raw.chirp6);
    				break;
    		case 7: mediaPlayer = MediaPlayer.create(this, R.raw.chirp7);
    				break;
    		case 8: mediaPlayer = MediaPlayer.create(this, R.raw.chirp8);
    				break;
    		case 9: mediaPlayer = MediaPlayer.create(this, R.raw.chirp9);
					break;
    		case 11: mediaPlayer = MediaPlayer.create(this, R.raw.chirp11);
					break;
    		case 12: mediaPlayer = MediaPlayer.create(this, R.raw.chirp12);
					break;
    		case 13: mediaPlayer = MediaPlayer.create(this, R.raw.chirp13);
					break;
    		case 14: mediaPlayer = MediaPlayer.create(this, R.raw.chirp14);
					break;
    		case 17: mediaPlayer = MediaPlayer.create(this, R.raw.chirp17);
					break;
    		case 18: mediaPlayer = MediaPlayer.create(this, R.raw.chirp18);
					break;
    		case 19: mediaPlayer = MediaPlayer.create(this, R.raw.chirp19);
					break;
    		case 20: mediaPlayer = MediaPlayer.create(this, R.raw.chirp20);
					break;
    		case 21: mediaPlayer = MediaPlayer.create(this, R.raw.chirp21);
					break;
    		case 22: mediaPlayer = MediaPlayer.create(this, R.raw.chirp22);
					break;
    		default:
    				mediaPlayer = MediaPlayer.create(this, R.raw.chirp22);
    	}
    	if(EmitFile.exists()){
    		try {
    			foutEmit = new FileOutputStream(EmitFile,true);
    			outwriterEmit = new OutputStreamWriter(foutEmit);
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    		}
    	}
    	else{
    		try {
    			EmitFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		try {
    			foutEmit = new FileOutputStream(EmitFile,true);
    			outwriterEmit = new OutputStreamWriter(foutEmit);
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    		}
    		
    		
    	}
    	
    	long curTime = System.currentTimeMillis();
    	String curTimeStr = ""+curTime+":   ";
    	Log.i(TAG,curTimeStr);
    	try {
				outwriterEmit.append(curTimeStr+"    "+strChirpSelect+"\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	try {
    		outwriterEmit.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	mediaPlayer.start();
    }
    private void stopEmitting() {
        // stops the recording activity
//        if (null != track) {
//            isEmitting = false;
//            track.stop();
//            track.release();
//            track = null;
//           
//		
//            emittingThread = null;
//        }
    	mediaPlayer.pause();
    	mediaPlayer.release();
    	mediaPlayer = null;
    }
    
    
    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format
    
    private void startRecording() {

		int min = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		record = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT, min);
        record.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

        //convert short to byte
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }

    private void writeAudioDataToFile() {
        // Write the output audio in byte
        int num = 0;
        short sData[] = new short[BufferElements2Rec];

        
        try {
        	foutSound = new FileOutputStream(SoundFile,true);
        	outwriterSound = new OutputStreamWriter(foutSound);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
		if(SoundDataFile.exists()){
			  
			  try{
	    		
				  foutSoundData = new FileOutputStream(SoundDataFile);
				  outwriterSoundData = new OutputStreamWriter(foutSoundData);
			
			  } catch(Exception e)
			  {

			  }
		}

        while (isRecording) {
            // gets the voice output from microphone to byte format

            num=record.read(sData, 0, BufferElements2Rec);
            System.out.println("Short wirting to file" + sData.toString());
            if (num == BufferElements2Rec){
	            long curTime = System.currentTimeMillis();
	            String curTimeStr = ""+curTime+";   ";
	            Log.i(TAG,curTimeStr);
	            try {
	            	outwriterSound.append(curTimeStr);
	    		} catch (IOException e1) {
	    			// TODO Auto-generated catch block
	    			e1.printStackTrace();
	    		}
	        	String strI = ""+(sData.length);
	        	Log.i(TAG, strI);
	        	
	        	for ( int i=0; i<sData.length;i++){
	        		try {
	        			String tempS = Short.toString(sData[i])+"    ";
	        			outwriterSound.append(tempS);
	        			outwriterSoundData.append(tempS);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
        		try {
        			String tempS = "\n";
        			outwriterSound.append(tempS);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
        	try {
        		outwriterSound.flush();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	try {
        		outwriterSoundData.flush();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            
            
        }
       
    }

    private void stopRecording() {
        // stops the recording activity
        if (null != record) {
            isRecording = false;
            record.stop();
            record.release();
            record = null;

            recordingThread = null;
        }
    }
    
    private void setButtonHandlers() {
        ((Button) findViewById(R.id.recordStart)).setOnClickListener(btnClick);
        ((Button) findViewById(R.id.recordStop)).setOnClickListener(btnClick);
        ((Button) findViewById(R.id.emitStart)).setOnClickListener(btnClick);
        ((Button) findViewById(R.id.emitStop)).setOnClickListener(btnClick);
    }

    private void enableButton(int id, boolean isEnable) {
        ((Button) findViewById(id)).setEnabled(isEnable);
    }

    private void enableButtons(boolean isRecording) {
        enableButton(R.id.recordStart, !isRecording);
        enableButton(R.id.recordStop, isRecording);
    }
    
    
    
    private View.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.recordStart: {
                enableButtons(true);
                startRecording();
                break;
            }
            case R.id.recordStop: {
                enableButtons(false);
                stopRecording();
                break;
            }
            case R.id.emitStart: {
            	enableButton(R.id.emitStart, false);
            	enableButton(R.id.emitStop, true);
                startEmitting();
                break;
            }
            case R.id.emitStop: {
            	enableButton(R.id.emitStart, true);
            	enableButton(R.id.emitStop, false);
                stopEmitting();
                break;
            }
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    
    
	boolean isSensor = false;

	public void SensorSwitch(View view) throws IOException {
		Button SensorBtn=(Button) findViewById(R.id.Sensor_switch);
		
		if (isSensor == true) {	
			if(LightSensor != null){
				mySensorManager.unregisterListener(LightSensorListener,LightSensor);
			
				try {
					outwriterLight.flush();
					outwriterLight.close();
					foutLight.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(MagneSensor != null){
				
				mySensorManager.unregisterListener(MagneSensorListener,MagneSensor);
				try {
					outwriterMagne.flush();
					outwriterMagne.close();
					foutMagne.close();
				} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
			}
			
			if(PressureSensor != null){
				mySensorManager.unregisterListener(PressureSensorListener,PressureSensor);
			
				try {
					outwriterPressure.flush();
					outwriterPressure.close();
					foutPressure.close();
				} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
			}
			if(TemperSensor != null){
			
				mySensorManager.unregisterListener(TemperSensorListener,TemperSensor);

				try {
					outwriterTemper.flush();
					outwriterTemper.close();
					foutTemper.close();
				} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
			}
			
			if(AcceleSensor != null){
					
				mySensorManager.unregisterListener(AcceleSensorListener,AcceleSensor);
	
				try {
					outwriterAccele.flush();
					outwriterAccele.close();
					foutAccele.close();
				} catch (IOException e) {
						// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(GyroSensor != null){
		
				mySensorManager.unregisterListener(GyroSensorListener,GyroSensor);
	
				try {
					outwriterGyro.flush();
					outwriterGyro.close();
					foutGyro.close();
				} catch (IOException e) {
						// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(ProxiSensor != null){
		
				mySensorManager.unregisterListener(ProxiSensorListener,ProxiSensor);

				try {
					outwriterProxi.flush();
					outwriterProxi.close();
					foutProxi.close();
				} catch (IOException e) {
						// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			Tel.listen(MyListener,PhoneStateListener.LISTEN_NONE);
			try {
				
				outwriterSignal.close();
				foutSignal.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (mGoogleApiClient.isConnected()) {
				
				mGoogleApiClient.disconnect();
			}
			try {
				
				outwriterSpeed.close();
				foutSpeed.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				outwriterWifi.flush();
				outwriterWifi.close();
				foutWifi.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				outwriterRGB.flush();
				outwriterRGB.close();
				foutRGB.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		

			
			isSensor = false;
			SensorBtn.setText("sensor off");
			
			
			
		}else {	
			if(LightSensor != null){
				mySensorManager.registerListener(LightSensorListener, LightSensor, SensorManager.SENSOR_DELAY_NORMAL);
				if(LightFile.exists()){
					try{
						foutLight = new FileOutputStream(LightFile,true);
						outwriterLight = new OutputStreamWriter(foutLight);
					} catch(Exception e)
					{
					}
				} 
			}
			if(MagneSensor != null){
				mySensorManager.registerListener(MagneSensorListener, MagneSensor, SensorManager.SENSOR_DELAY_NORMAL);
				if(MagneFile.exists()){
    			  
					try{
    	    		
						foutMagne = new FileOutputStream(MagneFile,true);
						outwriterMagne = new OutputStreamWriter(foutMagne);
					} catch(Exception e)
					{

					}
				}
			}
			if (PressureSensor != null){
				mySensorManager.registerListener(PressureSensorListener, PressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
				if(PressureFile.exists()){
    			  
					try{
    	    		
						foutPressure = new FileOutputStream(PressureFile,true);
						outwriterPressure = new OutputStreamWriter(foutPressure);
					} catch(Exception e)
					{

					}
				}
			}
			
			if (TemperSensor != null){
				mySensorManager.registerListener(TemperSensorListener, TemperSensor, SensorManager.SENSOR_DELAY_NORMAL);
				if(TemperFile.exists()){
    			  
					try{
    	    		
						foutTemper = new FileOutputStream(TemperFile,true);
						outwriterTemper = new OutputStreamWriter(foutTemper);
					} catch(Exception e)
					{

					}
				}
			}
			
			
			if ( AcceleSensor != null){
				mySensorManager.registerListener(AcceleSensorListener, AcceleSensor, SensorManager.SENSOR_DELAY_NORMAL);
				if(AcceleFile.exists()){
    			  
					try{
    	    		
						foutAccele = new FileOutputStream(AcceleFile,true);
						outwriterAccele = new OutputStreamWriter(foutAccele);
					} catch(Exception e)
					{

					}
				}
			}
			
			
			if ( GyroSensor != null){
				mySensorManager.registerListener(GyroSensorListener, GyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
				if(GyroFile.exists()){
    			  
					try{
    	    		
						foutGyro = new FileOutputStream(GyroFile,true);
						outwriterGyro = new OutputStreamWriter(foutGyro);
					} catch(Exception e)
					{

					}
				}
			}
			
			if ( ProxiSensor != null){
				mySensorManager.registerListener(ProxiSensorListener, ProxiSensor, SensorManager.SENSOR_DELAY_NORMAL);
				if(ProxiFile.exists()){
    			  
					try{
    	    		
						foutProxi = new FileOutputStream(ProxiFile,true);
						outwriterProxi = new OutputStreamWriter(foutProxi);
					} catch(Exception e)
					{

					}
				}
			}
			
			Tel.listen(MyListener ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
			if(SignalFile.exists()){
				try{
				  foutSignal = new FileOutputStream(SignalFile,true);
				  outwriterSignal = new OutputStreamWriter(foutSignal);
				} catch(Exception e)
				{}
			}
			
			mGoogleApiClient.connect();
			if(SpeedFile.exists()){
				try{
				  foutSpeed = new FileOutputStream(SpeedFile,true);
				  outwriterSpeed = new OutputStreamWriter(foutSpeed);
				} catch(Exception e)
				{}
			}
			
			
			if(WifiFile.exists()){
				  
				  try{
		    		
					  foutWifi = new FileOutputStream(WifiFile,true);
					  outwriterWifi= new OutputStreamWriter(foutWifi);
				
				  } catch(Exception e)
				  {

				  }
			}
			
			
			if(RGBFile.exists()){
				  
				  try{
		    		
					  foutRGB = new FileOutputStream(RGBFile,true);
					  outwriterRGB= new OutputStreamWriter(foutRGB);
				
				  } catch(Exception e)
				  {

				  }
			}
			
			
			


//			RGBIs = context.openFileInput(RGBfile);
//			iRGBr = new InputStreamReader(RGBIs);
//			bufferedReader = new BufferedReader(iRGBr);
			
		
//			if (wifiManager.isWifiEnabled() == false) {
//				wifiManager.setWifiEnabled(true);
//				scaning();
//			}
//			else{
//				scaning();
//			}
				
			isSensor = true;
			SensorBtn.setText("sensor on");
		}
	
	} 

    private final SensorEventListener LightSensorListener= new SensorEventListener(){

    	@Override
    	public void onAccuracyChanged(Sensor sensor, int accuracy) {
    		// TODO Auto-generated method stub
    
    	}

    	@Override
    	public void onSensorChanged(SensorEvent event) {
    		if(event.sensor.getType() == Sensor.TYPE_LIGHT){
    			long timeSta = System.currentTimeMillis();
    			textLIGHT_reading.setText("Light(Lx): " + event.values[0] );
    			writeJSON(outwriterLight,timeSta,event.values[0]);
    			int tmpsize = event.values.length;
    			for (int i=0; i< tmpsize; i++)
    			{
    				Log.d("value in light senser changes: ", String.valueOf(event.values[i]));
    			}
 
    					
    			SupplicantState supState; 
    			
    			wifiInfo = wifiManager.getConnectionInfo();
    			
    			supState = wifiInfo.getSupplicantState();
    			textWifi_available.setText("WiFI: "+supState + Integer.toString(wifiInfo.getRssi()));
    			writeJSON(outwriterWifi,timeSta,wifiInfo.getRssi());
    			if (RGBAvailabe ==1 ){
					try {
						bufferedReader = new BufferedReader(new FileReader(InputRGBFile));
					} catch (FileNotFoundException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}

					StringBuilder finalString = new StringBuilder();

					if (bufferedReader != null) {
						String line;
						try {
							while ((line = bufferedReader.readLine()) != null) {

								finalString.append(line);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					textRGB_reading.setText("RGB:" + finalString.toString());
					String curTimeStr = "" + timeSta + ";   ";
					try {
						outwriterRGB.append(curTimeStr);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						outwriterRGB.append(finalString.toString() + "\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						bufferedReader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
    		}
    	}
    };
    
    private final SensorEventListener MagneSensorListener= new SensorEventListener(){

    	@Override
    	public void onAccuracyChanged(Sensor sensor, int accuracy) {
    		// TODO Auto-generated method stub
    
    	}

    	@Override
    	public void onSensorChanged(SensorEvent event) {
    		if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
    			long timeSta = System.currentTimeMillis();
    			textMagne_reading.setText("Magnetic(uT): " + event.values[0]+"		"+event.values[1]+"		"+event.values[2]);
    			ThreewriteJSON(outwriterMagne,timeSta,event.values[0],event.values[1],event.values[2]);
    			
    		}
    	}
     
    };
    
    private final SensorEventListener PressureSensorListener= new SensorEventListener(){

    	@Override
    	public void onAccuracyChanged(Sensor sensor, int accuracy) {
    		// TODO Auto-generated method stub
    
    	}

    	@Override
    	public void onSensorChanged(SensorEvent event) {
    		if(event.sensor.getType() == Sensor.TYPE_PRESSURE){
    			long timeSta = System.currentTimeMillis();
    			float altitude = event.values[1];
    			//float altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);
    			textPressure_reading.setText("Altitude(m): " + altitude);
    			writeJSON(outwriterPressure,timeSta,altitude);
    		}
    	}
     
    };
    
    private final SensorEventListener TemperSensorListener= new SensorEventListener(){

    	@Override
    	public void onAccuracyChanged(Sensor sensor, int accuracy) {
    		// TODO Auto-generated method stub
    
    	}

    	@Override
    	public void onSensorChanged(SensorEvent event) {
    		if(event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE){
    			long timeSta = System.currentTimeMillis();
    			textTemper_reading.setText("Temperature(C): " + event.values[0]);
    			writeJSON(outwriterTemper,timeSta,event.values[0]);
    		}
    	}
     
    };
    
    
    private final SensorEventListener AcceleSensorListener= new SensorEventListener(){

    	@Override
    	public void onAccuracyChanged(Sensor sensor, int accuracy) {
    		// TODO Auto-generated method stub
    
    	}

    	@Override
    	public void onSensorChanged(SensorEvent event) {
    		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
    			long timeSta = System.currentTimeMillis();
    			textAccele_reading.setText("Temperature(m/s2): " + event.values[0]+"		"+event.values[1]+"		"+event.values[2]);
    			ThreewriteJSON(outwriterAccele,timeSta,event.values[0],event.values[1],event.values[2]);
    		}
    	}
     
    };
    
    
    private final SensorEventListener ProxiSensorListener= new SensorEventListener(){

    	@Override
    	public void onAccuracyChanged(Sensor sensor, int accuracy) {
    		// TODO Auto-generated method stub
    
    	}

    	@Override
    	public void onSensorChanged(SensorEvent event) {
    		if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
    			long timeSta = System.currentTimeMillis();
    			textProxi_reading.setText("Proximity: " + event.values[0]);
    			writeJSON(outwriterProxi,timeSta,event.values[0]);
    		}
    	}
     
    };
    
    private final SensorEventListener GyroSensorListener= new SensorEventListener(){

    	@Override
    	public void onAccuracyChanged(Sensor sensor, int accuracy) {
    		// TODO Auto-generated method stub
    
    	}

    	@Override
    	public void onSensorChanged(SensorEvent event) {
    		if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
    			long timeSta = System.currentTimeMillis();
    			textGyro_reading.setText("Gyro: " + event.values[0]+"		"+event.values[1]+"		"+event.values[2]);
    			ThreewriteJSON(outwriterGyro,timeSta,event.values[0],event.values[1],event.values[2]);
    		}
    	}
     
    };
    
    
    
    private class MyPhoneStateListener extends PhoneStateListener
    {
      /* Get the Signal strength from the provider, each time there is an update */
      @Override
      public void onSignalStrengthsChanged(SignalStrength signalStrength)
      {
         super.onSignalStrengthsChanged(signalStrength);
         int Strength = (signalStrength.getGsmSignalStrength() *2 -113);
         String StrStrength = ""+Strength;
         textSignal_reading.setText("GSM signal strength(dB): "+StrStrength);
         long timeSta = System.currentTimeMillis();
         writeJSON(outwriterSignal,timeSta,Strength);
         try {
			outwriterSignal.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//         Toast.makeText(getApplicationContext(), "Go to Firstdroid!!! GSM Cinr = "
//            + String.valueOf(signalStrength.getGsmSignalStrength()), Toast.LENGTH_SHORT).show();
      }

    };/* End of private Class */
    
    public void writeJSON(OutputStreamWriter myWriter, long timestamp, float value) {
    	  JSONObject object = new JSONObject();
    	  try {
    		  
    		  object.put("timestamp", timestamp);
    		  object.put("value", value);
    		  String content = object.toString() + "\n";
    		  myWriter.append(content);
    		  Log.i(TAG, content);
    	    
    	    
    	  	} catch (JSONException | IOException e) {
    	  		e.printStackTrace();
    	  	}

    	  	System.out.println(object);
  
    } 
    
    public void ThreewriteJSON(OutputStreamWriter myWriter, long timestamp, float value1,float value2, float value3) {
  	  JSONObject object = new JSONObject();
  	  try {
  		  
  		  object.put("timestamp", timestamp);
  		  object.put("x", value1);
  		  object.put("y", value2);
  		  object.put("z", value3);
  		  String content = object.toString() + "\n";
  		  myWriter.append(content);
  		  Log.i(TAG, content);
  	    
  	    
  	  	} catch (JSONException | IOException e) {
  	  		e.printStackTrace();
  	  	}

  	  	System.out.println(object);

    }
    
    public void LocationwriteJSON(OutputStreamWriter myWriter, long timestamp, String value1,double d, float value3) {
    	  JSONObject object = new JSONObject();
    	  try {
    		  
    		  object.put("timestamp", timestamp);
    		  object.put("location", value1);
    		  object.put("altitude", d);
    		  object.put("speed", value3);
    		  String content = object.toString() + "\n";
    		  myWriter.append(content);
    		  Log.i(TAG, content);
    	    
    	    
    	  	} catch (JSONException | IOException e) {
    	  		e.printStackTrace();
    	  	}

    	  	System.out.println(object);

      }

	@Override
	public void onConnected(Bundle connectionHint) {
		// Provides a simple way of getting a device's location and is well
		// suited for
		// applications that do not require a fine-grained location and that do
		// not need location
		// updates. Gets the best and most recent location currently available,
		// which may be null
		// in rare cases when a location is not available.
		mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, (LocationListener) this);
	
	}
	
//	private void scaning() {
//		// wifi scaned value broadcast receiver
//		receiverWifi = new WifiReceiver();
//		// Register broadcast receiver
//		// Broacast receiver will automatically call when number of wifi
//		// connections changed
//		registerReceiver(receiverWifi, new IntentFilter(
//				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//		wifiManager.startScan();
//		if(WifiFile.exists()){
//			  
//			  try{
//	    		
//				  foutWifi = new FileOutputStream(WifiFile,true);
//				  outwriterWifi= new OutputStreamWriter(foutWifi);
//			
//			  } catch(Exception e)
//			  {
//
//			  }
//		}
//	}
//	
	
	
//	class WifiReceiver extends BroadcastReceiver {
//
//		// This method call when number of wifi connections changed
//		public void onReceive(Context c, Intent intent) {
//			wifiList = wifiManager.getScanResults();
//			
//			Collections.sort(wifiList, new Comparator<ScanResult>() {
//				@Override
//				public int compare(ScanResult lhs, ScanResult rhs) {
//					return (lhs.level > rhs.level ? -1
//							: (lhs.level == rhs.level ? 0 : 1));
//				}
//			});
//			
//			
//			String providerName;
//            long curTimeWifi = System.currentTimeMillis();
//            String curTimeWifiStr = ""+curTimeWifi+";   ";
//            
//            try {
//            	outwriterWifi.append(curTimeWifiStr+" "+Integer.toString(wifiList.size())+" ");
//    		} catch (IOException e1) {
//    			// TODO Auto-generated catch block
//    			e1.printStackTrace();
//    		}
//
//			for (int i = 0; i < wifiList.size(); i++) {
//				/* to get SSID and BSSID of wifi provider*/
//				providerName =(wifiList.get(i).SSID).toString()
//						+'	'+Integer.toString(wifiList.get(i).level);
//				textWifi_reading.setText("Wifi(dm): " + providerName);
//
//        		try {
//        			outwriterWifi.append(Integer.toString(wifiList.get(i).level)+" ");
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					}
//			}
//
//    		try {
//    			String tempS = "\n";
//    			outwriterWifi.append(tempS);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			try {
//				outwriterWifi.flush();
//				outwriterWifi.close();
//				foutWifi.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}	
//	}
//	
	

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Refer to the javadoc for ConnectionResult to see what error codes
		// might be returned in
		// onConnectionFailed.
		Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// The connection to Google Play services was lost for some reason. We
		// call connect() to
		// attempt to re-establish the connection.
		Log.i(TAG, "Connection suspended");
		mGoogleApiClient.connect();
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		textLocation_reading.setText("Location received: " + location.toString()+' '+location.getAltitude()+' '+location.getSpeed());
        long timeSta = System.currentTimeMillis();
        LocationwriteJSON(outwriterSpeed,timeSta,location.toString(),location.getAltitude(),location.getSpeed());
        try {
			outwriterSpeed.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

