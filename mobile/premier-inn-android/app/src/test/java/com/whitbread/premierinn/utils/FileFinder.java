package com.whitbread.premierinn.utils;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class FileFinder {

    private static final String APP_FOLDER = "app";

    /**
     * The root for test runs seems to be different across machines, picking either the root of the directory, or the app folder in the
     * directory as its running location. This code looks in both locations for the file.
     */
    @SuppressWarnings({"checkstyle:emptystatement", "checkstyle:emptyblock"})
    public static FileInputStream getFileInputStream(@NonNull String fileLocationRelativeToAppFolder) {
        for (String streamLocation : Arrays.asList(fileLocationRelativeToAppFolder,
                APP_FOLDER + File.separator + fileLocationRelativeToAppFolder)) {
            try {
                return new FileInputStream(streamLocation);
            } catch (FileNotFoundException e) {
                // Do Nothing
            }
        }
        return null;
    }

    /**
     * The root for test runs seems to be different across machines, picking either the root of the directory, or the app folder.
     * Given a file location relative to the app folder, this method will look at the location from which the test is being run
     * and give the desired path relative to the current location.
     */
    public static String createPathFromHere(@NonNull String fileLocationRelativeToAppFolder) throws IOException {
        String currentLocation = new File(".").getCanonicalPath();
        String[] pathChunks = currentLocation.split(File.separator);
        if (pathChunks[pathChunks.length - 1].equals(APP_FOLDER)) {
            return fileLocationRelativeToAppFolder;
        }
        return APP_FOLDER + File.separator + fileLocationRelativeToAppFolder;
    }
}
