package dataaccess.mysqlDAO;

import dataaccess.generalDAO.AuthDAO;
import dataaccess.exception.DataAccessException;
import dataaccess.records.AuthData;

public class MySQLAuthDAO implements AuthDAO {

//    public MySQLAuthDAO() {
//        configureDatabase();
//    }

    @Override
    public void addAuth(AuthData data) {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public void clear() {

    }

//    private void configureDatabase() throws ResponseException {
//        DatabaseManager.createDatabase();
//        try (Connection conn = DatabaseManager.getConnection()) {
//            for (String statement : createStatements) {
//                try (var preparedStatement = conn.prepareStatement(statement)) {
//                    preparedStatement.executeUpdate();
//                }
//            }
//        } catch (SQLException ex) {
//            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to configure database: %s", ex.getMessage()));
//        }
//    }
}
