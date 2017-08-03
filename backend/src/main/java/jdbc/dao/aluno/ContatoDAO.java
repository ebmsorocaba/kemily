package jdbc.dao.aluno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import jdbc.ConnectionFactory;
import model.aluno.Contato;

public class ContatoDAO {
	private Connection connection;
	private AlunoDAO alunoDao;
	
	public ContatoDAO() throws SQLException {
		this.connection = ConnectionFactory.getConnection();
		this.alunoDao = new AlunoDAO();
	}
	
	public Contato adicionar(Contato contato) throws SQLException {
		try (
			PreparedStatement stmt = (PreparedStatement) this.connection.prepareStatement("INSERT INTO contato(nome, telefone, ra_aluno) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		) {
		
			stmt.setString(1, contato.getNome());
			stmt.setString(2, contato.getTelefone());
			stmt.setInt(3, contato.getAluno().getRa());
			
			int id = stmt.executeUpdate(); 
			
			if(id == 0) {
				contato.setId(-1);
				throw new SQLException("Falha na craição do contato, linha nao alterada");
			} 
			
			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if(generatedKeys.next()) {
					contato.setId(generatedKeys.getInt(1));
				} else {
					contato.setId(-1);
					throw new SQLException("Falha na craição do contato, ID nao retornado"); 	
				}
			}
			
			stmt.close();
		
		} catch (SQLException ex) {
			System.out.println(ex.toString());
		}
		
		return contato;
	}

	public List<Contato> getContato() throws SQLException {
		List<Contato> contatos = new ArrayList<Contato>();
		
		PreparedStatement stmt = (PreparedStatement) this.connection.prepareStatement("SELECT * FROM contato");
		ResultSet rs = stmt.executeQuery();
		
		while(rs.next()) {
			Contato contato = new Contato();
			
			contato.setId(rs.getInt("id"));
			contato.setNome(rs.getString("nome"));
			contato.setTelefone(rs.getString("telefone"));
			contato.setAluno(alunoDao.getAluno(rs.getInt("ra_aluno")));
			
			contatos.add(contato);
		}
		
		rs.close();
		stmt.close();
		
		return contatos;
	}
	
	public Contato getContato(int id) throws SQLException {
		Contato contato = new Contato();
		
		try {
			PreparedStatement stmt = (PreparedStatement) this.connection.prepareStatement("SELECT * FROM contato WHERE id = ?");
			stmt.setInt(1,  id);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next() == true) {
				contato.setId(rs.getInt("id"));
				contato.setNome(rs.getString("nome"));
				contato.setTelefone(rs.getString("telefone"));
				contato.setAluno(alunoDao.getAluno(rs.getInt("ra_aluno")));
			}
		} catch (SQLException ex) {
			System.out.println(ex.toString());
		}
		
		return contato;
	}
	
	public void excluir(int id) {
		try {
			
			PreparedStatement stmt = (PreparedStatement) this.connection.prepareStatement("DELETE FROM contato WHERE id = ?");
			stmt.setInt(1, id);
			
			stmt.execute();
			
		} catch(SQLException ex) {
			System.out.println(ex.toString());
		}
	}
	
	public void altera(Contato contato, int id) throws SQLException {
		PreparedStatement stmt = (PreparedStatement) this.connection.prepareStatement("UPDATE contato SET nome = ?, telefone = ?, ra_aluno = ? WHERE id = ?");
		
		stmt.setString(1, contato.getNome());
		stmt.setString(2, contato.getTelefone());
		stmt.setInt(3, contato.getAluno().getRa());
		stmt.setInt(4, id);
		
		stmt.execute();
		stmt.close();
	}
}