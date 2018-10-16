package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.CrossOrigin;

import jdbc.dao.UsuarioDAO;
import model.Usuario;

import java.sql.SQLException;
import java.text.ParseException;

@CrossOrigin //(origins = "http://localhost:8081/")
@RestController
 //E isso
public class UsuarioController {

	private Map<Integer, Usuario> usuarios;
	private UsuarioDAO usuarioDao = new UsuarioDAO();

	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public UsuarioController() throws SQLException {
	  usuarios = new HashMap<Integer, Usuario>();
	}

	private JavaMailSender javaMailSender;

	@Autowired
	void MailSubmissionController(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	@RequestMapping(value = "/api/usuarios", method = RequestMethod.GET)
	public ResponseEntity<List<Usuario>> listar() throws SQLException {
		int index=0;

		List<Usuario> usuariosGetted = new ArrayList<Usuario>();
		usuarios = new HashMap<Integer, Usuario>();

		usuariosGetted = usuarioDao.getLista();

		for (Usuario usu : usuariosGetted) { //Coloca todos alunos vindos do SELECT da DAO em um hashmap
			usuarios.put(index, usu);
			index++;
		}

		return new ResponseEntity<List<Usuario>>(new ArrayList<Usuario>(usuarios.values()), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/usuario/{nome}", method = RequestMethod.GET)
	public ResponseEntity<Usuario> buscar(@PathVariable("nome") String nome) throws SQLException {

	  Usuario usuario = usuarioDao.getUsuario(nome);
	  if (usuario == null) {
	    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  }

	  return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
	}

	@RequestMapping(value = "/api/usuario/{nome}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deletar(@PathVariable("nome") String nome) {
		//Aluno aluno = alunos.remove(id);

		/*Configurar caso não ache o objeto a ser excluido*/
		//if (aluno == null) {
	    //return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		//}
		usuarioDao.excluir(nome);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}


	@RequestMapping(value = "/api/usuario", method = RequestMethod.POST) //Esse metodo recebe uma String em formato de JSON
	public ResponseEntity<Usuario> addUsuario(@RequestBody Usuario usuario) throws JsonParseException, JsonMappingException, IOException, SQLException {

		//Usuario usuario = new ObjectMapper().readValue(usuarioJSON, Usuario.class); //Aqui o json é convertido em objeto Java Aluno
		usuarioDao.adiciona(usuario);

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(usuario.getEmail());
		mailMessage.setFrom("testingx99999@gmail.com");
		mailMessage.setSubject("Cadastro no EBM");
		mailMessage.setText("Saudações, senhor(a) " + usuario.getNome() + " seu cadastro no sistema do Educandario Bezerra de Menezes acaba de ser realizado, e você ja pode acessa-lo" + "\n"
		+ "Seus dados cadastrais foram os seguintes:" + "\n" + "Nome: " + usuario.getNome() + "\n" + "Email: " + usuario.getEmail() + "\n" + "Senha: " + usuario.getSenha() + "\n" + "Setor: " + usuario.getSetor());
		javaMailSender.send(mailMessage);

		return new ResponseEntity<Usuario>(usuario, HttpStatus.CREATED); //Aqui ele retorna o objecto aluno como confirmação que deu tudo certo, lá no t ele vai tranformar em JSON novamente
	}

	@RequestMapping(value = "/api/usuario/{nome}", method = RequestMethod.PUT) //Esse metodo recebe uma String em formato de JSON
	public ResponseEntity<Usuario> updateUsuario(@RequestBody Usuario usuario, @PathVariable("nome") String nome) throws JsonParseException, JsonMappingException, IOException, SQLException {
		if (usuario.getUsuarioSenha() != null) {
			Usuario userDb = usuarioDao.getUsuario(nome);
			if (passwordEncoder.matches(usuario.getUsuarioSenha().getSenhaAntiga(), userDb.getSenha()) && !passwordEncoder.matches(usuario.getUsuarioSenha().getSenhaNova(), userDb.getSenha())) {
				userDb.setSenha(usuario.getUsuarioSenha().getSenhaNova());
				usuarioDao.altera(userDb, nome);
				return new ResponseEntity<Usuario>(HttpStatus.CREATED);
			} else {
				return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
			}
		} else {
			usuarioDao.altera(usuario, nome);
			return new ResponseEntity<Usuario>(HttpStatus.CREATED);
		}
	}
	
	@RequestMapping(value = "/api/usuario/{nome}", method = RequestMethod.GET, params={"senha"})
	public ResponseEntity<Usuario> login(@PathVariable("nome") String nome, @RequestParam("senha") String senha) throws SQLException, ParseException {
	
		Usuario usuario;
		
		try{
			usuario = usuarioDao.getUsuario(nome);
		} catch (SQLException ex){
			System.out.println(ex.toString());
			usuario = null;
			return new ResponseEntity<Usuario>(usuario, HttpStatus.NOT_FOUND);
		}
		if(passwordEncoder.matches(senha, usuario.getSenha())){
			return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
		}
		else{
			usuario = null;
			return new ResponseEntity<Usuario>(usuario, HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = "/api/recuperarSenha/{nome}", method = RequestMethod.GET)
	public ResponseEntity<SimpleMailMessage> recuperarSenha(@PathVariable("nome") String nome) throws SQLException, ParseException {

		Usuario usuario = new Usuario();
		usuario = usuarioDao.getUsuario(nome);
		usuario.setSenha("SENHA@123");
		usuarioDao.altera(usuario, nome);

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(usuario.getEmail());
		mailMessage.setFrom("testingx99999@gmail.com");
		mailMessage.setSubject("Mudança de senha");
		mailMessage.setText("Saudações, senhor(a) " + usuario.getNome() + " sua senha foi resetada para a senha padrão SENHA@123, pedimos para que assim que acessar sua conta já a altere.");
		javaMailSender.send(mailMessage);
		//return mailMessage;

		return new ResponseEntity<SimpleMailMessage>(mailMessage, HttpStatus.CREATED);
	}
	
}
