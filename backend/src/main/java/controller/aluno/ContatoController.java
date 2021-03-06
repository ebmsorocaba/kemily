package controller.aluno;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jdbc.dao.aluno.ContatoDAO;
import model.aluno.Contato;

@RestController
public class ContatoController {

	private ContatoDAO contatoDao = new ContatoDAO();
	
	public ContatoController() throws SQLException { }
	
	@CrossOrigin
	@RequestMapping(value = "/api/contato/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deletar(@PathVariable("id") int id) throws SQLException {
		contatoDao.excluir(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
