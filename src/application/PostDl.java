package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

import javafx.event.ActionEvent;

public class PostDl {
	private String printloc;
	private String printamount;
	private String printtype;
	private String status;
	CloseableHttpClient httpclient = HttpClientBuilder.create().build();
	public File DownLoad(String id) throws ClientProtocolException, IOException{
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", "bixi"));
        params.add(new BasicNameValuePair("password", "bixi"));
        String url = "http://localhost:8080/user/login";
	    HttpPost httppost = new HttpPost(url);
		httppost.setEntity(new UrlEncodedFormEntity(params));
		CloseableHttpResponse response = httpclient.execute(httppost);
					httppost.releaseConnection();
					List<NameValuePair> docparams = new ArrayList<NameValuePair>();
					String urldoc = "http://localhost:8080/document/info";
					docparams.add(new BasicNameValuePair("id", id));
					HttpPost httppostdoc = new HttpPost(urldoc);
					httppostdoc.setEntity(new UrlEncodedFormEntity(docparams));
					response = httpclient.execute(httppostdoc);
					String strresponse = EntityUtils.toString(response.getEntity());
					JSONObject jsonObject = JSONObject.parseObject(strresponse);
					status = jsonObject.getString("status");
					String fileloc = jsonObject.getString("url");
					printtype = jsonObject.getString("printtype");
					printamount = jsonObject.getString("amount");
					printloc = "C:/demosys/" + fileloc;
					httppostdoc.releaseConnection();
					String geturl = "http://localhost:8080/filestorage/" + fileloc;
			        HttpGet httpget = new HttpGet(geturl);
			        response = httpclient.execute(httpget);      
		            HttpEntity entity = response.getEntity();
		            InputStream is = entity.getContent(); 
		            File file = new File(printloc);  
		            file.getParentFile().mkdirs();  
		            FileOutputStream fileout = new FileOutputStream(file);  
		            /** 
		             * 根据实际运行效果 设置缓冲区大小 
		             */  
		            byte[] buffer=new byte[10 * 1024];  
		            int ch = 0;  
		            while ((ch = is.read(buffer)) != -1) {  
		                fileout.write(buffer,0,ch);  
		            }  
		            is.close();  
		            fileout.flush();  
		            fileout.close();  
					httpget.releaseConnection();
					return file;
	}
	public boolean Print(){
		
		
		return true;
	}

}
