package model.dao;

import model.ejb.Service;

public class ServiceDAO extends DAO<Service> {
    public ServiceDAO() {
        super(Service.class);
    }
}
