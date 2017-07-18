package spring.simple;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class MyJdbcTemplate extends JdbcTemplate{
	
	public MyJdbcTemplate() {
		super();
	}
	
	
	public MyJdbcTemplate(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	protected RowMapper getColumnMapRowMapper() {
		return new MyColumnMapRowMapper();
	}

	
}
