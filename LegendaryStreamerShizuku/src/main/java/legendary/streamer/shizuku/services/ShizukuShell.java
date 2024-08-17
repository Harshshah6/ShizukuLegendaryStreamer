package legendary.streamer.shizuku.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;import java.util.ArrayList;

import legendary.streamer.shizuku.interfaces.ExecutionProcessListener;
import rikka.shizuku.Shizuku;
import rikka.shizuku.ShizukuRemoteProcess;

/**
 * A class for executing shell commands using the Shizuku service.
 *
 * This class provides methods for running shell commands, capturing output,
 * and handling command execution events through an {@link ExecutionProcessListener}.
 *
 * @author LegendaryStreamer
 * @version 1.0.0
 */
public class ShizukuShell {

    private final ArrayList<String> mOutput = new ArrayList<>();
    private static ShizukuRemoteProcess mProcess = null;
    private static String mCommand;
    private static String mDir = "/";

    /**
     * Constructor for ShizukuShell.
     *
     *@param command The shell command to execute.
     */
    public ShizukuShell(String command) {
        mCommand = command;
    }

    /**
     * Checks if the shell is currently busy executing a command.
     *
     * @return True if the shell is busy, false otherwise.
     */
    public boolean isBusy() {
        return !mOutput.isEmpty();
    }

    /**
     * Executes the shell command and captures output.
     *
     * @param successMessages A list to store successful output messages.
     * @param errorMessages A list to store error output messages.
     * @param executionProcessListenerListener Listener for command execution events.
     */
    public void exec(ArrayList<String> successMessages, ArrayList<String> errorMessages, ExecutionProcessListener executionProcessListenerListener) {
        successMessages.clear();
        errorMessages.clear();

        try {
            mProcess = Shizuku.newProcess(new String[] {"sh", "-c", mCommand}, null, mDir);
            BufferedReader mInput = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
            BufferedReader mError= new BufferedReader(new InputStreamReader(mProcess.getErrorStream()));
            String line;
            while ((line = mInput.readLine()) != null) {
                mOutput.add(line);
                successMessages.add(line);
                try {
                    executionProcessListenerListener.onSuccessProgressUpdate(line);
                } catch (Exception ignored) {}
            }
            while ((line = mError.readLine()) != null) {
                mOutput.add(line);
                errorMessages.add(line);
                try {
                    executionProcessListenerListener.onErrorProgressUpdate(line);
                } catch (Exception ignored) {}
            }

            // Handle directory changes (cd command)
            if (mCommand.startsWith("cd ")) {
                String[] array = mCommand.split("\\s+");
                String dir;
                if (array[array.length - 1].equals("/")) {
                    dir = "/";
                } else if (array[array.length - 1].startsWith("/")) {
                    dir = array[array.length - 1];
                } else {
                    dir = mDir + array[array.length - 1];
                }
                if (!dir.endsWith("/")) {
                    dir = dir + "/";
                }
                mDir = dir;
            }

            mProcess.waitFor();
        } catch (Exception ignored) {
        }
        mOutput.clear();
    }

    /**
     * Destroys the Shizuku remote process.
     */
    public void destroy() {
        if (mProcess != null) mProcess.destroy();
    }
}