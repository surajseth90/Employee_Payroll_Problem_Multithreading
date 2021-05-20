package com.bridgelabz.employeepayroll;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {
	private PreparedStatement preparedStatement;
	private static int connectionCounter =0;
	private static EmployeePayrollDBService employeePayrollDBService;

	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDBService == null) {
			employeePayrollDBService = new EmployeePayrollDBService();
		}
		return employeePayrollDBService;
	}

	public static synchronized Connection getConnection() throws SQLException {
		connectionCounter++;
		String jdbcURL = "jdbc:mysql://localhost:3306/employee_payroll_service";
		String userName = "root";
		String password = "Surajmysql@90";
		Connection connectionn;
		connectionn = DriverManager.getConnection(jdbcURL, userName, password);
		return connectionn;
	}

	public List<EmployeePayrollData> readDataFromDB() throws EmployeePayrollServiceException {
		String sql = "SELECT * FROM employee_payroll;";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = this.getConnection();) {
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				int emp_id = resultSet.getInt("emp_id");
				String name = resultSet.getString("name");
				String gender = resultSet.getString("Gender");
				double salary = resultSet.getDouble("salary");
				LocalDate startDate = resultSet.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(emp_id, name, gender, salary, startDate));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new EmployeePayrollServiceException(
					EmployeePayrollServiceException.EmployeePayrollExceptionType.EMPLOYEE_DATA_RETRIEVE_ISSUE,
					"Unable to read data from database!!");
		}
		return employeePayrollList;
	}

	public List<EmployeePayrollData> getEmployeeData(String name) {
		List<EmployeePayrollData> employeePayrollList = null;
		if (this.preparedStatement == null)
			this.preparedStatementForEmployeeData();
		try {
			// Sets the designated parameter to the given Java String value
			preparedStatement.setString(1, name);
			ResultSet resultSet = preparedStatement.executeQuery();
			employeePayrollList = this.getEmployeeData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private List<EmployeePayrollData> getEmployeeData(ResultSet resultSet) throws SQLException {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		while (resultSet.next()) {
			int emp_id = resultSet.getInt("emp_id");
			String name = resultSet.getString("name");
			String gender = resultSet.getString("gender");
			double salary = resultSet.getDouble("salary");
			LocalDate startDate = resultSet.getDate("start").toLocalDate();
			employeePayrollList.add(new EmployeePayrollData(emp_id, name, gender, salary, startDate));
		}
		return employeePayrollList;
	}

	private void preparedStatementForEmployeeData() {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT * FROM employee_payroll WHERE name=?";
			preparedStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public EmployeePayrollData addEmployee(int emp_id, String name, String gender, double salary, LocalDate startDate) {
		int employeeId = -1;
		EmployeePayrollData employeePayrollData = null;
		String sql = String.format(
				"INSERT INTO employee_payroll (name,Gender,salary,start) VALUES ('%s','%s','%s','%s')", name, gender,
				salary, Date.valueOf(startDate));
		try (Connection connection = this.getConnection();) {
			preparedStatement = connection.prepareStatement(sql);
			int rowAffected = preparedStatement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = preparedStatement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}
			employeePayrollData = new EmployeePayrollData(employeeId, name, gender, salary, startDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employeePayrollData;
	}
	
	public synchronized int updateEmployeeData(String name, Double salary) {
		return this.updateEmployeeDataUsingPreparedStatement(name, salary);
	}

	private int updateEmployeeDataUsingPreparedStatement(String name, Double salary) {
		String sql = String.format("UPDATE employee_payroll SET salary=%.2f WHERE name='%s'", salary, name);
		try (Connection connection = this.getConnection();) {
			PreparedStatement prepareStatement = connection.prepareStatement(sql);
			return prepareStatement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public EmployeePayrollData addEmployeeToPayroll(String name, String gender, double salary, LocalDate start)
			throws EmployeePayrollServiceException {
		int employeeID = -1;
		Connection connection = null;
		EmployeePayrollData employeePayrollData = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new EmployeePayrollServiceException(
					EmployeePayrollServiceException.EmployeePayrollExceptionType.ADDING_NEW_EMPLOYEE_ISSUE,
					"Unable to Add new Employee!!");
		}
		try (Statement statement = connection.createStatement()) {
			String sql = String.format(
					"INSERT INTO employee_payroll(name,Gender,salary,start) " + "VALUES ( '%s', '%s', %s, '%s' )", name,
					gender, salary, Date.valueOf(start));
			int rowsAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowsAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					employeeID = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			
			try {
				connection.rollback();
				return employeePayrollData;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		try (Statement statement = connection.createStatement()) {
			double deduction = salary * 0.2;
			double taxablePay = salary - deduction;
			double tax = taxablePay * 0.1;
			double netPay = salary - tax;
			String sql = String.format("INSERT INTO payroll_details "
					+ "(id, basicPay, deduction, taxableAmount, tax , netPay) VALUES "
					+ "(%s, %s, %s, %s, %s, %s)", employeeID, salary, deduction, taxablePay, tax, netPay);
			int rowsAffected = statement.executeUpdate(sql);
			if (rowsAffected == 1) {
				employeePayrollData = new EmployeePayrollData(employeeID, name, gender, salary, start);
			}
		} catch (SQLException e) {
			e.printStackTrace();

			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return employeePayrollData;
	}
}