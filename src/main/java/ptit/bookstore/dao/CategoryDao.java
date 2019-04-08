package ptit.bookstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import ptit.bookstore.model.Category;

@Repository
public class CategoryDao {
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private JdbcTemplate jdbctemplate;
	
	public List<Category> findAll(){
		List<Category> listCategory = new ArrayList<Category>();		
		PreparedStatement ps = null;
		Connection connection = null;
		try {				
			String sql = "select * from category";
			connection  = dataSource.getConnection();
			ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Category cat = new Category();
				cat.setId(rs.getInt("id"));
				cat.setName(rs.getString("name"));
				listCategory.add(cat);
			}
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				ps.close();
				connection.close();
			} catch (SQLException e) {				
				e.printStackTrace();
			}
		}
		return listCategory;
	}
	
	
	public Category findOne(int id) {
		PreparedStatement ps = null;
		Connection connection = null;
		Category category = null;		
		try {				
			String sql = "select * from category where id = ?";
			connection  = dataSource.getConnection();
			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				category = new Category();
				category.setId(rs.getInt("id"));
				category.setName(rs.getString("name"));				
			}
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				ps.close();
				connection.close();
			} catch (SQLException e) {				
				e.printStackTrace();
			}
		}
		return category;
	}
	
	public Category save(Category category) {
		PreparedStatement ps = null;
		int autoGeneratedKey = 0;
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			String sql = "insert into category (name) values (?)";
			ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setString(1, category.getName());
			int rowAffect = ps.executeUpdate();
			System.out.println("number of row affect in category dao save " + rowAffect);
			ResultSet rs = ps.getGeneratedKeys();
			while (rs.next()){
				autoGeneratedKey = rs.getInt(1);
			}
			category.setId(autoGeneratedKey);
		} catch(Exception ex) {
			ex.printStackTrace();			
		}
		return category;
	}
	
	public Category update(final Category category) {
		jdbctemplate.update(new PreparedStatementCreator() {			
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				String sql = "update category set name = ? where id = ?";
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setString(1, category.getName());
				ps.setInt(2, category.getId());				
				return ps;
			}
		});
		return category;
	}
	
	public boolean delete(int id) {
		PreparedStatement ps = null;
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			String sql = "delete from category where id = ?";
			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			int rowAffect = ps.executeUpdate();
			System.out.println("row affect after delete " + rowAffect);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {			
			try {
				ps.close();
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}
	
}