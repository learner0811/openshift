package ptit.bookstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.jdbc.core.PreparedStatementCreator;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;


import ptit.bookstore.model.Account;

@Repository
public class AccountDao {
	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	

	public Account findOne(Account account) {
		PreparedStatement ps = null;
		Connection connection = null;
		try {
			String sql = "select * from account where username = ? and password = ?";
			connection = dataSource.getConnection();
			ps = connection.prepareStatement(sql);
			ps.setString(1, account.getUsername());
			ps.setString(2, account.getPassword());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				account.setId(rs.getInt("id"));
				account.setUsername(rs.getString("username"));
				account.setPassword(rs.getString("password"));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			account = new Account();
		} finally {
			try {
				ps.close();
				connection.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return account;
	}
		
	public Account Save(final Account account) throws SQLException {		
		int autoGeneratedId = 0;
		
		KeyHolder holder = new GeneratedKeyHolder();

		int rowAffect = jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				String sql = "insert into account (username, password) values(?, ?)";
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, account.getUsername());
				ps.setString(2, account.getPassword());
				return ps;
			}
		}, holder);
		autoGeneratedId = holder.getKey().intValue();
		System.out.println("row affect in save accountDao" + rowAffect);
		if (autoGeneratedId == 0)
			return new Account();
		account.setId(autoGeneratedId);
		return account;
	}	
}
