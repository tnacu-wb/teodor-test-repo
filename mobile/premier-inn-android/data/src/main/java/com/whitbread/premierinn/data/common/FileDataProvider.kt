package com.whitbread.premierinn.data.common

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import javax.inject.Inject

class FileDataProvider @Inject constructor(val context: Context) {

    fun loadFileFromAsset(filename: String): String? {
        val inputStream: InputStream?
        try {
            inputStream = context.assets.open(filename)
        } catch (e: IOException) {
            throw e
        }

        return convertInputStreamToString(inputStream)
    }

    private fun convertInputStreamToString(inputStream: InputStream): String {
        val result: String
        try {
            val size = inputStream.available()
            val buffer = ByteArray(size)

            inputStream.read(buffer)
            inputStream.close()

            result = String(buffer, Charset.forName("UTF-8"))
        } catch (e: IOException) {
            throw e
        }

        return result
    }

    fun loadFileFromAssetGQL(filename: String): String {
        val inputStream: InputStream?
        try {
            inputStream = context.assets.open(filename)
        } catch (e: IOException) {
            throw e
        }

        return getFileContents(inputStream)
    }

    private fun getFileContents(inputStream: InputStream): String {
        val queryBuffer = StringBuilder()
        try {
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                queryBuffer.append(line)
            }
            inputStreamReader.close()
            bufferedReader.close()
        } catch (e: IOException) {
            throw e
        }
        return queryBuffer.toString()
    }
}