package com.vejagol.android.gui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;

import com.vejagol.android.R;
import com.vejagol.android.model.Jogo;

public class JogoAdapter extends BaseAdapter {

	private Context context;
	private List<Jogo> listaJogos;
	public static HashMap<String, Integer> bandeiras;
	
	static {
		  bandeiras = new HashMap<String, Integer>();
		  bandeiras.put("Brasil", R.drawable.br);
		  bandeiras.put("Argentina", R.drawable.ar);
		  bandeiras.put("Portugal", R.drawable.pt);
		  bandeiras.put("Italia", R.drawable.it);
		  bandeiras.put("Romenia", R.drawable.ro);
		  bandeiras.put("Grecia", R.drawable.gr);
		  bandeiras.put("Ucrania", R.drawable.ua);
		  bandeiras.put("Espanha", R.drawable.es);
		  bandeiras.put("Turquia", R.drawable.tr);
		  bandeiras.put("Bosnia", R.drawable.ba);
		  bandeiras.put("Alemanha", R.drawable.de);
		  bandeiras.put("Peru", R.drawable.pe);
		  bandeiras.put("Polonia", R.drawable.pl);
		  bandeiras.put("Franca", R.drawable.fr);
		  bandeiras.put("Inglaterra", R.drawable.england);
		  bandeiras.put("Chipre", R.drawable.cy);
		  bandeiras.put("Sui", R.drawable.ch);
		  bandeiras.put("Bulgaria", R.drawable.bg);
		  bandeiras.put("Montenegro", R.drawable.montenegro);
		  bandeiras.put("Holanda", R.drawable.nl);
		  bandeiras.put("Russia", R.drawable.ru);
		  bandeiras.put("Mexico", R.drawable.mx);
		  bandeiras.put("Costa Rica", R.drawable.cr);
		  bandeiras.put("Colombia", R.drawable.co);
		  bandeiras.put("Equador", R.drawable.eq);
		  bandeiras.put("Bolivia", R.drawable.bo);
		  bandeiras.put("Arabia Saudita", R.drawable.sarab);
		  bandeiras.put("Estados Unidos", R.drawable.us);
		  bandeiras.put("Marrocos", R.drawable.marocco);
		  bandeiras.put("Israel", R.drawable.il);
		  bandeiras.put("Rep", R.drawable.cz);
		  bandeiras.put("Champions League", R.drawable.champ);
		  bandeiras.put("Irao", R.drawable.iran);
		  bandeiras.put("Servia", R.drawable.sb);
		  bandeiras.put("FIFA", R.drawable.fifa);
		  bandeiras.put("Paraguay", R.drawable.py);
		  bandeiras.put("Croacia", R.drawable.hr);
		  bandeiras.put("Venezuela", R.drawable.ve);
		  bandeiras.put("Guatemala", R.drawable.guatemala);
		  bandeiras.put("Belgica", R.drawable.be);
		  bandeiras.put("Australia", R.drawable.au);
		  bandeiras.put("Japao", R.drawable.jp);
		  bandeiras.put("Chile", R.drawable.cl);
		  bandeiras.put("Coreia do Sul", R.drawable.south_korea);
		  bandeiras.put("Austria", R.drawable.at);
		  bandeiras.put("Outras Ligas", R.drawable.world);
		  bandeiras.put("Uruguai", R.drawable.uy);
		  bandeiras.put("Dinamarca", R.drawable.dk);
		  bandeiras.put("Liga Europa", R.drawable.europa);
		  bandeiras.put("Hungria", R.drawable.hu);
		  bandeiras.put("Latvia", R.drawable.latvia);
		  bandeiras.put("Noruega", R.drawable.no);
		  bandeiras.put("Finlandia", R.drawable.finland);
		  bandeiras.put("Suecia", R.drawable.se);
		  bandeiras.put("UEFA", R.drawable.uefa);
		  bandeiras.put("Amigavel", R.drawable.friendly);
		  bandeiras.put("Copa Libertadores", R.drawable.libertadores);
	}
	
	public JogoAdapter(Context newContext) {
		this.context = newContext;
		this.listaJogos = new ArrayList<Jogo>();
	}
	
	@Override
	public int getCount() {
		return this.listaJogos.size();
	}

	@Override
	public Object getItem(int position) {
		return this.listaJogos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return this.listaJogos.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Resources resources = context.getResources();
		Jogo jogo = listaJogos.get(position);
		
		View view = LayoutInflater.from(context).inflate(R.layout.linha_jogo, null);
		
		RelativeLayout relativeLayout = (RelativeLayout)view.findViewById(R.id.rlLinhaVejaGol);
		TextView txtData = (TextView)view.findViewById(R.id.txtData);
		TextView txtLiga = (TextView)view.findViewById(R.id.txtLiga);
		ImageView ivBandeira = (ImageView)view.findViewById(R.id.ivBandeira);
		TextView txtPlacar = (TextView)view.findViewById(R.id.txtPlacar);
		
		String data = jogo.getData().get(Calendar.DAY_OF_MONTH) + "/" 
						+ (jogo.getData().get(Calendar.MONTH)+1) + "/" 
						+ jogo.getData().get(Calendar.YEAR);
		String hora = String.format("%02d", jogo.getData().get(Calendar.HOUR)) + ":" 
						+ String.format("%02d", jogo.getData().get(Calendar.MINUTE)) 
						+ (jogo.getData().get(Calendar.AM) == 0 ? "am" : "pm");
		
		txtData.setText(data + " - " + hora);
		txtData.setTextColor(context.getResources().getColor(R.color.texto_linha_data));
		Log.i("JogoAdapter", "data=" + data + " hora=" + hora);
		Integer bandeiraId = JogoAdapter.bandeiras.get(jogo.getLiga());
		if (bandeiraId == null) {
			bandeiraId = R.drawable.world;;
		}
		ivBandeira.setImageDrawable(resources.getDrawable(bandeiraId));
		txtLiga.setText(jogo.getLiga());
		txtLiga.setTextColor(context.getResources().getColor(R.color.texto_linha_liga));
		txtPlacar.setText(jogo.getTimeCasa() + " " 
				+ String.valueOf(jogo.getPlacarCasa()) 
				+ " X " 
				+ String.valueOf(jogo.getPlacarVisitante()) 
				+ " " + jogo.getTimeVisitante());
		txtPlacar.setTextColor(context.getResources().getColor(R.color.texto_linha_jogo));
		if (!jogo.getCampeonato().equals("")) {
			TextView txtCampeonato = new TextView(context);
			relativeLayout.addView(txtCampeonato);
			txtCampeonato.setVisibility(View.VISIBLE);
			txtCampeonato.setGravity(Gravity.CENTER);
			LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(layoutParams);
			relativeLayoutParams.addRule(RelativeLayout.BELOW, txtPlacar.getId());
			txtCampeonato.setLayoutParams(relativeLayoutParams);
			txtCampeonato.setTextSize(TypedValue.COMPLEX_UNIT_PX, 20);
			txtCampeonato.setText(jogo.getCampeonato());
			txtCampeonato.setTextColor(context.getResources().getColor(R.color.texto_linha_campeonato));
			view.setTag(jogo.getId());
		} 		
		view.setBackgroundResource(R.color.lista_jogos);
		return view;
	}
	
	public void insert(Jogo newJogo) {
		this.listaJogos.add(newJogo);
	}
	
}
