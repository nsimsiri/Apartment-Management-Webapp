package com.apt_mgmt_sys.APIServer.spec;

import com.apt_mgmt_sys.APIServer.models.*;
import com.apt_mgmt_sys.APIServer.models.Contract_;
import com.apt_mgmt_sys.APIServer.models.CustomerProfile_;
import com.apt_mgmt_sys.APIServer.models.Room_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.Date;

public final class ContractSpecifications {
    private ContractSpecifications() { }
    /*
    * e1 > req ==> 1
    * e1 < req ==> -1
    * */
    public static Specification<Contract> compareStartDate(Date reqDate, int compare) {
        return (Root<Contract> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            Expression e1 = root.get(Contract_.startDate);
            Predicate predicate;
            if (compare > 0) {
                predicate = cb.greaterThan(e1, reqDate);
            } else if (compare < 0) {
                predicate = cb.lessThan(e1, reqDate);
            } else {
                predicate = cb.equal(e1, reqDate);
            }
            return predicate;
        };
    }

    public static Specification<Contract> compareEndDate(Date reqDate, int compare) {
        return (Root<Contract> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            Expression e1 = root.get(Contract_.endDate);
            Predicate predicate;
            if (compare > 0) {
                predicate = cb.greaterThan(e1, reqDate);
            } else if (compare < 0) {
                predicate = cb.lessThan(e1, reqDate);
            } else {
                predicate = cb.equal(e1, reqDate);
            }
            return predicate;
        };
    }

    public static Specification<Contract> contractWithinDateRange(Date startDate, Date endDate){
        return (root, query, cb) -> {
            Expression conStart = root.get(Contract_.startDate);
            Expression conEnd = root.get(Contract_.endDate);
            Predicate p1 = cb.lessThanOrEqualTo(conStart, endDate);
            Predicate p2 = cb.greaterThanOrEqualTo(conEnd, startDate);
            return cb.and(p1, p2);
        };
    }

    public static Specification<Contract> findByCustomerProfile(CustomerProfile profile){
        return (root, cq, cb) -> cb.equal(root.get(Contract_.customerProfile), profile);
    }

    public static Specification<Contract> findByBookedRoom(Room room){
        return (root, cq, cb) -> cb.equal(root.get(Contract_.bookedRoom), room);
    }

    public static Specification<Contract> findByBookedRoomName(String roomNumber){
        return (root, query, cb) -> {
          Join<Contract, Room> join1 = root.join(Contract_.bookedRoom);
          return cb.like(join1.get(Room_.roomNumber), "%"+roomNumber+"%");
        };
    }

    public static Specification<Contract> findByNameCustomerProfileFirstOrLastName(String name){
        return (root, query, cb) -> {
            Join<Contract, CustomerProfile> join1 = root.join(Contract_.customerProfile);
            Predicate p1 = cb.like(join1.get(CustomerProfile_.firstname), "%"+name+"%");
            Predicate p2 = cb.like(join1.get(CustomerProfile_.lastname), "%"+name+"%");
            return cb.or(p1,p2);
        };
    }


}
