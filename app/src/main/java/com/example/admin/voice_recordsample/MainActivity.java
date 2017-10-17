package com.example.admin.voice_recordsample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 1;
    Button start_recording, stop_Recording, start_Recordedaudi, stop_Recordedaudi,play_to_next;
    ImageView imageView;
    String audiopath = null;
    MediaRecorder mr;
    MediaPlayer mp;
    public static final int requestcode_permisson = 1;
    Random random;
    String randomaudio_Names = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_recording = (Button) findViewById(R.id.record_start);
        imageView=(ImageView)findViewById(R.id.get_iv);
        stop_Recording = (Button) findViewById(R.id.record_stop);
        //start_Recordedaudio = (Button) findViewById(R.id.play_recorded_audio);
        play_to_next = (Button) findViewById(R.id.play_to_next);
      //  stop_Recordedaudio = (Button) findViewById(R.id.stop_recorded_audio);
        stop_Recording.setEnabled(false);
      //  stop_Recordedaudio.setEnabled(false);
        //start_Recordedaudio.setEnabled(false);
        random = new Random();
        start_recording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermisson()) {
                    audiopath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + CreateRandomAudioFileName(5) + "AudioRecording.3gp";
                    MediaRecorderReady();
                    try {
                        mr.prepare();
                        mr.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    start_recording.setEnabled(false);
                    stop_Recording.setEnabled(true);
                    Snackbar.make(view, "Voice Recording", Snackbar.LENGTH_SHORT).show();
                } else
                    requestPermission();
            }
        });

        stop_Recording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mr.stop();
                stop_Recording.setEnabled(false);
                start_recording.setEnabled(true);
          //      start_Recordedaudio.setEnabled(true);
            //    stop_Recordedaudio.setEnabled(false);
                Snackbar.make(view, "Voice Recorded", Snackbar.LENGTH_SHORT).show();
            }
        });

      /*  start_Recordedaudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {
                stop_Recordedaudio.setEnabled(true);
                start_Recordedaudio.setEnabled(false);
                stop_Recording.setEnabled(false);
                start_recording.setEnabled(true);
                mp = new MediaPlayer();
                try {
                    mp.setDataSource(audiopath);
                    mp.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mp.start();
                Snackbar.make(view, "Recorded Voice played", Snackbar.LENGTH_SHORT).show();

            }
        });

        stop_Recordedaudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop_Recordedaudio.setEnabled(false);
                stop_Recording.setEnabled(false);
                start_recording.setEnabled(true);
                start_Recordedaudio.setEnabled(true);
                if (mp != null) {
                    mp.stop();
                    mp.release();
                    MediaRecorderReady();
                }
            }
        });
*/
        play_to_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,Songplay.class).putExtra("song",audiopath));

            }
        });
    }

    public boolean checkPermisson() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        return (result == PackageManager.PERMISSION_GRANTED) && (result1 == PackageManager.PERMISSION_GRANTED);

    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.RECORD_AUDIO}, requestcode_permisson);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case requestcode_permisson:
                if (grantResults.length > 0) {
                    boolean StoragePermisson = grantResults[0] == PackageManager.PERMISSION_DENIED;
                    boolean RecordPermisson = grantResults[1] == PackageManager.PERMISSION_DENIED;
                    if (StoragePermisson && RecordPermisson)
                        Toast.makeText(getApplicationContext(), "Permission Granded", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }

    public void MediaRecorderReady() {
        mr = new MediaRecorder();
        mr.setAudioSource(MediaRecorder.AudioSource.MIC);
        mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mr.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mr.setOutputFile(audiopath);
    }

    public String CreateRandomAudioFileName(int value) {
        StringBuilder sb = new StringBuilder(value);
        for (int i = 0; i < value; i++)
            sb.append(randomaudio_Names.charAt(random.nextInt(randomaudio_Names.length())));
        return sb.toString();
    }
    public void chooseimg(View v)
    {
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            ImageView imageView = (ImageView) findViewById(R.id.get_iv);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }
}
