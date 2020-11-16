package shareloc.model.dao;

import shareloc.model.ejb.Service;

public class ServiceDAO extends DAO<Service> {
    public ServiceDAO() {
        super(Service.class);
    }
}
