package com.pbs.tech.repo;

import com.pbs.tech.model.Smtp;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SmtpRepo extends PagingAndSortingRepository<Smtp,Long> {
}
