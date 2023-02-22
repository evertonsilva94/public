package com.desafiojava.springboot.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.desafiojava.springboot.model.Endereco;
import com.desafiojava.springboot.model.Pessoa;
import com.desafiojava.springboot.repository.EnderecoRepository;

@Service
public class EnderecoService {
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Transactional
	public List<Endereco> SalvaEndereco(List<Endereco> enderecos) {
	
		try {
			return enderecoRepository.saveAllAndFlush(enderecos);
		} catch (Exception e) {
			return null;
		}	
	}
	
	public List<Endereco> VinculaPessoasAoEndereco(List<Endereco> enderecos, Pessoa pessoa) {

		List<Endereco> enderecosEditados = new ArrayList<>();
		
		for (Endereco endereco : enderecos) {
			
			List<Pessoa> pessoas = new ArrayList<>();
			pessoas.add(pessoa);
			
			endereco.setPessoas(pessoas);
			enderecosEditados.add(endereco);
		}
		
		return enderecosEditados;
	}
}