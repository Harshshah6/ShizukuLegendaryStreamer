package legendary.streamer.shizukubylegendarystreamer;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import legendary.streamer.shizuku.LegendaryStreamerShizuku;
import legendary.streamer.shizuku.interfaces.ExecutionProcessListener;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private TextView textView;

    private LegendaryStreamerShizuku legendaryStreamerShizuku;
    private final ExecutionProcessListener executionProcessListener = new ExecutionProcessListener() {
        @Override
        public void onPreExecute() {
//            progressDialog = new ProgressDialog(MainActivity.this);
//            progressDialog.setCancelable(false);
//            progressDialog.setCanceledOnTouchOutside(false);
//            progressDialog.setMessage("starting...");
//            progressDialog.create();
//            progressDialog.show();

            textView.setText(textView.getText().toString() + "\n");
            showToast("starting...");
        }

        @Override
        public void onSuccessProgressUpdate(String message) {
            //progressDialog.setMessage(message);
            textView.setText(textView.getText().toString() + "\n" + message);
            showToast(message);
        }

        @Override
        public void onErrorProgressUpdate(String message) {
            //progressDialog.setMessage(message);
            textView.setText(textView.getText().toString() + "\n" + message);
            showToast(message);
        }

        @Override
        public void onPostExecute(ArrayList<String> successMessages, ArrayList<String> errorMessages) {
           // progressDialog.dismiss();
            textView.setText(textView.getText().toString() + "\n");
            showToast(successMessages.toString() + "\n" + errorMessages.toString());
            ((EditText)findViewById(R.id.editTextText)).setText("");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);

        legendaryStreamerShizuku = new LegendaryStreamerShizuku(this,true);
        legendaryStreamerShizuku.autoReqPermission();

        if(!legendaryStreamerShizuku.isShizukuInstalled()){
            Toast.makeText(this, "Please Install Shizuku first to make application work.", Toast.LENGTH_SHORT).show();
            legendaryStreamerShizuku.requestInstallShizuku();
        }

        findViewById(R.id.button).setOnClickListener(view -> {
            String input = ((EditText)findViewById(R.id.editTextText)).getText().toString();
            if(input.trim().equals("clear") || input.trim().equals("cls")){
                textView.setText("");
                return;
            }
            legendaryStreamerShizuku.runCustomCommand(input, executionProcessListener);
        });

        
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        legendaryStreamerShizuku.onDestroy();
    }

    void showToast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
    
}