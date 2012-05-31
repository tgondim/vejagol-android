package com.vejagol.android.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vejagol.android.R;

public class OrdenacaoAdapter extends BaseAdapter {

	private Context context;
	private CharSequence[] listaOrdenacao;
	
	public OrdenacaoAdapter(Context newContext, CharSequence[] newListaOrdenacao) {
		this.context = newContext;
		this.listaOrdenacao = newListaOrdenacao;
	}
	
	@Override
	public int getCount() {
		return this.listaOrdenacao.length;
	}

	@Override
	public Object getItem(int position) {
		return this.listaOrdenacao[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		CharSequence ordenacao = listaOrdenacao[position];
		
		View view = LayoutInflater.from(context).inflate(R.layout.linha_ordenacao, null);
		
		TextView txtOrdenacao = (TextView)view.findViewById(R.id.txtOrdenacao);
		txtOrdenacao.setText(ordenacao);
		txtOrdenacao.setTextColor(context.getResources().getColor(R.color.texto_linha_ordenacao));
		
		return view;
	}
	
}
