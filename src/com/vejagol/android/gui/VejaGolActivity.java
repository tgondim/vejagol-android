package com.vejagol.android.gui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.StringTokenizer;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.keyes.youtube.OpenYouTubePlayerActivity;
import com.vejagol.android.R;
import com.vejagol.android.model.Jogo;

public class VejaGolActivity extends Activity implements OnItemClickListener,
		GetListaJogosWSCallBack {

	private LinearLayout llVejaGol;
	
	private RelativeLayout rlTopBar;
	
	private ListView lvListaJogos;
	private ListView lvOrdenacao;
	private ListView lvFiltro;

	private JogoAdapter jogoAdapter;

	private ArrayList<Jogo> proximaPaginaListaJogos;

	private AlertDialog openDialog;

	private AlertDialog ordenacaoDialog;

	private AlertDialog filtroDialog;

	private View impossivelCarregar;

	private View filtroView;

	private View txtCarregandoListaJogos;

	private View ordenacaoView;

	private int indiceAtual;

	private String urlWebService;

	private boolean loading;

	private boolean inicioConsulta;

	private boolean erroConsulta;

	private boolean getListaJogosWebServiceResponseOk;

	private boolean getUltimoMelhorDaSemanaWebServiceResponseOk;
	
	private String linkUltimoMelhorDaSemana;

	private Toast toast;

	private SharedPreferences prefs;
	
	private ImageView ivTwitter;
	private ImageView ivFacebook;
	
	private Handler callBackHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (VejaGolActivity.this.getListaJogosWebServiceResponseOk) {				
				carregarListaJogos(VejaGolActivity.this.getListaJogosWebServiceResponseOk);
				VejaGolActivity.this.getListaJogosWebServiceResponseOk = false;
				VejaGolActivity.this.impossivelCarregar.setVisibility(View.GONE);
				
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				if (VejaGolActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					lp.weight = 2f;						
				} else {
					lp.weight = 6f;			
				}
				VejaGolActivity.this.rlTopBar.setLayoutParams(lp);
				
				VejaGolActivity.this.erroConsulta = false;
//				VejaGolActivity.this.lvListaJogos.removeFooterView(VejaGolActivity.this.impossivelCarregar);
			} else if (VejaGolActivity.this.getUltimoMelhorDaSemanaWebServiceResponseOk) {
				if (VejaGolActivity.this.linkUltimoMelhorDaSemana != "") {
					Intent melhorDaSemanaIntent = null;
					
					if (VejaGolActivity.this.linkUltimoMelhorDaSemana.toLowerCase().contains("youtube")) {
						String[] auxId = VejaGolActivity.this.linkUltimoMelhorDaSemana.split("/");
						String youtubeId = auxId[(auxId.length - 1)];
						melhorDaSemanaIntent = new Intent(null, Uri.parse("ytv://"+youtubeId), VejaGolActivity.this, OpenYouTubePlayerActivity.class);
						melhorDaSemanaIntent.putExtra("com.keyes.video.msg.init", getResources().getString(R.string.video_init));
						melhorDaSemanaIntent.putExtra("com.keyes.video.msg.detect", getResources().getString(R.string.video_detect));
						melhorDaSemanaIntent.putExtra("com.keyes.video.msg.token", getResources().getString(R.string.video_token));
						melhorDaSemanaIntent.putExtra("com.keyes.video.msg.loband", getResources().getString(R.string.video_loband));
						melhorDaSemanaIntent.putExtra("com.keyes.video.msg.hiband", getResources().getString(R.string.video_hiband));
						melhorDaSemanaIntent.putExtra("com.keyes.video.msg.error.title", getResources().getString(R.string.video_error_title));
						melhorDaSemanaIntent.putExtra("com.keyes.video.msg.error.msg", getResources().getString(R.string.video_error_msg));
					} else {
						melhorDaSemanaIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(VejaGolActivity.this.linkUltimoMelhorDaSemana));
					}
					if (melhorDaSemanaIntent != null) {
						startActivity(melhorDaSemanaIntent);
					}
				} else {
					mostraToast(getResources().getString(R.string.erro_de_conexao));
				}
				VejaGolActivity.this.getUltimoMelhorDaSemanaWebServiceResponseOk = false;
			} else {
				//mostraToast(getResources().getString(R.string.erro_de_conexao));
			}
			if ((VejaGolActivity.this.jogoAdapter == null) || (VejaGolActivity.this.jogoAdapter.getCount() == 0)){
				VejaGolActivity.this.impossivelCarregar.setVisibility(View.VISIBLE);
				
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				if (VejaGolActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					lp.weight = 1.5f;						
				} else {
					lp.weight = 4.3f;			
				}
				VejaGolActivity.this.rlTopBar.setLayoutParams(lp);
				
				VejaGolActivity.this.erroConsulta = true;
				VejaGolActivity.this.inicioConsulta = true;
//				VejaGolActivity.this.lvListaJogos.addFooterView(VejaGolActivity.this.impossivelCarregar);
			}
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);			
		
		this.toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
		
		this.ivTwitter = (ImageView) findViewById(R.id.ivTwitter);
		this.ivTwitter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.content.Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

				//Add data to the intent, the receiving app will decide what to do with it.
				intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_subject));
				intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msgbody));
				startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_via)));
			}
		});

		this.ivFacebook = (ImageView) findViewById(R.id.ivFacebook);
		this.ivFacebook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.content.Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				
				//Add data to the intent, the receiving app will decide what to do with it.
				intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_subject));
				intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msgbody));
				startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_via)));
			}
		});
		
		this.lvListaJogos = (ListView) findViewById(R.id.listaJogos);

		this.txtCarregandoListaJogos = LayoutInflater.from(this).inflate(
				R.layout.carregando_lista_jogos, null);
		this.lvListaJogos.addFooterView(txtCarregandoListaJogos);
		this.txtCarregandoListaJogos.setVisibility(View.GONE);

		this.lvListaJogos
				.setOnScrollListener(new AbsListView.OnScrollListener() {

					@Override
					public void onScrollStateChanged(AbsListView view,
							int scrollState) {
					}

					@Override
					public void onScroll(AbsListView view,
							int firstVisibleItem, int visibleItemCount,
							int totalItemCount) {
						if (VejaGolActivity.this.proximaPaginaListaJogos != null) {
							if (VejaGolActivity.this.txtCarregandoListaJogos.getVisibility() != View.VISIBLE) {
								boolean loadMore = (firstVisibleItem + visibleItemCount) >= totalItemCount;
								if ((totalItemCount != 0)
										&& (!VejaGolActivity.this.loading) && loadMore) {
									if (VejaGolActivity.this.jogoAdapter.getCount() >= 15) {
										VejaGolActivity.this.txtCarregandoListaJogos
												.setVisibility(View.VISIBLE);
										VejaGolActivity.this.inicioConsulta = false;
										if (VejaGolActivity.this.proximaPaginaListaJogos
												.size() >= 15) {
											buscarJogos();
										} else {
											VejaGolActivity.this.txtCarregandoListaJogos
													.setVisibility(View.GONE);
										}
									}
								}
						}
						}
					}
				});

		this.llVejaGol = (LinearLayout) findViewById(R.id.llVejaGol);
		this.rlTopBar = (RelativeLayout) findViewById(R.id.rlTopBar);
		
		impossivelCarregar = LayoutInflater.from(VejaGolActivity.this).inflate(R.layout.impossivel_carregar, null);
		impossivelCarregar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				buscarJogos();
			}
		});
		this.llVejaGol.addView(impossivelCarregar, 0);
		impossivelCarregar.setVisibility(View.GONE);
		
		if (savedInstanceState == null) {
			this.indiceAtual = 0;
			this.inicioConsulta = true;
			this.erroConsulta = false;
			
			
			if (getPrefs().getBoolean("firstTime", true)) {
				SharedPreferences.Editor editor = getPrefs().edit();
				editor.putBoolean("firstTime", false);
				editor.putString("ordenacao", "data");
				editor.putBoolean("ascending", false);
				
	//			ArrayList<String> listaFiltro = new ArrayList<String>(JogoAdapter.bandeiras.keySet());
	//			Collections.sort(listaFiltro);
	//			StringBuffer sb = new StringBuffer();
	//			for (String filtro : listaFiltro) {
	//				sb.append(filtro + ";");
	//			}
				editor.putString("filtro", "");
				editor.commit();
			}
			buscarJogos();	
		} else {
			this.erroConsulta = savedInstanceState.getBoolean("erroConsulta");
			if (erroConsulta) {
				this.impossivelCarregar.setVisibility(View.VISIBLE);
			} else {
				this.indiceAtual = savedInstanceState.getInt("indiceAtual");
				this.inicioConsulta = savedInstanceState.getBoolean("inicioConsulta");
				this.jogoAdapter = new JogoAdapter(this);
				lvListaJogos.setAdapter(jogoAdapter);
				lvListaJogos.setOnItemClickListener(this);
				registerForContextMenu(this.lvListaJogos);
				
				this.proximaPaginaListaJogos = (ArrayList<Jogo>)savedInstanceState.getSerializable("proximaPaginaListaJogos");
				ArrayList<Jogo> auxListaJogo = (ArrayList<Jogo>)savedInstanceState.getSerializable("listaJogos");
				if (auxListaJogo != null) {
					jogoAdapter.getListaJogos().clear();
					for (Jogo j : auxListaJogo) {
						try {
							jogoAdapter.insert((Jogo) j.clone());
						} catch (CloneNotSupportedException e) {
							e.printStackTrace();
						}
					}
					jogoAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (this.jogoAdapter != null) {
			if (this.jogoAdapter.getListaJogos() != null && this.jogoAdapter.getListaJogos().size() > 0) {
				outState.putSerializable("listaJogos", (ArrayList<Jogo>)this.jogoAdapter.getListaJogos());
			}
		}
		if (this.proximaPaginaListaJogos != null && this.proximaPaginaListaJogos.size() > 0) {
			outState.putSerializable("proximaPaginaListaJogos", this.proximaPaginaListaJogos);
		}
		outState.putInt("indiceAtual", this.indiceAtual);
		outState.putBoolean("erroConsulta", this.erroConsulta);
		outState.putBoolean("inicioConsulta", this.inicioConsulta);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, getResources().getString(R.string.ordenacao));
		menu.add(0, 2, 0, getResources().getString(R.string.paises));
		menu.add(0, 3, 0, getResources().getString(R.string.melhor_da_semana));
//		menu.add(0, 4, 0, getResources().getString(R.string.opcoes));
		menu.findItem(1).setIcon(getResources().getDrawable(R.drawable.organizar));
		menu.findItem(2).setIcon(getResources().getDrawable(R.drawable.filtrar));
		menu.findItem(3).setIcon(getResources().getDrawable(R.drawable.melhor));
//		menu.findItem(4).setIcon(getResources().getDrawable(R.drawable.opcoes));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			getOrdenacaoDialog().show();
			break;
		case 2:
			getFiltroDialog().show();
			break;
		case 3:
			verMelhorDaSemana();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent videoIntent = null;
		Jogo jogo = (Jogo) this.lvListaJogos.getAdapter().getItem(position);
		
		if (jogo != null) {
			if (jogo.getLink().toLowerCase().contains("youtube")) {
				String[] auxId = jogo.getLink().split("/");
				String youtubeId = auxId[(auxId.length - 1)];
				videoIntent = new Intent(null, Uri.parse("ytv://"+youtubeId), VejaGolActivity.this, OpenYouTubePlayerActivity.class);
				videoIntent.putExtra("com.keyes.video.msg.init", getResources().getString(R.string.video_init));
				videoIntent.putExtra("com.keyes.video.msg.detect", getResources().getString(R.string.video_detect));
				videoIntent.putExtra("com.keyes.video.msg.token", getResources().getString(R.string.video_token));
				videoIntent.putExtra("com.keyes.video.msg.loband", getResources().getString(R.string.video_loband));
				videoIntent.putExtra("com.keyes.video.msg.hiband", getResources().getString(R.string.video_hiband));
				videoIntent.putExtra("com.keyes.video.msg.error.title", getResources().getString(R.string.video_error_title));
				videoIntent.putExtra("com.keyes.video.msg.error.msg", getResources().getString(R.string.video_error_msg));
			} else {
				videoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(jogo.getLink()));
			}
			if (videoIntent != null) {
				startActivity(videoIntent);
			}
		}
	}

	@Override
	public void returnServiceResponse() {
		if (openDialog != null) {
			openDialog.dismiss();
		}
		callBackHandler.sendEmptyMessage(0);
	}

	private void verMelhorDaSemana() {
		this.urlWebService = getResources().getString(R.string.url_webservice) + "/" + getResources().getString(R.string.get_ultimo_melhor_da_semana_webservice_operation);
		mostrarDialogEsperaConsultaWebService();
		new Thread(new ConsultaGetMelhorDaSemanaWS()).start();		
	}
	
	private void buscarJogos() {
		this.urlWebService = getResources().getString(R.string.url_webservice) + "/" + getResources().getString(R.string.listar_jogos_webservice_operation); 
//		this.urlWebService = "http://192.168.2.17:8080/VejaGolWS/services/VejaGolWS/getListaJogos";
//		this.urlWebService = "http://10.0.2.2:8080/VejaGolWS/services/VejaGolWS/getListaJogos";
//		this.urlWebService = "http://tgondim.dyndns.info:8080/VejaGolWS/services/VejaGolWS/getListaJogos";
		
		if (this.inicioConsulta) {
			this.indiceAtual = 0;
			mostrarDialogEsperaConsultaWebService();
		}
		
		new Thread(new ConsultaGetListaJogosWS()).start();
	}

	private void mostrarDialogEsperaConsultaWebService() {
		AlertDialog.Builder builder =  new AlertDialog.Builder(this);  
		this.openDialog = builder.create();
		this.openDialog.show();
		this.openDialog.setContentView(R.layout.carregando_vejagol);
	}
	
	private void mostraToast(String msg) {
		VejaGolActivity.this.toast.setText(msg);
		VejaGolActivity.this.toast.cancel();
		VejaGolActivity.this.toast.show();
	};

	private AlertDialog getFiltroDialog() {
		if (filtroDialog == null) {
			filtroDialog = new AlertDialog.Builder(this)
					.setTitle(getResources().getString(R.string.paises))
					.setView(getFiltroView())
					.setPositiveButton(getResources().getString(R.string.confirmar),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									StringBuffer sb = new StringBuffer();
									for (String filtro : ((FiltroAdapter) VejaGolActivity.this.lvFiltro.getAdapter()).getListaFiltrosSelecionados()) {
										sb.append(filtro + ";");
									}
									SharedPreferences.Editor editor = getPrefs().edit();
									editor.putString("filtro", sb.toString());	
									editor.commit();
									getFiltroDialog().dismiss();
									VejaGolActivity.this.inicioConsulta = true;
									buscarJogos();
								}
							}).create();
		}
		return filtroDialog;
	}

	private View getFiltroView() {
		if (filtroView == null) {
			filtroView = LayoutInflater.from(this).inflate(R.layout.filtro_dialog, null);
			this.lvFiltro = (ListView) filtroView.findViewById(R.id.lvFiltro);
			ArrayList<String> listaFiltro = new ArrayList<String>(JogoAdapter.bandeiras.keySet());
			Collections.sort(listaFiltro);
			
			ArrayList<String> listaFiltroSelecionado = new ArrayList<String>();
			StringTokenizer st = new StringTokenizer(getPrefs().getString("filtro", ""), ";");
			while (st.hasMoreTokens()) {
				listaFiltroSelecionado.add(st.nextToken().trim());
			}
			
			final FiltroAdapter filtroAdapter = new FiltroAdapter(this, listaFiltro,
					listaFiltroSelecionado);
			lvFiltro.setAdapter(filtroAdapter);
		}
		return filtroView;
	}

	private AlertDialog getOrdenacaoDialog() {
		if (ordenacaoDialog == null) {
			ordenacaoDialog = new AlertDialog.Builder(this)
					.setTitle(getResources().getString(R.string.ordenar_por))
					.setView(getOrdenacaoView()).create();
		}
		return ordenacaoDialog;
	}

	private View getOrdenacaoView() {
		if (ordenacaoView == null) {
			ordenacaoView = LayoutInflater.from(this).inflate(R.layout.ordenacao_dialog, null);
			lvOrdenacao = (ListView) ordenacaoView.findViewById(R.id.lvOrdenacao);
			final OrdenacaoAdapter ordenacaoAdapter = new OrdenacaoAdapter(this, getResources().getTextArray(R.array.itens_ordenacao));
			lvOrdenacao.setAdapter(ordenacaoAdapter);
			lvOrdenacao.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					SharedPreferences.Editor editor = getPrefs().edit();
					switch (position) {
					case 0:
						mudaAscending("data");
						editor.putString("ordenacao", "data");
						break;
					case 1:
						mudaAscending("liga");
						editor.putString("ordenacao", "liga");
						break;
					case 2:
						mudaAscending("timeCasa");
						editor.putString("ordenacao", "timeCasa");
						break;
					case 3:
						mudaAscending("timeVisitante");
						editor.putString("ordenacao", "timeVisitante");
						break;
					}
					editor.commit();
					VejaGolActivity.this.inicioConsulta = true;
					buscarJogos();
					getOrdenacaoDialog().dismiss();
				}
			});
		}
		return ordenacaoView;
	}

	private void mudaAscending(String ordem) {
		if (ordem.equals(getPrefs().getString("ordenacao", ""))) {
			if (getPrefs().getBoolean("ascending", false)) {
				SharedPreferences.Editor editor = getPrefs().edit();
				editor.putBoolean("ascending", false);
				editor.commit();
			} else {
				SharedPreferences.Editor editor = getPrefs().edit();
				editor.putBoolean("ascending", true);
				editor.commit();
			}
		} else {
			SharedPreferences.Editor editor = getPrefs().edit();
			editor.putBoolean("ascending", true);
			editor.commit();
		}
	}

	@SuppressWarnings("unchecked")
	private void carregarListaJogos(boolean wsResponse) {
		if (wsResponse) {
			if (this.inicioConsulta || this.jogoAdapter == null) {
				jogoAdapter = new JogoAdapter(this);
				lvListaJogos.setAdapter(jogoAdapter);
				lvListaJogos.setOnItemClickListener(this);
				registerForContextMenu(this.lvListaJogos);
			}
			ArrayList<Jogo> auxListaJogo = (ArrayList<Jogo>) this.proximaPaginaListaJogos
					.clone();
			for (Jogo j : auxListaJogo) {
				try {
					jogoAdapter.insert((Jogo) j.clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
			jogoAdapter.notifyDataSetChanged();
		} else {
			mostraToast(getResources().getString(R.string.erro_de_conexao));
		}
		this.txtCarregandoListaJogos.setVisibility(View.GONE);
	}

	public SharedPreferences getPrefs() {
		if (this.prefs == null) {
			this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		}
		return this.prefs;
	}
	
	public class ConsultaGetListaJogosWS implements Runnable {

		@Override
		public void run() {
			VejaGolActivity.this.loading = true;
			SoapObject soap = new SoapObject(getResources().getString(
					R.string.namespace_webservice), getResources().getString(
					R.string.listar_jogos_webservice_operation));

			int pos = VejaGolActivity.this.indiceAtual;
			soap.addProperty("de", pos);
			soap.addProperty("ate", pos + 15);
			soap.addProperty("ordem", getPrefs().getString("ordenacao", ""));

//			StringBuffer sb = new StringBuffer();
//			for (String s : VejaGolActivity.this.filtrosSelecionados) {
//				sb.append(s + ";");
//			}
		
//			soap.addProperty("filtros", sb.toString());			
			soap.addProperty("filtros", getPrefs().getString("filtro", ""));			
			soap.addProperty("ascending", getPrefs().getBoolean("ascending", false));
			soap.addProperty("chave",
					getResources().getString(R.string.chave_vejagol_webservice));

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);

			envelope.setOutputSoapObject(soap);
			Log.d("VejaGol", "Chamando VejaGolWS.getListaJogos");

			HttpTransportSE httpTransport = new HttpTransportSE(urlWebService);
			try {
				VejaGolActivity.this.getListaJogosWebServiceResponseOk = false;
				httpTransport.call("", envelope);
				SoapObject results = (SoapObject) envelope.bodyIn;
				int count = results.getPropertyCount();
				if (count != 0) {
					VejaGolActivity.this.proximaPaginaListaJogos = new ArrayList<Jogo>();
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd'T'HH:mm");

					for (int i = 0; i < count; i++) {
						SoapObject obj = (SoapObject) results.getProperty(i);
						Calendar data = Calendar.getInstance();
						String strData = obj.getProperty("data").toString()
								.substring(0, 16);
						data.setTime(sdf.parse(strData));
						Jogo jogo = new Jogo(data, obj.getProperty("timeCasa")
								.toString(), obj.getProperty("timeVisitante")
								.toString(), Integer.valueOf(obj.getProperty(
								"placarCasa").toString()), Integer.valueOf(obj
								.getProperty("placarVisitante").toString()), (!obj
								.getProperty("campeonato").toString()
								.equals("anyType{}") ? obj
								.getProperty("campeonato").toString() : ""),
								(!obj.getProperty("liga").toString()
										.equals("anyType{}") ? obj.getProperty(
										"liga").toString() : ""), obj.getProperty(
										"link").toString());
						VejaGolActivity.this.proximaPaginaListaJogos.add(jogo);
					}
				} else {
					VejaGolActivity.this.proximaPaginaListaJogos.clear();
				}
				VejaGolActivity.this.indiceAtual += VejaGolActivity.this.proximaPaginaListaJogos.size();
				VejaGolActivity.this.getListaJogosWebServiceResponseOk = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			VejaGolActivity.this.loading = false;
			VejaGolActivity.this.returnServiceResponse();
		}
		
	}
	
	public class ConsultaGetMelhorDaSemanaWS implements Runnable {
		
		@Override
		public void run() {
			SoapObject soap = new SoapObject(getResources().getString(
					R.string.namespace_webservice), getResources().getString(
							R.string.get_ultimo_melhor_da_semana_webservice_operation));
			
			soap.addProperty("chave", getResources().getString(R.string.chave_vejagol_webservice));
			
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			
			envelope.setOutputSoapObject(soap);
			Log.d("VejaGol", "Chamando VejaGolWS.getUltimoMelhorDaSemana");
			
			HttpTransportSE httpTransport = new HttpTransportSE(urlWebService);
			try {
				VejaGolActivity.this.getUltimoMelhorDaSemanaWebServiceResponseOk = false;
				VejaGolActivity.this.linkUltimoMelhorDaSemana = "";
				httpTransport.call("", envelope);
				Object msg = envelope.getResponse();
				if (msg != null) {
					VejaGolActivity.this.linkUltimoMelhorDaSemana = msg.toString();
				} else {
					VejaGolActivity.this.linkUltimoMelhorDaSemana = "";
				}
				VejaGolActivity.this.getUltimoMelhorDaSemanaWebServiceResponseOk = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			VejaGolActivity.this.returnServiceResponse();
		}
		
	}
}