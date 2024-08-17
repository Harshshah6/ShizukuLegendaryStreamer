package legendary.streamer.shizuku;

import static android.content.pm.PackageManager.GET_ACTIVITIES;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import legendary.streamer.shizuku.enums.PermissionInformation;
import legendary.streamer.shizuku.exceptions.AccessDeniedException;
import legendary.streamer.shizuku.interfaces.ExecutionProcessListener;
import legendary.streamer.shizuku.services.ShizukuShell;
import rikka.shizuku.Shizuku;


/**
 * <h6>A utility class for interacting with the Shizuku service to execute shell commands.</h6>
 * <p>
 * This class provides methods for checking Shizuku permissions, requesting permissions,
 * running various shell commands (e.g., copy, move, delete, unzip), and handling
 * command execution results.
 * </p>
 *
 * @author LegendaryStreamer
 * @version 1.0.1
 */
public class LegendaryStreamerShizuku {

    private final Context mContext;
    private ShizukuShell mShizukuShell;

    /**
     * Constructor for LegendaryStreamerShizuku.
     *
     * @param context The application context.
     */
    public LegendaryStreamerShizuku(Context context) {
        mContext = context;
    }

    /**
     * Constructor for LegendaryStreamerShizuku with automatic permission request.
     *
     * @param context           The application context.
     * @param autoReqPermission If true, automatically requests Shizuku permission.
     */
    public LegendaryStreamerShizuku(Context context, boolean autoReqPermission) {
        mContext = context;
        if (autoReqPermission) autoReqPermission();
    }

    /**
     * Automatically requests Shizuku permission if not granted and thedevice is compatible.
     */
    public void autoReqPermission() {
        if (Shizuku.isPreV11()) return;
        if (checkPermission() == PermissionInformation.PERMISSION_NOT_GRANTED)
            requestShizukuPermission();
    }

    /**
     * Checks if Shizuku permission is granted.
     *
     * @return True if permission is granted, false otherwise.
     */
    public boolean isPermissionGranted() {
        if (Shizuku.isPreV11()) return false;
        return checkPermission() == PermissionInformation.PERMISSON_GRANTED;
    }

    /**
     * Checks the status of Shizuku permission.
     *
     * @return A {@link PermissionInformation} enum value representing the permission status.
     */
    public PermissionInformation checkPermission() {
        if (Shizuku.isPreV11())
            return PermissionInformation.SERVICE_NOT_AVAILABLE;

        if (Shizuku.pingBinder()) {
            if (Shizuku.checkSelfPermission() == PERMISSION_GRANTED) {
                return PermissionInformation.PERMISSON_GRANTED;
            } else {
                return PermissionInformation.PERMISSION_NOT_GRANTED;
            }
        } else {
            if (!isShizukuInstalled())
                return PermissionInformation.SHIZUKU_NOT_INSTALLED;
            else
                return PermissionInformation.SERVICE_NOT_AVAILABLE;
        }
    }

    /**
     * Checks if the Shizuku service is not available.
     *
     * @return True if the service is not available, false otherwise.
     */
    public boolean isServiceNotAvailable() {
        return checkPermission() == PermissionInformation.SERVICE_NOT_AVAILABLE;
    }

    /**
     * Checks if Shizuku is installed on the device.
     *
     * @return True if Shizuku is installed, false otherwise.
     */
    public boolean isShizukuInstalled() {
        try {
            mContext.getPackageManager().getPackageInfo("moe.shizuku.privileged.api", GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * Opens the Shizuku app in the Play Store for installation.
     */
    public void requestInstallShizuku() {
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=moe.shizuku.privileged.api")));
    }

    /**
     * Requests Shizuku permission if the device is compatible.
     */
    public void requestShizukuPermission() {
        if (!Shizuku.isPreV11())
            Shizuku.requestPermission(0);
    }

    /**
     * Destroys the Shizuku shell instance.
     */
    public void onDestroy() {
        if (mShizukuShell != null) mShizukuShell.destroy();
    }

    /**
     * Runs a custom shell command.
     *
     * @param command                  The command to execute.
     * @param executionProcessListener Listener for command execution events.
     */
    public void runCustomCommand(String command, ExecutionProcessListener executionProcessListener) {
        initializeShell(command, executionProcessListener);
    }

    /**
     * Runs an unzip command.
     *
     * @param zipFilePath              The path to the zip file.
     * @param unzipPath                The path to extract the zip file to.
     * @param executionProcessListener Listener for command execution events.
     */
    public void runUnzipCommand(String zipFilePath, String unzipPath, ExecutionProcessListener executionProcessListener) {
        initializeShell("unzip -o " + zipFilePath + " -d " + unzipPath, executionProcessListener);
    }

    /**
     * Runs a copy command.
     *
     * @param copyFileFrom             The path to the file to copy.
     * @param copyFileTo               The destination path for the copied file.
     * @param executionProcessListener Listener for command execution events.
     */
    public void runCopyCommand(String copyFileFrom, String copyFileTo, ExecutionProcessListener executionProcessListener) {
        initializeShell("cp -r " + copyFileFrom + " " + copyFileTo, executionProcessListener);
    }

    /**
     * Runs a move command.
     *
     * @param moveFileFrom             The path to the file to move.
     * @param moveFileTo               The destination path for the moved file.
     * @param executionProcessListener Listener for command execution events.
     */
    public void runMoveCommand(String moveFileFrom, String moveFileTo, ExecutionProcessListener executionProcessListener) {
        initializeShell("mv " + moveFileFrom + " " + moveFileTo, executionProcessListener);
    }

    /**
     * Runs a delete command.
     *
     * @param deleteFile               The path to the file or directory to delete.
     * @param executionProcessListener Listener for command execution events.
     */
    public void runDeleteCommand(String deleteFile, ExecutionProcessListener executionProcessListener) {
        initializeShell("rm -rf " + deleteFile, executionProcessListener);
    }

    /**
     * Runs a list directory command.
     *
     * @param dirPath                  The path to the directory to list.
     * @param executionProcessListener Listener for command execution events.
     */
    public void runListDirCommand(String dirPath, ExecutionProcessListener executionProcessListener) {
        initializeShell("ls " + dirPath, executionProcessListener);
    }

    /**
     * Runs a create directory command.
     *
     * @param createDirectoryPath      The path to the directory to create.
     * @param executionProcessListener Listener for command execution events.
     */
    public void runCreateDirectoryCommand(String createDirectoryPath, ExecutionProcessListener executionProcessListener) {
        initializeShell("mkdir " + createDirectoryPath, executionProcessListener);
    }

    /**
     * Runs a create file command with content.
     *
     * @param createDirectoryPath      The path to the file to create.
     * @param fileContentString        The content to write to the file.
     * @param executionProcessListener Listener for command execution events.
     */
    public void runCreateFileCommand(String createDirectoryPath, String fileContentString, ExecutionProcessListener executionProcessListener) {
        initializeShell("echo \"" + fileContentString + "\" > " + createDirectoryPath, executionProcessListener);
    }

    /**
     * Runs a create file command (empty file).
     *
     * @param createDirectoryPath      The path to the file to create.
     * @param executionProcessListener Listener for command execution events.
     */
    public void runCreateFileCommand(String createDirectoryPath, ExecutionProcessListener executionProcessListener) {
        initializeShell("touch " + createDirectoryPath, executionProcessListener);
    }

    /**
     * Runs a tap command to simulate a screen tap.
     *
     * @param x                        The x-coordinate ofthe tap.
     * @param y                        The y-coordinate of the tap.
     * @param executionProcessListener Listener for command execution events.
     */
    public void runTapCommand(int x, int y, ExecutionProcessListener executionProcessListener) {
        initializeShell("input tap " + x + " " + y, executionProcessListener);
    }

    /**
     * Runs a swipe command to simulate a screen swipe.
     *
     * @param fromX                    The starting x-coordinate of the swipe.
     * @param fromY                    The starting y-coordinate of the swipe.
     * @param toX                      The ending x-coordinate of the swipe.
     * @param toY                      The ending y-coordinate of the swipe.
     * @param durationMS               The duration of the swipe in milliseconds.
     * @param executionProcessListener Listener for command execution events.
     */
    public void runSwipeCommand(int fromX, int fromY, int toX, int toY, int durationMS, ExecutionProcessListener executionProcessListener) {
        initializeShell("input swipe " + fromX + " " + fromY + " " + toX + " " + toY + " " + durationMS, executionProcessListener);
    }

    /**
     * Runs a key event command.
     *
     * @param keyEvent                 The key event code.
     * @param executionProcessListener Listener for command execution events.
     */
    public void runKeyEventCommand(int keyEvent, ExecutionProcessListener executionProcessListener) {
        initializeShell("input keyevent " + keyEvent, executionProcessListener);
    }

    /**
     * Runs a command to input text into a text field.
     *
     * @param textToInput              The text to input.
     * @param textFieldX               The x-coordinate of the text field.
     * @param textFieldY               The y-coordinate of the text field.
     * @param executionProcessListener Listener for command execution events.
     */
    public void runInputTextCommand(String textToInput, int textFieldX, int textFieldY, ExecutionProcessListener executionProcessListener) {
        initializeShell("input tap " + textFieldX + " " + textFieldY + "; sleep 2s; input text " + textToInput, executionProcessListener);
    }

    /**
     * Runs a command to read the content of a file.
     *
     * @param filePath                 The path to the file.
     * @param executionProcessListener Listener for command execution events.
     */
    public void runReadFileContentCommand(String filePath, ExecutionProcessListener executionProcessListener) {
        initializeShell("cat " + filePath, executionProcessListener);
    }

    /**
     * Runs a rename command (using move).
     *
     * @param oldPath                  The old path of the file or directory.
     * @param newPath                  The new path of the file or directory.
     * @param executionProcessListener Listener for command execution events.
     */
    public void runRenameCommand(String oldPath, String newPath, ExecutionProcessListener executionProcessListener) {
        runMoveCommand(oldPath, newPath, executionProcessListener);
    }

    /**
     * Returns a sleep command for a specified duration.
     *
     * @param sleepDurationMS The sleep duration in milliseconds.
     * @return The sleep command string.
     */
    public String getSleepCommand(int sleepDurationMS) {
        return "sleep " + sleepDurationMS;
    }

    private void initializeShell(String command, ExecutionProcessListener executionProcessListener) {
        if (Shizuku.isPreV11()) return;
        if (checkPermission() != PermissionInformation.PERMISSON_GRANTED) {
            if (checkPermission() == PermissionInformation.PERMISSION_NOT_GRANTED) {
                throw new AccessDeniedException("Shizuku Permission Not Granted");
            } else if (checkPermission() == PermissionInformation.SERVICE_NOT_AVAILABLE) {
                throw new AccessDeniedException("Shizuku service offline or Shizuku App Not Installed");
            }
            return;
        }
        if (command == null || command.trim().isEmpty()) {
            return;
        }
        if (mShizukuShell != null && mShizukuShell.isBusy()) {
            return;
        }
        final ArrayList<String> successMessages = new ArrayList<>(), errorMessages = new ArrayList<>();
        executionProcessListener.onPreExecute();
        runShellCommand(command.replace("\n", ""), executionProcessListener, successMessages, errorMessages);
    }

    private void runShellCommand(String command, ExecutionProcessListener executionProcessListener, ArrayList<String> successMessages, ArrayList<String> errorMessages) {
        String finalCommand;
        if (command.startsWith("adb shell ")) {
            finalCommand = command.replace("adb shell ", "");
        } else if (command.startsWith("adb -d shell ")) {
            finalCommand = command.replace("adb -d shell ", "");
        } else {
            finalCommand = command;
        }

        if (finalCommand.equals("clear")) {
            successMessages.add("Successfully cleared console.");
            return;
        }

        ExecutorService mExecutors = Executors.newSingleThreadExecutor();
        mExecutors.execute(() -> {
            if (Shizuku.checkSelfPermission() == PERMISSION_GRANTED) {
                mShizukuShell = new ShizukuShell(finalCommand);
                mShizukuShell.exec(successMessages, errorMessages, executionProcessListener);
                try {
                    TimeUnit.MILLISECONDS.sleep(250);
                } catch (InterruptedException ignored) {
                }
            }

            if (!mExecutors.isShutdown()) mExecutors.shutdown();
            new Handler(Looper.getMainLooper()).post(() -> executionProcessListener.onPostExecute(successMessages, errorMessages));
        });
    }
}