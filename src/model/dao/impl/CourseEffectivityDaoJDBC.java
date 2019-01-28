/**
 * CourseEffectivityDaoJDBC.java
 *
 *  @author Daniel Bispo <danielvbispo@outlook.com>
 *  Created on 27 de jan de 2019
 *  GNU License
 *
 */
package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.CrudDAO;
import model.entities.CourseEffectivity;

/**
 * Implementation of CrudDAO for CourseEffectivity entity. It uses JDBC
 * connection only.
 */
public class CourseEffectivityDaoJDBC implements CrudDAO<CourseEffectivity> {

	private Connection conn;

	public CourseEffectivityDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	// A new CourseEffectivity object has to be created before. Then use that as the
	// parameter
	// for this method
	public void insert(CourseEffectivity obj) {

		String sql = "INSERT INTO course_effectivity (course_effec_info, create_date, update_date, user_loggin) VALUES (?, ?, ?, ?)";

		PreparedStatement pstmt = null;

		try {

			/*
			 * Statement.RETURN_GENERATED_KEYS is needed as second parameter to get the key
			 * generated by the database, since it will be used to update the object's id
			 */
			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, obj.getCourseEffecInfo());
			pstmt.setString(2, new Date().toString()); // Set to today always
			pstmt.setString(3, obj.getUpdateDate());
			pstmt.setString(4, obj.getUserLoggin());

			// Get the amount of the inserted row
			int insRows = pstmt.executeUpdate();

			if (insRows > 0) {
				ResultSet rs = pstmt.getGeneratedKeys();

				if (rs.next()) {
					obj.setId(rs.getInt(1)); // Set a new object Id according to database
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Unespected error when trying to set a new id for the course effectivity!");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pstmt);
		}
	}

	@Override
	// An new CourseEffectivity object has to be created before. Then use that as
	// the parameter
	// for this method
	public void upDate(CourseEffectivity obj) {

		String sql = "UPDATE course_effectivity SET course_effec_info=?, create_date=?, update_date=?, user_loggin=? WHERE id=?";

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, obj.getCourseEffecInfo());
			pstmt.setString(2, obj.getCreateDate());
			pstmt.setString(3, new Date().toString()); // Set to today always
			pstmt.setString(4, obj.getUserLoggin());
			pstmt.setInt(5, obj.getId());

			pstmt.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pstmt);
		}
	}

	@Override
	public void deleteById(int id) {

		String sql = "DELETE FROM course_effectivity WHERE id=?";

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pstmt);
		}
	}

	@Override
	public CourseEffectivity findById(int id) {

		String sql = "SELECT * FROM course_effectivity WHERE id=?";

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				return createCourseEffectivityObj(rs);
			}

			return null;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pstmt);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<CourseEffectivity> findAll() {

		// A list which contains all CourseEffectivity elements read from database
		List<CourseEffectivity> courseEffectivityList = new ArrayList<>();

		String sql = "SELECT * FROM course_effectivity ORDER BY course_effec_info";

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {

				courseEffectivityList.add(createCourseEffectivityObj(rs));
			}

			return courseEffectivityList;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pstmt);
			DB.closeResultSet(rs);
		}
	}

	// Instantiate an CourseEffectivity object used by findAll()
	private CourseEffectivity createCourseEffectivityObj(ResultSet rs) throws SQLException {

		CourseEffectivity courseEffectivity = new CourseEffectivity();

		courseEffectivity.setId(rs.getInt("id"));
		courseEffectivity.setCourseEffecInfo(rs.getString("course_effec_info"));
		courseEffectivity.setCreateDate(rs.getString("create_date"));
		courseEffectivity.setUpdateDate(rs.getString("update_date"));
		courseEffectivity.setUserLoggin(rs.getString("user_loggin"));

		return courseEffectivity;
	}
}
