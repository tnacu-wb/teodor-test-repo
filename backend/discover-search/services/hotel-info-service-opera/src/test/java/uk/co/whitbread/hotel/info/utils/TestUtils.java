package uk.co.whitbread.hotel.info.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;

/**
 * Utility class containing useful methods for tests.
 */
public final class TestUtils {

    private static final String DIR_DATA = "/__files/";

    /**
     * Returns the contents of the specified file within the data directory, throwing
     * an exception if the file's URL can't be constructed, or if an IO exception
     * occurs while reading the file.
     *
     * @param fileName The name of the file in the 'resources/data' folder.
     * @return The content of the file with the given name.
     *
     * @throws Exception If anything goes wrong while reading the file.
     */
    public static String getDataFileContent(String fileName) throws Exception {
        return getFileContent(DIR_DATA + fileName);
    }

    /**
     * Returns the contents of the specified file, throwing an exception if the file's URL
     * can't be constructed, or if an IO exception occurs while reading the file.
     *
     * @param fileName The name of the file in the 'resources' folder.
     * @return The content of the file with the given name.
     *
     * @throws Exception If anything goes wrong while reading the file.
     */
    public static String getFileContent(String fileName) throws Exception {
        if (fileName != null && !fileName.startsWith("/")) {
            fileName = "/" + fileName;
        }
        URL url = TestUtils.class.getResource(fileName);
        java.nio.file.Path resPath = java.nio.file.Paths.get(url.toURI());
        return new String(java.nio.file.Files.readAllBytes(resPath), "UTF8");
    }

    public static List<String> getDataFileListContent(String fileName) throws Exception {
        String content = getFileContent(DIR_DATA + fileName);
        JSONArray jsonArray = new JSONArray(content);
        List<String> result = new ArrayList<>();
        for(int i=0; i < jsonArray.length(); i++){
            result.add(jsonArray.get(i).toString());
        }
        return result;

    }
}
