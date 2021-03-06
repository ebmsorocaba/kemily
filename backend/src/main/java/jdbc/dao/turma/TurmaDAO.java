package jdbc.dao.turma;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jdbc.ConnectionFactory;
import model.turma.Turma;

public class TurmaDAO {
	private Connection connection;

	public TurmaDAO() throws SQLException {
		this.connection = ConnectionFactory.getConnection();
	}
	
	public void adiciona(Turma turma) throws SQLException {
		try {
			
			PreparedStatement stmt = (PreparedStatement) this.connection.prepareStatement("INSERT INTO turma (cpf_educador, periodo, nome) VALUES (?, ?, ?)");
			
			stmt.setString(1, turma.getCpfEducador());
			stmt.setString(2, turma.getPeriodo());
			stmt.setString(3, turma.getNome());
			
			stmt.execute();
			
			stmt.close();
			
		} catch (SQLException ex){
			System.out.println(ex.toString());
		}
	}
	
	public List<Turma> getLista() throws SQLException {
		List<Turma> turmas = new ArrayList<Turma>();
		
		try {
			PreparedStatement stmt = (PreparedStatement) this.connection.prepareStatement("SELECT * FROM turma");
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				Turma t = new Turma();
				
				t.setId(rs.getInt("id"));
				t.setCpfEducador(rs.getString("cpf_educador"));
				t.setPeriodo(rs.getString("periodo"));
				t.setNome(rs.getString("nome"));
				
				turmas.add(t);
			}
			
			rs.close();
			
			stmt.close();
		} catch (SQLException ex) {
			System.out.println(ex.toString());
		}
		
		
		return turmas;
	}
	
	public Turma getTurma(int search) throws SQLException {
		Turma turma = new Turma();
		
		try {
			PreparedStatement stmt = (PreparedStatement) this.connection.prepareStatement("SELECT * FROM turma WHERE " + "id = ?");
			stmt.setInt(1, search);
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next() == true) {
				
				turma.setId(rs.getInt("id"));
				turma.setCpfEducador(rs.getString("cpf_educador"));
				turma.setPeriodo(rs.getString("periodo"));
				turma.setNome(rs.getString("nome"));
			
			}
			
			rs.close();
			stmt.close();
			
		} catch (SQLException ex) {
			System.out.println(ex.toString());
		}
		
		return turma;
	}
	
	public void excluir(int search) {
		try {
			PreparedStatement stmt = (PreparedStatement) this.connection.prepareStatement("DELETE FROM turma WHERE id = ?");
            stmt.setInt(1, search);
            stmt.execute();
            stmt.close();
		} catch (SQLException ex) {
            System.out.println(ex.toString());
        }
	}
	
	public void altera(Turma turma, int id) throws SQLException {
		PreparedStatement stmt = (PreparedStatement) this.connection.prepareStatement("UPDATE turma SET cpf_educador = ?, periodo = ?, nome = ? WHERE id = ?");
		
		stmt.setString(1, turma.getCpfEducador());
		stmt.setString(2, turma.getPeriodo());
		stmt.setString(3, turma.getNome());
		stmt.setInt(4, id);
		
		stmt.execute();
		
		stmt.close();
	}
}
