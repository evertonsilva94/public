package com.desafiojava.springboot.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.desafiojava.springboot.model.Endereco;
import com.desafiojava.springboot.model.Pessoa;
import com.desafiojava.springboot.service.EnderecoService;
import com.desafiojava.springboot.service.PessoaService;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {
	
	@Autowired
	private PessoaService pessoaService;
	
	@Autowired
	private EnderecoService enderecoService;

	@RequestMapping(value = "/lista", method = RequestMethod.GET)
	public List<Pessoa> Lista() {
		
		List<Pessoa> pessoasRegistradas = pessoaService.BuscaTodasPessoas();
		
		return pessoasRegistradas;
	}

	@RequestMapping(value = "/consulta/{id}", method = RequestMethod.GET)
	public ResponseEntity<Pessoa> BuscaPorId(@PathVariable("id") Long id) {
		
		Pessoa pessoa = pessoaService.BuscaPessoaPorId(id);
		
        if(pessoa != null) {     
        	return new ResponseEntity<Pessoa>(pessoa, HttpStatus.OK);            
        }else {
            return new ResponseEntity<Pessoa>(HttpStatus.NOT_FOUND);
        }
	}
	
	@RequestMapping(value = "/consultaEnderecosPorPessoa/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<Endereco>> BuscaEnderecosPorIdPessoa(@PathVariable("id") Long id) {
		
		Pessoa pessoa = pessoaService.BuscaPessoaPorId(id);
		
        if(pessoa != null) {     
        	return new ResponseEntity<List<Endereco>>(pessoa.getEnderecos(), HttpStatus.OK);            
        }else
            return new ResponseEntity<List<Endereco>>(HttpStatus.NOT_FOUND);
	}

    @RequestMapping(value = "/atualiza/{id}", method =  RequestMethod.PUT)
    public ResponseEntity<Pessoa> Atualiza(@PathVariable(value = "id") Long id, @Valid @RequestBody Pessoa pessoaASalva){
    	
        Pessoa pessoaSalva = pessoaService.BuscaPessoaPorId(id);
          
        if(pessoaSalva != null) {
        	Pessoa pessoaAtualizada = pessoaSalva;
        	pessoaAtualizada.setId(id);
        	pessoaAtualizada.setNome(pessoaASalva.getNome());
        	pessoaAtualizada.setDataNascimento(pessoaASalva.getDataNascimento());
        	pessoaAtualizada.setEnderecos(pessoaASalva.getEnderecos());
        	
        	boolean possuiSomenteUmEnderecoPrincipal = pessoaService.PossuiUmEnderecoPrincipal(pessoaAtualizada);
        	
        	if(possuiSomenteUmEnderecoPrincipal) {
        		Pessoa pessoa = pessoaService.SalvaPessoa(pessoaAtualizada);
        	         
	        	if(pessoa != null) {	                
	        		// vincula pessoa ao endereço 
	        		enderecoService.VinculaPessoasAoEndereco(pessoa.getEnderecos(), pessoa);
	        		
	        		//salva o endereço
	            	List<Endereco> enderecos = enderecoService.SalvaEndereco(pessoa.getEnderecos());
	                
	                if(enderecos != null) {
	                	// edição realizada com sucesso
	                	return new ResponseEntity<Pessoa>(pessoaAtualizada, HttpStatus.OK); 
	                }else {
	                	// erro ao editar endereço
	                	return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
	                }                
	                
	            }else {
	            	// erro ao salvar pessoa
	            	return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
	            }
	        }else {	        	
	        	// necessário informar um endereço como principal
	        	return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
	        }
        	
        } else {
        	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }  
    }
	
	@RequestMapping(value = "/adiciona", method =  RequestMethod.POST)
	public ResponseEntity<Pessoa> Cadastra(@RequestBody Pessoa pessoaASalva) {
		
		// salva endereço
		List<Endereco> enderecosCadastrados = enderecoService.SalvaEndereco(pessoaASalva.getEnderecos());
		
		if(enderecosCadastrados != null && !enderecosCadastrados.isEmpty()) {
			
			// salva pessoa desde que esteja com somente um endereço como principal
			Pessoa pessoaCadastrada = pessoaService.SalvaPessoa(pessoaASalva);
			
			if(pessoaCadastrada != null) {
			
				// vincula pessoa ao endereço 
				enderecoService.VinculaPessoasAoEndereco(enderecosCadastrados, pessoaCadastrada);
				
				//salva endereço
				List<Endereco> enderecos = enderecoService.SalvaEndereco(enderecosCadastrados);
				
				if(enderecos != null) {
					// cadastro realizado com sucesso
					return new ResponseEntity<Pessoa>(pessoaCadastrada, HttpStatus.OK);
				}else {
					//erro ao atualizar endereço
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
				}
				
			}else {
				//erro ao salvar pessoa
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
			}
			
		}else {
			//erro ao salvar endereço
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
		}	
	}		
}
