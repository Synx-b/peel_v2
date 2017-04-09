package banana.com.thepeel;


import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.webkit.HttpAuthHandler;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class json_helper {

    private InputStream is = null;


    public String getJsonFromURL(String _url){
        try{
            URL url = new URL(_url);
            is = url.openConnection().getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

            String line = "";
            while((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line + "\n");
            }
            return stringBuffer.toString();

        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException q) {
            q.printStackTrace();
        }
        return null;
    }
}
