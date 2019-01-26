/**
 * AtaDaoJDBC.java
 *
 *  @author Daniel Bispo <danielvbispo@outlook.com>
 *  Created on 25 de jan de 2019
 *  GNU License
 *
 */
package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.CrudDAO;
import model.entities.Ata;

/**
 * Implementation of CrudDAO for Ata entity. It uses JDBC connection only.
 */
public class AtaDaoJDBC implements CrudDAO<Ata> {

	private Connection conn;

	public AtaDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Ata obj) {
	}

	@Override
	public void upDate(Ata obj) {

		String sql = "UPDATE ata SET ata_number=?, info=?, create_date=?, update_date=?, user_loggin=? WHERE id=?";

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, obj.getAtaNumber());
			pstmt.setString(2, obj.getInfo());
			pstmt.setString(3, obj.getCreateDate());
			pstmt.setString(4, new Date().toString()); // Set to today always
			pstmt.setString(5, obj.getUserLoggin());
			pstmt.setInt(6, obj.getId());

			pstmt.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pstmt);
		}
	}

	@Override
	public void deleteById(int id) {

		String sql = "DELETE FROM ata WHERE id=?";

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
	public Ata findById(int id) {

		String sql = "SELECT * FROM ata WHERE id=?";

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				return createAtaObj(rs);
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
	public List<Ata> findAll() {

		// A list which contains all Ata elements read from database
		List<Ata> ataList = new ArrayList<>();

		String sql = "SELECT * FROM ata ORDER BY ata_number";

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {

				ataList.add(createAtaObj(rs));
			}

			return ataList;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pstmt);
			DB.closeResultSet(rs);
		}
	}

	// Instantiate an Ata object used by findAll()
	private Ata createAtaObj(ResultSet rs) throws SQLException {

		Ata ata = new Ata();

		ata.setId(rs.getInt("id"));
		ata.setAtaNumber(rs.getString("ata_number"));
		ata.setInfo(rs.getString("info"));
		ata.setCreateDate(rs.getString("create_date"));
		ata.setUpdateDate(rs.getString("update_date"));
		ata.setUserLoggin(rs.getString("user_loggin"));

		return ata;
	}
}