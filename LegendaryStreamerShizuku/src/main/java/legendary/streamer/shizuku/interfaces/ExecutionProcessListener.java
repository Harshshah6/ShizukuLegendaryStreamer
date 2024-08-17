package legendary.streamer.shizuku.interfaces;

import java.util.ArrayList;

public interface ExecutionProcessListener {

    void onPreExecute();
    default void onSuccessProgressUpdate(String message){};
    default void onErrorProgressUpdate(String message){};
    void onPostExecute(ArrayList<String> successMessages, ArrayList<String> errorMessages);

}
