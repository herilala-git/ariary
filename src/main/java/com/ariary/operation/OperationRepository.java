package com.ariary.operation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OperationRepository extends JpaRepository<Operation, Long> {

	@Query(value = """
			select o from Operation o
			join fetch o.portfolio
			join fetch o.counterparty
			where (:status is null or o.status = :status)
			  and (:portfolio is null or o.portfolio.code = :portfolio)
			""", countQuery = """
			select count(o) from Operation o
			where (:status is null or o.status = :status)
			  and (:portfolio is null or o.portfolio.code = :portfolio)
			""")
	Page<Operation> search(Status status, String portfolio, Pageable pageable);

}
