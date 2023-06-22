package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface AuthDAO {

    public boolean authenticate(AuthToken token);
    public void registerAuthToken(AuthToken token);
    public void deleteAuthToken(AuthToken token);

}
