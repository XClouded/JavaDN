package es.hol.media.plugin;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * HttpRequest: Dumps to OutputStream
 */
public class HttpRequest {
    public static final String CONTENT_LENGTH = "Content-Length";

    public static boolean dump(String url, RandomAccessFile out) throws Exception {

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        HttpResponse response = httpClient.execute(httpGet);

        Integer statusCode = response.getStatusLine().getStatusCode();
        Header contentLength = response.getFirstHeader(CONTENT_LENGTH);
        Long remoteFileSize = Long.valueOf(contentLength.getValue());

        HttpEntity httpEntity = response.getEntity();

        InputStream is = httpEntity.getContent();
        Long localFileSize = 0L;
        try {
            int counter;
            byte[] buffer = new byte[1024];
            while ((counter = is.read(buffer)) != -1) {
                out.write(buffer, 0, counter);
            }
            localFileSize = out.length();
        } finally {
            close(out);
            close(is);
        }
        return statusCode == 200 && localFileSize.equals(remoteFileSize);
    }

    protected static void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected static void close(RandomAccessFile out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
