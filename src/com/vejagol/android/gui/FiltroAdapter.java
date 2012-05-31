package com.vejagol.android.gui;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.vejagol.android.R;

public class FiltroAdapter extends BaseAdapter {

	private Context context;
	private List<String> listaFiltros;
	private List<String> listaFiltrosSelecionados;
	
	public FiltroAdapter(Context newContext, List<String> newListaFiltro, List<String> newListaFiltrosSelecionados) {
		this.context = newContext;
		this.listaFiltros = newListaFiltro;
		this.listaFiltrosSelecionados = newListaFiltrosSelecionados; 
	}
	
	@Override
	public int getCount() {
		return this.listaFiltros.size();
	}

	@Override
	public Object getItem(int position) {
		return this.listaFiltros.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Resources resources = context.getResources();
		String filtro = listaFiltros.get(position);
		
		View view = LayoutInflater.from(context).inflate(R.layout.linha_filtro, null);
		
		ImageView ivBandeiraFiltro = (ImageView)view.findViewById(R.id.ivBandeiraFiltro);
		CheckBox cbFiltro = (CheckBox)view.findViewById(R.id.cbFiltro);
		cbFiltro.setText(filtro);
		cbFiltro.setTextScaleX(1.3F);
		cbFiltro.setTextColor(R.color.texto_linha_filtro);
		
		if (this.listaFiltrosSelecionados.contains(filtro)) {
			cbFiltro.setChecked(true);
		}
		cbFiltro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					FiltroAdapter.this.listaFiltrosSelecionados.add(buttonView.getText().toString());
				} else {
					FiltroAdapter.this.listaFiltrosSelecionados.remove(buttonView.getText().toString());
				}
			}
		});
		ivBandeiraFiltro.setImageDrawable(resources.getDrawable(JogoAdapter.bandeiras.get(filtro)));
		
		return view;
	}

	public List<String> getListaFiltrosSelecionados() {
		return listaFiltrosSelecionados;
	}

	public void setListaFiltrosSelecionados(List<String> listaFiltrosSelecionados) {
		this.listaFiltrosSelecionados = listaFiltrosSelecionados;
	}
	
}
