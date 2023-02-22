package com.desafiojava.springboot.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.desafiojava.springboot.model.Endereco;
import com.desafiojava.springboot.model.Pessoa;
import com.desafiojava.springboot.repository.PessoaRepository;

@Service
public class PessoaService {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	public List<Pessoa> BuscaTodasPessoas(){
		return pessoaRepository.findAll();
	}
	
	public Pessoa BuscaPessoaPorId(Long id) {
		try {			
			Pessoa pessoa = pessoaRepository.findById(id).get();		
			return pessoa;
		} catch (Exception e) {
			return null;
		}
	}
	
	@Transactional
	public Pessoa SalvaPessoa(Pessoa ps){	
		try {			
			if(this.PossuiUmEnderecoPrincipal(ps)) {			
				return pessoaRepository.saveAndFlush(ps);
			}
		} catch (Exception e) {
			return null;
		}
		
		return null;		
	}	
	
	public boolean PossuiUmEnderecoPrincipal(Pessoa pessoa) {
		int enderecoPrincipal = 0;
		
		for (Endereco endereco : pessoa.getEnderecos()) {
			if(endereco.isPrincipal()) {
				enderecoPrincipal++;
			}
		}
		
		return enderecoPrincipal == 1;
	}
}