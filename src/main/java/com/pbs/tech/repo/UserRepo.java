package com.pbs.tech.repo;

import com.pbs.tech.common.ROLES_TYPE;
import com.pbs.tech.model.User;
import com.pbs.tech.vo.UserTokenVO;
import com.pbs.tech.vo.UserVo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserRepo extends PagingAndSortingRepository<User,Long> {

    User findByUsername(String userName);

    @Query("SELECT new com.pbs.tech.vo.UserVo(u.id,u.username,u.role,u.firstName,u.lastName, u.dateOfBirth ,u.active ,u.createdDt,u.createdBy,u.updatedDt,u.updatedBy,u.department,u.location) from User u  ORDER BY u.updatedDt DESC")
    List<UserVo> findUsersPerPage(Pageable pageable);

    @Query("SELECT new com.pbs.tech.vo.UserVo(u.id,u.username,u.role,u.firstName,u.lastName, u.dateOfBirth ,u.active ,u.createdDt,u.createdBy,u.updatedDt,u.updatedBy,u.department,u.location) from User u")
    List<UserVo> findByAll();

    @Query("SELECT new com.pbs.tech.vo.UserTokenVO(u.id,u.username,u.role,u.firstName,u.lastName, u.dateOfBirth ,u.active ,u.createdDt,u.createdBy,u.updatedDt,u.updatedBy,u.department,u.location, f.token) from User u LEFT JOIN FCMToken f ON u.id = f.id WHERE u.id=?1 AND f.id=?1")
    UserTokenVO findAllById(long id);

    @Query("SELECT new com.pbs.tech.vo.UserVo(u.id,u.username,u.role,u.firstName,u.lastName, u.dateOfBirth ,u.active ,u.createdDt,u.createdBy,u.updatedDt,u.updatedBy,u.department,u.location) from User u WHERE u.role=?1")
    List<UserVo> findAllByRole(ROLES_TYPE role);

}
