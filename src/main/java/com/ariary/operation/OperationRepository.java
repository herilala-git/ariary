package com.ariary.operation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OperationRepository extends JpaRepository<Operation, Long> {

	@Query("""
			select new com.ariary.operation.BlotterLine(o.id, o.instrumentType, o.status, p.code, e.name, c.name,
			                                            o.currency.code, o.notional, o.maturityDate)
			from Operation o
			join o.portfolio p
			join p.entity e
			join o.counterparty c
			where o.status <> :excluded
			order by o.id
			""")
	List<BlotterLine> blotter(Status excluded);

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
