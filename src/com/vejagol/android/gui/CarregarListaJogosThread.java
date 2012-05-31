package com.vejagol.android.gui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.vejagol.android.model.Jogo;

import android.util.Log;

public class CarregarListaJogosThread implements Runnable {

	private ArrayList<Jogo> listaJogos;
	
	private GetListaJogosWSCallBack callBack;
	
	private String urlWebService; 
	
	public CarregarListaJogosThread(ArrayList<Jogo> newListaJogos, String newUrlWebService, GetListaJogosWSCallBack newCallBack) {
		this.listaJogos = newListaJogos;
		this.urlWebService = newUrlWebService;
		this.callBack = newCallBack;
	}
	
	public void run() {
		SoapObject soap = new SoapObject("http://webservices.vejagol.thgg.com.br", "getListaJogos");
		
		soap.addProperty("ordem", "data");		
		soap.addProperty("ascending", false);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		
		envelope.setOutputSoapObject(soap);
		Log.i("THGG", "Chamando getListaJogos");
		
		HttpTransportSE httpTransport = new HttpTransportSE(urlWebService);
		try {
			httpTransport.call("", envelope);
			SoapObject results = (SoapObject)envelope.bodyIn;
			int count = results.getPropertyCount();
			synchronized (listaJogos) {
				Jogo jogo;
				String strData;
				Calendar data = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");  
				
				for (int i = 0; i < count; i++) {
					SoapObject obj = (SoapObject)results.getProperty(i);
					strData = obj.getProperty("data").toString();
					data.setTime(sdf.parse(strData));
					jogo = new Jogo(data, 
							obj.getProperty("timeCasa").toString(),
							obj.getProperty("timeVisitante").toString(),
							Integer.valueOf(obj.getProperty("placarCasa").toString()),
							Integer.valueOf(obj.getProperty("placarVisitante").toString()),
							(!obj.getProperty("campeonato").toString().equals("anyType{}") ? obj.getProperty("campeonato").toString(): ""),
							(!obj.getProperty("liga").toString().equals("anyType{}") ? obj.getProperty("liga").toString() : ""),
							obj.getProperty("link").toString());
					this.listaJogos.add(jogo);
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.callBack.returnServiceResponse();
	}
	
}
