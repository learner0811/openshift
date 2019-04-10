package ptit.bookstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import ptit.bookstore.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class BookInfoDao {
	@Autowired 
	private DataSource dataSource;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private BookDao bookDao;
	
	private Connection conn;
	
	public List<BookInfo> getAllBook()
	{
		List<BookInfo> result = new ArrayList<BookInfo>();
		try {
			String sql = "select * from bookinfo "
					+ "left join author on bookinfo.authorId = author.id "
					+ "left join publisher on bookinfo.publisherId = publisher.id "
					+ "left join book_category on bookinfo.id = book_category.idbook "
					+ "left join category on book_category.idcat = category.id ";
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next())
			{
				int id = rs.getInt("bookinfo.id");
				BookInfo b = isBookInList(id, result);
				if(b == null)
				{
					sql = "select * from bookinfo join rating on bookinfo.id = rating.bookId "
							+ "where bookinfo.id = ?";
					b = new BookInfo();
					b.setId(id);
					b.setName(rs.getString("bookinfo.name"));
					Author author = new Author();
					author.setId(rs.getInt("bookinfo.authorId"));
					author.setDob(rs.getDate("author.dob"));
					author.setName(rs.getString("author.name"));
					b.setAuthor(author);
					Publisher publisher = new Publisher();
					publisher.setId(rs.getInt("bookinfo.publisherId"));
					publisher.setName(rs.getString("publisher.name"));
					b.setPublisher(publisher);
					b.setPrice(rs.getInt("bookinfo.price"));
					b.setDiscount(rs.getInt("bookinfo.discount"));
					b.setImgUrl(rs.getString("bookinfo.imgUrl"));
					b.setDescription(rs.getString("bookinfo.description"));
					Category category = new Category();
					category.setId(rs.getInt("category.id"));
					category.setName(rs.getString("category.name"));
					b.addCategory(category);
					ps = conn.prepareStatement(sql);
					ps.setInt(1, id);
					double rating = 0;
					ResultSet temp = ps.executeQuery();
					int count = 0;
					while(temp.next())
					{
						rating += temp.getDouble("rating.numberOfStar");
						count++;
					}
					if(count == 0)
						rating = 0;
					else
						rating /= count;
					b.setAverageRating(rating);
					int quantity = bookDao.getAvailableNumber(id);
					b.setAvailableQuantity(quantity);
					result.add(b);
				}
				else
				{
					Category category = new Category();
					category.setId(rs.getInt("category.id"));
					category.setName(rs.getString("category.name"));
					b.addCategory(category);
				}
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	public BookInfo getBookById(int id)
	{
		BookInfo b = null;
		String sql = "select * from bookinfo "
				+ "left join author on bookinfo.authorId = author.id "
				+ "left join publisher on bookinfo.publisherId = publisher.id "
				+ "left join book_category on bookinfo.id = book_category.idbook "
				+ "left join category on book_category.idcat = category.id "
				+ "where bookinfo.id = ?";
		PreparedStatement ps;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			b = new BookInfo();
			sql = "select * from bookinfo join rating on bookinfo.id = rating.bookId "
					+ "where bookinfo.id = ?";
			while(rs.next())
			{
				if(b.getId() != id)
				{
					b.setId(id);
					b.setName(rs.getString("bookinfo.name"));
					Author author = new Author();
					author.setId(rs.getInt("bookinfo.authorId"));
					author.setDob(rs.getDate("author.dob"));
					author.setName(rs.getString("author.name"));
					b.setAuthor(author);
					Publisher publisher = new Publisher();
					publisher.setId(rs.getInt("bookinfo.publisherId"));
					publisher.setName(rs.getString("publisher.name"));
					b.setPublisher(publisher);
					b.setPrice(rs.getInt("bookinfo.price"));
					b.setDiscount(rs.getInt("bookinfo.discount"));
					b.setImgUrl(rs.getString("bookinfo.imgUrl"));
					b.setDescription(rs.getString("bookinfo.description"));
					Category category = new Category();
					category.setId(rs.getInt("category.id"));
					category.setName(rs.getString("category.name"));
					b.addCategory(category);
					ps = conn.prepareStatement(sql);
					ps.setInt(1, id);
					double rating = 0;
					ResultSet temp = ps.executeQuery();
					int count = 0;
					while(temp.next())
					{
						rating += temp.getDouble("rating.numberOfStar");
						count++;
					}
					if(count == 0)
						rating = 0;
					else
						rating /= count;
					b.setAverageRating(rating);
					int quantity = bookDao.getAvailableNumber(id);
					b.setAvailableQuantity(quantity);
				}
				else
				{
					Category category = new Category();
					category.setId(rs.getInt("category.id"));
					category.setName(rs.getString("category.name"));
					b.addCategory(category);
				}
			}
			conn.close();
			return b;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public List<BookInfo> getBookByName(String bookName)
	{
		bookName = bookName.toLowerCase();
		bookName = "%" + bookName + "%";
		List<BookInfo> result = new ArrayList<BookInfo>();
		try {
			String sql = "select * from bookinfo "
					+ "left join author on bookinfo.authorId = author.id "
					+ "left join publisher on bookinfo.publisherId = publisher.id "
					+ "left join book_category on bookinfo.id = book_category.idbook "
					+ "left join category on book_category.idcat = category.id "
					+ "where lower(bookinfo.name) like ?";
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, bookName);
			ResultSet rs = ps.executeQuery();
			while(rs.next())
			{
				int id = rs.getInt("bookinfo.id");
				BookInfo b = isBookInList(id, result);
				if(b == null)
				{
					sql = "select * from bookinfo join rating on bookinfo.id = rating.bookId "
							+ "where bookinfo.id = ?";
					b = new BookInfo();
					b.setId(id);
					b.setName(rs.getString("bookinfo.name"));
					Author author = new Author();
					author.setId(rs.getInt("bookinfo.authorId"));
					author.setDob(rs.getDate("author.dob"));
					author.setName(rs.getString("author.name"));
					b.setAuthor(author);
					Publisher publisher = new Publisher();
					publisher.setId(rs.getInt("bookinfo.publisherId"));
					publisher.setName(rs.getString("publisher.name"));
					b.setPublisher(publisher);
					b.setPrice(rs.getInt("bookinfo.price"));
					b.setDiscount(rs.getInt("bookinfo.discount"));
					b.setImgUrl(rs.getString("bookinfo.imgUrl"));
					b.setDescription(rs.getString("bookinfo.description"));
					Category category = new Category();
					category.setId(rs.getInt("category.id"));
					category.setName(rs.getString("category.name"));
					b.addCategory(category);
					ps = conn.prepareStatement(sql);
					ps.setInt(1, id);
					double rating = 0;
					ResultSet temp = ps.executeQuery();
					int count = 0;
					while(temp.next())
					{
						rating += temp.getDouble("rating.numberOfStar");
						count++;
					}
					if(count == 0)
						rating = 0;
					else
						rating /= count;
					b.setAverageRating(rating);
					int quantity = bookDao.getAvailableNumber(id);
					b.setAvailableQuantity(quantity);
					result.add(b);
				}
				else
				{
					Category category = new Category();
					category.setId(rs.getInt("category.id"));
					category.setName(rs.getString("category.name"));
					b.addCategory(category);
				}
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	public List<BookInfo> getBookByCategory(int categoryId)
	{
		List<BookInfo> result = new ArrayList<BookInfo>();
		try {
			String sql = "select * from bookinfo "
					+ "left join book_category on bookinfo.id = book_category.idbook "
					+ "left join category on book_category.idcat = category.id "
					+ "where category.id = ?";
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, categoryId);
			ResultSet rs = ps.executeQuery();
			while(rs.next())
			{
				int id = rs.getInt("bookinfo.id");
				BookInfo b = this.getBookById(id);
				result.add(b);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	public double getUserRating(int bookId, int userId)
	{
		double result = 0;
		String sql = "select * from bookinfo "
				+ "join rating on bookinfo.id = rating.bookId "
				+ "where bookinfo.id = ? and rating.userId = ?";
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, bookId);
			ps.setInt(2, userId);
			ResultSet rs = ps.executeQuery();
			if(rs.next())
			{
				result = rs.getDouble("rating.numberOfStar");
			}
			else
				result = 0;
			conn.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	
	private BookInfo isBookInList(int id, List<BookInfo> listBook)
	{
		for(BookInfo b : listBook)
			if(b.getId() == id)
				return b;
		return null;
	}
	
//	private class BookRowMapper implements RowMapper<Book>
//	{
//
//		public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
//			Book b = new Book();
//			b.setId(rs.getInt("book.id"));
//			Author author = new Author();
//			author.setId(rs.getInt("book.authorId"));
//			author.setDob(rs.getDate("author.dob"));
//			author.setName(rs.getString("author.name"));
//			b.setAuthor(author);
//			Publisher publisher = new Publisher();
//			publisher.setId(rs.getInt("book.publisherId"));
//			publisher.setName(rs.getString("publisher.name"));
//			b.setPrice(rs.getInt("book.price"));
//			b.setDiscount(rs.getInt("book.discount"));
//			b.setStatus(rs.getString("book.status"));
//			b.setImgUrl(rs.getString("book.imgUrl"));
//			b.setDescription(rs.getString("book.description"));
//			return b;
//		}
//	}


	public BookInfo save(final BookInfo book) {
		KeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				String sql = "insert into bookInfo (authorId, publisherId, price, imgUrl, name, discount) values (?,?,?,?,?,?)";
				PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
				if (book.getAuthor().getId() == 0)
					ps.setNull(1, Types.INTEGER);
				else					
					ps.setInt(1, book.getAuthor().getId());
				if (book.getPublisher().getId() == 0)
					ps.setNull(2, Types.INTEGER);
				else
					ps.setInt(2, book.getPublisher().getId());
				ps.setInt(3, book.getPrice());
				ps.setString(4, book.getImgUrl());
				ps.setString(5, book.getName());
				ps.setInt(6, book.getDiscount());
				return ps;
			}
		}, holder);
		book.setId(holder.getKey().intValue());
				
		return book;
	}

	public void addBookCategory(final BookInfo book) {
		final List<Category> listCat = book.getCategory();
		int rowAffect = 0;
		for (int i = 0; i < listCat.size(); i++) {
			rowAffect += jdbcTemplate.update("insert into book_category values (?, ?)", book.getId(), listCat.get(i).getId());			
		}
				
		System.out.println("number of row affect in book dao function addBookCategory " + rowAffect);
	}
	
	/*public void addExistingBook(int bookinfoId, int quantity)
	{
		for(int i = 0; i < quantity; i++)
		{
			jdbcTemplate.update(new PreparedStatementCreator()
			{
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					String sql = "insert into "
							+ "book(book.status, book.bookinfoId) "
							+ "values (?, ?)";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setInt(1, 1);
					ps.setInt(2, bookinfoId);
					return ps;
				}
			});
		}
	}*/
}
